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

package ataraxis.gui.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.zip.ZipException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.crypt.CryptoMethodError;
import ataraxis.gui.GUIHelper;
import ataraxis.gui.utils.Browse;
import ataraxis.gui.utils.DropTargetAdapterSource;
import ataraxis.gui.utils.DropTargetAdapterTarget;


/**
 * AtaraxisDecryptGUI creates the decryption composite.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 */

public class AtaraxisDecryptGUI 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisDecryptGUI.class);

	// used AtaraxiS classes	
	private AtaraxisCrypter s_ac;
	private GUIHelper guiHelper;
	private ResourceBundle s_translations;

	private int s_deleteDefault;
	
	private Display s_display;
	private Control s_shell;

	private Text s_textDecryptSource;
	private Text s_textDecryptTarget;
	private Button s_buttonDecryptCompr;
	private Button s_buttonDecryptDelete;	

	private static final int DECRYPT_SOURCE = 3;
	private static final int DECRYPT_TARGET = 4;
	
	private static final String SUFFIX_FOLDER = ".acz";
	private static final String SUFFIX_ZIP = ".zip";

	// Paths and files
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	
	// Images
	private Image ICON_BROWSE;
	private Image ICON_DECRYPT;

	private Composite s_compositeDecrypt;

	private Button buttonDecrypt;
	
	/**
	 * AtaraxisConfigGUI manages all to create a composite
	 * for the decryption GUI.
	 * 
	 * @param translations with the translations
	 */
	public AtaraxisDecryptGUI(ResourceBundle translations, AtaraxisCrypter ac, int deleteDefault)
	{
		s_translations = translations;
		s_ac = ac;
		s_deleteDefault = deleteDefault;
	}
	
	/**
	 * Crate the Composite for the decryption GUI.
	 * 
	 * @param s_shell shell of outside SWT application
	 * @param display display of the outside SWT application
	 * @param parentComposite where the PasswordContent should be placed
	 * @return the composite
	 */
	public Composite createDecryptContent(final Shell shell, final Display display, Composite parentComposite) 
	{
		guiHelper = new GUIHelper(shell);
		s_shell = shell;
		s_display = display;
		setIcons();
		
		s_compositeDecrypt = new Composite(parentComposite, SWT.NONE);
		final GridLayout gridLayoutDecrypt = new GridLayout();
		gridLayoutDecrypt.numColumns = 3;
		s_compositeDecrypt.setLayout(gridLayoutDecrypt);
		final GridData gridDataDecrypt = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridDataDecrypt.widthHint = GUIHelper.COMPOSITE_WIDTH;
		gridDataDecrypt.heightHint = GUIHelper.COMPOSITE_HEIGHT;
		s_compositeDecrypt.setLayoutData(gridDataDecrypt);


		// ################## Composite Decrypt, Group Source ##################

		final Group groupDecryptSource = new Group(s_compositeDecrypt, SWT.NONE);
		groupDecryptSource.setText(s_translations.getString("SOURCE"));
		final GridData gridDataDecryptSource = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataDecryptSource.heightHint = GUIHelper.GROUP_HEIGHT;
		gridDataDecryptSource.widthHint = GUIHelper.GROUP_WIDTH;
		groupDecryptSource.setLayoutData(gridDataDecryptSource);
		final GridLayout gridLayoutDecryptSource = new GridLayout();
		gridLayoutDecryptSource.numColumns = 3;
		groupDecryptSource.setLayout(gridLayoutDecryptSource);

		s_textDecryptSource = new Text(groupDecryptSource, SWT.BORDER);
		final GridData gridDataDecryptSource_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gridDataDecryptSource_1.widthHint = GUIHelper.TEXT_WIDTH;
		s_textDecryptSource.setLayoutData(gridDataDecryptSource_1);

		final DropTarget dtDecryptSource = new DropTarget(groupDecryptSource, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtDecryptSource.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		

		final SashForm sashFormDecryptSource = new SashForm(groupDecryptSource, SWT.NONE);
		sashFormDecryptSource.setLayoutData(new GridData(185, SWT.DEFAULT));

		new Label(groupDecryptSource, SWT.NONE);

		final Button buttonDecryptSourceFile = new Button(groupDecryptSource, SWT.NONE);
		final GridData gridDataDecryptSource_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataDecryptSource_2.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataDecryptSource_2.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonDecryptSourceFile.setLayoutData(gridDataDecryptSource_2);
		buttonDecryptSourceFile.setImage(ICON_BROWSE);
		buttonDecryptSourceFile.setToolTipText(s_translations.getString("BROWSE"));
		


		// ################### Composite Decrypt, Group Target ##################

		final Group groupDecryptTarget = new Group(s_compositeDecrypt, SWT.NONE);
		final GridData gridDataDecryptTarget = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataDecryptTarget.heightHint = GUIHelper.GROUP_HEIGHT;
		gridDataDecryptTarget.widthHint = GUIHelper.GROUP_WIDTH;
		groupDecryptTarget.setLayoutData(gridDataDecryptTarget);
		final GridLayout gridLayoutDecryptTarget = new GridLayout();
		gridLayoutDecryptTarget.numColumns = 2;
		groupDecryptTarget.setLayout(gridLayoutDecryptTarget);
		groupDecryptTarget.setText(s_translations.getString("TARGET"));

		s_textDecryptTarget = new Text(groupDecryptTarget, SWT.BORDER);
		final GridData gridDataDecryptTarget_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gridDataDecryptTarget_1.widthHint = GUIHelper.TEXT_WIDTH;
		s_textDecryptTarget.setLayoutData(gridDataDecryptTarget_1);

		final DropTarget dtDecryptTarget = new DropTarget(groupDecryptTarget, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtDecryptTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtDecryptTarget.addDropListener(new DropTargetAdapterTarget(s_textDecryptTarget, s_textDecryptSource, false));

		final SashForm sashFormDecryptTarget = new SashForm(groupDecryptTarget, SWT.NONE);
		sashFormDecryptTarget.setLayoutData(new GridData(314, SWT.DEFAULT));

		final Button buttonDecryptTargetFile = new Button(groupDecryptTarget, SWT.NONE);
		final GridData gridDataDecryptTarget_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataDecryptTarget_2.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataDecryptTarget_2.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonDecryptTargetFile.setLayoutData(gridDataDecryptTarget_2);
		buttonDecryptTargetFile.setImage(ICON_BROWSE);
		buttonDecryptTargetFile.setToolTipText(s_translations.getString("BROWSE"));
		buttonDecryptTargetFile.addSelectionListener(new Browse(DECRYPT_TARGET, s_textDecryptTarget, guiHelper, s_translations));
		new Label(s_compositeDecrypt, SWT.NONE);

		// ##################### Composite Decrypt, Buttons ####################

		final SashForm sashFormDecrypt = new SashForm(s_compositeDecrypt, SWT.NONE);
		final GridData gridDataDecryptButtonSash = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridDataDecryptButtonSash.widthHint = 5;
		gridDataDecryptButtonSash.heightHint = 50;
		sashFormDecrypt.setLayoutData(gridDataDecryptButtonSash);
		new Label(s_compositeDecrypt, SWT.NONE);	
		
		s_buttonDecryptCompr = new Button(s_compositeDecrypt, SWT.CHECK);
		s_buttonDecryptCompr.setLayoutData(new GridData(255, 35));
		s_buttonDecryptCompr.setText(s_translations.getString("BUTTON.DECOMPRESS"));
		s_buttonDecryptCompr.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(s_buttonDecryptCompr.getSelection())
				{
					final String fileName = s_textDecryptSource.getText();
					if (!fileName.trim().equals(""))
					{
						s_textDecryptTarget.setText((new File(fileName)).getParent());
					}
				}
				else
				{
					final String fileName = s_textDecryptTarget.getText();
					if (!fileName.trim().equals("") && (new File(fileName).isDirectory()))
					{
						final String sourceName = s_textDecryptSource.getText();
						if(sourceName.endsWith(SUFFIX_FOLDER))
							s_textDecryptTarget.setText(sourceName.substring(0, sourceName.length() - SUFFIX_FOLDER.length()) + SUFFIX_ZIP);
					}
				}
			}
		});		
		
		buttonDecrypt = new Button(s_compositeDecrypt, SWT.NONE);
		final GridData gridDataDecryptButtons = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 2);
		gridDataDecryptButtons.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataDecryptButtons.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonDecrypt.setLayoutData(gridDataDecryptButtons);
		buttonDecrypt.setImage(ICON_DECRYPT);
		buttonDecrypt.setToolTipText(s_translations.getString("DECRYPT"));
		buttonDecrypt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String source = s_textDecryptSource.getText();
				String target = s_textDecryptTarget.getText();

				if (source.equals("") || target.equals(""))
				{
					guiHelper.displayWarningMessage(s_translations.getString("MESSAGE.EMPTY.FIELD.TITLE"), s_translations.getString("MESSAGE.EMPTY.FIELDS"));
				}
				else if (guiHelper.fileExists(source,s_translations) && guiHelper.createTargetFile(target,s_translations))
				{
					s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));
					s_compositeDecrypt.setEnabled(false);
					
					new DecryptThread(s_display, source, target, s_buttonDecryptCompr.getSelection(),s_buttonDecryptDelete.getSelection()).start();
				}
			}
		});
		new Label(s_compositeDecrypt, SWT.NONE);

		buttonDecryptSourceFile.addSelectionListener(new Browse(DECRYPT_SOURCE, false, s_textDecryptSource,s_textDecryptTarget, guiHelper, s_translations));
		dtDecryptSource.addDropListener(new DropTargetAdapterSource(s_textDecryptSource, s_textDecryptTarget, false));		
		s_buttonDecryptDelete = new Button(s_compositeDecrypt, SWT.CHECK);
		s_buttonDecryptDelete.setText(s_translations.getString("BUTTON.DELETE.SRC"));

		new Label(s_compositeDecrypt, SWT.NONE);	
		new Label(s_compositeDecrypt, SWT.NONE);
		new Label(s_compositeDecrypt, SWT.NONE);

		final Label l_rLabel = new Label(s_compositeDecrypt, SWT.NONE);
		l_rLabel.setAlignment(SWT.RIGHT);
		final GridData gridData_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridData_1.heightHint = 15;
		gridData_1.widthHint = 20;
		l_rLabel.setLayoutData(gridData_1);
		
		return s_compositeDecrypt;
	}
	
	/**
	 * Method to set the icons.
	 */
	private void setIcons()
	{
		ICON_BROWSE = new Image(s_display, ICON_DIR + "/browse.png");
		ICON_DECRYPT = new Image(s_display, ICON_DIR + "/Menu_Decrypt.png");
	}
	
	/**
	 * Worker class to decrypt files asynchronous.
	 * 
	 * @author Johnny Graber
	 */
	class DecryptThread extends Thread 
	{
		private Display display;
		private String sourcePath;
		private String targetPath;
		private boolean unzipTarget;
		private boolean deleteSource;
		
		public DecryptThread(Display sDisplay, String sourcePath, String targetPath, boolean unzipTarget, boolean deleteSource) 
		{
		    this.display = sDisplay;
		    this.sourcePath = sourcePath;
		    this.targetPath = targetPath;
		    this.unzipTarget = unzipTarget;
		    this.deleteSource = deleteSource;
		    
		    LOGGER.debug("DecryptThread created");
		}

		public void run() 
		{
			LOGGER.debug("start thread with run()");
			try
			{				
				s_ac.decryptFile(new File(sourcePath), new File(targetPath), unzipTarget);

				if (deleteSource)
				{
					try {
						AtaraxisShrederGUI.shredFile(sourcePath, s_deleteDefault, false);
					} catch (IOException e1) {
						LOGGER.error("Error during decryption!", e1);
						display.asyncExec(new Runnable() {
							public void run() {
								guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.DELETION.CONFLICT.TITLE"), s_translations.getString("MESSAGE.DELETION.CONFILCT"));        
							}
						});
					}
				}
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.DECRYPT.TITLE"), s_translations.getString("MESSAGE.DECRYPT"));        
					}
				});
				
			}
			catch (FileNotFoundException fnfe)
			{
				LOGGER.error("Error during decryption!", fnfe);
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.FILE.CONFLICT.TITLE"), s_translations.getString("MESSAGE.FILE.CONFILCT"));        
					}
				});
			}
			catch (CryptoMethodError cme)
			{
				LOGGER.error("Error during decryption!", cme);
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.CRYPTO.ERROR.TITLE"), s_translations.getString("MESSAGE.CRYPTO.ERROR"));        
					}
				});				
			}
			catch (ZipException ze)
			{
				LOGGER.error("Error during decryption!", ze);
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.FILE.CONFLICT.TITLE"), s_translations.getString("MESSAGE.CANT.UNZIP"));
					}
				});
			}
		    
		    display.asyncExec(new Runnable() {
		        public void run() {
		          if (buttonDecrypt.isDisposed() || s_compositeDecrypt.isDisposed() || s_shell.isDisposed())
		          {
		        	  return;
		          }
		          buttonDecrypt.setEnabled(true);
		          s_compositeDecrypt.setEnabled(true);
		          s_shell.setCursor(null);
		        }
		    });
		}
	}
}
