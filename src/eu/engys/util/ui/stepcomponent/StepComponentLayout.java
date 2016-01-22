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


package eu.engys.util.ui.stepcomponent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;


public class StepComponentLayout implements LayoutManager, java.io.Serializable {
    
	/*
     * serialVersionUID
     */
    private static final long serialVersionUID = -7411804673224730901L;

    int hgap;
    int vgap;
    
    int rows;
    int cols;

    public StepComponentLayout(int drift) {
    	this.rows = 1;
    	this.cols = 0;
    	this.hgap = -2*drift+2;
    	this.vgap = 0;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
    	synchronized (parent.getTreeLock()) {
    		Insets insets = parent.getInsets();
    		int ncomponents = parent.getComponentCount();
    		int nrows = rows;
    		int ncols = cols;

    		if (nrows > 0) {
    			ncols = (ncomponents + nrows - 1) / nrows;
    		} else {
    			nrows = (ncomponents + ncols - 1) / ncols;
    		}
    		int w = 0;
    		int h = 0;
    		for (int i = 0 ; i < ncomponents ; i++) {
    			Component comp = parent.getComponent(i);
    			Dimension d = comp.getPreferredSize();
    			if (w < d.width) {
    				w = d.width;
    			}
    			if (h < d.height) {
    				h = d.height;
    			}
    		}
    		return new Dimension(insets.left + insets.right + ncols*w + (ncols)*hgap, 
    				insets.top + insets.bottom + nrows*h + (nrows-1)*vgap);
    	}
    }

    public Dimension minimumLayoutSize(Container parent) {
//    	synchronized (parent.getTreeLock()) {
//    		Insets insets = parent.getInsets();
//    		int ncomponents = parent.getComponentCount();
//    		int nrows = rows;
//    		int ncols = cols;
//
//    		if (nrows > 0) {
//    			ncols = (ncomponents + nrows - 1) / nrows;
//    		} else {
//    			nrows = (ncomponents + ncols - 1) / ncols;
//    		}
//    		int w = 0;
//    		int h = 0;
//    		for (int i = 0 ; i < ncomponents ; i++) {
//    			Component comp = parent.getComponent(i);
//    			Dimension d = comp.getMinimumSize();
//    			if (w < d.width) {
//    				w = d.width;
//    			}
//    			if (h < d.height) {
//    				h = d.height;
//    			}
//    		}
//    		return new Dimension(insets.left + insets.right + ncols*w + (ncols)*hgap, 
//    				insets.top + insets.bottom + nrows*h + (nrows-1)*vgap);
//    	}
    	return new Dimension();
    }

    public void layoutContainer(Container parent) {
    	synchronized (parent.getTreeLock()) {
    		Insets insets = parent.getInsets();
    		int ncomponents = parent.getComponentCount();
    		int nrows = rows;
    		int ncols = cols;
    		boolean ltr = parent.getComponentOrientation().isLeftToRight();

    		if (ncomponents == 0) {
    			return;
    		}
    		if (nrows > 0) {
    			ncols = (ncomponents + nrows - 1) / nrows;
    		} else {
    			nrows = (ncomponents + ncols - 1) / ncols;
    		}
    		
    		int w = parent.getWidth() - (insets.left + insets.right);
    		int h = parent.getHeight() - (insets.top + insets.bottom);
    		w = (w - (ncols) * hgap) / ncols;
    		h = (h - (nrows) * vgap) / nrows;

    		if (ltr) {
    			for (int c = 0, x = insets.left ; c < ncols ; c++, x += w + hgap) {
    				for (int r = 0, y = insets.top ; r < nrows ; r++, y += h + vgap) {
    					int i = r * ncols + c;
    					if (i < ncomponents) {
    						if (i == 0) {//first
    							parent.getComponent(i).setBounds(x, y, w+hgap, h);
    							x += hgap;
//    						} else if (i == ncomponents-1) {//last
//    							parent.getComponent(i).setBounds(x, y, w+hgap, h);
    						} else {
    							parent.getComponent(i).setBounds(x, y, w, h);
    						}
    					}
    				}
    			}
    		} else {
    			for (int c = 0, x = parent.getWidth() - insets.right - w; c < ncols ; c++, x -= w + hgap) {
    				for (int r = 0, y = insets.top ; r < nrows ; r++, y += h + vgap) {
    					int i = r * ncols + c;
    					if (i < ncomponents) {
    						parent.getComponent(i).setBounds(x, y, w, h);
    					}
    				}
    			}
    		}
    	}
    }
    
    /**
     * Returns the string representation of this grid layout's values.
     * @return     a string representation of this grid layout
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + 
	    			       ",rows=" + rows + ",cols=" + cols + "]";
    }
}
