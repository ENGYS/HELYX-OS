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
package eu.engys.gui.solver.postprocessing.panels;

import static eu.engys.core.controller.AbstractController.REFRESH_ONCE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.executor.FileManagerSupport;
import eu.engys.core.modules.AbstractChart;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObject;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.gui.solver.postprocessing.panels.utils.WaitLayerUI;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.FileChooserUtils;

public abstract class AbstractParserView extends JPanel implements ParserView {

    private static final Logger logger = LoggerFactory.getLogger(AbstractParserView.class);
    protected Model model;
    protected MonitoringFunctionObject functionObject;
    protected ProgressMonitor monitor;
    protected JTabbedPane tabbedPane;
    private boolean parsingEnabled;

    private WaitLayerUI loadingPane;

    public AbstractParserView(Model model, MonitoringFunctionObject functionObject, ProgressMonitor monitor) {
        super(new BorderLayout());
        if (functionObject != null)
            setName(functionObject.getName());
        this.model = model;
        this.functionObject = functionObject;
        this.monitor = monitor;
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                stop();
            }
        });

        this.parsingEnabled = false;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        loadingPane = new WaitLayerUI(new EnableParsing());
        JLayer<JPanel> layer = new JLayer<>(mainPanel, loadingPane);

        add(layer, BorderLayout.CENTER);
    }

    @Override
    public void reset() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearData();
                loadingPane.init();
                setParsingEnabled(false);
            }
        });
    }

    @Override
    public void handleSolverStarted() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearData();
                loadingPane.stop();
            }
        });
    }

    @Override
    public void showLoading() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadingPane.start();
            }
        });
    }

    @Override
    public void stopLoading() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadingPane.stop();
            }
        });
    }

    @Override
    public void showLogFile() {
        try {
            if (Util.isWindows()) {
                showLogFileWindows();
            } else {
                showLogFileLinux();
            }
        } catch (Exception e) {
            showErrorMessage(e);
        }
    }

    private void showLogFileLinux() {
        List<Parser> parsersList = getReportParsersList();
        for (Parser parser : parsersList) {
            File logFile = parser.getFile();
            if (logFile != null && logFile.exists()) {
                FileManagerSupport.open(logFile);
            }
        }
    }

    private void showLogFileWindows() {
        List<File> logFiles = new ArrayList<>();
        for (Parser parser : getReportParsersList()) {
            File logFile = parser.getFile();
            if (logFile != null && logFile.exists()) {
                logFiles.add(logFile);
            }
        }
        if (logFiles.isEmpty()) {
            return;
        } else if (logFiles.size() == 1) {
            // 1 file -> open it
            FileManagerSupport.open(logFiles.get(0));
        } else {
            // 1+ files -> retrive parent folders
            List<File> parentFolders = new ArrayList<>();
            for (File logFile : logFiles) {
                File parentFile = logFile.getParentFile();
                if (!parentFolders.contains(parentFile)) {
                    parentFolders.add(parentFile);
                }
            }
            if (parentFolders.size() == 1) {
                // 1 parent folder -> open it
                FileManagerSupport.open(parentFolders.get(0));
            } else {
                // 1+ parent folders -> open common parent folder that is the function object folder
                FileManagerSupport.open(parentFolders.get(0).getParentFile());

            }
        }
    }

    @Override
    public void exportToExcel() {
        final File excelFile = FileChooserUtils.getExcelFile();
        if (excelFile != null) {
            monitor.setIndeterminate(true);
            monitor.start("Export to Excel", false, new Runnable() {
                @Override
                public void run() {
                    try {
                        getExporter().exportToExcel(excelFile, monitor);
                    } catch (Exception e) {
                        showErrorMessage(e);
                    } finally {
                        monitor.end();
                    }
                }

            });
        }
    }

    @Override
    public void exportToCSV() {
        final File csvFile = FileChooserUtils.getCSVFile();
        if (csvFile != null) {
            monitor.start("Export to CSV", false, new Runnable() {
                @Override
                public void run() {
                    try {
                        getExporter().exportToCSV(csvFile, monitor);
                    } catch (Exception e) {
                        showErrorMessage(e);
                    } finally {
                        monitor.end();
                    }
                }
            });
        }
    }

    @Override
    public void exportToPNG() {
        AbstractChart chart = getSelectedChart();
        File pngFile = FileChooserUtils.getPNGFile();
        if (pngFile != null && chart != null) {
            JFreeChart jfreeChart = chart.getChartPanel().getChart();
            // PRE-SAVE
            jfreeChart.setBackgroundPaint(Color.WHITE);
            if (jfreeChart.getLegend() != null) {
                jfreeChart.getLegend().setBackgroundPaint(Color.WHITE);
                jfreeChart.getLegend().setVisible(true);
            }
            // SAVE
            try {
                ChartUtilities.saveChartAsPNG(pngFile, jfreeChart, chart.getChartPanel().getSize().width, chart.getChartPanel().getSize().height);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // POST-SAVE
            jfreeChart.setBackgroundPaint(new Color(0, 0, 0, 0));
            if (jfreeChart.getLegend() != null) {
                jfreeChart.getLegend().setBackgroundPaint(new Color(0, 0, 0, 0));
                jfreeChart.getLegend().setVisible(false);
            }

            FileManagerSupport.open(pngFile);
        } else {
            logger.error("Problem saving chart to PNG");
        }
    }

    private void showErrorMessage(final Exception e) {
        ExecUtil.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(AbstractParserView.this), e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                logger.error("Export Error", e.getMessage());
            }
        });
    }

    public abstract AbstractChart getSelectedChart();

    @Override
    public boolean isParsingEnabled() {
        return parsingEnabled;
    }

    @Override
    public void setParsingEnabled(boolean parsingEnabled) {
        this.parsingEnabled = parsingEnabled;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void stop() {
    }

    protected class EnableParsing implements Runnable {
        @Override
        public void run() {
            setParsingEnabled(true);
            ActionManager.getInstance().invoke(REFRESH_ONCE);
            if (!model.getSolverModel().getServerState().getSolverState().isDoingSomething()) {
                setParsingEnabled(false);
            }
        }
    }

}
