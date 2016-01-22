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

package eu.engys.core.controller.actions;

import java.util.concurrent.ExecutorService;

import eu.engys.core.controller.Controller;
import eu.engys.core.controller.Server;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.ExecutorTerminal;
import eu.engys.core.project.Model;

public abstract class AbstractRunCommand implements RunCommand {

    protected Model model;
    protected Executor executor;
    protected Controller controller;
    protected ExecutorService service;
    protected ExecutorTerminal terminal;

    public AbstractRunCommand(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
    }

    @Override
    public void inService(ExecutorService service) {
        this.service = service;
    }

    @Override
    public void inTerminal(ExecutorTerminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public boolean isRunning() {
        return executor != null && executor.getState().isDoingSomething();
    }

    @Override
    public void kill() {
        executor.getService().shutdownNow();
    }

    @Override
    public void beforeExecute() {
    }

    @Override
    public void stop() throws TimeoutException {
    }

    @Override
    public void executeClient() {
    }

    @Override
    public void executeServer(Server server) throws CommandException {
    }

    @Override
    public String executeQueue(Server server) throws CommandException {
        return null;
    }

    @Override
    public void executeBatch() {
    }

}
