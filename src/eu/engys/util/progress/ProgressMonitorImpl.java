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


package eu.engys.util.progress;

import java.awt.Window;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class ProgressMonitorImpl implements ProgressMonitor {

	// private static final Logger logger =
	// LoggerFactory.getLogger(ProgressMonitorImpl.class);
	
	private static final String INDENT = "   ";

	private static final String START = "<html><pre>";
	private static final String END = "</pre></html>";

	private static final String START_INFO = "<font size=3 color=black >";
	private static final String END_INFO = "</font>\n";
	private static final String END_INFO_N = "</font>";

	private static final String START_B = "<b>";
	private static final String END_B = "</b\n";

	private static final String START_ERROR = "<font size=3 color=red >";
	private static final String END_ERROR = "</font>\n";

	private static final String START_WARING = "<font size=3 color=blue >";
	private static final String END_WARNING = "</font>\n";

	int total, current = -1;
	boolean indeterminate = false;
	boolean finished = false;

	private final StringBuffer sb = new StringBuffer();
	private boolean hasErrors;
	private ProgressDialog dialog;

    private Window parent;

	@Inject
	public ProgressMonitorImpl() {
		this(0, false);
	}

	public ProgressMonitorImpl(int total, boolean indeterminate) {
		this.total = total;
		this.indeterminate = indeterminate;
	}
	
	@Override
	public void setParent(Window parent){
	    this.parent = parent;
	}

	protected ProgressDialog dialog() {
		if (dialog == null) {
			try {
				ExecUtil.invokeAndWait(new Runnable() {
					@Override
					public void run() {
                        dialog = new ProgressDialog(parent != null ? parent : UiUtil.getActiveWindow());
						dialog.setName("progress.monitor");
						dialog.init(ProgressMonitorImpl.this);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dialog;
	}

	@Override
	public ProgressDialog getDialog() {
		return dialog;
	}

	@Override
	public int getTotal() {
		return total;
	}

	@Override
	public void setTotal(int total) {
		this.total = total;
		dialog().update();
	}

	private Future<?> currentTask = null;
	private boolean stoppable = false;

	@Override
	public void start(String status, boolean canStop, Runnable r) {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		this.currentTask = executor.submit(r);
		prepareStart(status, canStop);
		dialog().start();
		try {
		    currentTask.get();
		} catch (ExecutionException | InterruptedException e) {
		    error(e);
		    executor.shutdownNow();
		    end();
		    e.printStackTrace();
		}
	}
	
	@Override
	public Boolean start(String status, boolean canStop, final Callable<Boolean> c) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Boolean> task = executor.submit(c);
		prepareStart(status, canStop);
		dialog().start();
		try {
			return task.get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void start(String status) {
		prepareStart(status, false);
		dialog().start();
	}

	private void prepareStart(String status, boolean canStop) {
		stoppable = canStop;
		finished = false;
		hasErrors = false;
		if (current != -1) {
			current = -1;
		}
		sb.append(START);
		if (status != null)
			info(START_B + status + END_B);
		current = 0;
	}

	@Override
	public boolean canStop() {
	    return stoppable;
	}
	
	@Override
	public void stop() {
	    if (stoppable) {
	        if (currentTask != null) {
	            currentTask.cancel(true);
	        }
	    }
	}
	
	@Override
	public void end() {
		finished = true;
		if (current != total)
			current = total;
		dialog().end();
	}

	@Override
	public int getCurrent() {
		return current;
	}

	@Override
	public String getMessages() {
		return sb.toString() + END;
	}

	@Override
	public boolean isIndeterminate() {
		return indeterminate;
	}

	@Override
	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
		dialog().update();
	}
	
	@Override
	public void setCurrent(String msg, int current) {
		if (current == -1)
			throw new IllegalStateException("not started yet");
		this.current = current;
		if (msg != null)
			info(msg);
		dialog().update();
	}
	
	@Override
	public void setCurrent(String msg, int current, int indentLevel) {
		if (current == -1)
			throw new IllegalStateException("not started yet");
		this.current = current;
		if (msg != null)
			info(msg, indentLevel);
		dialog().update();
	}

	@Override
	public void setCurrent(String msg, int min, int max, int indentLevel) {
		if (current == -1)
			throw new IllegalStateException("not started yet");
		this.total = max;
		this.current = min;
		if (msg != null) {
			sb.append(START_INFO);
			sb.append(addIndentation(msg, indentLevel));
			sb.append(END_INFO);
		}
		dialog().update();
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void info(String message, int indentLevel) {
		info(addIndentation(message, indentLevel));
	}

	@Override
	public void infoN(String message, int indentLevel) {
	    infoN(addIndentation(message, indentLevel));
	}

	@Override
	public void info(String message) {
		sb.append(START_INFO);
		sb.append(message);
		sb.append(END_INFO);
		dialog().update();
	}

	@Override
	public void infoN(String message) {
	    sb.append(START_INFO);
	    sb.append(message);
	    sb.append(END_INFO_N);
	    dialog().update();
	}
	
	@Override
	public void warning(String message, int indentLevel) {
		warning(addIndentation(message, indentLevel));
	}
	
	private static String addIndentation(String message, int indentLevel) {
		String toAppend = "";
		for (int i = 0; i < indentLevel; i++) {
			toAppend += INDENT;
		}
		toAppend += message;
		return toAppend;
	}

    @Override
    public void debug(String message) {
//      logger.info(message);
//        sb.append(START_INFO);
        sb.append(message);
//        sb.append(END_INFO);
        dialog().update();
    }
    
	@Override
	public void warning(String message) {
		// logger.warn(message);
		sb.append(START_WARING);
		sb.append(message);
		sb.append(END_WARNING);
		dialog().update();
	}

	@Override
	public void warning(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String name : list) {
			sb.append("\n");
			sb.append("\t");
			sb.append(name);
		}
		warning(sb.toString());
	}
	
	@Override
	public void error(String message, int indentLevel) {
		error(addIndentation(message, indentLevel));
	}

	@Override
	public void error(String message) {
		// logger.error(message);
		hasErrors = true;
		sb.append(START_ERROR);
		sb.append(message);
		sb.append(END_ERROR);
		dialog().update();
	}

	private void error(Throwable t) {
		StackTraceElement[] stackTrace = t.getStackTrace();
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement el : stackTrace) {
			sb.append(START_ERROR);
			sb.append("    ");
			sb.append(el.toString());
			sb.append(END_ERROR);
		}
	}

	@Override
	public boolean hasErrors() {
		return hasErrors;
	}

}
