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

package us.pwc.vista.eclipse.server.core;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import us.pwc.vista.eclipse.server.VistAServerPlugin;

public class StatusHelper {
	public static IStatus getStatus(IStatus status, String pluginId, String newMessage) {
		return new Status (status.getSeverity(), status.getPlugin(), status.getCode(), newMessage, status.getException());
	}
	
	public static int updateOverallSeverity(int overallSeverity, int newSeverity) {
		int[] severities = new int[]{IStatus.ERROR, IStatus.WARNING, IStatus.INFO};
		for (int severity : severities) {
			if (newSeverity == severity) return severity;
			if (overallSeverity == severity) return severity;			
		}
		return IStatus.OK;	
	}
	
	public static MultiStatus getMultiStatus(int severity, String message, List<IStatus> statuses) {
		IStatus[] statusesAsArray = statuses.toArray(new IStatus[0]);
		MultiStatus result = new MultiStatus(VistAServerPlugin.PLUGIN_ID, IStatus.OK, statusesAsArray, message, null);
		return result;
	}
		
	public static int updateStatuses(IStatus status, String pluginId, String prefixForFile, int overallSeverity, List<IStatus> statuses) {
		if (status.getSeverity() == IStatus.OK) {
			IStatus newStatus = new Status(IStatus.INFO, pluginId, prefixForFile + "no issues");
			statuses.add(newStatus);
			return IStatus.INFO;
		} else {
			IStatus newStatus = new Status(status.getSeverity(), status.getPlugin(), status.getCode(), prefixForFile + status.getMessage() + "\n", status.getException());
			statuses.add(newStatus);
			int severity = status.getSeverity();
			return StatusHelper.updateOverallSeverity(overallSeverity, severity);				
		}	
	}
}
