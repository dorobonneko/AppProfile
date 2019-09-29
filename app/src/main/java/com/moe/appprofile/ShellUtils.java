package com.moe.appprofile;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.File;

public class ShellUtils
{
	public static int execCmd(String... cmds,boolean useRoot){
		int exit=1;
		try
		{
			Process p=Runtime.getRuntime().exec(useRoot ?"su": "sh");
			OutputStream output=p.getOutputStream();
			for(String cmd:cmds){
				output.write(cmd.getBytes());
				output.write("\n".getBytes());
			}
			output.write("exit $?\n".getBytes());
			output.flush();
			try
			{
				exit=p.waitFor();
			}
			catch (InterruptedException e)
			{}
		}
		catch (IOException e)
		{
			
		}
		return exit;
	}
}
