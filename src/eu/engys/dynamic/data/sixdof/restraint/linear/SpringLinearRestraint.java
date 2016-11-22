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
package eu.engys.dynamic.data.sixdof.restraint.linear;

public class SpringLinearRestraint extends LinearRestraint {

    public static final String STIFFNESS = "stiffness";
    public static final String DAMPING = "damping";
    public static final String REF_ATTACHMENT_PT = "refAttachmentPt";
    public static final String REST_LENGTH = "restLength";
    public static final String ANCHOR = "anchor";

    private double[] anchor = new double[] { 0, 0, 0 };
    private double[] refAttachmentPt = new double[] { 0, 0, 0 };
    private double stiffness = 0.0;
    private double damping = 0.0;
    private double restLength = 0.0;

    public SpringLinearRestraint() {
    }
    
    @Override
    public LinearRestraint copy() {
        SpringLinearRestraint copy = new SpringLinearRestraint();
        copy.stiffness = this.stiffness;
        copy.damping = this.damping;
        copy.refAttachmentPt[0] = this.refAttachmentPt[0];
        copy.refAttachmentPt[1] = this.refAttachmentPt[1];
        copy.refAttachmentPt[2] = this.refAttachmentPt[2];
        copy.restLength = this.restLength;
        copy.anchor[0] = this.anchor[0];
        copy.anchor[1] = this.anchor[1];
        copy.anchor[2] = this.anchor[2];
        return copy;
    }

    @Override
    public LinearRestraintType getType() {
        return LinearRestraintType.SPRING;
    }

    public double getStiffness() {
        return stiffness;
    }

    public void setStiffness(double stiffness) {
        firePropertyChange(STIFFNESS, this.stiffness, this.stiffness = stiffness);
    }

    public double getDamping() {
        return damping;
    }

    public void setDamping(double damping) {
        firePropertyChange(DAMPING, this.damping, this.damping = damping);
    }

    public double[] getRefAttachmentPt() {
        return refAttachmentPt;
    }

    public void setRefAttachmentPt(double[] refAttachmentPt) {
        firePropertyChange(REF_ATTACHMENT_PT, this.refAttachmentPt, this.refAttachmentPt = refAttachmentPt);
    }

    public double getRestLength() {
        return restLength;
    }

    public void setRestLength(double restLength) {
        firePropertyChange(REST_LENGTH, this.restLength, this.restLength = restLength);
    }

    public double[] getAnchor() {
        return anchor;
    }

    public void setAnchor(double[] anchor) {
        firePropertyChange(ANCHOR, this.anchor, this.anchor = anchor);
    }

}
