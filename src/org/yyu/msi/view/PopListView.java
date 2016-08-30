/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-4 上午10:51:32 
* @Version V2.0   
*/ 

package org.yyu.msi.view; 

import java.util.ArrayList;
import java.util.List;

import org.yyu.msi.R;
import org.yyu.msi.utils.MyFileInfor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;


/** 
 * @ClassName: PopListView 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-4 上午10:51:32  
 */
public class PopListView extends View
{

	private PopupWindow popWin = null;
	private View popPullDownView = null;
	private ListView dirListView = null;
	
	private OnDismissListener onDismissListener = null;
	private PopWinAdapter popWinAdapter = null;
	private List<MyFileInfor> inforList = new ArrayList<MyFileInfor>(); 
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public PopListView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		
		popPullDownView = LayoutInflater.from(context).inflate(R.layout.pop_list_win, null);
		dirListView = (ListView)popPullDownView.findViewById(R.id.lv_pop_list_win);	
		popWinAdapter = new PopWinAdapter(context, inforList);
		dirListView.setAdapter(popWinAdapter);
		
	}

	public void setOnDismissListener(OnDismissListener onDismissListener)
	{
		this.onDismissListener = onDismissListener;
	}
	public ListView getListView()
	{
		return dirListView;
	}
	public void notifyDatasetChanged()
	{
		popWinAdapter.notifyDataSetChanged();
	}
	public void dismiss()
	{
		if(popWin != null)
		{
			popWin.dismiss();
		}
	}
	
	public void showPopViewList(View view, List<MyFileInfor> inforList)
	{
		if(view == null || inforList == null)
			return;
		this.inforList = inforList;
		popWinAdapter.notifyDataSetChanged();
		//显示Popwindow
		initPopWindow(view.getWidth(), LayoutParams.WRAP_CONTENT, popPullDownView);
		popWin.showAsDropDown(view);
	}
	
	public void showPopViewList(View view, List<MyFileInfor> inforList, int width, int height)
	{
		if(view == null || inforList == null)
			return;
		this.inforList = inforList;
		popWinAdapter.notifyDataSetChanged();
		int w = 0;
		int h = 0;
		w = width <= 0 ?view.getWidth():width;
		//h = inforList.size() >= 3 ?300 : LayoutParams.WRAP_CONTENT;
		//显示Popwindow
		initPopWindow(w, LayoutParams.WRAP_CONTENT, popPullDownView);
		popWin.showAsDropDown(view, 20, 15);
	}
	
	public void initPopWindow(int w, int h, View popView)
	{
		if(popWin != null)
		{
			popWin.dismiss();
			popWin = null;
		}
    	//(popwin自定义布局文件,popwin宽度,popwin高度)(注：若想指定位置则后两个参数必须给定值不能为WRAP_CONTENT)
        popWin = new PopupWindow(popView, w,  h);
        
        //设置popwindow点击其他地方隐藏
        ColorDrawable dw = new ColorDrawable(-00000);
        popWin.setBackgroundDrawable(dw);
        popWin.setOutsideTouchable(true);
    	popWin.setFocusable(true);
        popWin.update(); 
        popWin.setOnDismissListener(onDismissListener);
    }
	
	class PopWinAdapter extends BaseAdapter
	{
		private Context context = null;
		private LayoutInflater mInflater = null;
		private List<MyFileInfor> list = null;
		public PopWinAdapter(Context context, List<MyFileInfor> inforList)
		{
			mInflater = LayoutInflater.from(context);
			this.list = inforList;
		}
		/**
		*callbacks
		*/
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return inforList.size();
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return inforList.get(arg0);
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
				vh.imageViewMore = (ImageView)convertView.findViewById(R.id.iv_list_view_item_more);
				convertView.setTag(vh);
			}
			else 
				vh = (ViewHolder)convertView.getTag();
			
			MyFileInfor tempInfor = inforList.get(position);
			if(position == 0)
				vh.imageViewIcon.setBackgroundResource(R.drawable.disk_format);
			else
				vh.imageViewIcon.setBackgroundResource(R.drawable.file_folder);
			vh.textViewInfor.setText(tempInfor.getFileName());
			
			return convertView;
		}
		
		class ViewHolder
		{
			ImageView imageViewIcon = null;
			TextView textViewInfor = null;
			ImageView imageViewMore = null;
		}
	}
}
 
