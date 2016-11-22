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

package eu.engys.core.executor;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import eu.engys.core.executor.ExecutorListener.ExecutorState;

public abstract class Executor {

    protected static final Logger logger = LoggerFactory.getLogger(Executor.class);

    
    public static Executor script(File file, String... args) {
        return new ScriptExecutor(file, args);
    }

    public static Executor command(String command, String... args) {
        return new CommandExecutor(command, args);
    }

    public static Executor command(File file, String... args) {
        return new CommandExecutor("\"" + file.getAbsolutePath() + "\"", args);
    }

    public static JavaExecutor jvm(String className, String... args) {
        return new JavaExecutor(className, args);
    }

    public abstract Executor description(String description);

    public abstract Executor keepFileOnEnd();

    public abstract Executor inFolder(File currentDir);

    public abstract Executor env(Map<String, String> environment);

    public abstract Executor inService(ExecutorService service);

    public abstract Executor inTerminal(ExecutorTerminal terminal);

    public abstract Executor withMonitors(ExecutorMonitor... monitor);

    public abstract Executor withOpenFoamEnv();

    public abstract void exec();

    public abstract int execAndWait();

    public abstract Executor properties(Properties p);
    
    protected abstract CommandLine getCommandLine();

    public abstract ExecutorService getService();
    public abstract ExecutorState getState();
    public abstract ExecutorError getError();
    public abstract String getDescription();

    public void notify(ExecutorState state) {
    }

    public static ThreadPoolExecutor newExecutor(final String name) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat(name + "-%d")
        .setDaemon(false)
        .build();
        
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone())
                            future.get();
                    } catch (CancellationException ce) {
                        t = ce;
                    } catch (ExecutionException ee) {
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // ignore/reset
                    }
                }
                if (t != null) {
                    logger.error("ERROR FOR EXECUTOR: " + name, t);
                }
            }
        };
    }

}
