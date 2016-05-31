package org.alliedmodders.pawn.project;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=ProjectFactory.class)
public class PawnProjectFactory implements ProjectFactory {
    public static final String PROJECT_FOLDER = ".sourcepawn";
    public static final String SOURCES_FOLDER = "src";
    public static final String INCLUDES_FOLDER = "include";
    public static final String TESTSUITES_FOLDER = "testsuite";
    
    @Override
    public boolean isProject(FileObject dir) {
	return dir.getFileObject(PROJECT_FOLDER.toString()) != null;
    }

    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
	return isProject(dir) ? new PawnProject(dir, state) : null;
    }

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
