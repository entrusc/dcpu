/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser.instructions;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public enum Opcode {
    SET(0x01, 2),
    ADD(0x02, 2),
    SUB(0x03, 2),
    MUL(0x04, 2),
    MLI(0x05, 2),
    DIV(0x06, 2),
    DVI(0x07, 2),
    MOD(0x08, 2),
    MDI(0x09, 2),
    AND(0x0A, 2),
    BOR(0x0b, 2),
    XOR(0x0c, 2),
    SHR(0x0d, 2),
    ASR(0x0e, 2),
    SHL(0x0f, 2),
    IFB(0x10, 2),
    IFC(0x11, 2),
    IFE(0x12, 2),
    IFN(0x13, 2),
    IFG(0x14, 2),
    IFA(0x15, 2),
    IFL(0x16, 2),
    IFU(0x17, 2),
    ADX(0x1a, 2),
    SBX(0x1b, 2),
    STI(0x1e, 2),
    STD(0x1f, 2),
    
    JSR(0x01, 1),
    INT(0x08, 1),
    IAG(0x09, 1),
    IAS(0x0A, 1),
    RFI(0x0B, 1),
    IAQ(0x0C, 1),
    HWN(0x10, 1),
    HWQ(0x11, 1),
    HWI(0x12, 1);
    
    private static final Map<String, Opcode> OPCODE_LOOKUP = new HashMap<>();
    static {
        for (Opcode opcode : Opcode.values()) {
            OPCODE_LOOKUP.put(opcode.name().toLowerCase(), opcode);
        }
    }
    private final int opcode;
    private final int parameterCount;

    private Opcode(int opcode, int parameterCount) {
        this.opcode = opcode;
        this.parameterCount = parameterCount;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public static Opcode parse(String raw) {
        return OPCODE_LOOKUP.get(raw.toLowerCase());
    }

    @Override
    public String toString() {
        return this.name();
    }
    
}
