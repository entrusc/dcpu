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

package de.darkblue.dcpu.parser;

import de.darkblue.dcpu.parser.instructions.Instruction;
import de.darkblue.dcpu.parser.instructions.Operand;
import de.darkblue.dcpu.parser.instructions.Operation;
import de.darkblue.dcpu.parser.instructions.Word;
import de.darkblue.dcpu.parser.instructions.operands.JumpMarkOperand;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Frankenberger
 * TODO: Jump Markings are not correctly resolved :( - maybe need second pass
 */
public class DCPUCode {
    
    private List<Instruction> instructions = new ArrayList<>();
    
    private String jumpMarking = null;
    private Map<Instruction, String> jumpMarkings = new HashMap<>();

    public DCPUCode() {
    }
    
    public int addInstruction(Instruction instruction) {
        final int position = this.instructions.size();
        this.instructions.add(instruction);
        
        //do we have a jump marking for this instruction?
        if (jumpMarking != null) {
            jumpMarkings.put(instruction, jumpMarking);
            jumpMarking = null;
        }
        
        return position;
    }
    
    /**
     * sets a jumpmarking for the next added instruction
     * 
     * @param label 
     */
    public void addJumpMarking(String label) {
        this.jumpMarking = label;
    }
    
    /**
     * resolves all jump marking to literal code positions
     */
    public void resolveJumpMarkings() throws SemanticException {
        //scan for positions
        int position = 0;
        final Map<String, Integer> jumpAddresses = new HashMap<>();
        for (final Instruction instruction : instructions) {
            if (jumpMarkings.containsKey(instruction)) {
                final String aJumpMarking = jumpMarkings.get(instruction);
                jumpAddresses.put(aJumpMarking, position);
            }

            if (instruction.getOperation() != Operation.DAT) {
                position++; //for the instruction itself except for DAT
            } 
            
            if (instruction.getOperandA().hasAdditionalWord()) {
                position++; //for operandA
            }
            if (instruction.getOperation().getParameterCount() == 2) {
                if (instruction.getOperandB().hasAdditionalWord()) {
                    position++; //for operandB
                }
            }
        }
        
        //resolve jump markings
        for (final Instruction instruction : instructions) {
            resolveJumpMarking(jumpAddresses, instruction.getOperandA());
            if (instruction.getOperation().getParameterCount() > 1) {
                resolveJumpMarking(jumpAddresses, instruction.getOperandB());
            }
        }
    }
    
    private void resolveJumpMarking(Map<String, Integer> jumpAddresses, Operand operand) throws SemanticException {
        if (operand instanceof JumpMarkOperand) {
            final JumpMarkOperand jumpMarkOperand = (JumpMarkOperand) operand;
            final String aJumpMarking = jumpMarkOperand.getJumpMarking();
            final Integer jumpMarkAddress = jumpAddresses.get(aJumpMarking);

            if (jumpMarkAddress == null) {
                throw new SemanticException("Label \"" + aJumpMarking + "\" is not defined.");
            } else {
                jumpMarkOperand.resolveMarking(jumpMarkAddress);
            }
        } else {
            
        }
    }
    
    /**
     * stores this DCPUCode binary in an output stream.
     * 
     * @param out
     * @throws IOException
     * @throws SemanticException 
     */
    public void store(OutputStream out) throws IOException, SemanticException {
        resolveJumpMarkings();

        DataOutputStream dataOut = new DataOutputStream(out);
        for (final Instruction instruction : instructions) {
            if (instruction.getOperation() != Operation.DAT) {
                //DAT is not stored
                instruction.store(dataOut);
            }
            
            //store possible additional parameters
            if (instruction.getOperandA().hasAdditionalWord()) {
                Word word = new Word();
                word.setSignedInt(instruction.getOperandA().getAdditionalWord());
                word.store(dataOut);
            }            
            if (instruction.getOperation().getParameterCount() == 2) {
                if (instruction.getOperandB().hasAdditionalWord()) {
                    Word word = new Word();
                    word.setSignedInt(instruction.getOperandB().getAdditionalWord());
                    word.store(dataOut);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int lineNo = 0;
        for (Instruction instruction : instructions) {
            final String line = instruction.toString();
            final String jumpLabel = this.jumpMarkings.containsKey(instruction) 
                    ? this.jumpMarkings.get(instruction) + ":"
                    : "";
            sb.append(String.format("%04d\t%20s\t%s\n", lineNo++, jumpLabel, line));
        }
        return sb.toString();
    }
    
}
