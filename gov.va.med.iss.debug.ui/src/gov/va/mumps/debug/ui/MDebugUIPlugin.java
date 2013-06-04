package gov.va.mumps.debug.ui;

import gov.va.mumps.debug.core.model.MDebugTarget;
import gov.va.mumps.debug.ui.console.MDevConsole;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class MDebugUIPlugin extends AbstractUIPlugin implements
		ILaunchListener {

	Map<MDebugTarget,MDevConsole> consoles;
	
	@Override
	public void start(BundleContext context) throws Exception { //TODO: what if this is started after launches were already added? is that possible? Can a launch be created without activating this UI plugin?
		super.start(context);	
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
		consoles = new HashMap<MDebugTarget,MDevConsole>(5);
	}

	@Override
	public void launchAdded(final ILaunch launch) {
	}

	@Override
	public void launchChanged(ILaunch launch) {
		
		if (launch.getDebugTarget() == null)
			return;
		MDebugTarget mDebugTarget = (MDebugTarget) launch.getDebugTarget();
		
		synchronized (mDebugTarget) {
			if (mDebugTarget.isLinkedToConsole())
				return;
			
//			String debugTargetName = launch.getLaunchConfiguration().getName();
//			try {
//				String debugTargetName = mDebugTarget.getName();
//			} catch (DebugException e) {
//			}
//			
			MDevConsole mDevConsole = new MDevConsole("MUMPS Console: " +launch.getLaunchConfiguration().getName(), null, null, true);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { mDevConsole });
			
			mDebugTarget.addReadCommandListener(mDevConsole);
			mDebugTarget.addWriteCommandListener(mDevConsole);
			mDevConsole.addInputReadyListener(mDebugTarget);
			
			mDebugTarget.setLinkedToConsole(true);
			consoles.put(mDebugTarget, mDevConsole);
		}
	}

	@Override
	public void launchRemoved(ILaunch launch) {
		
		if (launch.getDebugTarget() == null)
			return;
		MDebugTarget mDebugTarget = (MDebugTarget) launch.getDebugTarget();
		MDevConsole mDevConsole = consoles.get(mDebugTarget);
		
		ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] { mDevConsole});
		consoles.remove(mDevConsole);
	}
}
