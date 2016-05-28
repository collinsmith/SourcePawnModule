package org.alliedmodders.pawn.project.customizer.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;
import org.alliedmodders.pawn.project.PawnProject;
import org.alliedmodders.pawn.project.customizer.SpringUtilities;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class CompilerProperties implements ProjectCustomizer.CompositeCategoryProvider {
    private static final String COMPILER = "Compiler";
    
    private CompilerProperties() {
	
    }
    
    @ProjectCustomizer.CompositeCategoryProvider.Registration(
	    projectType = "org-alliedmodders-pawn-project",
	    position = 10
    )
    public static CompilerProperties createCompilerProperties() {
	return new CompilerProperties();
    }
    
    @NbBundle.Messages("LBL_Config_Compiler=Compiler")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
	return ProjectCustomizer.Category.create(
		COMPILER,
		Bundle.LBL_Config_Compiler(),
		null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup lookup) {
	final JPanel p = new JPanel();
	p.setLayout(new SpringLayout());
	
	final Preferences prefs = Preferences.userNodeForPackage(PawnProject.class);
	
	JLabel l = new JLabel("Compiler:", JLabel.LEADING);
	p.add(l);
	final JTextField compilerPath = new JTextField();
	compilerPath.setToolTipText("../addons/sourcemod/scripting/spcomp.exe");
	compilerPath.setText(prefs.get("compilerPath", ""));
	l.setLabelFor(compilerPath);
	p.add(compilerPath);
	JButton b = new JButton("\u2026");
	b.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser(compilerPath.getText());
		if (!compilerPath.getText().isEmpty()) {
		    jfc.setSelectedFile(new File(compilerPath.getText()));
		}
		
		jfc.setFileFilter(new FileFilter() {
		    @Override
		    public boolean accept(File f) {
			return f.getName().equalsIgnoreCase("spcomp.exe");
		    }

		    @Override
		    public String getDescription() {
			return "spcomp.exe";
		    }
		});
		
		jfc.setVisible(true);
		int retVal = jfc.showDialog(p, "Select");
		switch (retVal) {
		    case JFileChooser.APPROVE_OPTION:
			compilerPath.setText(jfc.getSelectedFile().getAbsolutePath());
			break;
		    default:
		}
	    }
	});
	
	p.add(b);
	
	l = new JLabel("Plugins:", JLabel.LEADING);
	p.add(l);
	final JTextField pluginsDir = new JTextField();
	pluginsDir.setToolTipText("../addons/sourcemod/plugins/");
	pluginsDir.setText(prefs.get("pluginsDir", ""));
	l.setLabelFor(pluginsDir);
	p.add(pluginsDir);
	b = new JButton("\u2026");
	b.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser(pluginsDir.getText());
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (!pluginsDir.getText().isEmpty()) {
		    jfc.setSelectedFile(new File(pluginsDir.getText()));
		}
		
		jfc.setFileFilter(new FileFilter() {
		    @Override
		    public boolean accept(File f) {
			return f.isDirectory();
		    }

		    @Override
		    public String getDescription() {
			return "plugins";
		    }
		});
		
		jfc.setVisible(true);
		int retVal = jfc.showDialog(p, "Select");
		switch (retVal) {
		    case JFileChooser.APPROVE_OPTION:
			pluginsDir.setText(jfc.getSelectedFile().getAbsolutePath());
			break;
		    default:
		}
	    }
	});
	
	p.add(b);
	
	SpringUtilities.makeCompactGrid(
		p,
		2,
		3,
		0,
		0,
		0,
		0);
	
	category.setOkButtonListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		prefs.put("compilerPath", compilerPath.getText());
		prefs.put("pluginsDir", pluginsDir.getText());
	    }
	});
	
	JPanel parent = new JPanel(new BorderLayout());
	parent.add(p, BorderLayout.NORTH);
	return parent;
    }
}
