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


package eu.engys.util;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import eu.engys.util.ui.ASCIIArt;

public class ApplicationInfo {

    private static final String DEFAULT_COPYRIGHT = "productCopyright";
    private static final String DEFAULT_SITE = "productSite";
    private static final String DEFAULT_MAIL = "productMail";
    private static final String DEFAULT_NUMBER = "-1";
    private static final String DEFAULT_VENDOR = "productVendor";
    private static final String DEFAULT_NAME = "productName";
    private static final String BUILD_KEY = "build";
    private static final String VERSION_KEY = "version";
    private static final String COPYRIGHT_KEY = "copyright";
    private static final String SITE_KEY = "site";
    private static final String MAIL_KEY = "mail";
    private static final String VENDOR_KEY = "vendor";
    private static final String NAME_KEY = "name";

    private static String name;
    private static String vendor;
    private static String versionNumber;
    private static String majorNumber;
    private static String minorNumber;
    private static String buildDate;
    private static String mail;
    private static String site;
    private static String copyright;

    public static void init() {
        try {
            ResourceBundle version = ResourceBundle.getBundle("eu.engys.resources.version");
            name = version.getString(NAME_KEY);
            vendor = version.getString(VENDOR_KEY);
            mail = version.getString(MAIL_KEY);
            site = version.getString(SITE_KEY);
            copyright = version.getString(COPYRIGHT_KEY);

            String v = version.getString(VERSION_KEY);

            try (Scanner s = new Scanner(v)) {
                s.useDelimiter("\\.");
                versionNumber = s.next();
                majorNumber = s.next();
                minorNumber = s.next();
            } catch (Exception e) {
                name = DEFAULT_NAME;
                vendor = DEFAULT_VENDOR;
                versionNumber = DEFAULT_NUMBER;
                majorNumber = DEFAULT_NUMBER;
                minorNumber = DEFAULT_NUMBER;
                mail = DEFAULT_MAIL;
                site = DEFAULT_SITE;
                copyright = DEFAULT_COPYRIGHT;
            }

            buildDate = version.getString(BUILD_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            name = DEFAULT_NAME;
            vendor = DEFAULT_VENDOR;
            versionNumber = DEFAULT_NUMBER;
            majorNumber = DEFAULT_NUMBER;
            minorNumber = DEFAULT_NUMBER;
            mail = DEFAULT_MAIL;
            site = DEFAULT_SITE;
            copyright = DEFAULT_COPYRIGHT;
        }
    }

    public static String getTitle() {
        return name != null ? ASCIIArt.toAA(name) : "";
    }

    public static String getName() {
        return name != null ? name : DEFAULT_NAME;
    }

    public static String getLicenseServerName() {
        return name != null ? name + "_LICENSE_SERVER_NAME" : DEFAULT_NAME;
    }

    public static String getLicenseServerPort() {
        return name != null ? name + "_LICENSE_SERVER_PORT" : DEFAULT_NAME;
    }

    public static String getVendor() {
        return vendor != null ? vendor : DEFAULT_VENDOR;
    }

    public static String getMail() {
        return mail != null ? mail : DEFAULT_MAIL;
    }

    public static String getSite() {
        return site != null ? site : DEFAULT_SITE;
    }

    public static String getCopyright() {
        return copyright != null ? copyright : DEFAULT_COPYRIGHT;
    }

    public static String getVersionRelease() {
        return versionNumber;
    }

    public static String getVersionMajor() {
        return majorNumber;
    }

    public static String getVersionMinor() {
        return minorNumber;
    }

    public static String getBuildDate() {
        return buildDate;
    }

    public static String getVersion() {
        return "v" + versionNumber + "." + majorNumber + "." + minorNumber;
    }

    public static String getRootPath() {
        URL appJarURL = ApplicationInfo.class.getProtectionDomain().getCodeSource().getLocation();
        File appJarFile;
        try {
            appJarFile = new File(appJarURL.toURI());
        } catch (URISyntaxException e) {
            appJarFile = new File(appJarURL.getPath());
        }
        return appJarFile.getParentFile().getParent();
    }

    public static File getHome() {
        String userHome = FileUtils.getUserDirectoryPath();
        File userDir;
        try {
            userDir = new File(userHome, "." + getName());
        } catch (Exception e) {
            userDir = new File(userHome, ".test");
        }

        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        return userDir;
    }

    public static File getPrefsFile() {
        final File userHome = getHome();
        final File userPrefs = new File(userHome, "application.properties");
        return userPrefs;
    }

    public static String getHeaderInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTitle());
        sb.append("\n PRODUCT");
        sb.append("\n -----------------------------------------------------");
        sb.append("\n     Name:          " + getName());
        sb.append("\n     Vendor:        " + getVendor());
        sb.append("\n     Release Date:  " + getBuildDate());
        sb.append("\n     Version:       " + getVersion());
        sb.append("\n     Mail:          " + getMail());
        sb.append("\n     Site:          " + getSite());
        sb.append("\n     Copyright:     " + getCopyright());
        sb.append("\n");
        sb.append("\n SYSTEM");
        sb.append("\n -----------------------------------------------------");
        sb.append("\n     Date:          " + new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").format(new Date()));
        sb.append("\n     OS:            " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
        sb.append("\n     Language       " + Locale.getDefault().getLanguage());
        sb.append("\n     Country        " + Locale.getDefault().getCountry());
        sb.append("\n");
        String hostName = "";
        String hostIp = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostName = address.getHostName();
            hostIp = address.getHostAddress();
        } catch (UnknownHostException ex) {
            hostName = "UNKNOWN";
            hostIp = "UNKNOWN";
        }
        sb.append("\n NETWORK");
        sb.append("\n -----------------------------------------------------");
        sb.append("\n     Hostname       " + hostName);
        sb.append("\n     Ip             " + hostIp);
        sb.append("\n");
        sb.append("\n JAVA");
        sb.append("\n -----------------------------------------------------");
        sb.append("\n     Version        " + System.getProperty("java.version"));
        sb.append("\n     Vendor         " + System.getProperty("java.vendor"));
        sb.append("\n     Home           " + System.getProperty("java.home"));
        sb.append("\n     ClassVersion   " + System.getProperty("java.class.version"));
        sb.append("\n     ClassPath      " + getClassPath());
        sb.append("\n");
        sb.append("\n USER");
        sb.append("\n -----------------------------------------------------");
        sb.append("\n     Name           " + System.getProperty("user.name"));
        sb.append("\n     Home           " + System.getProperty("user.home"));
        sb.append("\n     Dir            " + System.getProperty("user.dir"));
        if (System.getProperty("license.status") != null) {
            sb.append("\n LICENSE");
            sb.append("\n -----------------------------------------------------");
            sb.append("\n             " + System.getProperty("license.status"));
            sb.append("\n     Register       " + System.getProperty("license.register"));
            sb.append("\n     Exp. Date      " + System.getProperty("license.exp.date"));
        }
        return sb.toString();
    }

    public static String getClassPath() {
        return System.getProperty("java.class.path").replace(File.pathSeparator, "\n                   ");
    }
}
