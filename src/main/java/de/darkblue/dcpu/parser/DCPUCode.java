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
 */
public class DCPUCode {
    
    private List<Instruction> instructions = new ArrayList<>();
    private Map<String, Integer> jumpMarkings = new HashMap<>();
    private Map<Integer, String> jumpMarkingsReverse = new HashMap<>();

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
        int codePosition = this.instructions.size();
        this.addJumpMarking(codePosition, label);
    }
    
    /**
     * sets a jumpmarking at the given position
     * 
     * @param codePosition
     * @param label 
     */
    public void addJumpMarking(int codePosition, String label) {
        this.jumpMarkings.put(label, codePosition);
        this.jumpMarkingsReverse.put(codePosition, label);
    }
    
    /**
     * resolves all jump marking to literal code positions
     */
    public void resolveJumpMarkings() throws SemanticException {
        for (Instruction instruction : instructions) {
            final Operand operandA = instruction.getOperandA();
            resolveJumpMarking(operandA);
            
            final Operand operandB = instruction.getOperandB();
            if (operandB != null) {
                resolveJumpMarking(operandB);
            }
        }
    }
    
    private void resolveJumpMarking(Operand operand) throws SemanticException {
        if (operand instanceof JumpMarkOperand) {
            final JumpMarkOperand jumpMarkOperand = (JumpMarkOperand) operand;
            final String jumpMarking = jumpMarkOperand.getJumpMarking();
            final Integer jumpMarkAddress = this.jumpMarkings.get(jumpMarking);

            if (jumpMarkAddress == null) {
                throw new SemanticException("Label \"" + jumpMarking + "\" is not defined.");
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
        for (Instruction instruction : instructions) {
            instruction.store(dataOut);

            //store possible additional parameters
            if (instruction.getOpcode().getParameterCount() == 1) {
                if (instruction.getOperandA().hasAdditionalWord()) {
                    Word word = new Word();
                    word.setSignedInt(instruction.getOperandA().getAdditionalWord());
                    word.store(dataOut);
                }
            } else {
                if (instruction.getOperandB().hasAdditionalWord()) {
                    Word word = new Word();
                    word.setSignedInt(instruction.getOperandB().getAdditionalWord());
                    word.store(dataOut);
                }
                if (instruction.getOperandA().hasAdditionalWord()) {
                    Word word = new Word();
                    word.setSignedInt(instruction.getOperandA().getAdditionalWord());
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
            final String jumpLabel = this.jumpMarkingsReverse.containsKey(lineNo) 
                    ? this.jumpMarkingsReverse.get(lineNo) + ":"
                    : "";
            sb.append(String.format("%04d\t%20s\t%s\n", lineNo++, jumpLabel, line));
        }
        return sb.toString();
    }
    
}
