/*
 * Copyright (C) 2012 Florian Frankenberger <f.frankenberger@darkblue.de>
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Florian Frankenberger <f.frankenberger@darkblue.de>
 */
public class Tokenizer {
    
    private final Reader reader;
    private final String rawCode;
    
    private final char lineCommentChar;
    private final Set<Character> splitChars = new HashSet<>();
    
    private final List<Token> tokenHistory = new ArrayList<>();
    private final Map<Character, JoinChars> joinCharsMap = new HashMap<>();
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
    
    /**
     * registers a pair of chars where every char from the beginning to the ending
     * is wrapped together as one token. For example giving '[' and ']' would tokenize
     * [bla 123] as one token whereas without the registration it would result in
     * the tokens "[bla" and "123]".
     * 
     * @param startChar
     * @param endChar 
     */
    public void registerJoinChars(char startChar, char endChar) {
        joinCharsMap.put(startChar, new JoinChars(startChar, endChar));
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
        JoinChars joinChars = null;
        
        while (c > 32 || joinChars != null) {
            if (c == lineCommentChar) {
                c = readComment();
                continue;
            }
            
            if (joinChars != null) {
                if (((char)c) == joinChars.getEndChar()) {
                    joinChars = null;
                }
            }
            
            if (joinChars == null) {
                joinChars = this.joinCharsMap.get((char) c);
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
    
    private static class JoinChars {
        private final char startChar;
        private final char endChar;

        public JoinChars(char startChar, char endChar) {
            this.startChar = startChar;
            this.endChar = endChar;
        }

        public char getStartChar() {
            return startChar;
        }
        
        public char getEndChar() {
            return endChar;
        }
        
    }
    
}
