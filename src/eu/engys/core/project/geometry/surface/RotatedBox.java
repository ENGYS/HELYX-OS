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
package eu.engys.core.project.geometry.surface;

import java.util.Arrays;

import javax.vecmath.Vector2d;

import org.apache.commons.lang.ArrayUtils;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import vtk.vtkCellArray;
import vtk.vtkIdList;
import vtk.vtkPoints;
import vtk.vtkPolyData;

public class RotatedBox extends BaseSurface {

    public static final String ORIGIN_KEY = "origin";
    public static final String I_KEY = "i";
    public static final String J_KEY = "j";
    public static final String K_KEY = "k";
    
    public static final double[] DEFAULT_ORIGIN = { 0, 0, 0 };
    public static final double[] DEFAULT_I = { 1, 0, 0 };
    public static final double[] DEFAULT_J = { 0, 1, 0 };
    public static final double[] DEFAULT_K = { 0, 0, 1 };
    
    private double[] origin = DEFAULT_ORIGIN;
    private double[] i = DEFAULT_I;
    private double[] j = DEFAULT_J;
    private double[] k = DEFAULT_K;
    
	/**
	 * @deprecated Use GeometryFactory!!
	 */
	@Deprecated
	public RotatedBox(String name) {
		super(name);
	}

	@Override
	public Type getType() {
		return Type.RBOX;
	}

    public double[] getOrigin() {
        return origin;
    }
    public void setOrigin(double[] origin) {
        firePropertyChange(ORIGIN_KEY, this.origin, this.origin = origin);
    }
	public void setOrigin(double d1, double d2, double d3) {
	    setOrigin(new double[]{d1,d2,d3});
	}

    public double[] getI() {
        return i;
    }
    public void setI(double[] i) {
        firePropertyChange(I_KEY, this.i, this.i = i);
    }
    public void setI(double d1, double d2, double d3) {
        setI(new double[]{d1,d2,d3});
    }

    public double[] getJ() {
        return j;
    }
    public void setJ(double[] j) {
        firePropertyChange(J_KEY, this.j, this.j = j);
    }
    public void setJ(double d1, double d2, double d3) {
        setJ(new double[]{d1,d2,d3});
    }

    public double[] getK() {
        return k;
    }
    public void setK(double[] k) {
        firePropertyChange(K_KEY, this.k, this.k = k);
    }
    public void setK(double d1, double d2, double d3) {
        setK(new double[]{d1,d2,d3});
    }
	
	@Override
	public Surface cloneSurface() {
	    RotatedBox rbox = new RotatedBox(name);
        rbox.origin = ArrayUtils.clone(origin);
        rbox.i = ArrayUtils.clone(i);
        rbox.j = ArrayUtils.clone(j);
        rbox.k = ArrayUtils.clone(k);
	    cloneSurface(rbox);
	    return rbox;
	}
	
	@Override
	public void copyFrom(Surface delegate, boolean changeGeometry, boolean changeSurface, boolean changeVolume, boolean changeLayer, boolean changeZone) {
	    if (delegate instanceof RotatedBox) {
	        RotatedBox rbox = (RotatedBox) delegate;
	        if (changeGeometry) {
	            setOrigin(rbox.getOrigin());
	            setI(rbox.getI());
	            setJ(rbox.getJ());
	            setK(rbox.getK());
	        }
	        super.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
	    }
	}
	
	@Override
	public vtkPolyData getDataSet() {
	    double[] origin = getOrigin();
	    double[] i = getI();
	    double[] j = getJ();
	    double[] k = getK();

	    Vector2d v_i = new Vector2d(i);
	    Vector2d v_j = new Vector2d(j);
	    Vector2d v_k = new Vector2d(k);
	    
	    double d_i = v_i.length();
	    double d_j = v_j.length();
	    double d_k = v_k.length();
	    
        double[][] boxPoints = new double[8][3];
        boxPoints[0] = sum(origin);
        boxPoints[1] = sum(origin, i);
        boxPoints[2] = sum(origin, i, j);
        boxPoints[3] = sum(origin, j);
        boxPoints[4] = sum(origin, k);
        boxPoints[5] = sum(origin, k, i);
        boxPoints[6] = sum(origin, k, i, j);
        boxPoints[7] = sum(origin, k, j);
        
        vtkPolyData dataSet = new vtkPolyData();
        vtkPoints points = new vtkPoints();
        vtkCellArray cells = new vtkCellArray();
        
        points.InsertPoint(0, boxPoints[0]);
        points.InsertPoint(1, boxPoints[1]);
        points.InsertPoint(2, boxPoints[2]);
        points.InsertPoint(3, boxPoints[3]);

        points.InsertPoint(4, boxPoints[4]);
        points.InsertPoint(5, boxPoints[5]);
        points.InsertPoint(6, boxPoints[6]);
        points.InsertPoint(7, boxPoints[7]);
        
        cells.InsertNextCell(cell(0,1,2,3));
        cells.InsertNextCell(cell(0,1,5,4));
        cells.InsertNextCell(cell(1,2,6,5));
        cells.InsertNextCell(cell(2,3,7,6));
        cells.InsertNextCell(cell(3,0,4,7));
        cells.InsertNextCell(cell(4,5,6,7));
         
        dataSet.SetPoints(points);
        dataSet.SetPolys(cells);
        
	    return dataSet;
	}

    protected vtkIdList cell(int... i) {
        vtkIdList cell = new vtkIdList();
        for (int j : i) {
            cell.InsertNextId(j);
        }
        
        return cell;
    }
	
	
//	public static void main(String[] args) {
//	    double[] origin = {0, 0, 0};
//        double[] i = {2, 0, 1};
//        double[] j = {0, 1, 0};
//        double[] k = {-0.5, 0, 0.5};
//        
//        Point3d o = new Point3d(origin);
//        Vector3d v_i = new Vector3d(i);
//        Vector3d v_j = new Vector3d(j);
//        Vector3d v_k = new Vector3d(k);
//        
//	    double[][] boxPoints = new double[8][3];
//	    boxPoints[0] = sum(origin);
//	    boxPoints[1] = sum(origin, i);
//	    boxPoints[2] = sum(origin, i, j);
//	    boxPoints[3] = sum(origin, j);
//	    boxPoints[4] = sum(origin, k);
//	    boxPoints[5] = sum(origin, k, i);
//	    boxPoints[6] = sum(origin, k, i, j);
//	    boxPoints[7] = sum(origin, k, j);
//	    
//	    for (int l = 0; l < boxPoints.length; l++) {
//	        System.out.println("RotatedBox.main() " + l + " = " + Arrays.toString(boxPoints[l]));
//        }
//    }
	
	private static double[] sum(double[] origin, double[]... d) {
	    double[] sum = origin.clone();
	    for (double[] a : d) {
	        for (int i = 0; i < a.length; i++) {
	            sum[i] += a[i];
            }
        }
        return sum;
    }

    @Override
	public Dictionary toGeometryDictionary() {
	    return new Dictionary(name);
	}
	
	@Override
	public void fromGeometryDictionary(Dictionary g) {
	}
	
	@Override
	public String toString() {
	    String string = super.toString();
	    
        return string + String.format("[ origin: %s, max: %s] ", Arrays.toString(origin), Arrays.toString(i), Arrays.toString(j), Arrays.toString(k));
	}
}
