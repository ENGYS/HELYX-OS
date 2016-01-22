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

package eu.engys.util;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.net.util.Base64;

import com.jcraft.jsch.jce.Random;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

public final class Util {

    public enum ScriptStyle {
        WINDOWS, LINUX;
    }

    private static ScriptStyle scriptStyle = ScriptStyle.LINUX;

    public static void initScriptStyle() {
        scriptStyle = isWindows() ? ScriptStyle.WINDOWS : ScriptStyle.LINUX;
    }

    public static boolean isWindowsScriptStyle() {
        return scriptStyle == ScriptStyle.WINDOWS;
    }

    public static boolean isUnixScriptStyle() {
        return scriptStyle == ScriptStyle.LINUX;
    }

    public static void setScriptStyle(ScriptStyle scriptStyle) {
        Util.scriptStyle = scriptStyle;
    }

    public static String getTrimmedSingleSpaceLine(String string) {
        return string.trim().replaceAll("\\s+", " ");
    }

    public static final String getStringFromList(List<String> list, int itemsPerRow) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i == list.size() - 1) {
                sb.append("]");
            } else {
                sb.append(", ");
                if ((i + 1) % itemsPerRow == 0) {
                    sb.append("\n");
                }
            }

        }
        return sb.toString();
    }

    public static String replaceForbiddenCharacters(String name) {
        char[] charArray = name.toCharArray();
        if (Character.isDigit(charArray[0]))
            charArray[0] = '_';
        for (int i = 0; i < charArray.length; i++) {
            if (Util.isForbidden(charArray[i])) {
                charArray[i] = '_';
            }
        }
        return new String(charArray);
    }

    public static String padWithSpaces(String string, int lenght) {
        if (string.length() > lenght) {
            return string;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            for (int i = 0; i < lenght - string.length(); i++) {
                sb.append(" ");
            }
            return sb.toString();
        }
    }

    public static String[] fromKeystoLabels(String[] keys) {
        String[] labels = new String[keys.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = fromKeyToLabel(keys[i]);
        }
        return labels;
    }

    private static String fromKeyToLabel(String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(key.charAt(0)));
        for (int i = 1; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isUpperCase(c))
                sb.append(" ");
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        // Mac
        return (os.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        // linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
    }

    public static boolean isSolaris() {
        String os = System.getProperty("os.name").toLowerCase();
        // Solaris
        return (os.indexOf("sunos") >= 0);
    }

    public static int[] getFactorsFor(int np) {
        int cubeRoot = (int) Math.ceil(Math.cbrt(np));
        int firstFactor = 1;
        int secondFactor = 1;
        int thirdFactor = 1;

        for (int i = cubeRoot; i <= np; i++) {
            if (np % i == 0) {
                firstFactor = i;
                break;
            }
        }

        int remainder = np / firstFactor;
        int squareRoot = (int) Math.ceil(Math.sqrt(remainder));

        for (int j = squareRoot; j <= remainder; j++) {
            if (remainder % j == 0) {
                secondFactor = j;
                break;
            }
        }

        if (secondFactor > firstFactor) {
            int tmp = secondFactor;
            secondFactor = firstFactor;
            firstFactor = tmp;
        }

        thirdFactor = np / firstFactor / secondFactor;
        return new int[] { firstFactor, secondFactor, thirdFactor };
    }

    public static int getLinuxProcessId(Process proc) throws Exception {
        if (proc.getClass().getName().equals("java.lang.UNIXProcess")) {
            Field f = proc.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            int pid = f.getInt(proc);
            return pid;
        }
        return 0;
    }

    public static int getWindowsProcessId(Process proc) throws Exception {
        if (proc.getClass().getName().equals("java.lang.Win32Process") || proc.getClass().getName().equals("java.lang.ProcessImpl")) {
            /* determine the pid on windows plattforms */
            Field f = proc.getClass().getDeclaredField("handle");
            f.setAccessible(true);
            long handl = f.getLong(proc);
            Kernel32 kernel = Kernel32.INSTANCE;
            WinNT.HANDLE handle = new WinNT.HANDLE();
            handle.setPointer(Pointer.createConstant(handl));
            return kernel.GetProcessId(handle);
        }
        return 0;
    }

    public static boolean isRunning(String program) {
        if (isWindows()) {
            String listOfProcesses = getCommandOutput("tasklist");
            if (listOfProcesses == null || listOfProcesses.isEmpty()) {
                return false;
            } else {
                if (listOfProcesses.contains(program)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            String listOfProcesses = getCommandOutput("ps -f");
            // System.err.println(listOfProcesses);
            if (listOfProcesses == null || listOfProcesses.isEmpty()) {
                return false;
            } else {
                if (listOfProcesses.contains(program)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public static String getCommandOutput(String command) {
        String output = null; // the string to return

        Process process = null;
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        InputStream stream = null;

        try {
            process = Runtime.getRuntime().exec(command);

            // Get stream of the console running the command
            stream = process.getInputStream();
            streamReader = new InputStreamReader(stream);
            reader = new BufferedReader(streamReader);

            String currentLine = null; // store current line of output from the
                                       // cmd
            StringBuilder commandOutput = new StringBuilder(); // build up the
                                                               // output from
                                                               // cmd
            while ((currentLine = reader.readLine()) != null) {
                commandOutput.append(currentLine + "\n");
            }

            int returnCode = process.waitFor();
            if (returnCode == 0) {
                output = commandOutput.toString();
            }
            System.err.println(output);
        } catch (IOException e) {
            System.err.println("Cannot retrieve output of command");
            System.err.println(e);
            output = null;
        } catch (InterruptedException e) {
            System.err.println("Cannot retrieve output of command");
            System.err.println(e);
        } finally {
            // Close all inputs / readers

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    System.err.println("Cannot close stream input! " + e);
                }
            }
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    System.err.println("Cannot close stream input reader! " + e);
                }
            }
            if (reader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    System.err.println("Cannot close stream input reader! " + e);
                }
            }
        }
        // Return the output from the command - may be null if an error occured
        return output;
    }

    public static void deepCopy(double[] source, double[] target) {
        if (source == null || target == null)
            throw new IllegalArgumentException("Arrays should be not null");
        if (source.length != target.length)
            throw new IllegalArgumentException("Arrays should have same length");

        for (int i = 0; i < source.length; i++) {
            target[i] = source[i];
        }
    }

    public static void deepCopy(int[] source, int[] target) {
        if (source == null || target == null)
            throw new IllegalArgumentException("Arrays should be not null");
        if (source.length != target.length)
            throw new IllegalArgumentException("Arrays should have same length");

        for (int i = 0; i < source.length; i++) {
            target[i] = source[i];
        }

    }

    @SuppressWarnings("unchecked")
    public static <O extends Object> boolean isVarArgsNotNull(O... objs) {
        return isVarArgsNotNullAndOfSize(-1, objs);
    }

    @SuppressWarnings("unchecked")
    public static <O extends Object> boolean isVarArgsNotNullAndOfSize(int length, O... objs) {
        boolean notNull = objs != null;
        if (!notNull)
            return false;
        boolean correctSize = length < 0 ? objs.length > 0 : objs.length == length;
        if (!correctSize)
            return false;
        boolean elementsNotNull = true;
        for (O object : objs) {
            elementsNotNull &= (object != null);
        }
        return notNull && correctSize && elementsNotNull;
    }

    public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
        Map<V, K> out = new HashMap<>(map.size());
        java.util.Map.Entry<K, V> entry;
        for (Iterator<java.util.Map.Entry<K, V>> it = map.entrySet().iterator(); it.hasNext(); out.put(entry.getValue(), entry.getKey()))
            entry = it.next();

        return out;
    }

    public static Map<String, Double> sortMapByValues(Map<String, Double> passedMap, boolean descending, boolean useAbsoluteValues) {
        List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
        Collections.sort(mapKeys);

        List<Double> mapValues = new ArrayList<Double>(passedMap.values());
        if (useAbsoluteValues) {
            Collections.sort(mapValues, new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    return Double.valueOf(Math.abs(o1)).compareTo(Double.valueOf(Math.abs(o2)));
                }
            });
        } else {
            Collections.sort(mapValues);
        }
        if (descending) {
            Collections.reverse(mapValues);
        }

        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String comp1 = String.valueOf(Math.abs(passedMap.get(key)));
                String comp2 = String.valueOf(Math.abs(val));

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put(key, val);
                    break;
                }

            }

        }
        return sortedMap;
    }

    public static boolean canWrite(File folder) {
        if (!folder.canWrite()) {
            return false;
        }
        try {
            File testFile = Paths.get(folder.getAbsoluteFile().toURI()).resolve("testWrite").toFile();
            if (testFile.exists()) {
                testFile.delete();
            }
            boolean res = testFile.createNewFile();
            if (res) {
                testFile.delete();
            }
            return res;
        } catch (Exception e) {
            return false;
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String encrypt(String value) {
        if (!value.isEmpty()) {
            try {
                return Base64.encodeBase64String(value.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    public static String decrypt(String value) {
        if (!value.isEmpty()) {
            return new String(Base64.decodeBase64(value));
        }
        return value;
    }

    public static String[] getNumericSubFolders(File parentDir) {
        String[] folders = parentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                try {
                    Double.parseDouble(name);
                    return dir.isDirectory();
                } catch (NumberFormatException nfee) {
                    return false;
                }
            }
        });
        if (folders != null) {
            Arrays.sort(folders, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return Double.valueOf(s1).compareTo(Double.valueOf(s2));
                }
            });
            return folders;
        } else {
            return new String[0];
        }
    }

    public static void watchDirectoryPath(Path path) {
        // Sanity check - Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path, "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }

        // System.out.println("Watching path: " + path);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();

        // We create the new WatchService using the new try() block
        try (WatchService service = fs.newWatchService()) {

            // We register the path to the service
            // We watch for creation events
            path.register(service, ENTRY_CREATE);

            // Start the infinite polling loop
            WatchKey key = null;
            while (true) {
                key = service.take();

                // Dequeueing events
                Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue; // loop
                    } else if (ENTRY_CREATE == kind) {
                        // A new Path was created
                        // Path newPath = ((WatchEvent<Path>)
                        // watchEvent).context();
                        // Output
                        // System.out.println("New path created: " + newPath);
                    }
                }

                if (!key.reset()) {
                    break; // loop
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    public static String generateID() {
        byte[] foo = new byte[4];
        Util.rnd.fill(foo, 0, foo.length);
        String id = new String(Hex.encodeHex(foo)).toUpperCase();
        return id;
    }

    public static String getMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "";
        }
    }

    public static final Random rnd = new Random();
    public static final int WINDOWS_MAX_FILENAME_LENGTH = 200;

    public static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public static boolean isForbidden(char ch) {
        return !Character.isLetterOrDigit(ch) && " \"/\\*#$;".indexOf(ch) >= 0;
    }

}
