package org.alliedmodders.pawn.project.tool.fold;

import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldManagerFactory;

public class PawnFoldManagerFactory implements FoldManagerFactory {

    @Override
    public FoldManager createFoldManager() {
        return null;
        //return new PawnFoldManager();
    }

}
