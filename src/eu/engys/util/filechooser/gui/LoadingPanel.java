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

package eu.engys.util.filechooser.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.util.ui.ExecUtil;

public class LoadingPanel extends JPanel {

    private static final String LOADING = "Loading...";
    private static final String LOADING_FULL = "Loading..............";
    private JLabel loadingLabel;
    private Timer timer;

    public LoadingPanel() {
        super(new BorderLayout());
        setName("chooser.loadingpanel");
        setOpaque(true);
        setBackground(Color.WHITE);
        layoutComponents();
    }

    private void layoutComponents() {
        loadingLabel = new JLabel(LOADING);
        loadingLabel.setForeground(Color.LIGHT_GRAY);
        loadingLabel.setFont(new Font("Monotype Corsiva", 1, 28));
        add(loadingLabel);
    }

    public void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateLabel();
            }

        }, 0, 800);
    }

    private void updateLabel() {
	    ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                String current = loadingLabel.getText();
                if (LOADING_FULL.equals(current)) {
                    loadingLabel.setText(LOADING);
                } else {
                    loadingLabel.setText(current + ".");
                }
            }
	    });
	}

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        loadingLabel.setText(LOADING);
    }

}
