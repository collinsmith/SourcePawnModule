package org.alliedmodders.pawn.project.action;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import org.alliedmodders.pawn.project.PawnProject;
import org.alliedmodders.pawn.project.PawnProjectFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

@ActionID(
	category = "Project",
	id = "org.alliedmodders.pawn.actions.BuildProject"
)
@ActionRegistration(
	displayName = "#CTL_BuildProject"
)
@ActionReference(
	path = "Projects/org-alliedmodders-pawn-project")
@Messages("CTL_BuildProject=Build SourcePawn Project")
public final class BuildProjectAction extends AbstractAction {

    private final Project project;

    public BuildProjectAction(Project project) {
	this.project = project;
	setEnabled(project instanceof PawnProject);
	putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
	putValue(NAME, "Build SourcePawn Project");
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
	InputOutput io = IOProvider.getDefault().getIO(ProjectUtils.getInformation(project).getDisplayName(), false);
	io.getOut().printf("Building '%s' . . .%n", ProjectUtils.getInformation(project).getDisplayName());
	
	FileObject src = project.getProjectDirectory().getFileObject(PawnProjectFactory.SOURCES_FOLDER);
        if (src == null) {
            io.getOut().printf("Source folder not found: %s/%s%n",
                    project.getProjectDirectory().getPath(),
                    PawnProjectFactory.SOURCES_FOLDER);
            io.getOut().printf("Terminating build!%n", project.getProjectDirectory().getPath());
            return;
        }
        
        buildFilesInFolder(src);
    }
    
    private void buildFilesInFolder(FileObject context) {
        if (!context.isFolder()) {
            throw new IllegalArgumentException("context must be a folder");
        }
        
        for (FileObject fo : context.getChildren()) {
            if (fo.isFolder()) {
                buildFilesInFolder(fo);
                continue;
            }
            
            if (!fo.hasExt("sp")) {
                continue;
            }
            
            buildFile(fo);
        }
    }
    
    private void buildFile(FileObject context) {
	InputOutput io = IOProvider.getDefault().getIO(ProjectUtils.getInformation(project).getDisplayName(), false);
	Preferences prefs = Preferences.userNodeForPackage(PawnProject.class);
	String compilerPath = prefs.get("compilerPath", "");
	if (compilerPath.isEmpty()) {
	    io.getOut().println("Path to spcomp.exe not set.");
	    return;
	}

	String fileName = context.getName();
	String projectPath = project.getProjectDirectory().getPath();
	String buildPath = String.format("%s/build/%s.smx", projectPath, fileName);
        //String defaultInclude = FileUtil.normalizePath("src/org/alliedmodders/pawn/file/pawn/default.inc");
                
        if (project.getProjectDirectory().getFileObject("build") == null) {
            try {
                project.getProjectDirectory().createFolder("build");
            } catch (IOException e) {
                io.getErr().printf("Could not build %s: Failed to create build dir because: %s",
                        fileName, e.getMessage());
                return;
            }
        }
	
	io.getOut().println("------------------------------------------------");
	io.getOut().printf("Building '%s' . . .%n", context.getPath());

	try {
	    String[] cmd = {
		compilerPath,
		String.format("\"%s\"", context.getPath()),
		"-\\",
                //"-;",
		//"-h",
                //String.format("-p\"%s\"", defaultInclude),
		String.format("-o=\"%s\"", buildPath),
		String.format("-i=\"%s/" + PawnProjectFactory.SOURCES_FOLDER + "\"", projectPath),
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
