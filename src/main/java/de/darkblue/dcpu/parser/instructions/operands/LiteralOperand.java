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

import de.darkblue.dcpu.parser.instructions.Operand;

/**
 *
 * @author Florian Frankenberger
 */
public class LiteralOperand extends Operand {

    private final int value;
    
    public LiteralOperand(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int getOperandCode() {
        if (hasAdditionalWord()) {
            return 0x1f; //literal in next word
        } else {
            return 0x20 + (value + 1); //directly stored in operand code
        }
    }

    @Override
    public boolean hasAdditionalWord() {
        //values -1 .. 30 are stored directly in the instruction (if operand is operand A)
        //otherwise the value is stored in an additional word
        return (operandMode == OperandMode.MODE_OPERAND_A && !(value >= -1 && value <= 30));
    }

    @Override
    public int getAdditionalWord() {
        return value;
    }
    
}
