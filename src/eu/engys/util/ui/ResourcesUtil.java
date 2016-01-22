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


package eu.engys.util.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.slf4j.LoggerFactory;

public final class ResourcesUtil {
	
	private static final ResourceBundle bundle = getBundle("eu/engys/resources/bundle");
	private static final ClassLoader loader = ResourcesUtil.class.getClassLoader();

	private static final Icon EMPTY_ICON = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB) {
		{
			Graphics2D g = createGraphics();
			g.setColor(Color.RED);
			g.fillRect(0, 0, 16, 16);
		}
	});

	public static String getString(String key) {
		try {
			return bundle.getString(key);
		} catch (Exception e) {
			LoggerFactory.getLogger(ResourcesUtil.class).warn(e.getMessage());
			return "MISSING";
		}
	}

	private static ResourceBundle getBundle(String string) {
		try {
			return ResourceBundle.getBundle(string);
		} catch (Exception e) {
			return new ResourceBundle() {

				@Override
				protected Object handleGetObject(String key) {
					return null;
				}

				@Override
				public Enumeration<String> getKeys() {
					return null;
				}
				
			};
		}
	}

	public static Icon getResourceIcon(String path) {
	    URL resource = ResourcesUtil.class.getClassLoader().getResource(path);
	    if (resource == null) {
	        return EMPTY_ICON;
	    } else {
	        return new ImageIcon(resource);
	    }
	}
	
	public static Icon getIcon(String key) {
		try {
			String path = bundle.getString(key);
			URL res = loader.getResource(path);
			return new ImageIcon(res);
		} catch (Exception e) {
			LoggerFactory.getLogger(ResourcesUtil.class).warn(e.getMessage());
			return EMPTY_ICON;
		}
	}

	public static URL getIconURL(String key) {
		try {
			String path = bundle.getString(key);
			URL res = loader.getResource(path);
			return res;
		} catch (Exception e) {
			LoggerFactory.getLogger(ResourcesUtil.class).warn(e.getMessage());
			return null;
		}
	}
}
