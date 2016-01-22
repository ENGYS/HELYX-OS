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

package eu.engys.core.controller;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandInfo implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CommandInfo.class);
    
    public boolean success;
    public String message;
    public Exception exception;
    public String jobID;
    
    public static CommandInfo remoteException(RemoteException e) {
        logger.warn(">>> SERVER REMOTE ERROR", e);
        CommandInfo ci = new CommandInfo();
        ci.message = "Error";
        ci.exception = e;
        ci.success = false;
        return ci;
    }

    public static CommandInfo genericException(Exception e) {
        logger.error(">>> SERVER GENERIC ERROR", e);
        CommandInfo ci = new CommandInfo();
        ci.message = "Error";
        ci.exception = e;
        ci.success = false;
        return ci;
    }

    public static CommandInfo error(String message) {
        logger.warn(">>> SERVER WARNING: {}", message);
        CommandInfo ci = new CommandInfo();
        ci.message = message;
        ci.exception = null;
        ci.success = false;
        return ci;
    }
    
    public static CommandInfo notConnected() {
        CommandInfo ci = new CommandInfo();
        ci.message = "Not Connected";
        ci.exception = null;
        ci.success = false;
        return ci;
    }

    public static CommandInfo success() {
        return success("Success");
    }
    
    public static CommandInfo success(String message) {
        logger.info(">>> SERVER: {}", message);
        CommandInfo ci = new CommandInfo();
        ci.message = message;
        ci.exception = null;
        ci.success = true;
        return ci;
    }
    
}
