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

package de.darkblue.dcpu.parser.instructions.operands;

import de.darkblue.dcpu.parser.instructions.Operand;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class AddressDereferencedOperand implements Operand {

    private final int address;

    public AddressDereferencedOperand(int address) {
        this.address = address;
    }
    
    @Override
    public int getOperandCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "[" + address + "]";
    }

    @Override
    public boolean hasAdditionalWord() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAdditionalWord() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
