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

import static eu.engys.core.dictionary.Dictionary.SPACER;
import static eu.engys.core.dictionary.Dictionary.VALUE_LINK;
import static eu.engys.core.dictionary.Dictionary.VALUE_UNIFORM_LINK;
import static eu.engys.core.dictionary.Dictionary.VERBOSE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.DictionaryLinkResolver;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.util.IOUtils;

public class DictionaryReader2 {

    private static final String NAME_WITH_PARENTHESIS_REGEXP = ";\\s([^(\\s]*?\\([^\\s]*?\\)[^)\\s]*?)\\s";
    static final String LIST_START = "(";
    static final String LIST_END = ")";
    // private static final String LIST_START = "!";
    // private static final String MATRIX_START = "&";
    // private static final String MATRIX_DELIMITER = "|";
    static final String FIELD_END = ";";
    static final String DICTIONARY_END = "}";
    static final String DICTIONARY_START = "{";

    static final String TOKENS_LIST = DICTIONARY_END + FIELD_END + DICTIONARY_START + LIST_START + LIST_END;// " };{(";

    private static final String COMMENT_REGEX = "/\\*(?:.|[\\n\\r])*?\\*/";
    private Dictionary dictionary;
    private DictionaryLinkResolver linkResolver;

    public DictionaryReader2(Dictionary dictionary) {
        this(dictionary, new DictionaryLinkResolver(dictionary));
    }

    public DictionaryReader2(Dictionary dictionary, DictionaryLinkResolver linkResolver) {
        this.dictionary = dictionary;
        this.linkResolver = linkResolver;
    }

    public void read(File file) {
        String text = readFile(file);
        text = prepareText(text, file);
        textToDictionary(text);
        if (dictionary.found("FoamFile"))
            dictionary.remove("FoamFile");
    }

    public void read(InputStream is) {
        String text = readStream(is);
        text = prepareText(text, null);
        textToDictionary(text);
        if (dictionary.found("FoamFile"))
            dictionary.remove("FoamFile");
    }

    public void read(String text) {
        read(text, false);
    }

    public void read(String text, boolean removeHeader) {
        text = prepareText(text, null);
        textToDictionary(text);
        if (dictionary.found("FoamFile") && removeHeader) {
            dictionary.remove("FoamFile");
        }
    }

    private String readFile(File file) {
        return IOUtils.readStringFromFile(file);
    }

    private String readStream(InputStream is) {
        String text = "";
        try {
            text = IOUtils.readStringFromStream(is);
        } catch (IOException e) {
            System.err.println("Error reading stream : " + e.getMessage());
        }
        return text;
    }

    private String prepareText(String text, File file) {
        text = text.replaceAll(COMMENT_REGEX, "");
        text = text.replaceAll("\t", SPACER);

        StringTokenizer rowTokenizer = new StringTokenizer(text, "\n");
        StringBuffer sb = new StringBuffer();
        while (rowTokenizer.hasMoreTokens()) {
            String token = rowTokenizer.nextToken();
            token = token.trim();
            if (token.startsWith("//"))
                continue;
            if (token.contains("//")) {
                token = token.substring(0, token.indexOf("//")).trim();
            }
            if (token.startsWith("#include")) {
                sb.append(importFile(token, file));
                continue;
            }
            sb.append(token);
            sb.append("\n");
        }
        text = sb.toString();
        return text;
    }

    private String importFile(String token, File file) {
        String text = "";
        if (file != null) {
            try {
                Pattern regex = Pattern.compile("#include\\s+\"(.+)\"");
                Matcher regexMatcher = regex.matcher(token);
                if (regexMatcher.find() && regexMatcher.groupCount() == 1) {
                    String fileName = regexMatcher.group(1).trim();
                    String parentDir = file.getParent();

                    File fileToImport = new File(parentDir, fileName);
                    text = readFile(fileToImport);
                    text = prepareText(text, fileToImport);
                }
            } catch (PatternSyntaxException ex) {
                ex.printStackTrace();
            }
        }

        return text;
    }

    protected void textToDictionary(String text) {
        text = text.replace("\n", SPACER);
        text = new Rewriter(NAME_WITH_PARENTHESIS_REGEXP) {
            public String replacement() {
                String nameWithParenthesis = group(1);
                // System.out.println("nameWithParentesis = "+nameWithParenthesis);
                return FIELD_END + SPACER + nameWithParenthesis.replace("(", "<<").replace(")", ">>") + SPACER;
            }
        }.rewrite(text);

        printOut(text);

        text = text.replaceAll("\\{", SPACER + "{" + SPACER);
        text = text.replaceAll("\\}", SPACER + "}" + SPACER);
        text = text.replaceAll(";", SPACER + ";" + SPACER);
        text = text.replaceAll("\\(", SPACER + "(" + SPACER);
        text = text.replaceAll("\\)", SPACER + ")" + SPACER);
        text = text.replaceAll("\\s+", SPACER);
        text = text.replaceAll("<<", "(");
        text = text.replaceAll(">>", ")");

        printOut(text);

        parseDictionary(text);
    }

    protected void parseDictionary(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text, SPACER);
        Stack<String> stack = new Stack<String>();
        readDictionary(tokenizer, stack);
        printOut("##################################\n" + toString());
        linkResolver.resolve(dictionary);
    }

    void readDictionary(StringTokenizer st, Stack<String> stack) {
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            printOut("TOKEN: " + token);
            stack.push(token);// metto nella pila

            if (stack.peek().equals(DICTIONARY_START)) {
                stack.pop();
                String name = stack.pop();
                Dictionary d = new Dictionary(name);
                printOut("START DICTIONARY: " + name);

                new DictionaryReader2(d).readDictionary(st, stack);

                if (isMultiple(name)) {
                    String[] names = extractMultipleKeys(name);
                    for (String n : names) {
                        if (isGeneric(withDoubleQuotes(n))) {
                            Dictionary copy = new Dictionary(d);
                            copy.setName(withDoubleQuotes(n));
                            dictionary.addGeneric(copy);
                        } else {
                            Dictionary copy = new Dictionary(d);
                            copy.setName(n);
                            dictionary.add(copy);
                        }
                    }
                } else if (isGeneric(name)) {
                    dictionary.addGeneric(d);
                } else {
                    dictionary.add(d);
                }
            } else if (stack.peek().equals(DICTIONARY_END)) {
                // stack.pop();
                String name = stack.pop();
                printOut("FINE DICTIONARY: " + name);
                return;
            } else if (stack.peek().equals(FIELD_END)) {
                stack.pop(); // tolgo il ;

                /* in teoria nello stack c'e' tutto il field */
                if (stack.isEmpty())
                    continue;

                readFields(stack);
            } else if (stack.peek().equals(LIST_START)) {
                ListField2 list = listFromStack(stack);

                printOut("START LIST: " + list.getName());

                ListReader2 reader;
                if (list instanceof ThetaListField2) {
                    reader = new ThetaListReader2((ThetaListField2) list, true);
                } else {
                    if (dictionary.getFoamFile() != null) {
                        reader = new ListReader2(list, true);
                    } else {
                        reader = new ListReader2(list, false);
                    }
                }
                
                if (reader.readList(st, stack)) {
                    dictionary.add(list);
                } else {
                    List<String> unspecifiedList = new ArrayList<>(stack);
                    String key = list.getName();
                    String value = "";
                    for (String item : unspecifiedList) {
                        value += SPACER + item;
                    }
                    dictionary.add(key, value);
                }
            }
        }
    }

    private ListField2 listFromStack(Stack<String> stack) {
        String separator = stack.pop();
        Stack<String> listStack = new Stack<>();
        while (!stack.isEmpty() && !isSeparator(stack.peek())) {
            String pop = stack.pop();
            listStack.push(pop);
        }
        if (listStack.isEmpty()) {
            stack.push(separator);
            return new ListField2("");
        } else {
            String name = listStack.pop();
            while (!listStack.isEmpty()) {
                name += " " + listStack.pop();
            }
            stack.push(separator);
            
            if (name.equals("thetaProperties")) {
                return new ThetaListField2(name);
            } else {
                return new ListField2(name); 
            }
        }
    }

    private void readFields(Stack<String> stack) {
        Stack<String> fieldStack = new Stack<>();
        while (!stack.isEmpty() && !isSeparator(stack.peek())) {
            String pop = stack.pop();
            fieldStack.push(pop);
        }
        if (fieldStack.size() == 0) {
            // do nothing
        } else if (fieldStack.size() == 1) {
            dictionary.add(fieldStack.pop(), "");
        } else if (fieldStack.size() >= 2) {
            String key = fieldStack.pop();
            String value = fieldStack.pop();
            while (!fieldStack.isEmpty()) {
                value += " " + fieldStack.pop();
            }

            if (readDimensionedScalar(key + " " + value)) {
                return;
            }

            if (isMultiple(key)) {
                String[] keys = extractMultipleKeys(key);
                for (String k : keys) {
                    if (isGeneric(withDoubleQuotes(k))) {
                        dictionary.addGeneric(withDoubleQuotes(k), value);
                    } else {
                        dictionary.add(k, value);
                    }
                }
            } else if (isGeneric(key)) {
                dictionary.addGeneric(key, value);
            } else if (isLink(value)) {
                extractLink(key, value);
            } else {
                dictionary.add(key, value);
            }
        }
    }

    static boolean isSeparator(String token) {
        return token.equals("{") || token.equals("}") || token.equals("(") || token.equals(")") || token.equals(";");
    }

    private boolean readDimensionedScalar(String field) {
        try {
            dictionary.add(new DimensionedScalar(field));
            return true;
        } catch (DictionaryException e) {
            return false;
        }
    }

    private String withDoubleQuotes(String k) {
        return "\"" + k + "\"";
    }

    public static String[] extractMultipleKeys(String key) {
        key = key.replace("\"", "");

        if (key.contains("(") && key.contains(")")) {
            int start = key.indexOf("(");
            int end = key.indexOf(")");
            String header = key.substring(0, start);
            String footer = key.substring(end + 1, key.length());
            String core = key.substring(start + 1, end);

            String[] tokens = core.split("\\|");

            String[] keys = new String[tokens.length];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = header + tokens[i] + footer;
            }
            return keys;
        } else {
            String[] tokens = key.split("\\|");
            return tokens;
        }

    }

    public String cleanGenericKey(String key) {
        key = key.substring(1, key.length() - 1);
        key = key.substring(0, key.indexOf(".*"));
        return key;
    }

    public boolean isGeneric(String key) {
        return key.startsWith("\"") && key.endsWith("\"") && key.contains(".*");
    }

    public boolean isMultiple(String key) {
        return key.startsWith("\"") && key.endsWith("\"") && key.contains("|");
    }

    public boolean isLink(String value) {
        return !value.startsWith("\"") && value.contains("$");
    }

    private void extractLink(String key, String value) {
        if (value.startsWith("uniform")) {
            String link = value.replace("uniform", "").trim();
            if (link.startsWith("$")) {
                dictionary.add(VALUE_UNIFORM_LINK + key, link);
            }
        } else if (value.startsWith("$")) {
            dictionary.add(VALUE_LINK + key, value);
        }
    }

    private static void printOut(String msg) {
        if (VERBOSE)
            System.out.println("[DICT] " + msg);
    }

    public static abstract class Rewriter {
        private Pattern pattern;
        private Matcher matcher;

        public Rewriter(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        public String group(int i) {
            return matcher.group(i);
        }

        public abstract String replacement();

        public String rewrite(CharSequence original) {
            this.matcher = pattern.matcher(original);
            StringBuffer result = new StringBuffer(original.length());
            while (matcher.find()) {
                matcher.appendReplacement(result, "");
                result.append(replacement());
            }
            matcher.appendTail(result);
            return result.toString();
        }

    }
}
