/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2015 Johnny Graber & Andreas Muedespacher
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

package ataraxis.special;

import java.io.IOException;

import org.junit.Test;

import ataraxis.crypt.CryptoMethodError;
import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.crypt.NotImplementedException;
import ataraxis.gui.IllegalPasswordException;
import ataraxis.misc.UpdateException;
import ataraxis.passwordmanager.EntryAlreadyExistException;
import ataraxis.passwordmanager.EntryDoesNotExistException;
import ataraxis.passwordmanager.StorageException;


public class ExceptionTest 
{

	@Test(expected=UpdateException.class)
	public void UpdateException_emptyConstructor() throws Exception
	{
		throw new UpdateException();
	}	
	@Test(expected=UpdateException.class)
	public void UpdateException_StringConstructor() throws Exception
	{
		throw new UpdateException("Test");
	}	
	@Test(expected=UpdateException.class)
	public void UpdateException_StringExceptionConstructor() throws Exception
	{
		throw new UpdateException("Test", new IOException());
	}	
	@Test(expected=UpdateException.class)
	public void UpdateException_ExceptionConstructor() throws Exception
	{
		throw new UpdateException(new IOException());
	}
	
	
	@Test(expected=StorageException.class)
	public void StorageException_emptyConstructor() throws Exception
	{
		throw new StorageException();
	}	
	@Test(expected=StorageException.class)
	public void StorageException_StringConstructor() throws Exception
	{
		throw new StorageException("Test");
	}	
	@Test(expected=StorageException.class)
	public void StorageException_StringExceptionConstructor() throws Exception
	{
		throw new StorageException("Test", new IOException());
	}	
	@Test(expected=StorageException.class)
	public void StorageException_ExceptionConstructor() throws Exception
	{
		throw new StorageException(new IOException());
	}

	@Test(expected=EntryAlreadyExistException.class)
	public void EntryAlreadyExistException_emptyConstructor() throws Exception
	{
		throw new EntryAlreadyExistException();
	}	
	@Test(expected=EntryAlreadyExistException.class)
	public void EntryAlreadyExistException_StringConstructor() throws Exception
	{
		throw new EntryAlreadyExistException("Test");
	}	
	@Test(expected=EntryAlreadyExistException.class)
	public void EntryAlreadyExistException_StringExceptionConstructor() throws Exception
	{
		throw new EntryAlreadyExistException("Test", new IOException());
	}	
	@Test(expected=EntryAlreadyExistException.class)
	public void EntryAlreadyExistException_ExceptionConstructor() throws Exception
	{
		throw new EntryAlreadyExistException(new IOException());
	}
	
	@Test(expected=EntryDoesNotExistException.class)
	public void EntryDoesNotExistException_emptyConstructor() throws Exception
	{
		throw new EntryDoesNotExistException();
	}	
	@Test(expected=EntryDoesNotExistException.class)
	public void EntryDoesNotExistException_StringConstructor() throws Exception
	{
		throw new EntryDoesNotExistException("Test");
	}	
	@Test(expected=EntryDoesNotExistException.class)
	public void EntryDoesNotExistException_StringExceptionConstructor() throws Exception
	{
		throw new EntryDoesNotExistException("Test", new IOException());
	}	
	@Test(expected=EntryDoesNotExistException.class)
	public void EntryDoesNotExistException_ExceptionConstructor() throws Exception
	{
		throw new EntryDoesNotExistException(new IOException());
	}
	
	@Test(expected=JurisdictionPolicyError.class)
	public void JurisdictionPolicyError_emptyConstructor() throws Exception
	{
		throw new JurisdictionPolicyError();
	}	
	@Test(expected=JurisdictionPolicyError.class)
	public void JurisdictionPolicyError_StringConstructor() throws Exception
	{
		throw new JurisdictionPolicyError("Test");
	}	
	@Test(expected=JurisdictionPolicyError.class)
	public void JurisdictionPolicyError_StringExceptionConstructor() throws Exception
	{
		throw new JurisdictionPolicyError("Test", new IOException());
	}	
	@Test(expected=JurisdictionPolicyError.class)
	public void JurisdictionPolicyError_ExceptionConstructor() throws Exception
	{
		throw new JurisdictionPolicyError(new IOException());
	}
	
	@Test(expected=CryptoMethodError.class)
	public void CryptoMethodError_emptyConstructor() throws Exception
	{
		throw new CryptoMethodError();
	}	
	@Test(expected=CryptoMethodError.class)
	public void CryptoMethodError_StringConstructor() throws Exception
	{
		throw new CryptoMethodError("Test");
	}	
	@Test(expected=CryptoMethodError.class)
	public void CryptoMethodError_StringExceptionConstructor() throws Exception
	{
		throw new CryptoMethodError("Test", new IOException());
	}	
	@Test(expected=CryptoMethodError.class)
	public void CryptoMethodError_ExceptionConstructor() throws Exception
	{
		throw new CryptoMethodError(new IOException());
	}

	@Test(expected=NotImplementedException.class)
	public void NotImplementedException_emptyConstructor() throws Exception
	{
		throw new NotImplementedException();
	}	
	@Test(expected=NotImplementedException.class)
	public void NotImplementedException_StringConstructor() throws Exception
	{
		throw new NotImplementedException("Test");
	}	
	@Test(expected=NotImplementedException.class)
	public void NotImplementedException_StringExceptionConstructor() throws Exception
	{
		throw new NotImplementedException("Test", new IOException());
	}	
	@Test(expected=NotImplementedException.class)
	public void NotImplementedException_ExceptionConstructor() throws Exception
	{
		throw new NotImplementedException(new IOException());
	}
	
	@Test(expected=IllegalPasswordException.class)
	public void IllegalPasswordException_emptyConstructor() throws Exception
	{
		throw new IllegalPasswordException();
	}	
	@Test(expected=IllegalPasswordException.class)
	public void IllegalPasswordException_StringConstructor() throws Exception
	{
		throw new IllegalPasswordException("Test");
	}	
	@Test(expected=IllegalPasswordException.class)
	public void IllegalPasswordException_StringExceptionConstructor() throws Exception
	{
		throw new IllegalPasswordException("Test", new IOException());
	}	
	@Test(expected=IllegalPasswordException.class)
	public void IllegalPasswordException_ExceptionConstructor() throws Exception
	{
		throw new IllegalPasswordException(new IOException());
	}
}
