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
package eu.engys.core.project.state;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MultiphaseModel {

    public static final String OFF_LABEL = "Off";
    public static final MultiphaseModel OFF = new MultiphaseModel(OFF_LABEL, "", false, false);

    private String label;
    private boolean multiphase;
    private boolean dynamic;
    private String key;

    public MultiphaseModel(String label, String key, boolean multiphase, boolean dynamic) {
        this.label = label;
        this.key = key;
        this.multiphase = multiphase;
        this.dynamic = dynamic;
    }

    public String getLabel() {
        return label;
    }

    public String getKey() {
        return key;
    }

    public boolean isMultiphase() {
        return multiphase;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public boolean isOff() {
        return label.equals(OFF_LABEL);
    }

    public boolean isOn() {
        return !label.equals(OFF_LABEL);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultiphaseModel)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        MultiphaseModel fz = (MultiphaseModel) obj;
        return new EqualsBuilder().append(label, fz.label).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(label).toHashCode();
    }
}
