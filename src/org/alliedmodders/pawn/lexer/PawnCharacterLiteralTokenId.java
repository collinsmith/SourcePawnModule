package org.alliedmodders.pawn.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public enum PawnCharacterLiteralTokenId implements TokenId {
    
    TEXT("character"),
    
    BACKSPACE("character-escape"),
    FORM_FEED("character-escape"),
    NEWLINE("character-escape"),
    CR("character-escape"),
    TAB("character-escape"),
    SINGLE_QUOTE("character-escape"),
    DOUBLE_QUOTE("character-escape"),
    BACKSLASH("character-escape"),
    
    PERCENT_ESCAPE("character-escape"),
    PERCENT_ESCAPE_INVALID("character-escape-invalid"),
    
    DECIMAL_ESCAPE("character-escape"),
    DECIMAL_ESCAPE_INVALID("character-escape-invalid"),
    
    HEXADECIMAL_ESCAPE("character-escape"),
    HEXADECIMAL_ESCAPE_INVALID("character-escape-invalid"),
    
    ESCAPE_SEQUENCE_INVALID("character-escape-invalid"),
    
    LINE_CONCATENATION("line-concatenation"),
    ;
    
    private final String primaryCategory;
        
    private PawnCharacterLiteralTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }
    
    public static Language<PawnCharacterLiteralTokenId> language() {
        return language;
    }
    
    private static final Language<PawnCharacterLiteralTokenId> language = new LanguageHierarchy<PawnCharacterLiteralTokenId>() {

        @Override
        protected String mimeType() {
            return "text/x-pawn-character-literal";
        }
        
        @Override
        protected Collection<PawnCharacterLiteralTokenId> createTokenIds() {
            return EnumSet.allOf(PawnCharacterLiteralTokenId.class);
        }

        @Override
        protected Map<String, Collection<PawnCharacterLiteralTokenId>> createTokenCategories() {
            return super.createTokenCategories();
        }

        @Override
        protected Lexer<PawnCharacterLiteralTokenId> createLexer(LexerRestartInfo<PawnCharacterLiteralTokenId> info) {
            return new PawnStringLiteralLexer<PawnCharacterLiteralTokenId>(info, false);
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<PawnCharacterLiteralTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            return super.embedding(token, languagePath, inputAttributes);
        }
        
    }.language();
}
