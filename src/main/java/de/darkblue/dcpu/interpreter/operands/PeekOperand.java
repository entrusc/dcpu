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
import de.darkblue.dcpu.parser.instructions.Word;

/**
 *
 * @author Florian Frankenberger
 */
@OperandDefinition(operandCodes={0x19})
public class PeekOperand extends Operand {

    @Override
    public Word getMemoryCell(DCPU dcpu, OperandMode mode) {
        return dcpu.getRam(dcpu.getSp());
    }

    @Override
    public Command additionalCommand(OperandMode mode) {
        return null;
    }

}
