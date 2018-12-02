/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2018 Johnny Graber & Andreas Muedespacher
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
package ataraxis.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.gui.AtaraxisLoginGUI;
import ataraxis.gui.AtaraxisSplashScreen;
import ataraxis.gui.GUIHelper;
import ataraxis.passwordmanager.AccountEntry;
import ataraxis.passwordmanager.PasswordEntry;
import ataraxis.passwordmanager.PasswordManager;
import ataraxis.passwordmanager.StorageException;
import ataraxis.util.AtaraxisHelper;

/**
 * AtaraxisPasswordExport is used to export the passwords managed in AtaraxiS.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public class AtaraxisPasswordExport {
	
	
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisBackup.class);

	private static Shell s_shell;
	private static GUIHelper s_guiHelper;
	private static FileDialog s_fileDilaog;
	private static Properties s_langProps;
	private static String s_PWExportFileDialogTitle;
	private static String s_PWExportOK;
	private static String s_PWExportTitle;
	private static MessageBox s_messageBox;
	
	/**
	 * Create a AtaraxisPasswordExport instance, ask the user its 
	 * password and for a place to store the exported passwords.
	 * @throws StorageException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, StorageException 
	{
		Date date = new Date();

		AtaraxisHelper ataraxisHelper = new AtaraxisHelper();
		
		
		String runtimeLanguage = System.getProperty("user.language");
		Locale systemLanguage = new Locale(runtimeLanguage);
		ResourceBundle translations = ResourceBundle.getBundle("AtaraxisTranslation", systemLanguage); 
		
		LOGGER.info("##### Entering splash screen at: " + date.toString() + " #####");
		AtaraxisSplashScreen splashScreen = new AtaraxisSplashScreen(translations);
		boolean policyIsOk = splashScreen.open();
		
		LOGGER.info("Splashscreen: policyIsOK => " + policyIsOk);
		LOGGER.debug("##### Exiting splash screen at: " + date.toString() + " #####");
		
		
		LOGGER.info("##### Entering login GUI at: " + date.toString() + " #####");
		final AtaraxisLoginGUI login = new AtaraxisLoginGUI(translations);
		AtaraxisCrypter s_ac = login.open();
		date = new Date();
		LOGGER.debug("##### Exiting login GUI at: " + date.toString() + " #####");
		
		if (s_ac == null)
		{
			LOGGER.warn("AtaraxisCrypter could not be initalised!");
		}
		else // message window is opened by AtaraxisLoginGUI
		{
			date = new Date();
			String userLanguageString = "en";
			
			try {
				userLanguageString = ataraxisHelper.GetUserLanguage(s_ac.getKeyStorePath());
			} catch (FileNotFoundException e) {
				LOGGER.warn("Properties File not found", e);
			} catch (IOException e) {
				LOGGER.warn("Error with properties File", e);
			}
			
			Locale userLanguage = new Locale(userLanguageString);
			ResourceBundle.clearCache();
			translations = ResourceBundle.getBundle("AtaraxisTranslation", userLanguage); 
			
			LOGGER.debug("##### Entering main GUI at: " + date.toString() + " #####");
			
			final AtaraxisPasswordExport export = new AtaraxisPasswordExport(new Shell(Display.getDefault()));
			
			String location = export.getBackupFile();
			
			if(location == null)
			{
				// No file selected
				LOGGER.info("No File selected for backup target");
				s_guiHelper.displayErrorMessage("Error", "Export failed");
			}
			else {
				export.savePasswords(s_ac, location);	
				
				s_messageBox = new MessageBox(s_shell, SWT.ICON_INFORMATION | SWT.OK);
				s_messageBox.setMessage(s_PWExportOK);
				s_messageBox.setText(s_PWExportTitle);
				s_messageBox.open();
				LOGGER.info("Backup successful stored to: "+ location);
			}
			
			
			date = new Date();
			LOGGER.info("##### Exiting application at: " + date.toString() + " #####");
		}
		
		
	}

	private String getBackupFile() {
		s_fileDilaog.setFilterPath(System.getProperty("user.home"));
		String[] filterExtensions = {"*.csv"};
		s_fileDilaog.setFilterExtensions(filterExtensions);
		s_fileDilaog.setText(s_PWExportFileDialogTitle);
		
		String s_backupFile = s_fileDilaog.open();
		return s_backupFile;
	}


	/**
	 * Create a AtaraxisPasswordExport instance.
	 *
	 */
	public AtaraxisPasswordExport(Shell parent)
	{
		s_guiHelper = new GUIHelper(parent);
		s_shell = parent;
		s_fileDilaog = new FileDialog(s_shell, SWT.SAVE );
		loadProperties();
	}
	
	public void savePasswords(AtaraxisCrypter s_ac, String filePath) throws FileNotFoundException, IOException, StorageException {
		String pathOfKeyStore = s_ac.getKeyStorePath();
		File ksFile = new File(pathOfKeyStore);
		File directoryOfKs = ksFile.getParentFile();
		directoryOfKs.getName();
		String s_accountFile = directoryOfKs.getAbsolutePath() + "/accounts.data";
		
		PasswordManager pwManager = new PasswordManager(s_ac, s_accountFile);
		List<PasswordEntry> entries = pwManager.getAllEntries();
		
		List<String> rows = new ArrayList<String>();
		String firstLine = "\"Group\",\"Account\",\"Login Name\",\"Password\",\"Web Site\",\"Comments\"";
		
		
		
		Iterator<PasswordEntry> listIterator = entries.iterator();
		
		while(listIterator.hasNext())
		{
			PasswordEntry current = listIterator.next();
			
			if(current.getType().equals("account"))
			{
				AccountEntry currentAccount = (AccountEntry) current;
				
				PasswordEntry parent = currentAccount.getParentEntry();
				
				if(parent != null) {
					rows.add("\"" + parent.getId() +"\",\"" + currentAccount.getId()  +"\",\"" + currentAccount.getName() +"\",\"" + currentAccount.getPassword() +"\",\"" + currentAccount.getLink() +"\",\"" + currentAccount.getComment()+"\"");
				}
				else {
					rows.add("\"\",\"" + currentAccount.getId()  +"\",\"" + currentAccount.getName() +"\",\"" + currentAccount.getPassword() +"\",\"" + currentAccount.getLink() +"\",\"" + currentAccount.getComment()+"\"");
				}
			}	
			
		}
		java.util.Collections.sort(rows);
		
		rows.add(0, firstLine);
		
		
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		 
		for (String item : rows) {
		    writer.write(item);
		    writer.newLine();
		} 
		 
		writer.flush();
		writer.close();
	}
	
	/**
	 * Load the language properties.
	 */
	private void loadProperties() 
	{
		// Load the language file
		if(s_langProps == null)
		{
			s_langProps = new Properties();
			LOGGER.debug("Language: default values are used");
		}

		// set the strings
		s_PWExportFileDialogTitle = s_langProps.getProperty("ABACK.BACKUP_DIALOG_TITLE", "Select file to export the passwords");
		s_PWExportTitle  = s_langProps.getProperty("ABACK.PWExport_TITLE", "AtaraxiS Password Export");
		s_PWExportOK  = s_langProps.getProperty("ABACK.PWExport_OK", "Export was successful.");		
	} 
	
}
