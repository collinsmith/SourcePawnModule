package org.alliedmodders.pawn.lexer;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class PawnStringLiteralLexer<T extends TokenId> extends AbstractPawnLexer<T> {
    
    private static final int EOF = LexerInput.EOF;
    
    private static enum PercentEscapeLexerState {
        LOOKING_FOR_LEFT_JUSTIFY,
        LOOKING_FOR_WIDTH,
        WIDTH,
        LOOKING_FOR_PRECISION,
        PRECISION
    }
    
    private final boolean isStringLiteralTokenId;
    
    public PawnStringLiteralLexer(LexerRestartInfo<T> info, boolean isStringLiteralTokenId) {
        super(info);
        this.isStringLiteralTokenId = isStringLiteralTokenId;
        assert (info.state() == null); // passed argument always null
    }
    
    private boolean isStringLiteralTokenId() {
        return isStringLiteralTokenId;
    }
    
    @Override
    public Object state() {
        return null;
    }

    @Override
    @SuppressWarnings("fallthrough")
    public Token<T> nextToken() {
        while (true) {
            int ch;
            switch (ch = nextChar()) {
                case EOF:
                    if (0 < readLength()) {
                        return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.TEXT : PawnCharacterLiteralTokenId.TEXT);
                    } else {
                        return null;
                    }
                
                case '%':
                    if (1 < readLength()) {
                        backup(1);
                        return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.TEXT : PawnCharacterLiteralTokenId.TEXT, readLength());
                    }
                
                    PercentEscapeLexerState state = PercentEscapeLexerState.LOOKING_FOR_LEFT_JUSTIFY;
                    int i = -1;
                    while (true) {
                        i++;
                        switch (ch = nextChar()) {
                            case '%':
                                return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.PERCENT_ESCAPE : PawnCharacterLiteralTokenId.PERCENT_ESCAPE, readLength());
                            case '-':
                                switch (state) {
                                    case LOOKING_FOR_LEFT_JUSTIFY:
                                        state = PercentEscapeLexerState.LOOKING_FOR_WIDTH;
                                        continue;
                                    default:
                                        return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.PERCENT_ESCAPE_INVALID : PawnCharacterLiteralTokenId.PERCENT_ESCAPE_INVALID);
                                }
                            case '0': case '1': case '2': case '3': case '4':
                            case '5': case '6': case '7': case '8': case '9':
                                switch (state) {
                                    case LOOKING_FOR_LEFT_JUSTIFY:
                                    case LOOKING_FOR_WIDTH:
                                        state = PercentEscapeLexerState.WIDTH;
                                        continue;
                                    case WIDTH:
                                        continue;
                                    case LOOKING_FOR_PRECISION:
                                        state = PercentEscapeLexerState.PRECISION;
                                        continue;
                                    case PRECISION:
                                        continue;
                                    default:
                                        assert false : state;
                                }
                            
                            case '.':
                                if (state == PercentEscapeLexerState.LOOKING_FOR_PRECISION || state == PercentEscapeLexerState.PRECISION) {
                                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.PERCENT_ESCAPE_INVALID : PawnCharacterLiteralTokenId.PERCENT_ESCAPE_INVALID);
                                }
                                
                                state = PercentEscapeLexerState.LOOKING_FOR_PRECISION;
                                continue;
                            case 'd': case 'i':
                            case 'u':
                            case 'b':
                            case 'f':
                            case 'x': case 'X':
                            case 's':
                            case 't': case 'T':
                            case 'c':
                            case 'L':
                            case 'N':
                                return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.PERCENT_ESCAPE : PawnCharacterLiteralTokenId.PERCENT_ESCAPE, readLength());
                            default:
                                if (i == 0) {
                                    backup(1);
                                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.TEXT : PawnCharacterLiteralTokenId.TEXT);
                                }
                                
                                return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.PERCENT_ESCAPE_INVALID : PawnCharacterLiteralTokenId.PERCENT_ESCAPE_INVALID);
                        }
                    }
                
                case '\\':
                    if (1 < readLength()) {
                        backup(1);
                        return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.TEXT : PawnCharacterLiteralTokenId.TEXT, readLength());
                    }
                    
                    ch = nextChar();
                    if (Character.isWhitespace(ch)) {
                        while (Character.isWhitespace(nextChar())) {};
                        backup(1);
                        return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.LINE_CONCATENATION : PawnCharacterLiteralTokenId.LINE_CONCATENATION);
                    }
                
                    switch (ch) {
                        case 'b':   return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.BACKSPACE : PawnCharacterLiteralTokenId.BACKSPACE);
                        case 'f':   return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.FORM_FEED : PawnCharacterLiteralTokenId.FORM_FEED);
                        case 'n':   return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.NEWLINE : PawnCharacterLiteralTokenId.NEWLINE);
                        case 'r':   return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.CR : PawnCharacterLiteralTokenId.CR);
                        case 't':   return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.TAB : PawnCharacterLiteralTokenId.TAB);
                        case '\'':  return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.SINGLE_QUOTE : PawnCharacterLiteralTokenId.SINGLE_QUOTE);
                        case '"':   return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.DOUBLE_QUOTE : PawnCharacterLiteralTokenId.DOUBLE_QUOTE);
                        case '\\':  return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.BACKSLASH : PawnCharacterLiteralTokenId.BACKSLASH);
                        case 'x':
                            switch (nextChar()) {
                                case '0': case '1': case '2': case '3': case '4':
                                case '5': case '6': case '7': case '8': case '9':
                                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                    switch (nextChar()) {
                                        case '0': case '1': case '2': case '3': case '4':
                                        case '5': case '6': case '7': case '8': case '9':
                                        case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                        case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                            switch (nextChar()) {
                                                case '0': case '1': case '2': case '3': case '4':
                                                case '5': case '6': case '7': case '8': case '9':
                                                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                                case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                                    if (nextChar() == ';') {
                                                        return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.HEXADECIMAL_ESCAPE : PawnCharacterLiteralTokenId.HEXADECIMAL_ESCAPE);
                                                    }

                                                    backup(1);
                                                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.HEXADECIMAL_ESCAPE : PawnCharacterLiteralTokenId.HEXADECIMAL_ESCAPE);
                                                case ';':
                                                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.HEXADECIMAL_ESCAPE : PawnCharacterLiteralTokenId.HEXADECIMAL_ESCAPE);
                                            }

                                            backup(1);
                                            return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.HEXADECIMAL_ESCAPE : PawnCharacterLiteralTokenId.HEXADECIMAL_ESCAPE);
                                        case ';':
                                            return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.HEXADECIMAL_ESCAPE : PawnCharacterLiteralTokenId.HEXADECIMAL_ESCAPE);
                                    }

                                    backup(1);
                                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.HEXADECIMAL_ESCAPE : PawnCharacterLiteralTokenId.HEXADECIMAL_ESCAPE);
                            }
                        
                            backup(1);
                            return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.ESCAPE_SEQUENCE_INVALID : PawnCharacterLiteralTokenId.HEXADECIMAL_ESCAPE_INVALID);
                            
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                            switch (nextChar()) {
                                case '0': case '1': case '2': case '3': case '4':
                                case '5': case '6': case '7': case '8': case '9':
                                    switch (nextChar()) {
                                        case '0': case '1': case '2': case '3': case '4':
                                        case '5': case '6': case '7': case '8': case '9':
                                            if (nextChar() == ';') {
                                                return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.DECIMAL_ESCAPE : PawnCharacterLiteralTokenId.DECIMAL_ESCAPE);
                                            }

                                            backup(1);
                                            return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.DECIMAL_ESCAPE : PawnCharacterLiteralTokenId.DECIMAL_ESCAPE);
                                        case ';':
                                            return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.DECIMAL_ESCAPE : PawnCharacterLiteralTokenId.DECIMAL_ESCAPE);
                                    }
                                
                                    backup(1);
                                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.DECIMAL_ESCAPE : PawnCharacterLiteralTokenId.DECIMAL_ESCAPE);
                                case ';':
                                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.DECIMAL_ESCAPE : PawnCharacterLiteralTokenId.DECIMAL_ESCAPE);
                            }
                        
                            backup(1);
                            return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.DECIMAL_ESCAPE : PawnCharacterLiteralTokenId.DECIMAL_ESCAPE);
                        //case '\r': consumeNewline();
                        //case '\n':
                        //    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.LINE_CONCATENATION : PawnCharacterLiteralTokenId.LINE_CONCATENATION);
                    }
                
                    backup(1);
                    return token(isStringLiteralTokenId() ? PawnStringLiteralTokenId.ESCAPE_SEQUENCE_INVALID : PawnCharacterLiteralTokenId.ESCAPE_SEQUENCE_INVALID);
            }
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Token<T> token(TokenId tokenId) {
        return super.token((T)tokenId);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Token<T> token(TokenId tokenId, int length) {
        return super.token((T)tokenId, length);
    }

    @Override
    public void release() {
        //...
    }
    
}
