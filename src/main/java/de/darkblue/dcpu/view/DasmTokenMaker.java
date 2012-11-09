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
package de.darkblue.dcpu.view;

import java.io.IOException;
import java.io.Reader;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.DefaultToken;
import org.fife.ui.rsyntaxtextarea.Token;

/**
 *
 * @author Florian Frankenberger
 */
public class DasmTokenMaker extends AbstractJFlexTokenMaker {

    /**
     * This character denotes the end of file
     */
    public static final int YYEOF = -1;
    /**
     * initial size of the lookahead buffer
     */
    private static final int ZZ_BUFFERSIZE = 16384;
    /**
     * lexical states
     */
    public static final int EOL_COMMENT = 1;
    public static final int YYINITIAL = 0;
    /**
     * Translates characters to character classes
     */
    private static final String ZZ_CMAP_PACKED =
            "\11\0\1\16\1\7\1\0\1\16\1\14\22\0\1\16\1\6\1\14"
            + "\1\15\1\1\1\6\1\6\1\6\2\24\1\6\1\102\1\25\1\102"
            + "\1\22\1\26\1\4\3\3\4\3\2\3\1\37\1\17\1\14\1\6"
            + "\1\14\1\6\1\15\1\41\1\42\1\43\1\56\1\44\1\70\1\67"
            + "\1\64\1\46\1\47\1\1\1\71\1\73\1\57\1\61\1\50\1\66"
            + "\1\60\1\51\1\62\1\72\1\63\1\65\1\45\1\52\1\52\1\23"
            + "\1\10\1\23\1\14\1\2\1\0\1\53\1\13\1\54\1\5\1\36"
            + "\1\33\1\100\1\27\1\34\1\55\1\1\1\35\1\101\1\74\1\75"
            + "\1\31\1\77\1\12\1\32\1\30\1\11\1\76\1\40\1\20\1\52"
            + "\1\52\1\21\1\14\1\21\1\6\uff81\0";
    /**
     * Translates characters to character classes
     */
    private static final char[] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);
    /**
     * Translates DFA states to action switch labels.
     */
    private static final int[] ZZ_ACTION = zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0 =
            "\2\0\2\1\2\2\1\1\1\3\1\1\1\4\1\5"
            + "\1\6\1\7\1\10\1\11\3\1\1\4\1\1\3\4"
            + "\1\1\2\4\2\1\2\4\5\1\1\12\1\13\1\14"
            + "\3\13\1\0\2\15\12\1\1\4\3\1\1\4\15\1"
            + "\5\0\1\16\1\17\7\0\1\20\4\0";

    private static int[] zzUnpackAction() {
        int[] result = new int[91];
        int offset = 0;
        offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result) {
        int i = 0;       /* index in packed string  */
        int j = offset;  /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    /**
     * Translates a state to a row index in the transition table
     */
    private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0 =
            "\0\0\0\103\0\206\0\311\0\u010c\0\u014f\0\u0192\0\206"
            + "\0\u01d5\0\u0218\0\u025b\0\206\0\206\0\206\0\206\0\u029e"
            + "\0\u02e1\0\u0324\0\u0367\0\u03aa\0\u03ed\0\u0430\0\311\0\u0473"
            + "\0\u04b6\0\u04f9\0\u053c\0\u057f\0\u05c2\0\u0605\0\u0648\0\u068b"
            + "\0\u06ce\0\u0711\0\u0754\0\206\0\u0797\0\206\0\u07da\0\u081d"
            + "\0\u0860\0\u08a3\0\u08e6\0\u0929\0\u096c\0\u09af\0\u09f2\0\u0a35"
            + "\0\u0a78\0\u0abb\0\u0afe\0\u0b41\0\u0b84\0\u0bc7\0\u0c0a\0\u0c4d"
            + "\0\u0c90\0\u0cd3\0\u0d16\0\u0d59\0\u0d9c\0\u0ddf\0\u0e22\0\u0e65"
            + "\0\u0ea8\0\u0eeb\0\u0f2e\0\u0f71\0\u0fb4\0\u0ff7\0\u103a\0\u107d"
            + "\0\u10c0\0\u1103\0\u1146\0\u1189\0\u11cc\0\u0929\0\311\0\u120f"
            + "\0\u1252\0\u1295\0\u12d8\0\u131b\0\u135e\0\u13a1\0\u13e4\0\u1427"
            + "\0\u146a\0\u13e4\0\u14ad";

    private static int[] zzUnpackRowMap() {
        int[] result = new int[91];
        int offset = 0;
        offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int[] result) {
        int i = 0;  /* index in packed string  */
        int j = offset;  /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int high = packed.charAt(i++) << 16;
            result[j++] = high | packed.charAt(i++);
        }
        return j;
    }
    /**
     * The transition table of the DFA
     */
    private static final int[] ZZ_TRANS = zzUnpackTrans();
    private static final String ZZ_TRANS_PACKED_0 =
            "\1\3\2\4\1\5\1\6\1\7\1\3\1\10\1\3"
            + "\1\4\1\11\1\12\2\3\1\13\1\14\1\12\1\15"
            + "\1\3\1\16\1\15\1\17\1\3\1\20\1\4\1\21"
            + "\1\22\1\4\1\23\1\4\1\24\1\17\1\4\1\25"
            + "\1\26\1\27\1\30\1\26\1\31\1\32\1\33\1\34"
            + "\1\27\1\35\1\27\1\36\1\37\1\4\1\40\3\4"
            + "\1\41\6\4\1\42\5\4\1\43\1\44\7\45\1\46"
            + "\17\45\1\47\3\45\1\50\4\45\1\51\42\45\104\0"
            + "\5\4\2\0\1\52\3\4\4\0\1\4\6\0\10\4"
            + "\1\0\42\4\1\0\3\53\2\5\1\53\2\0\4\53"
            + "\1\0\1\53\2\0\1\53\6\0\10\53\1\0\42\53"
            + "\1\0\3\53\2\5\1\53\2\0\4\53\1\0\1\53"
            + "\2\0\1\54\6\0\10\53\1\0\42\53\2\0\5\4"
            + "\2\0\1\52\3\4\4\0\1\4\6\0\5\4\1\55"
            + "\2\4\1\0\13\4\1\56\22\4\1\57\3\4\2\0"
            + "\5\4\2\0\1\52\3\4\4\0\1\4\6\0\4\4"
            + "\1\57\3\4\1\0\42\4\2\0\5\4\2\0\1\52"
            + "\3\4\4\0\1\4\6\0\10\4\1\0\35\4\1\60"
            + "\4\4\17\0\1\13\65\0\5\4\2\0\1\52\3\4"
            + "\4\0\1\4\6\0\10\4\1\0\1\61\41\4\2\0"
            + "\5\4\2\0\1\52\3\4\4\0\1\4\6\0\10\4"
            + "\1\0\14\4\1\27\25\4\2\0\5\4\2\0\1\52"
            + "\1\62\1\4\1\63\4\0\1\4\6\0\1\64\1\65"
            + "\1\27\4\4\1\56\1\0\42\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\4\4\1\66\3\4"
            + "\1\0\13\4\1\67\20\4\1\56\5\4\2\0\5\4"
            + "\2\0\1\52\3\4\4\0\1\27\6\0\10\4\1\0"
            + "\42\4\2\0\5\4\2\0\1\52\3\4\4\0\1\4"
            + "\6\0\10\4\1\0\11\4\1\70\4\4\1\71\1\72"
            + "\22\4\2\0\5\4\2\0\1\52\3\4\4\0\1\4"
            + "\6\0\10\4\1\0\21\4\1\70\20\4\2\0\5\4"
            + "\2\0\1\52\3\4\4\0\1\4\6\0\10\4\1\0"
            + "\5\4\1\27\34\4\2\0\5\4\2\0\1\52\3\4"
            + "\4\0\1\4\6\0\10\4\1\0\1\4\1\73\15\4"
            + "\1\74\10\4\1\75\11\4\2\0\5\4\2\0\1\52"
            + "\3\4\4\0\1\4\6\0\10\4\1\0\11\4\1\70"
            + "\30\4\2\0\5\4\2\0\1\52\3\4\4\0\1\4"
            + "\6\0\10\4\1\0\3\4\1\27\36\4\2\0\5\4"
            + "\2\0\1\52\3\4\4\0\1\4\6\0\10\4\1\0"
            + "\2\4\1\76\1\4\1\74\3\4\1\27\11\4\1\77"
            + "\1\4\1\100\5\4\1\101\7\4\2\0\4\4\1\102"
            + "\2\0\1\52\3\4\4\0\1\4\6\0\3\4\1\60"
            + "\4\4\1\0\34\4\1\103\5\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\3\4\1\60\4\4"
            + "\1\0\42\4\2\0\5\4\2\0\1\52\3\4\4\0"
            + "\1\4\6\0\10\4\1\0\1\4\1\74\4\4\1\104"
            + "\14\4\1\105\16\4\2\0\5\4\2\0\1\52\3\4"
            + "\4\0\1\4\6\0\10\4\1\0\30\4\1\105\11\4"
            + "\2\0\5\4\2\0\1\52\3\4\4\0\1\4\6\0"
            + "\10\4\1\0\25\4\1\106\14\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\10\4\1\0\16\4"
            + "\1\105\2\4\1\72\7\4\1\105\1\107\7\4\2\0"
            + "\4\4\1\57\2\0\1\52\1\110\2\4\4\0\1\4"
            + "\6\0\6\4\1\57\1\4\1\0\35\4\1\103\4\4"
            + "\1\0\7\45\1\0\17\45\1\0\3\45\1\0\4\45"
            + "\1\0\42\45\30\0\1\111\102\0\1\112\3\0\1\113"
            + "\106\0\1\114\53\0\1\115\71\0\6\53\2\0\4\53"
            + "\1\0\1\53\2\0\1\53\6\0\10\53\1\0\42\53"
            + "\1\0\3\53\3\116\2\0\3\53\1\116\1\0\1\53"
            + "\2\0\1\53\6\0\4\53\1\116\2\53\1\116\1\0"
            + "\1\53\4\116\6\53\2\116\1\53\1\116\11\53\1\116"
            + "\11\53\2\0\5\4\2\0\1\52\3\4\4\0\1\4"
            + "\6\0\10\4\1\0\36\4\1\117\3\4\2\0\5\4"
            + "\2\0\1\52\3\4\4\0\1\4\6\0\1\4\1\117"
            + "\6\4\1\0\42\4\2\0\5\4\2\0\1\52\3\4"
            + "\4\0\1\4\6\0\5\4\1\117\2\4\1\0\42\4"
            + "\2\0\5\4\2\0\1\52\1\4\1\117\1\4\4\0"
            + "\1\4\6\0\10\4\1\0\42\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\5\4\1\117\2\4"
            + "\1\0\34\4\1\117\2\4\1\117\2\4\2\0\5\4"
            + "\2\0\1\52\2\4\1\117\4\0\1\4\6\0\10\4"
            + "\1\0\42\4\2\0\5\4\2\0\1\52\3\4\4\0"
            + "\1\117\6\0\10\4\1\0\42\4\2\0\5\4\2\0"
            + "\1\52\1\4\1\117\1\4\4\0\1\4\6\0\6\4"
            + "\1\117\1\4\1\0\42\4\2\0\4\4\1\117\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\5\4\1\117\2\4"
            + "\1\0\42\4\2\0\5\4\2\0\1\52\1\117\1\4"
            + "\1\117\4\0\1\4\6\0\6\4\2\117\1\0\13\4"
            + "\2\117\17\4\1\117\3\4\1\117\1\4\2\0\5\4"
            + "\2\0\1\52\3\4\4\0\1\4\6\0\3\4\1\117"
            + "\4\4\1\0\37\4\2\117\1\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\10\4\1\0\20\4"
            + "\1\117\21\4\2\0\5\4\2\0\1\52\3\4\4\0"
            + "\1\4\6\0\10\4\1\0\5\4\1\117\10\4\1\117"
            + "\23\4\2\0\5\4\2\0\1\52\3\4\4\0\1\4"
            + "\6\0\10\4\1\0\16\4\1\117\23\4\2\0\5\4"
            + "\2\0\1\52\3\4\4\0\1\4\6\0\10\4\1\0"
            + "\11\4\1\117\14\4\2\117\12\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\10\4\1\0\22\4"
            + "\1\117\17\4\2\0\5\4\2\0\1\52\3\4\4\0"
            + "\1\4\6\0\10\4\1\0\1\4\4\117\12\4\1\117"
            + "\7\4\1\117\1\4\2\117\7\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\10\4\1\0\5\4"
            + "\1\117\34\4\2\0\5\4\2\0\1\52\3\4\4\0"
            + "\1\4\6\0\10\4\1\0\6\4\1\117\7\4\1\117"
            + "\23\4\2\0\5\4\2\0\1\52\3\4\4\0\1\4"
            + "\6\0\10\4\1\0\20\4\1\117\10\4\1\117\10\4"
            + "\2\0\5\4\2\0\1\52\3\4\4\0\1\4\6\0"
            + "\10\4\1\0\2\4\1\117\37\4\2\0\4\4\1\117"
            + "\2\0\1\52\3\4\4\0\1\117\6\0\10\4\1\0"
            + "\42\4\2\0\4\4\1\117\2\0\1\52\3\4\4\0"
            + "\1\4\6\0\10\4\1\0\42\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\10\4\1\0\23\4"
            + "\1\117\16\4\2\0\5\4\2\0\1\52\3\4\4\0"
            + "\1\4\6\0\10\4\1\0\6\4\1\117\33\4\2\0"
            + "\5\4\2\0\1\52\3\4\4\0\1\4\6\0\10\4"
            + "\1\0\6\4\1\117\10\4\1\117\6\4\1\117\13\4"
            + "\2\0\5\4\2\0\1\52\3\4\4\0\1\4\6\0"
            + "\10\4\1\0\31\4\1\117\10\4\2\0\5\4\2\0"
            + "\1\52\3\4\4\0\1\4\6\0\6\4\1\117\1\4"
            + "\1\0\42\4\31\0\1\120\103\0\1\121\106\0\1\122"
            + "\105\0\1\123\45\0\3\124\5\0\1\124\17\0\1\124"
            + "\2\0\1\124\2\0\4\124\6\0\2\124\1\0\1\124"
            + "\11\0\1\124\43\0\1\125\110\0\1\126\101\0\1\121"
            + "\66\0\1\127\63\0\3\130\5\0\1\130\17\0\1\130"
            + "\2\0\1\130\2\0\4\130\6\0\2\130\1\0\1\130"
            + "\11\0\1\130\44\0\1\121\4\0\1\126\71\0\1\131"
            + "\55\0\1\127\1\132\3\127\1\132\2\0\3\127\1\0"
            + "\1\132\1\0\1\132\1\127\1\0\4\132\11\127\1\132"
            + "\42\127\1\132\3\0\3\133\5\0\1\133\17\0\1\133"
            + "\2\0\1\133\2\0\4\133\6\0\2\133\1\0\1\133"
            + "\11\0\1\133\40\0\1\127\57\0\3\4\5\0\1\4"
            + "\17\0\1\4\2\0\1\4\2\0\4\4\6\0\2\4"
            + "\1\0\1\4\11\0\1\4\12\0";

    private static int[] zzUnpackTrans() {
        int[] result = new int[5360];
        int offset = 0;
        offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackTrans(String packed, int offset, int[] result) {
        int i = 0;       /* index in packed string  */
        int j = offset;  /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            value--;
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    /* error codes */
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;

    /* error messages for the codes above */
    private static final String ZZ_ERROR_MSG[] = {
        "Unkown internal scanner error",
        "Error: could not match input",
        "Error: pushback value was too large"
    };
    /**
     * ZZ_ATTRIBUTE[aState] contains the attributes of state
     * <code>aState</code>
     */
    private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0 =
            "\2\0\1\11\4\1\1\11\3\1\4\11\24\1\1\11"
            + "\1\1\1\11\3\1\1\0\36\1\5\0\2\1\7\0"
            + "\1\1\4\0";

    private static int[] zzUnpackAttribute() {
        int[] result = new int[91];
        int offset = 0;
        offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int[] result) {
        int i = 0;       /* index in packed string  */
        int j = offset;  /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    /**
     * the input device
     */
    private java.io.Reader zzReader;
    /**
     * the current state of the DFA
     */
    private int zzState;
    /**
     * the current lexical state
     */
    private int zzLexicalState = YYINITIAL;
    /**
     * this buffer contains the current text to be matched and is the source of
     * the yytext() string
     */
    private char zzBuffer[];
    /**
     * the textposition at the last accepting state
     */
    private int zzMarkedPos;
    /**
     * the textposition at the last state to be included in yytext
     */
    private int zzPushbackPos;
    /**
     * the current text position in the buffer
     */
    private int zzCurrentPos;
    /**
     * startRead marks the beginning of the yytext() string in the buffer
     */
    private int zzStartRead;
    /**
     * endRead marks the last character in the buffer, that has been read from
     * input
     */
    private int zzEndRead;
    /**
     * number of newlines encountered up to the start of the matched text
     */
    private int yyline;
    /**
     * the number of characters up to the start of the matched text
     */
    private int yychar;
    /**
     * the number of characters from the last newline up to the start of the
     * matched text
     */
    private int yycolumn;
    /**
     * zzAtBOL == true <=> the scanner is currently at the beginning of a line
     */
    private boolean zzAtBOL = true;
    /**
     * zzAtEOF == true <=> the scanner is at the EOF
     */
    private boolean zzAtEOF;

    /* user code: */
    /**
     * Constructor. This must be here because JFlex does not generate a
     * no-parameter constructor.
     */
    public DasmTokenMaker() {
    }

    /**
     * Adds the token specified to the current linked list of tokens.
     *
     * @param tokenType The token's type.
     * @see #addToken(int, int, int)
     */
    private void addHyperlinkToken(int start, int end, int tokenType) {
        int so = start + offsetShift;
        addToken(zzBuffer, start, end, tokenType, so, true);
    }

    /**
     * Adds the token specified to the current linked list of tokens.
     *
     * @param tokenType The token's type.
     */
    private void addToken(int tokenType) {
        addToken(zzStartRead, zzMarkedPos - 1, tokenType);
    }

    /**
     * Adds the token specified to the current linked list of tokens.
     *
     * @param tokenType The token's type.
     * @see #addHyperlinkToken(int, int, int)
     */
    private void addToken(int start, int end, int tokenType) {
        int so = start + offsetShift;
        addToken(zzBuffer, start, end, tokenType, so, false);
    }

    /**
     * Adds the token specified to the current linked list of tokens.
     *
     * @param array The character array.
     * @param start The starting offset in the array.
     * @param end The ending offset in the array.
     * @param tokenType The token's type.
     * @param startOffset The offset in the document at which this token occurs.
     * @param hyperlink Whether this token is a hyperlink.
     */
    public void addToken(char[] array, int start, int end, int tokenType,
            int startOffset, boolean hyperlink) {
        super.addToken(array, start, end, tokenType, startOffset, hyperlink);
        zzStartRead = zzMarkedPos;
    }

    /**
     * Returns the text to place at the beginning and end of a line to "comment"
     * it in a this programming language.
     *
     * @return The start and end strings to add to a line to "comment" it out.
     */
    public String[] getLineCommentStartAndEnd() {
        return new String[]{";", null};
    }

    /**
     * Returns the first token in the linked list of tokens generated from
     * <code>text</code>. This method must be implemented by subclasses so they
     * can correctly implement syntax highlighting.
     *
     * @param text The text from which to get tokens.
     * @param initialTokenType The token type we should start with.
     * @param startOffset The offset into the document at which
     * <code>text</code> starts.
     * @return The first <code>Token</code> in a linked list representing the
     * syntax highlighted text.
     */
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

        resetTokenList();
        this.offsetShift = -text.offset + startOffset;

        // Start off in the proper state.
        int state = Token.NULL;
        switch (initialTokenType) {
            /* No multi-line comments */
            /* No documentation comments */
            default:
                state = Token.NULL;
        }

        s = text;
        try {
            yyreset(zzReader);
            yybegin(state);
            return yylex();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return new DefaultToken();
        }

    }

    /**
     * Refills the input buffer.
     *
     * @return      <code>true</code> if EOF was reached, otherwise
     * <code>false</code>.
     */
    private boolean zzRefill() {
        return zzCurrentPos >= s.offset + s.count;
    }

    /**
     * Resets the scanner to read from a new input stream. Does not close the
     * old reader.
     *
     * All internal variables are reset, the old input stream <b>cannot</b> be
     * reused (internal buffer is discarded and lost). Lexical state is set to
     * <tt>YY_INITIAL</tt>.
     *
     * @param reader the new input stream
     */
    public final void yyreset(Reader reader) {
        // 's' has been updated.
        zzBuffer = s.array;
        /*
         * We replaced the line below with the two below it because zzRefill
         * no longer "refills" the buffer (since the way we do it, it's always
         * "full" the first time through, since it points to the segment's
         * array).  So, we assign zzEndRead here.
         */
        //zzStartRead = zzEndRead = s.offset;
        zzStartRead = s.offset;
        zzEndRead = zzStartRead + s.count - 1;
        zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
        zzLexicalState = YYINITIAL;
        zzReader = reader;
        zzAtBOL = true;
        zzAtEOF = false;
    }

    /**
     * Creates a new scanner There is also a java.io.InputStream version of this
     * constructor.
     *
     * @param in the java.io.Reader to read input from.
     */
    public DasmTokenMaker(java.io.Reader in) {
        this.zzReader = in;
    }

    /**
     * Creates a new scanner. There is also java.io.Reader version of this
     * constructor.
     *
     * @param in the java.io.Inputstream to read input from.
     */
    public DasmTokenMaker(java.io.InputStream in) {
        this(new java.io.InputStreamReader(in));
    }

    /**
     * Unpacks the compressed character translation table.
     *
     * @param packed the packed character translation table
     * @return the unpacked character translation table
     */
    private static char[] zzUnpackCMap(String packed) {
        char[] map = new char[0x10000];
        int i = 0;  /* index in packed string  */
        int j = 0;  /* index in unpacked array */
        while (i < 192) {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do {
                map[j++] = value;
            } while (--count > 0);
        }
        return map;
    }

    /**
     * Closes the input stream.
     */
    public final void yyclose() throws java.io.IOException {
        zzAtEOF = true;            /* indicate end of file */
        zzEndRead = zzStartRead;  /* invalidate buffer    */

        if (zzReader != null) {
            zzReader.close();
        }
    }

    /**
     * Enters a new lexical state
     *
     * @param newState the new lexical state
     */
    public final void yybegin(int newState) {
        zzLexicalState = newState;
    }

    /**
     * Returns the text matched by the current regular expression.
     */
    public final String yytext() {
        return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    /**
     * Returns the character at position <tt>pos</tt> from the matched text.
     *
     * It is equivalent to yytext().charAt(pos), but faster
     *
     * @param pos the position of the character to fetch. A value from 0 to
     * yylength()-1.
     *
     * @return the character at position pos
     */
    public final char yycharat(int pos) {
        return zzBuffer[zzStartRead + pos];
    }

    /**
     * Returns the length of the matched text region.
     */
    public final int yylength() {
        return zzMarkedPos - zzStartRead;
    }

    /**
     * Reports an error that occured while scanning.
     *
     * In a wellformed scanner (no or only correct usage of yypushback(int) and
     * a match-all fallback rule) this method will only be called with things
     * that "Can't Possibly Happen". If this method is called, something is
     * seriously wrong (e.g. a JFlex bug producing a faulty scanner etc.).
     *
     * Usual syntax/scanner level error handling should be done in error
     * fallback rules.
     *
     * @param errorCode the code of the errormessage to display
     */
    private void zzScanError(int errorCode) {
        String message;
        try {
            message = ZZ_ERROR_MSG[errorCode];
        } catch (ArrayIndexOutOfBoundsException e) {
            message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
        }

        throw new Error(message);
    }

    /**
     * Pushes the specified amount of characters back into the input stream.
     *
     * They will be read again by then next call of the scanning method
     *
     * @param number the number of characters to be read again. This number must
     * not be greater than yylength()!
     */
    public void yypushback(int number) {
        if (number > yylength()) {
            zzScanError(ZZ_PUSHBACK_2BIG);
        }

        zzMarkedPos -= number;
    }

    /**
     * Resumes scanning until the next regular expression is matched, the end of
     * input is encountered or an I/O-Error occurs.
     *
     * @return the next token
     * @exception java.io.IOException if any I/O-Error occurs
     */
    public org.fife.ui.rsyntaxtextarea.Token yylex() throws java.io.IOException {
        int zzInput;
        int zzAction;

        // cached fields:
        int zzCurrentPosL;
        int zzMarkedPosL;
        int zzEndReadL = zzEndRead;
        char[] zzBufferL = zzBuffer;
        char[] zzCMapL = ZZ_CMAP;

        int[] zzTransL = ZZ_TRANS;
        int[] zzRowMapL = ZZ_ROWMAP;
        int[] zzAttrL = ZZ_ATTRIBUTE;

        while (true) {
            zzMarkedPosL = zzMarkedPos;

            zzAction = -1;

            zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

            zzState = zzLexicalState;


            zzForAction:
            {
                while (true) {

                    if (zzCurrentPosL < zzEndReadL) {
                        zzInput = zzBufferL[zzCurrentPosL++];
                    } else if (zzAtEOF) {
                        zzInput = YYEOF;
                        break zzForAction;
                    } else {
                        // store back cached positions
                        zzCurrentPos = zzCurrentPosL;
                        zzMarkedPos = zzMarkedPosL;
                        boolean eof = zzRefill();
                        // get translated positions and possibly new buffer
                        zzCurrentPosL = zzCurrentPos;
                        zzMarkedPosL = zzMarkedPos;
                        zzBufferL = zzBuffer;
                        zzEndReadL = zzEndRead;
                        if (eof) {
                            zzInput = YYEOF;
                            break zzForAction;
                        } else {
                            zzInput = zzBufferL[zzCurrentPosL++];
                        }
                    }
                    int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput]];
                    if (zzNext == -1) {
                        break zzForAction;
                    }
                    zzState = zzNext;

                    int zzAttributes = zzAttrL[zzState];
                    if ((zzAttributes & 1) == 1) {
                        zzAction = zzState;
                        zzMarkedPosL = zzCurrentPosL;
                        if ((zzAttributes & 8) == 8) {
                            break zzForAction;
                        }
                    }

                }
            }

            // store back cached position
            zzMarkedPos = zzMarkedPosL;

            switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
                case 9: {
                    addToken(Token.RESERVED_WORD);
                }
                case 17:
                    break;
                case 1: {
                    addToken(Token.IDENTIFIER);
                }
                case 18:
                    break;
                case 15: {
                    addToken(Token.FUNCTION);
                }
                case 19:
                    break;
                case 8: {
                    addToken(Token.RESERVED_WORD_2);
                }
                case 20:
                    break;
                case 5: {
                    addToken(Token.WHITESPACE);
                }
                case 21:
                    break;
                case 16: {
                    int temp = zzStartRead;
                    addToken(start, zzStartRead - 1, Token.COMMENT_EOL);
                    addHyperlinkToken(temp, zzMarkedPos - 1, Token.COMMENT_EOL);
                    start = zzMarkedPos;
                }
                case 22:
                    break;
                case 13: {
                    addToken(Token.ERROR_NUMBER_FORMAT);
                }
                case 23:
                    break;
                case 4: {
                    addToken(Token.DATA_TYPE);
                }
                case 24:
                    break;
                case 6: {
                    start = zzMarkedPos - 1;
                    yybegin(EOL_COMMENT);
                }
                case 25:
                    break;
                case 14: {
                    addToken(Token.LITERAL_NUMBER_HEXADECIMAL);
                }
                case 26:
                    break;
                case 10: {
                    addToken(Token.OPERATOR);
                }
                case 27:
                    break;
                case 2: {
                    addToken(Token.LITERAL_NUMBER_DECIMAL_INT);
                }
                case 28:
                    break;
                case 3: {
                    addNullToken();
                    return firstToken;
                }
                case 29:
                    break;
                case 12: {
                    addToken(start, zzStartRead - 1, Token.COMMENT_EOL);
                    addNullToken();
                    return firstToken;
                }
                case 30:
                    break;
                case 11: {
                }
                case 31:
                    break;
                case 7: {
                    addToken(Token.SEPARATOR);
                }
                case 32:
                    break;
                default:
                    if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
                        zzAtEOF = true;
                        switch (zzLexicalState) {
                            case EOL_COMMENT: {
                                addToken(start, zzStartRead - 1, Token.COMMENT_EOL);
                                addNullToken();
                                return firstToken;
                            }
                            case 92:
                                break;
                            case YYINITIAL: {
                                addNullToken();
                                return firstToken;
                            }
                            case 93:
                                break;
                            default:
                                return null;
                        }
                    } else {
                        zzScanError(ZZ_NO_MATCH);
                    }
            }
        }

    }
}