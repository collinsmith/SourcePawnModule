package org.alliedmodders.pawn.project.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.prefs.Preferences;
import org.alliedmodders.pawn.project.PawnProject;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

@ActionID(
	category = "Build",
	id = "org.alliedmodders.pawn.actions.BuildAction"
)
@ActionRegistration(
	displayName = "#CTL_BuildAction"
)
@ActionReference(path = "Loaders/text/x-pawn/Actions", position = 250, separatorAfter = 251)
@Messages("CTL_BuildAction=Build")
public final class BuildAction implements ActionListener {
    private final DataObject context;

    public BuildAction(DataObject context) {
	this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
	buildFile(context, ev);
    }

    public static void buildFile(DataObject context, ActionEvent ev) {
	InputOutput io = IOProvider.getDefault().getIO("SourcePawn Compiler", false);
	Preferences prefs = Preferences.userNodeForPackage(PawnProject.class);
	String compilerPath = prefs.get("compilerPath", "");
	if (compilerPath.isEmpty()) {
	    io.getOut().println("Path to spcomp.exe not set.");
	    return;
	}
	
	String fileName = context.getName();
	String filePath = context.getPrimaryFile().getPath();
	String fileDir = context.getFolder().getPrimaryFile().getPath();
	String buildPath = String.format("%s/%s.smx", fileDir, fileName);
        //String defaultInclude = FileUtil.normalizePath("src/org/alliedmodders/pawn/file/pawn/default.inc");
	
	io.getOut().println("------------------------------------------------");
	io.getOut().printf("Building '%s' . . .%n", filePath);

	try {
	    String[] cmd = {
		compilerPath,
		String.format("\"%s\"", filePath),
		"-\\",
                //"-;",
		//"-h",
                //String.format("-p\"%s\"", defaultInclude),
		String.format("-o=\"%s\"", buildPath),
		String.format("-i=\"%s\"", fileDir),
	    };
            
	    Process p = Runtime.getRuntime().exec(cmd);
	    InputStreamReader isr = new InputStreamReader(p.getInputStream());
	    BufferedReader br = new BufferedReader(isr);
	    
	    String line = null;
	    while ((line = br.readLine()) != null) {
		io.getOut().println(line);
	    }
	    
	    p.waitFor();
	    p.destroy();
	    
	    io.getOut().println();
	    io.getOut().println("Copying file to plugins directory. . .");
	    String pluginsDir = prefs.get("pluginsDir", "");
	    if (pluginsDir.isEmpty()) {
		io.getOut().println("Plugins directory not set. Skipping.");
		return;
	    } else {
                Files.copy(Paths.get(buildPath), Paths.get(pluginsDir, fileName + ".smx"), StandardCopyOption.REPLACE_EXISTING);
            }

	    io.getOut().println("Done.");
	} catch (IOException e) {
	    io.getErr().println("Cannot find " + e.getMessage());
	    //e.printStackTrace(io.getErr());
	} catch (InterruptedException e) {
	    e.printStackTrace(io.getErr());
	}
    }
}
