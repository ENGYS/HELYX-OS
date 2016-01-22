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

import javax.swing.JDialog;

public class ConsoleMonitor implements ProgressMonitor {

	@Override
	public void setTotal(int i) {
	}

	@Override
	public int getTotal() {
		return 0;
	}
	
	@Override
	public void setParent(Window parent) {
	}

	@Override
	public void start(String message, boolean canStop, Runnable r) {
		System.out.println("START " + message);
		r.run();
	}

	@Override
	public void start(String message) {
		System.out.println("START " + message);
	}

	@Override
	public void end() {
		System.out.println("END");
	}

	@Override
	public void error(String message) {
		System.err.println("ERROR: " + message);
	}

	@Override
	public void error(String message, int indentLevel) {
		System.err.println("ERROR: " + message);
	}

	@Override
	public void info(String message) {
		System.out.println("INFO: " + message);
	}

	@Override
	public void infoN(String message) {
	    System.out.print("INFO: " + message);
	}

	@Override
	public void info(String message, int indentLevel) {
		System.out.println("INFO: " + message);
	}

	@Override
	public void infoN(String message, int indentLevel) {
	    System.out.println("INFO: " + message);
	}

	@Override
	public void debug(String message) {
	    System.out.print(message);
	}

	@Override
	public void warning(String message) {
		System.out.println("WARNING: " + message);
	}

	@Override
	public void warning(String message, int indentLevel) {
		System.out.println("WARNING: " + message);
	}

	@Override
	public void warning(List<String> invalidFiles) {
		System.out.println("WARNING: " + invalidFiles);
	}

	@Override
	public int getCurrent() {
		return 0;
	}

	@Override
	public void setCurrent(String string, int i) {
	}
	
	@Override
	public void setCurrent(String string, int i, int indentLevel) {
	}

	@Override
	public void setCurrent(String string, int min, int max, int indentLevel) {
	}

	@Override
	public String getMessages() {
		return null;
	}

	@Override
	public boolean hasErrors() {
		return false;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isIndeterminate() {
		return false;
	}

	@Override
	public void setIndeterminate(boolean b) {
	}

	@Override
	public Boolean start(String message, boolean canStop, Callable<Boolean> c) {
		System.out.println("START " + message);
		try {
			return c.call();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public JDialog getDialog() {
		return null;
	}

	@Override
	public boolean canStop() {
	    return false;
	}
	
	@Override
	public void stop() {
	}
}
