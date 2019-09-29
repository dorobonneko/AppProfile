package com.moe.appprofile;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import android.accessibilityservice.AccessibilityServiceInfo;

public class ProfileService extends AccessibilityService
{
	private CharSequence packageName;
	@Override
	public void onAccessibilityEvent(AccessibilityEvent p1)
	{
		try
		{
			CharSequence packageName=p1.getPackageName();
			if (packageName.equals(this.packageName))
			{
				//不处理
			}
			else
			{
				this.packageName = packageName;
				ShellUtils.execCmd(new String[]{getFileStreamPath("mode").getAbsolutePath() + " " + Settings.getMode(getApplicationContext(), packageName.toString())}, true);
			}
		}
		catch (Exception e)
		{}
	}
	@Override
	protected void onServiceConnected()
	{
		AccessibilityServiceInfo info=new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
		info.feedbackType = info.FEEDBACK_ALL_MASK;
		setServiceInfo(info);
	}

	@Override
	public void onInterrupt()
	{
		Toast.makeText(getApplicationContext(), "已关闭", Toast.LENGTH_SHORT).show();
	}
	public static boolean hasServicePermission(Context ct, Class serviceClass)
	{
        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        String settingValue = android.provider.Settings.Secure.getString(ct.getContentResolver(), android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
		if (settingValue != null)
		{
			ms.setString(settingValue);
			while (ms.hasNext())
			{
				String accessibilityService = ms.next();
				if (accessibilityService.contains(serviceClass.getSimpleName()))
				{
					return true;
				}
			}
		}
        return false;
    }
	public static void openServicePermissonRoot(Context ct, Class service)
	{
        String cmd1 = "settings put secure enabled_accessibility_services  " + ct.getPackageName() + "/" + service.getName();
        String cmd2 = "settings put secure accessibility_enabled 1";
        String[] cmds = new String[]{cmd1, cmd2};
        ShellUtils.execCmd(cmds, true);
    }


}
