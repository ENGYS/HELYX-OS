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

import static eu.engys.util.ui.UiUtil.createButtonBarToggleButton;
import static eu.engys.util.ui.UiUtil.createToolBarButton;
import static eu.engys.util.ui.UiUtil.createToolBarToggleButton;

import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JToolBar;

import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.view3D.Context;
import eu.engys.gui.view3D.Controller3D;
import eu.engys.util.plaf.ILookAndFeel;

public class VTK3DActionsToolBar extends JToolBar {

    static final String _3D_LOAD_MESH = "3d.load.mesh";
    
    static final String _3D_AXIS_XPOS = "3d.axis.xpos";
    static final String _3D_AXIS_XNEG = "3d.axis.xneg";
    static final String _3D_AXIS_YPOS = "3d.axis.ypos";
    static final String _3D_AXIS_YNEG = "3d.axis.yneg";
    static final String _3D_AXIS_ZPOS = "3d.axis.zpos";
    static final String _3D_AXIS_ZNEG = "3d.axis.zneg";
    
    static final String _3D_ZOOM_RESET = "3d.zoom.reset";
    static final String _3D_ZOOM_TOBOX = "3d.zoom.tobox";
    static final String _3D_ZOOM_OUT = "3d.zoom.out";
    static final String _3D_ZOOM_IN = "3d.zoom.in";
    
    static final String _3D_VIEW_PROJECTIONS = "3d.view.projections";
    static final String _3D_VIEW_OUTLINE = "3d.view.outline";
    static final String _3D_VIEW_PROFILE = "3d.view.profile";
    static final String _3D_VIEW_EDGES = "3d.view.edges";
    static final String _3D_VIEW_SURFACE = "3d.view.surface";
    static final String _3D_VIEW_WIREFRAME = "3d.view.wireframe";
    private Model model;

    public VTK3DActionsToolBar(Model model, ILookAndFeel laf) {
        super(JToolBar.VERTICAL);
        this.model = model;
        putClientProperty("Synthetica.toolBar.buttons.paintBorder", Boolean.TRUE);
        putClientProperty("Synthetica.opaque", Boolean.FALSE);
        setFloatable(false);
        setRollover(true);
        setOpaque(false);
        layoutComponents();
    }

    private void layoutComponents() {
        add(createToolBarButton(ActionManager.getInstance().get(_3D_ZOOM_IN)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_ZOOM_OUT)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_ZOOM_TOBOX)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_ZOOM_RESET)));
        addSeparator();
        add(createToolBarButton(ActionManager.getInstance().get(_3D_AXIS_XPOS)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_AXIS_XNEG)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_AXIS_YPOS)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_AXIS_YNEG)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_AXIS_ZPOS)));
        add(createToolBarButton(ActionManager.getInstance().get(_3D_AXIS_ZNEG)));
        addSeparator();
        ButtonGroup viewGroup = new ButtonGroup();
        add(createButtonBarToggleButton(ActionManager.getInstance().get(_3D_VIEW_WIREFRAME), viewGroup));
        add(createButtonBarToggleButton(ActionManager.getInstance().get(_3D_VIEW_SURFACE), viewGroup));
        add(createButtonBarToggleButton(ActionManager.getInstance().get(_3D_VIEW_EDGES), viewGroup));
        add(createButtonBarToggleButton(ActionManager.getInstance().get(_3D_VIEW_PROFILE), viewGroup));
        add(createButtonBarToggleButton(ActionManager.getInstance().get(_3D_VIEW_OUTLINE), viewGroup));
        add(createToolBarToggleButton(ActionManager.getInstance().get(_3D_VIEW_PROJECTIONS)));
    }

    public void update(List<Controller3D> controllers) {
    	Context context = getaContext(controllers);
        if (context != null) {
            ActionManager.getInstance().get(_3D_VIEW_PROJECTIONS).setEnabled(true);
            ActionManager.getInstance().get(_3D_VIEW_WIREFRAME).setEnabled(true);
            ActionManager.getInstance().get(_3D_VIEW_SURFACE).setEnabled(true);
            ActionManager.getInstance().get(_3D_VIEW_EDGES).setEnabled(true);
            ActionManager.getInstance().get(_3D_VIEW_PROFILE).setEnabled(true);
            ActionManager.getInstance().get(_3D_VIEW_OUTLINE).setEnabled(true);

            switch (context.getRepresentation()) {
	            case WIREFRAME:
	            	ActionManager.getInstance().get(_3D_VIEW_WIREFRAME).setSelected(true);
	            	break;
            	case SURFACE:
	            	ActionManager.getInstance().get(_3D_VIEW_SURFACE).setSelected(true);
	                break;
            	case SURFACE_WITH_EDGES:
	            	ActionManager.getInstance().get(_3D_VIEW_EDGES).setSelected(true);
	                break;
            	case OUTLINE:
            		ActionManager.getInstance().get(_3D_VIEW_OUTLINE).setSelected(true);
            		break;
            	case PROFILE:
            	    ActionManager.getInstance().get(_3D_VIEW_PROFILE).setSelected(true);
            	    break;
            }
//            if (context instanceof MeshContext) {
//                MeshContext mc = (MeshContext) context;
//                ActionManager.getInstance().get(_3D_LOAD_MESH).setEnabled(!model.getPatches().isEmpty() && mc.isEmpty());
//            }
        } else {
        	ActionManager.getInstance().get(_3D_VIEW_WIREFRAME).setEnabled(false);
            ActionManager.getInstance().get(_3D_VIEW_SURFACE).setEnabled(false);
            ActionManager.getInstance().get(_3D_VIEW_EDGES).setEnabled(false);
            ActionManager.getInstance().get(_3D_VIEW_PROFILE).setEnabled(false);
            ActionManager.getInstance().get(_3D_VIEW_OUTLINE).setEnabled(false);
            ActionManager.getInstance().get(_3D_VIEW_PROJECTIONS).setEnabled(false);
        }
        
        MeshContext mContext = (MeshContext) getMeshContext(controllers);
        if(mContext != null){
            ActionManager.getInstance().get(_3D_LOAD_MESH).setEnabled(!model.getPatches().isEmpty() && mContext.isEmpty());
        }
    }

	private Context getaContext(List<Controller3D> controllers) {
		for (Controller3D c : controllers) {
			Context context = c.getCurrentContext();
			if (context != null) {
				return context; 
			}
		}
		return null;
	}

	private Context getMeshContext(List<Controller3D> controllers) {
	    for (Controller3D c : controllers) {
	        Context context = c.getCurrentContext();
	        if (context != null && context instanceof MeshContext) {
	            return context; 
	        }
	    }
	    return null;
	}

	public void clear() {
		ActionManager.getInstance().get(_3D_VIEW_WIREFRAME).setSelected(false);
		ActionManager.getInstance().get(_3D_VIEW_SURFACE).setSelected(false);
        ActionManager.getInstance().get(_3D_VIEW_EDGES).setSelected(false);
        ActionManager.getInstance().get(_3D_VIEW_PROFILE).setSelected(false);
        ActionManager.getInstance().get(_3D_VIEW_OUTLINE).setSelected(false);
        ActionManager.getInstance().get(_3D_VIEW_PROJECTIONS).setSelected(false);

        ActionManager.getInstance().get(_3D_VIEW_WIREFRAME).setEnabled(false);
        ActionManager.getInstance().get(_3D_VIEW_SURFACE).setEnabled(false);
        ActionManager.getInstance().get(_3D_VIEW_EDGES).setEnabled(false);
        ActionManager.getInstance().get(_3D_VIEW_PROFILE).setEnabled(false);
        ActionManager.getInstance().get(_3D_VIEW_OUTLINE).setEnabled(false);
        ActionManager.getInstance().get(_3D_VIEW_PROJECTIONS).setEnabled(false);
	}
}
