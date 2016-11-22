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
package eu.engys.core.controller.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.CommandInfo;

public class StopCommandInfo extends CommandInfo {

    private static final Logger logger = LoggerFactory.getLogger(StopCommandInfo.class);

    public static final String TIMEOUT = "Timeout";

    public boolean timeout;

    public static StopCommandInfo wrap(CommandInfo info) {
        StopCommandInfo ci = new StopCommandInfo();
        ci.message = info.message;
        ci.exception = info.exception;
        ci.success = info.success;
        ci.timeout = false;
        return ci;
    }

    public static StopCommandInfo timeoutException(Exception e) {
        logger.error(">>> SERVER STOP ERROR", e);
        StopCommandInfo ci = new StopCommandInfo();
        ci.message = TIMEOUT;
        ci.exception = e;
        ci.success = false;
        ci.timeout = true;
        return ci;
    }

    @Override
    public String toString() {
        return "Stop Command [success: " + success + "] - [timeout: " + timeout + "] - [message: " + message + "] - [exception: " + exception.getMessage() + "]";
    }

}
