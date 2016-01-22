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


package eu.engys.core.project;

public class TurbulenceModel {

	private String name;
    private String description;
    private boolean steady;
    private boolean trans;
    private boolean compressible;
    private boolean incompressible;

    private TurbulenceModelType type;

    public TurbulenceModel() {
	}
    
    public TurbulenceModel(String name) {
    	this.name = name;
    }

    public TurbulenceModel(String name, String description) {
    	this.name = name;
    	this.description = description;
    }

    public TurbulenceModel(String name, TurbulenceModelType type) {
    	this.name = name;
		this.type = type;
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

    public void setName(String name) {
        this.name = name;
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
    
    @Override
    public String toString() {
    	return getName();
    }
    
    public boolean equals(Object obj) {
    	if (obj instanceof TurbulenceModel) {
    		TurbulenceModel tm = (TurbulenceModel) obj;
    		return name == null? tm.name == null : name.equals(tm.name);
    	} else 
    		return super.equals(obj);
    };
}
