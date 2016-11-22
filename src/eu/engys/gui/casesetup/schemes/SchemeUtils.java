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
package eu.engys.gui.casesetup.schemes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.system.FvSchemes;

public class SchemeUtils {

    private static final Logger logger = LoggerFactory.getLogger(SchemeUtils.class);

    public static String divPhi(String gradName) {
        return String.format("div(phi,%s)", gradName);
    }

    public static String gradName(String field) {
        if (field.equals("ILambda"))
            return "Ii_h";
        else
            return field;
    }

    public static String readSchemeKeyFor(String field, FvSchemes fvSchemes) {
        String divName = divPhi(gradName(field));

        Dictionary divSchemes = fvSchemes.getDivSchemes();
        if (divSchemes != null) {
            if (divSchemes.found(divName)) {
                return divSchemes.lookup(divName);
            } else {
                logger.error(String.format("Scheme Key not found for div '%s'", divName));
                return "";
            }
        } else {
            logger.error("DivSchemes is NULL");
            return "";
        }
    }

    public static SchemeTemplate searchTemplate(String field, String scheme, List<SchemeTemplate> templates) {
        for (SchemeTemplate t : templates) {
            if (t.equalsIgnoringValues(field, scheme)) {
                return t;
            }
        }
        return null;
    }

}
