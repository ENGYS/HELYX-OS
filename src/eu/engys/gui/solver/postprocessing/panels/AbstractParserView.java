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

package eu.engys.gui.solver.postprocessing.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;
import org.jfree.chart.ChartUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.executor.FileManagerSupport;
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
        this.model = model;
        this.functionObject = functionObject;
        this.monitor = monitor;
        this.tabbedPane = new JTabbedPane();
        this.parsingEnabled = false;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        loadingPane = new WaitLayerUI(new EnableParsing());
        JLayer<JPanel> layer = new JLayer<JPanel>(mainPanel, loadingPane);

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
    public void setCrosshairVisibile(boolean visible) {
        for (Component c : tabbedPane.getComponents()) {
            if (c instanceof MovingAverageChartPanel<?>) {
                ((MovingAverageChartPanel<?>) c).setCrosshairVisible(visible);
            }
        }
    }

    @Override
    public void showLogFile() {
        try {
            List<Parser> parsersList = gerReportParsersList();
            for (Parser parser : parsersList) {
                File logFile = parser.getFile();
                if (logFile != null && logFile.exists()) {
                    if (Util.isWindows() && FilenameUtils.getExtension(logFile.getName()).isEmpty()) {
                        FileManagerSupport.open(logFile.getParentFile());
                    } else {
                        FileManagerSupport.open(logFile);
                    }
                }
            }
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(AbstractParserView.this), e1.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Cannot open log file", e1.getMessage());
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
        AbstractChartPanel chartPanel = (AbstractChartPanel) tabbedPane.getSelectedComponent();
        File pngFile = FileChooserUtils.getPNGFile();
        if (pngFile != null && chartPanel != null) {
            // PRE-SAVE
            chartPanel.getChart().setBackgroundPaint(Color.WHITE);
            if (chartPanel.getChart().getLegend() != null) {
                chartPanel.getChart().getLegend().setBackgroundPaint(Color.WHITE);
                chartPanel.getChart().getLegend().setVisible(true);
            }
            // SAVE
            try {
                ChartUtilities.saveChartAsPNG(pngFile, chartPanel.getChart(), chartPanel.getSize().width, chartPanel.getSize().height);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // POST-SAVE
            chartPanel.getChart().setBackgroundPaint(new Color(0, 0, 0, 0));
            if (chartPanel.getChart().getLegend() != null) {
                chartPanel.getChart().getLegend().setBackgroundPaint(new Color(0, 0, 0, 0));
                chartPanel.getChart().getLegend().setVisible(false);
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
                logger.error("Cannot export", e.getMessage());
            }
        });
    }

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

    protected class EnableParsing implements Runnable {
        @Override
        public void run() {
            setParsingEnabled(true);
            ActionManager.getInstance().invoke("solver.refresh.once");
            if (!model.getSolverModel().getServerState().getSolverState().isDoingSomething()) {
                setParsingEnabled(false);
            }
        }
    }

}
