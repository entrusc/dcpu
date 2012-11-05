/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class ParserException extends Exception {

    private Tokenizer tokenizer;
    
    public ParserException(String message, Tokenizer tokenizer) {
        super(message);
        this.tokenizer = tokenizer;
    }
    
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        String exceptionMessage = super.getMessage();
        
        message.append(exceptionMessage);
        int lineNo = tokenizer.getLineNo();
        message.append(" at ").append(lineNo).append(":").append(tokenizer.getColNo()).append("\n");
        
        String line = tokenizer.getLine();
        message.append("\t").append(String.format("%04d %s", lineNo, line)).append("\n");
        message.append("\t").append("     ");
        
        StringBuilder spacing = new StringBuilder();
        for (int i = 0; i < tokenizer.getColNo() - 1; ++i) {
            if (i < line.length() && line.charAt(i) == '\t') {
                spacing.append("\t");
            } else {
                spacing.append(" ");
            }
        }
        
        message.append(spacing);
        message.append("^").append("\n");
        message.append("\t").append("     ").append(spacing);
        message.append(exceptionMessage);
        
        return message.toString();
//        return super.getMessage();
    }
    
}
