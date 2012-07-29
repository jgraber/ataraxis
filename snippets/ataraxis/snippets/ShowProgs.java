package ataraxis.snippets;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

public class ShowProgs {

	/** lists all extensions of the operating systems and prints a list of the programs
	 * @param args
	 */
	public static void main(String[] args) {
		
		// according to Javadoc of Program, there should exist a display for correct results
		Display display = Display.getDefault();
		
 		Program[] progs = Program.getPrograms();
		System.out.println("Supported Extensions:");
		for(int j = 0; j<progs[0].getExtensions().length; j++){
			System.out.print(progs[0].getExtensions()[j] + "; ");
			if (j%8 == 0 && j !=0)
				System.out.println("");
		}
		System.out.println("\n\nPrograms:");
		for (int i = 0; i<progs.length; i++){
			System.out.println(progs[i]);
		}
		
		// Sample
		String ext = ".txt";
		System.out.println("\nOpen extension '" + ext + "' with:  ");
		System.out.print(Program.findProgram(ext).toString());
	}

}
