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
 * VFSURIParser class for bookmarks URIs
 * 
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Stan Love
 * @version 0.0.6
 */
public final class VFSURIParser {
    private static final char PATH_SEPARATOR = '/';
    private String username;
    private String password;
    private String path;
    private String hostname;
    private String portnumber;
    private Protocol protocol;

    /**
     * Create a new instance of <CODE>VFSURIParser</CODE>
     * 
     * @param fileURI
     *            The VFS file URI to parse
     */
    public VFSURIParser(final String fileURI) {
        this(fileURI, true);
    }

    public VFSURIParser(final String fileURI, boolean assignDefaultPort) {
        if (fileURI == null) {
            throw new NullPointerException("file URI is null");
        }

        VFSURIValidator v = new VFSURIValidator();
        boolean valid = v.isValid(fileURI);
        if (valid) {
            hostname = v.getHostname();
            username = v.getUser();
            password = v.getPassword();
            path = v.getFile();
            portnumber = v.getPort();
            String p = v.getProtocol();

            // fix up parsing results
            protocol = Protocol.valueOf(p.toUpperCase());
            if ((portnumber == null) && (!p.equalsIgnoreCase("file"))) {
                portnumber = String.valueOf(protocol.getPort());
            }
            if (path == null) {
                path = String.valueOf(PATH_SEPARATOR);
            }
        } else {
            hostname = null;
            username = null;
            password = null;
            path = fileURI;
            portnumber = null;
            protocol = null;
        }

    }

    /**
     * Returns the VFS hostname
     * 
     * @return the VFS hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Returns the VFS password
     * 
     * @return the VFS password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the VFS path
     * 
     * @return the VFS path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the VFS port number
     * 
     * @return the VFS port number
     */
    public String getPortnumber() {
        return portnumber;
    }

    /**
     * Returns the VFS protocol
     * 
     * @return the VFS protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Returns the VFS username
     * 
     * @return the VFS username
     */
    public String getUsername() {
        return username;
    }
}
