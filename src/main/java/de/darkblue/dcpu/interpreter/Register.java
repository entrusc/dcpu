/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.interpreter;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public enum Register {
    A,
    B,
    C,
    X, 
    Y, 
    Z, 
    I, 
    J;
    
    public static Register parse(String raw) {
        raw = raw.toLowerCase();
        
        for (Register register : Register.values()) {
            if (register.name().toLowerCase().equals(raw)) {
                return register;
            }
        }
        
        return null;
    }
}
