package ataraxis.snippets;

import org.apache.log4j.Logger;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.security.KeyStoreException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.crypt.CryptoMethodError;

public class USBStickPerformance {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(USBStickPerformance.class);

	/**
	 * TestSzenario:
	 * a) User copy Data to USB-Stick 
	 * B) and later copy them back (PLAIN).
	 * 
	 * c) User encrypt Data to USB-Stick 
	 * d) and later decrypt them back to Disk
	 */
	private static AtaraxisCrypter s_ac;
	private static final String DIR_TEST_LOCAL = System.getProperty("user.dir")+"/test/PerfTest";
	private static final String DIR_TEST_STICK = "F:/PerfTest";
	private static final String PLAIN_INPUT_DIR = DIR_TEST_LOCAL + "/PlainInTests";
	private static final String PLAIN_OUTPUT_DIR = DIR_TEST_STICK + "/PlainOutTests";
	private static final String ENCRYPT_OUTPUT_DIR = DIR_TEST_STICK + "/EncryptOutTests";
	private static final String DECRYPT_OUTPUT_DIR = DIR_TEST_LOCAL + "/DecryptOutTests";
	
	private static File fPLAIN_INPUT_DIR;
	private static File fPLAIN_OUTPUT_DIR;
	private static File fENCRYPT_OUTPUT_DIR;
	private static File fDECRYPT_OUTPUT_DIR;
	
	private static long timePlainCopy = 0;
	private static long timePlainBack = 0;
	private static long timeEncrypt = 0;
	private static long timeDecrypt = 0;
	
	private static int nbFiles = 0;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		BasicConfigurator.configure();
    	logger.getRootLogger().setLevel(Level.WARN);
    	//USBStickPerformance perf;
    	
		for(int i = 0; i< 5; i++)
		{
			USBStickPerformance perf = new USBStickPerformance(2);
			perf.runOperations();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Start next round");
		}
		

	}

	public USBStickPerformance(int nbOfFiles) throws Exception
	{
		fPLAIN_INPUT_DIR  = new File(PLAIN_INPUT_DIR);
		fPLAIN_INPUT_DIR.mkdirs();
		fPLAIN_OUTPUT_DIR  = new File(PLAIN_OUTPUT_DIR);
		fPLAIN_OUTPUT_DIR.mkdirs();
		fENCRYPT_OUTPUT_DIR  = new File(ENCRYPT_OUTPUT_DIR);
		fENCRYPT_OUTPUT_DIR.mkdirs();
		fDECRYPT_OUTPUT_DIR  = new File(DECRYPT_OUTPUT_DIR);
		fDECRYPT_OUTPUT_DIR.mkdirs();
		nbFiles = nbOfFiles;
		s_ac = new AtaraxisCrypter(DIR_TEST_LOCAL+"/perf.ks", "a long an d good PW".toCharArray(), true);
		timePlainCopy = 0;
		timePlainBack = 0;
		timeEncrypt = 0;
		timeDecrypt = 0;
	}
	
	private void createTestFiles(int nbFiles, int fileSize)
	{
		File createFile;

		for (int i = 0; i<nbFiles; i++)
		{
			createFile = new File(PLAIN_INPUT_DIR+"/perfTest_"+i+".file");
			fillFile(createFile, fileSize);
		}
		
	}
	
	private boolean fillFile(File createFile, long fileSize) {
		System.out.println("fillFile - size: "+fileSize+" name: "+createFile);
		boolean wasOK = false;
		int arrayLength = 1023; // multiple of 3
        byte[] patternArray = new byte[arrayLength];
        long loops = fileSize / arrayLength;
        int rest = (int) (fileSize % arrayLength);
        Random random = new Random();
        random.nextBytes(patternArray);
		
        try {
        // Open or create the output file
        final FileOutputStream os = new FileOutputStream(createFile);

        for (long i = 1; i <= loops; i++){
        	random.nextBytes(patternArray);
        	if(i % 5 == 0)
    			Arrays.fill(patternArray, (byte) 0xFF);
			os.write(patternArray);
					
        	if(i % 1025 == 0)
        		os.flush(); // flush +- each MB to file (1023*1025)
        }
        if (rest != 0){
        	random.nextBytes(patternArray);
        	os.write(patternArray, 0, rest);
        	os.flush();
        }        
        os.close();
        wasOK = true;
        } catch (IOException e) {
			e.printStackTrace();
		}
		return wasOK;
	}
	
	private void clearTestFiles()
	{
		String singleLine;
		System.out.println("Try to delete. ENTER for procceed:");
	
			Scanner scanner = new Scanner(System.in);
			singleLine = scanner.nextLine().trim();
			dirDel(fENCRYPT_OUTPUT_DIR);
			dirDel(fPLAIN_INPUT_DIR);
			dirDel(fPLAIN_OUTPUT_DIR);	
			dirDel(fDECRYPT_OUTPUT_DIR);
			File f = new File(DIR_TEST_LOCAL+"/perf.ks");
			f.delete();
			System.out.println("Dirs deletet");	
	}
	
	private void dirDel(File fileToRemove) {
		if(fileToRemove.isDirectory())
		{
			for(File f : fileToRemove.listFiles())
			{
				dirDel(f);
			}
			fileToRemove.delete();
		}
		else
		{
			fileToRemove.delete();
		}
		
	}

	private  void runPlainCopy()
	{
		File targetFile;
		long timeStart;
		try {
			for (File f : fPLAIN_INPUT_DIR.listFiles()){
				targetFile = new File(PLAIN_OUTPUT_DIR+"/"+f.getName());
				timeStart = System.nanoTime();

				copy(f, targetFile);

				// s_ac.encryptFile(FILE_M_O, FILE_M_E);
				timePlainCopy += System.nanoTime() - timeStart;
				System.out.println(targetFile.getName()+ " - "+targetFile.length());
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private  void runPlainBack()
	{
		File targetFile;
		long timeStart;
		try {
			for (File f : fPLAIN_OUTPUT_DIR.listFiles()){
				targetFile = new File(PLAIN_INPUT_DIR+"/back_"+f.getName());
				timeStart = System.nanoTime();

				copy(f, targetFile);

				timePlainBack += System.nanoTime() - timeStart;
				System.out.println(targetFile.getName()+ " - "+targetFile.length());
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private  void runEncryptCopy()
	{
		File targetFile;
		long timeStart;
		// encrypted files
		try {
		for (File f : fPLAIN_INPUT_DIR.listFiles()){
			targetFile = new File(ENCRYPT_OUTPUT_DIR+"/enc_"+f.getName());
			timeStart = System.nanoTime();

			
				
				s_ac.encryptFile(f.getAbsolutePath(), targetFile.getAbsolutePath());
			

			// s_ac.encryptFile(FILE_M_O, FILE_M_E);
			timeEncrypt += System.nanoTime() - timeStart;
			System.out.println(targetFile.getName()+ " - "+targetFile.length());
		} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CryptoMethodError e) {
			e.printStackTrace();
		}

	}
	
	private  void runDecryptCopy()
	{
		File targetFile;
		long timeStart;

		// Plain-Text files
		

		// decrypted files
		try {
			int i = 0;
		for (File f : fENCRYPT_OUTPUT_DIR.listFiles()){
			targetFile = new File(DECRYPT_OUTPUT_DIR+"/decrypt_"+i+".file");
			i++;
			timeStart = System.nanoTime();
	
			s_ac.decryptFile(f.getAbsolutePath(), targetFile.getAbsolutePath());
			
			timeDecrypt += System.nanoTime() - timeStart;
			System.out.println(targetFile.getName()+ " - "+targetFile.length());
		} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CryptoMethodError e) {
			e.printStackTrace();
		}
	}
	
	public void runOperations()
	{
				
		createTestFiles(nbFiles, 1024*1024*50);
		System.out.println(nbFiles + " Files created");
		runPlainCopy();
		runEncryptCopy();
		
		runDecryptCopy();
		runPlainBack();
		
		
		showStatistic();
		clearTestFiles();
		

	}
	
	
    private static void copy(File src, File dst) throws Exception {

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
      
        // Transfer bytes from in to out
        byte[] buf = new byte[2048];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
	private void showStatistic()
	{
		System.out.println("a) Plain Copy: " + 
	    		 TimeUnit.NANOSECONDS.toMillis(timePlainCopy)/nbFiles+ " ms (" 
	    		 + TimeUnit.NANOSECONDS.toSeconds(timePlainCopy)/nbFiles+ " s) "
	    		 +" for "+nbFiles+" Files");
		System.out.println("c) Encrypt Copy: " + 
	    		 TimeUnit.NANOSECONDS.toMillis(timeEncrypt)/nbFiles+ " ms (" 
	    		 + TimeUnit.NANOSECONDS.toSeconds(timeEncrypt)/nbFiles+ " s) "
	    		 +" for "+nbFiles+" Files");
		System.out.println("d) Decrypt back: " + 
	    		 TimeUnit.NANOSECONDS.toMillis(timeDecrypt)/nbFiles+ " ms (" 
	    		 + TimeUnit.NANOSECONDS.toSeconds(timeDecrypt)/nbFiles+ " s) "
	    		 +" for "+nbFiles+" Files");
		System.out.println("b) Plan back: " + 
	    		 TimeUnit.NANOSECONDS.toMillis(timePlainBack)/nbFiles+ " ms (" 
	    		 + TimeUnit.NANOSECONDS.toSeconds(timePlainBack)/nbFiles+ " s) "
	    		 +" for "+nbFiles+" Files");
	}
}
