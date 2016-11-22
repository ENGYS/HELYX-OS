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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DualList extends JPanel {
    private JList<Object> sourceList;
    private SortedListModel sourceListModel;

    private JList<Object> destList;
    private SortedListModel destListModel;

    private JButton addButton;
    private JButton removeButton;

    public DualList() {
        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        sourceListModel = new SortedListModel();
        sourceList = new JList<>(sourceListModel);
        sourceList.setName("source.list");

        addButton = new JButton(">>");
        addButton.setName(">>");
        addButton.addActionListener(new AddListener());
        removeButton = new JButton("<<");
        removeButton.setName("<<");
        removeButton.addActionListener(new RemoveListener());

        destListModel = new SortedListModel();
        destList = new JList<>(destListModel);
        destList.setName("dest.list");

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Available Elements:"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(sourceList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Selected Elements:"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(destList), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(addButton);
        centerPanel.add(removeButton);
        centerPanel.add(Box.createVerticalGlue());

        add(leftPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(centerPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 4, 0, 4), 0, 0));
        add(rightPanel, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    /*
     * SOURCE
     */

    public void setSourceElements(Object newValue[]) {
        sourceListModel.clear();
        sourceListModel.addAll(newValue);
    }

    @SuppressWarnings("deprecation")
    private void clearSourceSelected() {
        Object selected[] = sourceList.getSelectedValues();
        for (int i = selected.length - 1; i >= 0; --i) {
            sourceListModel.removeElement(selected[i]);
        }
        sourceList.getSelectionModel().clearSelection();
    }

    /*
     * DESTINATION
     */

    public void setDestinationElements(Object newValue[]) {
        destListModel.clear();
        destListModel.addAll(newValue);
    }

    public String[] getDestinationElements() {
        String[] elements = new String[destListModel.model.size()];
        int i = 0;
        for (Iterator<Object> it = destListModel.iterator(); it.hasNext();) {
            elements[i++] = (String) it.next();
        }
        return elements;
    }

    @SuppressWarnings("deprecation")
    private void clearDestinationSelected() {
        Object selected[] = destList.getSelectedValues();
        for (int i = selected.length - 1; i >= 0; --i) {
            destListModel.removeElement(selected[i]);
        }
        destList.getSelectionModel().clearSelection();
    }

    /*
     * OTHER
     */

    @SuppressWarnings("deprecation")
    private class AddListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object selected[] = sourceList.getSelectedValues();
            destListModel.addAll(selected);
            clearSourceSelected();
        }
    }

    @SuppressWarnings("deprecation")
    private class RemoveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object selected[] = destList.getSelectedValues();
            sourceListModel.addAll(selected);
            clearDestinationSelected();
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("Dual List Box Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DualList dual = new DualList();
        List<String> model = new ArrayList<>();
        model.add("One");
        model.add("Two");
        model.add("Three");
        model.add("Four");
        model.add("Five");
        model.add("Six");
        model.add("Seven");
        model.add("Eight");
        model.add("Nine");
        model.add("Ten");
        dual.setSourceElements(model.toArray(new String[0]));
        frame.add(dual, BorderLayout.CENTER);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    private class SortedListModel extends AbstractListModel<Object> {
        SortedSet<Object> model;

        public SortedListModel() {
            model = new TreeSet<Object>();
        }

        public int getSize() {
            return model.size();
        }

        public Object getElementAt(int index) {
            return model.toArray()[index];
        }

        public void add(Object element) {
            if (model.add(element)) {
                fireContentsChanged(this, 0, getSize());
            }
        }

        public void addAll(Object elements[]) {
            Collection<Object> c = Arrays.asList(elements);
            model.addAll(c);
            fireContentsChanged(this, 0, getSize());
        }

        public void clear() {
            model.clear();
            fireContentsChanged(this, 0, getSize());
        }

        public boolean contains(Object element) {
            return model.contains(element);
        }

        public Object firstElement() {
            return model.first();
        }

        public Iterator<Object> iterator() {
            return model.iterator();
        }

        public Object lastElement() {
            return model.last();
        }

        public boolean removeElement(Object element) {
            boolean removed = model.remove(element);
            if (removed) {
                fireContentsChanged(this, 0, getSize());
            }
            return removed;
        }
    }

}
