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

package eu.engys.gui.solver.postprocessing;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.ParsersManager;
import eu.engys.core.executor.Executor;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.ServerState;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObject;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.solver.postprocessing.panels.residuals.ResidualsView;
import eu.engys.util.ui.ExecUtil;

public class ParsersViewHandler implements ServerListener {

    private static final Logger logger = LoggerFactory.getLogger(ParsersViewHandler.class);

    private final Model model;

    private List<ParserView> views;
    private ResidualsView residualsView;
    private ThreadPoolExecutor executor;

    private ParsersManager parserManager;

    public ParsersViewHandler(Model model, ParsersManager parserManager, ResidualsView residualsView) {
        this.model = model;
        this.parserManager = parserManager;
        this.residualsView = residualsView;
        this.views = Collections.synchronizedList(new ArrayList<ParserView>());
        this.executor = Executor.newExecutor("ParserViewHandler");
        registerViews();
    }

    private void registerViews() {
        register(residualsView);
        for (MonitoringFunctionObject fo : model.getMonitoringFunctionObjects()) {
            ParserView view = fo.getView();
            register(view);
        }
    }

    private void register(ParserView view) {
        if (view != null) {
            logger.debug("REGISTERING {} VIEW", view.getKey());
            views.add(view);
        }
    }

    @Override
    public void serverChanged(ServerState serverState) {
        switch (serverState.getSolverState()) {
        case STARTED:
            started();
            break;
        case RUNNING:
            running();
            break;
        case ERROR:
            error();
            break;
        case FINISHED:
            running();// Needed by OS
            finished();
            break;

        default:
            break;
        }
    }

    private void started() {
        logger.debug("STARTED");
        for (ParserView view : views) {
            view.setParsingEnabled(true);
            view.handleSolverStarted();
        }
    }

    private void finished() {
        logger.debug("FINISHED");
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("ENDING PARSERS");
                    parserManager.endParsers();
                } catch (RemoteException e) {
                    logger.error("ERROR ENDING PARSERS", e);
                }
            }
        });
        executor.shutdown();
    }

    private void running() {
        for (final ParserView view : views) {
            if (view.isParsingEnabled()) {
                if (notInQueue(view)) {
                    executor.submit(new ParserUpdateTask(view));
                }
            }
        }
    }

    private boolean notInQueue(ParserView view) {
        for (Runnable r : executor.getQueue()) {
            if (r instanceof ParserUpdateTask) {
                ParserUpdateTask task = (ParserUpdateTask) r;
                if (task.getView() == view) {
                    return false;
                }
            }
        }

        return true;
    }

    public void refreshOnce() {
        logger.info("REFRESH ONCE");
        for (final ParserView view : views) {
            if (view.isParsingEnabled()) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        view.showLoading();
                        List<TimeBlocks> timeBlocks = updateParserOnce(parserManager, view.getKey());
                        logger.debug("RETRIVED {} TIME BLOCKS FOR {}", timeBlocks.size(), view.getKey());
                        updateView(view, timeBlocks);
                        view.stopLoading();
                    }
                });
            }
        }
        executor.shutdown();
    }

    private List<TimeBlocks> updateParser(ParsersManager parsersManager, String key) {
        logger.debug("REFRESH PARSER {}", key);
        try {
            return parsersManager.updateParser(key);
        } catch (RemoteException e) {
            logger.error("ERROR ON REFRESH", e);
        }
        return Collections.emptyList();
    }

    private List<TimeBlocks> updateParserOnce(ParsersManager parsersManager, String key) {
        logger.debug("REFRESH PARSER ONCE {}", key);
        try {
            return parsersManager.updateParserOnce(key);
        } catch (RemoteException e) {
            logger.error("ERROR ON REFRESH ONCE", e);
        }
        return Collections.emptyList();
    }

    private void updateView(final ParserView view, final List<TimeBlocks> newTimeBlocks) {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (newTimeBlocks != null) {
                    view.updateParsing(newTimeBlocks);
                } else {
                    logger.warn("EMPTY TIME BLOCK");
                }
            }
        });
    }

    private void error() {
        logger.debug("ERROR");
        executor.shutdown();
    }

    class ParserUpdateTask implements Runnable {

        private ParserView view;

        public ParserUpdateTask(ParserView view) {
            this.view = view;
        }

        @Override
        public void run() {
            view.showLoading();
            List<TimeBlocks> timeBlocks = updateParser(parserManager, view.getKey());
            logger.debug("RETRIVED {} TIME BLOCKS FOR {}", timeBlocks.size(), view.getKey());
            updateView(view, timeBlocks);
            view.stopLoading();
        }

        public ParserView getView() {
            return view;
        }

    }

}
