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

package eu.engys.vtk;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import vtk.vtkLookupTable;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.ScalarBarType;
import eu.engys.gui.view3D.Actor;

public class VTKColors {

    public static final double[] WHITE = toVTK(Color.WHITE);
    public static final double[] BLACK = toVTK(Color.BLACK);
    public static final double[] RED = toVTK(Color.RED);
    public static final double[] GREEN = toVTK(Color.GREEN);
    public static final double[] BLUE = toVTK(Color.BLUE);
    public static final double[] ORANGE = toVTK(Color.ORANGE);
    public static final double[] PINK = toVTK(Color.PINK);
    public static final double[] CYAN = toVTK(Color.CYAN);
    public static final double[] MAGENTA = toVTK(Color.MAGENTA);
    public static final double[] YELLOW = toVTK(Color.YELLOW);

    public static final double AMBIENT = 0.0;
    public static final double DIFFUSE = 0.9;
    public static final double SPECULAR = 0.1;
    public static final double SPECULAR_POWER = 0.8;

    public static final double SELECTION_AMBIENT = 0.0;
    public static final double SELECTION_DIFFUSE = 1.0;
    public static final double SELECTION_SPECULAR = 0.0;
    public static final double SELECTION_SPECULAR_POWER = 0;

    // public static final double[] SELECTION_COLOR = new double[] {1, 0.5, 0};
    public static final double[] SELECTION_COLOR = new double[] { 0.8, 0.1, 0.1 };
    public static final double[] DESELECTION_COLOR = WHITE;

    static class ScalarsColor {
        private List<Actor> actors = new ArrayList<>();
        private FieldItem field;

        ScalarsColor(FieldItem field) {
            this.field = field;
        }

        public ScalarsColor to(VTKActors actors) {
            this.actors.addAll(actors.getActors());

            if (field.isAutomaticRange()) {
                new VTKRangeCalculator(field).calculateRange_Automatically_For(actors);
            } else {
                // use existing range
            }

            return this;
        }

        public void apply() {
            vtkLookupTable lut = new vtkLookupTable();
            applyTypeToLookupTable(field, lut);

            for (Actor actor : actors) {
                actor.setScalarColors(lut, field);
            }
        }
    }

    public static void applyTypeToLookupTable(FieldItem fieldItem, vtkLookupTable table) {
        // Vector Mode
        if (fieldItem.getComponent() <= 0) {
            table.SetVectorModeToMagnitude();
        } else {
            table.SetVectorModeToComponent();
            table.SetVectorComponent(fieldItem.getComponent() - 1);
        }

        // Range
        table.SetRange(fieldItem.getRange());

        // Colors
        ScalarBarType scalarBarType = fieldItem.getScalarBarType();
        List<double[]> colors = scalarBarType.getColors(fieldItem.getResolution());
        if (scalarBarType.equals(ScalarBarType.RED_TO_BLUE_RAINBOW) || scalarBarType.equals(ScalarBarType.BLUE_TO_RED_RAINBOW)) {
            table.SetNumberOfTableValues(fieldItem.getResolution());
            table.SetHueRange(scalarBarType.getColors(-1).get(0));
            table.ForceBuild();
        } else {
            table.SetNumberOfTableValues(colors.size());
            for (int i = 0; i < colors.size(); i++) {
                double[] values = colors.get(i);
                double red = values[0];
                double green = values[1];
                double blue = values[2];
                table.SetTableValue(i, red, green, blue, 1);
            }
            table.Build();
        }
    }

    static class IndexedColor {
        private List<Actor> actors = new ArrayList<>();

        public IndexedColor to(VTKActors actors) {
            this.actors.addAll(actors.getActors());
            return this;
        }

        public void apply() {
            vtkLookupTable lut = new vtkLookupTable();
            lut.SetTableRange(0, actors.size() - 1);
            lut.ForceBuild();

            int colorIndex = 0;
            for (Actor actor : actors) {
                double[] color = lut.GetColor(colorIndex++);
                actor.setSolidColor(color, 1);
            }
            lut.Delete();
        }
    }

    static class SolidColor {
        private double[] color;
        private List<Actor> actors = new ArrayList<>();

        public SolidColor(double[] color) {
            this.color = color;
        }

        public SolidColor to(VTKActors actors) {
            this.actors.addAll(actors.getActors());
            return this;
        }

        public void apply() {
            for (Actor actor : actors) {
                actor.setSolidColor(color, 1);
            }
        }
    }

    public static SolidColor solidColor(double[] color) {
        return new SolidColor(color);
    }

    public static IndexedColor indexedColor() {
        return new IndexedColor();
    }

    public static ScalarsColor scalarsColor(FieldItem fieldItem) {
        return new ScalarsColor(fieldItem);
    }

    public static double[] toVTK(Color color) {
        return new double[] { color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0 };
    }

    public static Color toSwing(double[] color) {
        return new Color((float) color[0], (float) color[1], (float) color[2]);
    }

    public static Color inverse(double[] color) {
        return new Color(1 - (float) color[0], 1 - (float) color[1], 1 - (float) color[2]);
    }

}
