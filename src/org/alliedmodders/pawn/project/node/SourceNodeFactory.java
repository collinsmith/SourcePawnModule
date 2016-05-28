package org.alliedmodders.pawn.project.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.alliedmodders.pawn.project.PawnProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

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
    
    private static class SourceNodeList implements NodeList<Node> {
	PawnProject project;
	
	SourceNodeList(PawnProject project) {
	    this.project = project;
	}

	@Override
	public List<Node> keys() {
	    FileObject textsFolder = project.getProjectDirectory().getFileObject("src");
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
	    
	    return folderResult;
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
