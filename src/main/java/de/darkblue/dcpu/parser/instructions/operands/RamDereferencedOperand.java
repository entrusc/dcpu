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
public class RamDereferencedOperand implements Operand {

    private final int ramAddress;

    public RamDereferencedOperand(int ramAddress) {
        this.ramAddress = ramAddress;
    }
    
    @Override
    public int getOperandCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
