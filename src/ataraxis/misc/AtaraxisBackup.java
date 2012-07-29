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

package ataraxis.misc;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;

import ataraxis.util.FileList;


/**
 * AtaraxisBackup creates backups of the user_data directory.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public class AtaraxisBackup 
{

	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String USER_DATA_DIR = APPL_DIR + "/user_data";
	
	private static final Logger LOGGER = Logger.getLogger(AtaraxisBackup.class);

	private static Shell s_shell;
	private static FileDialog s_fileDilaog;
	private static Properties s_langProps;
	private static String s_BackupFileDialogTitle;
	private static String s_BackupOK;
	private static String s_BackupFail;
	private static String s_backupFile;
	private static String s_BackupTitle;
	private static MessageBox s_messageBox;
	
	/**
	 * Create a AtaraxisBackup instance and ask the user for a place to store 
	 * the backup of the user_data directory.
	 */
	public static void main(String[] args) 
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		
		AtaraxisBackup backup = new AtaraxisBackup(new Shell(new Display()));
		backup.makeBackup();
	}


	/**
	 * Create a AtaraxisBackup instance.
	 *
	 */
	public AtaraxisBackup(Shell parent)
	{
		s_shell = parent;
		s_fileDilaog = new FileDialog(s_shell, SWT.SAVE );
		loadProperties();
	}


	/**
	 * Constructor for running it inside an application.
	 *
	 * @param ParentShell the parent shell
	 * @param languageProperties the language properties
	 */
	public AtaraxisBackup(Shell ParentShell, Properties languageProperties)
	{
		s_shell = ParentShell;
		s_fileDilaog = new FileDialog(s_shell, SWT.SAVE);
		s_langProps = languageProperties;
		loadProperties();
	}


	/**
	 * Ask the user for the File, where the user_data-Backup should be stored.
	 *
	 * @return true if Backup was successful
	 */
	protected boolean makeBackup()
	{
		boolean backupSuccessful = true;
		
		
		s_fileDilaog.setFilterPath(System.getProperty("user.home"));
		String[] filterExtensions = {"*.zip"};
		s_fileDilaog.setFilterExtensions(filterExtensions);
		s_fileDilaog.setText(s_BackupFileDialogTitle);
		
		s_backupFile = s_fileDilaog.open();
		if(s_backupFile == null)
		{
			// No file selected
			LOGGER.info("No File selected for backup target");
			backupSuccessful = false;
		}
		else
		{
			// Try to backup to the selected file
			
			backupSuccessful = createZipFile(s_backupFile, USER_DATA_DIR);
		
			if(backupSuccessful)
			{
				s_messageBox = new MessageBox(s_shell, SWT.ICON_INFORMATION | SWT.OK);
				s_messageBox.setMessage(s_BackupOK);
				s_messageBox.setText(s_BackupTitle);
				s_messageBox.open();
				LOGGER.info("Backup successful stored to: "+ s_backupFile);
			}
			else
			{
				s_messageBox = new MessageBox(s_shell, SWT.ICON_ERROR | SWT.OK);
				s_messageBox.setMessage(s_BackupFail);
				s_messageBox.setText(s_BackupTitle);
				s_messageBox.open();
				LOGGER.info("Backup to "+ s_backupFile + " failed!");
			}		
		}

		return backupSuccessful;	
	}
	
	/**
	 * Backup the user_data/ to the BackupTargetFilePath.
	 *
	 * @param BackupTargetFilePath where to store the Backup
	 * @return true if Backup was successful
	 */
	public boolean makeBackup(String BackupTargetFilePath)
	{
		return createZipFile(BackupTargetFilePath, USER_DATA_DIR);
	}
	
	/**
	 * Create the ZipFile zipFileName with filePath as content
	 *
	 * @param zipFileName Path to the zip-file
	 * @param filePath Path to the content
	 * @return true if zip-file creation was successful
	 */
	public boolean createZipFile(String zipFileName, String filePath)
	{
		boolean backupSuccessful = true;
		File startFile = new File(filePath);
		FileList fileList = new FileList();
		List<File> paths = fileList.getFileList(filePath);
		byte[] buf = new byte[1024];
		FileOutputStream fileOut;
		ZipOutputStream zipOut;

		/* The File or Directory named by filePath is the top-Entry of the 
		 * Zip-File. For the ZipFile-Entry (the Name of the File/Directory
		 * in the Zip-File) will allays the Path of the filePath-Parent
		 * removed from the path of the entry.
		 */        
		String pathSubstract = startFile.getParentFile().getAbsolutePath()+File.separator;
		int substractLength = pathSubstract.length();

		try 
		{
			fileOut = new FileOutputStream(zipFileName);
			zipOut = new ZipOutputStream(fileOut);
			zipOut.setLevel(9); // max Level of compression


			// Compress the files
			for (File f : paths) 
			{

				//File f = new File(paths.get(i));

				if(f.isFile())
				{
					FileInputStream in = new FileInputStream(f.getAbsolutePath());
					String nameInZip = f.getAbsolutePath().substring(substractLength);
					ZipEntry entry = new ZipEntry(nameInZip);
					entry.setTime(f.lastModified());
					zipOut.putNextEntry(entry);

					// Transfer bytes from the file to the ZIP file
					int len;
					while((len = in.read(buf)) > 0) {
						zipOut.write(buf, 0, len);
					}

					// Complete the entry
					zipOut.closeEntry();
					in.close();
				}
				else 
				{
					String nameInZip = f.getAbsolutePath().substring(pathSubstract.length());

					// Directories in Zip-files are marked with a '/' at 
					// the End of the Filename
					ZipEntry entry = new ZipEntry(nameInZip+"/");
					zipOut.putNextEntry(entry);
				}
			}
			// Complete the ZIP file
			zipOut.close();
			fileOut.close();
		} 
		catch (IOException e) 
		{
			backupSuccessful = false;
			LOGGER.error("IOException "+ e.getMessage());
		}

		return backupSuccessful;
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
		s_BackupFileDialogTitle = s_langProps.getProperty("ABACK.BACKUP_DIALOG_TITLE", "Select file to save the backup");
		s_BackupTitle  = s_langProps.getProperty("ABACK.BACKUP_TITLE", "AtaraxiS Backup");
		s_BackupOK  = s_langProps.getProperty("ABACK.BACKUP_OK", "Backup was successful.");
		s_BackupFail  = s_langProps.getProperty("ABACK.BACKUP_FAILED", "Backup has failed!");		
	} 
}
