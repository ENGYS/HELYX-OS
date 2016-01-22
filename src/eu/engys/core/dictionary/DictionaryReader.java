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


package eu.engys.core.dictionary;

import static eu.engys.core.dictionary.Dictionary.DICTIONARY_LINK;
import static eu.engys.core.dictionary.Dictionary.SPACER;
import static eu.engys.core.dictionary.Dictionary.VALUE_LINK;
import static eu.engys.core.dictionary.Dictionary.VALUE_UNIFORM_LINK;
import static eu.engys.core.dictionary.Dictionary.VERBOSE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.IOUtils;
import eu.engys.util.RegexpUtils;

public class DictionaryReader {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryReader.class);

    private static final String LIST_START = "!";
    private static final String MATRIX_START = "&";
    private static final String MATRIX_DELIMITER = "|";
    private static final String FIELD_END = ";";
    private static final String DICTIONARY_END = "}";
    private static final String DICTIONARY_START = "{";
    private static final String COMMENT_REGEX = "/\\*(?:.|[\\n\\r])*?\\*/";
    private Dictionary dictionary;
    private DictionaryLinkResolver linkResolver;

    public DictionaryReader(Dictionary dictionary) {
        this(dictionary, new DictionaryLinkResolver(dictionary));
    }

    public DictionaryReader(Dictionary dictionary, DictionaryLinkResolver linkResolver) {
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
            logger.warn("Error reading stream: {} ", e.getMessage());
        }
        return text;
    }

    private String prepareText(String text, File file) {
//        text = text.replaceAll(COMMENT_REGEX, "");
        text = new jregex.Pattern(COMMENT_REGEX).replacer("").replace(text);
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

        // exploit table data structure
        text = text.replaceAll("\\(\\s*(" + RegexpUtils.DOUBLE + ")\\s*\\(([^\\)]*)\\)\\s*\\)", "< $1 <$2> >");

        // exploit matrix structure
        text = text.replaceAll("([\\d | \\s])\\(\\s*\\(", "$1" + SPACER + MATRIX_START);
        text = text.replaceAll("\\)\\s*\\(", MATRIX_DELIMITER);

        // exploit list structure
        /* a parenthesis preceded by whatever AND at least a space */
        text = text.replaceAll("(?<=[^\\]])\\s+\\(", SPACER + LIST_START);
        /* a parenthesis preceded by a space+digit OR digit+digit */
        text = text.replaceAll("(?<=\\W\\d)\\(", SPACER + LIST_START);

        if (VERBOSE)
            System.out.println(text);

        parseDictionary(text);
    }

    protected void parseDictionary(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text, "};{!&", true);
        Stack<String> stack = new Stack<String>();
        readDictionary(tokenizer, stack);
        if (VERBOSE)
            System.out.println("##################################\n" + toString());
        linkResolver.resolve(dictionary);
    }

    void readDictionary(StringTokenizer st, Stack<String> stack) {
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            token = token.trim(); // tolgo gli spazi
            stack.push(token);// metto nella pila

            if (stack.peek().equals(DICTIONARY_START)) {
                stack.pop();
                String name = stack.peek();
                Dictionary d = new Dictionary(name);
                if (VERBOSE)
                    System.out.println("START DICTIONARY: " + name);

                new DictionaryReader(d).readDictionary(st, stack);

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
                stack.pop();
                String name = stack.pop();
                if (VERBOSE)
                    System.out.println("FINE DICTIONARY: " + name);
                return;
            } else if (stack.peek().equals(FIELD_END)) {
                stack.pop();
                String field = stack.pop();
                if (VERBOSE)
                    System.out.println(field);

                if (field.startsWith("$")) {
                    dictionary.add(DICTIONARY_LINK, field);
                } else if (field.equals(")")) {
                    if (VERBOSE)
                        System.out.println("END LIST");
                    return;
                } else {
                    readField(field);
                }
            } else if (stack.peek().equals(LIST_START)) {
                stack.pop();
                String name = stack.pop();
                if (VERBOSE)
                    System.out.println("START LIST: " + name);
                ListField list = new ListField(name);
                ListReader reader = new ListReader(list, dictionary);
                reader.readList(st, stack);
                if (!reader.isSimpleList()) {
                    dictionary.add(list);
                }
            } else if (stack.peek().equals(MATRIX_START)) {
                stack.pop();
                String name = stack.pop();
                if (VERBOSE)
                    System.out.println("START MATRIX: " + name);
                FieldElement matrix = new FieldElement(name, "");
                MatrixReader reader = new MatrixReader(matrix, dictionary);
                reader.readMatrix(st, stack);
            }
        }
    }

    protected void readField(String field) {
        try {
            dictionary.add(new DimensionedScalar(field));
        } catch (DictionaryException e) {
            int splitIndex = field.indexOf(SPACER);
            if (splitIndex < 0) {
                String key = field.trim();
                String value = "";
                dictionary.add(key, value);
            } else {
                String key = field.substring(0, splitIndex).trim();
                String value = field.substring(splitIndex).trim();

                // System.out.println("DictionaryReader.readField() FIELD: "+field+" -> K: "+key+", V: "+value);

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
        return key.startsWith("\"") && key.endsWith("\"") && key.contains(MATRIX_DELIMITER);
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

}
