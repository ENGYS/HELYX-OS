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
package eu.engys.core.presentation;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.Icon;

import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class ActionManager {

    private static ActionManager instance;
    private ActionMap map = new ActionMap();

    private List<String> bannedKeys = new ArrayList<>();

    public static ActionManager getInstance() {
        if (instance == null)
            instance = new ActionManager();
        return instance;
    }

    public void parseActions(final ActionContainer o) {
        Class<?> klass = o.getClass();
        for (final Method m : klass.getMethods()) {
            Action action = m.getAnnotation(Action.class);
            if (action != null) {
                parseActions(o, m, action);
            }
            ActionToggle actionToggle = m.getAnnotation(ActionToggle.class);
            if (actionToggle != null) {
                parseToggleActions(o, m, actionToggle);
            }
        }
    }

    protected void parseActions(final ActionContainer o, final Method m, Action action) {
        String key = action.key();
        if (bannedKeys.contains(key))
            return;

        final boolean checkEnv = action.checkEnv();
        final boolean checkLic = action.checkLicense();

        String label = ResourcesUtil.getString(key + ".label");
        String tooltip = ResourcesUtil.getString(key + ".tooltip");
        Icon icon = ResourcesUtil.getIcon(key + ".icon");

        map.put(key, new ViewAction(label, icon, tooltip) {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (checkLic) {
                        if (o.isDemo()) {
                            UiUtil.showDemoMessage();
                            return;
                        }
                    }
                    if (checkEnv) {
                        if (!OpenFOAMEnvironment.isEnvironementLoaded()) {
                            UiUtil.showCoreEnvironmentNotLoadedWarning();
                            return;
                        }
                    }

                    m.invoke(o);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    protected void parseToggleActions(final ActionContainer o, final Method m, ActionToggle actionToggle) {
        Class<?>[] parameterTypes = m.getParameterTypes();
        if (parameterTypes.length != 1 || (parameterTypes.length == 1 && !parameterTypes[0].equals(boolean.class))) {
            throw new RuntimeException("'ActionToggle' annotation: the method must have 1 boolean parameter");
        }
        String key = actionToggle.key();
        if (bannedKeys.contains(key))
            return;
        
        String normal = actionToggle.normal();
        String selected = actionToggle.selected();
        // System.out.println("ActionManager.pa1rseActions() FOUND: "+key);

        String labelNormal = ResourcesUtil.getString(key + "." + normal + ".label");
        String tooltipNormal = ResourcesUtil.getString(key + "." + normal + ".tooltip");
        Icon iconNormal = ResourcesUtil.getIcon(key + "." + normal + ".icon");

//        String labelSelected = ResourcesUtil.getString(key + "." + selected + ".label");
        final String tooltipSelected = ResourcesUtil.getString(key + "." + selected + ".tooltip");
        final Icon iconSelected = ResourcesUtil.getIcon(key + "." + selected + ".icon");

        map.put(key, new ViewAction(labelNormal, iconNormal, tooltipNormal) {
            {
                putValue(SMALL_ICON + SELECTED_KEY, iconSelected);
                putValue(SHORT_DESCRIPTION + SELECTED_KEY, tooltipSelected);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m.invoke(o, isSelected());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void invoke(String string) {
        get(string).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, string));
    }

    public ViewAction get(String string) {
        return (ViewAction) map.get(string);
    }

    public boolean contains(String string) {
        return map.get(string) != null;
    }

    public void remove(String key) {
        bannedKeys.add(key);
        map.remove(key);
    }

}
