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

import java.io.IOException;
import java.io.OutputStream;

public class TerminalOutputStream extends OutputStream {

    public static final char NEW_LINE = '\n';

    private final StringBuffer charactersBuffer = new StringBuffer(128);
    private final StringBuffer linesBuffer = new StringBuffer();

    @Override
    public void write(int c) throws IOException {
        // append character to buffer
        charactersBuffer.append((char) c);
        // and newline appends to textarea
        if (c == NEW_LINE) {
            flush();
        }
    }

    @Override
    public void close() {
    }

    @Override
    public final void flush() {
        String str = charactersBuffer.toString();
        linesBuffer.append(str);
        charactersBuffer.setLength(0);
    }

    public String flushLinesBuffer() {
        String s = linesBuffer.toString();
        linesBuffer.setLength(0);
        return s;
    }

    public String peekLines() {
        return linesBuffer.toString();
    }
}
