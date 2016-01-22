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


package eu.engys.core.project.system;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class FvSchemes extends Dictionary {

    public static final String FV_SCHEMES = "fvSchemes";
    public static final String SN_GRAD_SCHEMES = "snGradSchemes";
    public static final String INTERPOLATION_SCHEMES = "interpolationSchemes";
    public static final String LAPLACIAN_SCHEMES = "laplacianSchemes";
    public static final String DIV_SCHEMES = "divSchemes";
    public static final String GRAD_SCHEMES = "gradSchemes";
    public static final String DDT_SCHEMES = "ddtSchemes";
	public static final String DEFAULT = "default";
	public static final String STEADY_STATE = "steadyState";
	public static final String EULER = "Euler";
	public static final String BACKWARD = "backward";
	public static final String LOCAL_EULER_RDELTAT = "localEuler rDeltaT";//LTSInterfoam

    public FvSchemes() {
        super(FV_SCHEMES);
        setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, FV_SCHEMES));
    }

    public Dictionary getDdtSchemes() {
        return subDict(DDT_SCHEMES);
    }

    public Dictionary getGradSchemes() {
        return subDict(GRAD_SCHEMES);
    }

    public Dictionary getDivSchemes() {
        return subDict(DIV_SCHEMES);
    }

    public Dictionary getLaplacianSchemes() {
        return subDict(LAPLACIAN_SCHEMES);
    }

    public Dictionary getInterpolationSchemes() {
        return subDict(INTERPOLATION_SCHEMES);
    }

    public Dictionary getSnGradSchemes() {
        return subDict(SN_GRAD_SCHEMES);
    }

    @Override
    public void check() throws DictionaryException {
        if (found(DDT_SCHEMES)) {

        } else {
            throw new DictionaryException(DDT_SCHEMES + " not found");
        }
        if (found(GRAD_SCHEMES)) {

        } else {
            throw new DictionaryException(GRAD_SCHEMES + " not found");
        }
        if (found(DIV_SCHEMES)) {

        } else {
            throw new DictionaryException(DIV_SCHEMES + " not found");
        }
        if (found(LAPLACIAN_SCHEMES)) {
            ;
        } else {
            throw new DictionaryException(LAPLACIAN_SCHEMES + " not found");
        }
        if (found(INTERPOLATION_SCHEMES)) {
            ;
        } else {
            throw new DictionaryException(INTERPOLATION_SCHEMES + " not found");
        }
        if (found(SN_GRAD_SCHEMES)) {
            ;
        } else {
            throw new DictionaryException(SN_GRAD_SCHEMES + " not found");
        }
    }

}
