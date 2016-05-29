package org.alliedmodders.pawn.project.node;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.alliedmodders.pawn.project.PawnProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

@NodeFactory.Registration(projectType = "org-alliedmodders-pawn-project", position = 0)
public class IncludeNodeFactory implements NodeFactory {
    private IncludeNodeFactory() {
        //...
    }

    @Override
    public NodeList<?> createNodes(Project project) {
	PawnProject p = project.getLookup().lookup(PawnProject.class);
	assert p != null : "PawnProject should have been added to its own lookups";
	return new IncludeNodeList(p);
    }
    
    @SuppressWarnings("unchecked")
    private static class IncludeNodeList implements NodeList<Node> {
	PawnProject project;
	
	IncludeNodeList(PawnProject project) {
	    this.project = project;
	}

	@Override
	public List<Node> keys() {
	    FileObject textsFolder = project.getProjectDirectory().getFileObject("src").getFileObject("include");
	    List<Node> fileResult = new ArrayList<>();
	    List<Node> folderResult = new ArrayList<>();
	    if (textsFolder != null) {
		for (FileObject textsFolderFile : textsFolder.getChildren()) {
		    try {
                        if (textsFolderFile.isFolder()) {
			    folderResult.add(DataObject.find(textsFolderFile).getNodeDelegate());
			} else {
			    fileResult.add(DataObject.find(textsFolderFile).getNodeDelegate());
			}
		    } catch (DataObjectNotFoundException ex) {
			Exceptions.printStackTrace(ex);
		    }
		}
	    }
	    
	    fileResult.sort(new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) {
		    return o1.getName().compareTo(o2.getName());
		}
	    });
	    
	    folderResult.sort(new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) {
		    return o1.getName().compareTo(o2.getName());
		}
	    });
	    
	    folderResult.addAll(fileResult);

            try {
                Children children = new Index.ArrayChildren();
                children.add(folderResult.toArray(new Node[0]));
                Node sourceNode = new FilterNode(
                        DataObject.find(textsFolder).getNodeDelegate(),
                        children) {

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
                        return "Include";
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
