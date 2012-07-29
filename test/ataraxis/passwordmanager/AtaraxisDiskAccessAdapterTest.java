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

package ataraxis.passwordmanager;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import ataraxis.passwordmanager.AtaraxisDiskAccessAdapter;


public class AtaraxisDiskAccessAdapterTest 
{

	@Test(expected=FileNotFoundException.class)
	public void getInputStream_FileDoesNotExist() throws Exception
	{
		AtaraxisDiskAccessAdapter adapter = new AtaraxisDiskAccessAdapter(null);
		adapter.getInputStream(new File("ThisDoesNotExists"));
	}
	
	@Test(expected=FileNotFoundException.class)
	public void getInputStream_FileIsNull() throws Exception
	{
		AtaraxisDiskAccessAdapter adapter = new AtaraxisDiskAccessAdapter(null);
		adapter.getInputStream(null);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void getOutputStream_FileIsNull() throws Exception
	{
		AtaraxisDiskAccessAdapter adapter = new AtaraxisDiskAccessAdapter(null);
		adapter.getOutputStream(null);
	}
}
