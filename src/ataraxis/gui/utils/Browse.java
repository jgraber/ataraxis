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
package ataraxis.gui.utils;

import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import ataraxis.gui.GUIHelper;
import ataraxis.gui.content.AtaraxisEncryptGUI;


/**
 * This class is used to browse for a Source and for a target file.
 * The int parameter can be AtaraxisGUI.OPEN or AtaraxisGUI.SAVE
 */
public final class Browse extends SelectionAdapter
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisEncryptGUI.class);

	private boolean opearateOnFolder = false;
	
	private int action;
	private Text sourceText;
	private Text targetText;

	private GUIHelper guiHelper;
	private ResourceBundle s_translations;
	
	public Browse(int action, Text source, GUIHelper guiHelper, ResourceBundle s_translations)
	{
		this.action = action;
		sourceText = source;
		this.guiHelper = guiHelper;
		this.s_translations = s_translations;
	}
	
	public Browse(int action, boolean opearateOnFolder, Text source, Text target, GUIHelper guiHelper, ResourceBundle s_translations)
	{
		this.action = action;
		sourceText = source;
		targetText = target;
		this.guiHelper = guiHelper;
		this.s_translations = s_translations;
		this.opearateOnFolder = opearateOnFolder;
	}

	public void widgetSelected(SelectionEvent e)
	{
		if (opearateOnFolder)
		{
			guiHelper.browseOpenFolder(action, sourceText, targetText, s_translations);
			LOGGER.debug("FOLDER (open)");
		}
		else
		{
			LOGGER.debug("FILE (open or save)");
			if(action == GUIHelper.ENCRYPT_TARGET || action == GUIHelper.DECRYPT_TARGET)
				guiHelper.browseSave(action, sourceText, s_translations);
			else
				guiHelper.browseOpenFile(action, sourceText, targetText, s_translations);
		}
	}
}
