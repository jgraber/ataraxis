/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2010 Johnny Graber & Andreas Muedespacher
 * ----------------------------------------------------------------------------
 * 
 * This File is part of AtaraxiS (http://ataraxis.origo.ethz.ch/) and is
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
package ch.ethz.origo.ataraxis.gui.utils;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import ch.ethz.origo.ataraxis.gui.GUIHelper;
import ch.ethz.origo.ataraxis.gui.content.AtaraxisEncryptGUI;

/**
 * This class is used to browse for a Source and for a target file.
 * The int parameter can be AtaraxisGUI.OPEN or AtaraxisGUI.SAVE
 */
public final class Browse extends SelectionAdapter
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisEncryptGUI.class);

	
	private static final int FILE = 1;
	private static final int FOLDER = 2;
	
	private static int s_encryptFiletype = 0;
	private static int s_shredFiletype = 0;
	
	int action;
	int filetype = FILE;
	Text sourceText;
	Text targetText;

	private GUIHelper guiHelper;
	private ResourceBundle s_translations;

	private static final int ENCRYPT_SOURCE = 1;
	private static final int ENCRYPT_TARGET = 2;
	private static final int DECRYPT_TARGET = 4;
	private static final int SHRED_SOURCE = 5;
	
	public Browse(int action, Text source, GUIHelper guiHelper, ResourceBundle s_translations)
	{
		this.action = action;
		sourceText = source;
		this.guiHelper = guiHelper;
		this.s_translations = s_translations;
	}
	
	public Browse(int action, Text source, Text target, GUIHelper guiHelper, ResourceBundle s_translations)
	{
		this.action = action;
		sourceText = source;
		targetText = target;
		this.guiHelper = guiHelper;
		this.s_translations = s_translations;
	}

	public void widgetSelected(SelectionEvent e)
	{
		// if called from shred source, then set filetype (file/folder)
		if (action == SHRED_SOURCE)
		{
			this.filetype = s_shredFiletype;
			LOGGER.debug("Browse in SHRED_SOURCE");
		}
		// if called from encrypt source, then set filetype (file/folder)
		else if(action == ENCRYPT_SOURCE)
		{
			this.filetype = s_encryptFiletype;
			LOGGER.debug("Browse in ENCRYPT_SOURCE");
		}

		if (filetype == FOLDER)
		{
			guiHelper.browseOpenFolder(action, sourceText, targetText, s_translations);
			LOGGER.debug("FOLDER (open)");
		}
		else
		{
			LOGGER.debug("FILE (open or save)");
			if(action == ENCRYPT_TARGET || action == DECRYPT_TARGET)
				guiHelper.browseSave(action, sourceText, s_translations);
			else
				guiHelper.browseOpenFile(action, sourceText, targetText, s_translations);
		}
	}
}
