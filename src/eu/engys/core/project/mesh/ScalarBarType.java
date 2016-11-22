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
package eu.engys.core.project.mesh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;

import com.google.common.collect.Lists;

import eu.engys.util.ui.ResourcesUtil;
import vtk.vtkDiscretizableColorTransferFunction;

public enum ScalarBarType {

    BLUE_TO_RED_HSV("Blue to Red (HSV)",                    ResourcesUtil.getIcon("scalarbar.bluetored.hsv.icon")),
    BLUE_TO_RED_RGB("Blue to Red (RGB)",                    ResourcesUtil.getIcon("scalarbar.bluetored.rgb.icon")),
    BLUE_TO_RED_DIV("Blue to Red (Diverging)",              ResourcesUtil.getIcon("scalarbar.bluetored.div.icon")),
    BLUE_TO_RED_DESATURATED("Blue to Red (Desaturated)",    ResourcesUtil.getIcon("scalarbar.bluetored.des.icon")),
    BLUE_TO_RED_JET("Blue to Red (Jet)",                    ResourcesUtil.getIcon("scalarbar.bluetored.jet.icon")),
    COLD_AND_HOT("Cold and Hot",                            ResourcesUtil.getIcon("scalarbar.bluetored.coldhot.icon")),

    BLUE_TO_YELLOW_HSV("Blue to Yellow (HSV)",              ResourcesUtil.getIcon("scalarbar.bluetoyellow.hsv.icon")),
    BLUE_TO_YELLOW_RGB("Blue to Yellow (RGB)",              ResourcesUtil.getIcon("scalarbar.bluetoyellow.rgb.icon")),
    BLUE_TO_YELLOW_DIV("Blue to Yellow (Diverging)",        ResourcesUtil.getIcon("scalarbar.bluetoyellow.div.icon")),

    BLACK_TO_WHITE("Black to White",                        ResourcesUtil.getIcon("scalarbar.blacktowhite.icon")),
    BLACK_BODY_RADIATION("Black Body Radiation",            ResourcesUtil.getIcon("scalarbar.blackbodyradiation.icon")),
    BLACK_BLUE_WHITE("Black Blue and White",                ResourcesUtil.getIcon("scalarbar.blackbluewhite.icon")),
    BLACK_ORANGE_WHITE("Black Orange and White",            ResourcesUtil.getIcon("scalarbar.blackorangewhite.icon"));

    private static final int RGB = 0;
    private static final int HSV = 1;
    private static final int LAB = 2;
    private static final int DIV = 3;

    /**
     * 0    ->  0
     * 32   ->  0.125
     * 64   ->  0.25
     * 96   ->  0.375
     * 128  ->  0.5
     * 160  ->  0.625
     * 192  ->  0.75
     * 224  ->  0.875
     * 256  ->  1
     */
//    BLUE_LIGHTER,BLUE_DARKER,CYAN,GREEN_DARK,YELLOW,ORANGE,RED_DARKER,RED_LIGHTER
    private static final double[] RED_DARKER = new double[]{0.375, 0, 0};
    private static final double[] RED_DARK = new double[]{0.5, 0, 0};
    private static final double[] RED = new double[]{1, 0, 0};
    private static final double[] RED_LIGHT = new double[]{1, 0.5, 0};
    private static final double[] RED_LIGHTER = new double[]{0.875, 0.375, 0.375};

    private static final double[] BLUE_DARKER = new double[]{0, 0, 0.375};
    private static final double[] BLUE_DARK = new double[]{0, 0, 0.5};
    private static final double[] BLUE = new double[]{0, 0, 1};
    private static final double[] BLUE_LIGHT = new double[]{0, 0.5, 1};
    private static final double[] BLUE_LIGHTER = new double[]{0, 0.375, 0.875};

    private static final double[] CYAN = new double[]{0, 1, 1};
    private static final double[] ORANGE = new double[]{1, 0.375, 0};
    private static final double[] YELLOW = new double[]{1, 1, 0};

    private static final double[] GREEN_DARK = new double[]{0, 0.5, 0};
    private static final double[] GREEN = new double[]{0, 1, 0};

    private static final double[] BLACK = new double[]{0, 0, 0};
    private static final double[] WHITE = new double[]{1, 1, 1};

    private String label;
    private Icon icon;

    private ScalarBarType(String label, Icon icon) {
        this.label = label;
        this.icon = icon;
    }

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

    public List<double[]> getColors(FieldItem item) {
        switch (label) {
            case "Blue to Red (HSV)":
                return createColors(item, Lists.newArrayList(BLUE, RED), HSV, false);
            case "Blue to Red (RGB)":
                return createColors(item, Lists.newArrayList(BLUE, RED), RGB, true);
            case "Blue to Red (Diverging)":
                return createColors(item, Lists.newArrayList(BLUE, RED), DIV, true);
            case "Blue to Red (Desaturated)":
                return createColors(item, Lists.newArrayList(BLUE_LIGHTER,BLUE_DARKER,CYAN,GREEN_DARK,YELLOW,ORANGE,RED_DARKER,RED_LIGHTER), LAB, true);
            case "Blue to Red (Jet)":
                return createJetColors(item);
            case "Cold and Hot":
                return createColdAndHotColors(item);
            case "Blue to Yellow (HSV)":
                return createColors(item, Lists.newArrayList(BLUE, YELLOW), HSV, true);
            case "Blue to Yellow (RGB)":
                return createColors(item, Lists.newArrayList(BLUE, YELLOW), RGB, true);
            case "Blue to Yellow (Diverging)":
                return createColors(item, Lists.newArrayList(BLUE, YELLOW), DIV, true);
            case "Black to White":
                return createColors(item, Lists.newArrayList(BLACK, WHITE), RGB, true);
            case "Black Body Radiation":
                return createColors(item, Lists.newArrayList(BLACK, RED, YELLOW, WHITE), RGB, true);
            case "Black Blue and White":
                return createColors(item, Lists.newArrayList(BLACK, BLUE_DARK, BLUE_LIGHT, WHITE), RGB, true);
            case "Black Orange and White":
                return createColors(item, Lists.newArrayList(BLACK, RED_DARK, RED_LIGHT, WHITE), RGB, true);
            default:
                return new ArrayList<>();
        }
    }

    private static List<double[]> createColors(FieldItem item, List<double[]> colorPivots, int colorSpace, boolean hsvWrap) {
        vtkDiscretizableColorTransferFunction function = new vtkDiscretizableColorTransferFunction();
        function.DiscretizeOn();
        switch (colorSpace) {
            case RGB:
                function.SetColorSpaceToRGB();
                break;
            case HSV:
                function.SetColorSpaceToHSV();
                break;
            case DIV:
                function.SetColorSpaceToDiverging();
                break;
            case LAB:
                function.SetColorSpaceToLab();
                break;
            default:
                function.SetColorSpaceToRGB();
                break;
        }

        function.SetHSVWrap(hsvWrap ? 1 : 0);

        function.SetNumberOfValues(item.getResolution());
        for (int i = 0; i < colorPivots.size(); i++) {
            double[] color = colorPivots.get(i);
            double index = item.getResolution() / (colorPivots.size() - 1) * i;
            function.AddRGBPoint(index, color[0], color[1], color[2]);
        }
        function.Build();

        List<double[]> colors = new LinkedList<>();
        for (int i = 0; i < item.getResolution(); i++) {
            double[] c = new double[3];
            function.GetColor(i, c);
            colors.add(c);
        }

        return item.isInverted() ? Lists.reverse(colors) : colors;
    }

    private static List<double[]> createColdAndHotColors(FieldItem item) {
        vtkDiscretizableColorTransferFunction function = new vtkDiscretizableColorTransferFunction();
        function.DiscretizeOn();
        function.SetColorSpaceToRGB();
        function.SetHSVWrap(1);

        double resolution_0 = 0.0;
        double resolution_1 = (item.getResolution() / 2) - (item.getResolution() / 20D);
        double resolution_2 = item.getResolution() / 2D;
        double resolution_3 = (item.getResolution() / 2) + (item.getResolution() / 20D);
        double resolution_4 = item.getResolution();

        function.SetNumberOfValues(item.getResolution());
        function.AddRGBPoint(resolution_0, CYAN[0],         CYAN[1],        CYAN[2]);
        function.AddRGBPoint(resolution_1, BLUE[0],         BLUE[1],        BLUE[2]);
        function.AddRGBPoint(resolution_2, BLUE_DARK[0],    BLUE_DARK[1],   BLUE_DARK[2]);
        function.AddRGBPoint(resolution_3, RED[0],          RED[1],         RED[2]);
        function.AddRGBPoint(resolution_4, YELLOW[0],       YELLOW[1],      YELLOW[2]);

        function.Build();

        List<double[]> colors = new LinkedList<>();
        for (int i = 0; i < item.getResolution(); i++) {
            double[] c = new double[3];
            function.GetColor(i, c);
            colors.add(c);
        }

        return item.isInverted() ? Lists.reverse(colors) : colors;
    }

    private static List<double[]> createJetColors(FieldItem item) {
        vtkDiscretizableColorTransferFunction function = new vtkDiscretizableColorTransferFunction();
        function.DiscretizeOn();
        function.SetColorSpaceToRGB();
        function.SetHSVWrap(1);

        double resolution_0 = 0.0;
        double resolution_1 = item.getResolution() * (1 / 10D);
        double resolution_2 = item.getResolution() * (4 / 10D);
        double resolution_3 = item.getResolution() * (5 / 10D);
        double resolution_4 = item.getResolution() * (6 / 10D);
        double resolution_5 = item.getResolution() * (9 / 10D);
        double resolution_6 = item.getResolution();

        function.SetNumberOfValues(item.getResolution());
        function.AddRGBPoint(resolution_0, BLUE_DARK[0],    BLUE_DARK[1],   BLUE_DARK[2]);
        function.AddRGBPoint(resolution_1, BLUE[0],         BLUE[1],        BLUE[2]);
        function.AddRGBPoint(resolution_2, CYAN[0],         CYAN[1],        CYAN[2]);
        function.AddRGBPoint(resolution_3, GREEN[0],        GREEN[1],       GREEN[2]);
        function.AddRGBPoint(resolution_4, YELLOW[0],       YELLOW[1],      YELLOW[2]);
        function.AddRGBPoint(resolution_5, RED[0],          RED[1],         RED[2]);
        function.AddRGBPoint(resolution_6, RED_DARK[0],     RED_DARK[1],    RED_DARK[2]);

        function.Build();

        List<double[]> colors = new LinkedList<>();
        for (int i = 0; i < item.getResolution(); i++) {
            double[] c = new double[3];
            function.GetColor(i, c);
            colors.add(c);
        }

        return item.isInverted() ? Lists.reverse(colors) : colors;
    }

    /*
     * Utils
     */

    public String getLabel() {
        return label;
    }

    public Icon getIcon() {
        return icon;
    }

}
