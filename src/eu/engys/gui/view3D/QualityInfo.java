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

package eu.engys.gui.view3D;

import static eu.engys.gui.view3D.QualityInfo.Test.LESS_THAN;
import static eu.engys.gui.view3D.QualityInfo.Test.MORE_THAN;

import java.awt.Color;

import eu.engys.util.bean.AbstractBean;


public class QualityInfo extends AbstractBean {

//    metrics[0] = "nonOrthogonality";
//    metrics[1] = "pyramids";
//    metrics[2] = "skewness" ;
//    metrics[3] = "weights";
//    metrics[4] = "volumeRatio";
//    metrics[5] = "determinant";
    public enum Test {
        MORE_THAN, LESS_THAN;
    }
    
    public enum QualityMeasure {
        NON_ORTHOGONALITY("nonOrthogonality", MORE_THAN), 
        PYRAMIDS("pyramids", MORE_THAN), 
        SKEWNESS("skewness", LESS_THAN), 
        WEIGHTS("weights", MORE_THAN), 
        VOLUME_RATIO("volumeRatio", MORE_THAN), 
        DETERMINANT("determinant", MORE_THAN);

        private String fieldName;
        private Test test;
        
        private QualityMeasure(String fieldName, Test test) {
            this.fieldName = fieldName;
            this.test = test;
        }
        
        public String getFieldName() {
            return fieldName;
        }
        
        public Test getTest() {
            return test;
        }
    }

    private QualityInfo.QualityMeasure measure;
    private double threshold;
    private Color color;
    
    public QualityInfo.QualityMeasure getMeasure() {
        return measure;
    }
    public void setMeasure(QualityInfo.QualityMeasure measure) {
        this.measure = measure;
    }
    public double getThreshold() {
        return threshold;
    }
    public void setThreshold(double threshold) {
        firePropertyChange("threshold", this.threshold, this.threshold = threshold);
    }
    
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return "fieldName: " + getMeasure().getFieldName() +  ", test: " + getMeasure().getTest() + ", threshold: " + getThreshold();
    }
    
}
