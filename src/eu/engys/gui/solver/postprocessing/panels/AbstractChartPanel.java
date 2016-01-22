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

import static org.jfree.chart.ChartPanel.DEFAULT_HEIGHT;
import static org.jfree.chart.ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT;
import static org.jfree.chart.ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH;
import static org.jfree.chart.ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT;
import static org.jfree.chart.ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH;
import static org.jfree.chart.ChartPanel.DEFAULT_WIDTH;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;

public abstract class AbstractChartPanel extends JPanel {

	protected static final String TIME_LABEL = "Time [s]";

	protected String title;
	protected String domainAxisLabel;
	protected String rangeAxisLabel;

	protected ChartPanel chartPanel;
	protected JFreeChart chart;

	protected CrosshairOverlay overlay;

	public AbstractChartPanel(String title, String domainAxisLabel, String rangeAxisLabel) {
		super(new BorderLayout());
		this.title = title;
		this.domainAxisLabel = domainAxisLabel;
		this.rangeAxisLabel = rangeAxisLabel;
	}

	public void layoutComponents() {
		createChart();
		this.chart.setBackgroundPaint(new Color(0, 0, 0, 0));
		this.chartPanel = createChartPanel();
		layoutLegend();
	}

	protected abstract void createChart();

	public abstract void stop();

	public abstract void addToDataSet(TimeBlocks list);

	public abstract void clearData();

	private ChartPanel createChartPanel() {
		boolean useBuffer = true;
		boolean showPropertiesMenu = true;
		boolean showCopyMenu = true;
		boolean showSaveMenu = false;
		boolean showPrintMenu = true;
		boolean showZoomMenu = true;
		boolean showTooltipsMenu = true;
		ChartPanel panel = new ChartPanel(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT, DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, useBuffer, showPropertiesMenu, showCopyMenu, showSaveMenu, showPrintMenu, showZoomMenu, showTooltipsMenu);
		this.overlay = createOverlay();
		return panel;
	}

	private CrosshairOverlay createOverlay() {
		CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		Crosshair xCrosshair = new Crosshair(Double.NaN, Color.BLUE.brighter(), new BasicStroke(0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f));
		xCrosshair.setLabelGenerator(new CrosshairLabelGenerator() {
            @Override
            public String generateLabel(Crosshair crosshair) {
                DecimalFormat decimalFormat = new DecimalFormat("#.######");
                return decimalFormat.format(crosshair.getValue());
            }
        });
		xCrosshair.setLabelBackgroundPaint(Color.WHITE);
		xCrosshair.setLabelOutlineVisible(false);
		xCrosshair.setLabelVisible(true);
		
		Crosshair yCrosshair = new Crosshair(Double.NaN, Color.BLUE.brighter(), new BasicStroke(0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f));
		yCrosshair.setLabelGenerator(new CrosshairLabelGenerator() {
            @Override
            public String generateLabel(Crosshair crosshair) {
                DecimalFormat decimalFormat = new DecimalFormat("#.######");
                return decimalFormat.format(crosshair.getValue());
            }
        });
		yCrosshair.setLabelBackgroundPaint(Color.WHITE);
		yCrosshair.setLabelOutlineVisible(false);
		yCrosshair.setLabelVisible(true);
		
		crosshairOverlay.addDomainCrosshair(xCrosshair);
		crosshairOverlay.addRangeCrosshair(yCrosshair);
		return crosshairOverlay;
	}

	private void layoutLegend() {
		LegendTitle legend = chart.getLegend();
		if (legend != null) {
			legend.setFrame(BlockBorder.NONE);
			legend.setBackgroundPaint(null);
			legend.setPosition(RectangleEdge.RIGHT);
			legend.setVisible(false);
		}
	}
	
	public JFreeChart getChart() {
		return chart;
	}

	public void initSeries() {
	}
}
