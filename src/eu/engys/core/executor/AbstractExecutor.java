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

package eu.engys.core.executor;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import eu.engys.core.executor.ExecutorListener.ExecutorState;


public abstract class AbstractExecutor extends Executor {

    
    protected File currentDir;
    protected String description;
    protected ExecutorTerminal terminal;
    protected Map<String, String> environment;
    protected ExecutorService service;
    protected Properties properties;
    protected ExecutorMonitor[] monitors;
    protected boolean keepFileOnEnd = false;
    protected boolean loadOpenFoamEnv;

    private ExecutorState state;
    private ExecutorError error;

    @Override
    public Executor env(Map<String, String> environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public Executor description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public Executor properties(Properties p) {
        this.properties = p;
        return this;
    }

    @Override
    public Executor inService(ExecutorService service) {
        this.service = service;
        return this;
    }
    
    @Override
    public ExecutorService getService() {
        return service;
    }

    @Override
    public Executor inTerminal(ExecutorTerminal terminal) {
        this.terminal = terminal;
        return this;
    }

    @Override
    public Executor withMonitors(ExecutorMonitor... monitors) {
        this.monitors = monitors;
        return this;
    }
    
    @Override
    public Executor inFolder(File currentDir) {
        this.currentDir = currentDir;
        return this;
    }

    @Override
    public Executor withOpenFoamEnv() {
        this.loadOpenFoamEnv = true;
        return this;
    }
    
    @Override
    public Executor keepFileOnEnd() {
        this.keepFileOnEnd = true;
        return this;
    }

    @Override
    public int execAndWait() {
        int returnValue = 0;
        if (service == null) {
            this.service = Executor.newExecutor("BuiltInExecutor");
        }
        
        if (terminal != null) {
            terminal.setExecutor(service);
            terminal.setTitle(description);
        }

        Future<Integer> task = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return _exec();
            }
        });
        try {
            returnValue = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    @Override
    public void exec() {
        if (service == null) {
            this.service = Executor.newExecutor("BuiltInExecutor");
        }
        if (terminal != null) {
            terminal.setExecutor(service);
            terminal.setTitle(description);
        }
        service.submit(new Runnable() {
            @Override
            public void run() {
                _exec();
            }
        });
    }

    protected abstract int _exec();
    
    @Override
    public ExecutorState getState() {
        return state;
    }
    
    @Override
    public ExecutorError getError() {
        return error;
    }
    
    protected void notifyStart() {
        this.state = ExecutorState.START;
        if (terminal != null) {
            terminal.start();
        }
        if (monitors != null) {
            for (ExecutorMonitor monitor : monitors) {
                monitor.start();
            }
        }
    }
    protected void notifyRefresh() {
        this.state = ExecutorState.RUNNING;
        if (terminal != null) {
            terminal.refresh();
        }
        if (monitors != null) {
            for (ExecutorMonitor monitor : monitors) {
                monitor.refresh();
            }
        }
    }
    protected void notifyError(int exitValue, String msg) {
        this.state = ExecutorState.ERROR;
        this.error = new ExecutorError(exitValue, msg);
        
        if (terminal != null) {
            terminal.error(exitValue, msg);
        }
        if (monitors != null) {
            for (ExecutorMonitor monitor : monitors) {
                monitor.error(exitValue, msg);
            }
        }
    }

    protected void notifyFinish(int exitValue) {
        this.state = ExecutorState.FINISH;
        if (terminal != null) {
            terminal.finish(exitValue);
        }
        if (monitors != null) {
            for (ExecutorMonitor monitor : monitors) {
                monitor.finish(exitValue);
            }
        }
    }

    @Override
    public void notify(ExecutorState state) {
        switch (state) {
            case START: notifyStart(); break;
            case RUNNING: notifyRefresh(); break;
            case ERROR: notifyError(1, ""); break;

            default: break;
        }
    }
}
