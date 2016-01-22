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

/*
Swing Hacks Tips and Tools for Killer GUIs
By Joshua Marinacci, Chris Adamson
First Edition June 2005  
Series: Hacks
ISBN: 0-596-00907-0
Pages: 542
website: http://www.oreilly.com/catalog/swinghks/
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class RichJLabel extends JLabel {

	private int tracking;

	public RichJLabel(String text, int tracking) {
		super(text);
		this.tracking = tracking;
	}

	private int left_x, left_y, right_x, right_y;

	private Color left_color, right_color;

	public void setLeftShadow(int x, int y, Color color) {
		left_x = x;
		left_y = y;
		left_color = color;
	}

	public void setRightShadow(int x, int y, Color color) {
		right_x = x;
		right_y = y;
		right_color = color;
	}

	public Dimension getPreferredSize() {
		String text = getText();
		FontMetrics fm = this.getFontMetrics(getFont());

		int w = fm.stringWidth(text);
		w += (text.length() - 1) * tracking;
		w += left_x + right_x;

		int h = fm.getHeight();
		h += left_y + right_y;

		return new Dimension(w, h);
	}

	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		char[] chars = getText().toCharArray();

		FontMetrics fm = this.getFontMetrics(getFont());
		int h = fm.getAscent();
		LineMetrics lm = fm.getLineMetrics(getText(), g);
		g.setFont(getFont());

		int x = 0;

		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			int w = fm.charWidth(ch) + tracking;

			g.setColor(getLeft_color());
			g.drawString("" + chars[i], x - left_x, h - left_y);

			g.setColor(getRight_color());
			g.drawString("" + chars[i], x + right_x, h + right_y);

			g.setColor(getForeground());
			g.drawString("" + chars[i], x, h);

			x += w;
		}

	}

	public Color getLeft_color() {
		return left_color;
	}
	
	public Color getRight_color() {
		return right_color;
	}
	
	public static void main(String[] args) {
		RichJLabel label = new RichJLabel("www.java2s.com", 0);
		label.setLeftShadow(1, 1, Color.white);
		label.setRightShadow(1, 1, Color.white);
		label.setForeground(Color.blue);
		label.setFont(label.getFont().deriveFont(140f));

		JFrame frame = new JFrame("RichJLabel hack");
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}

	public static void p(String str) {
		System.out.println(str);
	}
}
