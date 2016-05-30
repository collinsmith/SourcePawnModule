
package org.alliedmodders.pawn.project.node;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.ProxyLookup;

public class FileFilteredNode extends FilterNode {

    public FileFilteredNode(Node original, FileFilter fileFilter) {
        super(original, new FileFilteredChildren(original, fileFilter));
    }
    
    public FileFilteredNode(Node original, Lookup delegate,
            FileFilter fileFilter) {
        super(original,
                new FileFilteredChildren(original, fileFilter),
                new ProxyLookup(delegate, original.getLookup()));
    }
    
    static class FileFilteredChildren extends FilterNode.Children {

        private final FileFilter fileFilter;

        public FileFilteredChildren(Node original, FileFilter fileFilter) {
            super(original);
            this.fileFilter = fileFilter;
        }

        @Override
        protected Node copyNode(Node original) {
            return new FileFilteredNode(original, fileFilter);
        }

        @Override
        protected Node[] createNodes(Node key) {
            List<Node> result = new ArrayList<>();
            for (Node node : super.createNodes(key)) {
                FileObject fileObject = node.getLookup().lookup(FileObject.class);
                if (fileObject != null) {
                    File file = FileUtil.toFile(fileObject);
                    if (fileFilter.accept(file)) {
                        result.add(node);
                    }
                }
            }

            return result.toArray(new Node[0]);
        }

    }
    
}
