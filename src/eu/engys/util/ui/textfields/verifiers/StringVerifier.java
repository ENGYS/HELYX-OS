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


package eu.engys.util.ui.textfields.verifiers;

import javax.swing.JComponent;
import javax.swing.JTextField;

import eu.engys.util.Util;
import eu.engys.util.ui.textfields.PromptTextField;

public class StringVerifier extends AbstractVerifier {

	private boolean checkEmptyStrings = true;
    private boolean checkForbidden = true;

	public StringVerifier(PromptTextField c) {
		super(c);
	}
	
	@Override
	protected boolean validationCriteria(JComponent jc) {
		try {
			String text = ((JTextField) jc).getText();
			
			if ( checkEmptyStrings && (text == null || text.isEmpty())){
				setMessage("Empty name");
				return false;
			}

			if (checkForbidden) {
			    for (char ch : text.toCharArray()) {
			        if (Util.isForbidden(ch)) {
			            setMessage("Illegal charachter: " + ch);
			            return false;
			        }
			    }			
			}
		} catch (Exception e) {
			setMessage("Error parsing string");
			return false;
		}
		return true;
	}

	public void setCheckEmptyStrings(boolean b) {
		this.checkEmptyStrings = b;
	}

    public void setCheckForbidden(boolean checkForbidden) {
        this.checkForbidden = checkForbidden;
    }
}
