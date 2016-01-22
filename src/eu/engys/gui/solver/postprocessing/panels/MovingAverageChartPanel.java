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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlock;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.gui.solver.postprocessing.panels.utils.MovingAveragePanel;
import eu.engys.gui.solver.postprocessing.panels.utils.SeriesPanel;
import eu.engys.util.ui.ExecUtil;

public abstract class MovingAverageChartPanel<D extends AbstractIntervalXYDataset> extends AbstractChartPanel {

	private static final long serialVersionUID = 1L;

	protected SeriesPanel seriesPanel;
	protected MovingAveragePanel movingAveragePanel;

	protected D dataset;
	protected XYSeriesCollection movingAverageDataSet;
	private boolean showMovingAverage;

	private CrosshairListener crosshairListener;

	public MovingAverageChartPanel(String title, String domainAxisLabel, String rangeAxisLabel, boolean showMovingAverage) {
		super(title, domainAxisLabel, rangeAxisLabel);
		setName(title + ".chart.panel");
		this.showMovingAverage = showMovingAverage;
		this.movingAverageDataSet = new XYSeriesCollection();
		this.crosshairListener = new CrosshairListener();
	}

	@Override
	public void layoutComponents() {
		super.layoutComponents();
		chartPanel.addOverlay(overlay);
		
		this.seriesPanel = new SeriesPanel(showMovingAverage ? movingAveragePanel = new MovingAveragePanel(dataset, movingAverageDataSet) : null);

		JScrollPane seriesScrollPane = new JScrollPane(seriesPanel);
		seriesScrollPane.setBorder(BorderFactory.createEmptyBorder());

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(false);
		splitPane.setLeftComponent(chartPanel);
		splitPane.setRightComponent(seriesScrollPane);

		double scrollPaneWidth = splitPane.getPreferredSize().getWidth();
		double seriesPanelWidth = seriesPanel.getPreferredSize().getWidth();
		if (seriesPanelWidth != 0 && scrollPaneWidth != 0) {
			double value = 1 - (seriesPanelWidth / scrollPaneWidth) - 0.07;
			splitPane.setResizeWeight(value);
		} else {
			splitPane.setResizeWeight(0.9);
		}
		add(splitPane, BorderLayout.CENTER);
	}

	public void setCrosshairVisible(boolean visible) {
		if (visible) {
			chartPanel.addChartMouseListener(crosshairListener);
			chartPanel.addMouseListener(crosshairListener);
		} else {
			chartPanel.removeChartMouseListener(crosshairListener);
			chartPanel.removeMouseListener(crosshairListener);
			removeCrosshair();
		}
	}

	@Override
	public void addToDataSet(final TimeBlocks list) {
		synchronized (list) {
		    ExecUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    notifySeries(false);
                    for (final TimeBlock block : list) {
                        addTimeBlock(block);
                    }
                    if(showMovingAverage){
                        movingAveragePanel.updateMovingAverageDataset();
                    }
                    notifySeries(true);
                    refreshGUI();
                }
            });
		}
	}

	protected abstract void addTimeBlock(final TimeBlock block);

	protected void populateSeriesPanel(int seriesIndex, String seriesTitle) {
		XYItemRenderer baseRenderer = chart.getXYPlot().getRenderer();
		seriesPanel.addSeries(baseRenderer, seriesIndex, seriesTitle);

		if (showMovingAverage) {
			XYItemRenderer renderer = chart.getXYPlot().getRenderer(1);
			renderer.setSeriesPaint(seriesIndex, ((Color) baseRenderer.getItemPaint(seriesIndex, 0)).darker().darker());
			renderer.setSeriesStroke(seriesIndex, new BasicStroke(1));
			seriesPanel.addMovingAverageSeries(renderer, seriesIndex, seriesTitle);
		}
	}

	@Override
	public void clearData() {
		notifySeries(false);
		clearDataset();
		clearMovingAverageDataset();
		notifySeries(true);
		removeCrosshair();
		chartPanel.restoreAutoDomainBounds();
	}

	protected abstract void clearDataset();

	private void clearMovingAverageDataset() {
		for (int i = 0; i < movingAverageDataSet.getSeriesCount(); i++) {
			movingAverageDataSet.getSeries(i).clear();
		}
	}

	protected void notifySeries(boolean notify) {
		dataset.setNotify(notify);
		movingAverageDataSet.setNotify(notify);
	}

	protected void refreshGUI() {
		ExecUtil.invokeLater(new Runnable() {
			@Override
			public void run() {
				seriesPanel.revalidate();
				chartPanel.revalidate();
				chartPanel.repaint();
			}
		});
	}

	private void removeCrosshair() {
		((Crosshair) overlay.getDomainCrosshairs().get(0)).setValue(Double.NaN);
		((Crosshair) overlay.getRangeCrosshairs().get(0)).setValue(Double.NaN);
	}
	
	@Override
	public void stop() {
	}

	private class CrosshairListener extends MouseAdapter implements ChartMouseListener {

		@Override
		public void chartMouseClicked(ChartMouseEvent arg0) {
		}

		@Override
		public void chartMouseMoved(ChartMouseEvent event) {
		    if (chart.getPlot() instanceof XYPlot) {
		        XYPlot plot = (XYPlot) chart.getPlot();
		        double x = plot.getDomainAxis().java2DToValue(event.getTrigger().getX(), chartPanel.getScreenDataArea(), RectangleEdge.BOTTOM);
		        // make the crosshairs disappear if the mouse is out of range
		        if (!plot.getDomainAxis().getRange().contains(x)) {
		            x = Double.NaN;
		        }
		        ((Crosshair) overlay.getDomainCrosshairs().get(0)).setValue(x);
		        
		        double y = DatasetUtilities.findYValue(dataset, getClosestSeriesIndex(x, event.getTrigger().getY()), x);
		        ((Crosshair) overlay.getRangeCrosshairs().get(0)).setValue(y);
		    }

		}

		private int getClosestSeriesIndex(double x, int compare) {
			int series = 0;
			double distance = Double.MAX_VALUE;
			for (int i = 0; i < dataset.getSeriesCount(); i++) {
				double y = DatasetUtilities.findYValue(dataset, i, x);
				double toCompare = ((XYPlot) chart.getPlot()).getRangeAxis().java2DToValue(compare, chartPanel.getScreenDataArea(), ((XYPlot) chart.getPlot()).getRangeAxisEdge());

				if (Math.abs(y - toCompare) < distance) {
					distance = Math.abs(y - toCompare);
					series = i;
				}
			}

			return series;
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			removeCrosshair();
		}

	}

}
