<?php
/* ----------------------------------------------------------------------------
 * Copyright 2008 - 2010 Johnny Graber & Andreas Muedespacher
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

// Only for development
//error_reporting(E_ALL);

// define some basic vars
$version_is_valid = false;
$old_versions = array("1.0.0", "1.0.1", "1.0.2", "1.1.0", "1.1.1", "1.2.0","1.3.0");
$current_version = "1.3.1";
$next_versions = array("1.4.0", "2.0.0");
$version_addons = array("RC", "DEV");
$version_is_current = false;
$submitted_version = "";
$download_url = "http://ataraxis.origo.ethz.ch/";

// Only check if a version is submitted as parameter "?version=...."
if(isset($_GET['version']))
{

	// get the version parameter and split version part away
	$version_param = rawurldecode(strip_tags($_GET['version']));
	$version_param = trim($version_param);
	$version_regex = '([0-9]{1,2})\.([0-9]{1,2})\.([0-9]{1,2})';	// XX.XX.XX ev.
	$version_array = split(" ", $version_param);
	$version_digits = $version_array[0];
	
	//echo $version_digits;
	
	
	// Check if the version part matches the AtaraxiS Version Schema
	if(ereg($version_regex,$version_digits))
	{
		$version_is_valid = true;
	}


	if($version_is_valid)
	{

		if($version_digits == $current_version)
		{
	
			$version_is_current = true;
			$submitted_version = "current";
		
			//Contains the Version andy version additions like RC or DEV?
			foreach($version_addons as $addon)
			{
				if(strpos($version_param, $addon) != 0 )
				{
					$version_is_current = false;
					$submitted_version = "old";
				}
			}
		}
		else if (in_array($version_digits, $old_versions))
		{
			// is old Version
			$submitted_version = "old";
		}
		else if ( in_array($version_digits, $next_versions))
		{
			// to new do nothing
			$submitted_version = "next";
		}
		else
		{
			// valid format but not definet as one of the current, next or old versions
			$submitted_version = "error";
		}
	}
	else
	{
		$submitted_version = "error";
	}
}
else
{
	// No parameter is submitted
	$submitted_version = "error";
}

// generate the properties file for AtaraxisUpdateInfo
print "current.version = ".$current_version."\n";
print "submitted.version = ".$submitted_version."\n";
print "download.url = ".$download_url;


?>