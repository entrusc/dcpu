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

/**
 *
 * @author Florian Frankenberger
 */
public class ParserException extends Exception {

    private Tokenizer tokenizer;
    
    public ParserException(String message, Tokenizer tokenizer) {
        super(message);
        this.tokenizer = tokenizer;
    }
    
    public int getAffectedLineNo() {
        return this.tokenizer.getLineNo();
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
    }
    
    public String getPlainMessage() {
        StringBuilder message = new StringBuilder();
        String exceptionMessage = super.getMessage();
        
        int lineNo = tokenizer.getLineNo();
        String line = tokenizer.getLine();
        message.append(String.format("%04d %s", lineNo, line)).append("\n");
        message.append("     ");
        
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
        message.append("     ").append(spacing);
        message.append(exceptionMessage);
        
        return message.toString();        
    }
    
}
