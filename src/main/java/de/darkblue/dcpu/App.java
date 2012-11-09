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
import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.DCPUCode;
import de.darkblue.dcpu.parser.Parser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

public class App {
    
    public static void main(String[] args) throws Exception {
        final String simpleASM = "SET A, 20";
        
        Parser parser = new Parser(new StringReader(simpleASM));
        DCPUCode code = parser.parse();
        
        System.out.println(code);
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        code.store(byteOut);
        
        
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        DCPU dcpu = new DCPU();
        dcpu.readRam(byteIn);
        dcpu.step();
        
        System.out.println(dcpu.getRegister(Register.A));
    }
    
}
