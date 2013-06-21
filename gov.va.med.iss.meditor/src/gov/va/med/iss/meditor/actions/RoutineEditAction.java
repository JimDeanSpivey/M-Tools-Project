package gov.va.med.iss.meditor.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.mumps.meditor.MEditorRPC;
import org.mumps.meditor.MEditorUtils;
import org.mumps.meditor.RoutineNotFoundException;
import org.mumps.meditor.dialogs.RoutineDiffersDialog;
import org.mumps.pathstructure.generic.PathFileSearchVisitor;
import org.mumps.pathstructure.vista.RoutinePathResolver;
import org.mumps.pathstructure.vista.RoutinePathResolverFactory;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class RoutineEditAction implements IWorkbenchWindowActionDelegate {
	
	private static final String SEP = FileSystems.getDefault().getSeparator();

	public RoutineEditAction() {
	}
		
	public void run(IAction action) {
		//Resolve object dependencies (calculate dependencies at the entry point up front)
		VistaLinkConnection connection = VistaConnection.getConnection();
		MEditorRPC rpc = new MEditorRPC(connection);
		String projectName = VistaConnection.getPrimaryProject();
		RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
		
		//Collect input
		RoutineNameDialogData dialogInput = dialogForm.open();
		if (dialogInput.getButtonResponse() == false)
			return;
		String routineName = dialogInput.getTextResponse().trim().toUpperCase();
		//Validations on input
		validateInput(dialogInput, routineName);
		
		String serverCode;
		try { //attempt Load routine into a string, if not found show error
			serverCode = rpc.getRoutineFromServer(routineName);
		} catch (RoutineNotFoundException e) {
			//show error message about routine not existing on server
			MessageDialog.openInformation(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "MEditor",
					"Routine " +routineName+ " does not exist on server.");
			return;
		}
		//Collect additional input from user (note: this should be moved into a new, redesign routine load dialog which calculates all the input needed up front
		String relRoutinePath;
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			if (!project.exists())
				project.create(null);
			if (!project.isOpen())
				project.open(null);
		} catch (CoreException e1) {
		}
		
		//Look into project for file first, always use the existing location if it is found
		Path foundPath = searchForFile(Paths.get(project.getLocationURI()), routineName+ ".m");
		if (foundPath != null) {
			relRoutinePath = foundPath.toString().substring(
					project.getLocation().toOSString().length() + 1,
					foundPath.toString().length() - routineName.length() - 2);
		} else { //else get the default path
			RoutinePathResolver routinePathResolver = RoutinePathResolverFactory
					.getInstance()
					.getRoutinePathResolver(
							project.getLocation().toFile());
			relRoutinePath = routinePathResolver.getRelativePath(routineName);
			
			//make sure that this relative path exists
			createFolders(relRoutinePath, project);
		}
		
		//check to see if a routine is already loaded here. Are we syncing or are we loading it in new?
		IFile routineFile = project.getFile(relRoutinePath +SEP+ routineName + ".m");
		if (routineFile.exists()) {
			String fileCode;
			try {
				fileCode = MEditorUtils.readFile(routineFile);
			} catch (CoreException | IOException e) {
				e.printStackTrace();
				//show error
				MessageDialog.openError(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "MEditor",
						"Failed to load routine. Error occured while loading the local code for compairson to the server: " +e.getMessage());
				return;
			}
						
			//if cannot figure out how to load changes into an editor but not save them, show a warning ask what to do with a diff option
			if (!MEditorUtils.compareRoutines(serverCode, fileCode)) {
				//replace the local contents with latest from server but do not save the actual file
				RoutineDiffersDialog dialog = new RoutineDiffersDialog(Display
						.getDefault().getActiveShell(),
						"Routine " +routineName+ " found on server and locally in the project " +projectName+". Would you like to overwrite the local version with the server version?",
						" the project version (" +routineFile.getFullPath().toOSString()+ ")",
						routineName, MEditorUtils.cleanSource(fileCode),
						MEditorUtils.cleanSource(serverCode));
				if (dialog.open() != Dialog.OK)
					return;
			}
		}

		//simply save the file
		try {
			MEditorUtils.createOrReplace(routineFile, serverCode);
		} catch (UnsupportedEncodingException | CoreException e) {
			e.printStackTrace();
			//show error
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "MEditor",
					"Failed to load routine. Error occured while writing the local file: " +e.getMessage());
			return;
		}
		
		//Open the file in Eclipse
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(routineFile), MEditor.M_EDITOR_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		//Save backup file(s)
		try {
			MEditorUtils.syncBackup(projectName, routineName, serverCode);
		} catch (CoreException e) {
			// show warning only
			e.printStackTrace();
			e.printStackTrace();
			MessageDialog.openWarning(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "MEditor",
					"Routine Saved on server, but error occured while syncing the local backup file: " +e.getMessage());	
		}
	}

	private void createFolders(String relPath, IProject project) {
		
		if (relPath.equals("") || project.getFolder(relPath).exists())
			return;

		
		
		String[] paths;
		if (relPath.indexOf("/") == -1)
			paths = new String[] {relPath};
		else
			paths = relPath.split("/");
		String createPath = "";
		for (int i = 0; i < paths.length; i++){
			 createPath += paths[i] + "/";
			if (project.getFolder(createPath).exists())
				continue;

				try {
					project.getFolder(createPath).create(true, true, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private Path searchForFile(Path path, final String fileName) {
		
		if (fileName == null)
			return null;
		
		PathFileSearchVisitor fv = new PathFileSearchVisitor(fileName);
	      try {
	        Files.walkFileTree(path, fv);
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
		
//		for (Path file : directory.) {
//			if (file.isDirectory())
//				return searchForFile(directory, fileName);
//			else {
//				if (file.getName().equals(fileName))
//					return Paths.get(file.toURI());
//			}
//		}
		
		return fv.getResult();		
	}
	
	private boolean validateInput(RoutineNameDialogData dialogInput, String routineName) {
		if (!dialogInput.getButtonResponse())
			return false;
		
		if (!routineName.matches("%?[A-Z][A-Z0-9]+")) {
			//TODO: put validations into (redesigned) dialog class so the user sees this input immediately before they close the dialog
			return false;
		} else if (routineName.length() > 8) {
			//TODO: put validations into (redesigned) dialog class so the user sees this input immediately before they close the dialog
			return false;
		}
		
		return true;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}