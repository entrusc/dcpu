/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser;

import de.darkblue.dcpu.parser.instructions.Instruction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class DCPUCode {
    
    private List<Instruction> instructions = new ArrayList<>();
    private Map<String, Integer> jumpMarkings = new HashMap<>();

    public DCPUCode() {
    }
    
    public int addInstruction(Instruction instruction) {
        final int position = this.instructions.size();
        this.instructions.add(instruction);
        return position;
    }
    
    /**
     * sets a jumpmarking for the next added instruction
     * 
     * @param label 
     */
    public void addJumpMarking(String label) {
        this.jumpMarkings.put(label, this.instructions.size());
    }
    
    /**
     * sets a jumpmarking at the given position
     * 
     * @param pos
     * @param label 
     */
    public void addJumpMarking(int pos, String label) {
        this.jumpMarkings.put(label, pos);
    }
    
    /**
     * resolves all jump marking to literal code positions
     */
    public void resolveJumpMarkings() {
        for (Instruction instruction : instructions) {
            
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int lineNo = 0;
        for (Instruction instruction : instructions) {
            sb.append(String.format("%04d\t%s\n", lineNo++, instruction.toString()));
        }
        return sb.toString();
    }
    
}
