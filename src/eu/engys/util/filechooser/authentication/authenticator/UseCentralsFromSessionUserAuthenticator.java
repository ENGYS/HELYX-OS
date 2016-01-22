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
 * Copyright (c) 2012. Krzysztof Otrebski
 * All right reserved
 */

package eu.engys.util.filechooser.authentication.authenticator;

import java.util.Collection;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.slf4j.Logger;

import eu.engys.util.filechooser.authentication.AuthStore;
import eu.engys.util.filechooser.authentication.UserAuthenticationDataWrapper;
import eu.engys.util.filechooser.uri.VFSURIParser;
import eu.engys.util.ui.builder.PanelBuilder;

public class UseCentralsFromSessionUserAuthenticator extends AbstractUiUserAuthenticator {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UseCentralsFromSessionUserAuthenticator.class);

	private final AuthStore sessionAuthStore;
	private final AbstractUiUserAuthenticator otrosUserAuthenticator;

	public UseCentralsFromSessionUserAuthenticator(AuthStore sessionAuthStore, String url, FileSystemOptions fileSystemOptions, AbstractUiUserAuthenticator otrosUserAuthenticator) {
		super(url, fileSystemOptions);
		this.sessionAuthStore = sessionAuthStore;
		this.otrosUserAuthenticator = otrosUserAuthenticator;
	}

	@Override
	public UserAuthenticationDataWrapper getLastUserAuthenticationData() {
		if (otrosUserAuthenticator != null) {
			return otrosUserAuthenticator.getLastUserAuthenticationData();
		}
		return null;

	}

	@Override
	protected void updateAuthenticationData(UserAuthenticationData authenticationData) {
		otrosUserAuthenticator.updateAuthenticationData(authenticationData);
	}

	@Override
	protected PanelBuilder getOptionsPanelBuilder() {
		return otrosUserAuthenticator.getOptionsPanelBuilder();
	}

	@Override
	public UserAuthenticationData requestAuthentication(UserAuthenticationData.Type[] types) {
		UserAuthenticationData userAuthenticationData = getStaticWorkingUserAuthForSmb(sessionAuthStore, getUrl());
		if (userAuthenticationData == null) {
			userAuthenticationData = otrosUserAuthenticator.requestAuthentication(types);
		}
		return userAuthenticationData;
	}

	protected UserAuthenticationData getStaticWorkingUserAuthForSmb(AuthStore authStore, String url) {
		LOGGER.debug("Checking if have credentials for {}", url);
		VFSURIParser parser = new VFSURIParser(url);
		if (parser.getHostname() != null) {
			Collection<UserAuthenticationDataWrapper> userAuthenticationDatas = authStore.getUserAuthenticationDatas(parser.getProtocol().toString(), parser.getHostname());
			LOGGER.debug("Credentials count: {}", userAuthenticationDatas.size());
			if (userAuthenticationDatas.size() > 0) {
				UserAuthenticationData authenticationDataFromStore = userAuthenticationDatas.iterator().next();
				LOGGER.debug("Returning static authenticator for {}", url);
				return authenticationDataFromStore;
			}
		}
		LOGGER.debug("Do not have credentials for {}", url);
		return null;
	}

}
