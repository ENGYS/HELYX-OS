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
package eu.engys.core.project.system.monitoringfunctionobjects;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

public class TimeBlocks implements Iterable<TimeBlock>, Serializable {

    private static final long serialVersionUID = 42L;

    private List<TimeBlock> list;
    Object mutex;

    private String key;

    public TimeBlocks(String key) {
        this.key = key;
        list = new LinkedList<>();
        mutex = this;
    }
    
    public TimeBlocks() {
        this("");
    }

    public TimeBlocks(List<TimeBlocks> blocks) {
        this();
        if (!blocks.isEmpty()) {
            for (TimeBlocks tb : blocks) {
                addAll(tb);
            }
            this.key = blocks.get(0).getKey();
        }
    }

    public void setKey(String key) {
        synchronized (mutex) {
            this.key = key;
        }
    }

    public String getKey() {
        synchronized (mutex) {
            return key;
        }
    }

    public void clear() {
        synchronized (mutex) {
            list.clear();
        }
    }

    public String toString() {
        synchronized (mutex) {
            return list.toString();
        }
    }

    public int size() {
        synchronized (mutex) {
            return list.size();
        }
    }

    public boolean isEmpty() {
        synchronized (mutex) {
            return list.isEmpty();
        }
    }

    public boolean addAll(TimeBlocks blocks) {
        synchronized (mutex) {
            return list.addAll(Lists.newArrayList(blocks));
        }
    }

    public boolean add(TimeBlock block) {
        synchronized (mutex) {
            return list.add(block);
        }
    }

    public TimeBlock get(int index) {
        synchronized (mutex) {
            return list.get(index);
        }
    }

    public TimeBlock remove(int index) {
        synchronized (mutex) {
            return list.remove(index);
        }
    }

    public int indexOf(Object o) {
        synchronized (mutex) {
            return list.indexOf(o);
        }
    }

    public int lastIndexOf(Object o) {
        synchronized (mutex) {
            return list.lastIndexOf(o);
        }
    }

    public TimeBlock getLast() {
        return get(list.size() - 1);
    }

    public Iterator<TimeBlock> iterator() {
        synchronized (mutex) {
            return list.iterator();
        }
    }

    public void orderAscending() {
        Collections.sort(list, new Comparator<TimeBlock>() {
            @Override
            public int compare(TimeBlock o1, TimeBlock o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
    }
}
