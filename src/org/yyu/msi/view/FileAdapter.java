/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-6 下午4:15:20 
* @Version V2.0   
*/ 

package org.yyu.msi.view; 

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.yyu.msi.R;
import org.yyu.msi.common.Setting;
import org.yyu.msi.entity.FileType;
import org.yyu.msi.entity.Global;
import org.yyu.msi.utils.MyDateUtils;
import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.utils.MyFileUtil;
import org.yyu.msi.utils.MyStringUtil;
import org.yyu.msi.utils.MyViewUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;


/** 
 * @ClassName: FileAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-6 下午4:15:20  
 */
public class FileAdapter extends BaseAdapter implements Filterable 
{
	private AdapterViewHolder vh = null;
	private LayoutInflater mInflater = null;
	private TextView tvSearch = null;
	private ProgressBar pbSearch = null;
	private View bgView = null;
	
	private Set<LoadFileTask> taskCollection;  
	/*加载图片的线程池*/
	private ExecutorService searchThreadPool;
	
	private ArrayList<MyFileInfor> list = null;
	private ArrayList<MyFileInfor> allFileList = new ArrayList<MyFileInfor>();
	private ArrayList<MyFileInfor> resultList = new ArrayList<MyFileInfor>();
	
	private Context context = null;
	private boolean isSearch = false;
	private boolean isEdit = false;
	private boolean isFolderSelect = false;
	private boolean isGridView = false;
	private String curPath = null;
	public FileAdapter(Context context, ArrayList<MyFileInfor> list)
	{
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		searchThreadPool = Executors.newFixedThreadPool(5);
		taskCollection = new HashSet<LoadFileTask>();
	}
	
	public void notifyDataSetChanged(boolean isEdit)
	{
		isGridView = Setting.isGridView(context);
		this.isEdit = isEdit;
		this.isFolderSelect = !isEdit;
		
		notifyDataSetChanged();
	}
	
	public void shutDownThreadPool()
	{
		searchThreadPool.shutdown();
		cancelAllTasks();
	}
	public void restartThreadPool() 
	{
		synchronized (searchThreadPool) 
		{
			if (searchThreadPool.isTerminated() || searchThreadPool.isShutdown()) 
			{
				searchThreadPool = null;
				searchThreadPool = Executors.newFixedThreadPool(3);
			}
		}
	}
	
	public void setCurPath(String curPath)
	{
		this.curPath = curPath;
	}
	
	public void setViews(TextView tvSearch, ProgressBar pbSearch, View bgView)
	{
		this.tvSearch = tvSearch;
		this.pbSearch = pbSearch;
		this.bgView = bgView;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if(isSearch)
			return resultList.size();
		return list.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		if(isSearch)
			return resultList.get(arg0);
		return list.get(arg0);
	}

	/**
	*callbacks
	*/
	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return arg0;
	}

	/**
	*callbacks
	*/
	@Override
	public View getView(int position, View convertView, ViewGroup arg2)
	{
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			vh = new AdapterViewHolder();
			if(isSearch || !isGridView)
				convertView = mInflater.inflate(R.layout.file_infor_fragment_item, null);
			else
				convertView = mInflater.inflate(R.layout.file_infor_fragment_item_1, null);
			
			vh.imageView = (PowerImageView)convertView.findViewById(R.id.iv_file_infor_fragment_item_type);
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_file_infor_fragment_item_name);
			vh.tvInfor = (TextView)convertView.findViewById(R.id.tv_file_infor_fragment_item_infor);
			vh.checkBox = (CheckBox)convertView.findViewById(R.id.cb_file_infor_fragment_item);
			
			convertView.setTag(vh);
		}
		else
			vh = (AdapterViewHolder)convertView.getTag();
		
		MyFileInfor fileInfor = null;
		if(isSearch)
			fileInfor = resultList.get(position);
		else
			fileInfor = list.get(position);
		if(isSearch)
			vh.tvName.setText(getStyle(fileInfor.getFileName(), fileInfor.getStyleIndex()[0], fileInfor.getStyleIndex()[1]));
		else
			vh.tvName.setText(fileInfor.getFileName());
		
		if(isEdit)//处于编辑状态
		{
			vh.checkBox.setVisibility(View.VISIBLE);
			vh.checkBox.setChecked(fileInfor.isChecked());
			MyViewUtil.setViewSelcted(convertView, vh.checkBox.isChecked());
		}
		else
		{
			vh.checkBox.setVisibility(View.GONE);
			if(isFolderSelect)
				fileInfor.setIsChecked(false);
			MyViewUtil.setViewSelcted(convertView, false);
			
		}
		
		String dirInfor = null;
		if(fileInfor.isFolder())//文件夹
		{
			if(!fileInfor.isLoaded())
				dirInfor = context.getString(R.string.common_data_loading);
			else
				dirInfor = context.getString(R.string.local_file) + fileInfor.getSubFileCount()
					+ ", " + context.getString(R.string.local_folder) + fileInfor.getSubDirCount();
		}
		else 
		{
			if(!fileInfor.isLoaded())
				dirInfor = context.getString(R.string.common_data_loading);
			else
			{
				String size = MyStringUtil.getFormatSize(fileInfor.getFileSize());
				String date = MyDateUtils.longToDate(fileInfor.getModifyTime());
				dirInfor = date + "  " + size;
			}
		}
		vh.tvInfor.setText(dirInfor);
		
		setIcon(vh, fileInfor, position);
		
		return convertView;
	}
	
	private SpannableStringBuilder getStyle(String str, int start, int end)
	{
		if(start < 0)
			start = 0;
		if(end < 0)
			end = 0;
		SpannableStringBuilder style = new SpannableStringBuilder(str);
    	style.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    	return style;
	}
	
	private void setIcon(AdapterViewHolder vh, MyFileInfor fileInfor, int pos)
	{
		int type = fileInfor.getFileType();
		
		if(type == FileType.TYPE_FOLDER)
		{
			vh.imageView.setImageResource(R.drawable.file_folder);
		}
		else if(type == FileType.TYPE_AUDIO)
		{
			vh.imageView.setImageResource(R.drawable.file_music);
		}
		else if(type == FileType.TYPE_IMAGE)
		{
			Global.imageWorker.loadBitmap(fileInfor.getFileUrl(), vh.imageView);
		}
		else if(type == FileType.TYPE_VIDEO)
		{
			Global.imageWorker.loadBitmap(fileInfor.getFileUrl(), vh.imageView);
		}
		else if(type == FileType.TYPE_ZIP)
		{
			vh.imageView.setImageResource(R.drawable.file_zip);
		}
		else if(type == FileType.TYPE_APK)
		{
	    	vh.imageView.setImageResource(R.drawable.ic_launcher);
	    	vh.imageView.setImageDrawable(MyFileUtil.getApkIcon(context, fileInfor.getFileUrl()));
		}
		else 
		{
			vh.imageView.setImageResource(R.drawable.file_txt);
		}
	}
	
	public void loadFiles(int firstVisibleItem, int visibleItemCount)
	{
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) 
		{
			MyFileInfor fileInfor = list.get(i);
			if(!fileInfor.isLoaded())
				updateFileInfor(fileInfor);
		}
	}
	
	private void updateFileInfor(final MyFileInfor fileInfor)
	{
		
		LoadFileTask task = new LoadFileTask(fileInfor);
		if (!searchThreadPool.isTerminated() && !searchThreadPool.isShutdown()) 
		{
			searchThreadPool.execute(task);
		}
		taskCollection.add(task);
	}
	
	public void cancelAllTasks() 
	{  
        if (taskCollection != null) 
        {  
            for (LoadFileTask task : taskCollection) 
            {  
                task.cancel();  
            }  
        }  
    }  
	
	class LoadFileTask implements Runnable
	{
		MyFileInfor fileInfor = null;
		private boolean stop = false;
		
		public LoadFileTask(MyFileInfor fileInfor)
		{
			this.fileInfor = fileInfor;
		}
		
		public void cancel()
		{
			stop = true;
		}
		
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			if(fileInfor.isFolder())//文件夹
			{
				List<MyFileInfor> tempList = MyFileUtil.getLocalFiles(context, fileInfor.getFileUrl());
				int size = tempList.size();
				int dirCount = 0;
				int fileCount = 0;
				for(int i=0;i<size;i++)
				{
					if(stop)
						break;
					MyFileInfor infor = tempList.get(i);
					if(infor.isFolder())
						dirCount ++;
					else
						fileCount ++;
				}
				fileInfor.setSubDirCount(dirCount);
				fileInfor.setSubFileCount(fileCount);
			}
			else
			{
				if(stop)
					return;
				File tempFile = new File(fileInfor.getFileUrl());
				fileInfor.setFileSize(tempFile.length());//文件大小
				long time = tempFile.lastModified();
				fileInfor.setModifyTime(time);//文件修改时间
			}
			fileInfor.setIsLoad(true);
			MyViewUtil.sendMessage(handler, MSG_UPDATE_UI, fileInfor);
		}
	}
	
	public class AdapterViewHolder
	{
		public PowerImageView imageView = null;
		public TextView tvName = null;
		public TextView tvInfor = null;
		public CheckBox checkBox = null;
	}

	private final int MSG_SEARCH_START = 0;
	private final int MSG_SEARCH_FINISH = 1;
	private final int MSG_SEARCH_RESET = 2;
	private final int MSG_UPDATE_UI = 3;
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MSG_SEARCH_START)
			{
				bgView.setVisibility(View.VISIBLE);
				pbSearch.setVisibility(View.VISIBLE);
				tvSearch.setText(context.getString(R.string.search_start));
			}
			else if(msg.what == MSG_SEARCH_FINISH)
			{
				int result = (Integer)msg.obj;
				tvSearch.setText(context.getString(R.string.search_result) + result);
				pbSearch.setVisibility(View.GONE);
			}
			else if(msg.what == MSG_SEARCH_RESET)
			{
				bgView.setVisibility(View.GONE);
			}
			else if(msg.what == MSG_UPDATE_UI)
			{
				notifyDataSetChanged();
			}
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	public Filter getFilter()
	{
		// TODO Auto-generated method stub
		Filter filter = new Filter()
		{
			
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				// TODO Auto-generated method stub
				if(isSearch)
				{
					List<MyFileInfor> tempList = (List<MyFileInfor>)results.values;
					if(tempList != null)
					{
						resultList.clear();
						resultList.addAll(tempList);
					}
				}
				notifyDataSetChanged();
			}
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				// TODO Auto-generated method stub
				
				MyViewUtil.sendMessage(handler, MSG_SEARCH_START);
				
				FilterResults results = new FilterResults();

                if (constraint!= null && constraint.toString().length() > 0) 
                {
                	ArrayList<MyFileInfor> resultList = new ArrayList<MyFileInfor>();
                	allFileList.clear();
                	MyFileUtil.getLocalFiles(allFileList, -1, curPath);//获取全部文件列表
                	int size = allFileList.size();
                    for (int index = 0; index < size; index++) 
                    {
                    	MyFileInfor fileInfor = allFileList.get(index);
                        String si = fileInfor.getFileName();
                        String desStr = constraint.toString().toLowerCase();
                        String orgString = si.toLowerCase();
                        if(orgString.contains(desStr))
                        {
                        	int[] styleIndex = new int[2];
                        	styleIndex[0] = orgString.indexOf(desStr);
                        	styleIndex[1] = styleIndex[0] + desStr.length();
                        	fileInfor.setStyleIndex(styleIndex);
                        	resultList.add(fileInfor);  
                        }
                    }
                    results.values = resultList;
                    results.count = resultList.size(); 
                    isSearch = true;
                    
                    MyViewUtil.sendMessage(handler, MSG_SEARCH_FINISH, resultList.size());
                }
                else
                {
                	isSearch = false;
                	MyViewUtil.sendMessage(handler, MSG_SEARCH_RESET);
                    synchronized (list)
                    {
                        results.values = list;
                        results.count = list.size();
                    }
                }
                return results;
			}
		};
		return filter;
	}
}
