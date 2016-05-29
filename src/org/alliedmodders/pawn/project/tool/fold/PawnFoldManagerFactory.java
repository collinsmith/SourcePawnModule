package org.alliedmodders.pawn.project.tool.fold;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldManagerFactory;

//@MimeRegistration(mimeType="text/x-pawn",service=FoldManagerFactory.class)
public class PawnFoldManagerFactory implements FoldManagerFactory {

    @Override
    public FoldManager createFoldManager() {
        //return new PawnFoldManager();
        return null;
    }

}
