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

import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Operand;

/**
 * [register]
 * 
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class RegisterDereferencedOperand implements Operand {
    
    private final Register register;
    
    public RegisterDereferencedOperand(Register register) {
        this.register = register;
    }

    @Override
    public String toString() {
        return register.name();
    }

    @Override
    public int getOperandCode() {
        switch (this.register) {
            case A:
                return 0x08;
            case B:
                return 0x09;
            case C:
                return 0x0A;
            case X:
                return 0x0B;
            case Y:
                return 0x0C;
            case Z:
                return 0x0D;
            case I:
                return 0x0E;
            case J:
                return 0x0F;
            default:
                throw new IllegalArgumentException("operand code of register " 
                        + this.register.name() + " is unknown");
        }
    }
    
}
