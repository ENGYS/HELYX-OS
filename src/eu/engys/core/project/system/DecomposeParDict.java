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

import java.io.File;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.project.Model;
import eu.engys.util.Util;

public class DecomposeParDict extends Dictionary {

    public static final String DECOMPOSE_PAR_DICT = "decomposeParDict";

    public static final String NUMBER_OF_SUBDOMAINS_KEY = "numberOfSubdomains";
    public static final String HIERARCHICAL_COEFFS_KEY = "hierarchicalCoeffs";
    public static final String METHOD_KEY = "method";
    public static final String DELTA_KEY = "delta";
    public static final String ORDER_KEY = "order";
    public static final String YXZ_KEY = "yxz";
    public static final String DISTRIBUTED_KEY = "distributed";
    public static final String N_KEY = "n";

    public static final String HIERARCHICAL_KEY = "hierarchical";
    public static final String SCOTCH_KEY = "scotch";

    public static final String[] TYPE_KEYS = { HIERARCHICAL_KEY, SCOTCH_KEY };

    public DecomposeParDict() {
        super(DECOMPOSE_PAR_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, DECOMPOSE_PAR_DICT));
    }

    public DecomposeParDict(DecomposeParDict newDict) {
        super(newDict);
    }

    public DecomposeParDict(File file) {
        this();
        readDictionary(file);
    }

    public void check() throws DictionaryException {
    }

    public void toHierarchical(Model model) {
        if (methodIsScotch()) {
            Dictionary newHCDict = new Dictionary(model.getDefaults().getDefaultDecomposeParDict().subDict(HIERARCHICAL_COEFFS_KEY));
            int[] subdomainValues = calculateSubdomainValues();
            int x = subdomainValues[0];
            int y = subdomainValues[1];
            int z = subdomainValues[2];
            newHCDict.add(N_KEY, "(" + x + " " + y + " " + z + ")");

            add(METHOD_KEY, HIERARCHICAL_KEY);
            add(newHCDict);
            DictionaryUtils.writeDictionary(model.getProject().getSystemFolder().getFileManager().getFile(), this, null);
        }
    }

    private int[] calculateSubdomainValues() {
        int numberOfSubdomains = Integer.parseInt(lookup(NUMBER_OF_SUBDOMAINS_KEY));
        int[] subdomainValues = Util.getFactorsFor(numberOfSubdomains);
        return subdomainValues;
    }

    private boolean methodIsScotch() {
        return found(METHOD_KEY) && SCOTCH_KEY.equals(lookup(METHOD_KEY));
    }
}
