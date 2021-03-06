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

package de.darkblue.dcpu.interpreter.instructions;

import de.darkblue.dcpu.interpreter.Command;
import de.darkblue.dcpu.interpreter.DCPU;
import de.darkblue.dcpu.parser.instructions.Operation;
import de.darkblue.dcpu.parser.instructions.Word;

/**
 * BOR Instruction
 * @author Florian Frankenberger
 */
@InstructionDefinition(operation=Operation.BOR)
public class BORInstruction extends Instruction {

    @Override
    public Command[] execute(final Word... operands) {
        return new Command[] {
            new Command() {

                @Override
                public void execute(DCPU dcpu) {
                    operands[0].set(operands[0].or(operands[1]));
                }
                
            }
        };
    }

}
