/* ----------------------------------------------------------------------------
 * Copyright 2007 - 2010 Johnny Graber & Andreas Muedespacher
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The AtaraxisHashCreator creates MessageDigests for a given algorithm
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.0
 *
 */
public class AtaraxisHashCreator 
{
	private String s_digestType;
	private MessageDigest s_messageDigest;

	/**
	 * Constructor for the AtaraxisHashCreator.
	 * 
	 * @param digestAlgorithm the appropriate algorithm from HashingDigest
	 * @throws NoSuchAlgorithmException if the digestAlgorithm is not valid
	 */
	public AtaraxisHashCreator(HashingDigest digestAlgorithm) throws NoSuchAlgorithmException
	{		
		switch ( digestAlgorithm )
		{
		case MD5:
			s_digestType = "MD5";  break;
		case SHA_1:
			s_digestType = "SHA-1"; break;
		case SHA_256:
			s_digestType = "SHA-256"; break;
		case SHA_384:
			s_digestType = "SHA-384"; break;
		case SHA_512:
			s_digestType = "SHA-512"; break;
		}
		
		s_messageDigest = MessageDigest.getInstance( s_digestType );
	}

	
	/**
	 * createHashForString() creates a hash value for the inputString
	 *
	 * @param inputString the String who should be hashed
	 */
	public byte[] createHashForString(String inputString)
	{
		// clean the MessageDigest, create the new hash and return it
		s_messageDigest.reset();
		
		return s_messageDigest.digest(inputString.getBytes());	
	}
	
	/**
	 * createHashForFile() creates a hash value for the inputFile
	 *
	 * @param inputFile the File who should be hashed
	 * @throws IOException if the file does not exist or an other IOException happen
	 */
	public byte[] createHashForFile(File inputFile) throws IOException
	{
		byte[] createdHash = null;
		
		InputStream is = new FileInputStream(inputFile);
		createdHash = createHashForInputStream(is);
		is.close();
		
		return createdHash;	
	}
	
	/**
	 * createHashForInputStream() creates a hash value for the inputString
	 *
	 * @param inputSream the Stream who should be hashed
	 * @throws IOException if problems with IO occurrence
	 */
	public byte[] createHashForInputStream(InputStream inputStream) throws IOException
	{
		// clean the MessageDigest, create the new hash and return it
		s_messageDigest.reset();
		
		byte[] buf = new byte[2048];
        int len;
        
        while ((len = inputStream.read(buf)) > 0) 
        {
            s_messageDigest.update(buf, 0, len);
        }
        
        return s_messageDigest.digest();
	}
	
	/**
	 * getDigestLength() return the length of the digest in bytes
	 *
	 * @return the length of the digest
	 */
	public int getDigestLength()
	{
		return s_messageDigest.getDigestLength();
	}
	
	/**
	 * getAlgorithm() return the chosen algorithm
	 *
	 * @return the used algorithm
	 */
	public String getAlgorithm()
	{
		return s_messageDigest.getAlgorithm();
	}
	
	/**
	 * prettyPrintHash() create a pretty formed string from the hash array
	 *
	 * @param inputHash
	 * @return the pretty formed hash
	 */
	public static String prettyPrintHash(byte[] inputHash)
	{
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inputHash.length; ++i) {
            sb.append(
                Integer.toHexString(
                    (inputHash[i] & 0xFF) | 0x100
                ).toLowerCase().substring(1,3)
            );
        }
        
        return sb.toString();
	}
}