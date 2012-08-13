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

package ataraxis.gui.content;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import ataraxis.gui.GUIHelper;
import ataraxis.misc.AtaraxisUpdateInfo;


/**
 * AtaraxisInfoGUI creates the info composite.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 */
public class AtaraxisInfoGUI 
{

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisInfoGUI.class);

	// used AtaraxiS classes
	private GUIHelper guiHelper;
	
	// Information strings
	private static final String VERSION = "1.4.0";
	private static final String AUTHOR = "Johnny Graber + Andreas Muedespacher";
	private static final String DATE = "19.08.2012";
	private static final String WEBSITE = "http://github.com/jgraber/ataraxis/";
	
	// mapping of languages
	private static int s_lang = 0;
	private static final int GERMAN = 0;
	private static final int FRENCH = 1;
	private static final int ENGLISH = 2;
	
	// Paths and files
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	
	// general GUI elements
	private Display s_display;
	private ResourceBundle s_translations;	
	
	// Basic sizes
	private static final int COMPOSITE_WIDTH = 456;
	private static final int COMPOSITE_HEIGHT= 438;
	private static final int GROUP_HEIGHT = 120;
	private static final int GROUP_WIDTH = 450;

	// Images
	private Image ICON_ATARAXIS;
	
	/**
	 * AtaraxisInfoGUI manages all to create a composite
	 * for the info GUI.
	 * 
	 * @param translations with the translations
	 * @param lang for the about info
	 */
	public AtaraxisInfoGUI(ResourceBundle translations, int lang)
	{
		s_translations = translations;
		s_lang = lang;
	}

	/**
	 * Crate the Composite for the info GUI.
	 * 
	 * @param s_shell shell of outside SWT application
	 * @param display display of the outside SWT application
	 * @param parentComposite where the PasswordContent should be placed
	 * @return the composite
	 */
	public Composite createInfoContent(final Shell s_shell, final Display display, Composite parentComposite) 
	{
		s_display = display;
		guiHelper = new GUIHelper(s_shell);
		setIcons();
		
		Composite s_compositeInfo = new Composite(parentComposite, SWT.NONE);
		final GridLayout gridLayoutInfo = new GridLayout();
		gridLayoutInfo.numColumns = 2;
		s_compositeInfo.setLayout(gridLayoutInfo);
		final GridData gridDataInfo = new GridData(SWT.CENTER, SWT.TOP, false, false);
		gridDataInfo.widthHint = COMPOSITE_WIDTH;
		gridDataInfo.heightHint = COMPOSITE_HEIGHT;
		s_compositeInfo.setLayoutData(gridDataInfo);

		final Label label_01 = new Label(s_compositeInfo, SWT.CENTER);
		final GridData gridData_2 = new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 2);
		gridData_2.heightHint = 90;
		gridData_2.widthHint = 143;
		label_01.setLayoutData(gridData_2);
		label_01.setImage(ICON_ATARAXIS);

		final Label label_0 = new Label(s_compositeInfo, SWT.LEFT);
		final GridData gridData_1 = new GridData(143, 10);
		label_0.setLayoutData(gridData_1);

		final Label label_1 = new Label(s_compositeInfo, SWT.NONE);
		label_1.setLayoutData(new GridData(298, 50));
		label_1.setText("AtaraxiS");
		label_1.setFont(guiHelper.getBigFont(s_display));

		final Group groupAppl = new Group(s_compositeInfo, SWT.NONE);
		groupAppl.setText(s_translations.getString("APPLICATION.INFO"));
		final GridData gridDataInfoAppl = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gridDataInfoAppl.heightHint = GROUP_HEIGHT + 80;
		gridDataInfoAppl.widthHint = GROUP_WIDTH;
		groupAppl.setLayoutData(gridDataInfoAppl);
		final GridLayout gridLayoutInfoAppl = new GridLayout();
		gridLayoutInfoAppl.numColumns = 2;
		groupAppl.setLayout(gridLayoutInfoAppl);

		final Label label_3 = new Label(groupAppl, SWT.LEFT);
		label_3.setLayoutData(new GridData(143, 20));
		label_3.setText(s_translations.getString("AUTHOR") + ":");

		final Label label_4 = new Label(groupAppl, SWT.LEFT);
		label_4.setLayoutData(new GridData(290, 20));
		label_4.setText(AUTHOR);

		final Label label_5 = new Label(groupAppl, SWT.LEFT);
		label_5.setLayoutData(new GridData(143, 20));
		label_5.setText(s_translations.getString("LICENSE") + ":");

		final Button licenseButton = new Button(groupAppl,SWT.NONE);
		licenseButton.setLayoutData(new GridData(290, 25));
		licenseButton.setText("EUPL");

		licenseButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				s_shell.setCursor(new Cursor(display,SWT.CURSOR_WAIT));

				final Program progPDF = Program.findProgram (".pdf");
				final Program progTXT = Program.findProgram (".txt");
				
				if (progPDF != null)
				{
					Program.launch(APPL_DIR +"/LICENCE.pdf");
				}
				else if (progTXT != null)
				{
					Program.launch(APPL_DIR +"/LICENCE.txt");
				}
				
				s_shell.setCursor(null);
			}
		});

		
		final Label label_7 = new Label(groupAppl, SWT.LEFT);
		label_7.setLayoutData(new GridData(143, 20));
		label_7.setText(s_translations.getString("VERSION") + ":");

		final Label label_8 = new Label(groupAppl, SWT.LEFT);
		label_8.setLayoutData(new GridData(290, 20));
		label_8.setText(VERSION + "    (" + DATE + ")");

		final Label label_11 = new Label(groupAppl, SWT.LEFT);
		label_11.setLayoutData(new GridData(143, 20));
		label_11.setText(s_translations.getString("WEBSITE") + ":");

		final Link link = new Link(groupAppl, SWT.NONE);
		link.setLayoutData(new GridData(290, 20));
		link.setText("<a>" + WEBSITE + "</a>");
		link.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event event) 
			{				
				final Program p = Program.findProgram (".html");
				if (p != null)
					Program.launch(event.text);
			}
		});

		final Label label_9 = new Label(groupAppl, SWT.LEFT);
		final GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.heightHint = 20;
		gridData.widthHint = 143;
		label_9.setLayoutData(gridData);
		label_9.setText(s_translations.getString("DESCRIPTION") + ":");

		final Label label_10 = new Label(groupAppl, SWT.LEFT | SWT.WRAP);
		label_10.setLayoutData(new GridData(290, 47));

		String langStr = "";
		switch(s_lang)
		{
		case GERMAN: langStr = "de"; break;
		case FRENCH: langStr = "fr"; break;
		case ENGLISH: langStr = "en"; break;
		default: langStr = "en";
		}


		if(langStr.equals("de"))
		{
			label_10.setText("Dieses Programm ist eine Weiterentwicklung unserer Diplomarbeit an der HTI Biel.");
		}
		else if(langStr.equals("fr"))
		{
			label_10.setText("Ce programme  est un d\u00E9veloppement au base de notre travail de dipl\u00F4me \u00E0 la HESB-TI Bienne. ");
		}
		else
		{
			label_10.setText("This application is the result of the diploma work at the Bern university of applied sciences HTI Biel.");
		}

		final Label label_update = new Label(groupAppl, SWT.LEFT);
		final GridData gridDataUpdate = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.heightHint = 20;
		gridData.widthHint = 143;
		label_update.setLayoutData(gridDataUpdate);
		label_update.setText(" ");

		final Button updateButton = new Button(groupAppl,SWT.NONE);
		updateButton.setLayoutData(new GridData(290, 25));
		updateButton.setText(s_translations.getString("UPDATE.CHECK_FOR_UPDATES"));

		updateButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				s_shell.setCursor(new Cursor(display,SWT.CURSOR_WAIT));

				AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
				try {
					boolean existNewer = updateInfo.existNewerVersion(VERSION);
					if(existNewer)
					{
						LOGGER.info("A newer version exist");

						final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
						messageBox.setMessage("AtaraxiS "+updateInfo.getCurrentVersion()+" "+s_translations.getString("UPDATE.NEW_VERSION_DOWNLOAD"));
						messageBox.setText(s_translations.getString("UPDATE.UPDATE_BOX_TITLE"));
						int downloadIt = messageBox.open();

						if(downloadIt == SWT.YES)
						{
							LOGGER.debug("Try to open website in browser");
							final Program p = Program.findProgram ("html");
							if (p != null) {
								Program.launch(updateInfo.getCurrentURL());
							}
						}
					}
					else
					{
						LOGGER.info("Version is up to date");
						guiHelper.displayInfoMessage(s_translations.getString("UPDATE.UPDATE_BOX_TITLE"), s_translations.getString("UPDATE.VERSION_IS_UPTODATE"));
					}
				} 
				catch (Exception e1) 
				{
					LOGGER.warn(e1.getMessage());
					guiHelper.displayErrorMessage(s_translations.getString("UPDATE.UPDATE_BOX_TITLE"), s_translations.getString("UPDATE.ERROR_DONTWORK"));
				}
				
				s_shell.setCursor(null);
			}
		});

		// Start of System Info Box

		final Group groupSystem = new Group(s_compositeInfo, SWT.NONE);
		groupSystem.setText(s_translations.getString("SYSTEM.INFO"));
		final GridData gridDataInfoSystem = new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1);
		gridDataInfoSystem.heightHint = GROUP_HEIGHT;// + 20;
		gridDataInfoSystem.widthHint = GROUP_WIDTH;
		groupSystem.setLayoutData(gridDataInfoSystem);
		final GridLayout gridLayoutInfoSystem = new GridLayout();
		gridLayoutInfoSystem.numColumns = 2;
		groupSystem.setLayout(gridLayoutInfoSystem);

		final Label label_21 = new Label(groupSystem, SWT.LEFT);
		label_21.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_21.setText(s_translations.getString("OS.NAME") + ":");

		final Label label_22 = new Label(groupSystem, SWT.LEFT);
		//label_22.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_22.setText(System.getProperty("os.name") + ", " + 
				System.getProperty("sun.os.patch.level")+ " (" + 
				System.getProperty("os.arch") + ")");

		final Label label_23 = new Label(groupSystem, SWT.LEFT);
		label_23.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_23.setText(s_translations.getString("JAVA.VENDOR") + ":");

		final Label label_24 = new Label(groupSystem, SWT.LEFT);
		//label_24.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_24.setText(System.getProperty("java.vendor"));

		final Label label_27 = new Label(groupSystem, SWT.LEFT);
		label_27.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_27.setText(s_translations.getString("JAVA.VERSION") + ":");

		final Label label_28 = new Label(groupSystem, SWT.LEFT);
		//label_28.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_28.setText(System.getProperty("java.version"));

		final Label label_25 = new Label(groupSystem, SWT.LEFT);
		label_25.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_25.setText(s_translations.getString("JAVA.HOME") + ":");

		final Label label_26 = new Label(groupSystem, SWT.LEFT);
		//label_26.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_26.setText(System.getProperty("java.home"));

		return s_compositeInfo;
	}
	
	/**
	 * Method to set the icons.
	 */
	private void setIcons()
	{
		ICON_ATARAXIS = new Image(s_display, ICON_DIR + "/Info_Ataraxis.png");
	}
	
}
