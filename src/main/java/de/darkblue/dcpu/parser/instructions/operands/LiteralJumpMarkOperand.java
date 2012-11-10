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

/**
 *
 * @author Florian Frankenberger
 */
public class LiteralJumpMarkOperand extends JumpMarkOperand {
    
    private LiteralOperand literalOperand;

    public LiteralJumpMarkOperand(String jumpMarking) {
        super(jumpMarking);
    }
    
    @Override
    public String getJumpMarking() {
        return jumpMarking;
    }
    
    @Override
    public void resolveMarking(int address) {
        literalOperand = new LiteralOperand(address);
        literalOperand.setOperandMode(operandMode);
        literalOperand.setForceNextWordStorage(true);
    }
    
    @Override
    public String toString() {
        return jumpMarking;
    }

    @Override
    public int getOperandCode() {
        return literalOperand.getOperandCode();
    }

    @Override
    public boolean hasAdditionalWord() {
        return true; //always true
    }

    @Override
    public int getAdditionalWord() {
        return literalOperand.getAdditionalWord();
    }
    
}
