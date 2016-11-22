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
package eu.engys.gui.view;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.executor.TerminalManager;
import eu.engys.core.project.Model;
import eu.engys.gui.GUIPanel;
import eu.engys.launcher.StartUpMonitor;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class MainPanel extends JPanel {
    
    private class TabChangeListener implements ChangeListener {
        private ViewElement elementByIndex(int index) {
            String title = tabbedPane.getTitleAt(index);
            ViewElement element = elementsByTitle.get(title);
            return element;
        }

        public void stateChanged(ChangeEvent changeEvent) {
            int index = tabbedPane.getSelectedIndex();
            ViewElement newElement = elementByIndex(index);
            MainPanel.this.firePropertyChange("element", currentElement== null ? null : currentElement.getClass(), newElement.getClass());
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(MainPanel.class);
    
    private final Model model;
    private final ProgressMonitor monitor;
    private final Set<ViewElement> viewElements;

    private Map<Class<? extends ViewElement>, ViewElement> elementsByClass = new HashMap<Class<? extends ViewElement>, ViewElement>();
    private Map<String, ViewElement> elementsByTitle = new HashMap<String, ViewElement>();

    private JTabbedPane tabbedPane;
    private ViewElement currentElement;

    private TabChangeListener tabChangeListener;
    private TerminalManager terminalManager;
    
    public MainPanel(Model model, Set<ViewElement> viewElements, TerminalManager terminalManager, ProgressMonitor monitor) {
        this.model = model;
        this.viewElements = viewElements;
        this.terminalManager = terminalManager;
        this.monitor = monitor;
    }

    public void layoutComponents() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("Synthetica.tabbedPane.tabIndex", 0);
        tabbedPane.setName("view.tab");

        for (ViewElement element : viewElements) {
            layoutElements(element);
        }
        
        JSplitPane terminalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        terminalSplitPane.setTopComponent(tabbedPane);
        terminalSplitPane.setBottomComponent(terminalManager.getComponent());
        terminalSplitPane.setOneTouchExpandable(false);
        terminalManager.setParent(terminalSplitPane);
        terminalManager.collapse();
        
        add(terminalSplitPane);
        
        tabChangeListener = new TabChangeListener();
        tabbedPane.addChangeListener(tabChangeListener);
        
        disableAll();
    }

    private void layoutElements(ViewElement element) {
        StartUpMonitor.info("Layout " + element.getTitle());
        logger.info("Layout {}", element.getTitle());

        element.layoutComponents();
        elementsByClass.put(element.getClass(), element);
        elementsByTitle.put(element.getTitle(), element);

        JPanel panel = element.getPanel();
        tabbedPane.addTab(element.getTitle(), panel);
    }

    public ViewElement getElement(Class<? extends ViewElement> klass) {
        return elementsByClass.get(klass);
    }

    public ViewElement getElement(String title) {
        return elementsByTitle.get(title);
    }

    public void clear() {
        currentElement = null;
        for (ViewElement element : viewElements) {
            clear(element.getClass());
        }        
    }

    private void clear(Class<? extends ViewElement> klass) {
        if (elementsByClass.containsKey(klass)) {
            final ViewElement viewElement = elementsByClass.get(klass);
            ExecUtil.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    viewElement.clear();
                    // viewElement.getView3D().clear();
                    for (final GUIPanel panel : viewElement.getPanels()) {
                        panel.clear();
                    }
                }
            });

        }
    }

    @SuppressWarnings("unchecked")
    public void load(Class<? extends ViewElement>... klasses) {
        for (Class<? extends ViewElement> klass : klasses) {
            final ViewElement element = elementsByClass.get(klass);   
            _load(element);
        }
    }
    
    public void load() {
        for (ViewElement element : viewElements) {
            _load(element);
        }
    }
    
    private void _load(final ViewElement viewElement) {
        logger.debug("LOAD: {}", viewElement.getTitle());
        if (viewElement.isEnabled(model)) {
            monitor.setIndeterminate(true);
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    viewElement.load(model);
                    _enable(viewElement, true);
                }
            });
            monitor.setIndeterminate(false);
        } else {
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    _enable(viewElement, false);
                }
            });
        }
    }

    public void save() {
        for (final ViewElement viewElement : viewElements) {
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    if (viewElement.isEnabled(model)) {
                        viewElement.save(model);
                    }
                }
            });
        }
    }
    
    public ViewElement getCurrentElement() {
        return currentElement;
    }

    private void _enable(final ViewElement element, final boolean enable) {
        int index = tabbedPane.indexOfTab(element.getTitle());
        if (index >= 0) {
            tabbedPane.setEnabledAt(index, enable);
        }
        if (enable)
            UiUtil.enable(element.getPanel());
        else
            UiUtil.disable(element.getPanel());
    }

    public void disableAll() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                for (ViewElement element : viewElements) {
                    _enable(element, false);
                }
            }
        });
    }

    public void stop(Class<? extends ViewElement> klass) {
        if (klass == null) {
            if (currentElement != null) {
                _stop(currentElement);        
            } else {
                _stop(viewElements.iterator().next());        
            }
        } else if (elementsByClass.containsKey(klass)) {
            _stop(elementsByClass.get(klass));        
        }
    }

    private void _stop(final ViewElement viewElement) {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                viewElement.stop();
                viewElement.save(model);
            }
        });
    }

    public void start(Class<? extends ViewElement> klass) {
        if (klass == null) {
            _start(viewElements.iterator().next());        
        } else if (elementsByClass.containsKey(klass)) {
            _start(elementsByClass.get(klass));        
        }
    }

    private void _start(final ViewElement viewElement) {
        this.currentElement = viewElement;
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                
                viewElement.start();
                viewElement.getActions().update();
                
                int index = tabbedPane.indexOfTab(viewElement.getTitle());
                if (index >= 0 && index != tabbedPane.getSelectedIndex()) {
                    tabbedPane.removeChangeListener(tabChangeListener);
                    tabbedPane.setSelectedIndex(index);
                    tabbedPane.addChangeListener(tabChangeListener);
                }
            }
        });
    }
}
