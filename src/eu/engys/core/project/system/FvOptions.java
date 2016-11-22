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
package eu.engys.core.project.system;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class FvOptions extends Dictionary {
    
	public static final String FV_OPTIONS = "fvOptions";
	
	public static final String U_LIMITER_KEY = "Ulimiter";
	public static final String VELOCITY_LIMITER_SOURCE_KEY = "velocityLimiterSource";
	public static final String ACTIVE_KEY = "active";
	public static final String SELECTION_MODE_KEY = "selectionMode";
	public static final String ALL_KEY = "all";
	public static final String VELOCITY_LIMITER_SOURCE_COEFFS_KEY = "velocityLimiterSourceCoeffs";
	public static final String U_LIMIT_KEY = "Ulimit";
	public static final String LIMIT_MODE_KEY = "limitMode";
	public static final String DIAGONAL_KEY = "diagonal";
	public static final String DIAGONAL_MULTIPLIER_KEY = "diagonalMultiplier";
	public static final String U_LIMIT_RANGE_KEY = "UlimitRange";
	public static final String ALPHA_KEY = "alpha";
	public static final String DELTA_RELAX_KEY = "deltaRelax";
	public static final String VELOCITY_CLIP_KEY = "velocityClip";
	public static final String VERBOSE_KEY = "verbose";
	
    public FvOptions() {
        super(FV_OPTIONS);
        setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, FV_OPTIONS));
    }

    @Override
    public void check() throws DictionaryException {
    }
}
