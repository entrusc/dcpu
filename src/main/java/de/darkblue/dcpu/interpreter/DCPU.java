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

package de.darkblue.dcpu.interpreter;

import de.darkblue.dcpu.parser.instructions.Word;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * A simple DCPU-16 implementation based on notch's specification
 * found at http://dcpu.com/dcpu-16/
 * 
 * @author Florian Frankenberger
 */
public class DCPU {
 
    private Word[] ram = new Word[0x10000];
    private Word[] lastReadProgram = new Word[0x10000];
    private Map<Register, Word> registers = new EnumMap<>(Register.class);

    public DCPU() {
        clearRam();
        clearRegisters();
        
        for (int i = 0; i < lastReadProgram.length; ++i) {
            lastReadProgram[i] = Word.ZERO.clone();
        }
    }
    
    public final void clearRam() {
        for (int i = 0; i < ram.length; ++i) {
            ram[i] = Word.ZERO.clone();
        }
    }
    
    private void clearRegisters() {
        for (Register register : registers.keySet()) {
            registers.put(register, Word.ZERO.clone());
        }
    }

    private void resetRam() {
        for (int i = 0; i < ram.length; ++i) {
            ram[i].set(lastReadProgram[i]);
        }
    }
    
    /**
     * clears the ram and reads it from an input stream
     * 
     * @param in
     * @throws IOException 
     */
    public void readRam(InputStream in) throws IOException {
        clearRam();
        
        final Word codePosition = new Word();
        final Word word = new Word();
        final DataInputStream dataIn = new DataInputStream(in);
        try {
            word.read(dataIn);
            this.getRam(codePosition).set(word);
            this.lastReadProgram[codePosition.unsignedIntValue()].set(word);
            
            codePosition.inc();
        } catch (EOFException e) {
            //i know bad style: condition by exception - but I have no choice here ...
        }        
    }
    
    /**
     * sets all registers to 0, clears the ram and
     * sets it to the last read program or to 0 if no
     * program was loaded to ram before.
     */
    public void resetExecution() {
        clearRegisters();
        clearRam();
        
        resetRam();
    }
    
    /**
     * interpretes the next instruction
     */
    public void step() {
        Word instruction = this.getRam(this.getPc());
        
    }
    
    /**
     * the memory cell is returned directly and can therefore be
     * directly manipulated! That's also why there is no set method!
     * 
     * @param position
     * @return 
     */
    public Word getRam(Word position) {
        return this.ram[position.unsignedIntValue()];
    }

    /**
     * return the EX register as memory cell.
     * 
     * @return 
     */
    public Word getEx() {
        return this.getRegister(Register.EX);
    }

    /**
     * return the PC register as memory cell.
     * 
     * @return 
     */
    public Word getPc() {
        return this.getRegister(Register.PC);
    }

    /**
     * return the IA register as memory cell.
     * 
     * @return 
     */
    public Word getIa() {
        return this.getRegister(Register.IA);
    }

    /**
     * return the ex register as memory cell.
     * 
     * @return 
     */
    public Word getSp() {
        return this.getRegister(Register.SP);
    }
    
    public Word getRegister(Register register) {
        return this.registers.get(register);
    }
    
}
