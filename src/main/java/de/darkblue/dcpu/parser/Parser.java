/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser;

import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Instruction;
import de.darkblue.dcpu.parser.instructions.Opcode;
import de.darkblue.dcpu.parser.instructions.Operand;
import de.darkblue.dcpu.parser.instructions.operands.JumpMarkOperand;
import de.darkblue.dcpu.parser.instructions.operands.LiteralOperand;
import de.darkblue.dcpu.parser.instructions.operands.RamDereferencedOperand;
import de.darkblue.dcpu.parser.instructions.operands.RegisterDereferencedOperand;
import de.darkblue.dcpu.parser.instructions.operands.RegisterOperand;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class Parser {
    
    private static final String TOKEN_LABEL_START = ":";
    
    private static final Pattern HEX_NUMBER_PATTERN = Pattern.compile("^0x[0-9]+$");
    private static final Pattern DEC_NUMBER_PATTERN = Pattern.compile("^[0-9]+$");
    
    private Tokenizer tokenizer;
    
    public Parser(Reader reader) throws IOException {
        tokenizer = new Tokenizer(reader, ';', ',', ':');
    }
    
    public DCPUCode parse() throws IOException, ParserException {
        final DCPUCode code = new DCPUCode();
        while (parseInstructionOrLabel(code)) {
        }
        return code;
    }
    
    private boolean parseInstructionOrLabel(DCPUCode code) throws IOException, ParserException {
        String token = tokenizer.next();
        
        if (token != null) {
            tokenizer.pushBack();
            if (token.equals(TOKEN_LABEL_START)) {
                parseLabel(code);
            } else {
                parseInstruction(code);
            }
        } else {
            return false;
        }
        return true;
    }
    
    private void parseLabel(DCPUCode code) throws IOException, ParserException {
        checkNextTokenIs(TOKEN_LABEL_START, true);
        final String token = getNextToken();
        
        code.addJumpMarking(token);
    }
    
    private void parseInstruction(DCPUCode code) throws ParserException, IOException {
        final Opcode opcode = parseOpcode();

        Operand firstOperand = parseOperand();
        if (firstOperand == null) {
            throw new ParserException("You have to provide at least one operand", tokenizer);
        }

        Operand secondOperand = null;
        if (checkNextTokenIs(",", false)) {
            secondOperand = parseOperand();
        }

        //now we check if the actual instruction really awaits this amount
        //of operands (= syntax check)
        final Instruction instruction = new Instruction(opcode);
        if (firstOperand != null && secondOperand != null) {
            if (opcode.getParameterCount() != 2) {
                tokenizer.pushBack();
                throw new ParserException("Instruction \"" + opcode + "\" requires " 
                        + opcode.getParameterCount() + " parameters but found only 2", tokenizer);
            }

            //with two parameters the order is reversed: OPC b, a
            instruction.setOperandA(secondOperand);
            instruction.setOperandB(firstOperand);
        } else {
            if (opcode.getParameterCount() != 1) {
                tokenizer.pushBack();
                throw new ParserException("Instruction \"" + opcode + "\" requires " 
                        + opcode.getParameterCount() + " parameters but found only 1", tokenizer);
            }
            //with one parameter we have just: OPC a
            instruction.setOperandA(firstOperand);
        }

        code.addInstruction(instruction);
    }    
    
    private Opcode parseOpcode() throws IOException, ParserException {
        final String token = getNextToken();
        
        final Opcode opcode = Opcode.parse(token);
        if (opcode == null) {
            throw new ParserException("Opcode \"" + token + "\" is unknown", tokenizer);
        }
        
        return opcode;
    }
    
    private Operand parseOperand() throws IOException, ParserException {
        final String token = getNextToken();

        final Integer tokenNumeric = getNumberToken(token);
        if (tokenNumeric == null) {
            //word
            final String operand = token.toLowerCase();
            final Register register = Register.parse(operand);

            if (register != null) {
                return new RegisterOperand(register);
            } else
                if (operand.startsWith("[") || operand.endsWith("]")) {
                    final String dereferenced = operand.substring(1, operand.length() - 2);
                    final Integer dereferencedNumeric = getNumberToken(dereferenced);
                    if (dereferencedNumeric == null) {
                        final Register dereferencedRegister = Register.parse(dereferenced.toLowerCase());
                        if (dereferencedRegister != null) {
                            return new RegisterDereferencedOperand(dereferencedRegister);
                        } else {
                            throw new ParserException("Could not parse \"" + token + "\" as operand", tokenizer);
                        }
                    } else {
                        return new RamDereferencedOperand(dereferencedNumeric);
                    }
                } else {
                    return new JumpMarkOperand(token);
                }

        } else {
            //number (literal)
            return new LiteralOperand(tokenNumeric);
        }
        
    }
    
    /**
     * retrieves a token and throws an exception if none was found
     * @return 
     */
    private String getNextToken() throws IOException, ParserException {
        return getNextToken(true);
    }
    
    private String getNextToken(boolean fail) throws IOException, ParserException {
        final String token = tokenizer.next();
        if (token == null && fail) {
            throw new ParserException("Expected a token but found EOF", tokenizer);
        }
        return token;
    }
    
    /**
     * checks if the given token is a number if not null
     * is returned
     * 
     * @param token
     * @return 
     */
    private Integer getNumberToken(String token) {
        if (HEX_NUMBER_PATTERN.matcher(token).matches()) {
            return Integer.valueOf(token, 16);
        } else
            if (DEC_NUMBER_PATTERN.matcher(token).matches()) {
                return Integer.valueOf(token, 10);
            }
        return null;
    }
    
    /**
     * checks if the next token is the given one and fails
     * if fail is true, otherwise just returns false
     * 
     * @param token
     * @return 
     */
    private boolean checkNextTokenIs(String token, boolean fail) throws IOException, ParserException {
       final String readToken = getNextToken(fail); 
       final boolean found = readToken != null && readToken.equals(token);
       
       if (!found && fail) {
           throw new ParserException("Expected to find \"" + token + "\" but found \"" + readToken + "\"", tokenizer);
       }
       return found;
    }
    
    private static Instruction newInstance(Class<Instruction> instruction) {
        try {
            return instruction.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            throw new IllegalStateException("Problem instantiating a instruction", ex);
        }
    }



    
}
