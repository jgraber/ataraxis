/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2010 Johnny Graber & Andreas Muedespacher
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Text;

import ataraxis.gui.AtaraxisMainGUI;


/**
 * This class is used to handle the drop action for Target-Text fields
 */
public final class DropTargetAdapterTarget extends DropTargetAdapter
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisMainGUI.class);

	// SWT references
	private final Text source;
	private final Text target;
	
	// is encryption mode
	private final boolean encrypt;
	
	// Suffixes
	private static final String SUFFIX_FILE = ".ac";
	

	public DropTargetAdapterTarget(Text targetField, Text sourceField, boolean encrypt)
	{
		this.target = targetField;
		this.source = sourceField;
		this.encrypt = encrypt;
	}

	public void drop(DropTargetEvent event)
	{
		LOGGER.info("Data has been dropped in target");
		String fileList[] = null;
		final FileTransfer ft = FileTransfer.getInstance();
		if (ft.isSupportedType(event.currentDataType))
		{
			LOGGER.info("Dropped Data is one or more files");
			fileList = (String[])event.data;
			if (fileList != null && fileList[0].length() > 0)
			{
				if (!fileList[0].endsWith(SUFFIX_FILE))
					target.setText(fileList[0] + SUFFIX_FILE);
				else
					target.setText(fileList[0]);

				// to autocomplete source entry:
				String text = "";
				if(fileList[0].endsWith(SUFFIX_FILE))
					text = fileList[0].substring(0, fileList[0].length() - SUFFIX_FILE.length());
				else if (encrypt)
				{
					text = fileList[0];
				}
				else
				{
					text = fileList[0]+ SUFFIX_FILE;
					target.setText(fileList[0]);
				}

				source.setText(text);
			}
		}
	}
}

