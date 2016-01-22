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

package eu.engys.util.filechooser.authentication.authenticator;

import java.awt.BorderLayout;
import java.text.MessageFormat;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.UserAuthenticationData.Type;

import eu.engys.util.filechooser.authentication.UserAuthenticationDataWrapper;
import eu.engys.util.filechooser.uri.VFSURIParser;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractUiUserAuthenticator implements OtrosUserAuthenticator {

	private String url;
	private VFSURIParser vfsUriParser;
	private final FileSystemOptions fileSystemOptions;
	protected UserAuthenticationDataWrapper data;
	private final String title;

	public AbstractUiUserAuthenticator(String url, FileSystemOptions fileSystemOptions) {
		this.url = url;
		this.title = MessageFormat.format(AUTHENTICATOR_ENTERCREDENTIALSFORURL, url);
		this.fileSystemOptions = fileSystemOptions;
		this.vfsUriParser = new VFSURIParser(url);
	}

	@Override
	public UserAuthenticationData requestAuthentication(Type[] types) {
		ExecUtil.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				JPanel authOptionPanel = getOptionsPanelBuilder().getPanel();

				JPanel panel = new JPanel(new BorderLayout());
				panel.add(authOptionPanel);

				String[] options = { "OK", "Cancel" };
				int showConfirmDialog = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if (showConfirmDialog != JOptionPane.OK_OPTION) {
					return;
				}
				data = new UserAuthenticationDataWrapper();
				updateAuthenticationData(data);
			}
		});
		return data;
	}

	@Override
	public UserAuthenticationDataWrapper getLastUserAuthenticationData() {
		return data;
	}

	// @Override
	// public boolean isPasswordSave() {
	// return saveCredentialsCheckBox.isSelected();
	// }

	protected abstract void updateAuthenticationData(UserAuthenticationData authenticationData);

	protected abstract PanelBuilder getOptionsPanelBuilder();

	protected String getUrl() {
		return url;
	}

	protected VFSURIParser getVfsUriParser() {
		return vfsUriParser;
	}

	protected FileSystemOptions getFileSystemOptions() {
		return fileSystemOptions;
	}

	/**
	 * Resources
	 */

	private static final String AUTHENTICATOR_SAVEPASSWORD = ResourcesUtil.getString("authenticator.savePassword");
	private static final String AUTHENTICATOR_ENTERCREDENTIALSFORURL = ResourcesUtil.getString("authenticator.enterCredentialsForUrl");

}
