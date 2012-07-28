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

package ch.ethz.origo.ataraxis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.ethz.origo.ataraxis.i18n.ResourceBundleTest;
import ch.ethz.origo.ataraxis.misc.*;
import ch.ethz.origo.ataraxis.passwordmanager.AccountEntryTest;
import ch.ethz.origo.ataraxis.passwordmanager.AtaraxisDiskAccessAdapterTest;
import ch.ethz.origo.ataraxis.passwordmanager.GroupEntryTest;
import ch.ethz.origo.ataraxis.passwordmanager.PasswordManagerTest;
import ch.ethz.origo.ataraxis.passwordmanager.XMLHandlerTest;
import ch.ethz.origo.ataraxis.util.AtaraxisHelperTest;
import ch.ethz.origo.ataraxis.util.FileCopyTest;
import ch.ethz.origo.ataraxis.util.FileListTest;
import ch.ethz.origo.ataraxis.crypt.*;


@RunWith(Suite.class)
@SuiteClasses(value={AtaraxisFileComparatorTest.class,
		AtaraxisCrypterTest.class,ErrorHandlingTest.class,CryptTest.class,
		AtaraxisHashCreatorTest.class,
		ShredderTest.class,AtaraxisFileComparatorTest.class,
		AESKeyCreatorTest.class,RSAKeyCreatorTest.class,
		UBERKeyStoreCreatorTest.class,UBERKeyStoreHandlerTest.class,
		AtaraxisHeaderCreatorTest.class,PasswordManagerTest.class,
		PasswordGeneratorTest.class,XMLHandlerTest.class,AtaraxisUpdateInfoTest.class,
		ExceptionTest.class,AtaraxisDiskAccessAdapterTest.class,
		GroupEntryTest.class,AccountEntryTest.class,AtaraxisHeaderParserTest.class,
		FileCopyTest.class,FileListTest.class,ACEncryptOutputStreamTest.class,
		ACDecryptInputStreamTest.class, ResourceBundleTest.class, 
		AtaraxisHelperTest.class, AtaraxisBackupTest.class})
public class AtaraxisTestSuite {

}
