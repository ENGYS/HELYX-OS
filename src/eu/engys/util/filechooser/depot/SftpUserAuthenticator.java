/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/
package eu.engys.util.filechooser.depot;
///*
// * Copyright 2012 Krzysztof Otrebski (krzysztof.otrebski@gmail.com)
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package eu.engys.util.otrosfilechooser.authentication.authenticator;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//
//import javax.swing.JButton;
//import javax.swing.JFileChooser;
//import javax.swing.JLabel;
//import javax.swing.JTextField;
//
//import net.java.dev.designgridlayout.Componentizer;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.vfs2.FileSystemException;
//import org.apache.commons.vfs2.FileSystemOptions;
//import org.apache.commons.vfs2.UserAuthenticationData;
//import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
//
//import eu.engys.util.otrosfilechooser.authentication.UserAuthenticationDataWrapper;
//import eu.engys.util.ui.ResourcesUtil;
//import eu.engys.util.ui.builder.PanelBuilder;
//
//public class SftpUserAuthenticator extends FTPUserAuthenticator {
//
//	private JTextField sshKeyFileField;
//	private static JFileChooser chooser;
//
//	public SftpUserAuthenticator(String url, FileSystemOptions fileSystemOptions) {
//		super(url, fileSystemOptions);
//	}
//
//	@Override
//	protected void updateAuthenticationData(UserAuthenticationData authenticationData) {
//		super.updateAuthenticationData(authenticationData);
//		authenticationData.setData(UserAuthenticationDataWrapper.SSH_KEY, sshKeyFileField.getText().trim().toCharArray());
//
//		if (StringUtils.isNotBlank(sshKeyFileField.getText())) {
//			try {
//				SftpFileSystemConfigBuilder.getInstance().setIdentities(getFileSystemOptions(), new File[] { new File(sshKeyFileField.getText()) });
//				// TODO set user auth data file path
//			} catch (FileSystemException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	@Override
//	protected PanelBuilder getOptionsPanelBuilder() {
//		if (sshKeyFileField == null) {
//			sshKeyFileField = new JTextField(15);
//		}
//		if (chooser == null) {
//			chooser = new JFileChooser();
//		}
//		PanelBuilder builder = super.getOptionsPanelBuilder();
//
//		JButton browseButton = new JButton("...");
//		browseButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				chooser.setMultiSelectionEnabled(false);
//				chooser.setDialogTitle(AUTHENTICATOR_SELECTSSHKEY);
//				int showOpenDialog = chooser.showOpenDialog(null);
//				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
//					sshKeyFileField.setText(chooser.getSelectedFile().getAbsolutePath());
//				}
//			}
//		});
//		builder.addComponent(AUTHENTICATOR_SSHKEYFILE, Componentizer.create().prefAndMore(sshKeyFileField).minToPref(browseButton).component());
//		builder.addComponent(new JLabel(AUTHENTICATOR_SSHKEYFILEDESCRIPTION));
//
//		return builder;
//	}
//
//	@Override
//	protected void userSelectedHook(UserAuthenticationData userAuthenticationData) {
//		if (userAuthenticationData != null) {
//			char[] sshKeyPath = userAuthenticationData.getData(UserAuthenticationDataWrapper.SSH_KEY);
//			String path = "";
//			if (sshKeyPath != null && sshKeyPath.length > 0) {
//				path = new String(sshKeyPath);
//			}
//			sshKeyFileField.setText(path);
//		}
//	}
//
//	/**
//	 * Resources
//	 */
//
//	private static final String AUTHENTICATOR_SSHKEYFILE = ResourcesUtil.getString("authenticator.sshKeyFile");
//	private static final String AUTHENTICATOR_SELECTSSHKEY = ResourcesUtil.getString("authenticator.selectSshKey");
//	private static final String AUTHENTICATOR_SSHKEYFILEDESCRIPTION = ResourcesUtil.getString("authenticator.sshKeyFileDescription");
//
//}
