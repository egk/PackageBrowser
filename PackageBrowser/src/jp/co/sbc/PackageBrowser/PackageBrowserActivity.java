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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class PackageBrowserActivity extends Activity {

	ProgressDialog mProgressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		final ListView ll = (ListView)findViewById(R.id.PackageListview);
		ll.setAdapter(new PackageInfoAdapter(getApplicationContext(), R.layout.listitem));        
		ll.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				PackageInfo item = (PackageInfo) ll.getAdapter().getItem(position);
				Intent intent = new Intent();
				intent.setClassName(getPackageName(), PackageDetailInfoActivity.class.getName());
				intent.putExtra(Defines.KEY_PACKAGENAME, item.packageName);

				startActivity(intent);


			}

		});

		ll.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {

				PackageInfo item = (PackageInfo) ll.getAdapter().getItem(position);

				
				Intent intent = new Intent();
				intent.setClassName(getPackageName(), ActivitesListDialogActivity.class.getName());
				intent.putExtra(Defines.KEY_PACKAGENAME, item.packageName);		
				
				startActivity(intent);
/*
				Intent intent = new Intent().setPackage(item.packageName);
				//intent.setAction(Intent.ACTION_MAIN);
				
				
				List<ResolveInfo>  resolveinfo = getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
				
				if	(resolveinfo != null){
					for (int i = 0; i < resolveinfo.size(); i++){
						
						ActivityInfo ai = resolveinfo.get(i).activityInfo;
						
						intent.setClassName(ai.packageName, ai.name);
						
						try {
							startActivity(intent);
							break;

						} catch (ActivityNotFoundException  e) {
							e.printStackTrace();
						}
						catch (Exception e ){
							e.printStackTrace();
						}
						
						
					}
	
				}
				
				
*/				
				return true;
			}
		});
		
		
		ll.setTextFilterEnabled(true);        
		ll.requestFocus();
		if (Build.VERSION.SDK_INT >= 11) {

			SearchView search = (SearchView) findViewById(R.id.PackageSearchView);
			search.setIconifiedByDefault(false);
			search.setOnQueryTextListener(new OnQueryTextListener(){

				@Override
				public boolean onQueryTextChange(String newText) {
					ListView ll = (ListView)findViewById(R.id.PackageListview);
					if (TextUtils.isEmpty(newText)) {
						ll.clearTextFilter();
					} else {
						ll.setFilterText(newText.toString());
					}

					return true;
				}

				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

			});

			search.setSubmitButtonEnabled(true);
		}



	}

	static class ViewHolder {  
		ImageView mPackageIcon;  
		TextView mApplicationName;  
		TextView mPackageName;  
	}  

	public class PackageInfoAdapter extends ArrayAdapter<PackageInfo> {
		@Override
		public Filter getFilter() {
			return super.getFilter();
			/*
			return new Filter() {

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					returrn null;
				}
			};
			*/
		}


		private PackageManager mPackageManager;
		private LayoutInflater mInflater;

		public PackageInfoAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			this.mPackageManager = (PackageManager)context.getPackageManager();
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  


			List<PackageInfo> list = mPackageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
			try {
				addAll(list);
			}
			catch (NoSuchMethodError e) {
				for (int i =0; i < list.size(); i++){
					add(list.get(i));
				}

			}



		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			View view = convertView;  
			if (view == null) {  
				// 受け取ったビューがnullなら新しくビューを生成  
				view = mInflater.inflate(R.layout.listitem, null);


				holder = new ViewHolder();  
				holder.mPackageIcon = (ImageView) view.findViewById(R.id.PackageIcon); 
				holder.mApplicationName = (TextView) view.findViewById(R.id.ApplicationName);
				holder.mPackageName = (TextView) view.findViewById(R.id.PackageName);

				view.setTag(holder);
			}
			else{
				holder = (ViewHolder)view.getTag(); 
			}

			PackageInfo item = getItem(position);
			holder.mPackageIcon.setImageDrawable(item.applicationInfo.loadIcon(mPackageManager));
			holder.mApplicationName.setText(item.applicationInfo.loadLabel(mPackageManager));
			holder.mPackageName.setText(item.packageName);

			return view;
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