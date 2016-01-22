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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.Util;
import eu.engys.util.ui.UiUtil;

public class MailManagerSupport {

    private static final Logger logger = LoggerFactory.getLogger(MailManagerSupport.class);
    // private static final String PREFERRED_MAIL_MANAGER =
    // PrefUtil.getString(PrefUtil.HELYX_DEFAULT_MAIL_MANAGER);
    private static final String SUPPORT_MAIL = "support@engys.com";
    private static final String SUPPORT_SUBJECT = "Support request";

    public static void mailSupport() {
        mail(SUPPORT_MAIL, SUPPORT_SUBJECT);
    }

    public static void mail(String to, String subject) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.MAIL)) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:" + to + "?subject=" + subject + "&body="));
                } catch (IOException | URISyntaxException e) {
                    _mail(to, subject, e.getMessage());
                }
            } else {
                _mail(to, subject, "Mail action not supported");
            }
        } else {
            _mail(to, subject, "Desktop class not supported by this platform");
        }
    }

    private static void _mail(String to, String subject, String logInfo) {
        try {
            ProcessBuilder pb = getProcessBuilder(to, subject);
            pb.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No mail client found.", "Mail error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static ProcessBuilder getProcessBuilder(String to, String subject) {
        ProcessBuilder pb = null;
        if (Util.isWindows()) {
            pb = new ProcessBuilder("rundll32.exe", "url.dll,FileProtocolHandler", "mailto:" + to + "?subject=" + subject + "&body=");
        } else {
            MailToken token = getLinuxMailManager(to, subject);
            pb = new ProcessBuilder(token.getProvider(), token.getCommand());
        }
        return pb;
    }

    private static MailToken getLinuxMailManager(String to, String subject) {
        // if (PREFERRED_MAIL_MANAGER.isEmpty()) {
        if (checkMailClient("thunderbird")) {
            return new MailToken("thunderbird", "-compose \"to='" + to + "',subject='" + subject.replace("%20", " ") + "'\"");
        } else if (checkMailClient("evolution")) {
            return new MailToken("evolution", "");
        } else {
            logger.error("No mail client found");
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "No mail client found", "Mail Error", JOptionPane.ERROR_MESSAGE);
            return new MailToken("", "");
        }
        // } else {
        // return new MailToken(PREFERRED_MAIL_MANAGER, "");
        // }
    }

    private static boolean checkMailClient(String client) {
        try {
            new ProcessBuilder(client, "--help").start().waitFor();
            return true;
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }

    private static class MailToken {

        private final String provider;
        private final String command;

        public MailToken(String provider, String command) {
            this.provider = provider;
            this.command = command;
        }

        public String getProvider() {
            return provider;
        }

        public String getCommand() {
            return command;
        }

    }

}
