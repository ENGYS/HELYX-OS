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

package eu.engys.util.filechooser.favorites.list;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 */
public class MutableListModel<T> extends AbstractListModel {

    private ArrayList<T> list;

    public MutableListModel() {
        list = new ArrayList<T>();
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public T getElementAt(int index) {
        return list.get(index);
    }

    public void add(T favorite) {
        list.add(favorite);
        fireIntervalAdded(this, list.size() - 1, list.size() - 1);
    }

    public void remove(int index) {
        list.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    public void change(int index, T favorite) {
        list.set(index, favorite);
        fireContentsChanged(this, index, index);
    }

    public void move(int from, int to) {
        list.add(to, list.get(from));
        if (to < from) {
            from++;
        }
        list.remove(from);
        fireContentsChanged(this, Math.min(from, to), Math.max(from, to));

    }

    public List<T> getList() {
        return new ArrayList<T>(list);
    }

}
