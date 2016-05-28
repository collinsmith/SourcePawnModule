package org.alliedmodders.pawn.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=ProjectFactory.class)
public class PawnProjectFactory implements ProjectFactory {
    public static final String PROJECT_FOLDER = ".pawn";
    
    @Override
    public boolean isProject(FileObject dir) {
	return dir.getFileObject(PROJECT_FOLDER) != null;
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
