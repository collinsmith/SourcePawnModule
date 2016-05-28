package org.alliedmodders.pawn.parser;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.alliedmodders.pawn.jccparser.ParseException;
import org.alliedmodders.pawn.jccparser.Token;
import org.alliedmodders.pawn.parser.PawnParser.PawnParserResult;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

public class PawnSyntaxErrorHighlightingTask extends ParserResultTask<PawnParserResult> {
    @Override
    public void run(PawnParserResult pawnResult, SchedulerEvent event) {
	try {
	    //PawnParserResult pawnResult = (PawnParserResult)result;
	    List<ParseException> syntaxErrors = pawnResult.getPawnParser().syntaxErrors;
	    Document document = pawnResult.getSnapshot().getSource().getDocument(false);
	    List<ErrorDescription> errors = new ArrayList<>();
	    for (ParseException syntaxError : syntaxErrors) {
		Token token = syntaxError.currentToken;
		int start = NbDocument.findLineOffset((StyledDocument) document, token.beginLine - 1) + token.beginColumn - 1;
		int end = NbDocument.findLineOffset((StyledDocument) document, token.endLine - 1) + token.endColumn;
		ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
		    Severity.ERROR,
		    syntaxError.getMessage(),
		    document,
		    document.createPosition(start),
		    document.createPosition(end));
		errors.add(errorDescription);
	    }
	    
	    HintsController.setErrors(document, "pawn", errors);
	} catch (BadLocationException e) {
	    Exceptions.printStackTrace(e);
	} catch (ParseException e) {
	    Exceptions.printStackTrace(e);
	}
    }

    @Override
    public int getPriority() {
	return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
	return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {
	//...
    }
}
