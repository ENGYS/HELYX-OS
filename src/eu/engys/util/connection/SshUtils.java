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

package eu.engys.util.connection;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class SshUtils {

	public static final int TIMEOUT = 21000;

	private static final Logger logger = LoggerFactory.getLogger(SshUtils.class);

	private static final URL PLINK_URL = SshUtils.class.getClassLoader().getResource("eu/engys/gui/vtk/depot/ssh/plink.exe");

	public enum Terminal {
		XTERM, GNOMETERMINAL, KONSOLE
	}

	public static boolean testTelnetConnection(String machine, int port) {
		TelnetClient c = new TelnetClient();
		try {
			c.connect(machine, port);
			c.disconnect();
			return true;
		} catch (IOException e) {
			logger.error("TELNET: {}", e.getMessage());
			return false;
		}
	}

	public static boolean testPingConnection(String machine) {
		try {
			InetAddress address = InetAddress.getByName(machine);
			address.isReachable(3000);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	private static void checkParameters(SshParameters parameters) throws JSchException {
		if (parameters.getUser() == null || parameters.getUser().isEmpty())
			throw new JSchException("Username not set");
		if (parameters.getHost() == null || parameters.getHost().isEmpty())
			throw new JSchException("Hostname not set");
	}

	public static Session createSession(SshParameters parameters) throws JSchException {
		checkParameters(parameters);

		String user = parameters.getUser();
		String host = parameters.getHost();
		int port = parameters.getPort();
		String passwd = parameters.getSshpwd();
		boolean authWithKey = parameters.getSshauth().isKey();

		if (authWithKey) {
			Path key = Paths.get(parameters.getSshkey());
			return createSession(user, host, key, port);
		} else {
			return createSession(user, host, passwd, port);
		}
	}

	private static Session createSession(String user, String host, String password, int port) throws JSchException {
		Session session = new JSch().getSession(user, host, port);
		session.setPassword(password);
		UserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);
		session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
		session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
		session.setConfig("compression_level", "9");
		logger.info("CREATE SESSION: CONNECT");
		session.connect(TIMEOUT);
		logger.info("CREATE SESSION: CONNECTED");
		return session;
	}

	private static Session createSession(String user, String host, Path privateKey, int port) throws JSchException {
		JSch jsch = new JSch();
		jsch.addIdentity(privateKey.toString());
		Session session = jsch.getSession(user, host, port);
		UserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);
		session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
		session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
		session.setConfig("compression_level", "9");
		session.connect(20000);
		return session;
	}

	public static ChannelExec createEXEChannel(Session session) throws JSchException {
		Channel channel = session.openChannel("exec");
		logger.info("EXE CHANNEL: CREATED");
		return (ChannelExec) channel;
	}

	public static ChannelSftp createSFTPChannel(Session session) throws JSchException {
		Channel channel = session.openChannel("sftp");
		logger.info("SFTP CHANNEL: OPENED");
		channel.connect();
		logger.info("SFTP CHANNEL: CONNECTED");
		return (ChannelSftp) channel;
	}

	public static void uploadFileOrFolder(Path localFile, String remoteDestination, ChannelSftp channel, ProgressMonitor monitor) throws SftpException, JSchException, IOException {
		channel.cd(remoteDestination.toString());
		String remoteFile = remoteDestination + getFilePathSeparator(remoteDestination) + localFile.getFileName();
		if (remoteFileAlreadyExists(remoteFile, channel)) {
			int res = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "File " + remoteFile + " already exists. Override?");
			if (res == JOptionPane.OK_OPTION) {
				if (localFile.toFile().isDirectory()) {
					// channel.rmdir gives an error
					execSSHCommand("rm -rf " + remoteFile.toString(), channel.getSession());
				} else {
					channel.rm(localFile.getFileName().toString());
				}
				createFileOrFolder(localFile, remoteDestination, channel, monitor);
			}
		} else {
			createFileOrFolder(localFile, remoteDestination, channel, monitor);
		}
	}

	public static void uploadFileOrFolderForced(Path localFile, String remoteDestination, ChannelSftp channel, ProgressMonitor monitor) throws SftpException, JSchException, IOException {
		channel.cd(remoteDestination.toString());
		createFileOrFolder(localFile, remoteDestination, channel, monitor);
	}

	public static void createFileOrFolder(Path localFile, String remoteDestination, ChannelSftp channel, ProgressMonitor monitor) throws SftpException, JSchException, IOException {
		if (localFile.toFile().isDirectory()) {
			channel.mkdir(localFile.getFileName().toString());
			if (monitor != null) {
                monitor.info("New folder: " + localFile.getFileName().toString());
			}
			for (File file : localFile.toFile().listFiles()) {
				uploadFileOrFolder(Paths.get(file.toURI()), remoteDestination + getFilePathSeparator(remoteDestination) + localFile.getFileName(), channel, monitor);
			}
		} else {
			if (monitor != null) {
				monitor.info("Uploading: " + localFile.toString());
			}
			channel.put(new FileInputStream(localFile.toFile()), localFile.getFileName().toString(), monitor != null ? new UploadProgressMonitor(monitor, (int) Files.size(localFile)) : null);
		}
	}

	public static void removeFileOrFolder(Path localFile, String remoteFile, ChannelSftp channel, ProgressMonitor monitor) throws JSchException, IOException, SftpException {
		if (localFile.toFile().isDirectory()) {
			execSSHCommand("rm -rf " + remoteFile.toString(), channel.getSession());
		} else {
			channel.rm(localFile.getFileName().toString());
		}
	}
	
	public static void removeFile(String remoteFolder, String fileName, ChannelSftp channel) throws SftpException {
		channel.cd(remoteFolder);
		channel.rm(fileName);
	}

	public static boolean remoteFileAlreadyExists(String file, ChannelSftp channel) throws SftpException {
		try {
			SftpATTRS attrs = channel.stat(file);
			return attrs != null;
		} catch (SftpException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static void downloadFolder(String remoteFolder, Path localDestination, ChannelSftp channel, ProgressMonitor monitor) throws SftpException {
		Path localFolder = localDestination.resolve(new File(remoteFolder).getName());
		if (!localFolder.toFile().exists()) {
			localFolder.toFile().mkdirs();
		}
		Vector<ChannelSftp.LsEntry> list = channel.ls(remoteFolder.toString());
		for (ChannelSftp.LsEntry file : list) {
			if (!isCurrentOrParentDir(file)) {
				if (file.getAttrs().isDir()) {
					downloadFolder(remoteFolder + getFilePathSeparator(remoteFolder) + file.getFilename(), localFolder, channel, monitor);
				} else {
					downloadFile(remoteFolder + getFilePathSeparator(remoteFolder) + file.getFilename(), localFolder, channel, monitor);
				}
			}

		}
	}

	public static class UploadProgressMonitor implements SftpProgressMonitor {

		private ProgressMonitor monitor;
		private int total;

		public UploadProgressMonitor(ProgressMonitor monitor, int total) {
			this.monitor = monitor;
			this.total = total;
		}

		@Override
		public void init(int op, String src, String dest, long max) {
			monitor.setTotal(total);
			monitor.setCurrent(null, 0);
		}

		@Override
		public boolean count(long l) {
			monitor.setCurrent(null, monitor.getCurrent() + (int) l);
			return true;
		}

		@Override
		public void end() {
		}
	}

	public static class DownloadProgressMonitor implements SftpProgressMonitor {

		private ProgressMonitor monitor;

		public DownloadProgressMonitor(ProgressMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
		public void init(int op, String src, String dest, long max) {
			monitor.setTotal((int) max);
			monitor.setCurrent(null, 1);
			monitor.infoN(src + " -> " + dest);
		}

		@Override
		public boolean count(long l) {
			monitor.setCurrent(null, monitor.getCurrent() + (int) l);
			return true;
		}

		@Override
		public void end() {
			monitor.info(" DONE");
		}
	}

	public static void downloadFile(String remoteFile, Path localDestination, ChannelSftp channel, ProgressMonitor monitor) throws SftpException {
		channel.get(remoteFile, localDestination.toString(), new DownloadProgressMonitor(monitor));
	}

	public static void downloadFile(String remoteFile, Path localDestination, ChannelSftp channel) throws SftpException {
		channel.get(remoteFile, localDestination.toString());
	}

	public static void exexRemoteScriptInLocalShell(String user, String host, String privateKeyPath, String scriptFile) throws IOException, InterruptedException {
		String nameOS = System.getProperty("os.name");
		String command = null;
		if (nameOS.startsWith("Windows")) {
			String openTerminalcommand = "cmd /C start cmd.exe /K";
			String plinkPath = PLINK_URL.getFile().substring(1);
			String sshCommand = "-ssh " + host + " -l " + user + " -i " + privateKeyPath + " -m";
			File scriptLauncher = createScriptLauncher(scriptFile);
			String scriptLauncherPath = scriptLauncher.getAbsolutePath();
			command = openTerminalcommand + " " + plinkPath + " " + sshCommand + " " + scriptLauncherPath;
		} else if (nameOS.startsWith("Linux")) {
			Terminal terminal = getConsoleType();
			String openTerminalcommand = null;
			switch (terminal) {
			case GNOMETERMINAL:
				openTerminalcommand = "gnome-terminal -x";
				break;
			case KONSOLE:
				openTerminalcommand = "konsole -e";
				break;
			case XTERM:
				openTerminalcommand = "xterm -x";
				break;
			default:
				break;
			}

			String sshCommand = "ssh " + user + "@" + host + " -i " + privateKeyPath.toString() + " " + scriptFile;
			command = openTerminalcommand + " " + sshCommand;

		} else {
			System.out.println("OS NOT SUPPORTED!");
		}
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
	}

	public static void execSSHCommand(String command, Session session) throws JSchException, IOException {
		execSSHCommand(command, session, null);
	}

	public static void execSSHCommand(String command, Session session, Map<String, String> env) throws JSchException, IOException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(addEnvToCommand(command, env));
		channel.setInputStream(null);
		// InputStream stdout = channel.getInputStream();
		InputStream stderr = channel.getErrStream();

		channel.connect();

		waitForChannelClosed(channel);

		String ERR = IOUtils.toString(stderr);
		// String OUT = IOUtils.toString(stdout);

		// System.out.println("SSHUtils.execSSHCommand() OUT: "+OUT);

		try {
			if (channel.getExitStatus() > 0) {
				throw new JSchException(ERR);
			}
		} finally {
			channel.disconnect();
		}
	}

	public static String addEnvToCommand(String command, Map<String, String> env) {
		StringBuilder sb = new StringBuilder();
		if (env != null) {
			for (String key : env.keySet()) {
				sb.append("export " + key + "=" + env.get(key));
				sb.append(" && ");
			}
		}
		sb.append(command);
		return sb.toString();
	}

	public static String addProfileLoaderToCommand(String command) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("[[ -e ~/.profile ]] && source ~/.profile; ");
	    sb.append("[[ -e ~/.bash_profile ]] && source ~/.bash_profile; ");
	    sb.append(command);
	    return sb.toString();
	}

	private static void waitForChannelClosed(ChannelExec channel) {
		while (channel.getExitStatus() == -1 && !channel.isClosed()) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
	}

	public static void makeExecutable(String filePath, Session session) {
		try {
			execSSHCommand("chmod +x " + filePath, session);
		} catch (JSchException | IOException e) {
			e.printStackTrace();
		}
	}

	private static Terminal getConsoleType() throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec("which gnome-terminal");
		p.waitFor();
		if (p.exitValue() == 0) {
			return Terminal.GNOMETERMINAL;
		}
		Process p1 = Runtime.getRuntime().exec("which konsole");
		p1.waitFor();
		if (p1.exitValue() == 0) {
			return Terminal.KONSOLE;
		}
		return Terminal.XTERM;
	}

	private static File createScriptLauncher(String string) throws IOException {
		File file = File.createTempFile("xxx", null);
		FileWriter fstream = new FileWriter(file);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(string);
		out.close();
		return file;
	}

	public static boolean testConnection(SshParameters sshParameters) {
		return testConnection(sshParameters, null, false, false);
	}

	public static boolean testConnection(SshParameters sshParameters, Path localDestination, boolean testUpload, boolean testDownload) {
		logger.info("TEST SSH CONNECTION");
		try {
			Session session = createSession(sshParameters);

			// TEST FTP CONNECTION
			logger.info("TEST SFTP CONNECTION");
			ChannelSftp channel = SshUtils.createSFTPChannel(session);

			File tmpFile = File.createTempFile("xxx", null);

			String remoteDestination = sshParameters.getRemoteBaseDirParent();

			// TEST UPLOAD FILE
			if (testUpload) {
				logger.info("TEST SFTP CONNECTION: UPLOAD");
				uploadFileOrFolder(Paths.get(tmpFile.toURI()), remoteDestination, channel, null);
				execSSHCommand("rm " + remoteDestination + getFilePathSeparator(remoteDestination) + tmpFile.getName(), session);
			}

			// TEST DOWNLOAD FILE
			if (testDownload) {
				logger.info("TEST SFTP CONNECTION: DOWNLOAD");
				downloadFile(remoteDestination + getFilePathSeparator(remoteDestination) + tmpFile.getName(), localDestination, channel);
				new File(localDestination + File.separator + tmpFile.getName()).delete();
			}

			tmpFile.delete();
			channel.disconnect();
			session.disconnect();

			logger.info("TEST SSH CONNECTION: SUCCESS");

			return true;
		} catch (Exception e) {
			showErrorDialog(e);
			logger.error("TEST SSH CONNECTION: ERROR {} PARAMETERS ARE {}", e.getMessage(), sshParameters);
			return false;
		}
	}

	public static void showErrorDialog(Exception e) {
		showError(decodeErrorMessage(e));
	}

	public static String decodeErrorMessage(Exception e) {
		String message = "";
		if (e instanceof SftpException) {
			message = "Remote path does not exists!";
		} else if (e instanceof JSchException) {
			JSchException ex = ((JSchException) e);
			if (ex.getCause() != null && ex.getCause() instanceof UnknownHostException) {
				message = "Unknown host!";
			} else {
				message = "Wrong username or password/key!";
			}
		} else {
			message = "Unknown exception: " + e.getMessage();

		}
		return message;
	}

	private static void showError(final String message) {
		ExecUtil.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Connection failed!\n" + message, "SSH connection status", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private static boolean isCurrentOrParentDir(LsEntry file) {
		return file.getFilename().equals("..") || file.getFilename().equals(".");
	}

	public static String getFilePathSeparator(String path) {
		return path.indexOf("/") == -1 ? "\\" : "/";
	}

	private static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		private String passphrase;
		private JTextField passphraseField = null;

		public MyUserInfo() {
			ExecUtil.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					passphraseField = (JTextField) new JPasswordField(20);
				}
			});
		}

		public String getPassword() {
			return null;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return passphrase;
		}

		public boolean promptPassphrase(String message) {
			Object[] ob = { passphraseField };
			int result = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), ob, message, JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				passphrase = passphraseField.getText();
				return true;
			} else {
				return false;
			}
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), message, "SSH Info", JOptionPane.INFORMATION_MESSAGE);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(panel, "SSH Info", destination + ": " + name, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}
}
