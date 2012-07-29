package ataraxis.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Test;

public class TestTextInputDialog extends SWTBotTestCase {

	// pull this up into your own superclass that extends SWTBotTestCase and extend from your superclass instead
	static { 
		startApplicationInAnotherThread();
	}

	protected void setUp() throws Exception {
		super.setUp();
		waitForDisplayToAppear(2000); // wait for the display to appear before you do anything
	}


	private void waitForDisplayToAppear(long timeOut) throws TimeoutException, InterruptedException {
		long endTime = System.currentTimeMillis() + timeOut;
		while (System.currentTimeMillis() < endTime) { // wait until timeout
			try {
				Display display = SWTUtils.display();
				if (display != null)
					return;
			} catch (Exception e) {
				// did not find a display? no problems, try again
			}
			Thread.sleep(100); // sleep for a while and try again
		}
		throw new TimeoutException("timed out");
	}


	private static void startApplicationInAnotherThread() {
		new Thread(new Runnable() {
			public void run() {
				TextInputDialog t = new TextInputDialog();
				t.open();
			}
		}).start();
	}
	@Test
	public void testEmptyText() throws Exception{
	
		bot.button(1).click();
		bot.sleep(1000);
		bot.button("OK");
		
		
	
	}

	@Test 
	public void testClicksOnAButton() throws Exception {

		//bot.sleep(2000);
		bot.button("CANCEL");
	}

	@Test
	public void testClickOK() throws Exception {

		//bot.buttonWithLabel("CANCEL").click();
		bot.text(0).setText("TEST");
//		bot.sleep(2000);

		bot.button("OK");

		//   bot.button(2).click();

	}

}
