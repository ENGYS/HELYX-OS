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

public class ConsoleExecutorMonitor extends ExecutorTerminal {

	private boolean useHeader;
    private boolean flushErrors;
    
    public ConsoleExecutorMonitor() {
        this(false, false);
    }

    public ConsoleExecutorMonitor(boolean useHeader, boolean flushErrors) {
        this.useHeader = useHeader;
        this.flushErrors = flushErrors;
    }
    
    @Override
	public void start() {
	}

	
	@Override
	public void finish(int returnValue) {
		super.finish(returnValue);
		System.err.print(getErrorStream().flushLinesBuffer());
	}

	@Override
	public void error(int returnValue, String msg) {
	    System.err.println(msg);
	    System.err.print(getErrorStream().flushLinesBuffer());
	}

	@Override
	public void refresh() {
		String outputLines = getOutputStream().flushLinesBuffer();
		System.out.print(useHeader? outputLines.replace("\n", "\n[OUT]        ") : outputLines);
		if (flushErrors) {
		    String errorLines = getErrorStream().flushLinesBuffer();
		    System.err.print(useHeader ? errorLines.replace("\n", "\n[ERR]        ") : errorLines);
		}
	}

}
