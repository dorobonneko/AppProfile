package com.moe.appprofile;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.provider.Settings;
import android.content.Context;
import android.text.TextUtils;

public class ProfileService extends AccessibilityService
{

	@Override
	public void onAccessibilityEvent(AccessibilityEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onInterrupt()
	{
		// TODO: Implement this method
	}
	public static boolean hasServicePermission(Context ct, Class serviceClass) {
        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        String settingValue = Settings.Secure.getString(ct.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.contains(serviceClass.getSimpleName())) {
                        return true;
                    }
                }
            }
        return false;
    }
	public static void openServicePermissonRoot(Context ct, Class service) {
        String cmd1 = "settings put secure enabled_accessibility_services  " + ct.getPackageName() + "/" + service.getName();
        String cmd2 = "settings put secure accessibility_enabled 1";
        String[] cmds = new String[]{cmd1, cmd2};
        ShellUtils.execCmd(cmds, true);
    }
	
	
	}
