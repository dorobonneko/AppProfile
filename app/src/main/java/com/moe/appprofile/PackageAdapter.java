package com.moe.appprofile;
import android.widget.ListAdapter;
import android.view.View;
import android.database.DataSetObserver;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.res.Resources;
import android.graphics.Color;

public class PackageAdapter extends BaseAdapter
{
	private List<PackageItem> list,filter;
	private String filter_key;
	private int[] color=new int[3];
	public PackageAdapter(Resources res){
		String[] colors=res.getStringArray(R.array.color);
		for(int i=0;i<3;i++){
			color[i]=Color.parseColor(colors[i]);
			}
		list=new ArrayList<>();
		filter=new ArrayList<>();
	}
	public void setData(List<PackageItem> data){
		list.clear();
		list.addAll(data);
		onFilter(filter_key);
	}
	public void onFilter(String filter){
		filter_key=filter;
		if(filter!=null){
			this.filter.clear();
			for(PackageItem pi:list){
				if(pi.title.toString().contains(filter)||pi.packageName.contains(filter))
					this.filter.add(pi);
			}
		}
		notifyDataSetChanged();
	}
	@Override
	public int getCount()
	{
		return filter_key==null?list.size():filter.size();
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return filter_key!=null?filter.get(p1):list.get(p1);
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null){
			p2=LayoutInflater.from(p3.getContext()).inflate(R.layout.packageinfo_item,p3,false);
		}
		ViewHolder vh=(PackageAdapter.ViewHolder) p2.getTag();
		if(vh==null)
			p2.setTag(vh=new ViewHolder(p2));
			PackageItem pi=(PackageItem) getItem(p1);
			vh.icon.setImageDrawable(pi.icon);
			vh.title.setText(pi.title);
			vh.packageName.setText(pi.packageName);
			switch(pi.mode){
				case 0:
					vh.mode.setText("均衡模式");
					break;
				case 1:
					vh.mode.setText("性能模式");
					break;
				case 2:
					vh.mode.setText("省电模式");
					break;
			}
			vh.mode.setTextColor(color[pi.mode]);
		return p2;
	}
	

	
	private class ViewHolder{
		private TextView title,packageName,mode;
		private ImageView icon;
		public ViewHolder(View v){
			title=v.findViewById(R.id.title);
			packageName=v.findViewById(R.id.packagename);
			mode=v.findViewById(R.id.mode);
			icon=v.findViewById(R.id.icon);
		}
	}
	
}
