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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryAuthStore implements AuthStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryAuthStore.class);

    private Map<UserAuthenticationInfo, UserAuthenticationDataWrapper> map;

    public MemoryAuthStore() {
        map = new HashMap<UserAuthenticationInfo, UserAuthenticationDataWrapper>();
    }

    @Override
    public UserAuthenticationDataWrapper getUserAuthenticationData(UserAuthenticationInfo info) {
        return map.get(info);
    }

    @Override
    public Collection<UserAuthenticationDataWrapper> getUserAuthenticationDatas(String protocol, String host) {
        List<UserAuthenticationDataWrapper> list = new ArrayList<UserAuthenticationDataWrapper>();
        for (UserAuthenticationInfo key : map.keySet()) {
            if (StringUtils.equalsIgnoreCase(key.getProtocol(), protocol) && StringUtils.equalsIgnoreCase(key.getHost(), host)) {
                list.add(map.get(key));
            }
        }
        return list;
    }

    @Override
    public void add(UserAuthenticationInfo aInfo, UserAuthenticationDataWrapper authenticationData) {
        LOGGER.debug("Adding auth info {}://{}@{}", new Object[] { aInfo.getProtocol(), aInfo.getUser(), aInfo.getHost() });
        map.put(aInfo, authenticationData);
    }

    @Override
    public void remove(UserAuthenticationInfo authenticationInfo) {
        map.remove(authenticationInfo);
    }

    @Override
    public Collection<UserAuthenticationInfo> getAll() {
        return new ArrayList<UserAuthenticationInfo>(map.keySet());
    }
    
    @Override
    public void clear() {
    	map.clear();
	}

}
