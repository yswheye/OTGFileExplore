/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-26 上午11:57:10 
* @Version V1.0   
*/ 

package org.yyu.msi.view; 

import org.yyu.msi.R;
import org.yyu.msi.utils.MyDateUtils;
import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.utils.MyStringUtil;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/** 
 * @ClassName: FileInforDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-26 上午11:57:10  
 */
public class FileInforDialog extends PopupWindow
{
	private View parentView = null;
	private View contentView = null;
	private TextView tvTitle = null;
	private Button btnConfirm = null;
	private ListView listView = null;
	
	private String infors[] = null;
	private String fileDetail[] = null;
	private String folderDetail[] = null;
	
	public FileInforDialog(Context context)
	{
		super(context);
		
		parentView = LayoutInflater.from(context).inflate(R.layout.dialog_file_infor, null);
		contentView = parentView.findViewById(R.id.dialog_file_infor_content);
		tvTitle = (TextView)parentView.findViewById(R.id.tv_dialog_file_infor_title);
		btnConfirm = (Button)parentView.findViewById(R.id.btn_file_infor_dialog_confirm);
		listView = (ListView)parentView.findViewById(R.id.lv_dialog_file_infor);
		
		infors = context.getResources().getStringArray(R.array.FileInfor);
		fileDetail = context.getResources().getStringArray(R.array.FileDetail);
		folderDetail = context.getResources().getStringArray(R.array.FolderDetail);
		
		//设置View
		this.setContentView(parentView);
		//设置弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		//设置弹出窗体的高
		this.setHeight(LayoutParams.FILL_PARENT);
		//设置弹出窗体可点击
		this.setFocusable(true);
		//设置弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimAlph);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x90000000);
		//设置弹出窗体的背景
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(true);
		
		listView.setAdapter(new DialogAdapter(context, infors));
		
		parentView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent event)
			{
				// TODO Auto-generated method stub
				int top = contentView.getTop();
				int bottom = contentView.getBottom();
				int y = (int) event.getY();
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(y < top || y > bottom)
						dismiss();
				}
				return true;
			}
		});
		btnConfirm.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
	}
	
	public void showFileDetailDialog(Context context, View rootView, MyFileInfor fileInfor)
	{
		listView.setAdapter(new DialogAdapter(context, fileDetail, fileInfor));
		showDetailDialog(rootView);
		listView.setOnItemClickListener(null);
	}
	public void showFolderDetailDialog(Context context, View rootView, MyFileInfor fileInfor)
	{
		listView.setAdapter(new DialogAdapter(context, folderDetail, fileInfor));
		showDetailDialog(rootView);
		listView.setOnItemClickListener(null);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		listView.setOnItemClickListener(listener);
	}
	
	public void showDetailDialog(View rootView)
	{
		showAtLocation(rootView, Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0); 
	}
	
	public void setTitle(String title)
	{
		tvTitle.setText(title);
	}
	public void setTitle(int title)
	{
		tvTitle.setText(title);
	}
	
	class DialogAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater = null;
		private String[] infors = null;
		private MyFileInfor fileInfor = null;
		public DialogAdapter(Context context, String[] infors)
		{
			mInflater = LayoutInflater.from(context);
			this.infors = infors;
		}
		public DialogAdapter(Context context, String[] infors, MyFileInfor fileInfor)
		{
			mInflater = LayoutInflater.from(context);
			this.infors = infors;
			this.fileInfor = fileInfor;
		}
		/**
		*callbacks
		*/
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return infors.length;
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return infors[arg0];
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
			ViewHolder vh = null;
			if(convertView == null)
			{
				vh = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_view, null);
				vh.imageViewIcon = (ImageView)convertView.findViewById(R.id.iv_list_view_item_icon);
				vh.textViewInfor = (TextView)convertView.findViewById(R.id.tv_list_view_item_infor);
				vh.textViewInforSub = (TextView)convertView.findViewById(R.id.tv_list_view_item_infor_sub);
				vh.imageViewMore = (ImageView)convertView.findViewById(R.id.iv_list_view_item_more);
				View itemBg = convertView.findViewById(R.id.list_view_item_bg);
				itemBg.setBackgroundResource(R.drawable.selector_button_bg);
				convertView.setTag(vh);
			}
			else 
				vh = (ViewHolder)convertView.getTag();
			
			vh.textViewInfor.setText(infors[position]);
			if(fileInfor != null)
			{
				vh.textViewInforSub.setVisibility(View.VISIBLE);
				if(position == 0)//名称
					vh.textViewInforSub.setText(fileInfor.getFileName());
				if(position == 1)//类型
					vh.textViewInforSub.setText(fileInfor.getFileType()+"");
				if(position == 2)//大小
					vh.textViewInforSub.setText(MyStringUtil.getFormatSize(fileInfor.getFileSize()));
				if(position == 3)//时间
					vh.textViewInforSub.setText(MyDateUtils.longToDate(fileInfor.getModifyTime()));
				if(position == 4)//路径或者内容
					vh.textViewInforSub.setText(fileInfor.getFileUrl());
				if(position == 5)
					vh.textViewInforSub.setText(fileInfor.getFileName());
			}
			return convertView;
		}
		
		class ViewHolder
		{
			ImageView imageViewIcon = null;
			TextView textViewInfor = null;
			TextView textViewInforSub = null;
			ImageView imageViewMore = null;
		}
	}
}
 
