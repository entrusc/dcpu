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
public class JumpMarkOperand implements Operand {
    
    private final String jumpMarking;

    public JumpMarkOperand(String jumpMarking) {
        this.jumpMarking = jumpMarking;
    }

    @Override
    public String toString() {
        return jumpMarking;
    }

    @Override
    public int getOperandCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
