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

package eu.engys.core.project.mesh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;

import com.google.common.collect.Lists;

import eu.engys.util.ui.ResourcesUtil;

public enum ScalarBarType {

    BLUE_TO_RED_RAINBOW("Rainbow (Blue to Red)", ResourcesUtil.getIcon("scalarbar.rainbow.icon")), 
    RED_TO_BLUE_RAINBOW("Rainbow (Red to Blue)", ResourcesUtil.getIcon("scalarbar.rainbow.inverted.icon")),

    BLUE_TO_RED_HSV("Blue to Red (HSV)", ResourcesUtil.getIcon("scalarbar.bluetored.hsv.icon")), 
    RED_TO_BLUE_HSV("Red to Blue (HSV)", ResourcesUtil.getIcon("scalarbar.redtoblue.hsv.icon")), 
    BLUE_TO_RED_RGB("Blue to Red (RGB)", ResourcesUtil.getIcon("scalarbar.bluetored.rgb.icon")), 
    RED_TO_BLUE_RGB("Red to Blue (RGB)", ResourcesUtil.getIcon("scalarbar.redtoblue.rgb.icon")), 
    BLUE_TO_RED_DIV("Blue to Red (Diverging)", ResourcesUtil.getIcon("scalarbar.bluetored.div.icon")), 
    RED_TO_BLUE_DIV("Red to Blue (Diverging)", ResourcesUtil.getIcon("scalarbar.redtoblue.div.icon")),

    BLUE_TO_YELLOW_HSV("Blue to Yellow (HSV)", ResourcesUtil.getIcon("scalarbar.bluetoyellow.hsv.icon")), 
    YELLOW_TO_BLUE_HSV("Yellow to Blue (HSV)", ResourcesUtil.getIcon("scalarbar.yellowtoblue.hsv.icon")), 
    BLUE_TO_YELLOW_RGB("Blue to Yellow (RGB)", ResourcesUtil.getIcon("scalarbar.bluetoyellow.rgb.icon")), 
    YELLOW_TO_BLUE_RGB("Yellow to Blue (RGB)", ResourcesUtil.getIcon("scalarbar.yellowtoblue.rgb.icon")), 
    BLUE_TO_YELLOW_DIV("Blue to Yellow (Diverging)", ResourcesUtil.getIcon("scalarbar.bluetoyellow.div.icon")), 
    YELLOW_TO_BLUE_DIV("Yellow to Blue (Diverging)", ResourcesUtil.getIcon("scalarbar.yellowtoblue.div.icon")),

    BLACK_TO_WHITE("Black to White", ResourcesUtil.getIcon("scalarbar.blacktowhite.icon")), 
    WHITE_TO_BLACK("White to Black", ResourcesUtil.getIcon("scalarbar.whitetoblack.icon"));

    private String label;
    private Icon icon;

    private ScalarBarType(String label, Icon icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public List<double[]> getColors(int resolution) {
        switch (label) {
        // RAINBOW
        case "Rainbow (Blue to Red)":
            return getRainbowColors();
        case "Rainbow (Red to Blue)":
            return getRainbowColorsInverted();
            // RED TO BLUE
        case "Blue to Red (HSV)":
            return getBlueToRedHSVColors(resolution);
        case "Red to Blue (HSV)":
            return getRedToBlueHSVColors(resolution);
        case "Blue to Red (RGB)":
            return getBlueToRedRGBColors(resolution);
        case "Red to Blue (RGB)":
            return getRedToBlueRGBColors(resolution);
        case "Blue to Red (Diverging)":
            return getBlueToRedDivergingColors(resolution);
        case "Red to Blue (Diverging)":
            return getRedToBlueDivergingColors(resolution);
            // BLUE TO YELLOW
        case "Blue to Yellow (HSV)":
            return getBlueToYellowHSVColors(resolution);
        case "Yellow to Blue (HSV)":
            return getYellowToBlueHSVColors(resolution);
        case "Blue to Yellow (RGB)":
            return getBlueToYellowRGBColors(resolution);
        case "Yellow to Blue (RGB)":
            return getYellowToBlueRGBColors(resolution);
        case "Blue to Yellow (Diverging)":
            return getBlueToYellowDivergingColors(resolution);
        case "Yellow to Blue (Diverging)":
            return getYellowToBlueDivergingColors(resolution);
            // GRAYSCALE
        case "Black to White":
            return getBlackToWhiteColors(resolution);
        case "White to Black":
            return getWhiteToBlackColors(resolution);
        default:
            return new ArrayList<double[]>();
        }
    }

    public Icon getIcon() {
        return icon;
    }

    /*
     * Utils
     */

    public static Icon getIconByLabel(String label) {
        ScalarBarType[] all = values();
        for (ScalarBarType type : all) {
            if (type.getLabel().equals(label)) {
                return type.getIcon();
            }
        }
        return null;
    }

    public static ScalarBarType getTypeByLabel(String label) {
        ScalarBarType[] all = values();
        for (ScalarBarType type : all) {
            if (type.getLabel().equals(label)) {
                return type;
            }
        }
        return null;

    }

    public static String[] labels() {
        ScalarBarType[] all = values();
        String[] labels = new String[all.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = all[i].getLabel();
        }
        return labels;
    }

    /***** COLORS *****/

    /*
     * RED TO BLUE
     */

    // HSV (rainbow)
    private static List<double[]> getRainbowColors() {
        List<double[]> colors = new ArrayList<>();
        colors.add(new double[] { 0.667, 0 });
        return colors;
    }

    private static List<double[]> getRainbowColorsInverted() {
        List<double[]> colors = new ArrayList<>();
        colors.add(new double[] { 0, 0.667 });
        return colors;
    }

    // HSV (red -> pink -> blue)
    private static List<double[]> getBlueToRedHSVColors(int resolution) {
        List<double[]> colors = new LinkedList<>();
        if (resolution == 1) {
            colors.add(new double[] { 0, 0, 1 });
        } else if (resolution == 2) {
            colors.add(new double[] { 0, 0, 1 });
            colors.add(new double[] { 1, 0, 0 });
        } else if (resolution > 2) {
            int limit1 = (resolution % 2 == 0) ? (resolution / 2) : (resolution - 1)/2;
            int limit2 = (resolution - 1)/2;
            colors.add(new double[] { 0, 0, 1 });
            for (float i = 1; i < limit1; i++) {
                colors.add(new double[] { i / limit1, 0, 1 });
            }
            colors.add(new double[] { 1, 0, 1 });
            for (float i = 1; i < limit2; i++) {
                colors.add(new double[] { 1, 0, 1 - (i / limit2) });
            }
            colors.add(new double[] { 1, 0, 0 });
        }
        return colors;
    }

    private static List<double[]> getRedToBlueHSVColors(int resolution) {
        return Lists.reverse(getBlueToRedHSVColors(resolution));
    }

    // RGB (red -> blue)
    private static List<double[]> getBlueToRedRGBColors(int resolution) {
        List<double[]> colors = new LinkedList<>();
        if (resolution == 1) {
            colors.add(new double[] { 0, 0, 1 });
        } else if (resolution > 1) {
            int limit = resolution -1;
            colors.add(new double[] { 0, 0, 1 });
            for (float i = 1; i < limit; i++) {
                colors.add(new double[] { i / limit, 0, 1 - (i / limit) });
            }
            colors.add(new double[] { 1, 0, 0 });
        }
        return colors;
    }

    private static List<double[]> getRedToBlueRGBColors(int resolution) {
        return Lists.reverse(getBlueToRedRGBColors(resolution));
    }

    // DIVERGING (red -> white -> blue)
    private static List<double[]> getBlueToRedDivergingColors(int resolution) {
        List<double[]> colors = new LinkedList<>();
        if (resolution == 1) {
            colors.add(new double[] { 0, 0, 1 });
        } else if (resolution == 2) {
            colors.add(new double[] { 0, 0, 1 });
            colors.add(new double[] { 1, 0, 0 });
        } else if (resolution > 2) {
            int limit1 = (resolution % 2 == 0) ? (resolution / 2) : (resolution - 1)/2;
            int limit2 = (resolution - 1)/2;
            colors.add(new double[] { 0, 0, 1 });
            for (float i = 1; i < limit1; i++) {
                colors.add(new double[] { i / limit1, i / limit1, 1 });
            }
            colors.add(new double[] { 1, 1, 1 });
            for (float i = 1; i < limit2; i++) {
                colors.add(new double[] { 1, 1 - (i / limit2), 1 - (i / limit2) });
            }
            colors.add(new double[] { 1, 0, 0 });
        }
        return colors;
    }

    private static List<double[]> getRedToBlueDivergingColors(int resolution) {
        return Lists.reverse(getBlueToRedDivergingColors(resolution));
    }

    /*
     * BLUE TO YELLOW
     */

    // HSV (blue -> green -> yellow)
    private static List<double[]> getBlueToYellowHSVColors(int resolution) {
        List<double[]> colors = new LinkedList<>();
        if (resolution == 1) {
            colors.add(new double[] { 0, 0, 1 });
        } else if (resolution == 2) {
            colors.add(new double[] { 0, 0, 1 });
            colors.add(new double[] { 1, 1, 0 });
        } else if (resolution > 2) {
            int limit1 = (resolution % 2 == 0) ? (resolution / 2) : (resolution - 1)/2;
            int limit2 = (resolution - 1)/2;
            colors.add(new double[] { 0, 0, 1 });
            for (float i = 1; i < limit1; i++) {
                colors.add(new double[] { 0, i / limit1, 1 - (i / limit1) });
            }
            colors.add(new double[] { 0, 1, 0 });
            for (float i = 1; i < limit2; i++) {
                colors.add(new double[] { 1 - (i / limit2), 1, 0 });
            }
            colors.add(new double[] { 1, 1, 0 });
        }
        return colors;
    }

    private static List<double[]> getYellowToBlueHSVColors(int resolution) {
        return Lists.reverse(getBlueToYellowHSVColors(resolution));
    }

    // RGB (blue -> yellow)
    private static List<double[]> getBlueToYellowRGBColors(int resolution) {
        List<double[]> colors = new LinkedList<>();
        if (resolution == 1) {
            colors.add(new double[] { 0, 0, 1 });
        } else if (resolution > 1) {
            int limit = resolution - 1;
            colors.add(new double[] { 0, 0, 1 });
            for (float i = 1; i < limit; i++) {
                colors.add(new double[] { i / limit, i / limit, 1 - (i / limit) });
            }
            colors.add(new double[] { 1, 1, 0 });
        }
        return colors;
    }

    private static List<double[]> getYellowToBlueRGBColors(int resolution) {
        return Lists.reverse(getBlueToYellowRGBColors(resolution));
    }

    // DIVERGING (blue -> white -> yellow)
    private static List<double[]> getBlueToYellowDivergingColors(int resolution) {
        List<double[]> colors = new LinkedList<>();
        if (resolution == 1) {
            colors.add(new double[] { 0, 0, 1 });
        } else if (resolution == 2) {
            colors.add(new double[] { 0, 0, 1 });
            colors.add(new double[] { 1, 1, 0 });
        } else if (resolution > 2) {
            int limit1 = (resolution % 2 == 0) ? (resolution / 2) : (resolution - 1)/2;
            int limit2 = (resolution - 1)/2;
            colors.add(new double[] { 0, 0, 1 });
            for (float i = 1; i < limit1; i++) {
                colors.add(new double[] { i / limit1, i / limit1, 1 });
            }
            colors.add(new double[] { 1, 1, 1 });
            for (float i = 1; i < limit2; i++) {
                colors.add(new double[] { 1, 1, 1 - (i / limit2) });
            }
            colors.add(new double[] { 1, 1, 0 });
        }
        return colors;
    }

    private static List<double[]> getYellowToBlueDivergingColors(int resolution) {
        return Lists.reverse(getBlueToYellowDivergingColors(resolution));
    }

    /*
     * GRAYSCALE
     */
    private static List<double[]> getBlackToWhiteColors(int resolution) {
        List<double[]> colors = new LinkedList<>();
        if (resolution == 1) {
            colors.add(new double[] { 0, 0, 0 });
        } else if (resolution > 1) {
            int limit = resolution - 1;
            colors.add(new double[] { 0, 0, 0 });
            for (float i = 1; i < limit; i++) {
                colors.add(new double[] { i / (limit), i / (limit), i / (limit) });
            }
            colors.add(new double[] { 1, 1, 1 });
        }
        return colors;
    }

    private static List<double[]> getWhiteToBlackColors(int resolution) {
        return Lists.reverse(getBlackToWhiteColors(resolution));
    }

}
