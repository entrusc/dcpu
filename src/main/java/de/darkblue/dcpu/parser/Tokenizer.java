/**
 * Copyright 2012 by Darkblue Inh. Florian Frankenberger
 *
 * Be inspired by this source but please don't copy it ;)
 */
package de.darkblue.dcpu.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class Tokenizer {
    
    private final Reader reader;
    private final String rawCode;
    
    private final char lineCommentChar;
    private final Set<Character> splitChars = new HashSet<Character>();
    
    private List<Token> tokenHistory = new ArrayList<Token>();
    private int tokenHistoryPosition = -1;
    
    private int lineNo, colNo;
    private int position;
    
    public Tokenizer(Reader reader, char lineCommentChar, char... splitChars) throws IOException {
        this.rawCode = readStream(reader);
        this.reader = new StringReader(rawCode);
                
        this.lineCommentChar = lineCommentChar;
        for (char keyWord : splitChars) {
            this.splitChars.add(keyWord);
        }
        readToken();
    }

    public void pushBack() {
        if (tokenHistoryPosition >= 0) {
            tokenHistoryPosition--;
        }
    }
    
    public String current() {
        if (tokenHistoryPosition >= 0) {
            Token token = tokenHistory.get(tokenHistoryPosition);
            if (token instanceof StringToken) {
                final StringToken stringToken = (StringToken) token;
                return stringToken.token;
            }
        }
        return "";
    }
    
    /**
     * reads the next token and returns it. If the file has
     * ended null is returned
     * 
     * @return 
     */
    public String next() throws IOException {
        tokenHistoryPosition++;
        if (tokenHistoryPosition >= tokenHistory.size()) {
            tokenHistoryPosition = tokenHistory.size() - 1;
        }
        Token token = tokenHistory.get(tokenHistoryPosition);
        if (token instanceof StringToken) {
            if (tokenHistoryPosition == tokenHistory.size() - 1) {
                readToken();
            }
            return ((StringToken) token).token;
        } else {
            return null; //EOF
        }
    }
    
    private void readToken() throws IOException {
        StringBuilder currentToken = new StringBuilder();
        
        int c = skipSpaces();
        char splitChar = 0;
        int charsRead = 0;
        
        int startPosition = position;
        int startLine = lineNo;
        int startCol = colNo;
        
        while (c > 32) {
            if (c == lineCommentChar) {
                c = readComment();
                continue;
            }
            
            if (splitChars.contains((char) c)) {
                splitChar = (char) c;
                break;
            }
            
            currentToken.append((char) c);
            c = reader.read();
            charsRead++;
            updatePosition(c);
        }
        
        boolean readAnything = false;
        
        if (currentToken.length() > 0) {
            final Token token = new StringToken(currentToken.toString(), startPosition, startLine, startCol);
            this.tokenHistory.add(token);
            readAnything = true;
        }
        
        if (splitChar > 0) {
            final Token token = new StringToken(String.valueOf(splitChar), startPosition, startLine, startCol);
            this.tokenHistory.add(token);
            readAnything = true;
        }
        
        if (c == -1) {
            final Token eofToken = new EndOfFileToken(startPosition, startLine, startCol);
            this.tokenHistory.add(eofToken);
            readAnything = true;
        }
        
        if (!readAnything) {
            readToken();
        }
    }
    
    private int readComment() throws IOException {
        int c;
        do {
            c = reader.read();
            updatePosition(c);
        } while (c != 10 && c != -1);
        return c;
    }
    
    public int getLineNo() {
        if (tokenHistoryPosition > -1) {
            final Token token = tokenHistory.get(tokenHistoryPosition);
            return token.getLineNo();
        }
        return 0;
    }
    
    public int getColNo() {
        if (tokenHistoryPosition > -1) {
            final Token token = tokenHistory.get(tokenHistoryPosition);
            return token.getColNo();
        }
        return 0;
    }
    
    public String getLine() {
        if (tokenHistoryPosition > -1) {
            final Token token = tokenHistory.get(tokenHistoryPosition);
            final int tokenPosition = token.getPosition() - token.getColNo();
            return readLine(tokenPosition);
        }
        return "";
    }
    
    private int skipSpaces() throws IOException {
        int c;
        do {
            c = reader.read();
            updatePosition(c);
        } while (c <= 32 && c >= 0);
        
        return c;
    }
    
    private void updatePosition(int c) {
        colNo++;
        position++;
        if (c == 10) {
            lineNo++;
            colNo = 0;
        }
    }

    /**
     * reads a line from the given position to a \n or EOF.
     * Note that the \n is not part of the returned string.
     * 
     * @param lineStartPosition
     * @return
     */
    private String readLine(int lineStartPosition) {
        StringBuilder sb = new StringBuilder();
        
        char c;
        int currentPosition = lineStartPosition;
        do {
            c = rawCode.charAt(currentPosition);
            if (c != 10) {
                sb.append(c);
            }
            currentPosition++;
        } while (currentPosition < rawCode.length() && c != 10);
        
        return sb.toString();
    }
    
    private static String readStream(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[256];
        int read = 0;
        do {
            read = reader.read(buffer);
            if (read > 0) {
                sb.append(buffer, 0, read);
            }
        } while (read >= 0);
        
        return sb.toString();
    }
    
    private static class Token {
        private final int position;
        private final int lineNo;
        private final int colNo;

        public Token(int position, int lineNo, int colNo) {
            this.position = position;
            this.lineNo = lineNo;
            this.colNo = colNo;
        }

        public int getPosition() {
            return position;
        }

        public int getLineNo() {
            return lineNo;
        }

        public int getColNo() {
            return colNo;
        }

    }
    
    private static class EndOfFileToken extends Token {

        public EndOfFileToken(int position, int lineNo, int colNo) {
            super(position, lineNo, colNo);
        }
        
    }
    
    private static class StringToken extends Token {
        private final String token;
        
        public StringToken(String token, int position, int lineNo, int colNo) {
            super(position, lineNo, colNo);
            this.token = token;
        }
        
    }
    
}
