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

public class Scheme {

    private SchemeTemplate template;
    private String field;
    private double value1;
    private double value2;
    private double value3;

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setValue1(double value1) {
        this.value1 = value1;
    }

    public double getValue1() {
        return value1;
    }

    public void setValue2(double value2) {
        this.value2 = value2;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue3(double value3) {
        this.value3 = value3;
    }

    public double getValue3() {
        return value3;
    }

    public void setTemplate(SchemeTemplate template) {
        this.template = template;
    }

    public SchemeTemplate getTemplate() {
        return template;
    }
}
