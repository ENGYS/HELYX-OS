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


package eu.engys.util.ui.textfields.verifiers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import eu.engys.util.ui.textfields.PromptTextField;

/**
 * See Building a Swing Validation Package with InputVerifier 
 * http://www.javalobby.org/java/forums/t20551.html
 *
 */
public abstract class AbstractVerifier extends InputVerifier implements KeyListener {
	
	protected static final Color BG_COLOR = new Color(243, 255, 159);

	private final JPopupMenu popup = new JPopupMenu();

	private JLabel messageLabel = new JLabel();
	private ValidationStatusListener listener;
	private PromptTextField jcomponent;

	public AbstractVerifier(PromptTextField c) {
		this.jcomponent = c;
		initComponents();
		c.addKeyListener(this);
	}

	private void initComponents() {
		popup.setLayout(new FlowLayout());
		popup.setBorder(BorderFactory.createEmptyBorder());
		popup.setBackground(BG_COLOR);
		popup.add(messageLabel);
	}

	protected abstract boolean validationCriteria(JComponent jc);

	public boolean verify(JComponent jc) {
		if (!validationCriteria(jc)) {

			if (listener != null)
				listener.validateFailed();

			invalid();
			return false;
		}

		valid();

		if (jcomponent instanceof JFormattedTextField) {
			try {
				((JFormattedTextField) jcomponent).commitEdit();
			} catch (ParseException e) {
			}
		}

		if (listener != null)
			listener.validatePassed();

		return true;
	}

	private void invalid() {
		if (jcomponent.isShowing()) {
		    jcomponent.setInvalidColors();
		    
			popup.setSize(0, 0);
			Point point = jcomponent.getLocation();
			Dimension cDim = jcomponent.getSize();
			popup.pack();
			popup.show(jcomponent, point.x - (int) cDim.getWidth() / 2, point.y + (int) cDim.getHeight() / 2);
			jcomponent.requestFocus();
		} else {
			// Moved to another tab (or similar)
			// valid();
		    jcomponent.setInvalidColors();
		}
	}

	private void valid() {
	    jcomponent.setValidColors();
		popup.setVisible(false);
	}

	public void setMessage(String text) {
		messageLabel.setText(text);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		popup.setVisible(false);
		JTextField source = (JTextField) e.getSource();
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			verify(source); // ignore return value
			source.selectAll();
		} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			verify(source); // ignore return value
			source.selectAll();
		}
	}

	public void setValidationStatusListener(ValidationStatusListener listener) {
		this.listener = listener;
	}

	public interface ValidationStatusListener {
		void validateFailed(); // Called when a component has failed validation.

		void validatePassed(); // Called when a component has passed validation.
	}
}
