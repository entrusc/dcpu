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
    
    private Operation operation;
    
    private Operand operandA;
    private Operand operandB;
    
    private final int lineNo;
    
    public Instruction(Operation opcode, int lineNo) {
        this.operation = opcode;
        this.lineNo = lineNo;
    }

    public Operation getOperation() {
        return operation;
    }
    
    public void setOperandA(Operand operandA) {
        this.operandA = operandA;
        if (this.getOperation() != Operation.DAT) {
            this.operandA.setOperandMode(Operand.OperandMode.MODE_OPERAND_A);
        } else {
            //fix to store the dat value directly without literal value optimization
            this.operandA.setOperandMode(Operand.OperandMode.MODE_OPERAND_B);
        }
    }
    
    public void setOperandB(Operand operandB) {
        this.operandB = operandB;
        this.operandB.setOperandMode(Operand.OperandMode.MODE_OPERAND_B);
    }

    public Operand getOperandA() {
        return operandA;
    }

    public Operand getOperandB() {
        return operandB;
    }

    public int getLineNo() {
        return lineNo;
    }
    
    @Override
    public String toString() {
        String operand = "[unknown]";
        if (operation != null) {
            operand = operation.toString();
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
