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

package de.darkblue.dcpu.parser.instructions.operands;

import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Operand;

/**
 * register (A, B, C, X, Y, Z, I or J, in that order) and PC, EX, SP
 * 
 * @author Florian Frankenberger
 */
public class RegisterOperand implements Operand {
    
    private final Register register;
    
    public RegisterOperand(Register register) {
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
                return 0x00;
            case B:
                return 0x01;
            case C:
                return 0x02;
            case X:
                return 0x03;
            case Y:
                return 0x04;
            case Z:
                return 0x05;
            case I:
                return 0x06;
            case J:
                return 0x07;
            case SP:
                return 0x1b;
            case PC:
                return 0x1c;
            case EX:
                return 0x1d;
            default:
                throw new IllegalArgumentException("operand code of register " 
                        + this.register.name() + " is unknown");
        }
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
