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

package eu.engys.core.controller;

import static eu.engys.core.controller.AbstractController.CANCEL;
import static eu.engys.core.controller.AbstractController.KILL_SOLVER;
import static eu.engys.core.controller.AbstractController.STOP_SOLVER;

import javax.swing.JOptionPane;

import eu.engys.core.controller.actions.TimeoutException;
import eu.engys.util.ui.UiUtil;

public class StopOrKillCommandOS implements Runnable {

    private Controller controller;

    public StopOrKillCommandOS(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        Object[] options = new Object[] { STOP_SOLVER, KILL_SOLVER, CANCEL };
        int option = JOptionPane.showOptionDialog(UiUtil.getActiveWindow(), "Select an action to perform.", STOP_SOLVER, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (option == JOptionPane.YES_OPTION) {
            try {
                controller.stopCase();
            } catch (TimeoutException e) {
                // should never pass here
            }
        } else if (option == JOptionPane.NO_OPTION) {
            controller.kill();
        }
    }
}
