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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.UserAuthenticationData;

public class UserAuthenticationDataWrapper extends UserAuthenticationData {

    /**
     * The password.
     */
    public static final Type SSH_KEY = new Type("sshKey");

    private HashMap<Type, char[]> map;

    public UserAuthenticationDataWrapper() {
        super();
        map = new HashMap<Type, char[]>();
    }

    @Override
    public void setData(Type type, char[] data) {
        super.setData(type, data.clone());
        map.put(type, data);
    }

    public Map<Type, char[]> getAddedTypes() {
        return map;
    }

    public UserAuthenticationDataWrapper copy() {
        UserAuthenticationDataWrapper cp = new UserAuthenticationDataWrapper();
        for (Type type : map.keySet()) {
            cp.setData(type, map.get(type));
        }
        return cp;
    }

    @Override
    public void cleanup() {
    }

    public void cleanWrapper() {
        super.cleanup();
        for (char[] chars : map.values()) {
            for (int i = 0; i < chars.length; i++) {
                chars[i] = '0';
            }
        }
    }

    public void remove(Type type) {
        super.cleanup();
        map.remove(type);
        for (Type type1 : map.keySet()) {
            super.setData(type1, map.get(type1));
        }
    }

}
