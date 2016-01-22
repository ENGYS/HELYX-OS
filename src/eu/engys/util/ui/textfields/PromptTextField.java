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
package eu.engys.util.ui.textfields;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.DefaultFormatterFactory;

import eu.engys.util.ui.ExecUtil;

/**
 * PropmptTextField add the ability to display a message on the  
 * text field when not focused and a value is not set.
 * 
 * Auto-select the text when focused
 * See 
 * 
 */
public class PromptTextField extends JFormattedTextField {

    public static final Color HIGHLIGHT_FG = new Color(65, 90, 110);
    public static final Color HIGHLIGHT_BG = new Color(130, 180, 220, 128);
    private static final Color DEFAULT_FG = UIManager.getColor("TextField.foreground");
    private static final Color DEFAULT_BG = UIManager.getColor("TextField.background");
    private static final Color INVALID_COLOR = Color.PINK;

    public static final int NUMBER_FIELD_COLUMNS = 4;
    public static final int STRING_FIELD_COLUMNS = 10;
    
    private String prompt = "";
    
    public PromptTextField() {
        super();
    }
    
    public PromptTextField(Insets insets) {
        this();
        setMargin(insets);
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setValidColors() {
        setBackground(DEFAULT_BG);
        setForeground(DEFAULT_FG);
    }

    public void setInvalidColors() {
        setBackground(INVALID_COLOR);
        setForeground(DEFAULT_FG);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!hasFocus() && getText().isEmpty() && !prompt.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g;
            
            Object aa = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY.brighter());
            g2.drawString(prompt, 4, getFontMetrics(getFont()).getHeight());
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
        }
    }
    
    @Override
    protected void processFocusEvent(final FocusEvent e) {  
        super.processFocusEvent(e);  
        
        if (e.isTemporary()) {
            return;  
        }
        
        ExecUtil.invokeLater(new Runnable() {  
            @Override  
            public void run() {
                if (e.getID() == FocusEvent.FOCUS_GAINED) {
                    selectAll();
                } else {
                    getCaret().setDot(getCaretPosition());
                }
            }   
        });  
    }

    @Override
    public boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        return super.processKeyBinding(ks, e, condition, pressed);
    }

    @Override
    public void replaceSelection(String content) {
        String s = content;
        AbstractFormatterFactory formatterFactory = getFormatterFactory();
        if (formatterFactory != null && formatterFactory instanceof DefaultFormatterFactory) {
            DefaultFormatterFactory factory = (DefaultFormatterFactory) formatterFactory;
            AbstractFormatter displayFormat = factory.getDisplayFormatter();
            AbstractFormatter editFormat = factory.getEditFormatter();
            if (displayFormat != null && editFormat != null) {
                try {
                    Object value = displayFormat.stringToValue(getFixedinputString(content));
                    s = editFormat.valueToString(value);
                } catch (ParseException e) {
                }
            }
        } else {
        }
        super.replaceSelection(s);
    }

    /*
     * The string may need a fix. See DoubleField
     */
    protected String getFixedinputString(String content) {
        String s = content;
        return s;
    }
}
