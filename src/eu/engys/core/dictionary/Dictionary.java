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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import eu.engys.core.dictionary.parser.ListField2;
import eu.engys.core.dictionary.parser.ThetaListField2;

public class Dictionary extends DefaultElement {

	public static boolean VERBOSE = false;

	public static final String SPACER = " ";
	public static final String TAB = "    ";

	public static final String DICTIONARY_LINK = "DICTIONARY_LINK";
	public static final String VALUE_LINK = "VALUE_LINK";
	public static final String VALUE_UNIFORM_LINK = "VALUE_UNIFORM_LINK";

	public static final String TYPE = "type";
	public static final String VALUE = "value";
	public static final String CONSTANT = "constant";
	public static final String NONUNIFORM = "nonuniform";
	public static final String UNIFORM = "uniform";

	private FoamFile foamFile;

	private Map<String, DefaultElement> elements = new LinkedHashMap<String, DefaultElement>();
	private List<String> keys = new ArrayList<String>();
	private List<String> genericKeys = new ArrayList<String>();
	private List<String> includeFiles = new ArrayList<String>();

	public Dictionary(String name, File file) {
		super(name);
		readDictionary(file);
	}

	public Dictionary(File file) {
		this(file.getName(), file);
	}

	public Dictionary(String name, InputStream input, DictionaryLinkResolver resolver) {
		super(name);
		readDictionary(input, resolver);
	}

	public Dictionary(String name, InputStream input) {
		super(name);
		readDictionary(input);
	}

	public Dictionary(String name) {
		super(name);
	}

	public Dictionary(Dictionary d) {
		this(d.getName(), d);
	}

	public Dictionary(String name, Dictionary d) {
		super(name);
		setFoamFile(d.getFoamFile());
		for (String key : d.keys) {
			DefaultElement el = d.elements.get(key);
			if (el instanceof Dictionary) {
				add(new Dictionary((Dictionary) el));
			} else if (el instanceof DimensionedScalar) {
				DimensionedScalar ds = (DimensionedScalar) el;
				add(new DimensionedScalar(ds));
			} else if (el instanceof FieldElement) {
				FieldElement f = (FieldElement) el;
				add(f.getName(), f.getValue());
			} else if (el instanceof ListField) {
				ListField lf = (ListField) el;
				add(lf.getName(), new ListField(lf));
			} else if (el instanceof ThetaListField2) {
				ThetaListField2 tf = (ThetaListField2) el;
				add(tf.getName(), new ThetaListField2(tf));
			} else if (el instanceof ListField2) {
				ListField2 lf = (ListField2) el;
				add(lf.getName(), new ListField2(lf));
			}
		}
		genericKeys.addAll(d.genericKeys);
	}

	public void setFoamFile(FoamFile file) {
		if (found("FoamFile"))
			remove("FoamFile");
		this.foamFile = file;
	}

	public FoamFile getFoamFile() {
		return foamFile;
	}

	public void check() throws DictionaryException {
	}

	public void include(String includeFile) {
		includeFiles.add(includeFile);
	}

	private void put(String key, DefaultElement value) {
		elements.put(key, value);
		if (!keys.contains(key))
			keys.add(key);
	}

	public void add(DefaultElement el) {
		put(el.getName(), el);
	}

	public void addGeneric(DefaultElement el) {
		String name = el.getName();
		if (!genericKeys.contains(name)) {
			genericKeys.add(name);
		}
		put(name, el);
	}

	public void add(Dictionary dictionary) {
		put(dictionary.getName(), dictionary);
	}

	public void addToList(Dictionary dictionary) {
		addToList("", dictionary);
	}

	public void addToList(String listName, Dictionary dictionary) {
		ListField list = getList(listName);
		if (list == null) {
			list = new ListField(listName);
			add(list);
		}
		list.add(dictionary);
	}

	public void addToList(String listName, FieldElement fe) {
		ListField list = getList(listName);
		if (list == null) {
			list = new ListField(listName);
			add(list);
		}
		list.add(fe);
	}

	public void add(String name, String value) {
		put(name, new FieldElement(name, value));
	}

	public void add(String name, Boolean value) {
		put(name, new FieldElement(name, String.valueOf(value)));
	}

	public void add(String name, Double value) {
		put(name, new FieldElement(name, String.valueOf(value)));
	}

	public void add(String name, Integer value) {
		put(name, new FieldElement(name, String.valueOf(value)));
	}

	public void addUniform(String name, Double value) {
		put(name, new FieldElement(name, UNIFORM + " " + String.valueOf(value)));
	}

	public void addUniform(String name, Integer value) {
		put(name, new FieldElement(name, UNIFORM + " " + String.valueOf(value)));
	}

	public void addConstant(String name, Double value) {
		put(name, new FieldElement(name, CONSTANT + " " + String.valueOf(value)));
	}

	public void addConstant(String name, Integer value) {
		put(name, new FieldElement(name, CONSTANT + " " + String.valueOf(value)));
	}

	public void addGeneric(String name, String value) {
		if (!genericKeys.contains(name)) {
			genericKeys.add(name);
		}
		put(name, new FieldElement(name, value));
	}

	public void addUniform(String name, double[] values) {
		StringBuffer sb = new StringBuffer(UNIFORM + " ( ");
		for (Double d : values) {
			sb.append(String.valueOf(d) + " ");
		}
		sb.append(")");
		this.add(name, sb.toString());
	}

	public static String toString(double[] values) {
		StringBuffer sb = new StringBuffer("( ");
		for (Double d : values) {
			sb.append(String.valueOf(d) + " ");
		}
		sb.append(")");
		return sb.toString();
	}

	public static String toString(double[][] values) {
		StringBuffer sb = new StringBuffer("( ");
		for (int i = 0; i < values.length; i++) {
			sb.append(toString(values[i]) + " ");
		}
		sb.append(")");
		return sb.toString();
	}

	public void add(String name, double[] values) {
		this.add(name, toString(values));
	}

	public void add(String name, double[][] values) {
		this.add(name, toString(values));
	}

	public void add(String name, String[][] values) {
		StringBuffer sb = new StringBuffer("( ");
		for (int i = 0; i < values.length; i++) {
			sb.append("( ");
			for (int j = 0; j < values[i].length; j++) {
				String d = values[i][j];
				sb.append(d + " ");
			}
			sb.append(") ");
		}
		sb.append(")");
		this.add(name, sb.toString());
	}

	public void add(String name, int[] values) {
		StringBuffer sb = new StringBuffer("( ");
		for (int d : values) {
			sb.append(String.valueOf(d) + " ");
		}
		sb.append(")");
		this.add(name, sb.toString());
	}

	public void add(String name, String[] values) {
		StringBuffer sb = new StringBuffer("( ");
		for (String p : values) {
			sb.append(p + " ");
		}
		sb.append(")");
		this.add(name, sb.toString());
	}

	public void add(String name, List<String> values) {
		this.add(name, values.toArray(new String[0]));
	}

	public void add(DimensionedScalar ds) {
		put(ds.getName(), ds);
	}

	public void add(String name, ListField list) {
		put(name, list);
	}

	public void add(String name, ListField2 list) {
		put(name, list);
	}

	public void add(ListField list) {
		put(list.getName(), list);
	}

	public String findKey(Finder finder) {
		for (String key : keys) {
			if (finder.accept(key)) {
				return key;
			}
		}
		return null;
	}

	public boolean found(Finder finder) {
		String key = findKey(finder);
		return key != null;
	}

	public boolean found(String name) {
		if (foundGeneric(name))
			return true;
		else
			return keys.contains(name);
	}

	private boolean foundGeneric(String name) {
		if (!genericKeys.isEmpty()) {
			for (String genericKey : genericKeys) {
				if (("\"" + name + "\"").matches(genericKey)) {
					return true;
				}
			}
		}
		return false;
	}

	DefaultElement getElement(String name) {
		if (elements.containsKey(name))
			return elements.get(name);

		if (!genericKeys.isEmpty()) {
			for (String genericKey : genericKeys) {
				if (("\"" + name + "\"").matches(genericKey)) {
					return elements.get(genericKey);
				}
			}
		}
		return null;
	}

	public boolean isDictionary(String name) {
		DefaultElement el = getElement(name);
		return (el != null && el instanceof Dictionary);
	}

	public boolean isScalar(String name) {
		DefaultElement el = getElement(name);
		return (el != null && el instanceof DimensionedScalar);
	}

	public boolean isField(String name) {
		DefaultElement el = getElement(name);
		return (el != null && el instanceof FieldElement);
	}

	public boolean isList(String name) {
		DefaultElement el = getElement(name);
		return (el != null && el instanceof ListField);
	}

	public boolean isList2(String name) {
		DefaultElement el = getElement(name);
		return (el != null && el instanceof ListField2);
	}

	public boolean isThetaList2(String name) {
		DefaultElement el = getElement(name);
		return (el != null && el instanceof ThetaListField2);
	}

	public Dictionary subDict(String name) {
		DefaultElement el = getElement(name);
		if (isDictionary(name)) {
			return (Dictionary) el;
		} else if (el != null)
			throw new DictionaryException(name + " not a dictionary");
		else
			return null;
	}

	// public ListField getList() {
	// return getList("");
	// }
	//
	// public ListField2 getList2() {
	// return getList2("");
	// }

	public ListField getList(String name) {
		DefaultElement el = getElement(name);
		if (isList(name)) {
			return (ListField) el;
		} else if (el != null)
			throw new DictionaryException(name + " not a list");
		else
			return null;
	}

	public ListField2 getList2(String name) {
		DefaultElement el = getElement(name);
		if (isList2(name)) {
			return (ListField2) el;
		} else if (el != null)
			throw new DictionaryException(name + " not a list");
		else
			return null;
	}

	public ThetaListField2 getThetaList2(String name) {
		DefaultElement el = getElement(name);
		if (isThetaList2(name)) {
			return (ThetaListField2) el;
		} else if (el != null)
			throw new DictionaryException(name + " not a theta list");
		else
			return null;
	}

	public DefaultElement lookup(Finder finder) {
		String key = findKey(finder);
		if (key == null) {
			throw new DictionaryException("Key " + key + " is NULL");
		}

		return getElement(key);
	}

	public String lookup(String key) {
		return lookupString(key);
	}

	public String lookupString(String key) {
		if (key == null) {
			throw new DictionaryException("Key " + key + " is NULL");
		}
		DefaultElement el = getElement(key);
		if (isField(key)) {
			return ((FieldElement) el).getValue();
		} else if (el != null)
			throw new DictionaryException(key + " not a field");
		else
			return null;
	}

	public boolean lookupBoolean(String name) {
		String value = lookupString(name);
		if (value != null) {
			return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("on");
		} else {
			return false;
		}
	}

	public int lookupInt(String name) {
		String value = lookupString(name);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public int lookupIntConstant(String name) {
		String value = lookupString(name).replace(CONSTANT, "").trim();
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public double lookupDouble(String name) {
		String value = lookupString(name);
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	public double lookupDoubleUniform(String name) {
		String value = lookupString(name).replace(UNIFORM, "").trim();
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	public double lookupDoubleConstant(String name) {
		String value = lookupString(name).replace(CONSTANT, "").trim();
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	public DimensionedScalar lookupScalar(String name) {
		DefaultElement el = getElement(name);
		if (el != null && el instanceof DimensionedScalar) {
			return (DimensionedScalar) el;
		} else if (el != null)
			throw new DictionaryException(name + " not a dimensioned scalar");
		else
			return null;
	}

	public static String[] toArray(String value) {
		if (value.startsWith("(") && value.endsWith(")")) {
			value = value.replace("(", "").replace(")", "").trim();
			// value = value.substring(1, value.length()-1);
			if (value.isEmpty()) {
				return new String[0];
			}
			String[] values = value.split(SPACER);
			// System.out.println("Dictionary.lookupArray() "+Arrays.toString(values));
			return values;
		} else
			throw new DictionaryException("'" + value + "' not an array");
	}

	public String[] lookupArray(String name) {
		DefaultElement el = getElement(name);
		if (el != null && el instanceof FieldElement) {
			String value = ((FieldElement) el).getValue();
			if (value.startsWith(UNIFORM)) {
				value = value.replace(UNIFORM, "").trim();
			}
			if (value.startsWith(NONUNIFORM)) {
				return new String[] { "Infinity", "Infinity", "Infinity" };
			}

			return toArray(value);

		} else if (el != null) {
			throw new DictionaryException(name + " not a field [ " + el.getClass() + "]");
		} else {
			return null;
		}
	}

	public String[][] lookupMatrix(String name) {
		DefaultElement el = getElement(name);
		if (el != null && el instanceof FieldElement) {
			String value = ((FieldElement) el).getValue();
			if (value.startsWith(UNIFORM)) {
				value = value.replace(UNIFORM, "").trim();
			}
			if (value.startsWith("(") && value.endsWith(")")) {
				value = value.replaceAll("\\(\\s*\\(", "");
				value = value.replaceAll("\\)\\s*\\)", "");

				// is empty matrix
				if (value.trim().matches("\\(\\s*\\)")) {
					return new String[0][0];
				}

				String[] values = value.split("\\)\\s*\\(");
				String[][] matrix = new String[values.length][];
				for (int r = 0; r < values.length; r++) {
					String[] row = values[r].trim().split("\\s+");
					matrix[r] = row;
				}
				return matrix;
			} else
				throw new DictionaryException(name + " not an array");
		} else if (el != null)
			throw new DictionaryException(name + " not a field");
		else
			return null;
	}

	public String[] lookupArray2(String name) {
		DefaultElement el = getElement(name);
		if (el != null && el instanceof ListField2) {
			ListField2 list = (ListField2) el;
			return listToArray(list);
		} else if (el != null)
			throw new DictionaryException(name + " not a field");
		else
			return null;
	}

	private String[] listToArray(ListField2 list) {
		List<DefaultElement> elements = list.getListElements();

		String[] values = new String[elements.size()];

		for (int i = 0; i < elements.size(); i++) {
			DefaultElement element = elements.get(i);
			if (element instanceof FieldElement) {
				String value = ((FieldElement) element).getValue();
				values[i] = value;
			} else if (element instanceof ListField2) {
				String[] listField = listToArray((ListField2) element);
				values[i] = "(";
				for (int j = 0; j < listField.length; j++) {
					values[i] += listField[j] + " ";
				}
				values[i] += ")";
			}
		}
		return values;
	}

	public String[][] lookupMatrix2(String name) {
		DefaultElement el = getElement(name);
		if (el != null && el instanceof ListField2) {
			ListField2 list = (ListField2) el;
			return listToMatrix(list);
		} else if (el != null) {
			throw new DictionaryException(name + " not a field");
		} else {
			return null;
		}
	}

	private String[][] listToMatrix(ListField2 list) {
		List<DefaultElement> rows = list.getListElements();
		if (rows.size() > 0 && rows.get(0) instanceof ListField2) {
			ListField2 firstRow = (ListField2) rows.get(0);
			String[][] values = new String[rows.size()][firstRow.getListElements().size()];

			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i) instanceof ListField2) {
					ListField2 row = (ListField2) rows.get(i);
					values[i] = listToArray(row);
				}
			}
			return values;
		} else {
			return new String[0][0];
		}
	}

	public static double[] toDouble(String[] array) {
		double[] doubleArray = new double[array.length];
		for (int i = 0; i < doubleArray.length; i++) {
			try {
				doubleArray[i] = Double.valueOf(array[i]);
			} catch (NumberFormatException e) {
			}
		}
		return doubleArray;
	}

	public double[] lookupDoubleArray(String key) {
		String[] array = lookupArray(key);
		double[] doubleArray = new double[array.length];
		for (int i = 0; i < doubleArray.length; i++) {
			try {
				doubleArray[i] = Double.valueOf(array[i]);
			} catch (NumberFormatException e) {
			}
		}
		return doubleArray;
	}

	public double[] lookupDoubleArray(Finder finder) {
		String key = findKey(finder);
		if (key != null) {
			String[] array = lookupArray(key);
			double[] doubleArray = new double[array.length];
			for (int i = 0; i < doubleArray.length; i++) {
				try {
					doubleArray[i] = Double.valueOf(array[i]);
				} catch (NumberFormatException e) {
				}
			}
			return doubleArray;
		} else {
			throw new DictionaryException(key + " not a field");
		}
	}

	public double[] lookupDoubleArray2(String name) {
		String[] array = lookupArray2(name);
		double[] doubleArray = new double[array.length];
		for (int i = 0; i < doubleArray.length; i++) {
			try {
				doubleArray[i] = Double.valueOf(array[i]);
			} catch (NumberFormatException e) {
			}
		}
		return doubleArray;
	}

	public double[][] lookupDoubleMatrix(String name) {
		String[][] matrix = lookupMatrix(name);
		double[][] doubleMatrix = new double[matrix.length][matrix.length == 0 ? 0 : matrix[0].length];
		for (int i = 0; i < doubleMatrix.length; i++) {
			for (int j = 0; j < doubleMatrix[i].length; j++) {
				try {
					doubleMatrix[i][j] = Double.valueOf(matrix[i][j]);
				} catch (NumberFormatException e) {
				}
			}
		}
		return doubleMatrix;
	}

	public List<Point3d> lookupPointList(String name) {
		double[][] matrix = lookupDoubleMatrix(name);
		List<Point3d> doubleMatrixList = new LinkedList<>();
		for (double[] ds : matrix) {
			if (ds.length == 3) {
				doubleMatrixList.add(new Point3d(ds[0], ds[1], ds[2]));
			} else {
				doubleMatrixList.add(new Point3d());
			}
		}
		return doubleMatrixList;
	}

	public double[][] lookupDoubleMatrix2(String name) {
		String[][] matrix = lookupMatrix2(name);
		double[][] doubleMatrix = new double[matrix.length][matrix[0].length];
		for (int i = 0; i < doubleMatrix.length; i++) {
			for (int j = 0; j < doubleMatrix[i].length; j++) {
				try {
					doubleMatrix[i][j] = Double.valueOf(matrix[i][j]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		return doubleMatrix;
	}

	public int[] lookupIntArray(String name) {
		String[] array = lookupArray(name);
		int[] intArray = new int[array.length];
		for (int i = 0; i < intArray.length; i++) {
			try {
				intArray[i] = Integer.valueOf(array[i]);
			} catch (NumberFormatException e) {
			}
		}
		return intArray;
	}

	public Dictionary removeDict(String name) {
		keys.remove(name);
		return (Dictionary) elements.remove(name);
	}

	public DefaultElement remove(String name) {
		keys.remove(name);
		return elements.remove(name);
	}

	public DefaultElement remove(Finder finder) {
		String key = findKey(finder);
		return remove(key);
	}

	List<String> getGenericKeys() {
		return genericKeys;
	}

	public List<String> getKeys() {
		return keys;
	}

	List<String> getIncludeFiles() {
		return includeFiles;
	}

	public boolean isEmpty() {
		return keys.isEmpty();
	}

	@Override
	public String toString() {
		return new DictionaryWriter(this).write();
	}

	public List<Dictionary> getDictionaries() {
		List<Dictionary> list = new ArrayList<Dictionary>();
		for (String key : keys) {
			DefaultElement el = elements.get(key);
			if (el instanceof Dictionary) {
				list.add((Dictionary) el);
			}
		}
		return list;
	}

	public Map<String, Dictionary> getDictionariesMap() {
		Map<String, Dictionary> map = new LinkedHashMap<String, Dictionary>();
		for (String key : keys) {
			DefaultElement el = elements.get(key);
			if (el instanceof Dictionary) {
				map.put(el.getName(), (Dictionary) el);
			}
		}
		return map;
	}

	public List<FieldElement> getFields() {
		List<FieldElement> list = new ArrayList<FieldElement>();
		for (DefaultElement el : elements.values()) {
			if (el instanceof FieldElement) {
				list.add((FieldElement) el);
			}
		}
		return list;
	}

	public List<ListField> getListFields() {
		List<ListField> list = new ArrayList<>();
		for (DefaultElement el : elements.values()) {
			if (el instanceof ListField) {
				list.add((ListField) el);
			}
		}
		return list;
	}

	public boolean hasOnlyList() {
		return !getListFields().isEmpty() && getDictionaries().isEmpty() && getFieldsMap().isEmpty();
	}

	public boolean hasOnlyList2() {
		return !getListFields2().isEmpty() && getDictionaries().isEmpty() && getFieldsMap().isEmpty();
	}

	public List<ListField2> getListFields2() {
		List<ListField2> list = new ArrayList<>();
		for (DefaultElement el : elements.values()) {
			if (el instanceof ListField2) {
				list.add((ListField2) el);
			}
		}
		return list;
	}

	public Map<String, String> getFieldsMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (DefaultElement el : elements.values()) {
			if (el instanceof FieldElement) {
				FieldElement field = (FieldElement) el;
				map.put(field.getName(), field.getValue());
			}
		}
		return map;
	}

	public void merge(Dictionary dict) {
		merge(dict, new String[0]);
	}

	public void merge(Dictionary dict, String[] keysToExclude) {
		if (dict == null)
			return;
		// System.out.println("Dictionary.merge(): "+dict);
		for (String key : dict.getKeys()) {
			if (ArrayUtils.contains(keysToExclude, key)) {
				continue;
			}

			DefaultElement ele = dict.getElement(key);
			if (ele instanceof Dictionary) {
				Dictionary d = (Dictionary) ele;
				if (found(d.getName()) && isDictionary(d.getName())) {
					subDict(d.getName()).merge(d);
				} else {
					add(new Dictionary(d.getName()));
					subDict(d.getName()).merge(d);
				}
			} else if (ele instanceof ListField) {
				ListField l = (ListField) ele;
				if (found(l.getName()) && isList(l.getName())) {
					getList(l.getName()).merge(l);
				} else {
					add(new ListField(l.getName()));
					getList(l.getName()).merge(l);
				}
			} else if (ele instanceof ThetaListField2) {
				ThetaListField2 t = (ThetaListField2) ele;
				if (found(t.getName()) && isThetaList2(t.getName())) {
					getThetaList2(t.getName()).merge(t);
				} else {
					add(new ThetaListField2(t));
				}
			} else if (ele instanceof ListField2) {
				ListField2 l = (ListField2) ele;
				if (found(l.getName()) && isList2(l.getName())) {
					getList2(l.getName()).merge(l);
				} else {
					add(new ListField2(l));
				}
			} else if (ele instanceof FieldElement) {
				FieldElement f = (FieldElement) ele;
				if (f instanceof DimensionedScalar) {
					add(new DimensionedScalar((DimensionedScalar) f));
				} else {
					add(f.getName(), f.getValue());
				}
			}
		}
		genericKeys.addAll(dict.genericKeys);
	}

	public void clear() {
		elements.clear();
		keys.clear();
	}

	/* * * * * * * * * * * * *
	 * READ/WRITE STUFF * * * * * * * * * * * *
	 */

	public void readDictionary(File file) {
		DictionaryReader reader = new DictionaryReader(this);
		reader.read(file);
	}

	protected void readDictionary(InputStream input, DictionaryLinkResolver resolver) {
		DictionaryReader reader = new DictionaryReader(this, resolver);
		reader.read(input);
	}

	protected void readDictionary(InputStream input) {
		DictionaryReader reader = new DictionaryReader(this);
		reader.read(input);
	}

	protected void readDictionaryFromString(String text) {
		DictionaryReader reader = new DictionaryReader(this);
		reader.read(text);
	}

	protected String write() {
		DictionaryWriter writer = new DictionaryWriter(this);
		return writer.write();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dictionary)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Dictionary dict = (Dictionary) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(elements, dict.elements).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).appendSuper(super.hashCode()).append(elements).toHashCode();
	}

}
