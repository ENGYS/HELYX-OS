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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class PDFPageEvent extends PdfPageEventHelper {

    private static final int FRONT_PAGE_INDEX = 1;
    private URL frontPagewatermarkImage;
    private URL watermarkImage;

    public PDFPageEvent(URL frontPagewatermarkImage, URL watermarkImage) {
        this.frontPagewatermarkImage = frontPagewatermarkImage;
        this.watermarkImage = watermarkImage;
    }
    
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            _addWatermark(writer, document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _addWatermark(PdfWriter writer, Document document) throws BadElementException, MalformedURLException, IOException, DocumentException {
        PdfContentByte canvas = writer.getDirectContentUnder();
        Image image = null;
        if (document.getPageNumber() == FRONT_PAGE_INDEX) {
            image = Image.getInstance(frontPagewatermarkImage);
        } else {
            image = Image.getInstance(watermarkImage);
        }
        image.setAlignment(Image.MIDDLE);
        float x = (document.getPageSize().getWidth() - image.getWidth()) / 2;
        float y = (document.getPageSize().getHeight() - image.getHeight()) / 2;
        if (document.getPageNumber() == FRONT_PAGE_INDEX) {
            image.setAbsolutePosition(x, y - 30);
        } else {
            image.setAbsolutePosition(x, y);
        }
        canvas.addImage(image);

    }
}
