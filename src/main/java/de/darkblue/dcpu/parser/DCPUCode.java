/*
 * Copyright (C) 2012 Florian Frankenberger <f.frankenberger@darkblue.de>
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
