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

package de.darkblue.dcpu.parser.instructions;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Florian Frankenberger
 */
public enum Operation {
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
    HWI(0x12, 1),
    
    DAT(0x00, 1); //not specified in standard but common practice
    
    private static final Map<String, Operation> OERATION_LOOKUP = new HashMap<>();
    static {
        for (Operation operation : Operation.values()) {
            OERATION_LOOKUP.put(operation.name().toLowerCase(), operation);
        }
    }
    private final int opcode;
    private final int parameterCount;

    private Operation(int opcode, int parameterCount) {
        this.opcode = opcode;
        this.parameterCount = parameterCount;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public static Operation parse(String raw) {
        return OERATION_LOOKUP.get(raw.toLowerCase());
    }

    @Override
    public String toString() {
        return this.name();
    }
    
}
