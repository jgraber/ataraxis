/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2016 Johnny Graber & Andreas Muedespacher
 * ----------------------------------------------------------------------------
 * 
 * This File is part of AtaraxiS (https://github.com/jgraber/ataraxis) and is
 * licensed under the European Public License, Version 1.1 only (the "Licence").
 * You may not use this work except in compliance with the Licence. 
 * 
 * You may obtain a copy of the Licence at: 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence. 
 */

package ataraxis.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;



public class NavigationListener implements Listener 
{
	Composite parent = null;
	int selectedComp = 0;
	
	public NavigationListener(Composite parent)
    {
        this.parent = parent;
    }

	public void handleEvent (Event e)
	{
		Control [] children = parent.getChildren ();
		for (int i=0; i<children.length; i++) 
		{
			Control child = children [i];
			if (e.widget != child && child instanceof Button &&
					(child.getStyle () & SWT.TOGGLE) != 0)
				((Button) child).setSelection (false);
			if (e.widget == child)
				selectedComp = i + 1;
		}
		((Button) e.widget).setSelection (true);
		AtaraxisMainGUI.setSelectedComposite(selectedComp);
	}
}
