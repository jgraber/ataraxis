import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * Test Background Work for SWT based on tutorial on
 * http://www.java2s.com/Code/JavaAPI/org.eclipse.swt.widgets/DisplayasyncExecRunnablerun.htm
 * 
 * @author Johnny Graber
 */
public class BackgroundWorkTest {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final BackgroundWorkTest window = new BackgroundWorkTest();
		window.open();
	}
	
	/**
	 * Open the window
	 */
	public void open() 
	{

		final Display s_display = Display.getDefault();
		final Shell s_shell = new Shell(s_display);
		s_shell.setSize(new Point(400,400));
				
		s_shell.setText("BackgroundWork Test");
		s_shell.setLayout (new FillLayout ());
		s_shell.addListener (SWT.Traverse, new Listener () {
			public void handleEvent (Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE)
					s_shell.dispose();
			}
		});
		
		Composite s_composite = new Composite(s_shell, SWT.NONE);
		s_composite.setLayout (new FillLayout ());
		final Button workButton = new Button(s_composite, SWT.FLAT);
		workButton.setText("Work!");
		workButton.setLocation(new Point(0,0));
		workButton.setSize(100, 50);
		
		final ProgressBar progress = new ProgressBar(s_composite, SWT.HORIZONTAL | SWT.SMOOTH);
		
		workButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				progress.setSelection(0);
				workButton.setEnabled(false);
				new LongRunningOperation(s_display, progress, workButton).start();
				workButton.setText("SelectionListener finish!");
			}
		});
		
		s_shell.layout();
		s_shell.open();
		while (!s_shell.isDisposed()) 
		{
			if (!s_display.readAndDispatch())
				s_display.sleep();
		}

	}
	
	class LongRunningOperation extends Thread {
		  private Display display;
		  private ProgressBar progressBar;
		  private Button workButton;

		  public LongRunningOperation(Display display, ProgressBar progressBar, Button workButton) {
		    this.display = display;
		    this.progressBar = progressBar;
		    this.workButton = workButton;
		  }

		  public void run() {
		    for (int i = 0; i < 100; i++) {
		      try {
		        Thread.sleep(100);
		      } catch (InterruptedException e) {
		      }
		      display.asyncExec(new Runnable() {
		        public void run() {
		          if (progressBar.isDisposed())
		            return;
		          progressBar.setSelection(progressBar.getSelection() + 1);
		        }
		      });
		    }
		    
		    display.asyncExec(new Runnable() {
		        public void run() {
		          if (workButton.isDisposed())
		            return;
		          workButton.setText("Thread is finished");
		          workButton.setEnabled(true);
		        }
		      });
		  }
		}
}
