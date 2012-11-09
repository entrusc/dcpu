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
import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Word;

/**
 *
 * @author Florian Frankenberger
 */
@OperandDefinition(operandCodes={0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07})
public class RegisterOperand extends Operand {

    @Override
    public Word getMemoryCell(DCPU dcpu, OperandMode mode) {
        switch (this.value) {
            case 0x00:
                return dcpu.getRegister(Register.A);
            case 0x01:
                return dcpu.getRegister(Register.B);
            case 0x02:
                return dcpu.getRegister(Register.C);
            case 0x03:
                return dcpu.getRegister(Register.X);
            case 0x04:
                return dcpu.getRegister(Register.Y);
            case 0x05:
                return dcpu.getRegister(Register.Z);
            case 0x06:
                return dcpu.getRegister(Register.I);
            case 0x07:
                return dcpu.getRegister(Register.J);
            default:
                throw new IllegalArgumentException("value is not in range");
        }
    }

    @Override
    public Command additionalCommand() {
        return null;
    }

}
