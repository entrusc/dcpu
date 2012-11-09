/*
 * Copyright (C) 2012 Florian Frankenberger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.darkblue.dcpu;

import de.darkblue.dcpu.interpreter.DCPU;
import de.darkblue.dcpu.parser.DCPUCode;
import de.darkblue.dcpu.parser.Parser;
import de.darkblue.dcpu.view.MainFrame;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class App {
    
    public static void main(String[] args) throws Exception {
        activateLookAndFeel();
        
        final DCPU dcpu = new DCPU();
        
        final MainFrame mainFrame = new MainFrame(dcpu);
        mainFrame.setVisible(true);
        dcpu.step();
    }
    
    private static void activateLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
    }    
    
}
