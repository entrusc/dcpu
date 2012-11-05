/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser.instructions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class Word {
    
    private int word;

    public void setInstruction(Instruction instruction) {
        final Opcode opcode = instruction.getOpcode();
        if (opcode == null) {
            throw new IllegalArgumentException("The passed instruction needs to have an opcode");
        }
        
        if (opcode.getParameterCount() == 1) {
            this.word |= ((opcode.getOpcode() & 0b0001_1111) << 5);
        } else {
            this.word |= (opcode.getOpcode() & 0b0001_1111);
            this.word |= ((instruction.getOperandB().getOperandCode() & 0b0001_1111) << 5);
        }
        this.word |= ((instruction.getOperandA().getOperandCode() & 0b0011_1111) << 10);
    }
    
    public void store(DataOutputStream dataOut) throws IOException {
        dataOut.writeShort(word);
    }
    
    public void read(DataInputStream dataIn) throws IOException {
        this.word = dataIn.readUnsignedShort();
    }

    @Override
    public String toString() {
        return String.format("%02x%02x", ((word >> 8) & 0xFF), (word & 0xFF));
    }
    
    public String toBinaryString() {
        return fill(Integer.toBinaryString(((word >> 8) & 0xFF)), '0', 8) 
                + fill(Integer.toBinaryString((word & 0xFF)), '0', 8);
    }
    
    private static String fill(String str, char c, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - str.length(); ++i) {
            sb.append(c);
        }
        sb.append(str);
        return sb.toString();
    }
    
}
