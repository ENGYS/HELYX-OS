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


package eu.engys.core;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import eu.engys.util.Symbols;
import eu.engys.util.Util;

public class LoggerUtil {

	public static void initLogger() {
		if (Util.isWindows()) {
			initNormalLogger();
		} else {
			initColorLogger();
		}
	}
	
	private static void initColorLogger() {
		org.apache.log4j.Logger.getRootLogger().addAppender(new ColorConsoleAppender(new PatternLayout("%6r - %-30.30t - %-5p %-30.30c{1} %x - %m%n")));
    	org.apache.log4j.Logger.getRootLogger().setLevel(Arguments.logLevel);
	}

	private static void initNormalLogger() {
		org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%6r - %-30.30t - %-5p %-30.30c{1} %x - %m%n")));
		org.apache.log4j.Logger.getRootLogger().setLevel(Arguments.logLevel);
	}

	public static void initFlatLogger() {
		org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p %m%n")));
		org.apache.log4j.Logger.getRootLogger().setLevel(Arguments.logLevel);
	}

	public static void initTestLogger() {
		org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
	}

	public static void initTestLogger(Level level) {
		org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
		org.apache.log4j.Logger.getRootLogger().setLevel(level);
	}

	public static void initFileLogger(String file, Level level) throws IOException {
		org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
		org.apache.log4j.Logger.getRootLogger().addAppender(new FileAppender(new PatternLayout("%r [%t] %p %c %x - %m%n"), file, false));
		org.apache.log4j.Logger.getRootLogger().setLevel(level);
	}
	
	private static final String LOG_FILE_NAME = "log";
    private static final String TXT_EXT = ".txt";

	public static void redirectLog(File tempDirectory) {
	    File log = new File(tempDirectory, LOG_FILE_NAME + TXT_EXT);
	    try {
	        initFileLogger(log.getAbsolutePath(), Level.DEBUG);
	    } catch (Exception e) {
	        System.out.println("ERROR : Unable to open logger file!");
	        System.exit(-1);
	    }
	}
	
	static class ColorConsoleAppender extends ConsoleAppender {
	    private static final int NORMAL = 0;
	    private static final int BRIGHT = 1;
	    // private static final int FOREGROUND_BLACK = 30;
	    private static final int FOREGROUND_RED = 31;
	    private static final int FOREGROUND_GREEN = 32;
	    private static final int FOREGROUND_YELLOW = 33;
	    private static final int FOREGROUND_BLUE = 34;
	    // private static final int FOREGROUND_MAGENTA = 35;
	    private static final int FOREGROUND_CYAN = 36;
	    // private static final int FOREGROUND_WHITE = 37;

	    private static final String PREFIX = Symbols.ESC + "[";
	    private static final String SUFFIX = "m";
	    private static final char SEPARATOR = ';';
	    private static final String END_COLOUR = PREFIX + SUFFIX;

	    private static final String FATAL_COLOUR = PREFIX + BRIGHT + SEPARATOR + FOREGROUND_RED + SUFFIX;
	    private static final String ERROR_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_RED + SUFFIX;
	    private static final String WARN_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_YELLOW + SUFFIX;
	    private static final String INFO_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_GREEN + SUFFIX;
	    private static final String DEBUG_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_CYAN + SUFFIX;
	    private static final String TRACE_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_BLUE + SUFFIX;

	    public ColorConsoleAppender(Layout layout) {
	        super(layout);
	    }

	    @Override
	    protected void subAppend(LoggingEvent event) {
	        this.qw.write(getColour(event.getLevel()));
	        super.subAppend(event);
	        this.qw.write(END_COLOUR);

	        if (this.immediateFlush) {
	            this.qw.flush();
	        }
	    }

	    private String getColour(Level level) {
	        switch (level.toInt()) {
	        case Priority.FATAL_INT:
	            return FATAL_COLOUR;
	        case Priority.ERROR_INT:
	            return ERROR_COLOUR;
	        case Priority.WARN_INT:
	            return WARN_COLOUR;
	        case Priority.INFO_INT:
	            return INFO_COLOUR;
	        case Priority.DEBUG_INT:
	            return DEBUG_COLOUR;
	        default:
	            return TRACE_COLOUR;
	        }
	    }
	}
}
