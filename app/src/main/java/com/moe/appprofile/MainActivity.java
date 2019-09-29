package com.moe.appprofile;

import android.app.*;
import android.os.*;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.view.View;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.content.pm.PackageManager;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.content.pm.ApplicationInfo;
import android.widget.ListView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.ArrayList;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;

public class MainActivity extends Activity implements TextWatcher
{
	private Spinner type;
	private ListView listview;
	private EditText filter;
	private PackageAdapter mPackageAdapter;
	private View progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        checkRoot();
	}
	private void checkRoot()
	{
		new Thread(){
			public void run()
			{
				if (ShellUtils.execCmd(new String[]{}, true) == 0)
				{
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								setContentView(R.layout.welcome);
								type=((Spinner)findViewById(R.id.type));
								listview=(ListView)findViewById(R.id.listview);
								listview.setDivider(new BitmapDrawable());
								
								listview.setDividerHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics()));
								listview.setAdapter(mPackageAdapter=new PackageAdapter(listview.getResources()));
								
								progressbar=findViewById(R.id.progressbar);
								filter=(EditText)findViewById(R.id.filter);
								filter.addTextChangedListener(MainActivity.this);
								type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

										@Override
										public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
										{
											loadData();
										}

										@Override
										public void onNothingSelected(AdapterView<?> p1)
										{
											// TODO: Implement this method
										}
									});
							}
						});
				}
				else
				{
					runOnUiThread(new Runnable(){
							public void run()
							{
								setContentView(R.layout.main);
								findViewById(R.id.retry).setOnClickListener(new View.OnClickListener(){

										@Override
										public void onClick(View p1)
										{
											checkRoot();
										}
									});
							}
						});
				}
			}
		}.start();
	}
	private void loadData(){
		final int type=this.type.getSelectedItemPosition();
		progressbar.setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				final List<PackageItem> packageslist=new ArrayList<>();
				List<ResolveInfo> packages=getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),PackageManager.GET_UNINSTALLED_PACKAGES);
				switch(type){
					case 2:
						for(ResolveInfo info:packages){
							PackageItem pi=new PackageItem();
							pi.icon=info.activityInfo.loadIcon(getPackageManager());
							pi.title=info.activityInfo.loadLabel(getPackageManager());
							pi.packageName=info.activityInfo.packageName;
							packageslist.add(pi);
						}
						break;
					case 1:
						for(ResolveInfo info:packages){
							if((info.activityInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
								PackageItem pi=new PackageItem();
								pi.icon=info.activityInfo.loadIcon(getPackageManager());
								pi.title=info.activityInfo.loadLabel(getPackageManager());
								pi.packageName=info.activityInfo.packageName;
								packageslist.add(pi);
							}
						}
						break;
					case 0:
						for(ResolveInfo info:packages){
							if((info.activityInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)!=ApplicationInfo.FLAG_SYSTEM){
								PackageItem pi=new PackageItem();
								pi.icon=info.activityInfo.loadIcon(getPackageManager());
								pi.title=info.activityInfo.loadLabel(getPackageManager());
								pi.packageName=info.activityInfo.packageName;
								packageslist.add(pi);
							}
						}
						break;
				}
				runOnUiThread(new Runnable(){

						@Override
						public void run()
						{
							mPackageAdapter.setData(packageslist);
							progressbar.setVisibility(View.INVISIBLE);
						}
					});
			}
		}.start();
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		String filter=p1.toString().trim();
		if(mPackageAdapter!=null)
			mPackageAdapter.onFilter(filter.length()==0?null:filter);
	}



	
}
