/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser.instructions;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
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
