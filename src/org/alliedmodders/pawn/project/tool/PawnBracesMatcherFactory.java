package org.alliedmodders.pawn.project.tool;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.mimelookup.MimeRegistrations;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.bracesmatching.support.BracesMatcherSupport;

@MimeRegistrations(value = {
    @MimeRegistration(
        mimeType="text/x-pawn",
        service=BracesMatcherFactory.class),
    @MimeRegistration(
        mimeType="text/x-pawn-doc",
        service=BracesMatcherFactory.class),
    @MimeRegistration(
        mimeType="text/x-pawn-preprocessor",
        service=BracesMatcherFactory.class),
    @MimeRegistration(
        mimeType="text/x-pawn-string-literal",
        service=BracesMatcherFactory.class),
    @MimeRegistration(
        mimeType="text/x-pawn-character-literal",
        service=BracesMatcherFactory.class),
})
public class PawnBracesMatcherFactory implements BracesMatcherFactory {

    @Override
    public BracesMatcher createMatcher(MatcherContext context) {
        return BracesMatcherSupport.characterMatcher(context, -1, -1,
            '(', ')',
            '{', '}',
            '[', ']',
            '<', '>'
        );
    }
    
}
