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

package de.darkblue.dcpu.interpreter.operands;

import de.darkblue.dcpu.interpreter.Command;
import de.darkblue.dcpu.interpreter.DCPU;
import de.darkblue.dcpu.interpreter.NopCommand;
import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Word;

/**
 *
 * @author Florian Frankenberger
 */
@OperandDefinition(operandCodes={0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F})
public class RegisterDereferencedOperand extends Operand {

    @Override
    public Word getMemoryCell(DCPU dcpu, OperandMode mode) {
        Register register;
        switch (this.value) {
            case 0x08:
                register = Register.A;
                break;
            case 0x09:
                register = Register.B;
                break;
            case 0x0A:
                register = Register.C;
                break;
            case 0x0B:
                register = Register.X;
                break;
            case 0x0C:
                register = Register.Y;
                break;
            case 0x0D:
                register = Register.Z;
                break;
            case 0x0E:
                register = Register.I;
                break;
            case 0x0F:
                register = Register.J;
                break;
            default:
                throw new IllegalArgumentException("value is not in range");
        }
        
        return dcpu.getRam(dcpu.getRegister(register));
    }

    @Override
    public Command additionalCommand(OperandMode mode) {
        return new NopCommand(); //+1 cycles
    }

}
