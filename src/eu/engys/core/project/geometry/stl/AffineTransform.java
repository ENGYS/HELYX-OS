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


package eu.engys.core.project.geometry.stl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import vtk.vtkTransform;
import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;


public class AffineTransform {
	
    public static final String YAW_PITCH_ROLL_KEY = "yawPitchRoll";
    public static final String ROLL_PITCH_YAW_KEY = "rollPitchYaw";
    public static final String TRANSFORMS_KEY = "transforms";
    public static final String TRANSLATE_VEC_KEY = "translateVec";
    public static final String TRANSLATE_KEY = "translate";
    public static final String N1N2_KEY = "n1n2";
    public static final String ROTATE_KEY = "rotate";
    public static final String ABOUT_POINT_KEY = "aboutPoint";
    public static final String SCALE_VEC_KEY = "scaleVec";
    public static final String SCALE_KEY = "scale";
    public static final String TYPE_KEY = "type";
    
    private double originX = 0;
    private double originY = 0;
    private double originZ = 0;

    private double scaleX = 1;
    private double scaleY = 1;
    private double scaleZ = 1;
	
    private double rotX = 0;
    private double rotY = 0;
    private double rotZ = 0;
	
    private double posX = 0;
    private double posY = 0;
    private double posZ = 0;
    
    public AffineTransform() {}
    
    public AffineTransform(AffineTransform t) {
        setOrigin(t.getOrigin());
        setScale(t.getScale());
        setTranslate(t.getTranslation());
        setRotation(t.getRotation());
    }
    
    public void setOrigin(double[] origin) {
        originX = origin[0];
        originY = origin[1];
        originZ = origin[2];
    }
    
    public void setScale(double[] scale) {
        scaleX = scale[0];
        scaleY = scale[1];
        scaleZ = scale[2];
    }
	
	public void setTranslate(double[] translate) {
		posX = translate[0];
		posY = translate[1];
		posZ = translate[2];
	}
	
	public void setRotation(double[] rotation) {
		rotX = rotation[0];
		rotY = rotation[1];
		rotZ = rotation[2];
	}
	
	@Override
	public String toString() {
		return String.format("Scale: %f %f %f, Rot: %f %f %f, Pos: %f %f %f", scaleX, scaleY, scaleZ, rotX, rotY, rotZ, posX, posY, posZ);
	}

	public boolean isIdentity() {
		return scaleX==1 && scaleY==1 && scaleZ==1 && rotX==0 && rotY==0 && rotZ==0 && posX==0 && posY==0 && posZ==0;
	}
	
	public double[] getTranslation() {
	    return new double[] {posX, posY, posZ};
	}
	public double[] getScale() {
	    return new double[] {scaleX, scaleY, scaleZ};
	}
	public double[] getRotation() {
	    return new double[] {rotX, rotY, rotZ};
	}
	public double getRotationX() {
	    return rotX;
	}
	public double getRotationY() {
	    return rotY;
	}
	public double getRotationZ() {
	    return rotZ;
	}
	public double[] getOrigin() {
	    return new double[] {originX, originY, originZ};
	}
	
	public static AffineTransform getTranslation(double dx, double dy, double dz) {
		AffineTransform t = new AffineTransform();
		t.posX = dx;
		t.posY = dy;
		t.posZ = dz;
		
		return t;
	}
	
	public static AffineTransform getScale(double dx, double dy, double dz) {
		AffineTransform t = new AffineTransform();
		t.scaleX = dx;
		t.scaleY = dy;
		t.scaleZ = dz;
		
		return t;
	}

	public static AffineTransform getRotateX(double dx) {
		AffineTransform t = new AffineTransform();
		t.rotX = dx;
		return t;
	}
	public static AffineTransform getRotateY(double dx) {
		AffineTransform t = new AffineTransform();
		t.rotY = dx;
		return t;
	}
	public static AffineTransform getRotateZ(double dx) {
		AffineTransform t = new AffineTransform();
		t.rotZ = dx;
		return t;
	}

    public static AffineTransform fromVTK(vtkTransform t) {
        AffineTransform transform = new AffineTransform();
        transform.setOrigin(new double[]{0, 0, 0});
        transform.setRotation(t.GetOrientation());
        transform.setScale(t.GetScale());
        transform.setTranslate(t.GetPosition());
        
        return transform;
    }

    public vtkTransform toVTK(vtkTransform current) {
        vtkTransform transform = new vtkTransform();
        transform.PostMultiply();
        transform.SetInput(current);
        transform.Scale(scaleX, scaleY, scaleZ);
        transform.Translate(-originX, -originY, -originZ);
        transform.RotateY(rotY);
        transform.RotateX(rotX);
        transform.RotateZ(rotZ);
        transform.Translate(originX, originY, originZ);
        transform.Translate(posX, posY, posZ);
        
        return transform;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AffineTransform) {
            AffineTransform t = (AffineTransform) obj;
            return posX == t.posX && posY == t.posY && posZ == t.posZ && scaleX == t.scaleX && scaleY == t.scaleY && scaleZ == t.scaleZ && rotX == t.rotX && rotY == t.rotY && rotZ == t.rotZ;
        }
        return super.equals(obj);
    }
    
    public static AffineTransform fromGeometryDictionary(Dictionary g) {
        AffineTransform t = new AffineTransform();
        if (g.isList(TRANSFORMS_KEY)) {
            ListField transforms = g.getList(TRANSFORMS_KEY);
            for (DefaultElement el : transforms.getListElements()) {
                if (el instanceof Dictionary) {
                    Dictionary d = (Dictionary) el;
                    if (d.found(TYPE_KEY)) {
                        String type = d.lookup(TYPE_KEY);
                        switch (type) {
                        case TRANSLATE_KEY:
                            double[] translate = d.lookupDoubleArray(TRANSLATE_VEC_KEY);
                            t.setTranslate(translate);
                            break;
                        case ROTATE_KEY:
                            if (d.found(N1N2_KEY)) {
                                double[][] n1n2 = d.lookupDoubleMatrix(N1N2_KEY);
                                double[] n1 = n1n2[0];
                                double[] n2 = n1n2[1];
                                t.setRotation(new double[] {getRotX(n1, n2), getRotY(n1, n2), getRotZ(n1, n2)});
                            } else if (d.found(ROLL_PITCH_YAW_KEY)) {
                                double[] rollPitchYaw = d.lookupDoubleArray(ROLL_PITCH_YAW_KEY);
                                double roll  = rollPitchYaw[0];
                                double pitch = rollPitchYaw[1];
                                double yaw   = rollPitchYaw[2];
                                t.setRotation(new double[] { roll, pitch, yaw});
                            } else if (d.found(YAW_PITCH_ROLL_KEY)) {
                                double[] yawPitchRoll = d.lookupDoubleArray(YAW_PITCH_ROLL_KEY);
                                double yaw   = yawPitchRoll[0];
                                double pitch = yawPitchRoll[1];
                                double roll  = yawPitchRoll[2];
                                t.setRotation(new double[] { roll, pitch, yaw});
                            }
                            break;
                        case SCALE_KEY:
                            double[] value = d.lookupDoubleArray(SCALE_VEC_KEY);
                            t.setScale(value);
                            break;

                        default:
                            break;
                        }
                    }
                }
            }
        }
        return t;
    }
    
    public ListField toDictionary() {
        ListField transforms = new ListField(TRANSFORMS_KEY);
        if (scaleX != 1 || scaleY != 1 || scaleZ != 1) {
            Dictionary d = new Dictionary("");
            d.add(TYPE_KEY, SCALE_KEY);
            d.add(SCALE_VEC_KEY, format(getScale()));
            d.add(ABOUT_POINT_KEY, format(getOrigin()));
            
            transforms.add(d);
        }
        if (rotX != 0 || rotY != 0 || rotZ != 0) {
            Dictionary d = new Dictionary("");
            d.add(TYPE_KEY, ROTATE_KEY);
            d.add(ROLL_PITCH_YAW_KEY, format(getRotation()));
            d.add(ABOUT_POINT_KEY, format(getOrigin()));
            
            transforms.add(d);
        }
        if (posX != 0 || posY != 0 || posZ != 0) {
            Dictionary d = new Dictionary("");
            d.add(TYPE_KEY, TRANSLATE_KEY);
            d.add(TRANSLATE_VEC_KEY, format(getTranslation()));
            
            transforms.add(d);
        }
        return transforms;
    }

    private static final DecimalFormat formatter = new DecimalFormat("0.0##", new DecimalFormatSymbols(Locale.US));

    private String[] format(double[] d) {
        return new String[] { formatter.format(d[0]), formatter.format(d[1]), formatter.format(d[2])} ;
    }

    private String getN1N2() {
        Matrix3d R = getRotationMatrix(rotX, rotY, rotZ);
        
        Vector3d axis1 = new Vector3d(1, 0, 0);
        Vector3d axis2 = new Vector3d();
        R.transform(axis1, axis2);
        
        StringBuffer sb = new StringBuffer("(");
        sb.append("(");
        sb.append(formatter.format(axis1.x) + " " + formatter.format(axis1.y) + " " + formatter.format(axis1.z) + " ");
        sb.append(")");
        sb.append("(");
        sb.append(formatter.format(axis2.x) + " " + formatter.format(axis2.y) + " " + formatter.format(axis2.z) + " ");
        sb.append(")");
        sb.append(")");
        
        return sb.toString();
    }
    
    public Matrix3d getRotationMatrix(double rotX, double rotY, double rotZ) {
        
        Matrix3d X = new Matrix3d();
        X.rotX(Math.toRadians(rotX));

        Matrix3d Y = new Matrix3d();
        Y.rotY(Math.toRadians(rotY));

        Matrix3d Z = new Matrix3d();
        Z.rotZ(Math.toRadians(rotZ));
        
        Matrix3d R = new Matrix3d();
        R.mul(Y, X);
        R.mul(Z, R);
        
        return R;
    }
    
    public static double getRotX(double[] n1, double[] n2) {
        Vector3d v1 = new Vector3d(n1);
        Vector3d v2 = new Vector3d(n2);
        v1.normalize();
        v2.normalize();
        Vector3d v1_yz = new Vector3d(0, v1.y, v1.z);
        Vector3d v2_yz = new Vector3d(0, v2.y, v2.z);
        
        return Math.toDegrees(v1_yz.angle(v2_yz));
    }
    
    public static double getRotY(double[] n1, double[] n2) {
        Vector3d v1 = new Vector3d(n1);
        Vector3d v2 = new Vector3d(n2);
        v1.normalize();
        v2.normalize();
        Vector3d v1_xz = new Vector3d(v1.x, 0, v1.z);
        Vector3d v2_xz = new Vector3d(v2.x, 0, v2.z);
        
        return Math.toDegrees(v1_xz.angle(v2_xz));
    }

    public static double getRotZ(double[] n1, double[] n2) {
        Vector3d v1 = new Vector3d(n1);
        Vector3d v2 = new Vector3d(n2);
        v1.normalize();
        v2.normalize();
        Vector3d v1_xy = new Vector3d(v1.x, v1.y, 0);
        Vector3d v2_xy = new Vector3d(v2.x, v2.y, 0);
        
        return Math.toDegrees(v1_xy.angle(v2_xy));
    }
}
