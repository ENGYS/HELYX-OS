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

package eu.engys.core.project;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TurbulenceModel {

	private String name;
    private String description;
    private boolean steady;
    private boolean trans;
    private boolean compressible;
    private boolean incompressible;

    private TurbulenceModelType type;

    public TurbulenceModel(String name, TurbulenceModelType type) {
    	this(name, name, type);
    }

    public TurbulenceModel(String name, String description, TurbulenceModelType type) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public boolean isTrans() {
        return trans;
    }

    public void setTrans(boolean trans) {
        this.trans = trans;
    }

    public boolean isIncompressible() {
        return incompressible;
    }

    public void setIncompressible(boolean incompressible) {
        this.incompressible = incompressible;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSteady() {
        return steady;
    }

    public void setSteady(boolean steady) {
        this.steady = steady;
    }

    public boolean isCompressible() {
        return compressible;
    }

    public void setCompressible(boolean compressible) {
        this.compressible = compressible;
    }

    public void setType(TurbulenceModelType type) {
        this.type = type;
    }

    public TurbulenceModelType getType() {
        return type;
    }
    
    public static TurbulenceModel getPrototypeForDisplay(){
        return new TurbulenceModel("", "MMMMMMM", TurbulenceModelType.LAMINAR);
    }
    
    @Override
    public String toString() {
    	return getName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TurbulenceModel)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        TurbulenceModel fz = (TurbulenceModel) obj;
        return new EqualsBuilder().append(name, fz.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).toHashCode();
    }
    
}
