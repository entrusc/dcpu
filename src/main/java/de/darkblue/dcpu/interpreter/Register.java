/*
 * Copyright (C) 2012 Florian Frankenberger <f.frankenberger@darkblue.de>
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

package de.darkblue.dcpu.interpreter;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public enum Register {
    A,
    B,
    C,
    X, 
    Y, 
    Z, 
    I, 
    J,
    PC,
    SP,
    EX;
    
    public static Register parse(String raw) {
        raw = raw.toLowerCase();
        
        for (Register register : Register.values()) {
            if (register.name().toLowerCase().equals(raw)) {
                return register;
            }
        }
        
        return null;
    }
}
