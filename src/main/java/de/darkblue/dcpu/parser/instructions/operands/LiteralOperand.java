/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser.instructions.operands;

import de.darkblue.dcpu.parser.instructions.Operand;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class LiteralOperand implements Operand {

    private final int value;
    
    public LiteralOperand(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int getOperandCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
