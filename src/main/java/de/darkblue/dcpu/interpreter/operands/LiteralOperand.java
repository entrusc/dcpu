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
import de.darkblue.dcpu.parser.instructions.Word;

/**
 *
 * @author Florian Frankenberger
 */
@OperandDefinition(operandCodes={0x1f, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 
    0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 
    0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3F})
public class LiteralOperand extends Operand {

    @Override
    public Word getMemoryCell(DCPU dcpu, OperandMode mode) {
        if (this.value == 0x1f) {
            dcpu.getPc().inc();
            return dcpu.getRam(dcpu.getPc());
        } else {
            final Word word = new Word();
            word.setSignedInt(value - 0x21);
            return word;
        }
    }

    @Override
    public Command additionalCommand() {
        if (this.value == 0x1f) {
            return new NopCommand(); // +1 for reading the next word
        } else {
            return null;
        }
    }

}
