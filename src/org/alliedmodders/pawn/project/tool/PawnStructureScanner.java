package org.alliedmodders.pawn.project.tool;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

public class PawnStructureScanner implements StructureScanner {
    private PawnStructureScanner() {
        //...
    }

    @Override
    public List<? extends StructureItem> scan(ParserResult pr) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult pr) {
        return Collections.emptyMap();
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }
}
