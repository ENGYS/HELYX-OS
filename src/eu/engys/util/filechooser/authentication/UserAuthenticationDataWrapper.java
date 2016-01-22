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
