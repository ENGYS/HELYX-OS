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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveUtils {

	private final static int BUFFER = 2048;

	public static final String ZIP = "zip";
	public static final String BZ2 = "bz2";
	public static final String TAR = "tar";
	public static final String GZ = "gz";

	private static final Logger logger = LoggerFactory.getLogger(ArchiveUtils.class);

	public static List<File> unarchive(File archiveFile, File destinationDir) {
		return unarchive(archiveFile, destinationDir, "");
	}

	public static List<File> unarchive(File archiveFile, File destinationDir, String prefix) {
		String fileName = archiveFile.getName();
		if (isZip(fileName)) {
			return unzip(archiveFile, destinationDir, prefix);
		}
		if (isTarGz(fileName)) {
			return untarGZ(archiveFile, destinationDir, prefix);
		}
		if (isTarBz2(fileName)) {
			return untarBZ2(archiveFile, destinationDir, prefix);
		}
		if (isGz(fileName)) {
			return unGZ(archiveFile, destinationDir, prefix);
		}
		logger.error("Unknown archive type");
		return new ArrayList<>();
	}

	/*
	 * ZIP
	 */

	public static void zip(File zipFile, File... sourceFiles) {
		try {
			ZipArchiveOutputStream zOut = new ZipArchiveOutputStream(zipFile);
			for (File file : sourceFiles) {
				addToZipArchive(zOut, file, "");
			}
			IOUtils.closeQuietly(zOut);
		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
	}

	public static List<File> unzip(File zipFile, File destinationDir) {
		return unzip(zipFile, destinationDir, "");
	}

	public static List<File> unzip(File zipFile, File destinationDir, String prefix) {
		try {
			ZipFile zip = new ZipFile(zipFile);
			return extractFromZipArchive(destinationDir, zip, prefix);
		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
		return new ArrayList<>();
	}

	/*
	 * TAR.BZ2
	 */

	public static void tarBZ2(File tarBZ2File, File... sourceFiles) {
		try {
			FileOutputStream fOut = new FileOutputStream(tarBZ2File);
			BufferedOutputStream bOut = new BufferedOutputStream(fOut);
			BZip2CompressorOutputStream bz2Out = new BZip2CompressorOutputStream(bOut);
			TarArchiveOutputStream tOut = new TarArchiveOutputStream(bz2Out);

			for (File file : sourceFiles) {
				addToTarArchive(tOut, file, "");
			}

			IOUtils.closeQuietly(tOut);
			IOUtils.closeQuietly(bz2Out);
			IOUtils.closeQuietly(bOut);
			IOUtils.closeQuietly(fOut);
		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
	}

	public static List<File> untarBZ2(File tarBZ2File, File destinationDir) {
		return untarBZ2(tarBZ2File, destinationDir, "");
	}

	public static List<File> untarBZ2(File tarBZ2File, File destinationDir, String prefix) {
		try {
			InputStream fin = new FileInputStream(tarBZ2File);
			InputStream in = new BufferedInputStream(fin);
			InputStream bz2In = new BZip2CompressorInputStream(in);
			return extractFromTarArchive(destinationDir, new TarArchiveInputStream(bz2In), prefix);
		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
		return new ArrayList<>();
	}

	/*
	 * TAR.GZ
	 */

	public static void tarGZ(File tarGZFile, File... sourceFiles) {
		try {
			FileOutputStream fOut = new FileOutputStream(tarGZFile);
			BufferedOutputStream bOut = new BufferedOutputStream(fOut);
			GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
			TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);

			for (File file : sourceFiles) {
				addToTarArchive(tOut, file, "");
			}

			IOUtils.closeQuietly(tOut);
			IOUtils.closeQuietly(gzOut);
			IOUtils.closeQuietly(bOut);
			IOUtils.closeQuietly(fOut);
		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
	}

	public static List<File> untarGZ(File tarGZFile, File destinationDir) {
		return untarGZ(tarGZFile, destinationDir, "");
	}

	public static List<File> untarGZ(File tarGZFile, File destinationDir, String prefix) {
		try {
			FileInputStream fin = new FileInputStream(tarGZFile);
			BufferedInputStream in = new BufferedInputStream(fin);
			GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
			return extractFromTarArchive(destinationDir, new TarArchiveInputStream(gzIn), prefix);
		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
		return new ArrayList<>();
	}

	/*
	 * GZ
	 */

	public static void gz(File gzFile, File sourceFile) {
		try {
			FileOutputStream fOut = new FileOutputStream(gzFile);
			BufferedOutputStream bOut = new BufferedOutputStream(fOut);
			GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
			FileInputStream fin = new FileInputStream(sourceFile);

			IOUtils.copy(fin, gzOut);

			IOUtils.closeQuietly(fin);
			IOUtils.closeQuietly(gzOut);
			IOUtils.closeQuietly(bOut);
			IOUtils.closeQuietly(fOut);
		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
	}

	public static List<File> unGZ(File tarGZFile, File destinationDir) {
		return unGZ(tarGZFile, destinationDir, "");
	}

	public static List<File> unGZ(File gzFile, File destinationDir, String prefix) {
		try {
			FileInputStream fin = new FileInputStream(gzFile);
			BufferedInputStream in = new BufferedInputStream(fin);
			GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);

			File newFile = new File(destinationDir, prefix + FilenameUtils.removeExtension(gzFile.getName()));
			copyInputStreamToOutputStream(gzIn, newFile);

			IOUtils.closeQuietly(gzIn);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(fin);

			return Arrays.asList(new File[] { newFile });

		} catch (IOException e) {
			logger.error("Error creating archive", e);
		}
		return new ArrayList<>();
	}

	/*
	 * Utils
	 */

	private static boolean isZip(String fileName) {
		return FilenameUtils.getExtension(fileName).equalsIgnoreCase(ZIP);
	}

	private static boolean isGz(String fileName) {
		String firstExtension = FilenameUtils.getExtension(fileName);
		String secondExtension = FilenameUtils.getExtension(FilenameUtils.removeExtension(fileName));
		return firstExtension.equals(GZ) && !secondExtension.equals(TAR);
	}

	private static boolean isTarGz(String fileName) {
		String firstExtension = FilenameUtils.getExtension(fileName);
		String secondExtension = FilenameUtils.getExtension(FilenameUtils.removeExtension(fileName));
		return firstExtension.equals(GZ) && secondExtension.equals(TAR);
	}

	private static boolean isTarBz2(String fileName) {
		String firstExtension = FilenameUtils.getExtension(fileName);
		String secondExtension = FilenameUtils.getExtension(FilenameUtils.removeExtension(fileName));
		return firstExtension.equals(BZ2) && secondExtension.equals(TAR);
	}

	public static boolean isArchive(File file) {
		return isZip(file.getName()) || isGz(file.getName()) || isTarGz(file.getName()) || isTarBz2(file.getName());
	}

	private static void addToZipArchive(ArchiveOutputStream zOut, File fileToAdd, String basePath) throws IOException {
		String entryName = basePath + fileToAdd.getName();

		ArchiveEntry entry = new ZipArchiveEntry(fileToAdd, entryName);
		zOut.putArchiveEntry(entry);

		if (fileToAdd.isFile()) {
			FileInputStream fInputStream = new FileInputStream(fileToAdd);
			IOUtils.copy(fInputStream, zOut);
			zOut.closeArchiveEntry();
			IOUtils.closeQuietly(fInputStream);
		} else {
			zOut.closeArchiveEntry();
			for (File child : fileToAdd.listFiles()) {
				addToZipArchive(zOut, child, entryName + File.separator);
			}
		}
	}

	private static void addToTarArchive(ArchiveOutputStream zOut, File fileToAdd, String basePath) throws IOException {
		String entryName = basePath + fileToAdd.getName();

		ArchiveEntry entry = new TarArchiveEntry(fileToAdd, entryName);
		zOut.putArchiveEntry(entry);

		if (fileToAdd.isFile()) {
			FileInputStream fInputStream = new FileInputStream(fileToAdd);
			IOUtils.copy(fInputStream, zOut);
			zOut.closeArchiveEntry();
			IOUtils.closeQuietly(fInputStream);
		} else {
			zOut.closeArchiveEntry();
			for (File child : fileToAdd.listFiles()) {
				addToTarArchive(zOut, child, entryName + File.separator);
			}
		}
	}

	private static List<File> extractFromTarArchive(File destinationDir, TarArchiveInputStream tarIn, String prefix) throws IOException {
		List<File> extractedFiles = new ArrayList<>();
		ArchiveEntry entry = null;
		while ((entry = (ArchiveEntry) tarIn.getNextEntry()) != null) {
			File entryFile = new File(destinationDir, prefix + entry.getName());
			if (entry.isDirectory()) {
				entryFile.mkdirs();
			} else {
				entryFile.getParentFile().mkdirs();
				entryFile.createNewFile();
				copyInputStreamToOutputStream(tarIn, entryFile);

				extractedFiles.add(entryFile);
			}
		}
		tarIn.close();
		return extractedFiles;
	}

	private static List<File> extractFromZipArchive(File destinationDir, ZipFile zipFile, String prefix) throws IOException {
		List<File> extractedFiles = new ArrayList<>();
		List<ZipArchiveEntry> entries = Collections.list(zipFile.getEntries());
		for (ZipArchiveEntry entry : entries) {
			File entryFile = new File(destinationDir, prefix + entry.getName());
			if (entry.isDirectory()) {
				entryFile.mkdirs();
			} else {
				entryFile.getParentFile().mkdirs();
				entryFile.createNewFile();

				InputStream is = zipFile.getInputStream(entry);
				copyInputStreamToOutputStream(is, entryFile);
				IOUtils.closeQuietly(is);

				extractedFiles.add(entryFile);
			}
		}
		zipFile.close();
		return extractedFiles;
	}

	private static void copyInputStreamToOutputStream(InputStream is, File outputFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
		IOUtils.copy(is, bos);
		IOUtils.closeQuietly(bos);
	}

}
