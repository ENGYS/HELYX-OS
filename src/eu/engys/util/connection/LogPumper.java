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
package eu.engys.util.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPumper {

    private static final Logger logger = LoggerFactory.getLogger(LogPumper.class);

    private StringBuffer buffer = new StringBuffer();
    
    private PrintWriter out;
    private Socket socket;

    public void connect(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
        }
        pump("");
    }

    public void connect(OutputStream os) {
        this.socket = null;
        this.out = new PrintWriter(os, true);
    }
    
    public void pump(String lines) {
        if (out == null) {
            logger.debug("[LOG PUMPER] Buffering ... ");
            buffer.append(lines);
        } else {
            if (isBuffering()) {
                logger.debug("[LOG PUMPER] Flush buffer");
                out.print(buffer.toString());
                buffer.setLength(0);
            }
            out.print(lines);
            out.flush();
        }
    }

    public void pump(Exception e) {
        logger.error("[LOG PUMPER] ERROR: {}", e.getMessage());
    }
    
    public void close() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        }
        
        if (socket != null) {
            this.socket.close();
            this.socket = null;
        }

    }

    public boolean isBuffering() {
        return buffer.length() > 0;
    }
}
