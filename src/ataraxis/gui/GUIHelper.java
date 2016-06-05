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

import java.io.File;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ataraxis.gui.content.AtaraxisInfoGUI;


/**
 * GUIHelper combines utility methods for the GUI
 * of AtaraxiS.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 */
public class GUIHelper 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisInfoGUI.class);


	public static int COMPOSITE_WIDTH = 456;
	public static int COMPOSITE_HEIGHT = 438;
	public static int GROUP_WIDTH = 450;
	public static int GROUP_HEIGHT = 120;
	public static int TEXT_WIDTH = 425;
	public static int BUTTON_HEIGHT = 80;
	public static int BUTTON_WIDTH = 120;

	private Shell s_shell;

	// file suffixes for AtaraxiS
	private static final String SUFFIX_FILE = ".ac";
	private static final String SUFFIX_FOLDER = ".acz";
	private static final String SUFFIX_ZIP = ".zip";

	
	// Actions for browse
	public static final int ENCRYPT_SOURCE = 1;
	public static final int ENCRYPT_TARGET = 2;
	public static final int DECRYPT_SOURCE = 3;
	public static final int DECRYPT_TARGET = 4;
	public static final int SHRED_SOURCE = 5;
	
	// Paths
	private static final String APPL_DIR = System.getProperty("user.dir");
	

	public GUIHelper(Shell shell)
	{
		s_shell = shell;
	}

	/**
	 * Helper-Method to show a warning message.
	 * @param title the message title
	 * @param message the message text
	 */
	public void displayWarningMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_WARNING | SWT.YES);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Helper-Method to show a error message.
	 * @param title the message title
	 * @param message the message text
	 */
	public void displayErrorMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Helper-Method to show a info message.
	 * @param title the message title
	 * @param message the message text
	 */
	public void displayInfoMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_INFORMATION | SWT.YES);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Helper-Method to show a question message and return answer.
	 * @param title the message title
	 * @param message the message text (a question!)
	 * @return true if user answer is yes
	 */
	public boolean displayQuestionMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText(title);
		messageBox.setMessage(message);
		return (messageBox.open() == SWT.YES);
	}

	/**
	 * Getter for the middle font in AtaraxiS.
	 * 
	 * @param display to create the font
	 * @return
	 */
	public Font getMiddleFont(Display display)
	{
		final FontData[] fontData = s_shell.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++)
		{
			fontData[i].setHeight(16);
		}

		return new Font(display, fontData);
	}

	/**
	 * Getter for the big font in AtaraxiS.
	 * 
	 * @param display to create the font
	 * @return
	 */
	public Font getBigFont(Display display)
	{
		final FontData[] fontData = s_shell.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++)
		{
			fontData[i].setHeight(32);
		}

		return new Font(display, fontData);
	}

	/**
	 * Method to check if file exists, if not a info message opens
	 * @param source the file to check
	 * @return true if file exists
	 */
	public boolean fileExists(String source, ResourceBundle s_translations)
	{
		boolean exists = false;
		if ((new File(source)).exists()) 
			exists = true;
		else
			displayInfoMessage(s_translations.getString("MESSAGE.NO.SOURCE.TITLE"), s_translations.getString("MESSAGE.NO.SOURCE"));
		return exists;
	}
	
	/**
	 * Method to check if target file already exists.
	 * Ask user if existing file should be overwritten.
	 * @return true if file doesn't exist or user wants to overwrite it
	 */
	public boolean createTargetFile(String target, ResourceBundle s_translations)
	{
		final File checkFile = new File(target);
		boolean createFile = true;
		if (checkFile.exists() && !checkFile.isDirectory()) 
		{
			if(!displayQuestionMessage(s_translations.getString("MESSAGE.OVERWRITE.TITLE"), s_translations.getString("MESSAGE.OVERWRITE")))
				createFile = false;
		}
		return createFile;
	}
	
	/**
	 * This method is used to browse for a source file (open)
	 * @param action 'COMPOSITE'_'FIELD' i.e. ENCRYPT_SOURCE
	 */
	public void browseOpenFile(int action, Text sourceTextField, Text targetTextField,  ResourceBundle s_translations)
	{
		FileDialog fileDialog = null;
		fileDialog = new FileDialog (s_shell, SWT.OPEN);
		String path = sourceTextField.getText();

		if (action == DECRYPT_SOURCE)
		{
			fileDialog.setFilterExtensions(new String [] {"*" + SUFFIX_FILE + "; *" + SUFFIX_FOLDER, "*.*"}); //Windows wild cards
			fileDialog.setFilterNames(new String [] {s_translations.getString("FILEFILTER.ATARAXIS") + " (*.ac; *.acz)", s_translations.getString("FILEFILTER.ALL") + " (*.*)"});
		}
		else  // for ENCRYPT and SHRED open file dialog
		{
			fileDialog.setFilterExtensions(new String [] {"*.*"}); //Windows wild cards
			fileDialog.setFilterNames(new String [] {s_translations.getString("FILEFILTER.ALL") + " (*.*)"});
		}

		if (!path.equalsIgnoreCase(""))
		{
			fileDialog.setFilterPath(path);
			int separator;
			separator = path.lastIndexOf("/") + 1;
			fileDialog.setFileName(path.substring(separator, path.length()));
		}
		else
			fileDialog.setFilterPath(new File(APPL_DIR).getParent());

		final String fileName = fileDialog.open();

		if (fileName != null)
		{
			switch (action)
			{
			case ENCRYPT_SOURCE:
				sourceTextField.forceFocus();
				sourceTextField.setText(fileName);
				if (targetTextField.getText().equals(""))
				{
					if (fileName.endsWith(SUFFIX_ZIP))
						targetTextField.setText(fileName + SUFFIX_FOLDER);
					else
						targetTextField.setText(fileName + SUFFIX_FILE);
				}
				break;
			case DECRYPT_SOURCE:
				sourceTextField.forceFocus();
				sourceTextField.setText(fileName); 
				if (targetTextField.getText().equals("") && fileName.endsWith(SUFFIX_FILE))
					targetTextField.setText(fileName.substring(0, fileName.length() - SUFFIX_FILE.length()));
				if (targetTextField.getText().equals("") && fileName.endsWith(SUFFIX_FOLDER))
				{
					targetTextField.setText((new File(fileName)).getParent());
				}
				//System.err.println(fileName);
				//s_textDecryptTarget.setText(fileName.substring(0, fileName.lastIndexOf(File.pathSeparator)));
				break;
			case SHRED_SOURCE:
				sourceTextField.forceFocus();
				sourceTextField.setText(fileName); 
				break;
			default: 
				LOGGER.fatal("don't know where to fill the text field (not: encrypt, decrypt or shred)!");
			}
		}
	}

		/**
		 * This method is used to browse for a source folder (open).
		 * @param action 'COMPOSITE'_'FIELD' i.e. ENCRYPT_SOURCE
		 */
		public void browseOpenFolder(int action, Text sourceTextField, Text targetTextField,  ResourceBundle s_translations)
		{
			DirectoryDialog dirDialog = null;
			dirDialog = new DirectoryDialog (s_shell, SWT.OPEN);

			// if file is selected, open dialog at this location
			String path = sourceTextField.getText();
			
			if (! path.equalsIgnoreCase(""))
			{
				dirDialog.setFilterPath(path);
				int separator;
				separator = path.lastIndexOf("/") + 1;
				dirDialog.setFilterPath(path.substring(separator, path.length()));
			}

			else
				dirDialog.setFilterPath(new File(APPL_DIR).getParent());

			final String fileName = dirDialog.open();

			if (fileName != null)
			{
				if (action == ENCRYPT_SOURCE)
				{
					sourceTextField.forceFocus(); 
					sourceTextField.setText(fileName);
					if (targetTextField.getText().equals(""))
						targetTextField.setText(fileName + SUFFIX_FOLDER);
				}

				else{ // Shredder, cause only Enc Source and Shredder can work with folders
					sourceTextField.forceFocus(); 
					sourceTextField.setText(fileName);
				}
			} // if fileName not null
		}
		
		/**
		 * This method is used to browse for a target file (save).
		 * @param action ENCRYPT_TARGET or DECRYPT_TARGET
		 */
		public void browseSave(int action, Text targetTextField,  ResourceBundle s_translations)
		{
			FileDialog fileDialog = null;
			String path = "";
			fileDialog = new FileDialog (s_shell, SWT.SAVE);
			if (action == ENCRYPT_TARGET){
				path = targetTextField.getText();
				fileDialog.setFilterExtensions (new String [] {"*" + SUFFIX_FILE + "; *" + SUFFIX_FOLDER, "*.*"}); //Windows wild cards
				fileDialog.setFilterNames(new String [] {s_translations.getString("FILEFILTER.ATARAXIS") + " (*.ac; *.acz)", s_translations.getString("FILEFILTER.ALL") + " (*.*)"});
			}
			else // so it must be decrypt!
			{
				path = targetTextField.getText();
				fileDialog.setFilterExtensions (new String [] {"*.*"}); //Windows wild cards
				fileDialog.setFilterNames(new String [] {s_translations.getString("FILEFILTER.ALL") + " (*.*)"});
			}
			if (! path.equalsIgnoreCase(""))
			{
				fileDialog.setFilterPath(path);
				int separator;
				separator = path.lastIndexOf("/") + 1;
				fileDialog.setFileName(path.substring(separator, path.length()));
			}
			else
			{
				fileDialog.setFilterPath(new File(APPL_DIR).getParent());
			}

			final String fileName = fileDialog.open();

			if (fileName != null)
			{
				targetTextField.forceFocus();
				targetTextField.setText(fileName);				
			} // if fileName not null
		}
		
		/**
		 * Method to change the background color recursivly (all childs).
		 * @param parent the parent composite
		 * @param newColor the background color to set
		 */
		public void changeBackgroundColor(Composite parent, Color newColor)
		{
			parent.setBackground(newColor);
			final Control [] children = parent.getChildren();
			for (int i=0; i<children.length; i++)
			{
				//children[i].setBackground(newColor);
				if (children[i] instanceof Composite)
					changeBackgroundColor((Composite) children[i], newColor);
				else if (!(children[i] instanceof Text))
					children[i].setBackground(newColor);	
			}
		}	
}
