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


package eu.engys.suite;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Named;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.inject.Inject;

import eu.engys.core.Arguments;
import eu.engys.launcher.ApplicationLauncher;
import eu.engys.util.ui.UiUtil;

public class Suite {

    private String product;
    private ImageIcon icon;
    private Set<ApplicationLauncher> applications;
    
    @Inject
    public Suite(@Named("Product") String product, @Named("Product") ImageIcon icon, Set<ApplicationLauncher> applications) {
        this.product = product;
        this.icon = icon;
        this.applications = applications;
    }

    protected Set<ApplicationLauncher> getApplications() {
        return applications;
    }
    
    public void batch() {
//		if (applications.size() == 1) {
			try {
				ApplicationLauncher application = applications.iterator().next();
				application.batch();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
//		} else {
//			System.err.println("Only suites with one application can run batch. Exit");
//			System.exit(0);
//		}
	}
    
    public void launch() {
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (applications.size() == 1 || Arguments.baseDir != null) {
					try {
						ApplicationLauncher application = applications.iterator().next();
						application.checkLicense();
						application.launch();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					JFrame frame = createAndShowFrame();
					UiUtil.centerAndShow(frame);
				}
			}
		});
	}
    
    protected JFrame createAndShowFrame() {
        JFrame frame = new JFrame(product);
        List<Action> actions = createActions();
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new SuitePanel(actions, "Select Application"), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon.getImage());
        frame.setResizable(false);
        return frame;
    }

    private List<Action> createActions() {
        List<Action> actions = new ArrayList<Action>();
        for (final ApplicationLauncher app : applications) {
            AbstractAction action = new AbstractAction(app.getTitle(), app.getIcon()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                	SwingUtilities.getWindowAncestor((Component) e.getSource()).setVisible(false);
                	SwingUtilities.invokeLater(new Runnable() {
                		@Override
                		public void run() {
                			try {
                				app.checkLicense();
                				app.launch();
                			} catch (Exception e1) {
                				e1.printStackTrace();
                			}
                		}
                	});
                }
            };
            actions.add(action);
        }
        return actions;
    }

}
