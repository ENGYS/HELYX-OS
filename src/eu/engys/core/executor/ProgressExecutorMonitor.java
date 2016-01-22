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

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class ProgressExecutorMonitor extends ExecutorTerminal {

	private ProgressMonitor progressMonitor;
	private double counter = 0;
//	private int counter = 0;
	private int max;
	private boolean stopped;
	
	public ProgressExecutorMonitor(int max) {
		this.max = max;
	}
	
	@Override
	public void start() {
		counter = 0;
		progressMonitor = new ProgressMonitor(UiUtil.getActiveWindow(), getTitle(),"", 0, 100);
		progressMonitor.setProgress((int) counter);
//		progressMonitor.setProgress(counter);
	}

	@Override
	public void finish(int returnValue) {
		super.finish(returnValue);
		progressMonitor.setProgress(progressMonitor.getMaximum());
		progressMonitor.setNote("Finished");
	}

	@Override
	public void error(final int returnValue, final String msg) {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), ExecutorMonitor.decodeError(returnValue, msg), "Execution Error", JOptionPane.ERROR_MESSAGE);
            }
        });
	}

	@Override
	public void refresh() {
	    if (stopped) return;
	    
	    counter += getIncrement();
		progressMonitor.setProgress((int)counter);
//	    progressMonitor.setProgress(++counter);
        
	    String message = String.format("%d%% Completed", (int) counter);
        
        progressMonitor.setNote(message);
//        if (progressMonitor.isCanceled() || getState() == ExecutorState.FINISH || getState() == ExecutorState.ERROR ) {
            if (progressMonitor.isCanceled()) {
            	this.stopped = stopExecutor();
            }
//        }
	}

	private double getIncrement() {
	    if (counter < 50) {
	        return 1;
	    } else if (counter < 75) {
            return 1/2D;
        } else {
            return 1/4D;
        }
    }

    public boolean stopExecutor() {
//		if (getState() == ExecutorState.START || getState() == ExecutorState.RUNNING) {
			if (executor != null) {
				int retVal = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "Stop execution?", "Close Monitor", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (retVal == JOptionPane.YES_OPTION) {
					executor.shutdownNow();
					return true;
				}
				return false;
			} else {
				return true;
			}
//		} else {
//			return true;
//		}
	}
}
