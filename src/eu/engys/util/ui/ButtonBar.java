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

package eu.engys.util.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

public class ButtonBar extends JComponent {
	private class ButtonBarLayout extends BoxLayout {

		public void layoutContainer(Container container) {
			super.layoutContainer(container);
			Rectangle rectangle = new Rectangle();
			Component acomponent[];
			int k = (acomponent = container.getComponents()).length;
			for (int i = 0; i < k; i++) {
				Component component = acomponent[i];
				Rectangle rectangle1 = component.getBounds();
				if (getOrientation() == 0)
					rectangle = rectangle1.height <= rectangle.height ? rectangle : rectangle1;
				else
					rectangle = rectangle1.width <= rectangle.width ? rectangle : rectangle1;
			}

			k = (acomponent = container.getComponents()).length;
			for (int j = 0; j < k; j++) {
				Component component1 = acomponent[j];
				Rectangle rectangle2 = component1.getBounds();
				if (getOrientation() == 0) {
					rectangle2.y = rectangle.y;
					rectangle2.height = rectangle.height;
				} else {
					rectangle2.x = rectangle.x;
					rectangle2.width = rectangle.width;
				}
				component1.setBounds(rectangle2);
			}

		}

		public ButtonBarLayout(Container container, int i) {
			super(container, i);
		}
	}

	public static final String uiClassID = "ButtonBarUI";
	
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	private static final String POS_KEY = "JButton.segmentPosition";
	private static final String ONLY = "only";
	private static final String FIRST = "first";
	private static final String MIDDLE = "middle";
	private static final String LAST = "last";
	private int orientation;

	public ButtonBar() {
		this(HORIZONTAL);
	}
	
	public String getUIClassID() {
        return uiClassID;
    }
	
	public ButtonBar(int orientation) {
		this.orientation = orientation;
		if (orientation == HORIZONTAL)
			setLayout(new ButtonBarLayout(this, ButtonBarLayout.LINE_AXIS));
		else
			setLayout(new ButtonBarLayout(this, ButtonBarLayout.PAGE_AXIS));
		setName("ButtonBar");
	}

	public Component add(Component component) {
		return addButton((AbstractButton) component);
	}

	public AbstractButton addButton(AbstractButton abstractbutton) {
		abstractbutton.getMaximumSize();
		Component acomponent[] = getComponents();
		int i = acomponent.length;
		String s = null;
		if (i == 0)
			s = ONLY;
		else if (i >= 1) {
			s = LAST;
			AbstractButton abstractbutton1 = (AbstractButton) acomponent[i - 1];
			if (i == 1)
				abstractbutton1.putClientProperty(POS_KEY, FIRST);
			else
				abstractbutton1.putClientProperty(POS_KEY, MIDDLE);
		}
		abstractbutton.putClientProperty(POS_KEY, s);
		abstractbutton.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertychangeevent) {
				if ("componentOrientation".equals(propertychangeevent.getPropertyName())) {
					JComponent jcomponent = (JComponent) propertychangeevent.getSource();
					String s1 = (String) jcomponent.getClientProperty(POS_KEY);
					if (s1.equals(FIRST) || s1.equals(LAST))
						jcomponent.putClientProperty(POS_KEY, s1.equals(FIRST) ? LAST : FIRST);
				}
			}

		});
		super.add(abstractbutton);
		return abstractbutton;
	}

	public void remove(Component component) {
		removeButton((AbstractButton) component);
	}

	public void removeButton(AbstractButton abstractbutton) {
		Component acomponent[] = getComponents();
		int i = acomponent.length;
		int j = 0;
		Component acomponent1[];
		int l = (acomponent1 = acomponent).length;
		for (int k = 0; k < l; k++) {
			Component component = acomponent1[k];
			if (component == abstractbutton)
				break;
			j++;
		}

		if (i == j)
			return;
		String s = null;
		AbstractButton abstractbutton1 = null;
		if (i == 2) {
			s = ONLY;
			abstractbutton1 = j != 0 ? (AbstractButton) acomponent[0] : (AbstractButton) acomponent[1];
		} else if (i > 2)
			if (j == 0) {
				s = FIRST;
				abstractbutton1 = (AbstractButton) acomponent[j + 1];
			} else if (j == i - 1) {
				s = LAST;
				abstractbutton1 = (AbstractButton) acomponent[j - 1];
			}
		if (abstractbutton1 != null)
			abstractbutton1.putClientProperty(POS_KEY, s);
		super.remove(abstractbutton);
	}

	public int getOrientation() {
		return orientation;
	}

	// public static void main(String[] args) {
	// SwingUtilities.invokeLater(new Runnable() {
	//
	// @Override
	// public void run() {
	// new HelyxLookAndFeel().init();
	//
	// ButtonBar bar = new ButtonBar();
	// bar.add(new JButton("pippo"));
	// bar.add(new JButton("3"));
	//
	// JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
	// toolBar.add(new JButton("pippo1"));
	// toolBar.add(new JButton("pippo2"));
	// toolBar.add(bar);
	// toolBar.setRollover(true);
	//
	// JPanel panel = new JPanel(new BorderLayout());
	// panel.add(toolBar, BorderLayout.NORTH);
	// panel.add(new JLabel(""), BorderLayout.CENTER);
	//
	// UiUtil.show("prova", panel);
	// }
	// });
	// }
}
