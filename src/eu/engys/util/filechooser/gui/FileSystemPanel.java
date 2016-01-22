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

package eu.engys.util.filechooser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

import eu.engys.util.ArchiveUtils;
import eu.engys.util.filechooser.actions.DeleteFileAction;
import eu.engys.util.filechooser.actions.ExtractArchiveAction;
import eu.engys.util.filechooser.actions.NewFolderAction;
import eu.engys.util.filechooser.actions.pathnavigation.BaseNavigateActionGoUp;
import eu.engys.util.filechooser.actions.pathnavigation.BaseNavigateActionOpen;
import eu.engys.util.filechooser.actions.pathnavigation.BaseNavigateActionRefresh;
import eu.engys.util.filechooser.table.FileNameWithType;
import eu.engys.util.filechooser.table.FileNameWithTypeComparator;
import eu.engys.util.filechooser.table.FileSize;
import eu.engys.util.filechooser.table.FileSystemTableModel;
import eu.engys.util.filechooser.table.QuickSearchKeyAdapter;
import eu.engys.util.filechooser.table.renderer.FileNameWithTypeTableCellRenderer;
import eu.engys.util.filechooser.table.renderer.FileSizeTableCellRenderer;
import eu.engys.util.filechooser.table.renderer.FileTypeTableCellRenderer;
import eu.engys.util.filechooser.table.renderer.MixedDateTableCellRenderer;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.textfields.PromptTextField;
import eu.engys.util.ui.treetable.TableFilter;

public class FileSystemPanel extends JPanel {

    private static final String ACTION_OPEN = "OPEN";
    private static final String ACTION_GO_UP = "GO_UP";
    private static final String ACTION_REFRESH = "REFRESH";
    private static final String ACTION_DELETE = "DELETE";
    private static final String ACTION_APPROVE = "ACTION APPROVE";

    public static final String NAME = "chooser.filesystempanel";
    public static final String CREATE_FOLDER = "create.folder";
    public static final String DELETE_FILE = "delete.file";
    public static final String EXTRACT_ARCHIVE = "extract.archive";

    private JTable table;
    private FileChooserController controller;
    private JScrollPane scrollPane;
    private HelyxFileFilter appliedFilter;
    private final Accessory accessory;
    private TableFilter<TableModel> tableFilter;
    private TableRowSorter<TableModel> sorter;
    private JTextField searchField;

    public FileSystemPanel(FileChooserController controller, Accessory accessory) {
        super(new BorderLayout(0, 5));
        setName(NAME);
        this.controller = controller;
        this.accessory = accessory;
        this.appliedFilter = HelyxFileFilter.getAllFilesFilter();
        layoutComponents();
    }

    private void layoutComponents() {
        createTable();
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 300));
        add(createFileSystemBar(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createTable() {
        FileSystemTableModel model = new FileSystemTableModel();
        this.table = new JTable(model);
        populateActionMap();
        populateInputMap();
        setColumnSize();

        sorter = createSorter();
        table.setRowSorter(sorter);

        tableFilter = new TableFilter<TableModel>("");
        tableFilter.setColumnsWhereToSearch(0);
        sorter.setRowFilter(tableFilter);

        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setColumnSelectionAllowed(false);

        setRenderer();
        addListeners();
    }

    private JComponent createFileSystemBar() {
        JButton createFolderButton = new JButton(new NewFolderAction(controller));
        createFolderButton.setName(CREATE_FOLDER);

        JButton deleteFileButton = new JButton(new DeleteFileAction(controller));
        deleteFileButton.setName(DELETE_FILE);

        JButton extractArchiveButton = new JButton(new ExtractArchiveAction(controller));
        extractArchiveButton.setName(EXTRACT_ARCHIVE);

        JToolBar bar = UiUtil.getToolbar("filesystem.toolbar");
        bar.add(searchField = createSearchField());
        bar.add(createFolderButton);
        bar.add(deleteFileButton);
        bar.add(extractArchiveButton);

        bar.setBorder(BorderFactory.createEmptyBorder());

        return bar;
    }

    private JTextField createSearchField() {
        final PromptTextField filterField = new PromptTextField();
        filterField.setPrompt("Search (* = any string, ? = any character)");
        filterField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }

        });
        return filterField;
    }

    private void filter() {
        tableFilter.setFilterText(searchField.getText());
        sorter.sort();
    }

    public void resetFilter() {
        searchField.setText("");
        filter();
    }

    private void setColumnSize() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(140);
        columnModel.getColumn(1).setMaxWidth(80);
        columnModel.getColumn(2).setMaxWidth(80);
        columnModel.getColumn(3).setMaxWidth(180);
        columnModel.getColumn(3).setMinWidth(120);
    }

    private void setRenderer() {
        table.setDefaultRenderer(FileSize.class, new FileSizeTableCellRenderer());
        table.setDefaultRenderer(FileNameWithType.class, new FileNameWithTypeTableCellRenderer());
        table.setDefaultRenderer(Date.class, new MixedDateTableCellRenderer());
        table.setDefaultRenderer(FileType.class, new FileTypeTableCellRenderer());
    }

    private void addListeners() {
        addMouseListener();
        addSelectionListener();
        addKeyListener();
        addAccessoryListener();
    }

    private void addMouseListener() {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                boolean isLeftButton = e.getButton() == MouseEvent.BUTTON1;
                boolean isDoubleClick = e.getClickCount() == 2;
                boolean isSomethingSelected = table.getSelectedRows().length > 0;
                if (isLeftButton && isDoubleClick && isSomethingSelected) {
                    table.getActionMap().get(ACTION_OPEN).actionPerformed(null);
                }
            }
        });
    }

    private void addKeyListener() {
        table.addKeyListener(new QuickSearchKeyAdapter(table));
    }

    private void addSelectionListener() {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                controller.updateNewFileName();
                controller.updateOkButton();
            }
        });
    }

    private void addAccessoryListener() {
        if (accessory != null) {
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    accessory.onSelectionChanged();
                }
            });
        }
    }

    private void populateInputMap() {
        InputMap inputMap = table.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), ACTION_OPEN);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), ACTION_APPROVE);

        inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), ACTION_GO_UP);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), ACTION_GO_UP);

        inputMap.put(KeyStroke.getKeyStroke("F5"), ACTION_REFRESH);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), ACTION_REFRESH);

        inputMap.put(KeyStroke.getKeyStroke("DELETE"), ACTION_DELETE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_CANCEL, 0), ACTION_DELETE);

    }

    private void populateActionMap() {
        ActionMap actionMap = table.getActionMap();
        actionMap.put(ACTION_OPEN, new BaseNavigateActionOpen(controller));
        actionMap.put(ACTION_GO_UP, new BaseNavigateActionGoUp(controller));
        actionMap.put(ACTION_REFRESH, new BaseNavigateActionRefresh(controller));
        actionMap.put(ACTION_DELETE, new DeleteFileAction(controller));
    }

    public FileObject getSelectedFileObject() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow > -1) {
            int convertedRowIndex = table.convertRowIndexToModel(selectedRow);
            return ((FileSystemTableModel) table.getModel()).get(convertedRowIndex);
        }
        return null;
    }

    public FileObject[] getSelectedFileObjects() {
        int[] selectedRows = table.getSelectedRows();
        FileObject[] fileObjects = new FileObject[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            fileObjects[i] = ((FileSystemTableModel) table.getModel()).get(table.convertRowIndexToModel(selectedRows[i]));
        }
        return fileObjects;
    }

    public void applyFilter(HelyxFileFilter selectedFilter) {
        this.appliedFilter = selectedFilter;
        if (controller.getUriPanel().getFileObject() != null) {
            controller.goToURL(controller.getUriPanel().getFileObject());
        }
    }

    public void setContent(FileObject[] fileObjects) {
        try {
            _setContent(fileObjects);
            table.clearSelection();
        } catch (FileSystemException e) {
            JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), e.getMessage(), "File Chooser Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void _setContent(FileObject[] fileObjectsWithParent) throws FileSystemException {
        List<FileObject> filteredObjects = new ArrayList<>();
        for (FileObject fileObject : fileObjectsWithParent) {
            if (!VFSUtils.isHiddenFile(fileObject)) {
                if (fileObject.getType() == FileType.FILE) {
                    manageFiles(filteredObjects, fileObject);
                } else if (fileObject.getType() == FileType.FOLDER) {
                    filteredObjects.add(fileObject);
                }
            }

        }
        ((FileSystemTableModel) table.getModel()).setContent(filteredObjects.toArray(new FileObject[0]));
    }

    private void manageFiles(List<FileObject> filteredObjects, FileObject fileObject) throws FileSystemException {
        boolean showOnlyFolders = controller.getSelectionMode().isDirsOnly();
        boolean showOnlyFoldersAndArchives = controller.getSelectionMode().isDirsAndArchives();
        if (showOnlyFolders) {
            return;
        } else {
            if (showOnlyFoldersAndArchives) {
                File file = new File(VFSUtils.decode(fileObject.getName().getURI(), controller.getSshParameters()));
                if (ArchiveUtils.isArchive(file)) {
                    filteredObjects.add(fileObject);
                }
            } else {
                filter(filteredObjects, fileObject);
            }
        }
    }

    private void filter(List<FileObject> filteredObjects, FileObject fileObject) {
        String fileName = fileObject.getName().getBaseName();
        String fileExtension = fileObject.getName().getExtension();
        for (String ext : appliedFilter.getExtensions()) {
            boolean filterIsAllFiles = ext.equals("*");
            boolean extensionMatchesExtension = fileExtension.equalsIgnoreCase(ext);
            boolean extensionMatchesName = fileExtension.isEmpty() && fileName.equalsIgnoreCase(ext);

            if (filterIsAllFiles || extensionMatchesExtension || extensionMatchesName) {
                filteredObjects.add(fileObject);
            }
        }
    }

    public void resetScroll() {
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    private TableRowSorter<TableModel> createSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        final FileNameWithTypeComparator fileNameWithTypeComparator = new FileNameWithTypeComparator();
        sorter.addRowSorterListener(new RowSorterListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void sorterChanged(RowSorterEvent e) {
                RowSorterEvent.Type type = e.getType();
                if (type.equals(RowSorterEvent.Type.SORT_ORDER_CHANGED)) {
                    List<? extends RowSorter.SortKey> sortKeys = e.getSource().getSortKeys();
                    for (RowSorter.SortKey sortKey : sortKeys) {
                        if (sortKey.getColumn() == FileSystemTableModel.COLUMN_NAME) {
                            fileNameWithTypeComparator.setSortOrder(sortKey.getSortOrder());
                        }
                    }
                }
            }
        });
        sorter.setComparator(FileSystemTableModel.COLUMN_NAME, fileNameWithTypeComparator);
        return sorter;
    }

    public void setMultiSelection(boolean b) {
        int selectionMode = b ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION;
        table.getSelectionModel().setSelectionMode(selectionMode);
    }

    public void selectFileByName(String selectedFileName) {
        if (selectedFileName != null) {
            FileSystemTableModel model = (FileSystemTableModel) table.getModel();
            int index = model.getIndexByName(selectedFileName);
            if (index != -1) {
                table.getSelectionModel().setSelectionInterval(index, index);
                table.scrollRectToVisible(table.getCellRect(index, 0, false));
            }
        }

    }

}
