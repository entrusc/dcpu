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

import de.darkblue.dcpu.interpreter.instructions.InstructionDefinition;
import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Instruction;
import de.darkblue.dcpu.parser.instructions.Operation;
import de.darkblue.dcpu.parser.instructions.Operand;
import de.darkblue.dcpu.parser.instructions.operands.LiteralOperand;
import de.darkblue.dcpu.parser.instructions.operands.PickNOperand;
import de.darkblue.dcpu.parser.instructions.operands.PushPopOperand;
import de.darkblue.dcpu.parser.instructions.operands.AddressDereferencedOperand;
import de.darkblue.dcpu.parser.instructions.operands.JumpMarkDereferencedOperand;
import de.darkblue.dcpu.parser.instructions.operands.LiteralJumpMarkOperand;
import de.darkblue.dcpu.parser.instructions.operands.RegisterDereferencedAddNextWordOperand;
import de.darkblue.dcpu.parser.instructions.operands.RegisterDereferencedOperand;
import de.darkblue.dcpu.parser.instructions.operands.RegisterOperand;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.reflections.Reflections;

/**
 *
 * @author Florian Frankenberger
 */
public class Parser {
    
    private static final String TOKEN_LABEL_START = ":";
    
    private static final Pattern HEX_NUMBER_PATTERN = Pattern.compile("^0x[0-9]+$");
    private static final Pattern DEC_NUMBER_PATTERN = Pattern.compile("^[0-9]+$");
    
    private Tokenizer tokenizer;
    
    public Parser(Reader reader) throws IOException {
        tokenizer = new Tokenizer(reader, ';', ',', ':');
        tokenizer.registerJoinChars('[', ']');
    }
    
    public DCPUCode parse() throws IOException, ParserException, SemanticException {
        final DCPUCode code = new DCPUCode();
        while (parseInstructionOrLabel(code)) {
        }
        return code;
    }
    
    private boolean parseInstructionOrLabel(DCPUCode code) throws IOException, ParserException, SemanticException {
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
    
    private void parseInstruction(DCPUCode code) throws ParserException, IOException, SemanticException {
        final Operation operation = parseOperation();

        Operand firstOperand = parseOperand();
        if (firstOperand == null) {
            throw new ParserException("You have to provide at least one operand", tokenizer);
        }

        Operand secondOperand = null;
        if (checkNextTokenIs(",", false)) {
            secondOperand = parseOperand();
        } else {
            tokenizer.pushBack();
        }

        //now we check if the actual instruction really awaits this amount
        //of operands (= syntax check)
        final Instruction instruction = new Instruction(operation);
        if (firstOperand != null && secondOperand != null) {
            if (operation.getParameterCount() != 2) {
                tokenizer.pushBack();
                throw new ParserException("Instruction \"" + operation + "\" requires " 
                        + operation.getParameterCount() + " parameters but found only 2", tokenizer);
            }

            //with two parameters the order is reversed: OPC b, a
            instruction.setOperandA(secondOperand);
            instruction.setOperandB(firstOperand);
        } else {
            if (operation.getParameterCount() != 1) {
                tokenizer.pushBack();
                throw new ParserException("Instruction \"" + operation + "\" requires " 
                        + operation.getParameterCount() + " parameters but found only 1", tokenizer);
            }
            //with one parameter we have just: OPC a
            instruction.setOperandA(firstOperand);
        }

        code.addInstruction(instruction);
    }    
    
    private Operation parseOperation() throws IOException, ParserException {
        final String token = getNextToken();
        
        final Operation operation = Operation.parse(token);
        if (operation == null) {
            throw new ParserException("Operation \"" + token + "\" is unknown", tokenizer);
        }
        
        return operation;
    }
    
    private Operand parseOperand() throws IOException, ParserException, SemanticException {
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
                    final String dereferenced = operand.substring(1, operand.length() - 1);
                    final Integer dereferencedNumeric = getNumberToken(dereferenced);
                    if (dereferencedNumeric == null) {
                        final Register dereferencedRegister = Register.parse(dereferenced.toLowerCase());
                        if (dereferencedRegister != null) {
                            return new RegisterDereferencedOperand(dereferencedRegister);
                        } else {
                            if (dereferenced.contains("+") || dereferenced.contains("-")) {
                                final String[] parts = dereferenced.split("[\\+\\-]");
                                final boolean plusOperator = dereferenced.contains("+");
                                String registerRaw;
                                int numericPart;
                                Integer numericPart1 = getNumberToken(parts[0].trim());
                                Integer numericPart2 = getNumberToken(parts[1].trim());
                                if (numericPart1 != null && numericPart2 != null) {
                                    //both numeric - so arithmetic operation ;)
                                    int total = numericPart1 + (plusOperator ? +1 : -1 * numericPart2);
                                    return new AddressDereferencedOperand(total);
                                } else
                                    if (numericPart1 == null) {
                                        registerRaw = parts[0].trim();
                                        numericPart = numericPart2;
                                    } else {
                                        registerRaw = parts[1].trim();
                                        numericPart = numericPart1;
                                    }
                                
                                final Register registerPart = Register.parse(registerRaw);
                                if (registerPart != null) {
                                    return new RegisterDereferencedAddNextWordOperand(registerPart, 
                                            (plusOperator ? +1 : -1) * numericPart);
                                } else {
                                    throw new ParserException("Register \"" + registerRaw + "\" is unknown", tokenizer);
                                }
                            } else {
                                return new JumpMarkDereferencedOperand(dereferenced);
                            }
                        }
                    } else {
                        return new AddressDereferencedOperand(dereferencedNumeric);
                    }
                } else
                    if (operand.equals("push") || operand.equals("POP")) {
                        return new PushPopOperand();
                    } else 
                        if (operand.equals("pick")) {
                            final String nextToken = getNextToken();
                            final Integer nextTokenNumeric = getNumberToken(nextToken);
                            if (nextToken == null) {
                                throw new ParserException("PICK awaits one additional numeric parameter", tokenizer);
                            } else {
                                return new PickNOperand(nextTokenNumeric);
                            }
                        } else {
                            return new LiteralJumpMarkOperand(token);
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
    
    
}
