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
import android.view.Menu;
import java.io.File;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

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
        initFile();
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
								if(!ProfileService.hasServicePermission(getApplicationContext(),ProfileService.class)){
									ProfileService.openServicePermissonRoot(getApplicationContext(),ProfileService.class);
								}
								setContentView(R.layout.welcome);
								type=((Spinner)findViewById(R.id.type));
								listview=(ListView)findViewById(R.id.listview);
								listview.setDivider(new BitmapDrawable());
								
								listview.setDividerHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics()));
								listview.setAdapter(mPackageAdapter=new PackageAdapter(listview.getResources()));
								registerForContextMenu(listview);
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
							pi.mode=com.moe.appprofile.Settings.getMode(getApplicationContext(),pi.packageName);
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
								pi.mode=com.moe.appprofile.Settings.getMode(getApplicationContext(),pi.packageName);
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
								pi.mode=com.moe.appprofile.Settings.getMode(getApplicationContext(),pi.packageName);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu,menu);
		return true;
	}


private void initFile(){
	final File file=getFileStreamPath("mode");
	if(file.exists())
		checkRoot();
		else{
			new Thread(){
				public void run(){
				try
				{
					OutputStream output=openFileOutput("mode", MODE_PRIVATE);
					InputStream input=getAssets().open("mode.cfg");
					byte[] buff=new byte[128];
					int len=-1;
					while((len=input.read(buff))!=-1){
						output.write(buff,0,len);
					}
					output.flush();
					output.close();
					input.close();
					ShellUtils.execCmd(new String[]{"chmod 777 "+file.getAbsolutePath()},false);
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								checkRoot();
							}
						});
				}
				catch (Exception e)
				{}
			}
			}.start();
		}
}

@Override
public boolean onOptionsItemSelected(MenuItem item)
{
	switch(item.getItemId()){
		case R.id.mode_cfg:
			startActivity(new Intent(this,ConfigActivity.class));
			break;
	}
	return super.onOptionsItemSelected(item);
}

@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
{
	getMenuInflater().inflate(R.menu.context_menu,menu);
}

@Override
public boolean onContextItemSelected(MenuItem item)
{
	AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	int mode=0;
	switch(item.getItemId()){
		case R.id.balance:
			mode=0;
			break;
		case R.id.performance:
			mode=1;
			break;
		case R.id.powersave:
			mode=2;
			break;
	}
	com.moe.appprofile.Settings.putMode(this,((PackageItem)mPackageAdapter.getItem(info.position)).packageName,mode);
	((PackageItem)mPackageAdapter.getItem(info.position)).mode=mode;
	mPackageAdapter.notifyDataSetChanged();
	return true;
}

	
}
