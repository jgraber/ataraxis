package ataraxis.gui.content;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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

import ataraxis.gui.GUIHelper;
import ataraxis.gui.utils.Browse;
import ataraxis.gui.utils.DropTargetAdapterSource;
import ataraxis.misc.AtaraxisShredder;


public class AtaraxisShrederGUI 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisEncryptGUI.class);

	// used AtaraxiS classes	
	private GUIHelper guiHelper;
	private ResourceBundle s_translations;
	private static AtaraxisShredder s_shredder = new AtaraxisShredder();
	
	
	private Display s_display;
	private Control s_shell;

	// Paths and files
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	
	// Images
	private Image ICON_FOLDER;
	private Image ICON_FILE;
	private Image ICON_BROWSE;
	private Image ICON_SHRED;
	

	// settings for shred
	private static boolean opearateOnFolder = false;
	private int s_deleteDefault;
	private Text s_textShredSource;
	private static SelectionAdapter openDialogAdapter;

	private Label s_labelDeletion;
	private Button buttonShred;

	private Composite s_compositeShred;
	

	private static final int SHRED_ALGO_ZEROES = 0;
	private static final int SHRED_ALGO_DOD = 1;
	private static final int SHRED_ALGO_DOD_EXT = 2;
	private static final int SHRED_ALGO_VSITR = 3;
	private static final int SHRED_ALGO_SCHNEIER = 4;
	private static final int SHRED_ALGO_GUTMANN_FLOPPY = 5;
	private static final int SHRED_ALGO_GUTMANN = 6;
	private static final int SHRED_ALGO_RANDOM = 7;
	private static final int SHRED_ALGO_DEFAULT = SHRED_ALGO_RANDOM;


	/**
	 * AtaraxisConfigGUI manages all to create a composite
	 * for the configuration GUI.
	 * 
	 * @param translations with the translations
	 */
	public AtaraxisShrederGUI(ResourceBundle translations, int deleteDefault)
	{
		s_translations = translations;
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
	public Composite createShredContent(final Shell shell, final Display display, Composite parentComposite) 
	{
		guiHelper = new GUIHelper(shell);
		s_shell = shell;
		s_display = display;
		setIcons();
		
		s_compositeShred = new Composite(parentComposite, SWT.NONE);
		final GridLayout gridLayoutShred = new GridLayout();
		gridLayoutShred.numColumns = 3;
		s_compositeShred.setLayout(gridLayoutShred);
		final GridData gridDataShred = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridDataShred.widthHint = GUIHelper.COMPOSITE_WIDTH;
		gridDataShred.heightHint = GUIHelper.COMPOSITE_HEIGHT;
		s_compositeShred.setLayoutData(gridDataShred);

		// ################### Composite Shred, Group Source ##################
		final Group groupShred = new Group(s_compositeShred, SWT.NONE);
		groupShred.setText(s_translations.getString("SOURCE"));
		final GridData gridDataShredSource = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataShredSource.heightHint = GUIHelper.GROUP_HEIGHT;
		gridDataShredSource.widthHint = GUIHelper.GROUP_WIDTH;
		groupShred.setLayoutData(gridDataShredSource);
		final GridLayout gridLayoutShredSource = new GridLayout();
		gridLayoutShredSource.numColumns = 3;
		groupShred.setLayout(gridLayoutShredSource);

		s_textShredSource = new Text(groupShred, SWT.BORDER);
		final GridData gridDataShred_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gridDataShred_1.widthHint = GUIHelper.TEXT_WIDTH;
		s_textShredSource.setLayoutData(gridDataShred_1);

		final DropTarget dtShred = new DropTarget(s_compositeShred, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtShred.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtShred.addDropListener(new DropTargetAdapterSource(s_textShredSource, null, false));

		final Button buttonShredFile = new Button(groupShred, SWT.RADIO);
		final GridData gridDataShred_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataShred_2.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataShred_2.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonShredFile.setLayoutData(gridDataShred_2);
		buttonShredFile.setImage(ICON_FILE);
		buttonShredFile.setToolTipText(s_translations.getString("FILE"));
		buttonShredFile.setSelection(true);
		

		final Button buttonShredFolder = new Button(groupShred, SWT.RADIO);
		final GridData gridDataShred_3 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataShred_3.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataShred_3.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonShredFolder.setLayoutData(gridDataShred_3);
		buttonShredFolder.setImage(ICON_FOLDER);
		buttonShredFolder.setToolTipText(s_translations.getString("FOLDER"));
		

		final Button buttonShredBrowse = new Button(groupShred, SWT.NONE);
		final GridData gridDataShred_4 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataShred_4.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridDataShred_4.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonShredBrowse.setLayoutData(gridDataShred_4);
		buttonShredBrowse.setImage(ICON_BROWSE);
		buttonShredBrowse.setToolTipText(s_translations.getString("BROWSE"));
		
		
		
		openDialogAdapter = new Browse(GUIHelper.SHRED_SOURCE,opearateOnFolder, s_textShredSource, null,guiHelper, s_translations);
		buttonShredBrowse.addSelectionListener(openDialogAdapter);
		
		buttonShredFolder.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonShredFolder.getSelection())
				{
					LOGGER.debug("buttonShredFolder is selected");	
					opearateOnFolder = true;
					
					buttonShredBrowse.removeSelectionListener(openDialogAdapter);
					openDialogAdapter = new Browse(GUIHelper.SHRED_SOURCE,opearateOnFolder, s_textShredSource, null,guiHelper, s_translations);
					buttonShredBrowse.addSelectionListener(openDialogAdapter);
				}
			}
		});
		
		buttonShredFile.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonShredFile.getSelection())
				{
					LOGGER.debug("buttonShredFile is selected");	
					opearateOnFolder = false;
					
					buttonShredBrowse.removeSelectionListener(openDialogAdapter);
					openDialogAdapter = new Browse(GUIHelper.SHRED_SOURCE,opearateOnFolder, s_textShredSource, null,guiHelper, s_translations);
					buttonShredBrowse.addSelectionListener(openDialogAdapter);
				}
			}
		});
		
		
		new Label(s_compositeShred, SWT.NONE);
		new Label(s_compositeShred, SWT.NONE);


		//		 #################### Composite Shred, Group Options ###################

		final Group groupShredOptions = new Group(s_compositeShred, SWT.NONE);
		groupShredOptions.setText(s_translations.getString("SHRED.OPTIONS"));
		final GridData gridDataShredOptions = new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1);
		gridDataShredOptions.heightHint = GUIHelper.GROUP_HEIGHT + 20;
		gridDataShredOptions.widthHint = GUIHelper.GROUP_WIDTH;
		groupShredOptions.setLayoutData(gridDataShredOptions);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		groupShredOptions.setLayout(gridLayout);

		final Button shredZeroes = new Button(groupShredOptions, SWT.RADIO);
		//shredZeroes.setLayoutData(new GridData(143, SWT.DEFAULT)); 
		/* removed, because with the code it means width=146, height=SWT.DEFAULT 
			==> with the edit it fix the sizing-Problem in Linux
		 */
		shredZeroes.setText(s_translations.getString("SHRED.ZEROES"));
		shredZeroes.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 1x");

		final Button shredSchneier = new Button(groupShredOptions, SWT.RADIO);
		//shredSchneier.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredSchneier.setText(s_translations.getString("SHRED.SCHNEIER"));
		shredSchneier.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 7x");

		final Button shredDoD = new Button(groupShredOptions, SWT.RADIO);
		//shredDoD.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredDoD.setText(s_translations.getString("SHRED.DOD"));
		shredDoD.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 3x");

		final Button shredGutmannFloppy = new Button(groupShredOptions, SWT.RADIO);
		//shredGutmannFloppy.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredGutmannFloppy.setText(s_translations.getString("SHRED.GUTMANN.FLOPPY"));
		shredGutmannFloppy.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 18x");

		final Button shredDoDext = new Button(groupShredOptions, SWT.RADIO);
		//shredDoDext.setLayoutData(new GridData(SWT.DEFAULT));
		shredDoDext.setText(s_translations.getString("SHRED.DODEXT"));
		shredDoDext.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 7x");

		final Button shredGutmann = new Button(groupShredOptions, SWT.RADIO);
		//shredGutmann.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredGutmann.setText(s_translations.getString("SHRED.GUTMANN"));
		shredGutmann.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 35x");

		final Button shredVSITR = new Button(groupShredOptions, SWT.RADIO);
		//shredVSITR.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredVSITR.setText(s_translations.getString("SHRED.VSITR"));
		shredVSITR.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 7x");

		final Button shredRandom = new Button(groupShredOptions, SWT.RADIO);
		//shredRandom.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredRandom.setText(s_translations.getString("SHRED.RANDOM"));
		shredRandom.setToolTipText(s_translations.getString("SHRED.REPETITIONS") + ": 10x");


		switch(s_deleteDefault)
		{
		case SHRED_ALGO_ZEROES: shredZeroes.setSelection(true);
		break;
		case SHRED_ALGO_DOD: shredDoD.setSelection(true);
		break;
		case SHRED_ALGO_DOD_EXT: shredDoDext.setSelection(true);
		break;
		case SHRED_ALGO_VSITR: shredVSITR.setSelection(true);
		break;
		case SHRED_ALGO_SCHNEIER: shredSchneier.setSelection(true);
		break;
		case SHRED_ALGO_GUTMANN_FLOPPY: shredGutmannFloppy.setSelection(true);
		break;
		case SHRED_ALGO_GUTMANN: shredGutmann.setSelection(true);
		break;
		default: shredRandom.setSelection(true);
		}



		// ###################### Composite Shred, Buttons #####################

		final Button buttonShredDeleteRoot = new Button(s_compositeShred, SWT.LEFT | SWT.CHECK);
		buttonShredDeleteRoot.setText(s_translations.getString("SHRED.LEAVE.DIR"));
		buttonShredDeleteRoot.setLayoutData(new GridData(260, 55));

		buttonShred = new Button(s_compositeShred, SWT.NONE);
		final GridData gridData = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 2);
		gridData.heightHint = GUIHelper.BUTTON_HEIGHT;
		gridData.widthHint = GUIHelper.BUTTON_WIDTH;
		buttonShred.setLayoutData(gridData);
		buttonShred.setImage(ICON_SHRED);
		buttonShred.setToolTipText(s_translations.getString("SHRED"));
		buttonShred.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int shredAlgo = SHRED_ALGO_DEFAULT;
				final String filePath = s_textShredSource.getText();
				boolean leaveRoot = false;

				if(buttonShredDeleteRoot.getSelection())
					leaveRoot = true;
				if (shredZeroes.getSelection())
					shredAlgo = 0;
				else if (shredDoD.getSelection())
					shredAlgo = 1;
				else if (shredDoDext.getSelection())
					shredAlgo = 2;
				else if (shredVSITR.getSelection())
					shredAlgo = 3;
				else if (shredSchneier.getSelection())
					shredAlgo = 4;
				else if (shredGutmannFloppy.getSelection())
					shredAlgo = 5;
				else if (shredGutmann.getSelection())
					shredAlgo = 6;
				else if (shredRandom.getSelection())
					shredAlgo = 7;

				s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));
				shred(filePath, shredAlgo, leaveRoot);
				LOGGER.debug("Button event for shread finished.");
				//s_shell.setCursor(null);
			}
		});
		new Label(s_compositeShred, SWT.NONE);

		s_labelDeletion = new Label(s_compositeShred, SWT.LEFT);
		final GridData gridDataShredDeletion = new GridData(SWT.FILL, SWT.CENTER, false, true);
		gridDataShredDeletion.heightHint = 34;
		gridDataShredDeletion.widthHint = 260;
		s_labelDeletion.setLayoutData(gridDataShredDeletion);
		new Label(s_compositeShred, SWT.NONE);

		final Label l_uLabel = new Label(s_compositeShred, SWT.NONE);
		final GridData gridData_2 = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData_2.heightHint = 10;
		l_uLabel.setLayoutData(gridData_2);
		new Label(s_compositeShred, SWT.NONE);

		final Label l_rLabel = new Label(s_compositeShred, SWT.NONE);
		final GridData gridData_1 = new GridData(SWT.RIGHT, SWT.FILL, false, false);
		gridData_1.heightHint = 13;
		gridData_1.widthHint = 20;
		l_rLabel.setLayoutData(gridData_1);
		
		return s_compositeShred;
	}
	
	/**
	 * Method to set the icons.
	 */
	private void setIcons()
	{
		ICON_FOLDER = new Image(s_display, ICON_DIR + "/folder.png");
		ICON_FILE = new Image(s_display, ICON_DIR + "/file.png");
		ICON_BROWSE = new Image(s_display, ICON_DIR + "/browse.png");
		ICON_SHRED = new Image(s_display, ICON_DIR + "/Menu_Shredder.png");
		
	}
	
	/**
	 * Method to shred a file or directory.
	 * @param filePath the file or directory to shred
	 * @param shredAlgo the shred algorithm to use
	 * @param leaveRoot true if the directory should not been deleted 
	 * i.e. on Linux if filePath is a disk/partition use true
	 */
	private void shred(String filePath, int shredAlgo, boolean leaveRoot)
	{
		LOGGER.debug("enter  shred(String filePath, int shredAlgo, boolean leaveRoot)");
		final int BIG_FILE = 1024 * 1024 * 50; // 50MB
		final File checkFile = new File(filePath);

		if (checkFile.canWrite())
		{
			if((checkFile.length() / BIG_FILE) > 0)
				guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.DURATION.FILE.TITLE"), s_translations.getString("MESSAGE.DURATION.FILE"));
			else if(checkFile.isDirectory())
				guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.DURATION.FILE.TITLE"), s_translations.getString("MESSAGE.DURATION.DIR"));

			s_labelDeletion.setForeground(new Color(s_display, 220, 0, 0));
			s_labelDeletion.setText(s_translations.getString("SHRED.PROCEDURE"));
			LOGGER.debug("shred button pressed; leaveDirectory: " + leaveRoot);

			buttonShred.setEnabled(false);
			s_compositeShred.setEnabled(false);
			// start worker thread
			new ShredThread(s_display, filePath, shredAlgo, leaveRoot).start();
		}
		else if (!checkFile.exists())
		{
			s_shell.setCursor(null);
			guiHelper.displayWarningMessage(s_translations.getString("MESSAGE.NO.SOURCE.TITLE"), s_translations.getString("MESSAGE.NO.SOURCE"));
		}
		else
		{
			s_shell.setCursor(null);
			guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.DELETION.CONFLICT.TITLE"), s_translations.getString("MESSAGE.FILE.PERMISSION"));
		}
		
		LOGGER.debug("finish  shred(String filePath, int shredAlgo, boolean leaveRoot)");
	}


	/**
	 * Shred a file 
	 * @param filePath path of file to shred
	 * @param shredAlgo the algorithm to shred
	 * @param leaveRoot keep root
	 * @throws IOException on error
	 */	
	public static void shredFile(String filePath, int shredAlgo, boolean leaveRoot) throws IOException {
		switch(shredAlgo)
		{
		case SHRED_ALGO_ZEROES: s_shredder.wipeWithByte(filePath, leaveRoot, (byte)0x00);
		break;
		case SHRED_ALGO_DOD: s_shredder.wipeDoD(filePath, leaveRoot, false);
		break;
		case SHRED_ALGO_DOD_EXT: s_shredder.wipeDoD(filePath, leaveRoot, true);
		break;
		case SHRED_ALGO_VSITR: s_shredder.wipeVSITR(filePath, leaveRoot);
		break;
		case SHRED_ALGO_SCHNEIER: s_shredder.wipeSchneier(filePath, leaveRoot);
		break;
		case SHRED_ALGO_GUTMANN_FLOPPY: s_shredder.wipeGutmann(filePath, leaveRoot, true);
		break;
		case SHRED_ALGO_GUTMANN: s_shredder.wipeGutmann(filePath, leaveRoot, false);
		break;
		case SHRED_ALGO_RANDOM: s_shredder.wipeRandom(filePath, leaveRoot);
		break;
		default: s_shredder.wipeRandom(filePath, leaveRoot);
		}
	}
	
	/**
	 * Worker class to shred files asynchronous.
	 * 
	 * @author Johnny Graber
	 */
	class ShredThread extends Thread 
	{
		private Display display;
		private String filePath;
		private int shredAlgo;
		private boolean leaveRoot;
		
		public ShredThread(Display sDisplay, String filePath, int shredAlgo, boolean leaveRoot) 
		{
		    this.display = sDisplay;
		    this.filePath = filePath;
		    this.shredAlgo = shredAlgo;
		    this.leaveRoot = leaveRoot;
		    
		    LOGGER.debug("ShredThread created");
		}

		public void run() 
		{
			LOGGER.debug("start thread with run()");
			try
			{
				shredFile(filePath, shredAlgo, leaveRoot);
			}
			catch (IOException ioe)
			{
				LOGGER.error("Exception on shredFile in Thread: " + ioe.getMessage());
				display.asyncExec(new Runnable() {
					public void run() {
						guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.DELETION.CONFLICT.TITLE"), s_translations.getString("MESSAGE.DELETION.CONFILCT"));
					}
				});				
			}
		    
		    display.asyncExec(new Runnable() {
		        public void run() {
		          if (buttonShred.isDisposed() || s_labelDeletion.isDisposed() || s_compositeShred.isDisposed() || s_shell.isDisposed())
		          {
		        	  return;
		          }
		          buttonShred.setEnabled(true);
		          s_labelDeletion.setText("");
		          s_compositeShred.setEnabled(true);
		          s_shell.setCursor(null);
		        }
		    });
		}
	}
}
