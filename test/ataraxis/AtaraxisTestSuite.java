/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2010 Johnny Graber & Andreas Muedespacher
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

package ataraxis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ataraxis.crypt.*;
import ataraxis.i18n.ResourceBundleTest;
import ataraxis.misc.*;
import ataraxis.passwordmanager.AccountEntryTest;
import ataraxis.passwordmanager.AtaraxisDiskAccessAdapterTest;
import ataraxis.passwordmanager.GroupEntryTest;
import ataraxis.passwordmanager.PasswordManagerTest;
import ataraxis.passwordmanager.XMLHandlerTest;
import ataraxis.passwordmanager.XMLHandler_JMockit_Test;
import ataraxis.special.*;
import ataraxis.util.AtaraxisHelperTest;
import ataraxis.util.FileCopyTest;
import ataraxis.util.FileListTest;



@RunWith(Suite.class)
@SuiteClasses(value={AtaraxisFileComparatorTest.class,
		AtaraxisCrypterTest.class,AtaraxisCrypter_JMockit_Test.class,ErrorHandlingTest.class,CryptTest.class,
		AtaraxisHashCreatorTest.class,
		ShredderTest.class,AtaraxisFileComparatorTest.class,
		AESKeyCreatorTest.class,RSAKeyCreatorTest.class,
		UBERKeyStoreCreatorTest.class,UBERKeyStoreHandlerTest.class,
		AtaraxisHeaderCreatorTest.class,PasswordManagerTest.class,
		PasswordGeneratorTest.class,XMLHandlerTest.class,XMLHandler_JMockit_Test.class,
		AtaraxisUpdateInfoTest.class,
		ExceptionTest.class,AtaraxisDiskAccessAdapterTest.class,
		GroupEntryTest.class,AccountEntryTest.class,AtaraxisHeaderParserTest.class,
		FileCopyTest.class,FileListTest.class,ACEncryptOutputStreamTest.class,
		ACDecryptInputStreamTest.class, ResourceBundleTest.class, 
		AtaraxisHelperTest.class, AtaraxisBackupTest.class})
public class AtaraxisTestSuite {

}
