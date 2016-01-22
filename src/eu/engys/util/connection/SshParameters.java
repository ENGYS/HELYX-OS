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

package eu.engys.util.connection;

import java.io.Serializable;

public class SshParameters implements Serializable {

	public static final String USER = "user";
	public static final String SSH_PWD = "sshpwd";
	public static final String SSH_KEY = "sshkey";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String AUTHENTICATION = "sshauth";
	public static final String REMOTE_BASEDIR = "remoteBaseDir";
	public static final String REMOTE_BASEDIR_PARENT = "remoteBaseDirParent";
	public static final String APPLICATION_DIR = "applicationDir";
	public static final String OPENFOAM_DIR = "openFoamDir";
	public static final String PARAVIEW_DIR = "paraviewDir";

	public enum AuthType {
		SSH_KEY, SSH_PWD;

		public boolean isKey() {
			return this == SSH_KEY;
		}
	}

	private int port = 22;
	private String user = "";
	private String host = "";
	private String sshkey = "";
	private String sshpwd = "";

	private String remoteBaseDir = "";
	private String remoteBaseDirParent = "";
	private String openFoamDir = "";
	private String paraviewDir = "";
	private String applicationDir = "";
	private AuthType sshauth = AuthType.SSH_PWD;

	public void copy(SshParameters sshParameters) {
	    setUser(sshParameters.getUser());
	    setSshpwd(sshParameters.getSshpwd());
	    setSshkey(sshParameters.getSshkey());
	    setHost(sshParameters.getHost());
	    setPort(sshParameters.getPort());
	    setSshauth(sshParameters.getSshauth());
	    setRemoteBaseDir(sshParameters.getRemoteBaseDir());
	    setRemoteBaseDirParent(sshParameters.getRemoteBaseDirParent());
	    setApplicationDir(sshParameters.getApplicationDir());
	    setOpenFoamDir(sshParameters.getOpenFoamDir());
	    setParaviewDir(sshParameters.getParaviewDir());
    }

    public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public AuthType getSshauth() {
		return sshauth;
	}

	public void setSshauth(AuthType sshauth) {
		this.sshauth = sshauth;
	}

	public String getSshkey() {
		return sshkey;
	}

	public void setSshkey(String sshkey) {
		this.sshkey = sshkey;
	}

	public String getSshpwd() {
		return sshpwd;
	}

	public void setSshpwd(String sshpwd) {
		this.sshpwd = sshpwd;
	}

	public String getRemoteBaseDir() {
		return remoteBaseDir;
	}

	public void setRemoteBaseDir(String remoteBaseDir) {
		this.remoteBaseDir = remoteBaseDir;
	}

	public String getRemoteBaseDirParent() {
		return remoteBaseDirParent;
	}

	public void setRemoteBaseDirParent(String remoteBaseDirParent) {
		this.remoteBaseDirParent = remoteBaseDirParent;
	}

	public String getOpenFoamDir() {
		return openFoamDir;
	}

	public void setOpenFoamDir(String openFoamDir) {
		this.openFoamDir = openFoamDir;
	}

	public void setParaviewDir(String paraviewDir) {
        this.paraviewDir = paraviewDir;
    }
	
	public String getParaviewDir() {
        return paraviewDir;
    }
	
	public String getApplicationDir() {
		return applicationDir;
	}

	public void setApplicationDir(String elementsDir) {
		this.applicationDir = elementsDir;
	}

	public boolean isValidForRemoteChooser() {
		return (user != null) && (sshpwd != null) || (host != null);
	}

	@Override
	public String toString() {
		return "SSHParameters:   " +
				"\nUSER  [ " + getUser() + " ]" + 
				"\nHOST  [ " + getHost() + " ]" + 
				"\nPORT  [ " + getPort() + " ]" + 
				"\nAUTH  [ " + getSshauth() + " ]" + 
				"\nPASS  [ ******* ]" + 
				"\nKEY   [ " + getSshkey() + " ]" + 
				"\nRDIR  [ " + getRemoteBaseDir() + " ]" + 
				"\nCDIR  [ " + getRemoteBaseDirParent() + " ]" + 
				"\nOFDIR [ " + getOpenFoamDir() + " ]" + 
				"\nPVDIR [ " + getParaviewDir() + " ]" + 
				"\nAPDIR [ " + getApplicationDir() + " ]";
	}
}
