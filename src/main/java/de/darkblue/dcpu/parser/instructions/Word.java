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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Florian Frankenberger
 */
public class Word {

    public static final Word ZERO = new Word(0);
    public static final Word ONE = new Word(1);
    
    private int word;
    private final Set<WordChangeListener> listeners = new HashSet<>();

    public Word() {
        this(0);
    }
    
    private Word (int word) {
        this.setWord(word);
    }
    
    public void inc() {
        this.addLocal(ONE);
    }
    
    /**
     * adds the other word to this one and returns
     * a word that represents the overflow register.
     * 
     * @param other
     * @return 
     */
    public Word addLocal(Word other) {
        this.setWord(this.word + other.word);
        return checkOverOrUnderflow();
    }
    
    /**
     * adds the other word to a new(!) word
     * that is set to this word's value. This
     * new word is returned - over or underflow
     * is handled but not returned.
     * 
     * @param word
     * @return 
     */
    public Word add(Word word) {
        final Word result = new Word();
        result.set(this);
        result.addLocal(word);
        return result;
    }
    
    /**
     * subtracts the other word from this one and
     * returns a word that represents the underflow register.
     * 
     * @param other
     * @return 
     */
    public Word subtractLocal(Word other) {
        this.setWord(word - other.word);
        return checkOverOrUnderflow();
    }    
    
    /**
     * subtracts the other word from a new(!) word
     * that is set to this word's value. This
     * new word is returned - over or underflow
     * is handled but not returned.
     * 
     * @param word
     * @return 
     */
    public Word subtract(Word word) {
        final Word result = new Word();
        result.set(this);
        result.subtractLocal(word);
        return result;
    }
    
    public Word multiplyLocal(Word other) {
        this.word = word * other.word;
        final Word overflowWord = new Word();
        if (this.word > 0xFFFF) {
            overflowWord.setUnsignedInt((this.word >> 16) & 0xFFFF);
        }
        this.setUnsignedInt(word & 0xFFFF);
        return overflowWord;
    }
    
    public Word multiplySignedLocal(Word other) {
        int value = signedIntValue() * other.signedIntValue();
        final Word overflowWord = new Word();
        if (this.word > 127 || this.word < -128) {
            overflowWord.setSignedInt((this.word >> 16) & 0xFFFF);
        }
        this.setSignedInt(value);
        return overflowWord;
    }
    
    public void modLocal(Word other) {
        if (other.equals(Word.ZERO)) {
            this.set(Word.ZERO);
        } else {
            setWord(word % other.word);
        }
    }
    
    private Word checkOverOrUnderflow() {
        final Word overUnderflowRegister = new Word();
        while (this.word > 0xFFFF) {
            this.setWord(this.word - 0xFFFF);
            overUnderflowRegister.set(Word.ONE);
        }
        while (this.word < 0) {
            this.setWord(0xFFFF - this.word);
            overUnderflowRegister.set(Word.ONE);
        }
        return overUnderflowRegister;
    }
    
    public void setInstruction(Instruction instruction) {
        final Operation opcode = instruction.getOperation();
        if (opcode == null) {
            throw new IllegalArgumentException("The passed instruction needs to have an opcode");
        }
        
        if (opcode.getParameterCount() == 1) {
            this.setWord(this.word | ((opcode.getOpcode() & 0b0001_1111) << 5));
        } else {
            this.setWord(this.word | (opcode.getOpcode() & 0b0001_1111));
            this.setWord(this.word | ((instruction.getOperandB().getOperandCode() & 0b0001_1111) << 5));
        }
        this.setWord(this.word | ((instruction.getOperandA().getOperandCode() & 0b0011_1111) << 10));
    }
    
    public boolean hasTwoOperandsAsInstruction() {
        return (this.word & 0b0000_0000_0001_1111) > 0;
    }
    
    public int getOperationCode() {
        if (this.hasTwoOperandsAsInstruction()) {
            return this.word & 0b0000_0000_0001_1111;
        } else {
            return (this.word >> 5) & 0b0000_0000_0001_1111;
        }
    }
    
    public int getOperandA() {
        return (this.word >> 10) & 0b0000_0000_0011_1111;
    }
    
    public int getOperandB() {
        return (this.word >> 5) & 0b0000_0000_0001_1111;
    }
    
    public void setSignedInt(int integer) {
        if (integer < 0) {
            this.setWord(~(Math.abs(integer) & 0b0111_1111_1111_11111) + 1);
        } else {
            this.setWord(integer & 0b0111_1111_1111_11111);
        }
    }
    
    public void setUnsignedInt(int integer) {
        this.setWord(integer & 0xFFFF);
    }
    
    public void set(Word word) {
        this.setWord(word.word);
    }
    
    public void store(DataOutputStream dataOut) throws IOException {
        dataOut.writeShort(word);
    }
    
    public void read(DataInputStream dataIn) throws IOException {
        this.word = dataIn.readUnsignedShort();
    }
    
    public int signedIntValue() {
        int result = word;
        if ((word & 0b1000_0000_0000_0000) > 0) {
            //negative value
            result = ~(result - 1);
            result *= -1;
        }
        return result;
    }
    
    public int unsignedIntValue() {
        return word;
    }
    
    public synchronized void registerListener(WordChangeListener listener) {
        this.listeners.add(listener);
    }
    
    public synchronized void removeListener(WordChangeListener listener) {
        this.listeners.remove(listener);
    }
    
    private void setWord(int word) {
        boolean changed = (word != this.word);
        this.word = word;
        
        if (changed) {
            notifyOnValueUpdated();
        }
    }
    
    private synchronized void notifyOnValueUpdated() {
        for (WordChangeListener listener : listeners) {
            listener.onValueChanged(this);
        }
    }

    @Override
    public Word clone() {
        return new Word(this.word);
    }
    
    @Override
    public String toString() {
        return this.toHexString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.word;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Word other = (Word) obj;
        if (this.word != other.word) {
            return false;
        }
        return true;
    }
    
    public String toBinaryString() {
        return fill(Integer.toBinaryString(((word >> 8) & 0xFF)), '0', 8) 
                + fill(Integer.toBinaryString((word & 0xFF)), '0', 8);
    }
    
    public String toHexString() {
        return String.format("%02x%02x", ((word >> 8) & 0xFF), (word & 0xFF));
    }
    
    private static String fill(String str, char c, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - str.length(); ++i) {
            sb.append(c);
        }
        sb.append(str);
        return sb.toString();
    }

    public void dec() {
        this.subtractLocal(ONE);
    }

    public Word and(Word other) {
        final Word result = new Word();
        result.word = this.word & other.word; 
        return result;
    }
    
    public Word or(Word other) {
        final Word result = new Word();
        result.word = this.word | other.word; 
        return result;
    }    
    
    public Word xor(Word other) {
        final Word result = new Word();
        result.word = this.word ^ other.word; 
        return result;
    }

    /**
     * unsigned >. tells basically if the unsigned
     * numeric representation is bigger than the other
     * word
     * 
     * @param other
     * @return 
     */
    public boolean greaterThan(Word other) {
        return this.unsignedIntValue() > other.unsignedIntValue();
    }
    
    /**
     * signed >. tells basically if the signed
     * numeric representation is bigger than the other
     * word
     * 
     * @param other
     * @return 
     */
    public boolean greaterThanSigned(Word other) {
        return this.signedIntValue() > other.signedIntValue();
    }
    
    /**
     * unsigned <. tells basically if the unsigned
     * numeric representation is lower than the other
     * word
     * 
     * @param other
     * @return 
     */
    public boolean lowerThan(Word other) {
        return this.unsignedIntValue() < other.unsignedIntValue();
    }    
    
    /**
     * signed <. tells basically if the signed
     * numeric representation is lower than the other
     * word
     * 
     * @param other
     * @return 
     */
    public boolean lowerThanSigned(Word other) {
        return this.signedIntValue() < other.signedIntValue();
    }     
    
}
