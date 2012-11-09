/*
 * Copyright (C) 2012 florian
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
import de.darkblue.dcpu.parser.instructions.Operation;
import de.darkblue.dcpu.parser.instructions.Word;

/**
 *
 * @author Florian Frankenberger
 */
public abstract class Instruction {

    /**
     * returns the operation associated with this instruction
     * 
     * @return 
     */
    public Operation getOperation() {
        return this.getInstructionDefinition().operation();
    }

    private InstructionDefinition getInstructionDefinition() {
        InstructionDefinition definition = 
                this.getClass().getAnnotation(InstructionDefinition.class);
        if (definition != null) {
            return definition;
        } else {
            throw new IllegalStateException("This instruction needs a definition.");
        }
    }
    
    /**
     * returns a list of commands each command will be executed
     * by the interpreter and uses one CPU cycle to process
     * 
     * @param operands 1 or 2 operands depending on the operation
     * @return 
     */
    public abstract Command[] execute(Word... operands);
    
    @Override
    public String toString() {
        return this.getOperation().toString();
    }
    
    
    
}
