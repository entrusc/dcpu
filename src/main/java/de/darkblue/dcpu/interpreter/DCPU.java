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

import de.darkblue.dcpu.interpreter.instructions.Instruction;
import de.darkblue.dcpu.interpreter.instructions.InstructionDefinition;
import de.darkblue.dcpu.interpreter.operands.Operand;
import de.darkblue.dcpu.interpreter.operands.Operand.OperandMode;
import de.darkblue.dcpu.interpreter.operands.OperandDefinition;
import de.darkblue.dcpu.parser.instructions.Word;
import de.darkblue.dcpu.parser.instructions.WordChangeListener;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;

/**
 * A simple DCPU-16 implementation based on notch's specification
 * found at http://dcpu.com/dcpu-16/
 * 
 * @author Florian Frankenberger
 */
public class DCPU {
 
    private static final String PACKAGE_INSTRUCTIONS = "de.darkblue.dcpu.interpreter.instructions";
    private static final String PACKAGE_OPERANDS = "de.darkblue.dcpu.interpreter.operands";
    
    private static final Map<Integer, Class<? extends Instruction>> REGISTERED_INSTRUCTIONS = new HashMap<>();
    private static final Map<Integer, Class<? extends Operand>> REGISTERED_OPERANDS = new HashMap<>();
    
    private static final long DEFAULT_HZ = 16_000_000; //16 MHz
    
    static {
        final Reflections reflectionsInstructions = new Reflections(PACKAGE_INSTRUCTIONS);
        Set<Class<? extends Instruction>> instructions = reflectionsInstructions.getSubTypesOf(Instruction.class);
        for (Class<? extends Instruction> instruction : instructions) {
            final InstructionDefinition defition = instruction.getAnnotation(InstructionDefinition.class);
            if (defition != null) {
                REGISTERED_INSTRUCTIONS.put(defition.operation().getOpcode(), instruction);
            }
        }
        
        final Reflections reflectionsOperands = new Reflections(PACKAGE_OPERANDS);
        Set<Class<? extends Operand>> operands = reflectionsOperands.getSubTypesOf(Operand.class);
        for (Class<? extends Operand> operand : operands) {
            final OperandDefinition defition = operand.getAnnotation(OperandDefinition.class);
            if (defition != null) {
                for (int operandCode : defition.operandCodes()) {
                    REGISTERED_OPERANDS.put(operandCode, operand);
                }
            }
        }
    }
    
    private Word[] ram = new Word[0x10000];
    private Word[] lastReadProgram = new Word[0x10000];
    private Map<Register, Word> registers = new EnumMap<>(Register.class);
    
    private int cpuCycles = 0;
    private long lastCommandExecution = 0L;
    
    private long timePerCycle = 1_000_000_000L / DEFAULT_HZ;
    
    private final Set<MemoryListener> listeners = new HashSet<>();

    public DCPU() {
        initRam();
        initRegisters();
        
        for (int i = 0; i < lastReadProgram.length; ++i) {
            lastReadProgram[i] = Word.ZERO.clone();
        }
    }
    
    public final void initRam() {
        for (int i = 0; i < ram.length; ++i) {
            final Word position = new Word();
            position.setUnsignedInt(i);
            
            ram[i] = Word.ZERO.clone();
            
            ram[i].registerListener(new WordChangeListener() {

                @Override
                public void onValueChanged(Word word) {
                    onRamUpdated(position);
                    System.out.println("position " + position + " changed: " + getRam(position));
                }
                
            });
        }
    }
    
    private void initRegisters() {
        for (final Register register : Register.values()) {
            final Word registerWord = Word.ZERO.clone();
            
            registers.put(register, registerWord);
            
            registerWord.registerListener(new WordChangeListener() {

                @Override
                public void onValueChanged(Word word) {
                    onRegisterUpdated(register);
                }
                
            });
        }
    }
    
    private void clearRam() {
        for (int i = 0; i < ram.length; ++i) {
            ram[i].setSignedInt(0);
        }
    }
    
    private void clearRegisters() {
        for (final Register register : Register.values()) {
            registers.get(register).setSignedInt(0);
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
            //i know this is bad style: condition by 
            //exception - but I have no choice here ...
        }        
    }
    
    public int getRamSize() {
        return this.ram.length;
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
        Word instructionBinary = this.getRam(this.getPc());
        Class<? extends Instruction> instructionClass = REGISTERED_INSTRUCTIONS.get(instructionBinary.getOperationCode());
        if (instructionClass == null) {
            throw new IllegalStateException("Encountered unknown opcode: " + instructionBinary.getOperationCode());
        }
        
        Instruction instruction = instantiate(instructionClass);

        if (instruction.getOperation().getParameterCount() == 2 && !instructionBinary.hasTwoOperandsAsInstruction()) {
            throw new IllegalStateException(instruction.getOperation() + " expects 2 parameters but binary code has only 1");
        } else
            if (instruction.getOperation().getParameterCount() == 1 && instructionBinary.hasTwoOperandsAsInstruction()) {
                throw new IllegalStateException(instruction.getOperation() + " expects 1 parameter but binary code has 2");
            }
        
        final Word operandAMemoryCell = getOperandMemoryCell(instructionBinary.getOperandA(), OperandMode.MODE_OPERAND_A);
        Command[] commands;
        if (instruction.getOperation().getParameterCount() == 2) {
            final Word operandBMemoryCell = getOperandMemoryCell(instructionBinary.getOperandB(), OperandMode.MODE_OPERAND_B);
            commands = instruction.execute(operandBMemoryCell, operandAMemoryCell);
        } else {
            commands = instruction.execute(operandAMemoryCell);
        }
        
        executeCommands(commands);
    }
    
    private Word getOperandMemoryCell(int operandCode, OperandMode operandMode) {
        final Class<? extends Operand> operandClass = REGISTERED_OPERANDS.get(operandCode);
        if (operandClass == null) {
            throw new IllegalStateException("Operand " + operandCode + " unknown");
        }
        final Operand operand = instantiate(operandClass);
        operand.setValue(operandCode);
        
        final Word memoryCell = operand.getMemoryCell(this, operandMode);
        final Command command = operand.additionalCommand();
        
        executeCommand(command);
        return memoryCell;
    }
    
    private void executeCommands(Command[] commands) {
        for (Command command : commands) {
            executeCommand(command);
        }
    }
    
    private void executeCommand(Command command) {
        if (command != null) {
            command.execute(this);
            cpuCycles++;
            
            long timePassed = System.nanoTime() - lastCommandExecution;
            if (timePassed < timePerCycle) {
                try {
                    long timeToSleep = timePerCycle - timePassed;
                    Thread.sleep(timeToSleep / 100_000_000L, (int)(timeToSleep % 100_000_000));
                } catch (InterruptedException ex) {
                    //ignore
                }
            }
            
            lastCommandExecution = System.nanoTime();
        }
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
    
    /**
     * returns the word of the given register
     * 
     * @param register
     * @return 
     */
    public Word getRegister(Register register) {
        return this.registers.get(register);
    }
    
    public synchronized void registerListener(MemoryListener listener) {
        this.listeners.add(listener);
    }
    
    public synchronized void removeListener(MemoryListener listener) {
        this.listeners.remove(listener);
    }
    
    private void onRamUpdated(Word position) {
        for (MemoryListener listener : listeners) {
            listener.onRamValueChanged(this, position);
        }
    }
    
    private void onRegisterUpdated(Register register) {
        for (MemoryListener listener : listeners) {
            listener.onRegisterValueChanged(this, register);
        }
    }
    
    private static <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException("Could not instantiate " + clazz);
        }
    }
    
}
