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
package eu.engys.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.Window;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.engys.util.Util;

public class StartUpMonitor {

    private static StartUpMonitor instance;
    private static int counter = 0;
    private static boolean isElements = false;
    private static boolean isHelyxOS = false;

    public static void info(final String msg) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("+++ " + msg + " +++");
        } else {
            if (instance == null) {
                instance = new StartUpMonitor();
                pwnSplashScreen();
                setApplicationType();
            }
            instance.render(msg);
        }
    }

    private static void setApplicationType() {
        try {
            URL coreGuiJarURL = StartUpMonitor.class.getProtectionDomain().getCodeSource().getLocation();
            String decodedCoreGuiJarURL = URLDecoder.decode(coreGuiJarURL.getFile(), Util.UTF_8);
            File libDir = new File(decodedCoreGuiJarURL).getParentFile();
            isElements = new File(libDir, "ELEMENTS.jar").exists();
            isHelyxOS = new File(libDir, "HELYX-OS.jar").exists();
        } catch (UnsupportedEncodingException e) {
            isElements = false;
            isHelyxOS = false;
        }
    }

    private static void pwnSplashScreen() {
        try {
            Field field = Window.class.getDeclaredField("beforeFirstWindowShown");
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, new AtomicBoolean(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeSplash() {
        SplashScreen splash = getSplashScreen();
        if (splash != null) {
            splash.close();
        }
    }

    private static SplashScreen getSplashScreen() {
        try {
            return SplashScreen.getSplashScreen();
        } catch (HeadlessException e) {
        }
        return null;
    }

    private void render(final String string) {
        SplashScreen splash = getSplashScreen();
        if (splash == null) {
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            return;
        }

        if (isElements) {
            paintElementsProgress(g, string);
        } else {
            paintHelyxProgress(g, string);
        }

        splash.update();
        counter++;
    }

    /*
     * HELYX
     */
    private void paintHelyxProgress(Graphics g, String string) {
        paintHelyxProgressBar(g);
        paintHelyxProgressText(g, string);
    }

    private void paintHelyxProgressBar(Graphics g) {
        int leftPadding = 15;
        int topPadding = 205;
        int splashWidth = getSplashScreen().getSize().width;
        int width = Math.min(10 * counter, splashWidth - (leftPadding * 2));

        Color firstColor;
        Color secondColor;

        if (isHelyxOS) {
            firstColor = Color.BLUE.darker();
            secondColor = Color.BLUE.brighter();
        } else {
            firstColor = Color.RED.darker();
            secondColor = Color.RED.brighter();
        }

        g.setColor(firstColor);
        g.fillRect(leftPadding, topPadding, width, 2);
        g.setColor(secondColor);
        g.fillRect(leftPadding, topPadding, width, 1);
    }

    private void paintHelyxProgressText(Graphics g, String string) {
        int leftPadding = 15;
        int topPadding = 175;

        Color bgColor = Color.WHITE;
        Color textColor = Color.BLACK;

        // background
        int background_width = 210;
        int background_height = 30;
        g.setPaintMode();
        g.setColor(bgColor);
        g.fillRect(leftPadding, topPadding, background_width, background_height);

        // foreground
        int text_height = background_height - 5;
        g.setColor(textColor);
        enableAntialias(g);
        g.drawString(string, leftPadding, topPadding + text_height);
        disableAntialias(g);
    }

    /*
     * ELEMENTS
     */
    private void paintElementsProgress(Graphics g, String string) {
        paintElementsProgressBar(g);
        paintElementsProgressText(g, string);
    }

    private void paintElementsProgressBar(Graphics g) {
        int leftPadding = 20;
        int topPadding = 56;
        int splashWidth = getSplashScreen().getSize().width;
        int width = Math.min(10 * counter, splashWidth - (leftPadding * 2 + 2));

        g.setColor(Color.GREEN.darker());
        g.fillRect(leftPadding, topPadding, width, 3);
        g.setColor(Color.GREEN.brighter());
        g.fillRect(leftPadding, topPadding, width, 1);
    }

    private void paintElementsProgressText(Graphics g, String string) {
        int leftPadding = 15;
        int topPadding = getSplashScreen().getSize().height - 35;

        Color bgColor = Color.WHITE;
        Color textColor = Color.BLACK;

        // background
        int background_width = 210;
        int background_height = 30;
        g.setPaintMode();
        g.setColor(bgColor);
        g.fillRect(leftPadding, topPadding, background_width, background_height);

        // foreground
        int text_height = background_height - 5;
        g.setColor(textColor);
        enableAntialias(g);
        g.drawString(string, leftPadding, topPadding + text_height);
        disableAntialias(g);
    }

    /*
     * Utils
     */

    private void enableAntialias(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void disableAntialias(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    // public static void main(String[] args) {
    // new HelyxLookAndFeel().init();
    // Model model = new Model();
    // model.init();
    // JPanel panel = new JPanel(new GridLayout(4, 2));
    // JLabel l1 = new JLabel("HELYX OS 1");
    // JLabel l2 = new JLabel("HELYX OS 2");
    // JLabel l3 = new JLabel("HELYX 1");
    // JLabel l4 = new JLabel("HELYX 2");
    // JLabel l5 = new JLabel("ELEMENTS 1");
    // JLabel l6 = new JLabel("ELEMENTS 2");
    // JLabel l7 = new JLabel("CASE MANAGER 1");
    // JLabel l8 = new JLabel("CASE MANAGER 2");
    //
    // l1.setOpaque(true);
    // l2.setOpaque(true);
    // l3.setOpaque(true);
    // l4.setOpaque(true);
    // l5.setOpaque(true);
    // l6.setOpaque(true);
    // l7.setOpaque(true);
    // l8.setOpaque(true);
    //
    // l1.setBackground(Color.BLUE.brighter());
    // l2.setBackground(Color.BLUE.darker());
    //
    // l3.setBackground(Color.RED.brighter());
    // l4.setBackground(Color.RED.darker());
    //
    // l5.setBackground(Color.GREEN.brighter());
    // l6.setBackground(Color.GREEN.darker());
    //
    // l7.setBackground(Color.YELLOW.brighter());
    // l8.setBackground(Color.YELLOW.darker());
    //
    // panel.add(l1);
    // panel.add(l2);
    // panel.add(l3);
    // panel.add(l4);
    // panel.add(l5);
    // panel.add(l6);
    // panel.add(l7);
    // panel.add(l8);
    //
    // JFrame f = UiUtil.defaultTestFrame("a", panel);
    // f.setSize(600, 300);
    // f.setVisible(true);
    // }

}
