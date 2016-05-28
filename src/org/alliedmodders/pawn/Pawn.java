package org.alliedmodders.pawn;

public final class Pawn {
    private Pawn() {
        //...
    }
    
    public static boolean isPawnIdentifierStart(int codePoint) {
        return Character.isJavaIdentifierStart(codePoint);
    }
    
    public static boolean isPawnIdentifierPart(int codePoint) {
        return Character.isJavaIdentifierPart(codePoint);
    }
}
