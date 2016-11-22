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

package eu.engys.core.dictionary;

import static eu.engys.core.dictionary.Dictionary.TAB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class ListField extends DefaultElement {

    private static final String SPACER = " ";
    private List<DefaultElement> list = new ArrayList<DefaultElement>();

    public ListField(String name) {
        super(decodeName(name));
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public ListField(ListField lf) {
        super(lf.getName());
        for (DefaultElement el : lf.getListElements()) {
            if (el instanceof Dictionary) {
                add(new Dictionary((Dictionary) el));
            } else {
                System.err.println("ListField only dictionaries are allowed as elements: " + el.getName());
            }
        }
    }

    private static String decodeName(String name) {
        if (name.contains(SPACER)) {
            StringTokenizer tokenizer = new StringTokenizer(name, SPACER);
            if (tokenizer.countTokens() == 1) {
                // System.out.println("ListField.decodeName() 1 TOKEN");
                String token = tokenizer.nextToken();
                try {
                    int size = Integer.parseInt(token);
                    return "";
                } catch (NumberFormatException ex) {
                    return token;
                }
            } else if (tokenizer.countTokens() == 2) {
                // System.out.println("ListField.decodeName() 2 TOKEN");
                String token1 = tokenizer.nextToken();
                String token2 = tokenizer.nextToken();
                try {
                    int size = Integer.parseInt(token2);
                } catch (NumberFormatException ex) {
                    return name;
                }
                return token1;
            } else if (tokenizer.countTokens() == 3) {
                // System.out.println("ListField.decodeName() 3 TOKEN");
                String token1 = tokenizer.nextToken();
                String token2 = tokenizer.nextToken();
                String token3 = tokenizer.nextToken();
                try {
                    int size = Integer.parseInt(token3);
                } catch (NumberFormatException ex) {
                }
                return name;
            }
        } else {
            // System.out.println("ListField.decodeName() 4+ TOKEN");
            try {
                int size = Integer.parseInt(name);
                return "";
            } catch (NumberFormatException ex) {
                return name;
            }
        }
        return name;
    }

    public void add(DefaultElement element) {
        list.add(element);
    }

    public List<DefaultElement> getListElements() {
        return Collections.unmodifiableList(list);
    }

    public void merge(ListField l) {
        for (DefaultElement el : l.getListElements()) {
            if (containsElement(el)) {
                if (el instanceof Dictionary) {
                    Dictionary dictionary = getDictionary(el.getName());
                    if (dictionary != null) {
                        dictionary.merge((Dictionary) el);
                    } else {
                        System.err.println("Error merging list '" + getName() + "' with " + l.getName() + "");
                    }
                } else {
                    // do nothing
                }
            } else {
                add(el);
            }
        }
    }

    private boolean containsElement(DefaultElement element) {
        for (DefaultElement e : list) {
            if (e instanceof Dictionary && e.getName().equals(element.getName()) && !e.getName().isEmpty()) {
                return true;
            } else if (e.getName().equals(element.getName()) && e.equals(element)) {
                return true;
            }
        }
        return false;
    }

    public void writeListField(StringBuffer sb, String rowHeader) {
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append(getName());
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append("(");
        for (DefaultElement el : getListElements()) {
            DictionaryWriter.writeElement(sb, rowHeader, el);
        }
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append(");");
    }

    public void writeListDict(StringBuffer sb, String rowHeader) {
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append(list.size());
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append("(");
        for (DefaultElement el : getListElements()) {
            DictionaryWriter.writeElement(sb, rowHeader, el);
        }
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append(")");
    }

    public Dictionary getDictionary(String name) {
        for (DefaultElement e : list) {
            if (e instanceof Dictionary && !e.getName().isEmpty() && e.getName().equals(name)) {
                return (Dictionary) e;
            }
        }
        return null;
    }

    public List<Dictionary> getDictionaries() {
        List<Dictionary> dicts = new ArrayList<>();
        for (DefaultElement e : list) {
            if (e instanceof Dictionary) {
                dicts.add((Dictionary) e);
            }
        }
        return dicts;
    }

}
