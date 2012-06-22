/*
   Copyright 2012 Kenta Eguchi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package jp.co.sbc.PackageBrowser;

import android.content.pm.PackageManager;

public final class Defines {
	final static boolean DEBUG = true;
	final static  String KEY_PACKAGENAME = "jp.co.sbc.PackageBrowse.KEY_PACKAGENAME";
	final static int PACAKGEINFO_FLAG =  PackageManager.GET_ACTIVITIES |  PackageManager.GET_GIDS |  PackageManager.GET_CONFIGURATIONS |  PackageManager.GET_INSTRUMENTATION | PackageManager.GET_PERMISSIONS | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES | PackageManager.GET_SIGNATURES | PackageManager.GET_UNINSTALLED_PACKAGES ;




}
