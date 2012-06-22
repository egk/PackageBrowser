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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.SimpleExpandableListAdapter;

public class PackageDetailInfoActivity extends ExpandableListActivity  {


	private PackageManager mPackageManager;
	private PackageInfo mPackageInfo;

	private ExpandableListAdapter mAdapter; 
	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String packageName = intent.getStringExtra(Defines.KEY_PACKAGENAME);
		
		
		mPackageManager = getPackageManager();
		Drawable drawable;
		String appname;
		try {
			mPackageInfo = mPackageManager.getPackageInfo(packageName, Defines.PACAKGEINFO_FLAG);			
			drawable = mPackageInfo.applicationInfo.loadIcon(mPackageManager);
			appname = mPackageInfo.applicationInfo.loadLabel(mPackageManager).toString();


		} catch (NameNotFoundException e) {
			e.printStackTrace();
			mPackageInfo = null;
			drawable = null;
			appname = null;
		}

		if(Build.VERSION.SDK_INT >= 11){
			setupActionBar(drawable, appname);
		}
		else {
			setupTitileBar(drawable, appname);

		}
		
		
		 getExpandableListView().setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});
		 
		 getExpandableListView().setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				return false;
			}
		});
		
		 getExpandableListView().setOnItemLongClickListener( new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				return false;
			}
			 
		 
		});
		
		 


	}

	protected void setupActionBar(Drawable icon, String appName){

		ActionBar ac = getActionBar();
		if (icon != null){
			ac.setLogo(icon);
		}

		if (appName != null){
			ac.setTitle(appName);
		}

		//setContentView(R.layout.detail);
		setupPackageInfoList();


	}

	protected void setupTitileBar(Drawable icon, String appName){

		Window w = getWindow();
		if (icon != null){
			w.requestFeature(Window.FEATURE_LEFT_ICON);
		}

		//setContentView(R.layout.detail);
		setupPackageInfoList();

		if (icon != null){
			w.setFeatureDrawable(Window.FEATURE_LEFT_ICON, icon);
		}

		if (appName != null){
			w.setTitle(appName);
		}

	}

	final static String SAVED_ADAPTER = "jp.co.sbc.PackageBrowser.PackageDetailInfoActivity.SAVED_ADAPTER"; 
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// entityをシリアライズしてbyte配列で格納
		byte[] buf = null;
		try {

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
			objectOut.writeObject(mAdapter);
			buf = byteOut.toByteArray();

		} catch ( Exception e ) {
			// 何もしない
		}

		// byte配列で格納
		outState.putByteArray(SAVED_ADAPTER, buf);

	}

	/**
	 * savedInstanceから値を復帰
	 * @param savedInstanceState
	 */
	public void createdFromSavedInstance(Bundle savedInstanceState) {

		// 保存値がある場合、エンティティを復帰
		if ( savedInstanceState != null ) {
			byte[] buf = savedInstanceState.getByteArray(SAVED_ADAPTER);
			if ( buf != null && buf.length > 0 ){
				try {

					ByteArrayInputStream byteInput = new ByteArrayInputStream(buf);
					ObjectInputStream objectInput = new ObjectInputStream(byteInput);
					mAdapter = (ExpandableListAdapter) objectInput.readObject();

				} catch ( Exception e ) {
					// 何もしない
				}
			}
		}
	}
	
	
	final static String CATEGORY_NAME = "jp.co.sbc.PackageBrowser.PackageDetailInfoActivity.CATEGORY_NAME"; 
	final static String ELEMENT_NAME = "jp.co.sbc.PackageBrowser.PackageDetailInfoActivity.ELEMENT_NAME"; 

	
	protected void getPackageDetailInfo(List<Map<String, String>> groupData, List<List<Map<String, String>>> childData){
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		// パッケージ情報
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, String> packageInfo = new HashMap<String, String>();
		packageInfo.put(CATEGORY_NAME,"パッケージ情報");
		groupData.add(packageInfo);

		List<Map<String, String>> packageInfoChildren = new ArrayList<Map<String, String>>();

		///////////////////////////////////////////////////////////////////
		// パッケージ名
		///////////////////////////////////////////////////////////////////
		Map<String, String> packageNameEelement = new HashMap<String, String>();  
		packageNameEelement.put(CATEGORY_NAME, "パッケージ名");
		packageNameEelement.put(ELEMENT_NAME, mPackageInfo.packageName);
		packageInfoChildren.add(packageNameEelement);

		///////////////////////////////////////////////////////////////////
		// インストール日時
		///////////////////////////////////////////////////////////////////
		Map<String, String> firstInstallTimeEelement = new HashMap<String, String>(); 

		Date installdate = new Date(mPackageInfo.firstInstallTime);
		java.text.DateFormat datefomat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

		firstInstallTimeEelement.put(CATEGORY_NAME, "インストール日時");
		firstInstallTimeEelement.put(ELEMENT_NAME, datefomat.format(installdate));
		packageInfoChildren.add(firstInstallTimeEelement);

		///////////////////////////////////////////////////////////////////
		// アップデート日時
		///////////////////////////////////////////////////////////////////
		Map<String, String> lastUpdateTimeEelement = new HashMap<String, String>(); 
		Date lastUpdateDate = new Date(mPackageInfo.lastUpdateTime);
		lastUpdateTimeEelement.put(CATEGORY_NAME, "アップデート日時");
		lastUpdateTimeEelement.put(ELEMENT_NAME, datefomat.format(lastUpdateDate));
		packageInfoChildren.add(lastUpdateTimeEelement);

		///////////////////////////////////////////////////////////////////
		// android::sharedUserId
		///////////////////////////////////////////////////////////////////
		Map<String, String> sharedUserIdEelement = new HashMap<String, String>();  
		sharedUserIdEelement.put(CATEGORY_NAME, "sharedUserId");
		sharedUserIdEelement.put(ELEMENT_NAME, mPackageInfo.sharedUserId);
		packageInfoChildren.add(sharedUserIdEelement);
				
		
		///////////////////////////////////////////////////////////////////
		// アクティビティ
		///////////////////////////////////////////////////////////////////
		Map<String, String> activityEelement = new HashMap<String, String>(); 
		activityEelement.put(CATEGORY_NAME, "Acitivity数");

		ActivityInfo[] activity = mPackageInfo.activities;
		int activitycount = 0;
		if (activity != null) {
			activitycount = activity.length;
		}

		activityEelement.put(ELEMENT_NAME, Integer.toString(activitycount) );
		packageInfoChildren.add(activityEelement);		        


		///////////////////////////////////////////////////////////////////
		// プロバイダ
		///////////////////////////////////////////////////////////////////
		Map<String, String> providerEelement = new HashMap<String, String>(); 
		providerEelement.put(CATEGORY_NAME, "Provider数");

		ProviderInfo[] providers = mPackageInfo.providers;
		int providerscount = 0;
		if (providers != null) {
			providerscount = providers.length;
		}

		providerEelement.put(ELEMENT_NAME, Integer.toString(providerscount) );
		packageInfoChildren.add(providerEelement);		        

		///////////////////////////////////////////////////////////////////
		// レシーバー
		///////////////////////////////////////////////////////////////////
		Map<String, String> receiverEelement = new HashMap<String, String>(); 
		receiverEelement.put(CATEGORY_NAME, "Receiver数");	
		ActivityInfo[] receivers = mPackageInfo.receivers;
		int receiverscount = 0;
		if (receivers != null) {
			receiverscount = receivers.length;
		}
		
		receiverEelement.put(ELEMENT_NAME, Integer.toString(receiverscount) );
		packageInfoChildren.add(receiverEelement);		
		
		
		///////////////////////////////////////////////////////////////////
		// サービス
		///////////////////////////////////////////////////////////////////
		Map<String, String> serviceEelement = new HashMap<String, String>(); 
		serviceEelement.put(CATEGORY_NAME, "Service数");	
		ServiceInfo[] services = mPackageInfo.services;
		int servicescount = 0;
		if (services != null) {
			servicescount = services.length;
		}
		
		serviceEelement.put(ELEMENT_NAME, Integer.toString(servicescount) );
		packageInfoChildren.add(serviceEelement);		
		
		///////////////////////////////////////////////////////////////////
		// パーミッション
		///////////////////////////////////////////////////////////////////
		Map<String, String> permissionEelement = new HashMap<String, String>(); 
		permissionEelement.put(CATEGORY_NAME, "Permission数");	
		PermissionInfo[] permissions = mPackageInfo.permissions;
		int permissioncount = 0;
		if (permissions != null) {
			permissioncount = permissions.length;
		}
		
		permissionEelement.put(ELEMENT_NAME, Integer.toString(permissioncount) );
		packageInfoChildren.add(permissionEelement);		
		
		
		childData.add(packageInfoChildren);
	}
	
	protected void getActivityDetailInfo(List<Map<String, String>> groupData, List<List<Map<String, String>>> childData){
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		// アクティビティ情報
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		ActivityInfo[] activity = mPackageInfo.activities;
		int activitycount = 0;
		if (activity != null) {
			activitycount = activity.length;
		}
		
		for (int i = 0; i < activitycount; i++){

			Map<String, String> activityInfo = new HashMap<String, String>();
			activityInfo.put(CATEGORY_NAME,"Activity (" + (activity[i].name) + ")");
			groupData.add(activityInfo);		        
			List<Map<String, String>> activityInfoChildren = new ArrayList<Map<String, String>>();

			///////////////////////////////////////////////////////////////////
			// android:label
			///////////////////////////////////////////////////////////////////
			Map<String, String> activityLabelEelement = new HashMap<String, String>();  
			activityLabelEelement.put(CATEGORY_NAME, "android:label");
			activityLabelEelement.put(ELEMENT_NAME, activity[i].loadLabel(mPackageManager).toString());
			activityInfoChildren.add(activityLabelEelement);
			///////////////////////////////////////////////////////////////////
			// android:name
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> activityNameEelement = new HashMap<String, String>();  
			activityNameEelement.put(CATEGORY_NAME, "android:name");
			activityNameEelement.put(ELEMENT_NAME, activity[i].name);
			activityInfoChildren.add(activityNameEelement);
			///////////////////////////////////////////////////////////////////
			// android:allowTaskReparenting
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> allowTaskReparentingEelement = new HashMap<String, String>();  
			allowTaskReparentingEelement.put(CATEGORY_NAME, "android:allowTaskReparenting");
			allowTaskReparentingEelement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_ALLOW_TASK_REPARENTING) == ActivityInfo.FLAG_ALLOW_TASK_REPARENTING) ? "true" : "false"));
			activityInfoChildren.add(allowTaskReparentingEelement);		
			///////////////////////////////////////////////////////////////////
			// android:alwaysRetainTaskState
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> alwaysRetainTaskStateEelement = new HashMap<String, String>();  
			alwaysRetainTaskStateEelement.put(CATEGORY_NAME, "android:alwaysRetainTaskState");
			alwaysRetainTaskStateEelement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_ALWAYS_RETAIN_TASK_STATE) == ActivityInfo.FLAG_ALWAYS_RETAIN_TASK_STATE) ? "true" : "false"));
			activityInfoChildren.add(alwaysRetainTaskStateEelement);		
			///////////////////////////////////////////////////////////////////
			// android:clearTaskOnLaunch
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> clearTaskOnLaunchElement = new HashMap<String, String>();  
			clearTaskOnLaunchElement.put(CATEGORY_NAME, "android:clearTaskOnLaunch");
			clearTaskOnLaunchElement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_CLEAR_TASK_ON_LAUNCH) == ActivityInfo.FLAG_CLEAR_TASK_ON_LAUNCH) ? "true" : "false"));
			activityInfoChildren.add(clearTaskOnLaunchElement);												
			///////////////////////////////////////////////////////////////////
			// android:configChanges
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> configChangesEelement = new HashMap<String, String>();  
			configChangesEelement.put(CATEGORY_NAME, "android:configChanges");
			String configChangesString = "";  
	        final int configChanges = activity[i].configChanges;
        			        
	        if ((configChanges & ActivityInfo.CONFIG_MCC) == ActivityInfo.CONFIG_MCC ){
	        	configChangesString += "mcc ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_MNC) == ActivityInfo.CONFIG_MNC ){
	        	configChangesString += "mnc ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_LOCALE) == ActivityInfo.CONFIG_LOCALE ){
	        	configChangesString += "locale ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_TOUCHSCREEN) == ActivityInfo.CONFIG_TOUCHSCREEN ){
	        	configChangesString += "touchscreen ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_KEYBOARD) == ActivityInfo.CONFIG_KEYBOARD ){
	        	configChangesString += "keyboard ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_KEYBOARD_HIDDEN) == ActivityInfo.CONFIG_KEYBOARD_HIDDEN ){
	        	configChangesString += " keyboardHidden ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_NAVIGATION) == ActivityInfo.CONFIG_NAVIGATION ){
	        	configChangesString += "avigation ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_SCREEN_LAYOUT) == ActivityInfo.CONFIG_SCREEN_LAYOUT ){
	        	configChangesString += "creenLayout ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_FONT_SCALE) == ActivityInfo.CONFIG_FONT_SCALE ){
	        	configChangesString += "fontScale ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_UI_MODE) == ActivityInfo.CONFIG_UI_MODE ){
	        	configChangesString += "uiMode ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_ORIENTATION) == ActivityInfo.CONFIG_ORIENTATION ){
	        	configChangesString += "orientation ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_SCREEN_SIZE) == ActivityInfo.CONFIG_SCREEN_SIZE ){
	        	configChangesString += "screenSize ";
	        }
	        if ((configChanges & ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE) == ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE ){
	        	configChangesString += "smallestScreenSize ";
	        }
			
			configChangesEelement.put(ELEMENT_NAME, configChangesString);
			activityInfoChildren.add(configChangesEelement);		
			///////////////////////////////////////////////////////////////////
			// android:enabled
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> enabledElement = new HashMap<String, String>();  
			enabledElement.put(CATEGORY_NAME, "android:enabled");
			enabledElement.put(ELEMENT_NAME, (activity[i].enabled ? "true" : "false"));
			activityInfoChildren.add(enabledElement);	
			///////////////////////////////////////////////////////////////////
			// android:excludeFromRecents
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> excludeFromRecentsElement = new HashMap<String, String>();  
			excludeFromRecentsElement.put(CATEGORY_NAME, "android:excludeFromRecents");
			excludeFromRecentsElement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS) == ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS) ? "true" : "false"));
			activityInfoChildren.add(excludeFromRecentsElement);						
			///////////////////////////////////////////////////////////////////
			// android:exported
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> exportedElement = new HashMap<String, String>();  
			exportedElement.put(CATEGORY_NAME, "android:exported");
			exportedElement.put(ELEMENT_NAME, (activity[i].exported ? "true" : "false"));
			activityInfoChildren.add(exportedElement);						
			///////////////////////////////////////////////////////////////////
			// android:finishOnTaskLaunch
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> finishOnTaskLaunchElement = new HashMap<String, String>();  
			finishOnTaskLaunchElement.put(CATEGORY_NAME, "android:finishOnTaskLaunch");
			finishOnTaskLaunchElement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS) == ActivityInfo.FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS) ? "true" : "false"));
			activityInfoChildren.add(finishOnTaskLaunchElement);				
			///////////////////////////////////////////////////////////////////
			// android:hardwareAccelerated
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> hardwareAcceleratedElement = new HashMap<String, String>();  
			hardwareAcceleratedElement.put(CATEGORY_NAME, "android:hardwareAccelerated");
			hardwareAcceleratedElement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) == ActivityInfo.FLAG_HARDWARE_ACCELERATED) ? "true" : "false"));
			activityInfoChildren.add(hardwareAcceleratedElement);					
			///////////////////////////////////////////////////////////////////
			// android:launchMode
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> launchModeElement = new HashMap<String, String>();  
			launchModeElement.put(CATEGORY_NAME, "android:launchMode");
			String launchModeString = "";  
	        if ((activity[i].launchMode & ActivityInfo.LAUNCH_MULTIPLE) == ActivityInfo.LAUNCH_MULTIPLE ){
	        	launchModeString += "multiple ";
	        }
	        if ((activity[i].launchMode & ActivityInfo.LAUNCH_SINGLE_TOP) == ActivityInfo.LAUNCH_SINGLE_TOP ){
	        	launchModeString += "singleTop ";
	        }
	        if ((activity[i].launchMode & ActivityInfo.LAUNCH_SINGLE_TASK) == ActivityInfo.LAUNCH_SINGLE_TASK ){
	        	launchModeString += "singleTask ";
	        }
	        if ((activity[i].launchMode & ActivityInfo.LAUNCH_SINGLE_INSTANCE) == ActivityInfo.LAUNCH_SINGLE_INSTANCE ){
	        	launchModeString += "singleInstance ";
	        }
			launchModeElement.put(ELEMENT_NAME, launchModeString);
			activityInfoChildren.add(launchModeElement);	
			///////////////////////////////////////////////////////////////////
			// android:multiprocess
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> multiprocessElement = new HashMap<String, String>();  
			multiprocessElement.put(CATEGORY_NAME, "android:multiprocess");
			multiprocessElement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_MULTIPROCESS) == ActivityInfo.FLAG_MULTIPROCESS) ? "true" : "false"));
			activityInfoChildren.add(multiprocessElement);					
			///////////////////////////////////////////////////////////////////
			// android:noHistory
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> noHistoryElement = new HashMap<String, String>();  
			noHistoryElement.put(CATEGORY_NAME, "android:noHistory");
			noHistoryElement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_NO_HISTORY) == ActivityInfo.FLAG_NO_HISTORY) ? "true" : "false"));
			activityInfoChildren.add(noHistoryElement);						
			///////////////////////////////////////////////////////////////////
			// android:permission
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> permissionEelement = new HashMap<String, String>();  
			permissionEelement.put(CATEGORY_NAME, "android:permission");
			permissionEelement.put(ELEMENT_NAME, activity[i].permission);
			activityInfoChildren.add(permissionEelement);
			///////////////////////////////////////////////////////////////////
			// android:process
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> processNameEelement = new HashMap<String, String>();  
			processNameEelement.put(CATEGORY_NAME, "android:process");
			processNameEelement.put(ELEMENT_NAME, activity[i].processName);
			activityInfoChildren.add(processNameEelement);					
			///////////////////////////////////////////////////////////////////
			// android:screenOrientation
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> screenOrientationEelement = new HashMap<String, String>();  
			screenOrientationEelement.put(CATEGORY_NAME, "android:screenOrientation");
			String screenOrientationString = "";  
	        final int screenOrientation = activity[i].screenOrientation;
        			        
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ){
	        	screenOrientationString += "unspecified ";
	        }
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_USER) == ActivityInfo.SCREEN_ORIENTATION_USER ){
	        	screenOrientationString += "user ";
	        }
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_BEHIND) == ActivityInfo.SCREEN_ORIENTATION_BEHIND ){
	        	screenOrientationString += "behind ";
	        }
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ){
	        	screenOrientationString += "landscape ";
	        }
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ){
	        	screenOrientationString += "portrait ";
	        }
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE ){
	        	screenOrientationString += "reverseLandscape ";
	        }
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT ){
	        	screenOrientationString += "reversePortrait ";
	        }
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE ){
	        	screenOrientationString += "sensorLandscape ";
	        }					
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT ){
	        	screenOrientationString += "sensorPortrait ";
	        }	
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_SENSOR) == ActivityInfo.SCREEN_ORIENTATION_SENSOR ){
	        	screenOrientationString += "sensor ";
	        }				        
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR) == ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR ){
	        	screenOrientationString += "fullSensor ";
	        }				        
	        if ((screenOrientation & ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) == ActivityInfo.SCREEN_ORIENTATION_NOSENSOR ){
	        	screenOrientationString += "nosensor ";
	        }				        
	        screenOrientationEelement.put(ELEMENT_NAME, screenOrientationString);
			activityInfoChildren.add(screenOrientationEelement);
			///////////////////////////////////////////////////////////////////
			// android:stateNotNeeded
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> stateNotNeededElement = new HashMap<String, String>();  
			stateNotNeededElement.put(CATEGORY_NAME, "android:stateNotNeeded");
			stateNotNeededElement.put(ELEMENT_NAME, 
					(((activity[i].flags & ActivityInfo.FLAG_STATE_NOT_NEEDED) == ActivityInfo.FLAG_STATE_NOT_NEEDED) ? "true" : "false"));
			activityInfoChildren.add(stateNotNeededElement);							
			///////////////////////////////////////////////////////////////////
			// android:taskAffinity
			///////////////////////////////////////////////////////////////////			
			Map<String, String> taskAffinityEelement = new HashMap<String, String>();  
			taskAffinityEelement.put(CATEGORY_NAME, "android:taskAffinity");
			taskAffinityEelement.put(ELEMENT_NAME, activity[i].taskAffinity);
			activityInfoChildren.add(taskAffinityEelement);
			///////////////////////////////////////////////////////////////////
			// android:uiOptions
			///////////////////////////////////////////////////////////////////			        
			if (Build.VERSION.SDK_INT >= 14){
				Map<String, String> uiOptionsElement = new HashMap<String, String>();  
				uiOptionsElement.put(CATEGORY_NAME, "android:uiOptions");
				String uiOptionsString = "";  
		        if ((activity[i].uiOptions & ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW) == ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW ){
		        	configChangesString += "splitActionBarWhenNarrow";
		        }
		        else{
		        	configChangesString += "none";			        	
		        }
		        uiOptionsElement.put(ELEMENT_NAME, uiOptionsString);
				activityInfoChildren.add(uiOptionsElement);
			}
			///////////////////////////////////////////////////////////////////
			// android:windowSoftInputMode
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> windowSoftInputModeEelement = new HashMap<String, String>();  
			windowSoftInputModeEelement.put(CATEGORY_NAME, "android:windowSoftInputMode");
			String windowSoftInputModeString = "";  
	        final int windowSoftInputMode = activity[i].softInputMode;
        			        
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) == LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED ){
	        	windowSoftInputModeString += "stateUnspecified ";
	        }
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_STATE_UNCHANGED) == LayoutParams.SOFT_INPUT_STATE_UNCHANGED ){
	        	windowSoftInputModeString += "stateUnchanged ";
	        }
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_STATE_HIDDEN) == LayoutParams.SOFT_INPUT_STATE_HIDDEN ){
	        	windowSoftInputModeString += "stateHidden ";
	        }
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) == LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN ){
	        	windowSoftInputModeString += "stateAlwaysHidden ";
	        }
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_STATE_VISIBLE) == LayoutParams.SOFT_INPUT_STATE_VISIBLE ){
	        	windowSoftInputModeString += "stateVisible ";
	        }
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE) == LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE ){
	        	windowSoftInputModeString += "stateAlwaysVisible ";
	        }
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED) == LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED ){
	        	windowSoftInputModeString += "adjustUnspecified ";
	        }
	        if ((windowSoftInputMode & LayoutParams.SOFT_INPUT_ADJUST_PAN) == LayoutParams.SOFT_INPUT_ADJUST_PAN ){
	        	windowSoftInputModeString += "adjustPan ";
	        }					
	        windowSoftInputModeEelement.put(ELEMENT_NAME, windowSoftInputModeString);
			activityInfoChildren.add(windowSoftInputModeEelement);	
			///////////////////////////////////////////////////////////////////
			// android:targetActivity
			///////////////////////////////////////////////////////////////////			
			Map<String, String> targetActivityEelement = new HashMap<String, String>();  
			targetActivityEelement.put(CATEGORY_NAME, "android:targetActivity");
			targetActivityEelement.put(ELEMENT_NAME, activity[i].targetActivity);
			activityInfoChildren.add(targetActivityEelement);							

			
			childData.add(activityInfoChildren);

		}
	}
	
	
	protected void getProviderDetailInfo(List<Map<String, String>> groupData, List<List<Map<String, String>>> childData){
		ProviderInfo[] provider = mPackageInfo.providers;
		int providercount = 0;
		if (provider != null) {
			providercount = provider.length;
		}
		
		for (int i = 0; i < providercount; i++){

			Map<String, String> providerInfo = new HashMap<String, String>();
			providerInfo.put(CATEGORY_NAME,"Provider (" + (provider[i].name) + ")");
			groupData.add(providerInfo);		        
			List<Map<String, String>> providerInfoChildren = new ArrayList<Map<String, String>>();
		
		
			///////////////////////////////////////////////////////////////////
			// android:label
			///////////////////////////////////////////////////////////////////
			Map<String, String> providerLabelEelement = new HashMap<String, String>();  
			providerLabelEelement.put(CATEGORY_NAME, "android:label");
			providerLabelEelement.put(ELEMENT_NAME, provider[i].loadLabel(mPackageManager).toString());
			providerInfoChildren.add(providerLabelEelement);
		
			///////////////////////////////////////////////////////////////////
			// android:name
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> providerNameEelement = new HashMap<String, String>();  
			providerNameEelement.put(CATEGORY_NAME, "android:name");
			providerNameEelement.put(ELEMENT_NAME, provider[i].name);
			providerInfoChildren.add(providerNameEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:authorities
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> authoritiesNameEelement = new HashMap<String, String>();  
			authoritiesNameEelement.put(CATEGORY_NAME, "android:authorities");
			authoritiesNameEelement.put(ELEMENT_NAME, provider[i].authority);
			providerInfoChildren.add(authoritiesNameEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:enabled
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> enabledElement = new HashMap<String, String>();  
			enabledElement.put(CATEGORY_NAME, "android:enabled");
			enabledElement.put(ELEMENT_NAME, (provider[i].enabled ? "true" : "false"));
			providerInfoChildren.add(enabledElement);	

			///////////////////////////////////////////////////////////////////
			// android:exported
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> exportedElement = new HashMap<String, String>();  
			exportedElement.put(CATEGORY_NAME, "android:exported");
			exportedElement.put(ELEMENT_NAME, (provider[i].exported ? "true" : "false"));
			providerInfoChildren.add(exportedElement);
					
			///////////////////////////////////////////////////////////////////
			// android:grantUriPermissions
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> grantUriPermissionsElement = new HashMap<String, String>();  
			grantUriPermissionsElement.put(CATEGORY_NAME, "android:grantUriPermissions");
			grantUriPermissionsElement.put(ELEMENT_NAME, 
					(provider[i].grantUriPermissions ? "true" : "false"));
			providerInfoChildren.add(grantUriPermissionsElement);	

			///////////////////////////////////////////////////////////////////
			// android:initOrder
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> initOrderElement = new HashMap<String, String>();  
			initOrderElement.put(CATEGORY_NAME, "android:initOrder");
			initOrderElement.put(ELEMENT_NAME, String.valueOf(provider[i].initOrder));
			providerInfoChildren.add(initOrderElement);			
			
			///////////////////////////////////////////////////////////////////
			// android:multiprocess
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> multiprocessElement = new HashMap<String, String>();  
			multiprocessElement.put(CATEGORY_NAME, "android:multiprocess");
			multiprocessElement.put(ELEMENT_NAME, (provider[i].multiprocess ? "true" : "false"));
			providerInfoChildren.add(multiprocessElement);				
			
			///////////////////////////////////////////////////////////////////
			// android:permission
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> permissionEelement = new HashMap<String, String>();  
			permissionEelement.put(CATEGORY_NAME, "android:permission");
			permissionEelement.put(ELEMENT_NAME, provider[i].applicationInfo.permission);
			providerInfoChildren.add(permissionEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:process
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> processNameEelement = new HashMap<String, String>();  
			processNameEelement.put(CATEGORY_NAME, "android:process");
			processNameEelement.put(ELEMENT_NAME, provider[i].processName);
			providerInfoChildren.add(processNameEelement);				
			
			///////////////////////////////////////////////////////////////////
			// android:readPermission
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> readPermissionEelement = new HashMap<String, String>();  
			readPermissionEelement.put(CATEGORY_NAME, "android:readPermission");
			readPermissionEelement.put(ELEMENT_NAME, provider[i].readPermission);
			providerInfoChildren.add(readPermissionEelement);			
			
			///////////////////////////////////////////////////////////////////
			// android:syncable
			///////////////////////////////////////////////////////////////////			        
			//Map<String, String> syncableElement = new HashMap<String, String>();  
			//syncableElement.put(CATEGORY_NAME, "android:syncable");
			//syncableElement.put(ELEMENT_NAME, (provider[i].isSyncable ? "true" : "false"));
			//providerInfoChildren.add(syncableElement);				
			
			///////////////////////////////////////////////////////////////////
			// android:writePermission
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> writePermissionEelement = new HashMap<String, String>();  
			writePermissionEelement.put(CATEGORY_NAME, "android:writePermission");
			writePermissionEelement.put(ELEMENT_NAME, provider[i].writePermission);
			providerInfoChildren.add(writePermissionEelement);				
			
			
			childData.add(providerInfoChildren);
			
		}
		
	}
	
	
	protected void getReceiverDetailInfo(List<Map<String, String>> groupData, List<List<Map<String, String>>> childData){
		ActivityInfo[] receivers = mPackageInfo.receivers;
		int receiverscount = 0;
		if (receivers != null) {
			receiverscount = receivers.length;
		}
		
		for (int i = 0; i < receiverscount; i++){

			Map<String, String> receiversInfo = new HashMap<String, String>();
			receiversInfo.put(CATEGORY_NAME,"Provider (" + (receivers[i].name) + ")");
			groupData.add(receiversInfo);		        
			List<Map<String, String>> receiversInfoChildren = new ArrayList<Map<String, String>>();
	
		
			///////////////////////////////////////////////////////////////////
			// android:label
			///////////////////////////////////////////////////////////////////
			Map<String, String> labelEelement = new HashMap<String, String>();  
			labelEelement.put(CATEGORY_NAME, "android:label");
			labelEelement.put(ELEMENT_NAME, receivers[i].loadLabel(mPackageManager).toString());
			receiversInfoChildren.add(labelEelement);
		
			///////////////////////////////////////////////////////////////////
			// android:name
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> nameEelement = new HashMap<String, String>();  
			nameEelement.put(CATEGORY_NAME, "android:name");
			nameEelement.put(ELEMENT_NAME, receivers[i].name);
			receiversInfoChildren.add(nameEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:enabled
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> enabledElement = new HashMap<String, String>();  
			enabledElement.put(CATEGORY_NAME, "android:enabled");
			enabledElement.put(ELEMENT_NAME, (receivers[i].enabled ? "true" : "false"));
			receiversInfoChildren.add(enabledElement);	

			///////////////////////////////////////////////////////////////////
			// android:exported
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> exportedElement = new HashMap<String, String>();  
			exportedElement.put(CATEGORY_NAME, "android:exported");
			exportedElement.put(ELEMENT_NAME, (receivers[i].exported ? "true" : "false"));
			receiversInfoChildren.add(exportedElement);
			
			///////////////////////////////////////////////////////////////////
			// android:permission
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> permissionEelement = new HashMap<String, String>();  
			permissionEelement.put(CATEGORY_NAME, "android:permission");
			permissionEelement.put(ELEMENT_NAME, receivers[i].permission);
			receiversInfoChildren.add(permissionEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:process
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> processNameEelement = new HashMap<String, String>();  
			processNameEelement.put(CATEGORY_NAME, "android:process");
			processNameEelement.put(ELEMENT_NAME, receivers[i].processName);
			receiversInfoChildren.add(processNameEelement);				
			
			
		
			childData.add(receiversInfoChildren);
		}
	}
	
	protected void getServiceDetailInfo(List<Map<String, String>> groupData, List<List<Map<String, String>>> childData){
		ServiceInfo[] services = mPackageInfo.services;
		int count = 0;
		if (services != null) {
			count = services.length;
		}
		
		for (int i = 0; i < count; i++){

			Map<String, String> servicesInfo = new HashMap<String, String>();
			servicesInfo.put(CATEGORY_NAME,"Service (" + (services[i].name) + ")");
			groupData.add(servicesInfo);		        
			List<Map<String, String>> servicesInfoChildren = new ArrayList<Map<String, String>>();
	
		
			///////////////////////////////////////////////////////////////////
			// android:label
			///////////////////////////////////////////////////////////////////
			Map<String, String> labelEelement = new HashMap<String, String>();  
			labelEelement.put(CATEGORY_NAME, "android:label");
			labelEelement.put(ELEMENT_NAME, services[i].loadLabel(mPackageManager).toString());
			servicesInfoChildren.add(labelEelement);
		
			///////////////////////////////////////////////////////////////////
			// android:name
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> nameEelement = new HashMap<String, String>();  
			nameEelement.put(CATEGORY_NAME, "android:name");
			nameEelement.put(ELEMENT_NAME, services[i].name);
			servicesInfoChildren.add(nameEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:enabled
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> enabledElement = new HashMap<String, String>();  
			enabledElement.put(CATEGORY_NAME, "android:enabled");
			enabledElement.put(ELEMENT_NAME, (services[i].enabled ? "true" : "false"));
			servicesInfoChildren.add(enabledElement);	

			///////////////////////////////////////////////////////////////////
			// android:exported
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> exportedElement = new HashMap<String, String>();  
			exportedElement.put(CATEGORY_NAME, "android:exported");
			exportedElement.put(ELEMENT_NAME, (services[i].exported ? "true" : "false"));
			servicesInfoChildren.add(exportedElement);
			
			///////////////////////////////////////////////////////////////////
			// android:permission
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> permissionEelement = new HashMap<String, String>();  
			permissionEelement.put(CATEGORY_NAME, "android:permission");
			permissionEelement.put(ELEMENT_NAME, services[i].permission);
			servicesInfoChildren.add(permissionEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:process
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> processNameEelement = new HashMap<String, String>();  
			processNameEelement.put(CATEGORY_NAME, "android:process");
			processNameEelement.put(ELEMENT_NAME, services[i].processName);
			servicesInfoChildren.add(processNameEelement);				
			
			
		
			childData.add(servicesInfoChildren);
		}
	}
	
	protected void getPermissionDetailInfo(List<Map<String, String>> groupData, List<List<Map<String, String>>> childData){
		PermissionInfo[] permissions = mPackageInfo.permissions;
		int count = 0;
		if (permissions != null) {
			count = permissions.length;
		}
		
		for (int i = 0; i < count; i++){

			Map<String, String> servicesInfo = new HashMap<String, String>();
			servicesInfo.put(CATEGORY_NAME,"Permission (" + (permissions[i].name) + ")");
			groupData.add(servicesInfo);		        
			List<Map<String, String>> permissionInfoChildren = new ArrayList<Map<String, String>>();
	
		
			///////////////////////////////////////////////////////////////////
			// android:label
			///////////////////////////////////////////////////////////////////
			Map<String, String> labelEelement = new HashMap<String, String>();  
			labelEelement.put(CATEGORY_NAME, "android:label");
			labelEelement.put(ELEMENT_NAME, permissions[i].loadLabel(mPackageManager).toString());
			permissionInfoChildren.add(labelEelement);
		
			///////////////////////////////////////////////////////////////////
			// android:name
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> nameEelement = new HashMap<String, String>();  
			nameEelement.put(CATEGORY_NAME, "android:name");
			nameEelement.put(ELEMENT_NAME, permissions[i].name);
			permissionInfoChildren.add(nameEelement);
			
			///////////////////////////////////////////////////////////////////
			// android:description
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> descriptionElement = new HashMap<String, String>();  
			descriptionElement.put(CATEGORY_NAME, "android:description");
			CharSequence description = permissions[i].loadDescription(mPackageManager);
			descriptionElement.put(ELEMENT_NAME, ((description != null) ? description.toString() : ""));
			permissionInfoChildren.add(descriptionElement);	

			///////////////////////////////////////////////////////////////////
			// android:permissionGroup
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> permissionGroupElement = new HashMap<String, String>();  
			permissionGroupElement.put(CATEGORY_NAME, "android:permissionGroup");
			permissionGroupElement.put(ELEMENT_NAME, permissions[i].group);
			permissionInfoChildren.add(permissionGroupElement);	
			
			///////////////////////////////////////////////////////////////////
			// android:protectionLevel
			///////////////////////////////////////////////////////////////////			        
			Map<String, String> protectionLevelElement = new HashMap<String, String>();  
			protectionLevelElement.put(CATEGORY_NAME, "android:protectionLevel");
			String protectionLevelString = "";  
	        if ((permissions[i].protectionLevel & PermissionInfo.PROTECTION_NORMAL) == PermissionInfo.PROTECTION_NORMAL){
	        	protectionLevelString += "normal ";
	        }
	        if ((permissions[i].protectionLevel & PermissionInfo.PROTECTION_DANGEROUS) == PermissionInfo.PROTECTION_DANGEROUS ){
	        	protectionLevelString += "dangerous ";
	        }
	        if ((permissions[i].protectionLevel & PermissionInfo.PROTECTION_SIGNATURE) == PermissionInfo.PROTECTION_SIGNATURE ){
	        	protectionLevelString += "signature ";
	        }
	        if ((permissions[i].protectionLevel & PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM) == PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM ){
	        	protectionLevelString += "signatureOrSystem ";
	        }
			protectionLevelElement.put(ELEMENT_NAME, protectionLevelString);
			permissionInfoChildren.add(protectionLevelElement);				
			
		
			childData.add(permissionInfoChildren);
		}
	}
	
	protected void setupPackageInfoList(){

		showProgressDialog();



		new Thread(new Runnable() {

			@Override
			public void run() {
				// 親要素  
				List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();  
				// 子要素  
				List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();  


				getPackageDetailInfo(groupData, childData);
				
				
				getActivityDetailInfo(groupData, childData);


				getProviderDetailInfo(groupData, childData);


				getReceiverDetailInfo(groupData, childData);
				
				
				getServiceDetailInfo(groupData, childData);
				
				
				getPermissionDetailInfo(groupData, childData);
				
				/*
		        // 親要素  
		        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();  
		        // 子要素  
		        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();  
		        // 値を設定  
		        for (int i = 0; i < 20; i++) {  
		            // 親要素の値を設定  
		            Map<String, String> curGroupMap = new HashMap<String, String>();  
		            curGroupMap.put(CATEGORY_NAME, "Group " + i);  
		            groupData.add(curGroupMap);  

		            // 子要素の値を設定              
		            List<Map<String, String>> children = new ArrayList<Map<String, String>>();  
		            for (int j = 0; j < 15; j++) {  
		                Map<String, String> curChildMap = new HashMap<String, String>();  
		                curChildMap.put(CATEGORY_NAME, "Child " + j);  
		                curChildMap.put(ELEMENT_NAME, (j % 2 == 0) ? "This child is even" : "This child is odd");  
		                children.add(curChildMap);  
		            }  
		            childData.add(children);  
		        }  
				 */

				// adapter 設定  
				mAdapter = new SimpleExpandableListAdapter(  
						getApplicationContext(),  
						// 親要素  
						groupData,  
						// 親要素のレイアウト  
						//android.R.layout.simple_expandable_list_item_1,
						R.layout.expandable_list_item_parent,
						// 親要素のListで、表示するMapのKey  
						new String[] { CATEGORY_NAME },  
						// 親要素レイアウト内での文字を表示する TextView の ID  
						new int[] { android.R.id.text1 },  
						// 子要素  
						childData,  
						// 子要素のレイアウト  
						//android.R.layout.simple_expandable_list_item_2,  
						R.layout.expandable_list_item_child,
						// 子要素のListで、表示するMapのKey  
						new String[] {CATEGORY_NAME, ELEMENT_NAME },  
						// 子要素レイアウト内での文字を表示する TextView の ID  
						new int[] { android.R.id.text1, android.R.id.text2 }  
						); 

				
				Handler h = new Handler(getMainLooper());
				h.post(new Runnable() {			
					@Override
					public void run() {
						setListAdapter(mAdapter);
					}
				});
				updateProgressDialog(100);										
			}
		}).start();



	}

	public class  PackageInfoExpandableListAdapter extends BaseExpandableListAdapter {
		private int[] mRowId;  
		private String[] mGroups;  
		private String[][] mChildren;  

		public PackageInfoExpandableListAdapter(int[] rowId, String[] groups, String[][] children){  
			this.mRowId = rowId;  
			this.mGroups = groups;  
			this.mChildren = children;  
		}


		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public int getGroupCount() {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public boolean hasStableIds() {
			// TODO 自動生成されたメソッド・スタブ
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO 自動生成されたメソッド・スタブ
			return false;
		}
	}



	protected void showProgressDialog(){
		mProgressDialog = new ProgressDialog(this);
		// プログレスダイアログのタイトルを設定します
		//mProgressDialog.setTitle("タイトル");
		// プログレスダイアログのメッセージを設定します
		mProgressDialog.setMessage("読み込み中");
		// プログレスダイアログの確定（false）／不確定（true）を設定します
		mProgressDialog.setIndeterminate(false);
		// プログレスダイアログのスタイルを水平スタイルに設定します
		//mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// プログレスダイアログのスタイルを円スタイルに設定します
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// プログレスダイアログの最大値を設定します
		mProgressDialog.setMax(100);
		// プログレスダイアログの値を設定します
		//mProgressDialog.incrementProgressBy(30);
		// プログレスダイアログのセカンダリ値を設定します
		//mProgressDialog.incrementSecondaryProgressBy(70);
		// プログレスダイアログのキャンセルが可能かどうかを設定します
		mProgressDialog.setCancelable(false);
		// プログレスダイアログを表示します
		mProgressDialog.show();
	}

	protected void updateProgressDialog(final int percentage){

		Handler h = new Handler(getMainLooper());
		h.postDelayed(new Runnable() {			
			@Override
			public void run() {
				if (mProgressDialog.isShowing()){

					if (percentage >= 100 ){
						mProgressDialog.cancel();
					}
					else{
						mProgressDialog.incrementProgressBy(percentage);
					}
				}
			}
		}, 0);
	}

}
