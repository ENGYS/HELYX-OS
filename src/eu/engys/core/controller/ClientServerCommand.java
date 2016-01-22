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

public class ClientServerCommand implements Serializable {

    private Command[] commands;
    private String open;
    private boolean parallel;
    private boolean startParsers;
    private boolean queue;
    
    public ClientServerCommand(Command... commands) {
        this.commands = commands;
        this.open = null;
        this.parallel = false;
        this.startParsers = false;
    }
    
    public ClientServerCommand(Command command, String open, boolean parallel, boolean startParsers) {
        this.commands = new Command[]{command};
        this.open = open;
        this.parallel = parallel;
        this.startParsers = startParsers;
    }

    public Command getCommand() {
        return commands[0];
    }

    public Command[] getCommands() {
        return commands;
    }

    public void setCommands(Command[] commands) {
        this.commands = commands;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public boolean isStartParsers() {
        return startParsers;
    }

    public void setStartParsers(boolean startParsers) {
        this.startParsers = startParsers;
    }

    public boolean isCommandSequence() {
        return commands.length > 1;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
    }
    
    public boolean isQueue() {
        return queue;
    }
}
