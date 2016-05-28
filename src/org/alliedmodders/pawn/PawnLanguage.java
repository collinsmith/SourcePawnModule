package org.alliedmodders.pawn;

import org.alliedmodders.pawn.lexer.PawnTokenId;
import org.alliedmodders.pawn.parser.PawnParser;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;

@LanguageRegistration(mimeType = "text/x-pawn")
public class PawnLanguage extends DefaultLanguageConfig {
    @Override
    public Language<PawnTokenId> getLexerLanguage() {
	return PawnTokenId.language();
    }
    
    @Override
    public String getDisplayName() {
	return "Pawn";
    }
    
//    @Override
//    public Parser getParser() {
//	return new PawnParser();
//    }
}
