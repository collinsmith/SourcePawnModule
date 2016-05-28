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

public enum PawnDocTokenId implements TokenId {

    IDENTIFIER("pawn-doc-identifier"),
    
    TAG("pawn-doc-tag"),
    HTML_TAG("html-tag"),
    
    HASH("pawn-doc-hash"),
    
    TEXT("comment"),
    ;

    private final String primaryCategory;
    
    private PawnDocTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }
    
    @Override
    public String primaryCategory() {
        return primaryCategory;
    }
    
    public static Language<PawnDocTokenId> language() {
        return language;
    }
    
    private static final Language<PawnDocTokenId> language = new LanguageHierarchy<PawnDocTokenId>() {

        @Override
        protected String mimeType() {
            return "text/x-pawn-doc";
        }
        
        @Override
        protected Collection<PawnDocTokenId> createTokenIds() {
            return EnumSet.allOf(PawnDocTokenId.class);
        }

        @Override
        protected Map<String, Collection<PawnDocTokenId>> createTokenCategories() {
            return super.createTokenCategories();
        }

        @Override
        protected Lexer<PawnDocTokenId> createLexer(LexerRestartInfo<PawnDocTokenId> info) {
            return new PawnDocLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<PawnDocTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            return super.embedding(token, languagePath, inputAttributes);
        }
        
    }.language();
}
