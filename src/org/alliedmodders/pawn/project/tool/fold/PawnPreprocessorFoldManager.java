package org.alliedmodders.pawn.project.tool.fold;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.alliedmodders.pawn.lexer.PawnPreprocessorTokenId;
import org.alliedmodders.pawn.lexer.PawnTokenId;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.editor.fold.FoldHierarchy;
import org.netbeans.api.editor.fold.FoldTemplate;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.editor.fold.FoldUtilities;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

public class PawnPreprocessorFoldManager implements FoldManager {
    
    public static final FoldType CODE_BLOCK_FOLD_TYPE = FoldType.CODE_BLOCK;
    
    private static final int FOLD_UPDATE_DELAY = 250;

    private final List<Fold> currentFolds = new ArrayList<>(32);
    
    private final RequestProcessor RP
            = new RequestProcessor(PawnPreprocessorFoldManager.class.getSimpleName(),
                    Runtime.getRuntime().availableProcessors());
    
    private final RequestProcessor.Task FOLDS_UPDATE_TASK
            = RP.create(new Runnable() {
        @Override
        public void run() {
            try {
                documentDirty = false;
                updateFolds();
            } catch (BadLocationException e) {
                Exceptions.printStackTrace(e);
            }
        }
    });
    
    private FoldOperation operation;
    private boolean documentDirty = true;
    private BaseDocument doc;
    
    protected FoldOperation getOperation() {
        return operation;
    }
    
    private BaseDocument getDocument() {
        return doc;
    }
    
    private void updateFolds() throws BadLocationException {
        final FoldHierarchy hierarchy = getOperation().getHierarchy();
        final Set<Fold> zombies = new HashSet<>();
        final Set<FoldInfo> newborns = new HashSet<>();
        
        final BadLocationException[] ble = new BadLocationException[1];
        getDocument().render(new Runnable() {

            @Override
            public void run() {
                try {
                    hierarchy.lock();
                    List<FoldInfo> generated = generateFolds();
                    if (generated.isEmpty()) {
                        return;
                    }
                    
                    mergeFolds(generated, zombies, newborns);
                } catch (BadLocationException e) {
                    ble[0] = e;
                } finally {
                    hierarchy.unlock();
                }
            }
            
        });
        
        if (ble[0] != null) {
            throw ble[0];
        }
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                (getDocument()).readLock();
                try {
                    hierarchy.lock();
                    try {
                        FoldHierarchyTransaction transaction
                                = getOperation().openTransaction();
                        try {
                            Iterator<Fold> zombieFolds = zombies.iterator();
                            while (zombieFolds.hasNext()) {
                                Fold fold = zombieFolds.next();
                                getOperation().removeFromHierarchy(fold,
                                        transaction);
                                currentFolds.remove(fold);
                            }
                            
                            Iterator<FoldInfo> newbornFolds = newborns.iterator();
                            while (newbornFolds.hasNext()) {
                                FoldInfo foldInfo = newbornFolds.next();
                                if (foldInfo.getStartOffset()>= 0
                                 && foldInfo.getEndOffset()>= 0
                                 && foldInfo.getStartOffset()< foldInfo.getEndOffset()
                                 && foldInfo.getEndOffset()<= getDocument().getLength()) {
                                    try {
                                        currentFolds.add(getOperation()
                                                .addToHierarchy(
                                                        foldInfo.foldType,
                                                        foldInfo.getStartOffset(),
                                                        foldInfo.getEndOffset(),
                                                        null,
                                                        foldInfo.foldTemplate,
                                                        foldInfo.description,
                                                        null,
                                                        transaction));
                                    } catch (BadLocationException e) {
                                    }
                                }
                            }
                        } finally {
                            transaction.commit();
                        }
                    } finally {
                        hierarchy.unlock();
                    }
                } finally {
                    getDocument().readUnlock();
                }
            }
            
        });
        
    }
    
    private boolean isInitiallyCollapsed(FoldType foldType) {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private List<FoldInfo> generateFolds() {
        BaseDocument bdoc = getDocument();
        List<FoldInfo> found = new ArrayList<>(32);
        TokenHierarchy<Document> tokenHierarchy = TokenHierarchy.get(bdoc);
        TokenSequence<PawnTokenId> tokenSequence
                = tokenHierarchy.tokenSequence(PawnTokenId.language());
        Deque<Integer> blockMatcher = new ArrayDeque<>();
        int start = 0;
        int end = 0;
        int offset = 0;
        Fold fold = null;
        FoldType type = null;
        Token<PawnTokenId> token;
        Token<PawnPreprocessorTokenId> embeddedToken;
        TokenSequence<PawnPreprocessorTokenId> embeddedTokenSequence;
        while (tokenSequence.moveNext()) {
            if (documentDirty) {
                return Collections.EMPTY_LIST;
            }
            
            token = tokenSequence.token();
            if (token.id() != PawnTokenId.PREPROCESSOR_DIRECTIVE) {
                continue;
            }
            
            embeddedTokenSequence = tokenSequence.embedded(
                    PawnPreprocessorTokenId.language());
            boolean lookingForEOL = false;
            while (embeddedTokenSequence.moveNext()) {
                offset = embeddedTokenSequence.offset();
                embeddedToken = embeddedTokenSequence.token();
                try {
                    switch (embeddedToken.id()) {
                        case IF:
                            lookingForEOL = true;
                            break;
                        case EOL:
                            if (!lookingForEOL) {
                                break;
                            }
                            
                            blockMatcher.addLast(offset);
                            lookingForEOL = false;
                            break;
                        case ENDIF:
                            if (blockMatcher.isEmpty()) {
                                break;
                            } else if (blockMatcher.size() > 1) {
                                blockMatcher.removeLast();
                                break;
                            }

                            start = blockMatcher.removeLast();
                            end = offset + embeddedToken.length();
                            found.add(new FoldInfo(
                                    doc,
                                    start,
                                    end,
                                    CODE_BLOCK_FOLD_TYPE,
                                    "{ ... }",
                                    FoldTemplate.DEFAULT_BLOCK));
                            break;
                    }
                } catch (BadLocationException e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }

        return found;
    }
    
    private void finishIncludesFold(List<FoldInfo> found, int start, int end) 
            throws BadLocationException {
        found.add(new FoldInfo(
                doc,
                start,
                end,
                CODE_BLOCK_FOLD_TYPE,
                "...",
                FoldTemplate.DEFAULT_BLOCK));
    }
    
    private void mergeFolds(List<FoldInfo> generated,
            Set<Fold> zombies,
            Set<FoldInfo> newborns) throws BadLocationException {
        FoldHierarchy hierarchy = getOperation().getHierarchy();
        Set<FoldInfo> oneLineFolds = new HashSet<>();
        for (FoldInfo foldInfo : generated) {
            if (isOneLineElement(foldInfo)) {
                oneLineFolds.add(foldInfo);
            }
        }
        
        generated.removeAll(oneLineFolds);
        
        @SuppressWarnings("unchecked")
        List<Fold> existingFolds = (List<Fold>)FoldUtilities.findRecursive(
                hierarchy.getRootFold());
        existingFolds.retainAll(currentFolds);
        
        Map<Integer, FoldInfo> newbornsLineCache = new HashMap<>();
        Set<FoldInfo> duplicateNewborns = new HashSet<>();
        for (FoldInfo foldInfo : generated) {
            int lineOffset = Utilities.getLineOffset(doc,
                    foldInfo.getStartOffset());
            FoldInfo found = newbornsLineCache.get(lineOffset);
            if (found != null
             && found.getEndOffset() < foldInfo.getEndOffset()) {
                duplicateNewborns.add(found);
            }
            
            newbornsLineCache.put(lineOffset, foldInfo);
            
            Fold fold = FoldUtilities.findNearestFold(hierarchy,
                    foldInfo.getStartOffset());
            if (fold != null
             && fold.getStartOffset() == foldInfo.getStartOffset()
             && fold.getEndOffset() == foldInfo.getEndOffset()
             && currentFolds.contains(fold)) {
                if (foldInfo.foldType != fold.getType()
                 || !foldInfo.description.equals(fold.getDescription())) {
                    zombies.add(fold);
                    newborns.add(foldInfo);
                }
            } else {
                newborns.add(foldInfo);
            }
        }
        
        newborns.removeAll(duplicateNewborns);
        existingFolds.removeAll(zombies);
        
        Map<Integer, Fold> linesToFoldsCache = new HashMap<>();
        for (Fold fold : existingFolds) {
            boolean found = false;
            for (FoldInfo fi : generated) {
                if (fold.getStartOffset() == fi.getStartOffset()
                 && fold.getEndOffset() == fi.getEndOffset()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                zombies.add(fold);
            } else {
                int lineoffset = Utilities.getLineOffset(getDocument(),
                        fold.getStartOffset());
                linesToFoldsCache.put(lineoffset, fold);
            }
        }

        Set<FoldInfo> newbornsToRemove = new HashSet<>();
        for (FoldInfo foldInfo : newborns) {
            Fold existing = linesToFoldsCache.get(Utilities
                    .getLineOffset(getDocument(), foldInfo.getStartOffset()));
            if (existing != null) {
                if (existing.getEndOffset() < foldInfo.getEndOffset()) {
                    zombies.add(existing);
                } else {
                    newbornsToRemove.add(foldInfo);
                }
            }
        }
        
        newborns.removeAll(newbornsToRemove);
    }

    private boolean isOneLineElement(FoldInfo fi) throws BadLocationException {
        return Utilities.getLineOffset(getDocument(), fi.getStartOffset())
            == Utilities.getLineOffset(getDocument(), fi.getEndOffset());
    }
    
    private void restartTimer() {
        FOLDS_UPDATE_TASK.schedule(FOLD_UPDATE_DELAY);
    }
    
    @Override
    public void init(FoldOperation operation) {
        this.operation = operation;
    }

    @Override
    public void initFolds(FoldHierarchyTransaction transaction) {
        Document document
                = getOperation().getHierarchy().getComponent().getDocument();
        if (document instanceof BaseDocument) {
            this.doc = (BaseDocument) document;
            restartTimer();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {
        restartTimer();
    }

    @Override
    public void removeUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {
        restartTimer();
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
    public void release() {
    }
    
    private static class FoldInfo {

        private Position startOffset, endOffset;
        private FoldType foldType;
        private String description;
        private FoldTemplate foldTemplate;

        public FoldInfo(Document doc, int startOffset, int endOffset,
                FoldType foldType, String description,
                FoldTemplate foldTemplate) throws BadLocationException {
            this.startOffset = doc.createPosition(startOffset);
            this.endOffset = doc.createPosition(endOffset);
            this.foldType = foldType;
            this.description = description;
            this.foldTemplate = foldTemplate;
        }

        public String getDescription() {
            return description;
        }

        public int getEndOffset() {
            return endOffset.getOffset();
        }

        public FoldType getFoldType() {
            return foldType;
        }

        public int getStartOffset() {
            return startOffset.getOffset();
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT,
                    "FoldInfo[start=%s, end=%s, desc=%d, type=%s, "
                            + "foldTemplate=%s]",
                    startOffset, endOffset, description, foldType,
                    foldTemplate);
        }
    }
    
}
