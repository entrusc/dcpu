/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser.instructions.operands;

import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Operand;

/**
 * register (A, B, C, X, Y, Z, I or J, in that order)
 * 
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class RegisterOperand implements Operand {
    
    private final Register register;
    
    public RegisterOperand(Register register) {
        this.register = register;
    }

    @Override
    public String toString() {
        return register.name();
    }

    @Override
    public int getOperandCode() {
        switch (this.register) {
            case A:
                return 0x00;
            case B:
                return 0x01;
            case C:
                return 0x02;
            case X:
                return 0x03;
            case Y:
                return 0x04;
            case Z:
                return 0x05;
            case I:
                return 0x06;
            case J:
                return 0x07;
            default:
                throw new IllegalArgumentException("operand code of register " 
                        + this.register.name() + " is unknown");
        }
    }
    
}
