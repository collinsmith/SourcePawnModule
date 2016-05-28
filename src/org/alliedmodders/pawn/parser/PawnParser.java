package org.alliedmodders.pawn.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

public class PawnParser extends Parser {
    private Snapshot snapshot;
    private org.alliedmodders.pawn.jccparser.PawnParser parser;
    
    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) {
	this.snapshot = snapshot;
	Reader reader = new StringReader(snapshot.getText().toString());
	parser = new org.alliedmodders.pawn.jccparser.PawnParser(reader);
	try {
	    parser.CompilationUnit();
	} catch (org.alliedmodders.pawn.jccparser.ParseException e) {
	    Logger.getLogger(PawnParser.class.getName()).log(Level.WARNING, null, e);
	}
    }

    @Override
    public Result getResult(Task task) {
	return new PawnParserResult(snapshot, parser);
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
	//...
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
	//...
    }
    
    public static class PawnParserResult extends ParserResult {
	private org.alliedmodders.pawn.jccparser.PawnParser parser;
	private boolean valid = true;
	
	PawnParserResult(Snapshot snapshot, org.alliedmodders.pawn.jccparser.PawnParser parser) {
	    super(snapshot);
	    this.parser = parser;
	}
	
	public org.alliedmodders.pawn.jccparser.PawnParser getPawnParser() throws org.alliedmodders.pawn.jccparser.ParseException {
	    if (!valid) {
		throw new org.alliedmodders.pawn.jccparser.ParseException();
	    }
	    
	    return parser;
	}
	
	@Override
	protected void invalidate() {
	    valid = false;
	}

	@Override
	public List<? extends Error> getDiagnostics() {
	    return Collections.emptyList();
	}
    }
}
