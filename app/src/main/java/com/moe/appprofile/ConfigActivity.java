package com.moe.appprofile;
import android.view.*;
import java.io.*;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigActivity extends Activity
{
	private EditText edit;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		edit=new EditText(this);
		edit.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
		edit.setBackground(null);
		edit.setGravity(Gravity.START|Gravity.TOP);
		//加载文件数据
		loadFile();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.clear,menu);
		return true;
	}

	
	private void loadFile(){
		new Thread(){
			public void run(){
		try
		{
			InputStream input=openFileInput("mode");
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			byte[] buff=new byte[128];
			int len=-1;
			while((len=input.read(buff))!=-1){
				baos.write(buff,0,len);
			}
			String data=baos.toString();
			baos.close();
			input.close();
			loadData(data);
		}
		catch (Exception e)
		{
			loadCfg();
		}
		}
		}.start();
	}
	private void loadCfg(){
		new Thread(){
			public void run(){
		try
		{
			InputStream input=getAssets().open("mode.cfg");
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			byte[] buff=new byte[128];
			int len=-1;
			while((len=input.read(buff))!=-1){
				baos.write(buff,0,len);
			}
			String data=baos.toString();
			baos.close();
			input.close();
			loadData(data);
		}
		catch (Exception e)
		{
			loadData(null);
		}
		}
		}.start();
	}
	private void loadData(final String data){
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					edit.setText(data);
					if(edit.getParent()==null)
						setContentView(edit);
				}
			});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.clear:
				loadCfg();
				break;
			case R.id.save:
				final String data=edit.getText().toString();
				new Thread(){
					public void run(){
						try
						{
							OutputStream output=ConfigActivity.this.openFileOutput("mode", 0);
							output.write(data.getBytes());
							output.flush();
							output.close();
							ShellUtils.execCmd(new String[]{"chmod 777 "+getFileStreamPath("mode").getAbsolutePath()},false);
							runOnUiThread(new Runnable(){

									@Override
									public void run()
									{
										Toast.makeText(getApplicationContext(),"已保存",Toast.LENGTH_SHORT).show();
									}
								});
						}
						catch (IOException e)
						{}
					}
				}.start();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
