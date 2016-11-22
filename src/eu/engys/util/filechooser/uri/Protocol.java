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
package eu.engys.util.filechooser.uri;

/**
 * <p>
 * T1ODO Disable the SSL port option when not supported in the connection dialog
 * It's probably better/faster than removing the SSL member For the file
 * protocol no port will be associated, need to handle that case using a -1
 * value in the connection dialog
 * </p>
 * Enumeration holding protocol constants
 * 
 * @author Yves Zoundi<yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public enum Protocol { // Protocol constants
	SMB("SMB", 445, "Connect to windows LAN or SAMBA"), 
	SFTP("SFTP", 22, "Connect to a SSH server"), 
	FTP("FTP", 21, "Connect to a FTP server"), 
	WEBDAV("WEBDAV", 9800, "Connect to a WEBDAV server"), 
	HTTP("HTTP", 80, "Connect to a HTTP server"), 
	HTTPS("HTTPS", 443, "HTTP connection over SSL"), 
	FILE("FILE", -1, "Local files");

	private final String name; // displayed name
	private final Integer port; // port number
	private final String description; // protocol description

	/**
	 * Create a new protocol
	 * 
	 * @param name
	 *            The name of the protocol
	 * @param port
	 *            The port used by the protocol
	 */
	Protocol(final String name, final int port, final String description) {
		this.name = name;
		this.port = port;
		this.description = description;
	}

	/**
	 * Returns the protocol name
	 * 
	 * @return the protocol name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the protocol port number
	 * 
	 * @return the protocol port number
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * Returns the protocol description
	 * 
	 * @return the protocol description
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
