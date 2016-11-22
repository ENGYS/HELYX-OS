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
package eu.engys.util.ui.treetable.tree;

import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

public class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {

    private boolean updatingListSelectionModel;
    private JTree tree;

    public ListToTreeSelectionModelWrapper(JTree tree) {
        super();
        this.tree = tree;
        getListSelectionModel().addListSelectionListener(createListSelectionListener());
    }

    public ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }

    public void resetRowSelection() {
        if (!updatingListSelectionModel) {
            updatingListSelectionModel = true;
            try {
                super.resetRowSelection();
            } finally {
                updatingListSelectionModel = false;
            }
        }
    }

    protected ListSelectionListener createListSelectionListener() {
        return new ListSelectionHandler();
    }

    protected void updateSelectedPathsFromSelectedRows() {
        if (!updatingListSelectionModel) {
            updatingListSelectionModel = true;
            try {
                int min = listSelectionModel.getMinSelectionIndex();
                int max = listSelectionModel.getMaxSelectionIndex();

                clearSelection();
                if (min != -1 && max != -1) {
                    for (int counter = min; counter <= max; counter++) {
                        if (listSelectionModel.isSelectedIndex(counter)) {
                            TreePath selPath = tree.getPathForRow(counter);

                            if (selPath != null) {
                                addSelectionPath(selPath);
                            }
                        }
                    }
                }
            } finally {
                updatingListSelectionModel = false;
            }
        }
    }

    class ListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            updateSelectedPathsFromSelectedRows();
        }
    }
}
