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
package eu.engys.util;

public class SystemEnv {

    /*
     * LICENSE
     */
    public static final String LICENSE_STATUS = "license.status";
    public static final String LICENSE_ERROR_MESSAGE = "license.error.message";
    public static final String LICENSE_REGISTER = "license.register";
    public static final String LICENSE_EXP_DATE = "license.exp.date";

    /*
     * OS
     */
    public static final String OS_NAME = "os.name";
    public static final String OS_ARCH = "os.arch";
    public static final String OS_VERSION = "os.version";

    /*
     * JAVA
     */
    public static final String JAVA_HOME = "java.home";
    public static final String JAVA_VENDOR = "java.vendor";
    public static final String JAVA_VERSION = "java.version";
    public static final String JAVA_CLASS_PATH = "java.class.path";
    public static final String JAVA_CLASS_VERSION = "java.class.version";

    /*
     * USER
     */
    public static final String USER_DIR = "user.dir";
    public static final String USER_HOME = "user.home";
    public static final String USER_NAME = "user.name";

    /*
     * RMI
     */
    public static final String RMI_CLIENT_LOG = "sun.rmi.client.logCalls"; //client-side remote calls and exceptions
    public static final String RMI_CLIENT_LOG_LEVEL = "sun.rmi.client.logLevel"; //client-side remote reference activity
    public static final String RMI_SERVER_LOG = "java.rmi.server.logCalls"; // server-side remote calls and exceptions
    public static final String RMI_SERVER_LOG_LEVEL = "sun.rmi.server.logLevel"; //server-side remote reference activity
    public static final String RMI_TRANSPORT_LOG_LEVEL = "sun.rmi.transport.logLevel"; //transport-layer activity
    public static final String RMI_TRANSPORT_TCP_LOG_LEVEL = "sun.rmi.transport.tcp.logLevel"; //TCP binding and connection activity
    public static final String RMI_SERVER_HOSTNAME = "java.rmi.server.hostname";
}
