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

package gov.va.mumps.debug.ui;

import gov.va.mumps.debug.core.model.MCacheTelnetDebugTarget;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import us.pwc.vista.eclipse.terminal.VistATerminalView;

class CacheTelnetUIManager implements ILaunchListener {
	private IViewPart view;
	
	@Override
	public void launchAdded(final ILaunch launch) {
		this.view = null;
	}

	@Override
	public void launchChanged(ILaunch launch) {
		if (launch.getDebugTarget() != null) {
			final MCacheTelnetDebugTarget target = (MCacheTelnetDebugTarget) launch.getDebugTarget();
		
			synchronized (target) {
				final CacheTelnetUIManager thiz = this;
				Display.getDefault().syncExec(new Runnable() {						
					@Override
					public void run() {
						try {
							IWorkbench wb = PlatformUI.getWorkbench();
							IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
							IWorkbenchPage wbp = window.getActivePage();
							IViewPart vp = wbp.findView("us.pwc.vista.eclipse.terminal.VistATerminalView");
							if (vp == null) {
								try {
									vp = wbp.showView("us.pwc.vista.eclipse.terminal.VistATerminalView");
								} catch (Throwable t) {
									vp = null;
								}
							}							
							thiz.view = vp;
							((VistATerminalView) vp).connect(target); 
						} catch (Throwable t) {
						}
					}
				});
			}
		}
	}

	@Override
	public void launchRemoved(ILaunch launch) {
		if (this.view != null) {
			this.view.getSite().getWorkbenchWindow().getActivePage().hideView(this.view);
		}
	}
}