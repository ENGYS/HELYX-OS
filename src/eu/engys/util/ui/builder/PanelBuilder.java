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
package eu.engys.util.ui.builder;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.util.TooltipUtils;
import eu.engys.util.ui.UiUtil;
import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.INonGridRow;
import net.java.dev.designgridlayout.IRowCreator;
import net.java.dev.designgridlayout.ISpannableGridRow;
import net.java.dev.designgridlayout.RowGroup;

/**
 * <code><pre>
 * startChoice("Autore");
 * startGroup("Disney");
 *   startChoice("Citta");
 *       startGroup("Topolinia");
 *           startChoice("Personaggi");
 *               startGroup("Topolino");
 *                   newRow().grid(label("topolino")).add(label("TOPOLINO"));
 *                   newRow().grid(label("minnie")).add(label("MINNIE"));
 *                  newRow().grid(label("pluto")).add(label("PLUTO"));
 *               endGroup();
 *              startGroup("Pippo");
 *                   newRow().grid(label("pippo")).add(label("PIPPO"));
 *                   newRow().grid(label("orazio")).add(label("ORAZIO"));
 *                   newRow().grid(label("clarabella")).add(label("CLARABELLA"));
 *               endGroup();
 *               startGroup("Commissariato");
 *                   newRow().grid(label("basettoni")).add(label("BASETTONI"));
 *                   newRow().grid(label("manetta")).add(label("MANETTA"));
 *              endGroup();
 *          endChoice();//personaggi
 *       endGroup();//topolinia
 *       startGroup("Paperopoli");
 *           startChoice("Personaggi");
 *               startGroup("Paperino");
 *                   newRow().grid(label("paperino")).add(label("PAPERINO"));
 *                   newRow().grid(label("paperina")).add(label("PAPERINA"));
 *                   newRow().grid(label("qui")).add(label("QUI"));
 *                   newRow().grid(label("quo")).add(label("QUO"));
 *                   newRow().grid(label("qua")).add(label("QUA"));
 *               endGroup();
 *               startGroup("NonnaPapera");
 *                  newRow().grid(label("nonna")).add(label("NONNA"));
 *                   newRow().grid(label("ciccio")).add(label("CICCIO"));
 *               endGroup();
 *           endChoice();//personaggi
 *       endGroup();//paperopoli
 *   evndChoice();//citta
 * endGroup();//disney
 * startGroup("Marvel");
 *   startChoice("Gotham City");
 *       startGroup("Batman");
 *           newRow().grid(label("batman")).add(label("BATMAN"));
 *           newRow().grid(label("robin")).add(label("ROBIN"));
 *           newRow().grid(label("qui")).add(label("QUI"));
 *           newRow().grid(label("quo")).add(label("QUO"));
 *       endGroup();
 *       startGroup("Cattivi");
 *           newRow().grid(label("penguin")).add(label("PENGUIN"));
 *           newRow().grid(label("poisonivy")).add(label("POISONIVY"));
 *       endGroup();
 *   endChoice();//gotham city
 * endGroup();//marvel
 * endChoice();//autore
 * </pre></code>
 */
public class PanelBuilder {

    private final JPanel parent;
    protected Stack<KeydRowGroup> groups = new Stack<>();
    protected Stack<GroupController> controllers = new Stack<>();
    protected Stack<ShowHideAction> actions = new Stack<>();
    private DesignGridLayout layout;
    private int level = 0;
    private int indent = 0;
    private HashMap<String, HideController> hideables = new HashMap<>();

    private String prefix = "";

    public PanelBuilder() {
        super();
        this.parent = new JPanel();
        this.parent.setOpaque(false);
        this.layout = new DesignGridLayout(parent);
        // layout.labelAlignment(LabelAlignment.RIGHT);
        layout.withoutConsistentWidthAcrossNonGridRows();
        layout.emptyRow();
    }

    public PanelBuilder(String name) {
        this();
        this.parent.setName(name);
    }

    public void update() {
        parent.revalidate();
        parent.repaint();
    }

    public JPanel getPanel() {
        return parent;
    }

    public PanelBuilder removeMargins() {
        layout.margins(0, 0, 0, 0);
        return this;
    }

    public PanelBuilder withTitle(String title) {
        parent.setBorder(BorderFactory.createTitledBorder(title));
        return this;
    }

    public PanelBuilder withName(String name) {
        parent.setName(name);
        return this;
    }

    public PanelBuilder nonOpaque() {
        parent.setOpaque(false);
        return this;
    }

    public PanelBuilder margins(double top, double left, double bottom, double right) {
        layout.margins(top, left, bottom, right);
        return this;
    }

    private IRowCreator newRow() {
        // System.out.println("ChoicePanelBuilder.newRow() level: "+level+", groups: "+groups.size()
        // );
        if (level == 0)
            return layout.row();
        else if (level == 1)
            return layout.row().group(groups.get(0).group);
        else if (level == 2)
            return layout.row().group(groups.get(0).group).group(groups.get(1).group);
        else if (level == 3)
            return layout.row().group(groups.get(0).group).group(groups.get(1).group).group(groups.get(2).group);
        else if (level == 4)
            return layout.row().group(groups.get(0).group).group(groups.get(1).group).group(groups.get(2).group).group(groups.get(3).group);
        else if (level == 5)
            return layout.row().group(groups.get(0).group).group(groups.get(1).group).group(groups.get(2).group).group(groups.get(3).group).group(groups.get(4).group);
        else
            throw new IllegalStateException("Level > 5");
    }

    private ISpannableGridRow newGridRow() {
        return newRow().grid().indent(indent);
    }

    private ISpannableGridRow newGridRow(String string, String tooltip) {
        JLabel l = label(string);
        l.setToolTipText(TooltipUtils.format(tooltip));
        return newRow().grid(l).indent(indent);
    }

    private ISpannableGridRow newGridRow(JLabel label, String tooltip) {
        label.setToolTipText(tooltip);
        return newRow().grid(label).indent(indent);
    }

    private INonGridRow newLeftRow() {
        return newRow().left().indent(indent);
    }

    public void addSeparator(JComponent c) {
        newLeftRow().add(c).fill();
    }

    public void emptyRow() {
        newLeftRow().add(new JLabel(" ")).fill();
    }

    public void addSeparator(String string) {
        addSeparator(boldlabel(string));
    }

    public void addButtons(JComponent... components) {
        addLeft(components);
    }

    public void addLeft(JComponent... components) {
        newRow().bar().left(components);
    }

    public void addRight(JComponent... components) {
        newRow().bar().right(components);
    }

    public void addCenter(JComponent... components) {
        newRow().center().add(components);
    }

    public void addFill(JComponent... components) {
        newRow().center().add(components).fill();
    }

    public void addComponentToGroup(RowGroup group, JComponent c) {
        layout.row().group(group).grid().add(c);
    }

    public void addComponentToGroup(RowGroup group, String s, JComponent c) {
        layout.row().group(group).grid(new JLabel(s)).add(c);
    }

    public JComponent addComponent(JComponent c) {
        newGridRow().add(c);
        return c;
    }

    public JComponent addComponent(JLabel label, JComponent c) {
        newGridRow(label, null).add(c);
        c.setName(prefix + label.getName());
        return c;
    }

    public JComponent addComponent(String label, JComponent c) {
        newGridRow(label, null).add(c);
        if (prefix.isEmpty() && label.isEmpty() && c.getName() != null) {
            // if a name already in place do nothing
        } else {
            c.setName(prefix + label);
        }
        return c;
    }

    public JComponent addComponent(String label, String tooltip, JComponent c) {
        newGridRow(label, tooltip).add(c);
        c.setName(prefix + label);
        c.setToolTipText(TooltipUtils.format(tooltip));
        return c;
    }

    public JComponent addComponentAndSpan(String label, JComponent c) {
        newGridRow(label, null).add(c).spanRow();
        c.setName(prefix + label);
        return c;
    }

    public JComponent addComponentAndSpan(String label, JComponent c, int span) {
        newGridRow(label, null).add(c, span).spanRow();
        c.setName(prefix + label);
        return c;
    }

    public JComponent addSubComponent(String label, JComponent c) {
        newGridRow().grid(label(label)).add(c);
        return c;
    }

    public JComponent[] addComponent(JComponent... c) {
        newGridRow().add(c);
        return c;
    }

    public JComponent[] addComponent(int spanRows, JComponent... c) {
        newGridRow().addMulti(spanRows, c);
        return c;
    }

    public JComponent[] addComponent(String label, JComponent... c) {
        newGridRow(label, null).add(c);
        setNames(label, c);
        return c;
    }

    public JComponent[] addComponent2Columns(String l1, JComponent c1, String l2, JComponent c2) {
        newGridRow(l1, null).add(c1).grid(label(l2)).add(c2);
        c1.setName(l1);
        c2.setName(l2);
        return new JComponent[] { c1, c2 };
    }

    public JComponent[] addComponent3Columns(String l1, JComponent c1, String l2, JComponent c2, String l3, JComponent c3) {
        newGridRow(l1, null).add(c1).grid(label(l2)).add(c2).grid(label(l3)).add(c3);
        c1.setName(l1);
        c2.setName(l2);
        c3.setName(l3);
        return new JComponent[] { c1, c2, c3 };
    }

    public JComponent[] addComponent(String label, String tooltip, JComponent... c) {
        newGridRow(label, tooltip).add(c);
        setNames(label, c);
        return c;
    }

    public JComponent[] addComponent(String label, int spanCol, JComponent spanComponent, JComponent... c) {
        newGridRow(label, null).add(spanComponent, 3).add(c);
        setNames(label, c);
        return c;
    }

    public JComponent[] addComponentAndSpan(String label, JComponent... c) {
        newGridRow(label, null).add(c).spanRow();
        setNames(label, c);
        return c;
    }

    public void addComponent(List<List<JComponent>> comps) {
        ISpannableGridRow row = newGridRow();
        for (List<JComponent> list : comps) {
            if (list.size() == 1) {
                row.add(list.get(0));
            } else if (list.size() > 1) {
                row.addMulti(list.toArray(new JComponent[0]));
            }
        }
        row.spanRow();
    }

    public void addSpanRow() {
        newGridRow().spanRow();
    }

    private void setNames(String label, JComponent... c) {
        if (c.length == 1) {
            if (c[0].getName() == null) {
                c[0].setName(prefix + label);
            }
        } else {
            for (int i = 0; i < c.length; i++) {
                if (c[i].getName() == null) {
                    c[i].setName(prefix + label + "." + i);
                }
            }
        }
    }

    public JPanel addComponentsAsOne(String label, JComponent... c) {
        PanelBuilder pb = new PanelBuilder();
        pb.addComponent(c);
        setNames(label, c);
        JPanel panel = pb.removeMargins().getPanel();
        addComponent(label, panel);
        return panel;
    }

    public void indent() {
        indent++;
    }

    public void outdent() {
        indent--;
    }

    public void clear() {
        getPanel().setLayout(null);
        getPanel().removeAll();
        this.layout = new DesignGridLayout(parent);
        layout.withoutConsistentWidthAcrossNonGridRows();
        layout.emptyRow();
    }

    public GroupController startChoice(String choiceName) {
        return startChoice(choiceName, (String) null);
    }

    public GroupController startChoice(String choiceName, String tooltip) {
        return startChoice(choiceName, new JComboBoxController(), tooltip);
    }

    public GroupController startChoice(String choiceName, GroupController comboBox) {
        return startChoice(choiceName, comboBox, (String) null);
    }

    public GroupController startChoiceNoLabel(String name, GroupController comboBox) {
        ShowHideAction action = new ShowHideAction();

        actions.push(action);
        controllers.push(comboBox);

        JComponent c = controllers.peek().getComponent();
        c.setName(name);
        addFill(c);

        level++;

        return comboBox;
    }

    public GroupController startChoice(String choiceName, GroupController comboBox, String tooltip) {
        ShowHideAction action = new ShowHideAction();

        actions.push(action);
        controllers.push(comboBox);

        addComponent(choiceName, tooltip, controllers.peek().getComponent());

        level++;

        ((JComponent) comboBox).setToolTipText(TooltipUtils.format(tooltip));

        return comboBox;
    }

    public void endChoice() {
        level--;
        GroupController combo = controllers.pop();
        combo.addActionListener(actions.pop());
        combo.setSelectedKey(combo.getKeys().get(0));
    }

    public void startHidable(String key) {
        ShowHideAction action = new ShowHideAction();

        GroupController hider = hider();
        hideables.put(key, (HideController) hider);
        actions.push(action);
        controllers.push(hider);

        level++;
    }

    public void endHidable() {
        level--;
        // indent--;
        GroupController check = controllers.pop();
        check.addActionListener(actions.pop());
        check.setSelectedKey(check.getKeys().get(0));
    }

    public void setShowing(String hideable, String group) {
        // ci sono casi in cui non ci sono delle chiavi ad es turbulence
        // openings non ha timevarying
        if (hideables.containsKey(hideable)) {
            hideables.get(hideable).setSelectedItem(group);
        }
    }

    public GroupController startCheck(String checkName) {
        return startCheck(checkName, (String) null);
    }

    public GroupController startCheck(String checkName, String tooltip) {
        ShowHideAction action = new ShowHideAction();

        GroupController checkBox = checkBox(checkName);

        actions.push(action);
        controllers.push(checkBox);

        addSeparator(controllers.peek().getComponent());

        level++;
        indent();
        startGroup(checkName);

        ((JCheckBoxController) checkBox).setToolTipText(TooltipUtils.format(tooltip));

        return checkBox;
    }

    public GroupController startCheck(String checkName, JCheckBoxController checkBox) {
        return startCheck(checkName, checkBox, null);
    }

    public GroupController startCheck(String checkName, JCheckBoxController checkBox, String tooltip) {
        ShowHideAction action = new ShowHideAction();

        actions.push(action);
        controllers.push(checkBox);

        addSeparator(controllers.peek().getComponent());

        level++;
        indent();
        startGroup(checkName);

        checkBox.setToolTipText(TooltipUtils.format(tooltip));

        return checkBox;
    }

    public GroupController startCheck2(String checkName, JCheckBoxController checkBox, String tooltip) {
        ShowHideAction action = new ShowHideAction();

        actions.push(action);
        controllers.push(checkBox);

        addSeparator(controllers.peek().getComponent());

        level++;
        indent();
        startGroup(checkName);

        checkBox.setToolTipText(TooltipUtils.format(tooltip));

        return checkBox;
    }

    public void endCheck() {
        endCheck(true);
    }

    public void endCheck(boolean enable) {
        endGroup();
        level--;
        outdent();
        GroupController check = controllers.pop();
        check.addActionListener(actions.pop());
        check.setSelectedKey(check.getKeys().get(0));
        if (enable)
            return; // questo significa che se voglio inizialmente deselezionato
        // devo fare click due volte
        check.setSelectedKey(check.getKeys().get(0));
    }

    public void endCheck2(boolean enable) {
        endGroup();
        level--;
        outdent();
        GroupController check = controllers.pop();
        check.addItemListener(actions.pop());
        check.setSelectedKey(check.getKeys().get(0));
        if (enable)
            return; // questo significa che se voglio inizialmente deselezionato
        // devo fare click due volte
        check.setSelectedKey(check.getKeys().get(0));
    }

    public RowGroup startGroup(String groupName) {
        return startGroup(groupName, groupName);
    }

    public RowGroup startGroup(String groupKey, String groupName) {
        RowGroup group = new RowGroup();
        actions.peek().addItem(groupKey, group); // prima questo altrimenti
        // scassa
        controllers.peek().addGroup(groupKey, groupName);
        groups.push(new KeydRowGroup(groupKey, group));

        return group;
    }

    public void endGroup() {
        groups.pop().group.hide();
    }

    protected void checkForParent() {
        GroupController controller = controllers.pop();
        if (!controllers.isEmpty()) { // sto mettendo un choice dentro un choice
            GroupController parentController = controllers.pop();
            parentController.addChildController(controller);

            if (!controllers.isEmpty()) { // sto mettendo un choice dentro un choice dentro un choice
                GroupController grandParentController = controllers.peek();
                grandParentController.addChildController(parentController);
            } else {

            }
            controllers.push(parentController);
        } else {
        }
        controllers.push(controller);
    }

    public void setEnabled(boolean enabled) {
        if (enabled)
            UiUtil.enable(parent);
        else
            UiUtil.disable(parent);
    }

    private JLabel label(String string) {
        return new JLabel(string);
    }

    private JLabel boldlabel(String string) {
        JLabel boldlabel = label(string);
        boldlabel.setFont(boldlabel.getFont().deriveFont(Font.BOLD));
        return boldlabel;
    }

    // private JLabel bluelabel(String string) {
    // JLabel bluelabel = label(string);
    // bluelabel.setForeground(Color.BLUE);
    // return bluelabel;
    // }

    private GroupController checkBox(String name) {
        return new JCheckBoxController(name);
    }

    private GroupController hider() {
        return new HideController();
    }

    protected void beforeSelection(String selectedKey) {

    }

    protected void afterSelection(String selectedKey) {

    }

    /* set a prefix for naming component */
    public void prefix(String name) {
        this.prefix = name;
    }

    class ShowHideAction implements ActionListener, ItemListener {
        Map<String, RowGroup> groups = new HashMap<String, RowGroup>();
        String previousKey = null;

        public void addItem(String groupKey, RowGroup group) {
            groups.put(groupKey, group);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getSource();
            if (source instanceof GroupController) {
                GroupController controller = (GroupController) e.getSource();
                String selectedKey = controller.getSelectedKey();
                handleSelection(selectedKey);
                GroupController childController = controller.getChildController(selectedKey);
                if (childController != null) {
                    String childSelectedKey = childController.getSelectedKey();
                    childController.setSelectedKey(childSelectedKey);
                }
            } else {
                if (previousKey != null) {
                    groups.get(previousKey).hide();
                    previousKey = null;
                } else {
                    String selectedKey = groups.keySet().iterator().next();
                    groups.get(selectedKey).show();
                    previousKey = selectedKey;
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof GroupController) {
                GroupController controller = (GroupController) e.getSource();
                String selectedKey = controller.getSelectedKey();
                handleSelection(selectedKey);
                GroupController childController = controller.getChildController(selectedKey);
                if (childController != null) {
                    String childSelectedKey = childController.getSelectedKey();
                    childController.setSelectedKey(childSelectedKey);
                }
            } else {
                if (previousKey != null) {
                    groups.get(previousKey).hide();
                    previousKey = null;
                } else {
                    String selectedKey = groups.keySet().iterator().next();
                    groups.get(selectedKey).show();
                    previousKey = selectedKey;
                }
            }
        }

        private void handleSelection(String selectedKey) {
            beforeSelection(selectedKey);
            if (previousKey != null)
                groups.get(previousKey).hide();
            if (selectedKey != null)
                groups.get(selectedKey).show();

            previousKey = selectedKey;
            afterSelection(selectedKey);
        }

    }

    protected class KeydRowGroup {
        public String groupKey;
        public RowGroup group;

        public KeydRowGroup(String groupKey, RowGroup group) {
            this.group = group;
            this.groupKey = groupKey;
        }
    }
}
