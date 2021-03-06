//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package us.pwc.vista.eclipse.server.command;

import java.util.List;

import gov.va.med.iss.connection.VistAConnection;
import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreePath;

import us.pwc.vista.eclipse.core.ServerData;
import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.core.CommandResult;
import us.pwc.vista.eclipse.server.core.LoadRoutineEngine;
import us.pwc.vista.eclipse.server.core.MServerRoutine;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;

/**
 * This implementation of <code>AbstractHandler</code> loads the selected M 
 * files (routines), or the specified routine or all the routines in the specified 
 * namespace from the M server.
 *
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class LoadRoutine extends AbstractHandler {
	private IFolder getFolder(final ExecutionEvent event) {
		TreePath[] paths = CommandCommon.getTreePaths(event);
		if (paths == null) {
			return null;
		}
		if (paths.length != 1) {
			MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, Messages.MULTI_LOAD_RTN_FOLDER_SINGLE);
			return null;
		}		
		IFolder folder = ResourceUtilExtension.getFolder(paths[0]);
		if (folder == null) {
			MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, Messages.MULTI_LOAD_RTN_FOLDER_ONLY);
			return null;			
		}
		
		return folder;
	}
	
	private String[] getRoutinesInNamespace(VistAConnection vistaConnection) {
		ServerData data = vistaConnection.getServerData();
		String title = Messages.bind(Messages.LOAD_M_RTNS_DLG_TITLE, data.getAddress(), data.getPort());
		String routineNamespace = InputDialogHelper.getRoutineNamespace(title);
		if (routineNamespace == null) {
			return null;
		}
		
		String routines = CommandCommon.getRoutineNames(vistaConnection, routineNamespace);
		if (routines == null) {
			return null;
		}
		if (routines.isEmpty()) {
			String message = Messages.bind(Messages.MULTI_LOAD_RTN_NONE_IN_NAMESPC, routineNamespace);
			MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, message);
			return null;						
			
		}
		
		String[] routineArray = routines.split("\n");
		return routineArray;
	}
	
	private String[] getRoutine(VistAConnection vistaConnection) {
		ServerData data = vistaConnection.getServerData();
		String title = Messages.bind(Messages.LOAD_M_RTN_DLG_TITLE, data.getAddress(), data.getPort());
		String routineName = InputDialogHelper.getRoutineName(title);
		if (routineName == null) {
			return null;
		}
		return new String[]{routineName};
	}
	
	private List<IFile> getFiles(final ExecutionEvent event, boolean namespaceFlag, boolean folderFlag) {
		if (folderFlag) {
			IFolder folder = this.getFolder(event);
			if (folder == null) {
				return null;
			}
			IProject project = folder.getProject();
			VistAConnection vistaConnection = VLConnectionPlugin.getConnectionManager().getConnection(project);
			if (vistaConnection == null) {
				return null;
			}
			
			String[] routines = namespaceFlag ? this.getRoutinesInNamespace(vistaConnection) : this.getRoutine(vistaConnection);
			if (routines == null) {
				return null;
			}
			
			return CommandCommon.getFileHandles(folder, routines);
		} else {
			List<IFile> selectedFiles = CommandCommon.getSelectedMFiles(event);
			return selectedFiles;
		}		
	}
		
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Object namespaceParam = event.getObjectParameterForExecution("us.pwc.vista.eclipse.server.command.loadRoutine.namespace");
		boolean namespaceFlag = ((Boolean) namespaceParam).booleanValue();
		
		Object folderParam = event.getObjectParameterForExecution("us.pwc.vista.eclipse.server.command.loadRoutine.folder");
		boolean folderFlag = ((Boolean) folderParam).booleanValue();

		List<IFile> files = getFiles(event, namespaceFlag, folderFlag);
		if (files != null) {
			IFile firstFile = files.get(0);
			IProject project = firstFile.getProject();
			VistAConnection vistaConnection = VLConnectionPlugin.getConnectionManager().getConnection(project);
			if (vistaConnection == null) {
				return null;
			}		
			if (files.size() == 1) {
				CommandResult<MServerRoutine> r = LoadRoutineEngine.loadRoutine(vistaConnection, firstFile);
				MessageDialogHelper.logAndShow(Messages.LOAD_MSG_TITLE, r.getStatus());
			} else {
				CommandCommon.loadRoutines(vistaConnection, files);
			}
		}
		return null;
	}
}
