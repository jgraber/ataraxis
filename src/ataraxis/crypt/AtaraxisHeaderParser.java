/* ----------------------------------------------------------------------------
 * Copyright 2008 - 2016 Johnny Graber & Andreas Muedespacher
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

package ataraxis.crypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

/**
 * AtaraxisHeaderParser parses the Header to use in the decryption whit an 
 * initial vector for the CBC mode.
 *
 * @author J. Graber
 * @version 1.0
 */
public class AtaraxisHeaderParser 
{

	private String headerVersion = "";
	private byte[] iv;
	private boolean fileContainsHeader = false;
	private List<String> headerLines;

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisHeaderParser.class);

	
	/**
	 * AtaraxisHeaderParser parse the input File.
	 *
	 * @param filetoParse the input File
	 * @throws IOException if the file does not exist or can not be readed
	 */
	public AtaraxisHeaderParser(File filetoParse) throws IOException
	{
		LOGGER.debug("Start AtaraxisHeaderParser() for File "+ filetoParse.getAbsolutePath());
		headerLines = new ArrayList<String>();
		parseFile(filetoParse);

	}

	
	/**
	 * The private method to parse the file.
	 *
	 * @param inFile the input File
	 * @throws IOException if the file does not exist or can not be readed
	 */
	private void parseFile(File inFile) throws IOException
	{
		FileInputStream fis = new FileInputStream(inFile);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String firstLine = br.readLine();

		if(firstLine != null && firstLine.equals("AtaraxiS Header Start"))
		{
			LOGGER.debug("matches AtaraxisHeaderStart");
			headerLines.add(firstLine);

			String curLine = "";

			curLine = br.readLine();
			while(curLine != null && !curLine.equals("AtaraxiS Header End"))
			{
				headerLines.add(curLine);

				LOGGER.debug(curLine);

				if(curLine.startsWith("HeaderVersion:"))
				{
					String[] tempVersion = curLine.split(" ");
					headerVersion = tempVersion[1];
				}
				else if (curLine.startsWith("IV:"))
				{
					String[] tempIV = curLine.split(" ");
					iv = Base64.decode(tempIV[1]);
				}


				curLine = br.readLine();
			}

			if(curLine != null)
			{
				headerLines.add(curLine);
				fileContainsHeader = true;
			}
		}
		fis.close();
	}

	
	/**
	 * It returns the number of bytes between the start of the file and the 
	 * start of the real contend.
	 *
	 * @return nb of bytes to skip
	 */
	public int bytesToSkip()
	{
		ListIterator<String> it = headerLines.listIterator(); 
		int headerSize = 0;
		String tempLine = "";
		while(it.hasNext())
		{
			tempLine = it.next();
			//System.out.println(tempLine);
			headerSize += tempLine.length() + 1;
		}
		
		// +1 for the empty line at the end of the Header, if there are Lines
		if(headerSize != 0)
		{
			headerSize++;
		}
		
		LOGGER.debug("Bytes to skip: " + headerSize);
		
		return headerSize; 
	}

	/**
	 * Returns the Version of the header
	 *
	 * @return the version
	 */
	public String getHeaderVersion()
	{
		return headerVersion;
	}

	
	/**
	 * The InitialVector to decrypt the file
	 *
	 * @return the IV as byte-Array
	 */
	public byte[] getIV()
	{
		return Arrays.copyOf(iv,iv.length);
	}

	/**
	 * Returns true if the File contains an AtaraxisHeader.
	 *
	 * @return true if it contains the header
	 */
	public boolean containsFileHeader()
	{
		return fileContainsHeader;
	}

}
