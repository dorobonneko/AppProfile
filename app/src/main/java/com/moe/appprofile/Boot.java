package com.moe.appprofile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Boot extends BroadcastReceiver
{

	@Override
	public void onReceive(Context p1, Intent p2)
	{
		if(!ProfileService.hasServicePermission(p1,ProfileService.class)){
			ProfileService.openServicePermissonRoot(p1,ProfileService.class);
		}
	}
	
}
