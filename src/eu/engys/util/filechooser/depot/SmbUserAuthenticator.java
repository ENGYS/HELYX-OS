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
//package eu.engys.util.otrosfilechooser.depot;
//
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//
//import org.apache.commons.vfs2.FileSystemOptions;
//import org.apache.commons.vfs2.UserAuthenticationData;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import eu.engys.util.otrosfilechooser.authentication.AuthStore;
//import eu.engys.util.ui.ResourcesUtil;
//import eu.engys.util.ui.builder.PanelBuilder;
//
//public class SmbUserAuthenticator extends FTPUserAuthenticator {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(SmbUserAuthenticator.class);
//
//    private JTextField fieldTextField;
//
//    public SmbUserAuthenticator(AuthStore authStore, String url, FileSystemOptions fileSystemOptions) {
//        super(authStore, url, fileSystemOptions);
//    }
//
//    @Override
//    public UserAuthenticationData requestAuthentication(UserAuthenticationData.Type[] types) {
//        LOGGER.debug("Requested for authentication");
//        for (UserAuthenticationData.Type type : types) {
//            LOGGER.debug("Requested for authentication: %s", type);
//        }
//        if (data == null) {
//            return super.requestAuthentication(types);
//        } else {
//            return data;
//        }
//    }
//
//    @Override
//    protected void updateAuthenticationData(UserAuthenticationData authenticationData) {
//        super.updateAuthenticationData(authenticationData);
//        authenticationData.setData(UserAuthenticationData.DOMAIN, fieldTextField.getText().toCharArray());
//    }
//
//    @Override
//    protected void userSelectedHook(UserAuthenticationData userAuthenticationData) {
//        char[] domain = new char[0];
//        if (userAuthenticationData != null) {
//            domain = userAuthenticationData.getData(UserAuthenticationData.DOMAIN);
//        }
//        fieldTextField.setText(new String(domain));
//
//    }
//
//    @Override
//    protected PanelBuilder getOptionsPanelBuilder() {
//        PanelBuilder panel = super.getOptionsPanelBuilder();
//        fieldTextField = new JTextField(15);
//        panel.addComponent(AUTHENTICATOR_DOMAIN, fieldTextField);
//        return panel;
//    }
//    
//    /**
//     * Resources
//     */
//
//    private static final String AUTHENTICATOR_DOMAIN = ResourcesUtil.getString("authenticator.domain");
//
//}
