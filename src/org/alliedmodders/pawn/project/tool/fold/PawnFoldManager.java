package org.alliedmodders.pawn.project.tool.fold;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.alliedmodders.pawn.lexer.PawnTokenId;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.editor.fold.FoldHierarchy;
import org.netbeans.api.editor.fold.FoldTemplate;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldInfo;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public class PawnFoldManager implements FoldManager {
    
    public static final FoldType COMMENT_FOLD_TYPE = FoldType.COMMENT;
    public static final FoldType DOC_COMMENT_FOLD_TYPE = FoldType.DOCUMENTATION;
    public static final FoldType CODE_BLOCK = FoldType.CODE_BLOCK;
    
    private FoldOperation operation;
    private FileObject file;
    private boolean first;
    private Collection<FoldInfo> foldInfo = new ArrayList<>();

    @Override
    public void init(FoldOperation operation) {
        this.operation = operation;
    }

    @Override
    public synchronized void initFolds(FoldHierarchyTransaction transaction) {
        FoldHierarchy hierarchy = operation.getHierarchy();
        Document doc = hierarchy.getComponent().getDocument();
        TokenHierarchy<Document> tokenHierarchy = TokenHierarchy.get(doc);
        TokenSequence<PawnTokenId> tokenSequence = tokenHierarchy.tokenSequence(PawnTokenId.language());
        
        Deque<Integer> bracesMatcher = new ArrayDeque<>();
        
        int start = 0;
        int end = 0;
        int offset = 0;
        Fold fold = null;
        FoldType type = null;
        Token<PawnTokenId> token;
        while (tokenSequence.moveNext()) {
            offset = tokenSequence.offset();
            token = tokenSequence.token();
            try {
                switch (token.id()) {
                    case DOC_COMMENT:
                        type = DOC_COMMENT_FOLD_TYPE;
                        start = offset;
                        end = start + token.length();
                        fold = operation.addToHierarchy(
                                type,
                                start,
                                end,
                                null,
                                FoldTemplate.DEFAULT_BLOCK,
                                "/** Documentation ... */",
                                null,
                                transaction);
                        foldInfo.add(FoldInfo.range(start, end, type));
                        break;
                    case BLOCK_COMMENT:
                        type = COMMENT_FOLD_TYPE;
                        start = offset;
                        end = start + token.length();
                        fold = operation.addToHierarchy(
                                type,
                                start,
                                end,
                                null,
                                FoldTemplate.DEFAULT,
                                "/* ... */",
                                null,
                                transaction);
                        foldInfo.add(FoldInfo.range(start, end, type));
                        break;
                    case LBRACE:
                        bracesMatcher.addLast(offset);
                        break;
                    case RBRACE:
                        if (bracesMatcher.isEmpty()) {
                            break;
                        } else if (bracesMatcher.size() > 1) {
                            bracesMatcher.removeLast();
                            break;
                        }
                        
                        type = CODE_BLOCK;
                        start = bracesMatcher.removeLast();
                        end = offset + token.length();
                        fold = operation.addToHierarchy(
                                type,
                                start,
                                end,
                                null,
                                FoldTemplate.DEFAULT_BLOCK,
                                "{ ... }",
                                null,
                                transaction);
                        foldInfo.add(FoldInfo.range(start, end, type));
                        break;
                }
            } catch (BadLocationException e) {
                Exceptions.printStackTrace(e);
            }
        }
    }
    
    @Override
    public void insertUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {
    }

    @Override
    public void removeUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {
    }

    @Override
    public void changedUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {
    }

    @Override
    public void removeEmptyNotify(Fold fold) {
    }

    @Override
    public void removeDamagedNotify(Fold fold) {
    }

    @Override
    public void expandNotify(Fold fold) {
    }

    @Override
    public synchronized void release() {
    }
}
