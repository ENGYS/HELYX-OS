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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import eu.engys.core.project.Model;

public class PDFUtils {

    private static final Logger logger = LoggerFactory.getLogger(PDFUtils.class);

    private static final int DEFAULT_FONT = Font.HELVETICA;
    public static final String POSTPRO = "POSTPRO";

    public static final int MEDIUM_FONT = 18;
    public static final int BIG_FONT = 25;

    public static final int SMALL_LINE_SPACING = 10;
    public static final int MEDIUM_LINE_SPACING = 20;
    public static final int LARGE_LINE_SPACING = 50;
    public static final int HUGE_LINE_SPACING = 100;

    public static final String NO_VALUE = "-";

    public static Paragraph createParagraph() {
        return createParagraph(MEDIUM_LINE_SPACING);
    }

    public static Paragraph createParagraph(int lineSpacing) {
        return createParagraph(lineSpacing, Element.ALIGN_CENTER);
    }

    public static Paragraph createParagraph(int lineSpacing, int align) {
        Paragraph paragraph = new Paragraph();
        paragraph.setLeading(lineSpacing);
        paragraph.setAlignment(align);
        paragraph.setSpacingBefore(10);
        return paragraph;
    }

    public static Phrase createLine(String text) {
        return createLine(text, MEDIUM_FONT, false, Color.BLACK);
    }

    public static Phrase createLine(String text, int fontSize) {
        return createLine(text, fontSize, false, Color.BLACK);
    }

    public static Phrase createLine(String text, int fontSize, boolean bold) {
        return createLine(text, fontSize, bold, Color.BLACK);
    }

    public static Phrase createLine(String text, int fontSize, boolean bold, Color color) {
        Font font = new Font(DEFAULT_FONT, fontSize, bold ? Font.BOLD : Font.NORMAL, color);
        Phrase phrase = new Phrase(text + "\n", font);
        return phrase;
    }

    public static PdfPTable createTableKeyValue(String[][] data) {
        PdfPTable table = new PdfPTable(data[0].length);
        for (String[] row : data) {
            String key = row[0];
            String value = row[1];

            Paragraph keyParagraph = new Paragraph(key);
            keyParagraph.getFont().setStyle(Font.BOLD);

            PdfPCell keyCell = new PdfPCell(keyParagraph);
            keyCell.setPadding(5);
            keyCell.setHorizontalAlignment(PdfPCell.LEFT);
            keyCell.setBorderWidth(1);
            keyCell.setBorderColor(Color.GRAY);
            table.addCell(keyCell);

            PdfPCell valueCell = new PdfPCell(new Paragraph(value));
            valueCell.setPadding(5);
            valueCell.setHorizontalAlignment(PdfPCell.LEFT);
            valueCell.setBorderWidth(1);
            valueCell.setBorderColor(Color.GRAY);
            table.addCell(valueCell);

        }
        return table;
    }

    public static PdfPTable createTable(String[][] data) {
        PdfPTable table = new PdfPTable(data[0].length);
        for (String[] row : data) {
            for (String value : row) {
                PdfPCell cell = new PdfPCell(new Paragraph(value));
                cell.setPadding(5);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setBorderWidth(1);
                cell.setBorderColor(Color.GRAY);
                if (table.getRows().size() % 2 == 0) {
                    cell.setBackgroundColor(new Color(245, 245, 245));
                } else {
                    cell.setBackgroundColor(new Color(230, 250, 250));
                }
                table.addCell(cell);
            }
        }
        return table;
    }

    public static Image createImage(Document document, URL url) throws BadElementException, MalformedURLException, IOException {
        Image image = Image.getInstance(url);
        image.setAlignment(Image.MIDDLE);
        float ratio = image.getWidth() / (document.getPageSize().getWidth() - 100);
        image.scaleAbsolute((image.getWidth() / ratio), (image.getHeight() / ratio));
        return image;
    }

    public static PdfPTable createImagePage(Model model, PDFImage[] images) {
        File postProFolder = new File(model.getProject().getBaseDir(), POSTPRO);
        if (postProFolder.exists()) {
            PdfPTable table = new PdfPTable(1);
            table.getDefaultCell().setBorder(Rectangle.BOX);

            for (PDFImage image : images) {
                File file = new File(postProFolder, image.getName());
                addImageToTable(table, file);
                addTitleToTable(table, image.getTitle());
                addEmptyCellToTable(table);
            }
            table.setWidthPercentage(70);
            table.setKeepTogether(true);

            return table;
        } else {
            return new PdfPTable(1);
        }
    }

    private static void addEmptyCellToTable(PdfPTable table) {
        PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        emptyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyCell);
    }

    private static void addTitleToTable(PdfPTable table, String title) {
        if (title.isEmpty()) {
            return;
        }
        PdfPCell textCell = new PdfPCell(new Phrase(title));
        textCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(textCell);
    }

    private static void addImageToTable(PdfPTable table, File imageFile) {
        try {
            if (imageFile.exists()) {
                Image image = Image.getInstance(imageFile.toURI().toURL());
                image.setAlignment(Image.MIDDLE);
                table.addCell(image);
            } else {
                PdfPCell missingImageText = new PdfPCell(new Phrase("Missing Image"));
                missingImageText.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                table.addCell(missingImageText);
            }
        } catch (BadElementException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static Paragraph createImagesBlock(Model model, PDFImage... images) {
        Paragraph p = createParagraph();
        p.add(createImagePage(model, images));
        return p;
    }
}
