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


package eu.engys.gui.tree;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import eu.engys.util.ui.TreeUtil;

public class TreeSelectionModel extends DefaultTreeSelectionModel {

	public TreeSelectionModel() {
		super();
	}
	
	@Override
	public void setSelectionPaths(TreePath[] selectionPath) {
		if ((selectionPath != null) && (selectionPath.length > 0)) {
			TreePath firstPathParent = selectionPath[0].getParentPath();

			if (firstPathParent == null) {
				TreePath[] paths = new TreePath[] {selectionPath[0]};
				super.setSelectionPaths(paths);
				return;
			}

			if (TreeUtil.areSiblings(selectionPath, firstPathParent)) {
					TreePath[] consistentPath = TreeUtil.getAConsistentSelection(selectionPath);
					if (consistentPath.length > 0) {
						super.setSelectionPaths(consistentPath);
					}
//				}
			}
		}		
	}
	
	@Override
    public void addSelectionPaths(TreePath[] selectionPath) {
        if (getSelectionPath() != null) {
            TreePath firstPathParent = getSelectionPath().getParentPath();
            if (TreeUtil.areSiblings(selectionPath, firstPathParent)) {
                Class<?> leadSelectionClass = ((DefaultMutableTreeNode) getSelectionPath().getLastPathComponent()).getUserObject().getClass();
                List<Object> selection = TreeUtil.toUserObjects(selectionPath);
				
                if (TreeUtil.isConsistent(selection.toArray(), leadSelectionClass)) {
                    super.addSelectionPaths(selectionPath);
                }
			}
		}
	}
}
