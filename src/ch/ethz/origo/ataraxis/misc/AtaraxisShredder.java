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

package ch.ethz.origo.ataraxis.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import ch.ethz.origo.ataraxis.util.FileList;

/**
 * AtaraxisShredder supports different secure deletion methods for wiping files 
 * or directories.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class AtaraxisShredder {
	
	/**
	 * random mode which repeats a 1KByte random pattern
	 */
	public static final int RANDOM_PATTERN = 0;
	
	/**
	 * random mode which fills the file with {@link java.util.Random} bytes
	 */
	public static final int RANDOM = 1;
	
	/**
	 * random mode which fills the file with {@link java.security.SecureRandom} bytes
	 */
	public static final int RANDOM_SECURE = 2;
	
	/**
	 * default number of random passes
	 */
	private static final int RANDOM_PASSES = 10;
	
	/**
	 * Byte 0x00 = 00000000  (complement of 0xFF)
	 */
	private static final byte BYTE_00 = (byte) 0x00;
	
	/**
	 * Byte 0x55 = 01010101 (complement of 0xAA)
	 */
	private static final byte BYTE_55 = (byte) 0x55;
	
	/**
	 * Byte 0xAA = 10101010 (complement of 0x55)
	 */
	private static final byte BYTE_AA = (byte) 0xAA;
	
	/**
	 * Byte 0xFF = 11111111 (complement of 0x00)
	 */
	private static final byte BYTE_FF = (byte) 0xFF;
	
	/**
	 * first pattern for MFM encoding (1,3)RLL with time bit 0 (sequence #5)
	 */
	private static final byte PATTERN_5 = BYTE_55;
	
	/**
	 * second pattern for MFM encoding (1,3)RLL with time bit 0 (sequence #6)
	 */
	private static final byte PATTERN_6 = BYTE_AA;
	
	/**
	 * first pattern for MFM encoding (1,3)RLL with 3 time bits (sequence #7 + #26)
	 */
	private static final byte[] PATTERN_7_26 = {(byte) 0x92, (byte) 0x49, (byte) 0x24};
	
	/**
	 * second pattern for MFM encoding (1,3)RLL with 3 time bits (sequence #8 + #27)
	 */
	private static final byte[] PATTERN_8_27 = {(byte) 0x49, (byte) 0x24, (byte) 0x92};
	
	/**
	 * thrid pattern for MFM encoding (1,3)RLL with 3 time bits (sequence #9 + #28)
	 */
	private static final byte[] PATTERN_9_28 = {(byte) 0x24, (byte) 0x92, (byte) 0x49};
	
	/**
	 *  single byte patterns for sequence #10 to #25 of Gutmann algorithm
	 *  this array includes 16 patterns
	 */
	private static final byte[] PATTERN_ARRAY_1_BYTE = new byte[] {(byte) 0x00, 
		(byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55,
		(byte) 0x66, (byte) 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xAA,
		(byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF};
	
	/**
	 * pattern for sequence #29 encoding (2,7)RLL
	 */
	private static final byte[] PATTERN_29 = {(byte) 0x6D, (byte) 0xB6, (byte) 0xDB};
	
	/**
	 * pattern for sequence #29 encoding (2,7)RLL
	 */
	private static final byte[] PATTERN_30 = {(byte) 0xB6, (byte) 0xDB, (byte) 0x6D};
	
	/**
	 * pattern for sequence #29 encoding (2,7)RLL
	 */
	private static final byte[] PATTERN_31 = {(byte) 0xDB, (byte) 0x6D, (byte) 0xB6};
	
	/**
	 *  triple byte patterns for sequence #7 to #9 and #26 to #31 of Gutmann algorithm
	 *  this array includes 9 patterns
	 */
	private static final byte[][] PATTERN_ARRAY_3_BYTES = new byte[][] { PATTERN_7_26, 
		PATTERN_8_27, PATTERN_9_28, PATTERN_29, PATTERN_30, PATTERN_31};
	
	private  long s_fileSize = 0;
	private  String s_fileName = "";
	private  File s_file = null;
	private  SecureRandom s_random = null;
	private  Integer[] s_sequence = null;
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisShredder.class);
	
	
	
	/**
	 * Creates an instance of AtaraxisShredder
	 */
	public AtaraxisShredder() 
	{
		// empty constructor
	}

	/**
	 * Use this method if you want to specify yourself with which byte value the
	 * data shall once be overwritten.
	 * 
	 * @param fileName the file or directory to be overwritten and deleted
	 * @param leaveDir if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @param data     the byte to write
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	public final void wipeWithByte(String fileName, boolean leaveDir, byte data)
		throws IOException
	{

		s_fileName = fileName;
		s_file = new File(s_fileName);
		s_fileSize = s_file.length();
		
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: "
					+ s_fileName);
		
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir)
				listSize--;
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile())
					writeOneByte(data);
				unNameAndDelete(s_fileName);
			}
		}
		else // it's a single file, not a directory
		{
			writeOneByte(data);
			unNameAndDelete(s_fileName);
		}
	}
	
	/**
	 * Use this method to overwrite data 10 times with random bytes.<br>
	 * According to Peter Gutmann to delete data on a modern hard disk
	 * (PRML/EPRML encoding) "a few passes of random scrubbing is the best you can do".
	 * 
	 * @param fileName the file or directory to be overwritten and deleted
	 * @param leaveDir if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	public final void wipeRandom(String fileName, boolean leaveDir) throws IOException
	{
		s_fileName = fileName;
		s_file = new File(s_fileName);
		s_fileSize = s_file.length();
		
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: "
					+ s_fileName);
		
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir)
				listSize--;
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile())
					writeRandom(RANDOM, RANDOM_PASSES);
				unNameAndDelete(s_fileName);
			}
			LOGGER.debug(listSize + " files or directories deleted using wipeRandom in default mode");
		}
		else // it's a single file, not a directory
		{			
			writeRandom(RANDOM, RANDOM_PASSES);
			unNameAndDelete(s_fileName);
			LOGGER.debug("1 file deleted using wipeRandom in default mode");
		}
	}
	
	/**
	 * Use this method if you want to specify yourself how many times the data
	 * should be overwritten and which mode you would like to use.<br>
	 * According to Peter Gutmann to delete data on a modern hard disk
	 * (PRML/EPRML encoding) "a few passes of random scrubbing is the best you can do".<br>
	 * <br>
	 * <br>
	 * Use the public constants of AtaraxisShreder to set the mode; if 
	 * {@link RANDOM_PATTERN } is selected, the file will be overwritten with a repeated 
	 * random pattern of 1kB.
	 * 
	 * @param fileName the file or directory to be overwritten and deleted
	 * @param leaveDir if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @param mode     eather {@link RANDOM_PATTERN}, <code>RANDOM</code> or <code>RANDOM_SECURE</code>
	 * @param passes   the number of times the files gets overwritten
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	public final void wipeRandom(String fileName, boolean leaveDir, int mode, int passes)
		throws IOException
	{		
		String logMode = "no mode selected!";
		s_fileName = fileName;
		s_file = new File(s_fileName);
		s_fileSize = s_file.length();
		
		if (LOGGER.isDebugEnabled()) 
		{	
			if (mode == RANDOM_PATTERN)
				logMode = "RANDOM_PATTERN";
			else if (mode == RANDOM)
				logMode = "RANDOM";
			else if (mode == RANDOM_SECURE)
				logMode = "RANDOM_SECURE";
		}
		
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: "
					+ s_fileName);
		
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir)
				listSize--;
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile())
					writeRandom(mode, passes);
				unNameAndDelete(s_fileName);
			}
			LOGGER.debug(listSize + " files or directories deleted using wipeRandom; " +
					"mode: " + logMode + ", passes: " + passes);
			
		}
		else // it's a single file, not a directory
		{			
			writeRandom(mode, passes);
			unNameAndDelete(s_fileName);
			LOGGER.debug("1 file deleted using wipeRandom; mode: "
					+ logMode + ", passes: " + passes);
		}
	}
	
	/**
	 * Implementation of Gutmann algorithm, securest algorithm known today.<br>
	 * For information about the algorithm see 
	 * <a href="http://www.cs.auckland.ac.nz/~pgut001/pubs/secure_del.html">Gutmann's paper</A>.
	 * The full mode overwrites the data 35 times (4 random, 27 pattern and 4 random passes).
	 * If in floppyMode, it does only the part of the Gutmann algorithm which is
	 * needed for FM and MFM encoding (i.e. floppy disks) plus before and after 
	 * it writes four passes with random bytes so its faster than the whole 
	 * Gutmann algorithm.
	 * 
	 * @param fileName   the file or directory to be overwritten and deleted
	 * @param leaveDir   if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @param floppyMode if true, only random and floppy passes will be written
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	public final void wipeGutmann(String fileName, boolean leaveDir, boolean floppyMode)
		throws IOException
	{		
		String logMode = "";
		s_fileName = fileName;
		s_file = new File(s_fileName);
		s_fileSize = s_file.length();

		if (floppyMode)
		{
			logMode = "floppy mode";
			generateSequence(true);
		}			
		else
		{
			generateSequence(false);
			logMode = "full mode";
		}
			
		
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: " + s_fileName);
		
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir) 
				listSize--; // because the last file of the list is the fileName(dir)!
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile()){
					writeRandom(4);
					for(int j = 0; j < s_sequence.length; j++)
					{
						if (s_sequence[j] == 5){
							writeOneByte(PATTERN_5);
						}
						else if (s_sequence[j] == 6){
							writeOneByte(PATTERN_6);
						}
						else if (s_sequence[j] < 10){
							writeThreeBytes(PATTERN_ARRAY_3_BYTES[s_sequence[j]-7]);
						}
						else if (s_sequence[j] < 26){    // not used for floppy mode
							writeOneByte(PATTERN_ARRAY_1_BYTE[s_sequence[j]-10]);
						}
						else{   // not used for floppy mode
							writeThreeBytes(PATTERN_ARRAY_3_BYTES[s_sequence[j]-26]);
						}
					}
					writeRandom(4);
				}
				unNameAndDelete(s_fileName);
			}
			LOGGER.debug(listSize + " files or directories deleted using wipeGutmann in " + logMode);
			
		}
		else // it's a single file, not a directory
		{			
			writeRandom(4);
			for(int j = 0; j < s_sequence.length; j++)
			{
				if (s_sequence[j] == 5)
					writeOneByte(PATTERN_5);
				else if (s_sequence[j] == 6)
					writeOneByte(PATTERN_6);
				else if (s_sequence[j] < 10)
					writeThreeBytes(PATTERN_ARRAY_3_BYTES[s_sequence[j]-7]);
				else if (s_sequence[j] < 26)    // not used for floppy mode
					writeOneByte(PATTERN_ARRAY_1_BYTE[s_sequence[j]-10]);
				else    // not used for floppy mode
					writeThreeBytes(PATTERN_ARRAY_3_BYTES[s_sequence[j]-26]);
			}
			writeRandom(4);
			unNameAndDelete(s_fileName);
			LOGGER.debug("1 file deleted using wipeGutmann in " + logMode);
		}
	}
	
	/**
	 * Secure deletion using VSITR standard of BSI, Germany.<br>
	 * 
	 * BSI   = Bundesamt fuer Sicherheit in der Informationstechnik
	 *         (German Federal Office for IT Security)<br>
	 * VSITR = Verschlusssachen-IT-Richtlinien<br>
	 * 
	 * @param fileName the fully qualified name of a file or directory to delete
	 * @param leaveDir if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	public final void wipeVSITR(String fileName, boolean leaveDir) throws IOException
	{		
		s_fileName = fileName;
		s_file = new File(s_fileName);
		s_fileSize = s_file.length();
		
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: " + s_fileName);
		
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir)
				listSize--;
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile())
				{
					writeOneByte(BYTE_00);
					writeOneByte(BYTE_FF);
					writeOneByte(BYTE_00);
					writeOneByte(BYTE_FF);
					writeOneByte(BYTE_00);
					writeOneByte(BYTE_FF);
					writeOneByte(BYTE_AA);
					writeRandom(1);
				}
				unNameAndDelete(s_fileName);
			}
			LOGGER.debug(listSize + " files or directories deleted using wipeVSITR mode");
			
		}
		else // it's a single file, not a directory
		{			
			writeOneByte(BYTE_00);
			writeOneByte(BYTE_FF);
			writeOneByte(BYTE_00);
			writeOneByte(BYTE_FF);
			writeOneByte(BYTE_00);
			writeOneByte(BYTE_FF);
			writeOneByte(BYTE_AA);
			writeRandom(1);
			unNameAndDelete(s_fileName);
			LOGGER.debug("1 file deleted using wipeVSITR mode");
		}
	}
	
	/**
	 * Secure deletion using Bruce Schneier's algorithm.
	 * 
	 * @param fileName the fully qualified name of a file or directory
	 * @param leaveDir if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	public final void wipeSchneier(String fileName, boolean leaveDir) throws IOException
	{		
		s_fileName = fileName;
		s_file = new File(s_fileName);
		s_fileSize = s_file.length();
		
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: " + s_fileName);
		
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir)
				listSize--;
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile()){
					writeOneByte(BYTE_00);
					writeOneByte(BYTE_FF);
					writeRandom(5);
				}
				unNameAndDelete(s_fileName);
			}
			LOGGER.debug(listSize + " files or directories deleted using wipeSchneier mode");
			
		}
		else // it's a single file, not a directory
		{			
			writeOneByte(BYTE_00);
			writeOneByte(BYTE_FF);
			writeRandom(5);
			unNameAndDelete(s_fileName);
			LOGGER.debug("1 file deleted using wipeSchneier mode");
		}
	}
	
	/**
	 * Secure deletion implementing the United States Department of Defence (DodD) 
	 * Standard 5220.22-M.<br>
	 * <br>
	 * Standard version overwrites data 3 times - 520.22-M (E)<br>
	 * Extended version overwrites data 7 times - 520.22-M (ECE)<br>
	 * <br>
	 * Keep in mind that according to the DoD, drives containing top secret 
	 * data are not permitted to be sanitised in this manner; they must be physically 
	 * destroyed, or the disks subjected to degaussing, scrambling completely the 
	 * magnetic patterns used to store data on the disk, rendering the drive itself inoperable.<br> 
	 * <br>
	 * Furhter information on pages 57 and 58 of document 
	 * <a href="http://www.usaid.gov/policy/ads/500/d522022m.pdf">DoD 5220.22-M.pdf</A>.
	 * 
	 * @param fileName the fully qualified name of a file or directory to delete
	 * @param leaveDir if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @param extended if true the extended version of the 
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	public final void wipeDoD(String fileName, boolean leaveDir, boolean extended) throws IOException
	{		
		s_fileName = fileName;
		s_file = new File(s_fileName);
		s_fileSize = s_file.length();
		
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: " + s_fileName);
		
		if (extended)
			writeDoDExtended(fileName, leaveDir);
		else
			writeDoD(fileName, leaveDir);
	}
	


	/**
	 * Implementation of the DoD standard 520.22-M (E):<br>
	 * <ol>
	 * <li>overwrite with a character</li>
  	 * <li>overwrite with it's complement</li>
  	 * <li>overwrite with a random character</li>
	 * </ol>
	 * 
	 * @param fileName the fully qualified name of a file or directory to delete
	 * @param leaveDir if true and if the fileName is a directory, it won't be deleted
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	private final void writeDoD(String fileName, boolean leaveDir) throws IOException
	{
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir)
				listSize--;
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile())
				{
					writeOneByte(BYTE_00);
					writeOneByte(BYTE_FF);
					writeRandom(1);
				}
				unNameAndDelete(s_fileName);
			}
			LOGGER.debug(listSize + " files or directories deleted using wipeDoD in normal mode");
			
		}
		else // it's a single file, not a directory
		{
			writeOneByte(BYTE_00);
			writeOneByte(BYTE_FF);
			writeRandom(1);
			
			unNameAndDelete(s_fileName);
			LOGGER.debug("1 file deleted using wipeDoD in normal mode");
		}
	}
	
	/**
	 * Implementation of the DoD standard 520.22-M (ECE):<br>
	 * <ol>
	 * <li>overwrite with a random character</li>
	 * <li>overwrite with a character</li>
  	 * <li>overwrite with it's complement</li>
  	 * <li>overwrite with a random character</li>
  	 * <li>overwrite with a character</li>
  	 * <li>overwrite with it's complement</li>
  	 * <li>overwrite with a random character</li>
	 * </ol>
	 * 
	 * @param fileName the fully qualified name of a file or directory to delete
	 * @param leaveDir if true and if the <code>fileName</code> is a directory, it won't be deleted
	 * @throws IOException if no file or directory with <code>fileName</code> exists
	 */
	private final void writeDoDExtended(String fileName, boolean leaveDir) throws IOException
	{
		if (s_file.isDirectory())
		{
			final FileList fileList = new FileList();
			final List<String> files = fileList.getFilePathList(fileName);
			int listSize = files.size();
			if(leaveDir)
				listSize--;
			for (int i = 0; i < listSize; i++)
			{
				s_fileName = files.get(i);
				s_file = null;
				s_file = new File(s_fileName);
				s_fileSize = s_file.length(); 
				if(s_file.isFile())
				{
					writeRandom(1);
					writeOneByte(BYTE_55);
					writeOneByte(BYTE_AA);
					writeRandom(1);
					writeOneByte(BYTE_00);
					writeOneByte(BYTE_FF);
					writeRandom(1);
				}
				unNameAndDelete(s_fileName);
			}
			LOGGER.debug(listSize + " files or directories deleted using wipeDoD in extended mode");
			
		}
		else // it's a single file, not a directory
		{
			writeRandom(1);
			writeOneByte(BYTE_55);
			writeOneByte(BYTE_AA);
			writeRandom(1);
			writeOneByte(BYTE_00);
			writeOneByte(BYTE_FF);
			writeRandom(1);
			
			unNameAndDelete(s_fileName);
			LOGGER.debug("1 file deleted using wipeDoD in extended mode");
		}
	}
	
	private final boolean writeRandom(int passes) throws IOException{
		return writeRandom(RANDOM, passes);
	}
	
	private final boolean writeRandom(int mode, int passes) throws IOException
	{
		final int repeat = passes;
		boolean shredOK = true;

		if (mode == RANDOM)
		{
			LOGGER.debug("overwriting data " + repeat + " times in RANDOM mode");
			for (int i = 0; i < repeat; i++)
			{
				shredOK = shredOK && writeRandomBytes();
			}
		}
		else if (mode == RANDOM_SECURE)
		{
			LOGGER.debug("overwriting data " + repeat + " times in RANDOM_SECURE mode");
			for (int i = 0; i < repeat; i++)
			{
				shredOK = shredOK && writeSecureRandomBytes();
			}
		}
		else if (mode == RANDOM_PATTERN)
		{
			LOGGER.debug("overwriting data " + repeat + " times in RANDOM_PATTERN mode");
			for (int i = 0; i < repeat; i++)
			{
				shredOK = shredOK && writeRandomPatterns();
			}	
		}
		else
		{
			shredOK = false;
			LOGGER.error("selected random mode is not supported");
		}
		return shredOK;
	}
	
	
	private final boolean writeOneByte(byte data) throws IOException
	{
		final int arrayLength = 1024;
		final byte[] patternArray = new byte[arrayLength];
		final long loops = s_fileSize / arrayLength;
		final int rest = (int) (s_fileSize % arrayLength);
        Arrays.fill(patternArray, data);
		
        // Open or create the output file
        final FileOutputStream os = new FileOutputStream(s_fileName);

        
        for (long i = 1; i <= loops; i++)
        {
        	os.write(patternArray);
        	if(i % 1024 == 0)
        		os.flush(); // flush each MB to file
        }
        if (rest != 0)
        {
        	os.write(patternArray, 0, rest);
        	os.flush();
        }        
        os.close();
        
		return true;
	}
	
	private final boolean writeThreeBytes(byte[] data) throws IOException
	{
		if (data.length != 3)
			throw new IOException("Use this method only with a 3 bytes pattern!");
		
		final int arrayLength = 1023; // multiple of 3 (else NullpointerException!!!)
		final byte[] patternArray = new byte[arrayLength];
		final long loops = s_fileSize / arrayLength;
		final int rest = (int) (s_fileSize % arrayLength);
        for (int i = 0; i < arrayLength; i = i + 3)
        {
        	patternArray[i] = data[0];
        	patternArray[i+1] = data[1];
        	patternArray[i+2] = data[2];
        }
		
        // Open or create the output file
        final FileOutputStream os = new FileOutputStream(s_fileName);

        for (long i = 1; i <= loops; i++)
        {
        	os.write(patternArray);
        	if(i % 1025 == 0)
        		os.flush(); // flush +- each MB to file (1023*1025)
        }
        if (rest != 0)
        {
        	os.write(patternArray, 0, rest);
        	os.flush();
        }        
        os.close();
		return true;
	}
	
	
	private final boolean writeRandomPatterns() throws IOException{

		final int arrayLength = 1024;
		final byte[] patternArray = new byte[arrayLength];
		final long loops = s_fileSize / arrayLength;
		final int rest = (int) (s_fileSize % arrayLength);
		final Random random = new Random();
        random.nextBytes(patternArray);
		
        // Open or create the output file
        final FileOutputStream os = new FileOutputStream(s_fileName);

        for (long i = 1; i <= loops; i++)
        {
        	os.write(patternArray);
        	if(i % 1024 == 0)
        		os.flush(); // flush each MB to file (1024*1024)
        }
        if (rest != 0)
        {
        	os.write(patternArray, 0, rest);
        	os.flush();
        }        
        os.close();
		return true;
	}
	
	// slow
	private final boolean writeRandomBytes() throws IOException
	{

		final int arrayLength = 1024;
		final byte[] patternArray = new byte[arrayLength];
		final long loops = s_fileSize / arrayLength;
		final int rest = (int) (s_fileSize % arrayLength);
		final Random random = new Random();
		
        // Open or create the output file
        final FileOutputStream os = new FileOutputStream(s_fileName);

        for (long i = 1; i <= loops; i++)
        {
        	random.nextBytes(patternArray);
        	os.write(patternArray);
        	if(i % 1024 == 0)
        		os.flush(); // flush each MB to file (1024*1024)
        }
        if (rest != 0)
        {
        	random.nextBytes(patternArray);
        	os.write(patternArray, 0, rest);
        	os.flush();
        }        
        os.close();
		return true;
	}
	
	// slow and in terms of security (hacking) should not be used
	private final boolean writeSecureRandomBytes() throws IOException
	{

		final int arrayLength = 1024;
		final byte[] patternArray = new byte[arrayLength];
		final long loops = s_fileSize / arrayLength;
		final int rest = (int) (s_fileSize % arrayLength);
        final SecureRandom random = new SecureRandom();
		
        // Open or create the output file
        final FileOutputStream os = new FileOutputStream(s_fileName);

        for (long i = 1; i <= loops; i++)
        {
        	random.nextBytes(patternArray);
        	os.write(patternArray);
        	if(i % 1024 == 0)
        		os.flush(); // flush each MB to file (1024*1024)
        }
        if (rest != 0)
        {
        	random.nextBytes(patternArray);
        	os.write(patternArray, 0, rest);
        	os.flush();
        }        
        os.close();
		return true;
	}
	
	private final void generateSequence(boolean floppyMode)
	{
		if(s_random == null)
		{
			s_random = new SecureRandom();
		}
		
		LOGGER.debug("SecureRandom to permute Gutmann sequences:");
		LOGGER.debug("SecureRandom Provider: "+ s_random.getProvider());
		LOGGER.debug("SecureRandom Algorithm: "+ s_random.getAlgorithm());
		if (floppyMode)
		{   // create sequence for floppy-tailored Gutmann algorithm
			s_sequence = new Integer[]{5, 5, 6, 6, 7, 7, 8, 8, 9, 9};
			LOGGER.debug("Generates sequence for Gutmann's algorithm in floppy mode");
		}			
		else
		{   // create sequence for Gutmann algorithm
			s_sequence = new Integer[]{5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 
					18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
			LOGGER.debug("Generates sequence for Gutmann's algorithm");
		}
		
		// shuffle the sequence using secure random
		final List<Integer> list = Arrays.asList(s_sequence);
		Collections.shuffle(list);
		s_sequence = list.toArray(new Integer[0]);		
	}
	
	protected final boolean unNameAndDelete(String str) throws IOException
	{
		s_file = new File(str);
		int fileNameLength = s_file.getName().length();
		if (! s_file.exists())
			throw new FileNotFoundException("File to delete could not been found: " + s_fileName);
		generateShorterFilename(0);
		while (fileNameLength > 1)
		{
			generateShorterFilename(1);
			fileNameLength--;
		}

		if(!s_file.delete())
			LOGGER.warn("file could not be deleted after renaming");
		return true;
	}
	
	private void generateShorterFilename(int shorten) throws IOException
	{
		// make sure, that the file object corresponds to renamed file
		final String path = s_file.getAbsolutePath();
		s_file = null;
		s_file = new File(path);
		final boolean isFile = s_file.isFile();
		
		// save directory (static part of file path)
		final String filePath = s_file.getParent() + "/";
		
		int nameLength = s_file.getName().length();
		final int point = nameLength - s_file.getName().lastIndexOf(".") - 1;
		nameLength = nameLength - shorten;
		int position = 0;
		String newName = "";
		
		final Random random = new Random();
		int randomInt = 0;
		char randomChar;
		boolean renamed = false;

        while(position < nameLength)
        {
        	if (position == point && isFile)
        	{
        			newName = "." + newName;
        			position++;
        	}
        	else
        	{
        		randomInt = random.nextInt(51);;
            	if (randomInt < 25)
            		randomInt = randomInt + 65;
            	else
            		randomInt = randomInt + 72; // 97 - 25 = 72
            	
            	randomChar = (char)randomInt;
            	newName = randomChar + newName;
            	position++;
        	}
        }
        if ((point == nameLength) && (nameLength != 1) && isFile)
			newName = "." + newName.substring(1, newName.length());
        
        final File test = new File(filePath + newName);
        if (test.exists())
        	generateShorterFilename(shorten);
        else
        {
        	renamed = s_file.renameTo(new File(filePath + newName));
            if (!renamed)
            	throw new IOException("File could not be renamed");
            
            s_file = null;
            s_file = new File (filePath + newName);
        }
	}

}
