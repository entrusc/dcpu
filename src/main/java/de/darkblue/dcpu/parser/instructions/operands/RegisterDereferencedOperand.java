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
 * [register]
 * 
 * @author Florian Frankenberger
 */
public class RegisterDereferencedOperand extends Operand {
    
    private final Register register;
    
    public RegisterDereferencedOperand(Register register) throws SemanticException {
        this.register = register;
        if (this.register == Register.PC) {
            throw new SemanticException("[PC] is not allowed");
        }
        if (this.register == Register.EX) {
            throw new SemanticException("[EX] is not allowed");
        }
        if (this.register == Register.IA) {
            throw new SemanticException("[IA] is not allowed");
        }
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
            case SP:
                return 0x19;
            default:
                throw new IllegalArgumentException("operand code of register " 
                        + this.register.name() + " is unknown");
        }
    }

    @Override
    public boolean hasAdditionalWord() {
        return false;
    }

    @Override
    public int getAdditionalWord() {
        throw new UnsupportedOperationException("No additional word.");
    }
    
}
