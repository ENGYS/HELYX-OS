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

package eu.engys.core.dictionary.parser;

import static eu.engys.core.dictionary.Dictionary.VERBOSE;
import static eu.engys.core.dictionary.parser.DictionaryReader2.DICTIONARY_END;
import static eu.engys.core.dictionary.parser.DictionaryReader2.DICTIONARY_START;
import static eu.engys.core.dictionary.parser.DictionaryReader2.FIELD_END;
import static eu.engys.core.dictionary.parser.DictionaryReader2.LIST_END;
import static eu.engys.core.dictionary.parser.DictionaryReader2.LIST_START;

import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldElement;

class ListReader2 {

    private final ListField2 list;
    private final boolean subList;

    public ListReader2(ListField2 list, boolean subList) {
        this.list = list;
        this.subList = subList;
    }

    public boolean readList(StringTokenizer st, Stack<String> stack) {
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            stack.push(token);

            printOut("TOKEN: " + token);

            if (stack.peek().equals(DICTIONARY_START)) {
                stack.pop();
                String name = (stack.isEmpty() || stack.peek().equals(LIST_START) || stack.peek().equals(LIST_END)) ? "" : stack.pop();
                Dictionary d = new Dictionary(name);
                printOut("START LIST DICTIONARY: " + name);

                new DictionaryReader2(d).readDictionary(st, stack);
                list.add(d);
            } else if (stack.peek().equals(DICTIONARY_END)) {
                /* should not happen ! */
            } else if (stack.peek().equals(LIST_START)) {
                printOut("START SUB LIST");
                processStack(stack);

                ListField2 child = new ListField2("");
                ListReader2 reader = new ListReader2(child, true);
                reader.readList(st, stack);
                list.add(child);
                printOut("ADDING CHILD: " + child);
            } else if (stack.peek().equals(LIST_END)) {
                if (subList) {
                    printOut("END SUB LIST");
                    return processStack(stack);
                }
            } else if (stack.peek().equals(FIELD_END)) {
                printOut("FIELD_END");
                stack.pop(); // ;

                if (stack.peek().equals(LIST_END)) {
                    return processStack(stack);
                } else {
                    // throw new
                    // RuntimeException("finisce il field ma non la lista");
                    /* forse qui non si arriva mai ? */
                    return false;
                }
            }
        }
        return true;
    }

    private boolean processStack(Stack<String> stack) {
        printOut("-BEGIN PROCESS STACK: " + stackToString(stack));
        boolean success = false;
        if (containsBox(stack)) {
            printOut("-PROCESS STACK BOX");
            // readBox(stack);
            success = false;
        } else if (containsMatrix(stack)) {
            printOut("-PROCESS STACK MATRIX");
            readMatrix(stack);
            success = true;
        } else if (containsVector(stack)) {
            printOut("-PROCESS STACK VECTOR");
            readVector(stack);
            success = true;
        } else if (containsScalar(stack)) {
            printOut("-PROCESS STACK SCALAR");
            readScalar(stack);
            success = true;
        } else if (containsWord(stack)) {
            printOut("-PROCESS STACK WORD");
            readWord(stack);
            success = true;
        } else if (contains2Words(stack)) {
            printOut("-PROCESS STACK 2 WORDS");
            read2Words(stack);
            success = true;
        } else if (containsScalarList(stack)) {
            printOut("-PROCESS STACK SCALAR LIST");
            readScalarList(stack);
            success = true;
        } else {
            printOut("-PROCESS STACK NOT RECOGNIZED");
            success = false;
        }
        printOut("-END PROCESS STACK");
        return success;
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static boolean isWord(String str) {
        for (char c : str.toCharArray()) {
            if(!isValid(c)){
                return false;
            }
        }
        return true;
    }
    
    private static boolean isValid(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '-';
    }

    private String stackToString(Stack<String> stack) {
        StringBuilder sb = new StringBuilder();
        for (String s : stack) {
            sb.append(s + ",");
        }
        return sb.toString();
    }

    private boolean containsBox(Stack<String> stack) {
        int counter = 0, start = 0, end = 0, max = 0;
        for (int i = stack.size() - 1; i >= 0; i--) {
            String s = stack.get(i);

            if (s.equals(LIST_START)) {
                counter--;
                start++;
            }
            if (s.equals(LIST_END)) {
                counter++;
                end++;
            }

            max = Math.max(max, counter);

            if (counter == 0 && max == 1 && start == 2 && end == 2) {
                printOut("Contains BOX");
                return true;
            }
        }
        return false;
    }

    // private void readBox(Stack<String> stack) {
    // int start = getListStart(stack, 2);
    // int end = getListEnd(stack, );
    // List<String> subList = stack.subList(start, end);
    // for (String item : subList) {
    // if (item.equals(LIST_START) || item.equals(LIST_END) ) continue;
    // list.add(new FieldElement("", item));
    // }
    // subList.clear();
    // }

    private boolean containsWord(Stack<String> stack) {
        String list_start = stack.pop(); // could be a LIST_START
        String word = stack.pop(); // token to analyze
        if (!stack.isEmpty() && stack.peek().equals(LIST_START)) {
            stack.push(word);
            stack.push(list_start);
            if (isWord(word)) {
                printOut("Contains WORD");
                return true;
            } else {
                return false;
            }
        } else {
            stack.push(word);
            stack.push(list_start);
            return false;
        }
    }

    private boolean contains2Words(Stack<String> stack) {
        String list_start = stack.pop(); // could be a LIST_START
        String word1 = stack.pop(); // token to analyze
        if(!stack.isEmpty()){
            String word2 = stack.pop(); // token to analyze
            if (!stack.isEmpty() && stack.peek().equals(LIST_START)) {
                stack.push(word2);
                stack.push(word1);
                stack.push(list_start);
                if (isWord(word1) && isWord(word2)) {
                    printOut("Contains TWO WORDS");
                    return true;
                } else {
                    return false;
                }
            } else {
                stack.push(word2);
                stack.push(word1);
                stack.push(list_start);
                return false;
            }
        }
        stack.push(word1);
        stack.push(list_start);
        return false;
    }
    
    private boolean containsScalar(Stack<String> stack) {
        String list_start = stack.pop(); // could be a LIST_START
        String scalar = stack.pop(); // token to analyze
        if (!stack.isEmpty() && stack.peek().equals(LIST_START)) {
            stack.push(scalar);
            stack.push(list_start);
            if (isNumeric(scalar)) {
                printOut("Contains SCALAR");
                return true;
            } else {
                return false;
            }
        } else {
            stack.push(scalar);
            stack.push(list_start);
            return false;
        }
    }

    private void readScalarList(Stack<String> stack) {
        int start = 1;
        int end = stack.size() - 1;
        List<String> subList = stack.subList(start, end);
        for (String item : subList) {
            if (item.equals(LIST_START) || item.equals(LIST_END))
                continue;
            list.add(new FieldElement("", item));
        }
        subList.clear();
    }

    private void readScalar(Stack<String> stack) {
        String pop = stack.pop();
        String s = stack.pop();
        stack.push(pop);
        list.add(new FieldElement("", s));
    }

    private void readWord(Stack<String> stack) {
        String pop = stack.pop();//should be list start
        String s = stack.pop();
        stack.push(pop);
        list.add(new FieldElement("", s));
    }

    private void read2Words(Stack<String> stack) {
        String pop = stack.pop();//should be list start
        String w1 = stack.pop();
        String w2 = stack.pop();
        stack.push(pop);
        list.add(new FieldElement("", w2));
        list.add(new FieldElement("", w1));
    }

    private boolean containsScalarList(List<String> stack) {
        if (stack.get(0).equals(LIST_START)) {
            if (stack.get(stack.size() - 1).equals(LIST_START)) {
                if (stack.size() > 2) {
                    for (int i = stack.size() - 2; i > 0; i--) {
                        if (!isNumeric(stack.get(i))) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean containsVector(List<String> stack) {
        int counter = 0, max = 0;
        for (int i = stack.size() - 1; i >= 0; i--) {
            String s = stack.get(i);

            if (s.equals(LIST_START))
                counter--;
            if (s.equals(LIST_END))
                counter++;

            max = Math.max(max, counter);

            if (counter == 0 && max == 1) {
                printOut("-Contains VECTOR");
                return true;
            }
        }
        return false;
    }

    private void readVector(Stack<String> stack) {
        int start = getListStart(stack);
        int end = getListEnd(stack);
        List<String> subList = stack.subList(start, end);
        for (String item : subList) {
            if (item.equals(LIST_START) || item.equals(LIST_END))
                continue;
            list.add(new FieldElement("", item));
        }
        subList.clear();
    }

    private int getListStart(List<String> stack) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            String s = stack.get(i);
            if (s.equals(LIST_START))
                return i;
        }
        return -1;
    }

    private int getListEnd(List<String> stack) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            String s = stack.get(i);
            if (s.equals(LIST_END))
                return i + 1;
        }
        return -1;
    }

    private boolean containsMatrix(List<String> stack) {
        int start = 0, end = 0, level = 0, max = 0;
        for (String s : stack) {
            if (s.equals(LIST_START)) {
                start++;
                level++;
            }
            if (s.equals(LIST_END)) {
                end++;
                level--;
            }
            max = Math.max(max, level);
        }
        return start == end && max == 2;
    }

    private void readMatrix(List<String> stackList) {
        ListField2 child = null;
        for (String item : stackList) {
            if (item.equals(LIST_START) && child == null) {
                child = new ListField2("");
                list.add(child);
                continue;
            }
            if (item.equals(LIST_END) && child != null) {
                child = null;
                continue;
            }
            if (item.equals(LIST_START) || item.equals(LIST_END))
                continue;
            child.add(new FieldElement("", item));
        }
    }

    private String processField(String field) {
        if (field.contains("|")) {
            field = field.replace("|", ") (");
        }
        return field;
    }

    private static void printOut(String msg) {
        if (VERBOSE)
            System.out.println("[LIST] " + msg);
    }
}
