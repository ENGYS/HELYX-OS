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

package eu.engys.core.report.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import eu.engys.core.project.Model;

public abstract class PDFReport {

    protected Model model;

    private Document document;
    private PdfWriter writer;
    private File reportFile;

    private List<PDFPage> pages = new LinkedList<>();

    public PDFReport(Model model, File reportFile) {
        this.model = model;
        this.reportFile = reportFile;
    }

    public void create() throws Exception {
        document = new Document();
        writer = PdfWriter.getInstance(document, new FileOutputStream(reportFile));
        writer.setPageEvent(new PDFPageEvent(getFrontPageWaterMarkImage(), getWaterMarkImage()));
        document.open();
        addFooter();
        populate();
        end();
    }

    private void addFooter() {
        HeaderFooter headerFooter = new HeaderFooter(new Phrase(getFooter()), false);
        headerFooter.setAlignment(Element.ALIGN_CENTER);
        headerFooter.setBorder(Rectangle.TOP);
        document.setFooter(headerFooter);
    }

    protected abstract void populate() throws Exception;

    public abstract URL getWaterMarkImage();

    public abstract URL getFrontPageWaterMarkImage();

    public abstract String getFooter();

    protected void addPage(PDFPage page) {
        pages.add(page);
    }

    private void end() throws Exception {
        for (PDFPage page : pages) {
            document.add(page.getElement());
        }
        document.close();
        writer.close();
    }

    public Model getModel() {
        return model;
    }

    public PdfWriter getWriter() {
        return writer;
    }

    public Document getDocument() {
        return document;
    }

}
