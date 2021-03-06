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
import de.darkblue.dcpu.parser.SemanticException;
import de.darkblue.dcpu.parser.instructions.Operand;

/**
 *
 * @author Florian Frankenberger
 */
public class RegisterDereferencedAddNextWordOperand extends Operand {

    private final Register register;
    private final int value;
    
    public RegisterDereferencedAddNextWordOperand(Register register, int value) throws SemanticException {
        this.register = register;
        this.value = value;
        if (register == Register.EX) {
            throw new SemanticException("[EX + " + value + "] is not specified");
        }
        if (register == Register.PC) {
            throw new SemanticException("[PC + " + value + "] is not specified");
        }
        if (register == Register.IA) {
            throw new SemanticException("[IA + " + value + "] is not specified");
        }
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
            case SP:
                return 0x19;
            default:
                throw new IllegalArgumentException("Register " + this.register + " is unknown");
        }
    }

    @Override
    public String toString() {
        return "[" + register.name() + " + " + value + "]";
    }

    @Override
    public boolean hasAdditionalWord() {
        return true; //always stored in next word
    }

    @Override
    public int getAdditionalWord() {
        return value;
    }

}
