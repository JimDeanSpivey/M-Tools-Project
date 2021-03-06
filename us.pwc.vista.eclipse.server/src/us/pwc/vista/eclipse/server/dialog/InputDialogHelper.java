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

package us.pwc.vista.eclipse.server.dialog;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import us.pwc.vista.eclipse.core.validator.ICommonRegexs;
import us.pwc.vista.eclipse.core.validator.RegexInputValidator;
import us.pwc.vista.eclipse.server.Messages;

public class InputDialogHelper {
	private static String getRequiredEntity(String title, String entityName, String regex) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String inputMessage  = Messages.bind(Messages.INPUT_DLG_INPUT_MSG, entityName);
		
		IInputValidator validator = new RegexInputValidator(true, ICommonRegexs.M_ROUTINE_NAME, entityName);
		
		final InputDialog dialog = new InputDialog(shell, title, inputMessage, "", validator);
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				dialog.open();
			}
		});
		if (dialog.getReturnCode() == Window.OK) {
			String routineName = dialog.getValue();
			return routineName;
		} else {
			return  null;
		}
	}
	
	public static String getRoutineName(String title) {
		return getRequiredEntity(title, Messages.ROUTINE_NAME, ICommonRegexs.M_ROUTINE_NAME);
	}

	public static String getRoutineNamespace(String title) {
		return getRequiredEntity(title, Messages.ROUTINE_NAMESPACE, ICommonRegexs.M_ROUTINE_NAME);
	}

	public static String getGlobalNamespace(String title) {
		return getRequiredEntity(title, Messages.GLOBAL_NAMESPACE, null);
	}
}
