package org.alliedmodders.pawn.project;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.alliedmodders.pawn.project.action.BuildProjectAction;
import org.alliedmodders.pawn.project.customizer.PawnProjectCustomizerProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeMemberEvent;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class PawnProject implements Project {
    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lookup;
    
    PawnProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
	this.state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
	return projectDir;
    }

    @Override
    public Lookup getLookup() {
	if (lookup == null) {
	    lookup = Lookups.fixed(new Object[] {
		this,
		new Info(),
		new PawnProjectLogicalView(this),
		new PawnProjectCustomizerProvider(this),
	    });
	}
	
	return lookup;
    }
    
    private final class Info implements ProjectInformation {
	@StaticResource
	public static final String PAWN_ICON = "org/alliedmodders/pawn/project/icon.png";
	
	@Override
	public String getName() {
	    return getProjectDirectory().getName();
	}

	@Override
	public String getDisplayName() {
	    return getName();
	}

	@Override
	public Icon getIcon() {
	    return new ImageIcon(ImageUtilities.loadImage(PAWN_ICON));
	}

	@Override
	public Project getProject() {
	    return PawnProject.this;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener pl) {
	    //...
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener pl) {
	    //...
	}
    }
    
    public static class PawnProjectLogicalView implements LogicalViewProvider {
	@StaticResource
	public static final String PAWN_ICON = "org/alliedmodders/pawn/project/icon.png";
        
        @StaticResource
	public static final String FOLDER_ICON = "org/alliedmodders/pawn/project/folder.png";
	
	private final PawnProject project;

	public PawnProjectLogicalView(PawnProject project) {
	    this.project = project;
	}
	
	@Override
	public Node createLogicalView() {
	    try {
		FileObject projectDir = project.getProjectDirectory();
		DataFolder projectFolder = DataFolder.findFolder(projectDir);
		Node node = projectFolder.getNodeDelegate();
		return new ProjectNode(node, project);
	    } catch (DataObjectNotFoundException e) {
		Exceptions.printStackTrace(e);
		return new AbstractNode(Children.LEAF);
	    }
	}

	@Override
	public Node findPath(Node arg0, Object arg1) {
	    return null;
	}
	
	private final class ProjectNode extends FilterNode {
	    final PawnProject project;
	    
	    public ProjectNode(Node node, PawnProject project) throws DataObjectNotFoundException {
		super(node,
			NodeFactorySupport.createCompositeChildren(
			    project,
			    "Projects/org-alliedmodders-pawn-project/Nodes"),
			//new FilterNode.Children(node),
			new ProxyLookup(new Lookup[] {
			    Lookups.singleton(project),
			    node.getLookup()
			}));
		this.project = project;
	    }

	    @Override
	    public Action[] getActions(boolean arg0) {
		return new Action[] {
		    new BuildProjectAction(project),
		    CommonProjectActions.newFileAction(),
		    CommonProjectActions.copyProjectAction(),
		    CommonProjectActions.deleteProjectAction(),
		    CommonProjectActions.closeProjectAction(),
		    CommonProjectActions.moveProjectAction(),
		    CommonProjectActions.renameProjectAction(),
		    CommonProjectActions.customizeProjectAction(),
		};
	    }

	    @Override
	    public Image getIcon(int type) {
		return ImageUtilities.loadImage(PAWN_ICON);
	    }

	    @Override
	    public Image getOpenedIcon(int type) {
		return getIcon(type);
	    }

	    @Override
	    public String getDisplayName() {
		return project.getProjectDirectory().getName();
	    }
	}
    }
}
