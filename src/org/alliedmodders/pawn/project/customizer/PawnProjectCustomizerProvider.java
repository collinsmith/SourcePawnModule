package org.alliedmodders.pawn.project.customizer;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.alliedmodders.pawn.project.PawnProject;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.CustomizerProvider2;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.lookup.Lookups;

public class PawnProjectCustomizerProvider implements CustomizerProvider2 {
    public final PawnProject project;
    
    public static final String CUSTOMIZER_FOLDER_PATH
	    = "Projects/org-alliedmodders-pawn-project/Customizer";
    
    public PawnProjectCustomizerProvider(PawnProject project) {
        this.project = project;
    }

    public PawnProject getPawnProject() {
	return project;
    }
    
    @Override
    public void showCustomizer(String arg0, String arg1) {
	showCustomizer();
    }

    @Override
    public void showCustomizer() {
	Dialog d = ProjectCustomizer.createCustomizerDialog(
		CUSTOMIZER_FOLDER_PATH,
		Lookups.fixed(project),
		"",
		new OkayOptionListener(),
		null);
	
	d.setTitle(ProjectUtils.getInformation(project).getDisplayName());
	d.setVisible(true);
    }
    
    private static class OkayOptionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
	    
	}
    }
}
