/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.preferences;


import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.m.MCodeScanner;
import gov.va.med.iss.meditor.utils.MColorProvider;
import gov.va.med.iss.meditor.utils.MEditorUtilities;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */


public class MEditorPreferencesPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	public static final String P_PATH = "pathPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";
	
	public MEditorPreferencesPage() {
		super(GRID);
		setPreferenceStore(MEditorPlugin.getDefault().getPreferenceStore());
		setDescription("MEditor Preferences");
		initializeDefaults();
		MEditorPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(preferenceChangeListener);
	}

	/**
	 * Sets the initializes all the preferences to a default value.
	 * That is, if these preferences are not in eclipses persistence store, this
	 * method shall create them and set them to their default value.
	 */
	public void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(MEditorPlugin.P_AUTO_SAVE_TO_SERVER, true);
		store.setDefault(MEditorPlugin.P_DEFAULT_UPDATE, true);
		store.setDefault(MEditorPlugin.P_WRAP_LINES, false);
		store.setDefault(MEditorPlugin.P_PROJECT_NAME,"mcode");
		store.setDefault(MEditorPlugin.P_SAVE_BY_SERVER,true);
		store.setDefault(MEditorPlugin.P_SAVE_BY_NAMESPACE,"0");
//		store.setDefault(MEditorPlugin.P_SAVE_DIR_EXAMPLE,
//				MEditorPreferencesPage.getDirectoryPreference("", "Server","ROUTINE"));

		store.setDefault(MEditorPlugin.P_MULTI_LINE_COMMENT_COLOR, "64,128,128");
		store.setDefault(MEditorPlugin.P_COMMENT_COLOR, "128,128,0");
		
		store.setDefault(MEditorPlugin.P_DEFAULT_COLOR, "0,0,0");
		store.setDefault(MEditorPlugin.P_KEYWORD_COLOR, "127,0,85"); ///  <====
		store.setDefault(MEditorPlugin.P_TYPE_COLOR, "64,0,200");  /// <====
		store.setDefault(MEditorPlugin.P_STRING_COLOR, "0,0,255");

		store.setDefault(MEditorPlugin.P_FUNCS_COLOR, "255,0,0");
		store.setDefault(MEditorPlugin.P_OPS_COLOR, "155,50,50");
		store.setDefault(MEditorPlugin.P_TAGS_COLOR, "0,127,0");
		store.setDefault(MEditorPlugin.P_VARS_COLOR, "170,0,170");
		store.setDefault(MEditorPlugin.P_COMMAND_COLOR, "155,50,50");  // 97,162,97
		store.setDefault(MEditorPlugin.P_CONDITIONS_COLOR,"0,0,0");
	}
	
	//--jspivey this logic has all been re-implemented and moved to RoutineEditAction
//	public static String getDirectoryPreference(String projectName, String serverName, String routineName) {
//		if (serverName.compareTo("") == 0) {
//			String currServer = VistaConnection.getCurrentServer();
//			if (currServer.compareTo(";;;") == 0) {
//				//currServer = VistaConnection.getPrimaryServer();
//				VistaConnection.getPrimaryServer();
//				currServer = VistaConnection.getCurrentServer();
//			}
//			serverName = MPiece.getPiece(currServer,";");
//		}
//		IResource resource = null;
//		if (MEditorPrefs.isPrefsActive()) {
//			try {
//				resource = MEditorUtilities.getProject(projectName); //force it to auto discover the correct projectName or use the passed in projectName
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		String str = "";
//		if (!(resource == null)) { //why does it return malformed locations? Before the project pref is active, it returns a relative path, but afterwards it returns a complete path
//			str = resource.getLocation().toString();
//		}
//		IPreferenceStore store = MEditorPlugin.getDefault().getPreferenceStore();
//		//VistaConnection.getPrimaryServer(); //must force the properties to load...
//		boolean vcPorject = !MPiece.getPiece(VistaConnection.getCurrentServer(), ";", 4).equals("");	
//		boolean saveByServer = store.getBoolean(MEditorPlugin.P_SAVE_BY_SERVER);
//		String val = store.getString(MEditorPlugin.P_SAVE_BY_NAMESPACE);
//		int saveByNamespaceCnt = val != null && ! val.equals("") ? Integer.parseInt(val) : 0;
//		
//		if (vcPorject) {
//			if (str.equals("") || !str.contains("/"))
//				try {
//					str = MEditorUtilities.getProject(MPiece.getPiece(VistaConnection.getCurrentServer(), ";", 4)).getLocation().toString();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			
//			File projectPath = new File(str);
//			//RoutinePathResolver routinePathResolver = RoutinePathResolverFactory.getInstance().getRoutinePathResolver(projectPath);
//			return str +"/"; //+routinePathResolver.getRelativePath(routineName);
//		}
//
//		
//		if (saveByServer) {
//			str += "/"+serverName;
//		}
//		str += "/"+ routineName.substring(0, saveByNamespaceCnt);
//
//		
//		if (! ((serverName.compareTo("Server") == 0) && (routineName.compareTo("ROUTINE") == 0) )) {
//			if (! (new File(str).exists())) {
//				new File(str).mkdirs();
//			}
//		}
//		return str;
//	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		
		Composite parent = getFieldEditorParent();
		
//		Group saveGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
//		loadGroup.setText("Routing Save Options");
		
//		FieldEditor saveLabel = new LabelFieldEditor();
//		saveLabel.setLabelText("Save Routine Options");
//		addField(saveLabel);
		
		addField(new BooleanFieldEditor(MEditorPlugin.P_AUTO_SAVE_TO_SERVER, "Automatically save files onto server", parent));
		addField(
			new BooleanFieldEditor(
					MEditorPlugin.P_DEFAULT_UPDATE,
				"Update routine header on server save",
				parent));
		
		
		
		//getFieldEditorParent().setLayout(new GridLayout(1, true));
		
//		Group loadGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
//		loadGroup.setText("Routing Load Options");
		
		addField(new BooleanFieldEditor(MEditorPlugin.P_SAVE_BY_SERVER,
				"Load Routines into directy with their server's name", parent));
		IntegerFieldEditor nameSpaceField = new IntegerFieldEditor(MEditorPlugin.P_SAVE_BY_NAMESPACE,
				"Load Routines into directory containing their namespace",parent);
		nameSpaceField.setValidRange(0, 3);
		nameSpaceField.setEmptyStringAllowed(false);
		nameSpaceField.setErrorMessage("Namespace must be between 0 (disbled) and 3.");
		addField(nameSpaceField);

//		StringFieldEditor strFldEdtr = new StringFieldEditor(MEditorPlugin.P_SAVE_DIR_EXAMPLE,
//				"Sample Directory for routine 'ROUTINE'",getFieldEditorParent());
//		strFldEdtr.setEnabled(false, getFieldEditorParent());
//		String project = VistaConnection.getPrimaryProject();
//		
//		MEditorPlugin
//				.getDefault()
//				.getPreferenceStore()
//				.setValue(
//						MEditorPlugin.P_SAVE_DIR_EXAMPLE,
//						VistaConnection.getPrimaryServerName()
//								+ FileSystems.getDefault().getSeparator());
//		addField(strFldEdtr);
		

		addField(new BooleanFieldEditor(MEditorPlugin.P_WRAP_LINES,"&Wrap lines",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_VARS_COLOR,"Variables",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_COMMAND_COLOR,"Commands",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_STRING_COLOR,"Strings",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_COMMENT_COLOR,"Comments",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_FUNCS_COLOR,"Functions",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_CONDITIONS_COLOR,"Conditions",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_TAGS_COLOR,"Tags && Routines",parent));
		
		adjustGridLayout();
				
		//removed:
		//addField(new StringFieldEditor(MEditorPlugin.P_PROJECT_NAME,"Enter the desired project for saving routines: ",getFieldEditorParent()));
		//jspivey, it is too buggy to support this. If they change it from "mcode" to something else, the existing files and projects need to be moved to
	}

	public void init(IWorkbench workbench) {
	}
	
	private final IPropertyChangeListener preferenceChangeListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			boolean seenFlag = false;
			IEditorPart part = MEditorUtilities.getIWorkbenchPage().getActiveEditor();
			for (int i=0; i<MEditorPlugin.preferenceColors.length; i++) {
				if (event.getProperty().equals(MEditorPlugin.preferenceColors[i])) {
					seenFlag = true;
					MColorProvider.updateColorTable(MEditorPlugin.preferenceColors[i],MEditorPrefs.getPrefs(MEditorPlugin.preferenceColors[i]));
				}
			}
			if (! seenFlag) {
				if (event.getProperty().equals(MEditorPlugin.P_WRAP_LINES)) {
					seenFlag = true;
					boolean wordWrap;
					if (MEditorPrefs.getPrefs(MEditorPlugin.P_WRAP_LINES).equals("true")) {
						wordWrap = true;
					}
					else wordWrap = false;
					MEditor.setWordWrapValue(wordWrap);
					((MEditor) part).setWordWrap();
				}
			}
			if (seenFlag) {
				MCodeScanner.setTokens();
				if (part instanceof MEditor) {
					((MEditor) part).updateSourceViewerConfiguration();
					((MEditor) part).sourceViewer.invalidateTextPresentation();
				}
			}
		}
	};
}