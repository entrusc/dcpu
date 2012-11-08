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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Florian Frankenberger
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
    
    public void setInt(int integer) {
        if (integer < 0) {
            word = ~(Math.abs(integer) & 0b01111111111111111) + 1;
        } else {
            word = integer & 0b01111111111111111;
        }
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
