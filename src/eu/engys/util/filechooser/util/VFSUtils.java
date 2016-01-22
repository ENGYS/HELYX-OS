/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

/*
 * Copyright 2012 Krzysztof Otrebski (krzysztof.otrebski@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.engys.util.filechooser.util;

import static eu.engys.util.ui.FileChooserUtils.DEFAULT_SSH_PORT;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.provider.sftp.SftpFileObject;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.connection.SshParameters;
import eu.engys.util.filechooser.LinkFileObject;
import eu.engys.util.filechooser.authentication.AuthStore;
import eu.engys.util.filechooser.authentication.MemoryAuthStore;
import eu.engys.util.filechooser.authentication.UserAuthenticationDataWrapper;
import eu.engys.util.filechooser.authentication.UserAuthenticationInfo;
import eu.engys.util.filechooser.authentication.UserAuthenticatorFactory;
import eu.engys.util.filechooser.authentication.authenticator.OtrosUserAuthenticator;
import eu.engys.util.filechooser.uri.Protocol;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;

/**
 * A helper class to deal with commons-vfs file abstractions
 * 
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Jojada Tirtowidjojo <jojada at users.sourceforge.net>
 * @author Stephan Schuster <stephanschuster at users.sourceforge.net>
 * @version 0.0.5
 */
public final class VFSUtils {

    private static final Logger logger = LoggerFactory.getLogger(VFSUtils.class);

    public enum LocationType {
        file, sftp;

        public String toString() {
            if (Util.isWindows() && equals(file)) {
                return super.toString() + WIN_PROTOCOL_PREFIX;
            }
            return super.toString() + UNIX_PROTOCOL_PREFIX;
        };

        public static String[] toStringArray() {
            return new String[] { file.toString(), sftp.toString() };
        };
    }

    private static final int SYMBOLIC_LINK_MAX_SIZE = 128;

    private static FileSystemManager fileSystemManager;
    private static FileSystemOptions fileSystemOptions = new FileSystemOptions();
    public static final String UNIX_PROTOCOL_PREFIX = "://";
    public static final String WIN_PROTOCOL_PREFIX = ":///";
    private static final File HOME_DIRECTORY = new File(System.getProperty("user.home"));

    public static final File CONFIG_DIRECTORY = new File(HOME_DIRECTORY, ".otrosvfsbrowser");
    public static final File USER_AUTH_FILE = new File(CONFIG_DIRECTORY, "auth.xml");
    public static final File USER_AUTH_FILE_BAK = new File(CONFIG_DIRECTORY, "auth.xml.bak");
    private static ReadWriteLock aLock = new ReentrantReadWriteLock(true);

    // File size localized strings

    private static final Map<String, Icon> schemeIconMap = new HashMap<String, Icon>();
    private static final Set<String> archivesSuffixes = new HashSet<String>();

    static {
        schemeIconMap.put("file", ResourcesUtil.getIcon("drive"));
        schemeIconMap.put("sftp", ResourcesUtil.getIcon("networkCloud"));
        schemeIconMap.put("ftp", ResourcesUtil.getIcon("networkCloud"));
        schemeIconMap.put("smb", ResourcesUtil.getIcon("sambaShare"));
        schemeIconMap.put("http", ResourcesUtil.getIcon("networkCloud"));
        schemeIconMap.put("https", ResourcesUtil.getIcon("networkCloud"));
        schemeIconMap.put("zip", ResourcesUtil.getIcon("folderZipper"));
        schemeIconMap.put("tar", ResourcesUtil.getIcon("folderZipper"));
        schemeIconMap.put("jar", ResourcesUtil.getIcon("jarIcon"));
        schemeIconMap.put("tgz", ResourcesUtil.getIcon("folderZipper"));
        schemeIconMap.put("tbz", ResourcesUtil.getIcon("folderZipper"));

        archivesSuffixes.add("zip");
        archivesSuffixes.add("tar");
        archivesSuffixes.add("jar");
        archivesSuffixes.add("tgz");
        archivesSuffixes.add("gz");
        archivesSuffixes.add("bz2");
        archivesSuffixes.add("tar");
        archivesSuffixes.add("tbz");
        archivesSuffixes.add("tgz");

    }

    public static FileSystemManager getFileSystemManager() {
        aLock.readLock().lock();

        try {
            if (fileSystemManager == null) {
                try {
                    EngysFileSystemManager fm = new EngysFileSystemManager();
                    // fm.setClassLoader(StandardFileSystemManager.class.getClassLoader());
                    fm.setConfiguration(StandardFileSystemManager.class.getResource("providers.xml"));
                    fm.setCacheStrategy(CacheStrategy.MANUAL);
                    fm.init();
                    logger.trace("Supported schemes: {} ", Joiner.on(", ").join(fm.getSchemes()));
                    fileSystemManager = fm;
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            }

            return fileSystemManager;
        } finally {
            aLock.readLock().unlock();
        }
    }

    public static String getFriendlyName(String fileName) {
        return getFriendlyName(fileName, true);
    }

    public static String getFriendlyName(String fileName, boolean excludeLocalFilePrefix) {
        if (fileName == null) {
            return "";
        }
        StringBuilder filePath = new StringBuilder();

        int pos = fileName.lastIndexOf('@');

        if (pos == -1) {
            filePath.append(fileName);
        } else {
            int pos2 = fileName.indexOf(UNIX_PROTOCOL_PREFIX);

            if (pos2 == -1) {
                filePath.append(fileName);
            } else {
                String protocol = fileName.substring(0, pos2);

                filePath.append(protocol).append(UNIX_PROTOCOL_PREFIX).append(fileName.substring(pos + 1, fileName.length()));
            }
        }

        String returnedString = filePath.toString();

        if (excludeLocalFilePrefix && returnedString.startsWith(LocationType.file.toString())) {
            return filePath.substring(LocationType.file.toString().length());
        }

        return returnedString;
    }

    public static FileObject createFileSystemRoot(FileObject fileObject) {
        try {
            return fileObject.getFileSystem().getRoot();
        } catch (FileSystemException ex) {
            return null;
        }
    }

    public static FileObject[] getFiles(Component parent, FileObject folder) {
        try {
            return getChildren(folder);
        } catch (FileSystemException ex) {
            String url = folder == null ? "non existing file" : folder.getName().getPath();
            VFSUtils.showErrorMessage(parent, url, ex);
            return new FileObject[0];
        }
    }

    public static FileObject getRootFileSystem(FileObject fileObject) {
        try {
            if ((fileObject == null) || !fileObject.exists()) {
                return null;
            }

            return fileObject.getFileSystem().getRoot();
        } catch (FileSystemException ex) {
            return null;
        }
    }

    public static boolean isHiddenFile(FileObject fileObject) {
        try {
            return fileObject.getName().getBaseName().charAt(0) == '.';
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isRoot(FileObject fileObject) {
        try {
            return fileObject.getParent() == null;
        } catch (FileSystemException ex) {
            return false;
        }
    }

    public static FileObject resolveFileObject(String filePath) throws FileSystemException {
        return resolveFileObject(filePath, null);
    }

    public static FileObject resolveFileObject(String filePath, SshParameters sshParameters) throws FileSystemException {
        logger.trace("Resolving file: {}", filePath);
        if (filePath.startsWith(LocationType.sftp.toString())) {
            SftpFileSystemConfigBuilder builder = SftpFileSystemConfigBuilder.getInstance();
            builder.setStrictHostKeyChecking(fileSystemOptions, "no");
            builder.setUserDirIsRoot(fileSystemOptions, false);
            builder.setCompression(fileSystemOptions, "zlib,none");
        }

        AuthStore sessionAuthStore = new MemoryAuthStore();
        if (sshParameters != null) {
            String host = sshParameters.getHost();
            String user = sshParameters.getUser();
            String pwd = sshParameters.getSshpwd();
            String key = sshParameters.getSshkey();
            int port = sshParameters.getPort();
            if (host != null && user != null && pwd != null && key != null) {
                setAuthenticationFromSSHParameters(sessionAuthStore, host, user, pwd, key);
            }
        }
        return resolveFileObject(sessionAuthStore, filePath);
    }

    private static void setAuthenticationFromSSHParameters(AuthStore sessionAuthStore, String host, String user, String pwd, String key) {
        UserAuthenticationInfo auInfo = new UserAuthenticationInfo(Protocol.SFTP.getName(), host, user);
        UserAuthenticationDataWrapper authenticationData = new UserAuthenticationDataWrapper();
        authenticationData.setData(UserAuthenticationData.USERNAME, user.toCharArray());
        authenticationData.setData(UserAuthenticationData.PASSWORD, pwd.toCharArray());
        authenticationData.setData(UserAuthenticationDataWrapper.SSH_KEY, key.toCharArray());
        sessionAuthStore.add(auInfo, authenticationData);
    }

    private static FileObject resolveFileObject(AuthStore sessionAuthStore, String filePath) throws FileSystemException {
        UserAuthenticatorFactory factory = new UserAuthenticatorFactory();
        OtrosUserAuthenticator authenticator = factory.getUiUserAuthenticator(sessionAuthStore, filePath, fileSystemOptions);
        if (filePath.startsWith(LocationType.sftp.toString())) {
            SftpFileSystemConfigBuilder builder = SftpFileSystemConfigBuilder.getInstance();
            builder.setStrictHostKeyChecking(fileSystemOptions, "no");
            builder.setUserDirIsRoot(fileSystemOptions, false);
            builder.setCompression(fileSystemOptions, "zlib,none");

        }

        DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions, authenticator);
        FileObject resolveFile;
        try {
            resolveFile = getFileSystemManager().resolveFile(filePath, fileSystemOptions);// SLOW ACTION (circa 120ms)
            resolveFile.getType();
        } catch (FileSystemException e) {
            logger.error("Error resolving file " + filePath, e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return resolveFile;
    }

    public static boolean exists(FileObject fileObject) {
        if (fileObject == null) {
            return false;
        }

        try {
            return fileObject.exists();
        } catch (FileSystemException ex) {
            return false;
        }
    }

    public static boolean isDirectory(FileObject fileObject) {
        try {
            return fileObject.getType().equals(FileType.FOLDER);
        } catch (FileSystemException ex) {
            logger.info("Exception when checking if fileobject is folder", ex);
            return false;
        }
    }

    public static boolean isLocalFile(FileObject fileObject) {
        try {
            return fileObject.getURL().getProtocol().equalsIgnoreCase("file") && FileType.FILE.equals(fileObject.getType());
        } catch (FileSystemException e) {
            logger.info("Exception when checking if fileobject is local file", e);
            return false;
        }
    }

    public static boolean isFileSystemRoot(FileObject folder) {
        return isRoot(folder);
    }

    public static boolean isParent(FileObject folder, FileObject file) {
        try {
            FileObject parent = file.getParent();

            return parent != null && parent.equals(folder);

        } catch (FileSystemException ex) {
            return false;
        }
    }

    public static String getRemoteUserHome(SshParameters sshParameters) throws FileSystemException {
        return "/home/" + sshParameters.getUser();
    }

    public static FileObject getUserHome() throws FileSystemException {
        return resolveFileObject(Util.isUnix() ? PrefUtil.USER_DIR : PrefUtil.USER_HOME);
    }

    public static void checkForSftpLinks(FileObject[] files, TaskContext taskContext) {
        logger.trace("Checking for SFTP links");
        taskContext.setMax(files.length);
        long ts = System.currentTimeMillis();
        for (int i = 0; i < files.length && !taskContext.isStop(); i++) {
            FileObject fileObject = files[i];
            try {
                if (fileObject instanceof SftpFileObject) {
                    SftpFileObject sftpFileObject = (SftpFileObject) fileObject;
                    long size = sftpFileObject.getContent().getSize();
                    if (sftpFileObject.getType() == FileType.FILE && size < SYMBOLIC_LINK_MAX_SIZE && size != 0) {
                        if (!pointToItself(sftpFileObject)) {
                            files[i] = new LinkFileObject(sftpFileObject);
                        }
                    }

                }
                taskContext.setCurrentProgress(i);
            } catch (Exception e) {

            }

        }
        long checkDuration = System.currentTimeMillis() - ts;
        logger.trace("Checking SFTP links took {} ms [{}ms/file]", checkDuration, (float) checkDuration / files.length);
    }

    public static boolean pointToItself(FileObject fileObject) throws FileSystemException {
        if (!fileObject.getURL().getProtocol().equalsIgnoreCase("file") && FileType.FILE.equals(fileObject.getType())) {
            logger.trace("Checking if {} is pointing to itself", fileObject.getName().getFriendlyURI());
            FileObject[] children = VFSUtils.getChildren(fileObject);
            logger.trace("Children number of {} is {}", fileObject.getName().getFriendlyURI(), children.length);
            if (children.length == 1) {
                FileObject child = children[0];
                if (child.getContent().getSize() != child.getContent().getSize()) {
                    return false;
                }
                if (child.getName().getBaseName().equals(fileObject.getName().getBaseName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static FileObject[] getChildren(FileObject fileObject) throws FileSystemException {
        FileObject[] result;
        if (isLocalFileSystem(fileObject) && isArchive(fileObject)) {
            String extension = fileObject.getName().getExtension();
            result = VFSUtils.resolveFileObject(extension + ":" + fileObject.getURL().toString() + "!/").getChildren();
        } else {
            result = fileObject.getChildren();
        }
        return filterInvalidFiles(result);
    }

    private static FileObject[] filterInvalidFiles(FileObject[] result) {
        List<FileObject> validFileObjects = new ArrayList<>();
        for (FileObject fo : result) {
            if (fo.getName() instanceof InvalidFileName) {
                logger.warn("Invalid filename filtered: {}", ((InvalidFileName) fo.getName()).getOriginalName());
            } else {
                if (isLocalFileSystem(fo) && isInvalidFolder(fo)) {
                    logger.warn("Invalid folder filtered: {}", fo.getName());
                } else {
                    validFileObjects.add(fo);
                }
            }
        }
        return validFileObjects.toArray(new FileObject[0]);
    }

    private static boolean isInvalidFolder(FileObject folder) {
        try {
            if (folder.getType() != FileType.FOLDER) {
                return false;
            }

            File file = new File(decode(folder.getName().getURI(), null));
            String[] files = UriParser.encode(file.list());

            return files == null;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isArchive(FileObject fileObject) {
        return isArchive(fileObject.getName());
    }

    public static boolean isArchive(FileName fileName) {
        String extension = fileName.getExtension();
        return archivesSuffixes.contains(extension.toLowerCase());
    }

    private static boolean isLocalFileSystem(FileObject fileObject) {
        return fileObject.getName().getScheme().equalsIgnoreCase("file");
    }

    public static Icon getIconForFileSystem(String url) {
        String schema = "file";
        if (null != url) {
            int indexOf = url.indexOf(UNIX_PROTOCOL_PREFIX);
            if (indexOf > 0) {
                schema = url.substring(0, indexOf);
            }
        }
        return schemeIconMap.get(schema);
    }

    public static boolean canGoUrl(FileObject fileObject) throws FileSystemException {
        if (VFSUtils.pointToItself(fileObject)) {
            return false;
        }
        if (VFSUtils.isLocalFile(fileObject)) {
            return false;
        }
        return true;

    }

    public static String encode(String url, SshParameters sshParameters) {
        String fixedUrl = url.replace("\\", "/");
        if (sshParameters != null) {
            String host = sshParameters.getHost();
            String port = String.valueOf(sshParameters.getPort());
            String typePrefix = LocationType.sftp.toString();
            if (port.equals(DEFAULT_SSH_PORT)) {
                return typePrefix + host + fixedUrl;
            } else {
                return typePrefix + host + ":" + port + fixedUrl;
            }
        } else {
            String typePrefix = LocationType.file.toString();
            return typePrefix + fixedUrl;
        }
    }

    public static String decode(String path, SshParameters sshParameters) {
        if (path.startsWith(LocationType.file.toString())) {
            return StringUtils.removeStart(path, LocationType.file.toString());
        } else if (path.startsWith(LocationType.sftp.toString()) && sshParameters != null) {
            String noType = StringUtils.removeStart(path, LocationType.sftp.toString());
            String noHost = StringUtils.removeStart(noType, sshParameters.getHost());
            String noPort = StringUtils.removeStart(noHost, ":" + sshParameters.getPort());
            return noPort;
        }
        return null;
    }

    public static void showErrorMessage(final Component parent, final String url, final Exception e) {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                String message = "Error opening " + url;
                if (e != null) {
                    message += "\n" + e.getMessage();
                    if (message.contains("descendent")) {
                        message = message.replace("descendent", "");
                    }
                }
                logger.error(message);
                JOptionPane.showMessageDialog(parent, message, "File System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
