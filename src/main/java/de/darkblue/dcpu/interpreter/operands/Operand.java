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
public abstract class Operand {
    
    public static enum OperandMode {
        MODE_OPERAND_A,
        MODE_OPERAND_B
    }
    
    protected int value;

    public void setValue(int value) {
        this.value = value;
    }
    
    /**
     * returns the memory cell that is designated by
     * this operand either to set or read from. Note that no
     * side effects other than an increasing in pc is allowed
     * here - all other side effects have to put in the additional
     * command (because it is possible they are skipped).
     * 
     * @param dcpu the dcpu interpreter
     * @param mode determines if this operand is A or B
     * @return 
     */
    public abstract Word getMemoryCell(DCPU dcpu, OperandMode mode);
    
    /**
     * returns an possible additional command that is
     * executed right after(!) the getMemoryCell call
     * 
     * @return 
     */
    public abstract Command additionalCommand(OperandMode mode);
    
}
