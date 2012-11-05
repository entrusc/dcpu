/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser.instructions.operands;

import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Operand;

/**
 * [register]
 * 
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class RegisterDereferencedOperand implements Operand {
    
    private final Register register;
    
    public RegisterDereferencedOperand(Register register) {
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
                return 0x08;
            case B:
                return 0x09;
            case C:
                return 0x0A;
            case X:
                return 0x0B;
            case Y:
                return 0x0C;
            case Z:
                return 0x0D;
            case I:
                return 0x0E;
            case J:
                return 0x0F;
            default:
                throw new IllegalArgumentException("operand code of register " 
                        + this.register.name() + " is unknown");
        }
    }
    
}
