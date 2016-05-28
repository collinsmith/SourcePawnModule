package org.alliedmodders.pawn.lexer;

import org.alliedmodders.pawn.Pawn;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

public class PawnPreprocessorLexer extends AbstractPawnLexer<PawnPreprocessorTokenId> {
    
    private static final int EOF = LexerInput.EOF;
    
    private TokenFactory<PawnPreprocessorTokenId> tokenFactory;
    private PawnPreprocessorTokenId state = null;
    
    public PawnPreprocessorLexer(LexerRestartInfo<PawnPreprocessorTokenId> info) {
        super(info);
        this.tokenFactory = info.tokenFactory();
        this.state = (PawnPreprocessorTokenId)info.state();
    }
    
    @Override
    public Object state() {
        return state;
    }
    
    @Override
    @SuppressWarnings("fallthrough")
    public Token<PawnPreprocessorTokenId> nextToken() {
        int ch = nextChar();
        
        if (ch == EOF) {
            return null;
        }
        
        switch (ch) {
            case '/':
                switch (nextChar()) {
                    case '=': // found /=
                        return token(PawnPreprocessorTokenId.SLASHEQ);
                } // end of switch ()

                backup(1);
                return token(PawnPreprocessorTokenId.SLASH);
            case '=':
                if (nextChar() == '=') {
                    return token(PawnPreprocessorTokenId.EQEQ);
                }

                backup(1);
                return token(PawnPreprocessorTokenId.EQ);
            case '>':
                switch (nextChar()) {
                    case '>': // after >>
                        switch (ch = nextChar()) {
                            case '>': // after >>>
                                if (nextChar() == '=')
                                    return token(PawnPreprocessorTokenId.GTGTGTEQ);
                                backup(1);
                                return token(PawnPreprocessorTokenId.GTGTGT);
                            case '=': // >>=
                                return token(PawnPreprocessorTokenId.GTGTEQ);
                        }
                        backup(1);
                        return token(PawnPreprocessorTokenId.GTGT);
                    case '=': // >=
                        return token(PawnPreprocessorTokenId.GTEQ);
                }
                backup(1);
                return token(PawnPreprocessorTokenId.GT);

            case '<':
                if (state == PawnPreprocessorTokenId.INCLUDE) {
                    while (true) {
                        ch = nextChar();
                        if (ch == EOF || Character.isWhitespace(ch)) {
                            return token(PawnPreprocessorTokenId.LEXICAL_ERROR);
                        } else if (ch == '>') {
                            return token(PawnPreprocessorTokenId.INCLUDE_SYSTEM_FILE);
                        }
                    }
                }
                
                switch (nextChar()) {
                    case '<': // after <<
                        if (nextChar() == '=')
                            return token(PawnPreprocessorTokenId.LTLTEQ);
                        backup(1);
                        return token(PawnPreprocessorTokenId.LTLT);
                    case '=': // <=
                        return token(PawnPreprocessorTokenId.LTEQ);
                }
                
                backup(1);
                return token(PawnPreprocessorTokenId.LT);

            case '+':
                switch (nextChar()) {
                    case '+':
                        return token(PawnPreprocessorTokenId.PLUSPLUS);
                    case '=':
                        return token(PawnPreprocessorTokenId.PLUSEQ);
                }
                backup(1);
                return token(PawnPreprocessorTokenId.PLUS);

            case '-':
                switch (nextChar()) {
                    case '-':
                        return token(PawnPreprocessorTokenId.MINUSMINUS);
                    case '=':
                        return token(PawnPreprocessorTokenId.MINUSEQ);
                }
                backup(1);
                return token(PawnPreprocessorTokenId.MINUS);

            case '*':
                switch (nextChar()) {
                    case '=':
                        return token(PawnPreprocessorTokenId.STAREQ);
                }
                backup(1);
                return token(PawnPreprocessorTokenId.STAR);

            case '|':
                switch (nextChar()) {
                    case '|':
                        return token(PawnPreprocessorTokenId.BARBAR);
                    case '=':
                        return token(PawnPreprocessorTokenId.BAREQ);
                }
                backup(1);
                return token(PawnPreprocessorTokenId.BAR);

            case '&':
                switch (nextChar()) {
                    case '&':
                        return token(PawnPreprocessorTokenId.AMPAMP);
                    case '=':
                        return token(PawnPreprocessorTokenId.AMPEQ);
                }
                backup(1);
                return token(PawnPreprocessorTokenId.AMP);

            case '%':
                switch (ch = nextChar()) {
                    case '=':
                        return token(PawnPreprocessorTokenId.PERCENTEQ);
                    case '0': case '1': case '2': case '3': case '4':
                    case '5': case '6': case '7': case '8': case '9':
                        finishNumberLiteral(ch, false);
                        return token(PawnPreprocessorTokenId.MACRO_PARAM);
                }
                
                backup(1);
                return token(PawnPreprocessorTokenId.PERCENT);
            case '^':
                if (nextChar() == '=')
                    return token(PawnPreprocessorTokenId.CARETEQ);
                backup(1);
                return token(PawnPreprocessorTokenId.CARET);

            case '!':
                if (nextChar() == '=')
                    return token(PawnPreprocessorTokenId.BANGEQ);
                backup(1);
                return token(PawnPreprocessorTokenId.BANG);
            case '.':
                if ((ch = nextChar()) == '.')
                    if (nextChar() == '.') { // ellipsis ...
                        return token(PawnPreprocessorTokenId.ELLIPSIS);
                    } else
                        backup(2);
                else if ('0' <= ch && ch <= '9') { // float literal
                    return finishNumberLiteral(nextChar(), true);
                } else
                    backup(1);
                return token(PawnPreprocessorTokenId.DOT);
            case '~':
                return token(PawnPreprocessorTokenId.TILDE);
            case ',':
                return token(PawnPreprocessorTokenId.COMMA);
            case ';':
                return token(PawnPreprocessorTokenId.SEMICOLON);
            case ':':
                return token(PawnPreprocessorTokenId.COLON);
            case '?':
                return token(PawnPreprocessorTokenId.QUESTION);
            case '(':
                return token(PawnPreprocessorTokenId.LPAREN);
            case ')':
                return token(PawnPreprocessorTokenId.RPAREN);
            case '[':
                return token(PawnPreprocessorTokenId.LBRACKET);
            case ']':
                return token(PawnPreprocessorTokenId.RBRACKET);
            case '{':
                return token(PawnPreprocessorTokenId.LBRACE);
            case '}':
                return token(PawnPreprocessorTokenId.RBRACE);
            case '"': // string literal
                while (true) {
                    switch (nextChar()) {
                        case '"':
                            return token(PawnPreprocessorTokenId.STRING_LITERAL);
                        case '\\':
                            nextChar();
                            break;
                        case '\r': consumeNewline();
                        case '\n':
                        case EOF:
                            // TODO: return EOL token
                            return tokenFactory.createToken(PawnPreprocessorTokenId.STRING_LITERAL,
                                    readLength(), PartType.START);
                    }
                }
            case '\'': // char literal
                while (true) {
                    switch (nextChar()) {
                        case '\'':
                            return token(PawnPreprocessorTokenId.CHAR_LITERAL);
                        case '\\':
                            nextChar();
                            break;
                        case '\r': consumeNewline();
                        case '\n':
                        case EOF:
                            // TODO: return EOL token
                            return tokenFactory.createToken(PawnPreprocessorTokenId.CHAR_LITERAL,
                                    readLength(), PartType.START);
                    }
                }
            case '0': // in a number literal
                ch = nextChar();
                if (ch == 'x' || ch == 'X') { // in hexadecimal literal
                    while (true) {
                        switch (nextChar()) {
                            case '0': case '1': case '2': case '3': case '4':
                            case '5': case '6': case '7': case '8': case '9':
                            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                break;
                            default:
                                backup(1);
                                return token(PawnPreprocessorTokenId.CELL_LITERAL);
                        }
                    } // end of while(true)
                } else if (ch == 'b') { // in binary literal
                    boolean afterDigit = false;
                    while (true) {
                        switch (nextChar()) {
                            case '0': case '1':
                                break;
                            default:
                                backup(1);
                                return token(PawnPreprocessorTokenId.CELL_LITERAL);
                        }
                    }
                }
                return finishNumberLiteral(ch, false);
            case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return finishNumberLiteral(nextChar(), false);
            case 'a':
                switch (ch = nextChar()) {
                    case 'l':
                        if ((ch = nextChar()) == 'i'
                         && (ch = nextChar()) == 'g'
                         && (ch = nextChar()) == 'n')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_ALIGN);
                        break;
                    case 'm':
                        if ((ch = nextChar()) == 'x')
                            switch (ch = nextChar()) {
                                case 'l':
                                    if ((ch = nextChar()) == 'i'
                                     && (ch = nextChar()) == 'm'
                                     && (ch = nextChar()) == 'i'
                                     && (ch = nextChar()) == 't')
                                        return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_AMXLIMIT);
                                    break;
                                case 'r':
                                    if ((ch = nextChar()) == 'a'
                                     && (ch = nextChar()) == 'm')
                                        return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_AMXRAM);
                                    break;
                            }
                        break;
                    case 's':
                        if ((ch = nextChar()) == 's'
                         && (ch = nextChar()) == 'e'
                         && (ch = nextChar()) == 'r'
                         && (ch = nextChar()) == 't') {
                            return directiveOrIdentifier(state == null ? PawnPreprocessorTokenId.ASSERT : PawnPreprocessorTokenId.KEYWORD_ASSERT);
                        }
                        break;
                }
                
                return finishIdentifierOrTag(ch);
            case 'b':
                if ((ch = nextChar()) == 'r'
                 && (ch = nextChar()) == 'e'
                 && (ch = nextChar()) == 'a'
                 && (ch = nextChar()) == 'k')
                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_BREAK);
                return finishIdentifierOrTag(ch);
            case 'c':
                switch (ch = nextChar()) {
                    case 'a':
                        if ((ch = nextChar()) == 's'
                         && (ch = nextChar()) == 'e')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_CASE);
                        break;
                    case 'o':
                        switch (ch = nextChar()) {
                            case 'd':
                                if ((ch = nextChar()) == 'e'
                                 && (ch = nextChar()) == 'p'
                                 && (ch = nextChar()) == 'a'
                                 && (ch = nextChar()) == 'g'
                                 && (ch = nextChar()) == 'e')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_CODEPAGE);
                                break;
                            case 'n':
                                switch (ch = nextChar()) {
                                    case 's':
                                        if ((ch = nextChar()) == 't')
                                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_CONST);
                                        break;
                                    case 't':
                                        if ((ch = nextChar()) == 'i'
                                         && (ch = nextChar()) == 'n'
                                         && (ch = nextChar()) == 'u'
                                         && (ch = nextChar()) == 'e')
                                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_CONTINUE);
                                        break;
                                }
                                break;
                        }
                        
                        break;
                    case 't':
                        if ((ch = nextChar()) == 'r'
                         && (ch = nextChar()) == 'l'
                         && (ch = nextChar()) == 'c'
                         && (ch = nextChar()) == 'h'
                         && (ch = nextChar()) == 'a'
                         && (ch = nextChar()) == 'r')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_CTRLCHAR);
                        break;
                }
            
                return finishIdentifierOrTag(ch);
            case 'd':
                switch (ch = nextChar()) {
                    case 'e':
                        switch (ch = nextChar()) {
                            case 'f':
                                switch (ch = nextChar()) {
                                    case 'a':
                                        if ((ch = nextChar()) == 'u'
                                         && (ch = nextChar()) == 'l'
                                         && (ch = nextChar()) == 't')
                                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_DEFAULT);
                                        break;
                                    case 'i':
                                        if ((ch = nextChar()) == 'n'
                                         && (ch = nextChar()) == 'e') {
                                            switch (ch = nextChar()) {
                                                case 'd':
                                                    return directiveOrIdentifier(PawnPreprocessorTokenId.IF_DEFINED);
                                                default:
                                                    return directiveOrIdentifier(PawnPreprocessorTokenId.DEFINE, ch);
                                            }
                                        }
                                        break;
                                }
                                
                                break;
                            case 'p':
                                if ((ch = nextChar()) == 'r'
                                 && (ch = nextChar()) == 'e'
                                 && (ch = nextChar()) == 'c'
                                 && (ch = nextChar()) == 'a'
                                 && (ch = nextChar()) == 't'
                                 && (ch = nextChar()) == 'e'
                                 && (ch = nextChar()) == 'd')
                                   return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_DEPRECATED);
                                break;
                        }
                    
                        break;
                    case 'y':
                        if ((ch = nextChar()) == 'n'
                         && (ch = nextChar()) == 'a'
                         && (ch = nextChar()) == 'm'
                         && (ch = nextChar()) == 'i'
                         && (ch = nextChar()) == 'c')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_DYNAMIC);
                        break;
                    case 'o':
                        return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_DO);
                }
                
                return finishIdentifierOrTag(ch);
            case 'e':
                switch (ch = nextChar()) {
                    case 'l':
                        if ((ch = nextChar()) == 's'
                         && (ch = nextChar()) == 'e') {
                            switch (ch = nextChar()) {
                                case 'i':
                                    if ((ch = nextChar()) == 'f')
                                        return directiveOrIdentifier(PawnPreprocessorTokenId.ELSEIF);
                                    break;
                                default:
                                    return directiveOrIdentifier(state == null ? PawnPreprocessorTokenId.ELSE : PawnPreprocessorTokenId.KEYWORD_ELSE);
                            }
                        }
                        
                        break;
                    case 'n':
                        switch (ch = nextChar()) {
                            case 'd':
                                if ((ch = nextChar()) == 'i') {
                                    switch (ch = nextChar()) {
                                        case 'f':
                                            return directiveOrIdentifier(PawnPreprocessorTokenId.ENDIF);
                                        case 'n':
                                            if ((ch = nextChar()) == 'p'
                                             && (ch = nextChar()) == 'u'
                                             && (ch = nextChar()) == 't')
                                                return directiveOrIdentifier(PawnPreprocessorTokenId.ENDINPUT);
                                    }
                                }

                                break;
                            case 'u':
                                if ((ch = nextChar()) == 'm')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_ENUM);
                                break;
                        }
                    
                        break;
                    case 'r':
                        if ((ch = nextChar()) == 'r'
                         && (ch = nextChar()) == 'o'
                         && (ch = nextChar()) == 'r')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.ERROR);
                        break;
                }
            
                return finishIdentifierOrTag(ch);
            case 'f':
                switch (ch = nextChar()) {
                    case 'a':
                        if ((ch = nextChar()) == 'l'
                         && (ch = nextChar()) == 's'
                         && (ch = nextChar()) == 'e')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_FALSE);
                        break;
                    case 'o':

                        if ((ch = nextChar()) == 'r') {
                            switch (ch = nextChar()) {
                                case 'w':
                                    if ((ch = nextChar()) == 'a'
                                     && (ch = nextChar()) == 'r'
                                     && (ch = nextChar()) == 'd')
                                        return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_FORWARD);
                                    break;
                                default:
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_FOR, ch);
                            }
                        }
                        break;
                }

                return finishIdentifierOrTag(ch);
            case 'g':
                    if ((ch = nextChar()) == 'o'
                     && (ch = nextChar()) == 't'
                     && (ch = nextChar()) == 'o')
                        return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_GOTO);
                    return finishIdentifierOrTag(ch);
                
            case 'i':
                switch (ch = nextChar()) {
                    case 'f':
                        return directiveOrIdentifier(PawnPreprocessorTokenId.IF);
                    case 'n':
                        if ((ch = nextChar()) == 'c'
                         && (ch = nextChar()) == 'l'
                         && (ch = nextChar()) == 'u'
                         && (ch = nextChar()) == 'd'
                         && (ch = nextChar()) == 'e')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.INCLUDE);
                }
            
                return finishIdentifierOrTag(ch);
            case 'l':
                switch (ch = nextChar()) {
                    case 'i':
                        switch (ch = nextChar()) {
                            case 'b':
                                if ((ch = nextChar()) == 'r'
                                 && (ch = nextChar()) == 'a'
                                 && (ch = nextChar()) == 'r'
                                 && (ch = nextChar()) == 'y')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_LIBRARY);
                                break;
                            case 'n':
                                if ((ch = nextChar()) == 'e')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.LINE);
                                break;
                        }
                    
                        break;
                    case 'o':
                        if ((ch = nextChar()) == 'a'
                         && (ch = nextChar()) == 'd'
                         && (ch = nextChar()) == 'l'
                         && (ch = nextChar()) == 'i'
                         && (ch = nextChar()) == 'b')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_LOADLIB);
                        break;
                }
                
                return finishIdentifierOrTag(ch);
                
            case 'n':
                switch (ch = nextChar()) {
                    case 'a':
                        if ((ch = nextChar()) == 't'
                         && (ch = nextChar()) == 'i'
                         && (ch = nextChar()) == 'v'
                         && (ch = nextChar()) == 'e')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_NATIVE);
                        break;
                    case 'e':
                        if ((ch = nextChar()) == 'w') {
                            switch (ch = nextChar()) {
                                case 'd':
                                    if ((ch = nextChar()) == 'e'
                                     && (ch = nextChar()) == 'c'
                                     && (ch = nextChar()) == 'l'
                                     && (ch = nextChar()) == 's')
                                        return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_NEWDECLS);
                                    break;
                                default:
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_NEW, ch);
                            }
                        }
                        break;
                }
                return finishIdentifierOrTag(ch);
                
            case 'o':
                switch (ch = nextChar()) {
                    case 'p':
                        if ((ch = nextChar()) == 'e'
                         && (ch = nextChar()) == 'r'
                         && (ch = nextChar()) == 'a'
                         && (ch = nextChar()) == 't'
                         && (ch = nextChar()) == 'o'
                         && (ch = nextChar()) == 'r')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_OPERATOR);
                        break;
                    case 'v':
                        if ((ch = nextChar()) == 'e'
                         && (ch = nextChar()) == 'r'
                         && (ch = nextChar()) == 'l'
                         && (ch = nextChar()) == 'a'
                         && (ch = nextChar()) == 'y')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_OVERLAY);
                        break;
                }
                return finishIdentifierOrTag(ch);
            case 'p':
                switch (ch = nextChar()) {
                    case 'r':
                        if ((ch = nextChar()) == 'a'
                         && (ch = nextChar()) == 'g'
                         && (ch = nextChar()) == 'm'
                         && (ch = nextChar()) == 'a')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA);
                        break;
                    case 'u':
                        if ((ch = nextChar()) == 'b'
                         && (ch = nextChar()) == 'l'
                         && (ch = nextChar()) == 'i'
                         && (ch = nextChar()) == 'c')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_PUBLIC);
                        break;
                }
                return finishIdentifierOrTag(ch);
            case 'r':
                switch (ch = nextChar()) {
                    case 'a':
                        if ((ch = nextChar()) == 't'
                         && (ch = nextChar()) == 'i'
                         && (ch = nextChar()) == 'o'
                         && (ch = nextChar()) == 'n'
                         && (ch = nextChar()) == 'a'
                         && (ch = nextChar()) == 'l')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_RATIONAL);
                        break;
                    case 'e':
                        switch (ch = nextChar()) {
                            case 'q':
                                if ((ch = nextChar()) == 'l'
                                 && (ch = nextChar()) == 'i'
                                 && (ch = nextChar()) == 'b')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_REQLIB);
                                break;
                            case 't':
                                if ((ch = nextChar()) == 'u'
                                 && (ch = nextChar()) == 'r'
                                 && (ch = nextChar()) == 'n')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_RETURN);
                                break;
                        }
                        break;
                }
            
                return finishIdentifierOrTag(ch);
            case 's':
                switch (ch = nextChar()) {
                    case 'e':
                        switch (ch = nextChar()) {
                            case 'c':
                                if ((ch = nextChar()) == 't'
                                 && (ch = nextChar()) == 'i'
                                 && (ch = nextChar()) == 'o'
                                 && (ch = nextChar()) == 'n')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.SECTION);
                                break;
                            case 'm':
                                if ((ch = nextChar()) == 'i'
                                 && (ch = nextChar()) == 'c'
                                 && (ch = nextChar()) == 'o'
                                 && (ch = nextChar()) == 'l'
                                 && (ch = nextChar()) == 'o'
                                 && (ch = nextChar()) == 'n')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_SEMICOLON);
                                break;
                        }
                        break;
                    case 'i':
                        if ((ch = nextChar()) == 'z'
                         && (ch = nextChar()) == 'e'
                         && (ch = nextChar()) == 'o'
                         && (ch = nextChar()) == 'f')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_SIZEOF);
                        break;
                    case 't':
                        switch (ch = nextChar()) {
                            case 'a':
                                if ((ch = nextChar()) == 't'
                                 && (ch = nextChar()) == 'i'
                                 && (ch = nextChar()) == 'c')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_STATIC);
                                break;
                            case 'o':
                                if ((ch = nextChar()) == 'c'
                                 && (ch = nextChar()) == 'k')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_STOCK);
                                break;
                        }
                        break;
                    case 'w':
                        if ((ch = nextChar()) == 'i'
                         && (ch = nextChar()) == 't'
                         && (ch = nextChar()) == 'c'
                         && (ch = nextChar()) == 'h')
                            return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_SWITCH);
                        break;
                }
                
                return finishIdentifierOrTag(ch);
            case 't':
                switch (ch = nextChar()) {
                    case 'a':
                        switch (ch = nextChar()) {
                            case 'b':
                                if ((ch = nextChar()) == 's'
                                 && (ch = nextChar()) == 'i'
                                 && (ch = nextChar()) == 'z'
                                 && (ch = nextChar()) == 'e')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.PRAGMA_TABSIZE);
                                break;
                            case 'g':
                                if ((ch = nextChar()) == 'o'
                                 && (ch = nextChar()) == 'f')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_TAGOF);
                                break;
                        }
                        break;
                    case 'r':
                        switch (ch = nextChar()) {
                            case 'u':
                                if ((ch = nextChar()) == 'e')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_TRUE);
                                break;
                            case 'y':
                                if ((ch = nextChar()) == 'i'
                                 && (ch = nextChar()) == 'n'
                                 && (ch = nextChar()) == 'c'
                                 && (ch = nextChar()) == 'l'
                                 && (ch = nextChar()) == 'u'
                                 && (ch = nextChar()) == 'd'
                                 && (ch = nextChar()) == 'e')
                                    return directiveOrIdentifier(PawnPreprocessorTokenId.TRYINCLUDE);
                                break;
                        }
                        break;
                }
                
                return finishIdentifierOrTag(ch);
            case 'u':
                if ((ch = nextChar()) == 'n'
                 && (ch = nextChar()) == 'd'
                 && (ch = nextChar()) == 'e'
                 && (ch = nextChar()) == 'f')
                    return directiveOrIdentifier(PawnPreprocessorTokenId.UNDEF);
                return finishIdentifierOrTag(ch);
                
            case 'w':
                    if ((ch = nextChar()) == 'h'
                     && (ch = nextChar()) == 'i'
                     && (ch = nextChar()) == 'l'
                     && (ch = nextChar()) == 'e')
                        return directiveOrIdentifier(PawnPreprocessorTokenId.KEYWORD_WHILE);
                    return finishIdentifierOrTag(ch);
                
            case '_':
                return directiveOrIdentifier(PawnPreprocessorTokenId.UNDERSCORE);
                
            // Rest of lowercase letters starting identifiers
            case 'h': case 'j': case 'k': case 'm': case 'q':
            case 'v': case 'x': case 'y': case 'z':
            
            // Uppercase letters starting identifiers
            case 'A': case 'B': case 'C': case 'D': case 'E':
            case 'F': case 'G': case 'H': case 'I': case 'J':
            case 'K': case 'L': case 'M': case 'N': case 'O':
            case 'P': case 'Q': case 'R': case 'S': case 'T':
            case 'U': case 'V': case 'W': case 'X': case 'Y':
            case 'Z':
                return finishIdentifierOrTag();
            case '\\':
                while (Character.isWhitespace(ch = nextChar())) {};
                backup(1);
                return token(PawnPreprocessorTokenId.LINE_CONCATENATION);
                /*switch (nextChar()) {
                    case '\r': consumeNewline();
                    case '\n':
                        return token(PawnPreprocessorTokenId.LINE_CONCATENATION);
                }
                
                return token(PawnPreprocessorTokenId.LEXICAL_ERROR);*/
            case '\t':
            case '\n':
            case '\f':
            case '\r':
                return finishWhitespace();
            case ' ':
                ch = nextChar();
                if (ch == EOF || !Character.isWhitespace(ch)) { // Return single space as flyweight token
                    backup(1);
                    return   readLength() == 1
                           ? tokenFactory.getFlyweightToken(PawnPreprocessorTokenId.WHITESPACE, " ")
                           : tokenFactory.createToken(PawnPreprocessorTokenId.WHITESPACE);
                }
                return finishWhitespace();
            case EOF:
                return null;
                // Taken from PawnLexer
            default:
                if (ch >= 0x80) { // lowSurr ones already handled above
                    if (Pawn.isPawnIdentifierStart(ch))
                        return finishIdentifierOrTag();
                    if (Character.isWhitespace(ch))
                        return finishWhitespace();
                }

                // Invalid char, assume allowed as these are preprocessor directives
                return token(PawnPreprocessorTokenId.OTHER_TEXT);
        }
    }
    
    // Taken from PawnLexer
    private Token<PawnPreprocessorTokenId> finishWhitespace() {
        while (true) {
            int ch = nextChar();
            // There should be no surrogates possible for whitespace
            // so do not call translateSurrogates()
            if (ch == EOF || !Character.isWhitespace(ch)) {
                backup(1);
                return token(PawnPreprocessorTokenId.WHITESPACE);
            }
        }
    }
    
    private Token<PawnPreprocessorTokenId> directiveOrIdentifier(PawnPreprocessorTokenId keywordId) {
        return directiveOrIdentifier(keywordId, nextChar());
    }

    private Token<PawnPreprocessorTokenId> directiveOrIdentifier(PawnPreprocessorTokenId keywordId, int ch) {
        // Check whether the given char is non-ident and if so then return keyword
        if (ch == EOF || !Pawn.isPawnIdentifierPart(ch)) {
            // For surrogate 2 chars must be backed up
            backup(1);
            if (state == null) {
                state = keywordId;
                return token(keywordId);
            } else if (state == PawnPreprocessorTokenId.PRAGMA && !PawnPreprocessorTokenId.language().tokenCategories(keywordId).contains("pragma")) {
                //return token(keywordId);
                return finishIdentifierOrTag();
            } else if (state == PawnPreprocessorTokenId.IF && !PawnPreprocessorTokenId.language().tokenCategories(keywordId).contains("if")) {
                //return token(keywordId);
                return finishIdentifierOrTag();
            }// else if (PawnPreprocessorTokenId.language().tokenCategories(keywordId).contains("keyword")) {
            //    return token(keywordId);
            //}
            
            //return finishIdentifierOrTag();
            return token(keywordId);
        } else // ch is identifier part
            return finishIdentifierOrTag();
    }
    
    private Token<PawnPreprocessorTokenId> finishIdentifierOrTag() {
        return finishIdentifierOrTag(nextChar());
    }
    
    private Token<PawnPreprocessorTokenId> finishIdentifierOrTag(int ch) {
        while (true) {
            if (ch == EOF || !Pawn.isPawnIdentifierPart(ch)) {
                if (ch == ':') {
                    return token(PawnPreprocessorTokenId.TAG);
                } else {
                    // For surrogate 2 chars must be backed up
                    backup(1);
                    return token(PawnPreprocessorTokenId.IDENTIFIER);
                }
            }
            ch = nextChar();
        }
    }
    
    private Token<PawnPreprocessorTokenId> finishNumberLiteral(int ch, boolean inFraction) {
        boolean afterDigit = true;
        while (true) {
            switch (ch) {
                case '.':
                    if (!inFraction) {
                        inFraction = true;
                        afterDigit = false;
                    } else { // two dots in the literal
                        return token(PawnPreprocessorTokenId.FLOAT_LITERAL_INVALID);
                    }
                    break;
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    afterDigit = true;
                    break;
                case 'e': // exponent part
                    return finishFloatExponent();
                default:
                    backup(1);
                    return token(inFraction ? PawnPreprocessorTokenId.FLOAT_LITERAL
                            : PawnPreprocessorTokenId.CELL_LITERAL);
            }
            ch = nextChar();
        }
    }
    
    private Token<PawnPreprocessorTokenId> finishFloatExponent() {
        int ch = nextChar();
        if (ch == '-') {
            ch = nextChar();
        }
        
        if (ch < '0' || '9' < ch) {
            return token(PawnPreprocessorTokenId.FLOAT_LITERAL_INVALID);
        }
        
        do {
            ch = nextChar();
        } while ('0' <= ch && ch <= '9'); // reading exponent
        
        backup(1);
        return token(PawnPreprocessorTokenId.FLOAT_LITERAL);
    }
    
    @Override
    public void release() {
        //...
    }
    
    @Override
    protected Token<PawnPreprocessorTokenId> token(PawnPreprocessorTokenId id) {
        String fixedText = id.fixedText();
        return (fixedText != null && fixedText.length() == readLength())
                ? tokenFactory.getFlyweightToken(id, fixedText)
                : tokenFactory.createToken(id);
    }
}
