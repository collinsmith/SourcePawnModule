package org.alliedmodders.pawn.lexer;

import org.alliedmodders.pawn.Pawn;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

public class PawnLexer extends AbstractPawnLexer<PawnTokenId> {
    
    private static final int EOF = LexerInput.EOF;
    
    private final TokenFactory<PawnTokenId> tokenFactory;
        
    public PawnLexer(LexerRestartInfo<PawnTokenId> info) {
        super(info);
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // never set to non-null value in state()
    }

    @Override
    public Object state() {
        return null; // always in default state after token recognition
    }
    
    @Override
    @SuppressWarnings("fallthrough")
    public Token<PawnTokenId> nextToken() {
        while (true) {
            int ch = nextChar();
            PawnTokenId lookupId = null;
            switch (ch) {
                case '#': // in preprocessor directive
                    while (true) {
                        switch (nextChar()) {
                            case '\\':
                                while (Character.isWhitespace(nextChar())) {};
                                backup(1);
                                break;
                            case '\r': consumeNewline();
                            case '\n':
                            case EOF:
                                return tokenFactory.createToken(PawnTokenId.PREPROCESSOR_DIRECTIVE,
                                        readLength(), PartType.START);
                        }
                    }
                case '"': // string literal
                    if (lookupId == null) {
                        lookupId = PawnTokenId.STRING_LITERAL;
                    }
                
                    while (true) {
                        switch (nextChar()) {
                            case '"':
                                return token(lookupId);
                            case '\\':
                                while (Character.isWhitespace(nextChar())) {};
                                break;
                            case '\r': consumeNewline();
                            case '\n':
                            case EOF:
                                // TODO: return EOL token
                                return tokenFactory.createToken(lookupId,
                                        readLength(), PartType.START);
                        }
                    }
                case '\'': // char literal
                    while (true) {
                        switch (nextChar()) {
                            case '\'':
                                return token(PawnTokenId.CHAR_LITERAL);
                            case '\\':
                                while (Character.isWhitespace(nextChar())) {};
                                break;
                            case '\r': consumeNewline();
                            case '\n':
                            case EOF:
                                // TODO: return EOL token
                                return tokenFactory.createToken(PawnTokenId.CHAR_LITERAL,
                                        readLength(), PartType.START);
                        }
                    }
                case '/':
                    switch (nextChar()) {
                        case '/': //in single-line comment
                            while (true) {
                                switch (nextChar()) {
                                    case '\r': consumeNewline();
                                    case '\n':
                                    case EOF:
                                        return token(PawnTokenId.LINE_COMMENT);
                                }
                            }
                        case '=': // found /=
                            return token(PawnTokenId.SLASHEQ);
                        case '*': // in multi-line or doc comment
                            ch = nextChar();
                            if (ch == '*') { // either doc comment or empty multi-line comment /**/
                                ch = nextChar();
                                if (ch == '/') {
                                    return token(PawnTokenId.BLOCK_COMMENT);
                                }
                                
                                while (true) { // in doc comment
                                    while (ch == '*') {
                                        ch = nextChar();
                                        if (ch == '/') {
                                            return token(PawnTokenId.DOC_COMMENT);
                                        } else if (ch == EOF) {
                                            return tokenFactory.createToken(PawnTokenId.DOC_COMMENT,
                                                    readLength(), PartType.START);
                                        }
                                    }
                                    
                                    if (ch == EOF) {
                                        return tokenFactory.createToken(PawnTokenId.DOC_COMMENT,
                                                readLength(), PartType.START);
                                    }
                                    
                                    ch = nextChar();
                                }
                                
                            } else { // in multi-line comment (and not after '*')
                                while (true) {
                                    ch = nextChar();
                                    while (ch == '*') {
                                        ch = nextChar();
                                        if (ch == '/') {
                                            return token(PawnTokenId.BLOCK_COMMENT);
                                        } else if (ch == EOF) {
                                            return tokenFactory.createToken(PawnTokenId.BLOCK_COMMENT,
                                                    readLength(), PartType.START);
                                        }
                                    }
                                    
                                    if (ch == EOF) {
                                        return tokenFactory.createToken(PawnTokenId.BLOCK_COMMENT,
                                                readLength(), PartType.START);
                                    }
                                }
                            }
                    } // end of switch ()
                    
                    backup(1);
                    return token(PawnTokenId.SLASH);
                case '=':
                    if (nextChar() == '=') {
                        return token(PawnTokenId.EQEQ);
                    }
                
                    backup(1);
                    return token(PawnTokenId.EQ);
                case '>':
                    switch (nextChar()) {
                        case '>': // after >>
                            switch (ch = nextChar()) {
                                case '>': // after >>>
                                    if (nextChar() == '=')
                                        return token(PawnTokenId.GTGTGTEQ);
                                    backup(1);
                                    return token(PawnTokenId.GTGTGT);
                                case '=': // >>=
                                    return token(PawnTokenId.GTGTEQ);
                            }
                            backup(1);
                            return token(PawnTokenId.GTGT);
                        case '=': // >=
                            return token(PawnTokenId.GTEQ);
                    }
                    backup(1);
                    return token(PawnTokenId.GT);

                case '<':
                    switch (nextChar()) {
                        case '<': // after <<
                            if (nextChar() == '=')
                                return token(PawnTokenId.LTLTEQ);
                            backup(1);
                            return token(PawnTokenId.LTLT);
                        case '=': // <=
                            return token(PawnTokenId.LTEQ);
                    }
                    backup(1);
                    return token(PawnTokenId.LT);

                case '+':
                    switch (nextChar()) {
                        case '+':
                            return token(PawnTokenId.PLUSPLUS);
                        case '=':
                            return token(PawnTokenId.PLUSEQ);
                    }
                    backup(1);
                    return token(PawnTokenId.PLUS);

                case '-':
                    switch (nextChar()) {
                        case '-':
                            return token(PawnTokenId.MINUSMINUS);
                        case '=':
                            return token(PawnTokenId.MINUSEQ);
                    }
                    backup(1);
                    return token(PawnTokenId.MINUS);

                case '*':
                    switch (nextChar()) {
                        case '/': // invalid comment end - */
                            return token(PawnTokenId.INVALID_COMMENT_END);
                        case '=':
                            return token(PawnTokenId.STAREQ);
                    }
                    backup(1);
                    return token(PawnTokenId.STAR);

                case '|':
                    switch (nextChar()) {
                        case '|':
                            return token(PawnTokenId.BARBAR);
                        case '=':
                            return token(PawnTokenId.BAREQ);
                    }
                    backup(1);
                    return token(PawnTokenId.BAR);

                case '&':
                    switch (nextChar()) {
                        case '&':
                            return token(PawnTokenId.AMPAMP);
                        case '=':
                            return token(PawnTokenId.AMPEQ);
                    }
                    backup(1);
                    return token(PawnTokenId.AMP);

                case '%':
                    if (nextChar() == '=')
                        return token(PawnTokenId.PERCENTEQ);
                    backup(1);
                    return token(PawnTokenId.PERCENT);

                case '^':
                    if (nextChar() == '=')
                        return token(PawnTokenId.CARETEQ);
                    backup(1);
                    return token(PawnTokenId.CARET);

                case '!':
                    if (nextChar() == '=')
                        return token(PawnTokenId.BANGEQ);
                    backup(1);
                    return token(PawnTokenId.BANG);
                case '.':
                    if ((ch = nextChar()) == '.')
                        if (nextChar() == '.') { // ellipsis ...
                            return token(PawnTokenId.ELLIPSIS);
                        } else
                            backup(2);
                    else if ('0' <= ch && ch <= '9') { // float literal
                        return finishNumberLiteral(nextChar(), true);
                    } else
                        backup(1);
                    return token(PawnTokenId.DOT);
                case '~':
                    return token(PawnTokenId.TILDE);
                case ',':
                    return token(PawnTokenId.COMMA);
                case ';':
                    return token(PawnTokenId.SEMICOLON);
                case ':':
                    return token(PawnTokenId.COLON);
                case '?':
                    return token(PawnTokenId.QUESTION);
                case '(':
                    return token(PawnTokenId.LPAREN);
                case ')':
                    return token(PawnTokenId.RPAREN);
                case '[':
                    return token(PawnTokenId.LBRACKET);
                case ']':
                    return token(PawnTokenId.RBRACKET);
                case '{':
                    return token(PawnTokenId.LBRACE);
                case '}':
                    return token(PawnTokenId.RBRACE);
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
                                    return token(PawnTokenId.CELL_LITERAL);
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
                                    return token(PawnTokenId.CELL_LITERAL);
                            }
                        }
                    }
                    return finishNumberLiteral(ch, false);
                case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    return finishNumberLiteral(nextChar(), false);
                    
                // Keywords lexing    
                case 'a':
                    switch (ch = nextChar()) {
                        case 'n':
                            if ((ch = nextChar()) == 'y')
                                return keywordIdentifierOrTag(PawnTokenId.ANYTAG);
                            break;
                        case 's':
                            if ((ch = nextChar()) == 's'
                             && (ch = nextChar()) == 'e'
                             && (ch = nextChar()) == 'r'
                             && (ch = nextChar()) == 't')
                                return keywordIdentifierOrTag(PawnTokenId.ASSERT);
                            break;
                    }
                    
                    return finishIdentifierOrTag(ch);

                case 'b':
                    switch (ch = nextChar()) {
                        case 'o':
                            if ((ch = nextChar()) == 'o'
                             && (ch = nextChar()) == 'l') {
                                switch (ch = nextChar()) {
                                    case ':':
                                        return token(PawnTokenId.BOOLTAG);
                                    default:
                                        return keywordIdentifierOrTag(PawnTokenId.BOOL, ch);
                                }
                            }
                            break;
                        case 'r':
                            if ((ch = nextChar()) == 'e'
                             && (ch = nextChar()) == 'a'
                             && (ch = nextChar()) == 'k')
                                return keywordIdentifierOrTag(PawnTokenId.BREAK);
                            break;
                    }
                    
                    return finishIdentifierOrTag(ch);

                case 'c':
                    switch (ch = nextChar()) {
                        case 'a':
                            if ((ch = nextChar()) == 's'
                             && (ch = nextChar()) == 'e')
                                return keywordIdentifierOrTag(PawnTokenId.CASE);
                            break;
                        case 'h':
                            if ((ch = nextChar()) == 'a'
                             && (ch = nextChar()) == 'r')
                                return keywordIdentifierOrTag(PawnTokenId.CHAR);
                            break;
                        case 'o':
                            if ((ch = nextChar()) == 'n') {
                                switch (ch = nextChar()) {
                                    case 's':
                                        if ((ch = nextChar()) == 't')
                                            return keywordIdentifierOrTag(PawnTokenId.CONST);
                                        break;
                                    case 't':
                                        if ((ch = nextChar()) == 'i'
                                         && (ch = nextChar()) == 'n'
                                         && (ch = nextChar()) == 'u'
                                         && (ch = nextChar()) == 'e')
                                            return keywordIdentifierOrTag(PawnTokenId.CONTINUE);
                                        break;
                                }
                            }
                            break;
                    }
                    return finishIdentifierOrTag(ch);

                case 'd':
                    switch (ch = nextChar()) {
                        case 'e':
                            switch (ch = nextChar()) {
                                case 'c':
                                    if ((ch = nextChar()) == 'l')
                                        return keywordIdentifierOrTag(PawnTokenId.DECL);
                                    break;
                                case 'f':
                                    if ((ch = nextChar()) == 'a'
                                     && (ch = nextChar()) == 'u'
                                     && (ch = nextChar()) == 'l'
                                     && (ch = nextChar()) == 't')
                                        return keywordIdentifierOrTag(PawnTokenId.DEFAULT);
                                    break;
                            }
                            break;
                        case 'o':
                            return keywordIdentifierOrTag(PawnTokenId.DO);
                    }
                    return finishIdentifierOrTag(ch);

                case 'e':
                    switch (ch = nextChar()) {
                        case 'l':
                            if ((ch = nextChar()) == 's'
                             && (ch = nextChar()) == 'e')
                                return keywordIdentifierOrTag(PawnTokenId.ELSE);
                            break;
                        case 'n':
                            if ((ch = nextChar()) == 'u'
                             && (ch = nextChar()) == 'm')
                                return keywordIdentifierOrTag(PawnTokenId.ENUM);
                            break;
                    }
                    return finishIdentifierOrTag(ch);

                case 'f':
                    switch (ch = nextChar()) {
                        case 'a':
                            if ((ch = nextChar()) == 'l'
                             && (ch = nextChar()) == 's'
                             && (ch = nextChar()) == 'e')
                                return keywordIdentifierOrTag(PawnTokenId.FALSE);
                            break;
                        case 'l':
                            if ((ch = nextChar()) == 'o'
                             && (ch = nextChar()) == 'a'
                             && (ch = nextChar()) == 't')
                                return keywordIdentifierOrTag(PawnTokenId.FLOAT);
                            break;
                        case 'o':
                            if ((ch = nextChar()) == 'r') {
                                switch (ch = nextChar()) {
                                    case 'w':
                                        if ((ch = nextChar()) == 'a'
                                         && (ch = nextChar()) == 'r'
                                         && (ch = nextChar()) == 'd')
                                            return keywordIdentifierOrTag(PawnTokenId.FORWARD);
                                        break;
                                    default:
                                        return keywordIdentifierOrTag(PawnTokenId.FOR, ch);
                                }
                            }
                            break;
                    }
                    return finishIdentifierOrTag(ch);

                case 'g':
                    if ((ch = nextChar()) == 'o'
                     && (ch = nextChar()) == 't'
                     && (ch = nextChar()) == 'o')
                        return keywordIdentifierOrTag(PawnTokenId.GOTO);
                    return finishIdentifierOrTag(ch);
                    
                case 'i':
                    switch (ch = nextChar()) {
                        case 'f':
                            return keywordIdentifierOrTag(PawnTokenId.IF);
                        case 'n':
                            switch (ch = nextChar()) {
                                case 't':
                                    return keywordIdentifierOrTag(PawnTokenId.FORWARD);
                                default:
                                    return keywordIdentifierOrTag(PawnTokenId.IN, ch);
                            }
                    }
                    return finishIdentifierOrTag(ch);

                case 'm':
                    if ((ch = nextChar()) == 'e'
                     && (ch = nextChar()) == 't'
                     && (ch = nextChar()) == 'h'
                     && (ch = nextChar()) == 'o'
                     && (ch = nextChar()) == 'd'
                     && (ch = nextChar()) == 'm'
                     && (ch = nextChar()) == 'a'
                     && (ch = nextChar()) == 'p')
                       return keywordIdentifierOrTag(PawnTokenId.METHODMAP);
                    return finishIdentifierOrTag(ch);
                    
                case 'n':
                    switch (ch = nextChar()) {
                        case 'a':
                            if ((ch = nextChar()) == 't'
                             && (ch = nextChar()) == 'i'
                             && (ch = nextChar()) == 'v'
                             && (ch = nextChar()) == 'e')
                                return keywordIdentifierOrTag(PawnTokenId.NATIVE);
                            break;
                        case 'e':
                            if ((ch = nextChar()) == 'w')
                                return keywordIdentifierOrTag(PawnTokenId.NEW);
                            break;
                        case 'u':
                            if ((ch = nextChar()) == 'l'
                             && (ch = nextChar()) == 'l')
                                return keywordIdentifierOrTag(PawnTokenId.NULL);
                            break;
                    }
                    return finishIdentifierOrTag(ch);

                case 'o':
                    if ((ch = nextChar()) == 'p'
                     && (ch = nextChar()) == 'e'
                     && (ch = nextChar()) == 'r'
                     && (ch = nextChar()) == 'a'
                     && (ch = nextChar()) == 't'
                     && (ch = nextChar()) == 'o'
                     && (ch = nextChar()) == 'r')
                       return keywordIdentifierOrTag(PawnTokenId.OPERATOR);
                    return finishIdentifierOrTag(ch);
                    
                case 'p':
                    if ((ch = nextChar()) == 'u'
                     && (ch = nextChar()) == 'b'
                     && (ch = nextChar()) == 'l'
                     && (ch = nextChar()) == 'i'
                     && (ch = nextChar()) == 'c')
                       return keywordIdentifierOrTag(PawnTokenId.PUBLIC);
                    return finishIdentifierOrTag(ch);

                case 'r':
                    if ((ch = nextChar()) == 'e'
                     && (ch = nextChar()) == 't'
                     && (ch = nextChar()) == 'u'
                     && (ch = nextChar()) == 'r'
                     && (ch = nextChar()) == 'n')
                        return keywordIdentifierOrTag(PawnTokenId.RETURN);
                    return finishIdentifierOrTag(ch);

                case 's':
                    switch (ch = nextChar()) {
                        case 'i':
                            if ((ch = nextChar()) == 'z'
                             && (ch = nextChar()) == 'e'
                             && (ch = nextChar()) == 'o'
                             && (ch = nextChar()) == 'f')
                                return keywordIdentifierOrTag(PawnTokenId.SIZEOF);
                            break;
                        case 't':
                            switch (ch = nextChar()) {
                                case 'a':
                                    if ((ch = nextChar()) == 't') {
                                        switch (ch = nextChar()) {
                                            case 'e':
                                                return keywordIdentifierOrTag(PawnTokenId.STATE);
                                            case 'i':
                                                if ((ch = nextChar()) == 'c')
                                                    return keywordIdentifierOrTag(PawnTokenId.STATIC);
                                        }
                                    }
                                    
                                    break;
                                case 'o':
                                    if ((ch = nextChar()) == 'c'
                                     && (ch = nextChar()) == 'k')
                                        return keywordIdentifierOrTag(PawnTokenId.STOCK);
                                    break;
                            }
                            break;
                        case 'w':
                            if ((ch = nextChar()) == 'i'
                             && (ch = nextChar()) == 't'
                             && (ch = nextChar()) == 'c'
                             && (ch = nextChar()) == 'h')
                                return keywordIdentifierOrTag(PawnTokenId.SWITCH);
                            break;
                    }
                    return finishIdentifierOrTag(ch);

                case 't':
                    switch (ch = nextChar()) {
                        case 'a':
                            if ((ch = nextChar()) == 'g'
                             && (ch = nextChar()) == 'o'
                             && (ch = nextChar()) == 'f')
                                return keywordIdentifierOrTag(PawnTokenId.TAGOF);
                            break;
                        case 'h':
                            if ((ch = nextChar()) == 'i'
                             && (ch = nextChar()) == 's')
                                return keywordIdentifierOrTag(PawnTokenId.THIS);
                            break;
                        case 'r':
                            if ((ch = nextChar()) == 'u'
                             && (ch = nextChar()) == 'e')
                                return keywordIdentifierOrTag(PawnTokenId.TRUE);
                            break;
                    }
                    
                    return finishIdentifierOrTag(ch);

                case 'w':
                    if ((ch = nextChar()) == 'h'
                     && (ch = nextChar()) == 'i'
                     && (ch = nextChar()) == 'l'
                     && (ch = nextChar()) == 'e')
                        return keywordIdentifierOrTag(PawnTokenId.WHILE);
                    return finishIdentifierOrTag(ch);
                    
                case 'v':
                    switch (ch = nextChar()) {
                        case 'i':
                            if ((ch = nextChar()) == 'e'
                             && (ch = nextChar()) == 'w'
                             && (ch = nextChar()) == '_'
                             && (ch = nextChar()) == 'a'
                             && (ch = nextChar()) == 's')
                                return keywordIdentifierOrTag(PawnTokenId.VIEW_AS);
                            break;
                        case 'o':
                            if ((ch = nextChar()) == 'i'
                             && (ch = nextChar()) == 'd')
                                return keywordIdentifierOrTag(PawnTokenId.VOID);
                            break;
                    }
                    return finishIdentifierOrTag(ch);
                    
                case 'F':
                    if ((ch = nextChar()) == 'l'
                     && (ch = nextChar()) == 'o'
                     && (ch = nextChar()) == 'a'
                     && (ch = nextChar()) == 't')
                        return keywordIdentifierOrTag(PawnTokenId.FLOATTAG);
                    return finishIdentifierOrTag(ch);
                    
                case 'S':
                    if ((ch = nextChar()) == 't'
                     && (ch = nextChar()) == 'r'
                     && (ch = nextChar()) == 'i'
                     && (ch = nextChar()) == 'n'
                     && (ch = nextChar()) == 'g')
                        return keywordIdentifierOrTag(PawnTokenId.STRINGTAG);
                    return finishIdentifierOrTag(ch);
                
                case '_':
                    return keywordIdentifierOrTag(PawnTokenId.UNDERSCORE);
                    
                // Rest of lowercase letters starting identifiers
                case 'h': case 'j': case 'k': case 'l': case 'q':
                case 'u': case 'x': case 'y': case 'z':
                // Uppercase letters starting identifiers
                case 'A': case 'B': case 'C': case 'D': case 'E':
                case 'G': case 'H': case 'I': case 'J': case 'K':
                case 'L': case 'M': case 'N': case 'O': case 'P':
                case 'Q': case 'R': case 'T': case 'U': case 'V':
                case 'W': case 'X': case 'Y': case 'Z': case '@':
                    return finishIdentifierOrTag();
                    
                // All Character.isWhitespace(c) below 0x80 follow
                // ['\t' - '\r'] and [0x1c - ' ']
                /*case '\r': consumeNewline();
                case '\n':
                    return tokenFactory.createToken(PawnTokenId.EOL,
                            input.readLength());*/
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
                               ? tokenFactory.getFlyweightToken(PawnTokenId.WHITESPACE, " ")
                               : tokenFactory.createToken(PawnTokenId.WHITESPACE);
                    }
                    return finishWhitespace();

                case EOF:
                    return null;

                default:
                    if (ch >= 0x80) { // lowSurr ones already handled above
                        if (Pawn.isPawnIdentifierStart(ch))
                            return finishIdentifierOrTag();
                        if (Character.isWhitespace(ch))
                            return finishWhitespace();
                    }

                    // Invalid char
                    return token(PawnTokenId.ERROR);
            }
        }
    }
        
    private Token<PawnTokenId> finishWhitespace() {
        while (true) {
            int ch = nextChar();
            if (ch == EOF || !Character.isWhitespace(ch)) {
                backup(1);
                return tokenFactory.createToken(PawnTokenId.WHITESPACE);
            }
        }
    }
    
    private Token<PawnTokenId> finishIdentifierOrTag() {
        return finishIdentifierOrTag(nextChar());
    }
    
    private Token<PawnTokenId> finishIdentifierOrTag(int ch) {
        while (true) {
            if (ch == EOF || !Pawn.isPawnIdentifierPart(ch)) {
                if (ch == ':') {
                    return tokenFactory.createToken(PawnTokenId.TAG);
                } else {
                    backup(1);
                    return tokenFactory.createToken(PawnTokenId.IDENTIFIER);
                }
            }
            ch = nextChar();
        }
    }
    
    private Token<PawnTokenId> keywordIdentifierOrTag(PawnTokenId keywordId) {
        return keywordIdentifierOrTag(keywordId, nextChar());
    }

    private Token<PawnTokenId> keywordIdentifierOrTag(PawnTokenId keywordId, int ch) {
        // Check whether the given char is non-ident and if so then return keyword
        if (ch == EOF || !Character.isJavaIdentifierPart(ch)) {
            if (ch == ':') {
                if (keywordId == PawnTokenId.UNDERSCORE) {
                    return token(PawnTokenId._TAG);
                }
                
                return token(keywordId);
            } else if (!PawnTokenId.language().tokenCategories(keywordId).contains("tag")) {
                backup(1);
                return token(keywordId);
            } else
                return finishIdentifierOrTag();
        } else // ch is identifier part
            return finishIdentifierOrTag();
    }
    
    private Token<PawnTokenId> finishNumberLiteral(int ch, boolean inFraction) {
        boolean afterDigit = true;
        while (true) {
            switch (ch) {
                case '.':
                    if (!inFraction) {
                        inFraction = true;
                        afterDigit = false;
                    } else { // two dots in the literal
                        return token(PawnTokenId.FLOAT_LITERAL_INVALID);
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
                    return token(inFraction ? PawnTokenId.FLOAT_LITERAL
                            : PawnTokenId.CELL_LITERAL);
            }
            ch = nextChar();
        }
    }
    
    private Token<PawnTokenId> finishFloatExponent() {
        int ch = nextChar();
        if (ch == '-') {
            ch = nextChar();
        }
        
        if (ch < '0' || '9' < ch) {
            return token(PawnTokenId.FLOAT_LITERAL_INVALID);
        }
        
        do {
            ch = nextChar();
        } while ('0' <= ch && ch <= '9'); // reading exponent
        
        backup(1);
        return token(PawnTokenId.FLOAT_LITERAL);
    }
    
    @Override
    protected Token<PawnTokenId> token(PawnTokenId id) {
        String fixedText = id.fixedText();
        return (fixedText != null && fixedText.length() == readLength())
                ? tokenFactory.getFlyweightToken(id, fixedText)
                : tokenFactory.createToken(id);
    }

    @Override
    public void release() {
        //...
    }
}
