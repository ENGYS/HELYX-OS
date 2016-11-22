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
package eu.engys.core.controller;

import static eu.engys.core.controller.AbstractController.CANCEL;
import static eu.engys.core.controller.AbstractController.KILL_SOLVER_LABEL;
import static eu.engys.core.controller.AbstractController.STOP_SOLVER_LABEL;

import javax.swing.JOptionPane;

import eu.engys.util.ui.UiUtil;

public class StopOrKillCommandOS implements Runnable {

    private Controller controller;

    public StopOrKillCommandOS(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        Object[] options = new Object[] { STOP_SOLVER_LABEL, KILL_SOLVER_LABEL, CANCEL };
        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), "Select an action to perform.", STOP_SOLVER_LABEL, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (option == JOptionPane.YES_OPTION) {
            try {
                controller.stopCase();
            } catch (Exception e) {
                // should never pass here
            }
        } else if (option == JOptionPane.NO_OPTION) {
            controller.kill();
        }
    }
}
