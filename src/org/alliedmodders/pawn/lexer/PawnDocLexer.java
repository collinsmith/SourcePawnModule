package org.alliedmodders.pawn.lexer;

import org.alliedmodders.pawn.Pawn;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class PawnDocLexer extends AbstractPawnLexer<PawnDocTokenId> {
    
    public static enum NextIdentifierLexerState {
        PARAM,
        CODE,
        LITERAL
    }
    
    private NextIdentifierLexerState state;
    
    public PawnDocLexer(LexerRestartInfo<PawnDocTokenId> info) {
        super(info);
        this.state = (NextIdentifierLexerState)info.state();
    }

    @Override
    public Object state() {
        return state;
    }
    
    @Override
    public Token<PawnDocTokenId> nextToken() {
        int ch = nextChar();
        
        if (ch == EOF) {
            return null;
        }
        
        switch (ch) {
            case '@':
                if (state == null) {
                    switch (ch = nextChar()) {
                        case 'c':
                            if ((ch = nextChar()) == 'o'
                             && (ch = nextChar()) == 'd'
                             && (ch = nextChar()) == 'e'
                             && Character.isWhitespace(nextChar())) {
                                backup(1);
                                state = NextIdentifierLexerState.CODE;
                            }
                            break;

                        case 'l':
                            if ((ch = nextChar()) == 'i'
                             && (ch = nextChar()) == 't'
                             && (ch = nextChar()) == 'e'
                             && (ch = nextChar()) == 'r'
                             && (ch = nextChar()) == 'a'
                             && (ch = nextChar()) == 'l'
                             && Character.isWhitespace(nextChar())) {
                                backup(1);
                                state = NextIdentifierLexerState.LITERAL;
                            }
                            break;

                        case 'p':
                            if ((ch = nextChar()) == 'a'
                             && (ch = nextChar()) == 'r'
                             && (ch = nextChar()) == 'a'
                             && (ch = nextChar()) == 'm'
                             && Character.isWhitespace(nextChar())) {
                                backup(1);
                                state = NextIdentifierLexerState.PARAM;
                            }
                            break;
                    }
                }
                finishWord();
                return token(PawnDocTokenId.TAG);
            
            case '#':
                state = null;
                finishWord();
                return token(PawnDocTokenId.HASH);
                
            case '<':
                if (state == NextIdentifierLexerState.LITERAL || state == NextIdentifierLexerState.CODE) {
                    return otherText(ch);
                }
                
                state = null;
                int backupCounter = 0;
                boolean newline = false;
                boolean asterisk = false;
                while (true) {
                    backupCounter++;
                    switch (ch = nextChar()) {
                        case EOF:
                        case '>':
                            return token(PawnDocTokenId.HTML_TAG);
                        case '<':
                            backup(1);
                            return token(PawnDocTokenId.HTML_TAG);
                        case '\n':
                            backupCounter = 1;
                            newline = true;
                            asterisk = false;
                            break;
                        case '@':
                            if (newline) {
                                backup(backupCounter);
                                return token(PawnDocTokenId.HTML_TAG);
                            }
                            break;
                        case '*':
                            if (newline && !asterisk) {
                                asterisk = true;
                            }
                            break;
                        default:
                            if (newline && !Character.isWhitespace(ch)) {
                                newline = false;
                            }
                    }
                }
                
            default:
                if (!Pawn.isPawnIdentifierStart(ch)) {
                    if (!Character.isWhitespace(ch)) {
                        state = null;
                    }
                    
                    if (state == NextIdentifierLexerState.PARAM) {
                        switch (ch) {
                            case '.':
                                if ((ch = nextChar()) == '.'
                                 && (ch = nextChar()) == '.')
                                    return token(PawnDocTokenId.IDENTIFIER);
                                break;
                            case '%':
                                while (Character.isDigit(nextChar())) {};
                                backup(1);
                                return token(PawnDocTokenId.IDENTIFIER);
                        }
                    }
                    
                    return otherText(ch);
                }
                
                while (Pawn.isPawnIdentifierPart(nextChar())) {};

                backup(1);
                if (state == null) {
                    return token(PawnDocTokenId.TEXT);
                }
                
                switch (state) {
                    case CODE:
                    case LITERAL:
                    case PARAM:
                        state = null;
                        return token(PawnDocTokenId.IDENTIFIER);
                }
            
                return otherText(ch);
        }
    }
    
    private Token<PawnDocTokenId> otherText(int ch) {
        boolean newline = state == null;
        boolean leftbr = false;
        while (true) {
            // TODO: Change to isPawnIdentifierStart(ch)
            if (Character.isJavaIdentifierStart(ch)) {
                if ((newline || leftbr) && state != NextIdentifierLexerState.LITERAL && state != NextIdentifierLexerState.CODE) {
                    state = null;
                }
                
                backup(1);
                return token(PawnDocTokenId.TEXT);
            }
            
            switch (ch) {
                case '<':
                    if (state == NextIdentifierLexerState.LITERAL || state == NextIdentifierLexerState.CODE) {
                        leftbr = false;
                        newline = false;
                        break;
                    }
                case '#':
                    backup(1);
                case EOF:
                    return token(PawnDocTokenId.TEXT);
                case '@':
                    if ((newline || leftbr) && (state == null || (state != NextIdentifierLexerState.LITERAL && state != NextIdentifierLexerState.CODE))) {
                        state = null;
                        backup(1);
                        return token(PawnDocTokenId.TEXT);
                    }
                
                    leftbr = false;
                    newline = false;
                    break;
                case '{':
                    leftbr = true;
                    newline = false;
                    break;
                case '\n':
                    newline = true;
                    break;
                case '}':
                    if (state == NextIdentifierLexerState.LITERAL || state == NextIdentifierLexerState.CODE) {
                        state = null;
                        if (1 < readLength()) {
                            backup(1);
                        }
                        
                        return token(PawnDocTokenId.TEXT);
                    }
                    
                    leftbr = false;
                    newline = false;
                    break;
                case '*':
                    if (newline) {
                        break;
                    }
                default:
                    if (!Character.isWhitespace(ch)) {
                        leftbr = false;
                        newline = false;
                    }
            }
            
            ch = nextChar();
        }
    }
    
    private void finishWord() {
        finishWord(nextChar());
    }
    
    private void finishWord(int ch) {
        while (true) {
            if (ch == EOF || Character.isWhitespace(ch) || ch == '}') {
                backup(1);
                return;
            }

            ch = nextChar();
        }
    }
    
    @Override
    public void release() {
        //...
    }
}
