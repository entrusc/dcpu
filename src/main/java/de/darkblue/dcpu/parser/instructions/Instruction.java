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

import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Florian Frankenberger
 */
public class Instruction {
    
    private Opcode opcode;
    
    private Operand operandA;
    private Operand operandB;
    
    public Instruction(Opcode opcode) {
        this.opcode = opcode;
    }

    public Opcode getOpcode() {
        return opcode;
    }
    
    public void setOperandA(Operand operandA) {
        this.operandA = operandA;
    }
    
    public void setOperandB(Operand operandB) {
        this.operandB = operandB;
    }

    public Operand getOperandA() {
        return operandA;
    }

    public Operand getOperandB() {
        return operandB;
    }
    
    @Override
    public String toString() {
        String operand = "[unknown]";
        if (opcode != null) {
            operand = opcode.toString();
        }
        
        if (operandB != null) {
            return operand + "\t" + operandB + ",\t" + operandA;
        } else {
            return operand + "\t" + operandA;
        }
    }

    public void store(DataOutputStream dataOut) throws IOException {
        final Word word = new Word();
        word.setInstruction(this);
        word.store(dataOut);
    }
    
}
