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


package eu.engys.core.project.geometry.stl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkCleanPolyData;
import vtk.vtkPolyData;
import vtk.vtkSTLReader;
import eu.engys.core.LoggerUtil;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.util.TempFolder;
import eu.engys.util.Util;
import eu.engys.util.VTKSettings;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.progress.SilentMonitor;
import eu.engys.util.ui.ExecUtil;

public class STLReader implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(STLReader.class);

    private static final long CHAR_PER_ROW = 30;
    private static final int SIZE = 8192;
    private static final int NP = 4;

    private final File sourceFile;
    private final String fileName;
    private final File tmp;
    private final ProgressMonitor monitor;

    private BufferedReader reader = null;
    private PrintWriter regionWriter = null;

	private int lineCounter = 0;
    private int counter = 0;
    private boolean Ascii;

    private List<Solid> solids;
    private List<File> regionFiles;

    private ExecutorService executor;

    public STLReader(File source, ProgressMonitor monitor) {
        this.sourceFile = source;
        this.fileName = source.getName();
        this.monitor = monitor;
        this.solids = new ArrayList<Solid>();
        this.regionFiles = new ArrayList<>();
        this.tmp = TempFolder.get(STLReader.class.getSimpleName());
    }

    @Override
    public void run() {
        try {
            initMonitor();
            initFilesIO();
            readFile();
        } catch (Exception e) {
            logAnError(e);
        } finally {
            monitor.setCurrent(null, monitor.getTotal());
            FileUtils.deleteQuietly(tmp);
        }
    }

    void initMonitor() throws IOException {
        int totalLines = count(sourceFile);
        monitor.setTotal(totalLines);
        monitor.setIndeterminate(false);
    }

	void initFilesIO() throws IOException {
	    reader = new BufferedReader(new FileReader(sourceFile), SIZE);
	}

    void logAnError(Exception e) {
        monitor.error(e.getMessage(), 1);
        logger.error("Error reading STL file ", e);
        solids.clear();
    }
    
    void readFile() throws Exception {
        if (reader.ready()) {
            String line = reader.readLine();
            detectType(line);

            try {
                executor = ExecUtil.createParallelExecutor(NP);
                if (isAscii()) {
                    logger.info("Read STL " + sourceFile.getName() + " [ASCII]");
                    monitor.info(sourceFile.getName() + " [ASCII]", 2);
                    
                    parseLine(line);
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        parseLine(line);
                        increaseCounter();
                    }
                    
                } else {
                    logger.info("Read STL " + sourceFile.getName() + " [BINARY]");
                    monitor.info(sourceFile.getName() + " [BINARY]", 2);
                    readBinary();
                }
                ExecUtil.awaitTermination(executor);
            } finally {
                closeFilesIO();
            }
        }
    }
    
    private void increaseCounter() {
        lineCounter++;
        if (lineCounter % 50000 == 0) 
            monitor.setCurrent(null, lineCounter);
    }

    void closeFilesIO() throws IOException {
        if (reader != null)
            reader.close();
        if (regionWriter != null)
            regionWriter.close();

        reader = null;
        regionWriter = null;
    }

    private void detectType(String line) {
        if (line.startsWith("solid")) {
            this.setAscii(true);
        } else {
            // If the first word is not "solid" then we consider the file is binary
            // Can give us problems if the comment of the binary file begins by "solid"
            this.setAscii(false);
        }
    }

    private String regionName = "";
    private File regionFile;
    private Solid solid;

    private String parseLine(String line) throws IOException {
        if (line.startsWith("solid")) {
            setValidRegionName(line);

            newRegionFile();

            startRegionWriter();
        } else if (line.startsWith("endsolid")) {
            flushRegionWriter();
        } else {
            writeln(line);
        }

        return regionName;
    }

    private File newRegionFile() {
        regionFile = new File(tmp, fileName + "_" + regionName);
        regionFiles.add(regionFile);
        solid = new Solid(regionName);
        solids.add(solid);
        return regionFile;
    }

    private void startRegionWriter() throws IOException {
        regionWriter = new PrintWriter(new FileWriter(regionFile));
        writeln("solid " + regionName);
    }

    private void writeln(String string) throws IOException {
        if (regionWriter != null)
            regionWriter.write(string.replace(',', '.') + "\n");
    }

    private void write(String string) throws IOException {
        if (regionWriter != null)
            regionWriter.write(string.replace(',', '.'));
    }

    private void flushRegionWriter() throws IOException {
        write("endsolid " + regionName);
        if (regionWriter != null) {
            regionWriter.flush();
            regionWriter.close();
            regionWriter = null;
        }
        
        executor.submit(new SolidReader(regionFile, solid));
        
        regionFile = null;
    }
    
    class SolidReader implements Runnable {
        
        private File regionFile;
        private Solid solid;

        public SolidReader(File regionFile, Solid solid) {
            this.regionFile = regionFile;
            this.solid = solid;
        }
        
        @Override
        public void run() {
            if (VTKSettings.librariesAreLoaded()) {
                vtkSTLReader reader = new vtkSTLReader();
                reader.SetFileName(regionFile.getAbsolutePath());
                reader.Update();

                this.solid.setDataSet(reader.GetOutput());

                reader.Delete();
            }
            this.regionFile.delete();            

        }
    }
    
//    public static vtkPolyData repairDataSet(vtkPolyData dataset) {
//        System.out.println("STLReader.repairDataSet() POINTS: " + dataset.GetNumberOfPoints());
//        System.out.println("STLReader.repairDataSet() LINES:  " + dataset.GetNumberOfLines());
//        System.out.println("STLReader.repairDataSet() CELLS:  " + dataset.GetNumberOfCells());
//        
//        vtkPolyData pippo = new RemoveDuplicates(dataset).execute();
//        
//        System.out.println("STLReader.repairDataSet() POINTS: " + repaired.GetNumberOfPoints());
//        System.out.println("STLReader.repairDataSet() LINES:  " + repaired.GetNumberOfLines());
//        System.out.println("STLReader.repairDataSet() CELLS:  " + repaired.GetNumberOfCells());
//        return pippo;
//    }
    
    public static vtkPolyData repairDataSet(vtkPolyData dataset) {
        System.out.println("STLReader.repairDataSet() POINTS: " + dataset.GetNumberOfPoints());
        System.out.println("STLReader.repairDataSet() LINES:  " + dataset.GetNumberOfLines());
        System.out.println("STLReader.repairDataSet() CELLS:  " + dataset.GetNumberOfCells());

        vtkCleanPolyData clean = new vtkCleanPolyData();
        // clean.ConvertLinesToPointsOff(); //def: on
        // clean.ConvertPolysToLinesOff(); //def: on
        // clean.ConvertStripsToPolysOff(); //def: on
        // clean.PieceInvariantOff(); //def: on
        // clean.PointMergingOff(); //def: on
        // clean.SetAbsoluteTolerance(0); //def: 1.0
        // clean.SetTolerance(0);//def: 0.0
        // clean.ToleranceIsAbsoluteOn(); //def: off
        clean.SetInputData(dataset);
        clean.Update();

        vtkPolyData repaired = clean.GetOutput();
        System.out.println("STLReader.repairDataSet() POINTS: " + repaired.GetNumberOfPoints());
        System.out.println("STLReader.repairDataSet() LINES:  " + repaired.GetNumberOfLines());
        System.out.println("STLReader.repairDataSet() CELLS:  " + repaired.GetNumberOfCells());
        
        return repaired;
    }
    
    private void setValidRegionName(String line) {
        int startIndex = line.indexOf(" ");
        if (startIndex < 0) {
            regionName = "solid" + counter++;
            logger.info("- " + "Found empty name. Set to " + regionName, 1);
            return;
        }
        String name = line.substring(startIndex).trim();
        if (name.isEmpty()) {
            regionName = "solid" + counter++;
            logger.info("- " + "Found empty name. Set to " + regionName, 1);
            return;
        }
        regionName = Util.replaceForbiddenCharacters(name);
        regionName = uniqueNameAmongSolids(regionName);
        if (regionName.equals(name)) {
            logger.info("- " + regionName + " found", 1);
        } else {
            logger.info(String.format("- " + "Found invalid name \"%s\". Set to \"%s\".", name, regionName), 1);
        }
    }

    private String uniqueNameAmongSolids(String name) {
        for (Solid solid : solids) {
            if (solid.getName().equals(name)) {
                return uniqueNameAmongSolids(name + counter++);
            }
        }
        return name;
    }

    private int count(File file) throws IOException {
        int i = (int) (file.length() / CHAR_PER_ROW);
        return i;
    }

    private void readBinary() throws Exception {
        ByteBuffer dataBuffer; // For reading in the correct endian
        byte[] Info = new byte[80]; // Header data
        byte[] Array_number = new byte[4]; // Holds the number of faces
        byte[] Temp_Info; // Intermediate array

        int Number_faces; // First info (after the header) on the file

        FileInputStream data = new FileInputStream(sourceFile);

        // First 80 bytes aren't important
        if (80 != data.read(Info)) {
            // File is incorrect
            // System.out.println("Format Error: 80 bytes expected");
            data.close();
            throw new Exception("Incorrect Format");
        } else {
            // We must first read the number of faces -> 4 bytes int
            // It depends on the endian so..

            data.read(Array_number); // We get the 4 bytes
            dataBuffer = ByteBuffer.wrap(Array_number); // ByteBuffer for reading correctly the int
            dataBuffer.order(ByteOrder.nativeOrder()); // Set the right order

            Number_faces = dataBuffer.getInt();

            Temp_Info = new byte[50 * Number_faces]; // Each face has 50 bytes of data

            data.read(Temp_Info); // We get the rest of the file

            dataBuffer = ByteBuffer.wrap(Temp_Info); // Now we have all the data in this ByteBuffer
            dataBuffer.order(ByteOrder.nativeOrder());

            // We can create that array directly as we know how big it's going to be
            // coordArray = new Point3f[Number_faces * 3]; // Each face has 3 vertices
            // normArray = new Vector3f[Number_faces];

            setValidRegionName("");

            // we create an ascii file -> an stl surface
            
            newRegionFile();

            startRegionWriter();

            int[] stripCounts = new int[Number_faces];
            for (int i = 0; i < Number_faces; i++) {
                stripCounts[i] = 3;
                try {
                    readFacetB(dataBuffer, i);
                    // After each facet there are 2 bytes without information
                    // In the last iteration we don't have to skip those bytes..
                    if (i != Number_faces - 1) {
                        dataBuffer.get();
                        dataBuffer.get();
                    }
                } catch (IOException e) {
                    // Quit
                    System.out.println("Format Error: iteration number " + i);
                    data.close();
                    throw new Exception("Incorrect Format");
                }
            }
            flushRegionWriter();
        }

        data.close();
    }

    private void readFacetB(ByteBuffer in, int index) throws IOException {
        Vector3f normal = new Vector3f(in.getFloat(), in.getFloat(), in.getFloat());
        Point3f vertex1 = new Point3f(in.getFloat(), in.getFloat(), in.getFloat());
        Point3f vertex2 = new Point3f(in.getFloat(), in.getFloat(), in.getFloat());
        Point3f vertex3 = new Point3f(in.getFloat(), in.getFloat(), in.getFloat());

        /*
         * facet normal -1 0 0 
         *   outer loop 
         *     vertex 5 15 10 
         *     vertex 5 5 10 
         *     vertex 5 15 15 
         *   endloop 
         * endfacet
         */
        writeln(String.format("facet normal %f %f %f", normal.x, normal.y, normal.z));
        writeln("  outer loop");
        writeln(String.format("    vertex %f %f %f", vertex1.x, vertex1.y, vertex1.z));
        writeln(String.format("    vertex %f %f %f", vertex2.x, vertex2.y, vertex2.z));
        writeln(String.format("    vertex %f %f %f", vertex3.x, vertex3.y, vertex3.z));
        writeln("  endloop");
        writeln("endfacet");
    }

    public List<Solid> getSolids() {
        return solids;
    }

    public boolean isAscii() {
        return this.Ascii;
    }

    public void setAscii(boolean b) {
        this.Ascii = b;
    }

    public static void main(String[] args) {
        LoggerUtil.initTestLogger(Level.DEBUG);
        VTKSettings.LoadAllNativeLibraries();
        new STLReader(new File("/home/stefano/ENGYS/examples/STL/plateExchanger.stl"), new SilentMonitor()).run();
        
//        long fileLenght = 40 * 1024 * 1024;
//        int i = (int) (fileLenght / CHAR_PER_ROW);
//        System.out.println(fileLenght + " Byte -> " + i + " rows");

//        System.out.println("**************");
//        System.out.println("*   SERIAL   *");
//        System.out.println("**************");
//        NP = 1;
//        for (int i=0; i<10; i++) {
//            long start = System.currentTimeMillis();
//            new STLReader(new File("/home/stefano/ENGYS/examples/STL/Bravo/car.stl"), new SilentMonitor()).run();
//            System.err.println("Time: " + (System.currentTimeMillis() - start)/1000.0  );
//        }
//        NP = 4;
//        System.out.println("**************");
//        System.out.println("* PARALLEL "+NP+" *");
//        System.out.println("**************");
//        for (int i=0; i<10; i++) {
//            long start = System.currentTimeMillis();
//            new STLReader(new File("/home/stefano/ENGYS/examples/STL/Bravo/car.stl"), new SilentMonitor()).run();
//            System.err.println("Time: " + (System.currentTimeMillis() - start)/1000.0  );
//        }
    }
}
