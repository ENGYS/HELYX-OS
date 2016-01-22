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


package eu.engys.core.dictionary.parser;

import static eu.engys.core.dictionary.Dictionary.SPACER;
import static eu.engys.core.dictionary.Dictionary.TAB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.math.NumberUtils;

import com.google.common.primitives.Doubles;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryWriter;
import eu.engys.core.dictionary.FieldElement;

public class ListField2 extends DefaultElement {

    private List<DefaultElement> listElements = new ArrayList<DefaultElement>();
    private int size;
    private String uniformity = "";
    private String identifier = "";

    public ListField2(String name) {
        super(name);
        decodeName(name);
    }

    public boolean isEmpty() {
        return listElements.isEmpty();
    }

    public ListField2(ListField2 lf) {
        super(lf.getName());
        for (DefaultElement el : lf.getListElements()) {
            if (el instanceof Dictionary) {
                add(new Dictionary((Dictionary) el));
            } else if (el instanceof ListField2) {
                add(new ListField2((ListField2) el));
            } else if (el instanceof FieldElement) {
                add(new FieldElement((FieldElement) el));
            } else {
                System.err.println("ListField2: only dictionaries are allowed as elements " + el.getName());
            }
        }
        this.size = lf.size;
        this.uniformity = lf.uniformity;
        this.identifier = lf.identifier;
    }

    private void decodeName(String name) {
        if (name.contains(SPACER)) {
            StringTokenizer tokenizer = new StringTokenizer(name, SPACER);
            if (tokenizer.countTokens() == 1) { // internalField (...); oppure
                                                // 10 (...);
//                System.out.println("ListField2.decodeName() 1");
                String token = tokenizer.nextToken();
                try {
                    this.size = Integer.parseInt(token);
                    setName("");
                } catch (NumberFormatException ex) {
                    this.size = -1;
                    setName(token);
                }
                this.uniformity = "";
                this.identifier = "";
            } else if (tokenizer.countTokens() == 2) {// internalField 10 (...);
                String token1 = tokenizer.nextToken();
                String token2 = tokenizer.nextToken();
//                System.out.println("ListField2.decodeName() token1: [" + token1 + "], token2: [" + token2 + "]");
                
                try {
                    this.size = Integer.parseInt(token2);
                } catch (NumberFormatException ex) {
                    this.size = -1;
                }
                if(size < 0){
                	setName(token1 + " " + token2);
                } else {
                	setName(token1);
                }
                this.uniformity = "";
                this.identifier = "";
            } else if (tokenizer.countTokens() == 3) {// internalField
                                                      // nonuniform 0()
//                System.out.println("ListField2.decodeName() 3");
                String token1 = tokenizer.nextToken();
                String token2 = tokenizer.nextToken();
                String token3 = tokenizer.nextToken();
//                System.out.println("ListField2.decodeName() token1 = '" + token1 + "'");
//                System.out.println("ListField2.decodeName() token2 = '" + token2 + "'");
//                System.out.println("ListField2.decodeName() token3 = '" + token3 + "'");
                try {
                    this.size = Integer.parseInt(token3);
                } catch (NumberFormatException ex) {
                    this.size = -1;
                }
                setName(token1);
                this.uniformity = token2;
                this.identifier = "";
            } else if (tokenizer.countTokens() == 4) { // internalField
                                                       // nonuniform
                                                       // List<scalar> 10
//                System.out.println("ListField2.decodeName() 4");
                String token1 = tokenizer.nextToken();
                String token2 = tokenizer.nextToken();
                String token3 = tokenizer.nextToken();
                String token4 = tokenizer.nextToken();
                try {
                    this.size = Integer.parseInt(token4);
                } catch (NumberFormatException ex) {
                    this.size = -1;
                }
                setName(token1);
                this.uniformity = token2;
                this.identifier = token3;
            }
        } else {
            try {
                this.size = Integer.parseInt(name);
                setName(Integer.toString(hashCode()));
            } catch (NumberFormatException ex) {
                this.size = -1;
                setName(name);
            }
        }
    }

    public void add(DefaultElement element) {
        listElements.add(element);
    }

    public void add(Collection<DefaultElement> elements) {
        listElements.addAll(elements);
    }

    public void add(String... values) {
        for (String value : values) {
            listElements.add(new FieldElement("", value));
        }
    }

    public List<DefaultElement> getListElements() {
        return Collections.unmodifiableList(listElements);
    }

    public void removeTopElements(int n) {
        for (int i = 0; i < n; i++) {
            listElements.remove(0);
        }
    }
    
    public void merge(ListField2 l) {
        for (DefaultElement el : l.getListElements()) {
            if (!containsElement(el)) {
                add(el);
            } else {
            }
        }
    }

    private boolean containsElement(DefaultElement element) {
        if (element instanceof FieldElement) {
            return false;
        }
        for (DefaultElement e : listElements) {
            if (haveSameName(element, e) && e.equals(element)) {
                return true;
            }
        }
        return false;
    }

    protected boolean haveSameName(DefaultElement element, DefaultElement e) {
        return e.getName().equals(element.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ListField2) {
            ListField2 list = (ListField2) obj;
            boolean haveSameName = haveSameName(this, list);
//            boolean equalCollection = CollectionUtils.isEqualCollection(list.getListElements(), listElements);
            boolean equalCollection = list.getListElements().containsAll(listElements) && listElements.containsAll(list.getListElements());
            return haveSameName && equalCollection;
        }
        return false;
    }

    public void writeListField(StringBuffer sb, String rowHeader) {
//        System.out.println("ListField2.writeListField() name: "+getName()+", size: "+size+", uniformity: "+uniformity+", identifier: "+identifier);
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        writeName(sb);
        sb.append(SPACER);
        sb.append(uniformity);
        sb.append(SPACER);
        sb.append(identifier);
        if (size >= 0 && !identifier.isEmpty()) {
            sb.append(SPACER);
            sb.append(size);
        } else if (size == 0 && isNonuniform()) {
            sb.append(SPACER);
            sb.append("0();");
            return;
        }
        if (!getName().isEmpty()) {
            sb.append("\n");
            sb.append(rowHeader);
            sb.append(TAB);
        }
        sb.append("(");
        for (DefaultElement el : getListElements()) {
            DictionaryWriter.writeElement(sb, rowHeader, el);
        }
        if (!getName().isEmpty()) {
            sb.append("\n");
            sb.append(rowHeader);
            sb.append(TAB);
            sb.append(");");
        } else {
            sb.append(")");
        }
    }

    public boolean nameIsANumber() {
        String name = getName();
        return NumberUtils.isNumber(name);
    }
    
    private void writeName(StringBuffer sb) {
        String name = getName();
        if (NumberUtils.isNumber(name)) {
            sb.append("");
        } else {
            sb.append(name);
        }
    }

    public void writeListDict(StringBuffer sb, String rowHeader) {
        sb.append(uniformity);
        sb.append(identifier);
        sb.append("\n");
        sb.append(rowHeader);
        writeName(sb);
        sb.append("\n");
        sb.append(rowHeader);
        sb.append(TAB);
        sb.append(listElements.size());
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
        for (DefaultElement e : listElements) {
            if (e instanceof Dictionary && !e.getName().isEmpty() && e.getName().equals(name)) {
                return (Dictionary) e;
            }
        }
        return null;
    }

    public void setListSize(int size2) {
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        writeListField(sb, "");
        return sb.toString();
    }

    public boolean isNonuniform() {
        return uniformity.equals("nonuniform");
    }
    
    public static String convertToString(ListField2 listField) {
    	StringBuilder sb = new StringBuilder();
    	convertToString(listField, sb);
    	return sb.toString();
    }
    
    private static void convertToString(ListField2 listField, StringBuilder sb) {
		sb.append("(");
		for (DefaultElement el : listField.getListElements()) {
			if(el instanceof FieldElement){
				sb.append(((FieldElement) el).getValue());
				sb.append(" ");
			} else if(el instanceof ListField2){
				convertToString((ListField2)el, sb);
			}
		}
		sb.append(")");
	}

    public List<Double> getElementsAsScalarList() {
        List<Double> list = new ArrayList<>();
        for (DefaultElement e : listElements) {
            if (e instanceof FieldElement && e.getName().isEmpty()) {
                String value = ((FieldElement) e).getValue();
                try {
                    list.add(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                }
            }
        }
        return list;
    }

    public List<double[]> getElementsAsVectorList() {
        List<double[]> list = new ArrayList<>();
        for (DefaultElement e : listElements) {
            if (e instanceof ListField2 && e.getName().isEmpty()) {
                List<Double> value = ((ListField2) e).getElementsAsScalarList();
                list.add(Doubles.toArray(value));
            }
        }
        return list;
    }

}
