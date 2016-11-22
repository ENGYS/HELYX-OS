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
//import java.util.Collection;
//
//import javax.swing.JComboBox;
//import javax.swing.JPasswordField;
//import javax.swing.event.AncestorEvent;
//import javax.swing.event.AncestorListener;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.vfs2.FileSystemOptions;
//import org.apache.commons.vfs2.UserAuthenticationData;
//
//import eu.engys.util.otrosfilechooser.authentication.AuthStore;
//import eu.engys.util.otrosfilechooser.authentication.UserAuthenticationDataWrapper;
//import eu.engys.util.otrosfilechooser.authentication.UserAuthenticationInfo;
//import eu.engys.util.ui.ResourcesUtil;
//import eu.engys.util.ui.builder.PanelBuilder;
//
//public class FTPUserAuthenticator extends AbstractUiUserAuthenticator {
//
//    protected JComboBox<?> nameTextField;
//    protected JPasswordField passTextField;
//
//    public FTPUserAuthenticator(String url, FileSystemOptions fileSystemOptions) {
//        super(url, fileSystemOptions);
//    }
//
//    @Override
//    protected void updateAuthenticationData(UserAuthenticationData authenticationData) {
//        authenticationData.setData(UserAuthenticationData.USERNAME, nameTextField.getSelectedItem().toString().toCharArray());
//        authenticationData.setData(UserAuthenticationData.PASSWORD, passTextField.getPassword());
//
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    @Override
//    protected PanelBuilder getOptionsPanelBuilder() {
//
//        Collection<UserAuthenticationDataWrapper> userAuthenticationDatas = getAuthStore().getUserAuthenticationDatas(getVfsUriParser().getProtocol().getName(), getVfsUriParser().getHostname());
//        String[] names = new String[userAuthenticationDatas.size()];
//        int i = 0;
//        for (UserAuthenticationData userAuthenticationData : userAuthenticationDatas) {
//            names[i] = new String(userAuthenticationData.getData(UserAuthenticationData.USERNAME));
//            i++;
//        }
//
//        nameTextField = new JComboBox(names);
//        nameTextField.setEditable(true);
//        nameTextField.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                userSelected(nameTextField.getSelectedItem().toString());
//            }
//        });
//
//        nameTextField.addAncestorListener(new AncestorListener() {
//
//            @Override
//            public void ancestorRemoved(AncestorEvent event) {
//
//            }
//
//            @Override
//            public void ancestorMoved(AncestorEvent event) {
//
//            }
//
//            @Override
//            public void ancestorAdded(AncestorEvent event) {
//                event.getComponent().requestFocusInWindow();
//            }
//        });
//        
//        passTextField = new JPasswordField(15);
//        passTextField.setText(getVfsUriParser().getPassword());
//
//        if (StringUtils.isNotBlank(getVfsUriParser().getUsername())) {
//            nameTextField.setSelectedItem(getVfsUriParser().getUsername());
//        }
//        if (names.length > 0) {
//            nameTextField.setSelectedIndex(0);
//        }
//        
//        PanelBuilder pb = new PanelBuilder();
//        pb.addComponent(AUTHENTICATOR_USERNAME, nameTextField);
//        pb.addComponent(AUTHENTICATOR_PASSWORD, passTextField);
//        
//        return pb;
//    }
//
//    private void userSelected(String user) {
//        UserAuthenticationData userAuthenticationData = getAuthStore().getUserAuthenticationData(new UserAuthenticationInfo(getVfsUriParser().getProtocol().getName(), getVfsUriParser().getHostname(), user));
//        char[] passChars = new char[0];
//
//        if (userAuthenticationData != null && userAuthenticationData.getData(UserAuthenticationData.PASSWORD) != null) {
//            passChars = userAuthenticationData.getData(UserAuthenticationData.PASSWORD);
//        }
//        passTextField.setText(new String(passChars));
//
//        userSelectedHook(userAuthenticationData);
//    }
//
//    protected void userSelectedHook(UserAuthenticationData userAuthenticationData) {
//
//    }
//
//    /**
//     * Override this method to be notified when user from authstore is selected
//     * 
//     * @param authenticationData
//     */
//    protected void updateUserAuthenticationData(UserAuthenticationData authenticationData) {
//
//    }
//
//    /**
//     * Resources
//     */
//
//    private static final String AUTHENTICATOR_USERNAME = ResourcesUtil.getString("authenticator.username");
//    private static final String AUTHENTICATOR_PASSWORD = ResourcesUtil.getString("authenticator.password");
//
//}
