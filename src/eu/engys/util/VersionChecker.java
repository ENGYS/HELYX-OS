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
package eu.engys.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionChecker {

    private static final Logger logger = LoggerFactory.getLogger(VersionChecker.class);

    private static final String PATH = "http://engys.com/helyx-os/version.txt";

    public enum VersionType {

        UPDATED, OLD, NOT_AVAILABLE;
        
        public boolean isUpdated(){
            return this == UPDATED;
        }

        public boolean isOld(){
            return this == OLD;
        }

        public boolean isNotAvailable(){
            return this == NOT_AVAILABLE;
        }

    }

    public static VersionType isNewVersionAvailable() {
        try {
            String actual = getActualVersion();
            String online = getOnlineVersion();
            logger.info("Acutal Version: " + actual + ", Latest Version: " + online);
            if (actual.isEmpty() || online.isEmpty()) {
                return VersionType.NOT_AVAILABLE;
            }
            int[] actualNumber = extractVersionNumber(actual);
            int[] onlineNumber = extractVersionNumber(online);
            if (isEarlier(actualNumber, onlineNumber)) {
                return VersionType.OLD;
            } else {
                return VersionType.UPDATED;
            }
        } catch (Exception e) {
            return VersionType.NOT_AVAILABLE;
        }
    }

    public static int[] extractVersionNumber(String version) {
        String versionNumber = version.replace("v", "").trim();
        final int[] vers = new int[3];
        final StringTokenizer token = new StringTokenizer(versionNumber, ".");

        for (int i = 0; i < 3; i++) {
            if (token.hasMoreTokens()) {
                try {
                    vers[i] = Integer.parseInt(token.nextToken());
                } catch (final Exception e) {
                    break;
                }
            }
        }

        return vers;
    }

    public static String getActualVersion() {
        return ApplicationInfo.getVersion();
    }

    public static String getOnlineVersion() {
        try {
            URL url = new URL(PATH);
            URLConnection uc = url.openConnection();

            InputStreamReader input = new InputStreamReader(uc.getInputStream());
            BufferedReader in = new BufferedReader(input);
            String inputLine;
            StringBuffer sb = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
            return sb.toString();
        } catch (IOException e) {
            logger.warn("Unable to find latest version online", e.getMessage());
            return "";
        }
    }

    public static boolean isEarlier(int[] v1, int[] v2) {
        boolean returnValue = false;

        if (!((v1 == null) || (v2 == null))) {
            for (int i = 0; i < 3; i++) {
                if (v1[i] < v2[i]) {
                    returnValue = true;
                    break;
                } else if (v1[i] > v2[i]) {
                    break;
                }
            }
        }

        return returnValue;
    }

}
