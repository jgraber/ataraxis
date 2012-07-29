package ataraxis.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NavigationIdeaJG1 
{

	private static Display s_display =  Display.getDefault();
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String USER_DATA_DIR = APPL_DIR + "/user_data";
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	// images for GUI
	private final static Image ICON_ENCRYPT = new Image(s_display, ICON_DIR + "/Menu_Encrypt.png");
	private final static Image ICON_DECRYPT = new Image(s_display, ICON_DIR + "/Menu_Decrypt.png");
	private final static Image ICON_SHRED = new Image(s_display, ICON_DIR + "/Menu_Shredder.png");
	private final static Image ICON_PASSWORDS = new Image(s_display, ICON_DIR + "/Menu_Password.png");
	private final static Image ICON_CONFIG = new Image(s_display, ICON_DIR + "/Menu_Config.png");
	private final static Image ICON_INFO = new Image(s_display, ICON_DIR + "/Menu_Info.png");
	private final static Image ICON_EXIT = new Image(s_display, ICON_DIR + "/Menu_Exit.png");
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NavigationIdeaJG1 window = new NavigationIdeaJG1();
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell();
		shell.setSize(578, 476);
		shell.setText("SWT Application");
		//

		shell.open();

		final ExpandBar expandBar = new ExpandBar(shell, SWT.V_SCROLL | SWT.SCROLL_LOCK );
		expandBar.setBackgroundMode(SWT.INHERIT_DEFAULT);

		expandBar.setBounds(0, 0, 110, 440);

		

		final ExpandItem newItemExpandItem = new ExpandItem(expandBar, SWT.NONE);
		newItemExpandItem.setText("File");

		final Composite composite_1 = new Composite(expandBar, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));
		newItemExpandItem.setHeight(180);
		

		final Button button = new Button(composite_1, SWT.NONE);
		button.setImage(ICON_ENCRYPT);

		final Button button_6 = new Button(composite_1, SWT.NONE);
		button_6.setImage(ICON_DECRYPT);

		final Button button_2 = new Button(composite_1, SWT.NONE);
		button_2.setImage(ICON_SHRED);
/*
		final Button button_5 = new Button(composite_1, SWT.NONE);
		button_5.setText("button");

		final Button button_3 = new Button(composite_1, SWT.NONE);
		button_3.setText("button");

		final Button button_7 = new Button(composite_1, SWT.NONE);
		button_7.setText("button");

		final Button button_4 = new Button(composite_1, SWT.NONE);
		button_4.setText("button");*/

		newItemExpandItem.setControl(composite_1);

		final ExpandItem newItemExpandItem_2 = new ExpandItem(expandBar, SWT.NONE);
		newItemExpandItem_2.setExpanded(true);
		newItemExpandItem_2.setText("Password");

		final Composite composite_2 = new Composite(expandBar, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.VERTICAL));
		newItemExpandItem_2.setHeight(composite_2.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		newItemExpandItem_2.setControl(composite_2);
		newItemExpandItem_2.setHeight(120);

		final Button button_1 = new Button(composite_2, SWT.NONE);
		button_1.setImage(ICON_PASSWORDS);

		final Button button_8 = new Button(composite_2, SWT.NONE);
		button_8.setText("button");
		
		final ExpandItem newItemExpandItem_1 = new ExpandItem(expandBar, SWT.NONE);
		newItemExpandItem_1.setExpanded(true);
		newItemExpandItem_1.setText("AtaraxiS");
		newItemExpandItem_1.setHeight(180);

		final Composite composite = new Composite(expandBar, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		
		newItemExpandItem_1.setControl(composite);

		final Button button_9 = new Button(composite, SWT.NONE);
		button_9.setImage(ICON_CONFIG);
		

		final Button button_11 = new Button(composite, SWT.NONE);
		button_11.setImage(ICON_INFO);

		final Button button_10 = new Button(composite, SWT.NONE);
		button_10.setImage(ICON_EXIT);

		// needet to generate all Icons right
		newItemExpandItem.setExpanded(false);
		newItemExpandItem_1.setExpanded(false);
		newItemExpandItem_2.setExpanded(false);
		newItemExpandItem.setExpanded(true);
		newItemExpandItem_1.setExpanded(true);
		newItemExpandItem_2.setExpanded(true);
		
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
