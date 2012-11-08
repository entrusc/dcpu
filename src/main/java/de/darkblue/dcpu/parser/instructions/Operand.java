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

package de.darkblue.dcpu.parser.instructions;

/**
 *
 * @author Florian Frankenberger
 */
public abstract class Operand {
    
    protected OperandMode operandMode;
    
    public enum OperandMode {
        MODE_OPERAND_A,
        MODE_OPERAND_B
    }
    
    /**
     * set by the parser according to if this used as operand
     * A or as operand B
     * @param operandMode 
     */
    public void setOperandMode(OperandMode operandMode) {
        this.operandMode = operandMode;
    }
    
    /**
     * returns this operand's code
     * @return 
     */
    public abstract int getOperandCode();
    
    /**
     * returns true if this operand has
     * data for an additional word
     * 
     * @return 
     */
    public abstract boolean hasAdditionalWord();
    
    /**
     * returns an additional word if
     * available
     * 
     * @return 
     */
    public abstract int getAdditionalWord();
    
}
