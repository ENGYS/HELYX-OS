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

package eu.engys.util.filechooser.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CompositeAuthStore implements AuthStore {

    public AuthStore[] authStores;

    public CompositeAuthStore(AuthStore... authStores) {
        super();
        this.authStores = authStores;
    }

    @Override
    public void add(UserAuthenticationInfo auInfo, UserAuthenticationDataWrapper authenticationData) {
        for (AuthStore authStore : authStores) {
            authStore.add(auInfo, authenticationData);
        }

    }

    @Override
    public UserAuthenticationDataWrapper getUserAuthenticationData(UserAuthenticationInfo auInfo) {
        for (AuthStore authStore : authStores) {
            UserAuthenticationDataWrapper userAuthenticationData = authStore.getUserAuthenticationData(auInfo);
            if (userAuthenticationData != null) {
                return userAuthenticationData;
            }
        }
        return null;
    }

    @Override
    public Collection<UserAuthenticationDataWrapper> getUserAuthenticationDatas(String protocol, String host) {
        HashSet<UserAuthenticationDataWrapper> set = new HashSet<UserAuthenticationDataWrapper>();
        for (AuthStore authStore : authStores) {
            set.addAll(authStore.getUserAuthenticationDatas(protocol, host));
        }
        return set;
    }

    @Override
    public void remove(UserAuthenticationInfo authenticationInfo) {
        for (AuthStore authStore : authStores) {
            authStore.remove(authenticationInfo);
        }

    }

    @Override
    public Collection<UserAuthenticationInfo> getAll() {
        List<UserAuthenticationInfo> l = new ArrayList<UserAuthenticationInfo>();
        for (AuthStore authStore : authStores) {
            l.addAll(authStore.getAll());
        }
        return l;
    }
    
    @Override
    public void clear() {
    	for (AuthStore authStore : authStores) {
			authStore.clear();
		}
    }

}
