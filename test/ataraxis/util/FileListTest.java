/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2010 Johnny Graber & Andreas Muedespacher
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

package ataraxis.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.util.FileList;

public class FileListTest 
{

	private static final Logger logger = LogManager.getLogger(FileCopyTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	
	
	@BeforeClass 
	public static void initClass() throws IOException
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE);
		logger.info("Start AtaraxisHeaderParserTest");
	}
	
	@Test
	public void testGetFilesOnlyPathList()
	{
		FileList fl = new FileList();
		List<String> onlyFiles = fl.getFilesOnlyPathList(TEST_DIR);
		
		assertTrue(onlyFiles.size() > 0);
		
		for(String filePath : onlyFiles)
		{
			File file = new File(filePath);
			assertTrue(file.isFile());
			assertTrue(!file.isDirectory());
		}
	}
	

}
