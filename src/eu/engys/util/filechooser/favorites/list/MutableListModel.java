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

/*
 * Copyright 2012 Krzysztof Otrebski (krzysztof.otrebski@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
