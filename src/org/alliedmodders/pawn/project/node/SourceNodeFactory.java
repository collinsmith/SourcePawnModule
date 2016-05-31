package org.alliedmodders.pawn.project.node;

import java.awt.Image;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.alliedmodders.pawn.project.PawnProject;
import org.alliedmodders.pawn.project.PawnProjectFactory;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

@NodeFactory.Registration(projectType = "org-alliedmodders-pawn-project", position = 10)
public class SourceNodeFactory implements NodeFactory {
    private SourceNodeFactory() {
        //...
    }

    @Override
    public NodeList<?> createNodes(Project project) {
	PawnProject p = project.getLookup().lookup(PawnProject.class);
	assert p != null : "PawnProject should have been added to its own lookups";
	return new SourceNodeList(p);
    }
    
    @SuppressWarnings("unchecked")
    private static class SourceNodeList implements NodeList<Node> {
	PawnProject project;
	
	SourceNodeList(PawnProject project) {
	    this.project = project;
	}

	@Override
	public List<Node> keys() {
            try {
                FileObject sourcesFolder = project.getProjectDirectory()
                        .getFileObject(PawnProjectFactory.SOURCES_FOLDER);
                if (sourcesFolder == null) {
                    return Collections.EMPTY_LIST;
                }
                
                Node delegate = DataObject.find(sourcesFolder).getNodeDelegate();                
                Node sourceNode = new FileFilteredNode(
                        delegate,
                        new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return pathname.getName().endsWith(".sp");
                            }
                        }) {

                    @Override
                    public Image getIcon(int type) {
                        return ImageUtilities.loadImage(PawnProject.PawnProjectLogicalView.FOLDER_ICON);
                    }

                    @Override
                    public Image getOpenedIcon(int type) {
                        return getIcon(type);
                    }

                    @Override
                    public String getDisplayName() {
                        return "Source";
                    }

                    @Override
                    public Action[] getActions(boolean context) {
                        return new Action[] {
                            CommonProjectActions.newFileAction()
                        };
                    }

                };

                List<Node> finalList = new ArrayList<>(1);
                finalList.add(sourceNode);
                return finalList;
            } catch (DataObjectNotFoundException e) {
                Exceptions.printStackTrace(e);
                return Collections.EMPTY_LIST;
            }
	}

	@Override
	public Node node(Node key) {
	    return new FilterNode(key);
	}

	@Override
	public void addNotify() {
	    //...
	}

	@Override
	public void removeNotify() {
	    //...
	}
	
	@Override
	public void addChangeListener(ChangeListener l) {
	    //...
	}

	@Override
	public void removeChangeListener(ChangeListener l) {
	    //...
	}
    }
    
}
