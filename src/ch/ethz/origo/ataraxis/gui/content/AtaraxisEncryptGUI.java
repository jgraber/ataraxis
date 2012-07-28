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
package ch.ethz.origo.ataraxis.gui.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

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

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;
import ch.ethz.origo.ataraxis.crypt.CryptoMethodError;
import ch.ethz.origo.ataraxis.gui.GUIHelper;
import ch.ethz.origo.ataraxis.gui.utils.Browse;
import ch.ethz.origo.ataraxis.gui.utils.DropTargetAdapterSource;
import ch.ethz.origo.ataraxis.gui.utils.DropTargetAdapterTarget;

/**
 * AtaraxisEncryptGUI creates the encryption composite.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 */
public class AtaraxisEncryptGUI 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisEncryptGUI.class);

	// used AtaraxiS classes	
	private AtaraxisCrypter s_ac;
	private GUIHelper guiHelper;
	private ResourceBundle s_translations;

	private Text s_textEncryptSource;

	private Text s_textEncryptTarget;

	private Display s_display;
	private Control s_shell;

	private Button s_buttonEncryptDelete;

	private Button s_buttonEncryptCompr;
	
	// settings for shredder
	private static final int SHRED_ALGO_RANDOM = 7;
	private static int s_deleteDefault = SHRED_ALGO_RANDOM;
	
	
	// settings for encryption
	private static final int FILE = 1;
	private static final int FOLDER = 2;
	@SuppressWarnings("unused")
	private static int s_encryptFiletype = 0;
	
	private static final int ENCRYPT_SOURCE = 1;
	private static final int ENCRYPT_TARGET = 2;
	
	//private static final String SUFFIX_FOLDER = ".acz";
	private static final String SUFFIX_ZIP = ".zip";
	
	// Paths and files
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	
	// Images
	private Image ICON_FOLDER;
	private Image ICON_FILE;
	private Image ICON_BROWSE;
	private Image ICON_ENCRYPT;

	private Button buttonEncrypt;

	private Composite s_compositeEncrypt;
	
	
	
	/**
	 * AtaraxisConfigGUI manages all to create a composite
	 * for the encryption GUI.
	 * 
	 * @param translations with the translations
	 */
	public AtaraxisEncryptGUI(ResourceBundle translations, AtaraxisCrypter ac, int deleteDefault)
	{
		s_translations = translations;
		s_ac = ac;
		s_deleteDefault = deleteDefault;
	}
	
	/**
	 * Crate the Composite for the encryption GUI.
	 * 
	 * @param s_shell shell of outside SWT application
	 * @param display display of the outside SWT application
	 * @param parentComposite where the PasswordContent should be placed
	 * @return the composite
	 */
	public Composite createEncryptContent(final Shell shell, final Display display, Composite parentComposite) 
	{
		guiHelper = new GUIHelper(shell);
		s_shell = shell;
		s_display = display;
		setIcons();
		
		s_compositeEncrypt = new Composite(parentComposite, SWT.NONE);
		final GridLayout gridLayoutEncrypt = new GridLayout();
		gridLayoutEncrypt.numColumns = 3;
		s_compositeEncrypt.setLayout(gridLayoutEncrypt);
		final GridData gridDataEncrypt = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridDataEncrypt.widthHint = GUIHelper.COMPOSITE_WIDTH;
		gridDataEncrypt.heightHint = GUIHelper.COMPOSITE_HEIGHT;
		s_compositeEncrypt.setLayoutData(gridDataEncrypt);


		// ################## Composite Encrypt, Group Source ##################

		final Group groupEncryptSource = new Group(s_compositeEncrypt, SWT.NONE);
		groupEncryptSource.setText(s_translations.getString("SOURCE"));
		final GridData gridDataEncryptSource = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataEncryptSource.heightHint = GUIHelper.GROUP_HEIGHT;
		gridDataEncryptSource.widthHint = GUIHelper.GROUP_WIDTH;
		groupEncryptSource.setLayoutData(gridDataEncryptSource);
		final GridLayout gridLayoutEncryptSource = new GridLayout();
		gridLayoutEncryptSource.numColumns = 3;
		groupEncryptSource.setLayout(gridLayoutEncryptSource);

		s_textEncryptSource = new Text(groupEncryptSource, SWT.BORDER);
		final GridData gridDataEncryptSource_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gridDataEncryptSource_1.widthHint = GUIHelper.TEXT_WIDTH;
		s_textEncryptSource.setLayoutData(gridDataEncryptSource_1);


		final DropTarget dtEncryptSource = new DropTarget(groupEncryptSource, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtEncryptSource.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		



		final Button buttonEncryptSourceFile = new Button(groupEncryptSource, SWT.RADIO);
		final GridData gridDataEncryptSource_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptSource_2.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataEncryptSource_2.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonEncryptSourceFile.setLayoutData(gridDataEncryptSource_2);
		buttonEncryptSourceFile.setImage(ICON_FILE);
		buttonEncryptSourceFile.setToolTipText(s_translations.getString("FILE"));
		buttonEncryptSourceFile.setSelection(true);
		buttonEncryptSourceFile.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonEncryptSourceFile.getSelection())
				{
					LOGGER.debug("buttonEncryptSourceFile is selected");	
					s_encryptFiletype = FILE;
				}
			}
		});

		final Button buttonEncryptSourceFolder = new Button(groupEncryptSource, SWT.RADIO);
		final GridData gridDataEncryptSource_3 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptSource_3.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataEncryptSource_3.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonEncryptSourceFolder.setLayoutData(gridDataEncryptSource_3);
		buttonEncryptSourceFolder.setImage(ICON_FOLDER);
		buttonEncryptSourceFolder.setToolTipText(s_translations.getString("FOLDER"));
		buttonEncryptSourceFolder.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonEncryptSourceFolder.getSelection())
				{
					LOGGER.debug("buttonEncryptSourceFolder is selected");	
					s_encryptFiletype = FOLDER;
				}
			}
		});


		final Button buttonEncryptSource = new Button(groupEncryptSource, SWT.NONE);
		final GridData gridDataEncryptSource_4 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptSource_4.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataEncryptSource_4.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonEncryptSource.setLayoutData(gridDataEncryptSource_4);
		buttonEncryptSource.setImage(ICON_BROWSE);
		buttonEncryptSource.setToolTipText(s_translations.getString("BROWSE"));
		


		// ################### Composite Encrypt, Group Target ##################

		final Group groupEncryptTarget = new Group(s_compositeEncrypt, SWT.NONE);
		final GridData gridDataEncryptTarget = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataEncryptTarget.heightHint = GUIHelper.GROUP_HEIGHT;
		gridDataEncryptTarget.widthHint = GUIHelper.GROUP_WIDTH;
		groupEncryptTarget.setLayoutData(gridDataEncryptTarget);
		final GridLayout gridLayoutEncryptTarget = new GridLayout();
		gridLayoutEncryptTarget.numColumns = 2;
		groupEncryptTarget.setLayout(gridLayoutEncryptTarget);
		groupEncryptTarget.setText(s_translations.getString("TARGET"));

		s_textEncryptTarget = new Text(groupEncryptTarget, SWT.BORDER);
		final GridData gridDataEncryptTarget_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gridDataEncryptTarget_1.widthHint = GUIHelper.TEXT_WIDTH;
		s_textEncryptTarget.setLayoutData(gridDataEncryptTarget_1);

		final DropTarget dtEncryptTarget = new DropTarget(groupEncryptTarget, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtEncryptTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtEncryptTarget.addDropListener(new DropTargetAdapterTarget(s_textEncryptTarget, s_textEncryptSource, true));

		final SashForm sashFormEncryptTarget = new SashForm(groupEncryptTarget, SWT.NONE);
		sashFormEncryptTarget.setLayoutData(new GridData(314, SWT.DEFAULT));

		final Button buttonEncryptTargetFile = new Button(groupEncryptTarget, SWT.NONE);
		final GridData gridDataEncryptTarget_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptTarget_2.heightHint = GUIHelper.BUTTON_HEIGHT; // BUTTON_HEIGHT
		gridDataEncryptTarget_2.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonEncryptTargetFile.setLayoutData(gridDataEncryptTarget_2);
		buttonEncryptTargetFile.setImage(ICON_BROWSE);
		buttonEncryptTargetFile.setToolTipText(s_translations.getString("BROWSE"));
		buttonEncryptTargetFile.addSelectionListener(new Browse(ENCRYPT_TARGET, s_textEncryptTarget, guiHelper, s_translations));
		new Label(s_compositeEncrypt, SWT.NONE);

		buttonEncryptSource.addSelectionListener(new Browse(ENCRYPT_SOURCE, s_textEncryptSource, s_textEncryptTarget, guiHelper, s_translations));
		dtEncryptSource.addDropListener(new DropTargetAdapterSource(s_textEncryptSource, s_textEncryptTarget, true));
		// ##################### Composite Encrypt, Buttons ####################

		final SashForm sashFormEncrypt = new SashForm(s_compositeEncrypt, SWT.NONE);
		final GridData gridData_2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData_2.widthHint = 5;
		gridData_2.heightHint = 50;
		sashFormEncrypt.setLayoutData(gridData_2);
		new Label(s_compositeEncrypt, SWT.NONE);


		s_buttonEncryptCompr = new Button(s_compositeEncrypt, SWT.CHECK);
		s_buttonEncryptCompr.setLayoutData(new GridData(255, 35));
		s_buttonEncryptCompr.setText(s_translations.getString("BUTTON.COMPRESS"));
		s_buttonEncryptCompr.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(!s_buttonEncryptCompr.getSelection())
				{
					if((new File(s_textEncryptSource.getText()).isDirectory()))
					{
						s_buttonEncryptCompr.setSelection(true);
						guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.NOT.ALLOWED.TITLE"), s_translations.getString("MESSAGE.NOT.COMPRESS.FOLDER"));
					}	
				}
				else if (s_textEncryptSource.getText().endsWith(SUFFIX_ZIP))
				{
					s_buttonEncryptCompr.setSelection(false);
					guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.NOT.ALLOWED.TITLE"), s_translations.getString("MESSAGE.COMPRESS.ZIP"));
				}
			}
		});



		buttonEncrypt = new Button(s_compositeEncrypt, SWT.NONE);
		final GridData gridDataEncryptButtons = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 2);
		gridDataEncryptButtons.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataEncryptButtons.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonEncrypt.setLayoutData(gridDataEncryptButtons);
		buttonEncrypt.setImage(ICON_ENCRYPT);
		buttonEncrypt.setToolTipText(s_translations.getString("ENCRYPT"));
		buttonEncrypt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String source = s_textEncryptSource.getText();
				String target = s_textEncryptTarget.getText();

				if (source.equals("") || target.equals(""))
				{
					guiHelper.displayWarningMessage(s_translations.getString("MESSAGE.EMPTY.FIELD.TITLE"), s_translations.getString("MESSAGE.EMPTY.FIELDS"));
				}
				else if (guiHelper.fileExists(source, s_translations) && guiHelper.createTargetFile(target, s_translations))
				{
					s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));
					s_compositeEncrypt.setEnabled(false);
					
					new EncryptThread(s_display, source, target, s_buttonEncryptCompr.getSelection(),s_buttonEncryptDelete.getSelection()).start();
				}
			}
			
		});

		new Label(s_compositeEncrypt, SWT.NONE);

		s_buttonEncryptDelete = new Button(s_compositeEncrypt, SWT.CHECK);
		//final GridData gridData = new GridData(100, SWT.DEFAULT);
		//s_buttonEncryptDelete.setLayoutData(gridData);
		s_buttonEncryptDelete.setText(s_translations.getString("BUTTON.DELETE.SRC"));
		new Label(s_compositeEncrypt, SWT.NONE);
		new Label(s_compositeEncrypt, SWT.NONE);
		new Label(s_compositeEncrypt, SWT.NONE);

		final Label l_rLabel = new Label(s_compositeEncrypt, SWT.NONE);
		l_rLabel.setAlignment(SWT.RIGHT);
		final GridData gridData_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridData_1.heightHint = 15;
		gridData_1.widthHint = 20;
		l_rLabel.setLayoutData(gridData_1);

		return s_compositeEncrypt;
	}
	
	/**
	 * Method to set the icons.
	 */
	private void setIcons()
	{
		ICON_FOLDER = new Image(s_display, ICON_DIR + "/folder.png");
		ICON_FILE = new Image(s_display, ICON_DIR + "/file.png");
		ICON_BROWSE = new Image(s_display, ICON_DIR + "/browse.png");
		ICON_ENCRYPT = new Image(s_display, ICON_DIR + "/Menu_Encrypt.png");
	}
	
	/**
	 * Worker class to encrypt files asynchronous.
	 * 
	 * @author Johnny Graber
	 */
	class EncryptThread extends Thread 
	{
		private Display display;
		private String sourcePath;
		private String targetPath;
		private boolean zipTarget;
		private boolean deleteSource;
		
		public EncryptThread(Display sDisplay, String sourcePath, String targetPath, boolean zipTarget, boolean deleteSource) 
		{
		    this.display = sDisplay;
		    this.sourcePath = sourcePath;
		    this.targetPath = targetPath;
		    this.zipTarget = zipTarget;
		    this.deleteSource = deleteSource;
		    
		    LOGGER.debug("EncryptThread created");
		}

		public void run() 
		{
			LOGGER.debug("start thread with run()");
			try
			{
				s_ac.encryptFile(new File(sourcePath), new File(targetPath), zipTarget);

				if (deleteSource)
				{
					try {
						AtaraxisShrederGUI.shredFile(sourcePath, s_deleteDefault, false);
					} 
					catch (IOException e1) {
						LOGGER.error("Error during encryption!", e1);
						display.asyncExec(new Runnable() {
							public void run() {
								guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.DELETION.CONFLICT.TITLE"), s_translations.getString("MESSAGE.DELETION.CONFILCT"));        
							}
						});						
					}
				}
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.ENCRYPT.TITLE"), s_translations.getString("MESSAGE.ENCRYPT"));        
					}
				});
			}
			catch (FileNotFoundException fnfe)
			{
				// should never happen, because source file must exist,
				// maybe it can happen if directory is write protected
				LOGGER.debug("Error during encryption!", fnfe);
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.FILE.CONFLICT.TITLE"), s_translations.getString("MESSAGE.FILE.PERMISSION"));        
					}
				});				
			}
			catch (CryptoMethodError cme)
			{
				LOGGER.error("Error during encryption!", cme);
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.CRYPTO.ERROR.TITLE"), s_translations.getString("MESSAGE.CRYPTO.ERROR"));        
					}
				});				
			}
			
			display.asyncExec(new Runnable() {
		        public void run() {
		          if (buttonEncrypt.isDisposed() || s_compositeEncrypt.isDisposed() || s_shell.isDisposed())
		          {
		        	  return;
		          }
		          buttonEncrypt.setEnabled(true);
		          s_compositeEncrypt.setEnabled(true);
		          s_shell.setCursor(null);
		        }
		    });
		}
	}
}
