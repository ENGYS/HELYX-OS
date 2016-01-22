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

public interface ProgressMonitor {
	
	void setTotal(int i);
	int getTotal();


	void start(String string, boolean canStop, Runnable r);
	Boolean start(String string, boolean canStop, Callable<Boolean> c);
	
	void start(String string);
	void end();

	void debug(String message);
	void error(String message);
	void error(String message, int indentLevel);
	void info(String message);
	void info(String message, int indentLevel);
	void infoN(String message);
	void infoN(String message, int indentLevel);
	void warning(String message);
	void warning(String message, int indentLevel);
	void warning(List<String> invalidFiles);

	int getCurrent();
	void setCurrent(String string, int i);
	void setCurrent(String string, int i, int indentLevel);
	void setCurrent(String string, int min, int max, int indentLevel);


	String getMessages();

	boolean hasErrors();

	boolean isFinished();

	boolean isIndeterminate();
	void setIndeterminate(boolean b);
	public JDialog getDialog();
    
	void setParent(Window parent);
    
	boolean canStop();
    void stop();

}
