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

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivitesListDialogActivity extends Activity {
	
	private PackageManager mPackageManager = null;
	private PackageInfo mPackageInfo = null;
	
	private ActivityInfoAdapter mActivityInfoAdapter = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		
		Intent intent = getIntent();
		String packageName = intent.getStringExtra(Defines.KEY_PACKAGENAME);
		
		mPackageManager = getPackageManager();
		
		try {
			mPackageInfo = 	mPackageManager.getPackageInfo(packageName, Defines.PACAKGEINFO_FLAG);
			ActivityInfo[] activity = mPackageInfo.activities;

			if (activity != null) {
				mActivityInfoAdapter = new ActivityInfoAdapter(getApplicationContext(), 0, activity);
				setContentView(R.layout.activities);
				
				ListView lv = (ListView)findViewById(R.id.activity_list);				
				lv.setAdapter(mActivityInfoAdapter);
				
				
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						ActivityInfo item = mActivityInfoAdapter.getItem(position);
						
						Intent intent = new Intent();
						intent.setClassName(item.packageName, item.name);
						
						
						try {
							startActivity(intent);							
						} catch (Exception e) {

							showDialog("起動失敗", e.getLocalizedMessage());
						}
						
					}
					
				});
				
				
				
			}
			else{
				showDialog("起動失敗", "Activityがみつかりません");
			}
			


		} catch (NameNotFoundException e) {
			showDialog("起動失敗", e.getLocalizedMessage());

		}
		
	}
	
	private void  showDialog(String title, String msg) {

		new AlertDialog.Builder(ActivitesListDialogActivity.this)
		.setTitle(title)
	    .setMessage(msg)
	    .show();

		
		
	}
	
	
	
	static class ViewHolder {  
		ImageView mActivityIcon;  
		TextView mActivityName;  
		TextView myobiName;  
	}  

	public class ActivityInfoAdapter extends ArrayAdapter<ActivityInfo> {
		private LayoutInflater mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		
		
		public ActivityInfoAdapter(Context context, int textViewResourceId,
				ActivityInfo[] objects) {
			super(context, textViewResourceId, objects);

		}

		public ActivityInfoAdapter(Context context, int resource,
				int textViewResourceId, ActivityInfo[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public ActivityInfoAdapter(Context context, int resource,
				int textViewResourceId, List<ActivityInfo> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public ActivityInfoAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		public ActivityInfoAdapter(Context context, int textViewResourceId,
				List<ActivityInfo> objects) {
			super(context, textViewResourceId, objects);
		}

		public ActivityInfoAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View view = convertView;  
			if (view == null) {  
				// 受け取ったビューがnullなら新しくビューを生成  
				view = mInflater.inflate(R.layout.activity_item, null);

				holder = new ViewHolder();  
				holder.mActivityIcon = (ImageView) view.findViewById(R.id.activity_icon); 
				holder.mActivityName = (TextView) view.findViewById(R.id.activity_name);

				view.setTag(holder);
			}
			else{
				holder = (ViewHolder)view.getTag(); 
			}

			ActivityInfo item = getItem(position);
			
			
			String label = item.loadLabel(mPackageManager).toString();
			String name = item.name;
			
			if (label != null){
				label += "(" + name + ")";
				
			}
			else{
				label = name;
			}
					
			
			holder.mActivityIcon.setImageDrawable(item.loadIcon(mPackageManager));
			holder.mActivityName.setText(label);

			return view;
		}
		
		
	}
	
}
