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

package eu.engys.util.filechooser.authentication;

import org.apache.commons.vfs2.FileSystemOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.authentication.authenticator.AbstractUiUserAuthenticator;
import eu.engys.util.filechooser.authentication.authenticator.OtrosUserAuthenticator;
import eu.engys.util.filechooser.authentication.authenticator.UseCentralsFromSessionUserAuthenticator;

public class UserAuthenticatorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticatorFactory.class);

    public OtrosUserAuthenticator getUiUserAuthenticator(AuthStore sessionAuthStore, String url, FileSystemOptions fileSystemOptions) {
        LOGGER.info("Getting authenticator for {}", url);
        AbstractUiUserAuthenticator authenticator = null;
//        if (url.startsWith("sftp://")) {
//            authenticator = new SftpUserAuthenticator(url, fileSystemOptions);
//        }
        UseCentralsFromSessionUserAuthenticator fromSessionUserAuthenticator = new UseCentralsFromSessionUserAuthenticator(sessionAuthStore, url, fileSystemOptions, authenticator);
        return fromSessionUserAuthenticator;

    }

}
