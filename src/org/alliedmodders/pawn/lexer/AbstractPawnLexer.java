package org.alliedmodders.pawn.lexer;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

public abstract class AbstractPawnLexer<T extends TokenId> implements Lexer<T> {
    public static final int EOF = LexerInput.EOF;
    
    private final LexerInput input;
    private final TokenFactory<T> tokenFactory;
    
    private int currentLength = -1;
    
    AbstractPawnLexer(LexerRestartInfo<T> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
    }
    
    protected int currentLength() {
        return currentLength;
    }
    
    protected int nextChar() {
        currentLength = 1;
        return input.read();
    }
    
    protected void backup(int num) {
        input.backup(num);
    }
    
    protected int readLength() {
        return input.readLength();
    }
    
    protected void consumeNewline() {
        if (nextChar() != '\n') {
            backup(1);
        }
    }
    
    protected Token<T> token(T tokenId) {
        return tokenFactory.createToken(tokenId);
    }
    
    protected Token<T> token(T tokenId, int length) {
        return tokenFactory.createToken(tokenId, length);
    }
}
