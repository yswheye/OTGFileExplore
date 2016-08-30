package org.yyu.msi.activity;

import org.yyu.msi.R;
import org.yyu.msi.activity.RightFragment.SettingAdapter.ViewHolder;
import org.yyu.msi.common.Setting;
import org.yyu.msi.entity.Global;
import org.yyu.msi.utils.MyToast;
import org.yyu.msi.utils.MyViewUtil;
import org.yyu.msi.view.MyAlertDialog;
import org.yyu.msi.view.MyInforDialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;



public class RightFragment extends Fragment implements OnClickListener{

	private ListView lvSetting = null;
	private View parentView = null;
	
	private FileInforFragment fileInforFragment = null;
	private MyAlertDialog alertDialog = null;
	private MyInforDialog inforDialog = null;
	private MyToast mToast = null;
	private String[] settings = null;
	
	private final int MSG_CLEAR_CACHE_START = 0;
	private final int MSG_CLEAR_CACHE_FINISH = 1;
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MSG_CLEAR_CACHE_START)
			{
				dismissDialog();
				alertDialog = new MyAlertDialog(getActivity());
				alertDialog.setTitle(R.string.common_clear_cache);
				alertDialog.setInfor(R.string.common_clear_cache_loading);
				alertDialog.showProgressDialog(parentView);
			}
			else if(msg.what == MSG_CLEAR_CACHE_FINISH)
			{
				mToast.show(R.string.common_clear_cache_finish);
				dismissDialog();
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		parentView = inflater.inflate(R.layout.main_right_fragment, container,false);
		initView(parentView);
		return parentView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mToast = new MyToast(getActivity());
	}

	private void initView(View view) {
		// title
		lvSetting = (ListView)view.findViewById(R.id.lv_setting_fragment_infor);
		TextView mTitleName = (TextView) view.findViewById(R.id.menu_title);
		mTitleName.setText(R.string.common_set);
		
		settings = getResources().getStringArray(R.array.Settings);
		
		lvSetting.setAdapter(new SettingAdapter(getActivity(), settings));
		
		setListener();
	}

	public void setFileInforFragment(FileInforFragment fileInforFragment)
	{
		this.fileInforFragment = fileInforFragment;
	}
	
	private void setListener() 
	{
		lvSetting.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3)
			{
				// TODO Auto-generated method stub
				ViewHolder vh = (ViewHolder)view.getTag();
				
				if(position == 1)//显示缩略图
				{
					boolean status = Setting.isGridView(getActivity());
					vh.tBtn.setChecked(!status);
					Setting.setGridView(getActivity(), vh.tBtn.isChecked());
					
					notifyFileChange();
				}
				else if(position == 2)//显示隐藏文件
				{
					boolean status = Setting.isHide(getActivity());
					vh.tBtn.setChecked(!status);
					Setting.hideFile(getActivity(), vh.tBtn.isChecked());

					filterFiles();
				}
				else if(position == 3)//过滤小图片
				{
					boolean status = Setting.isFilter(getActivity());
					vh.tBtn.setChecked(!status);
					Setting.setFilter(getActivity(), vh.tBtn.isChecked());
					
					filterFiles();
				}
				else if(position == 4)//排序
				{
			
				}
				else if(position == 5)//清除缓存
				{
					showClearCacheDialog();
				}
				else if(position == 0)//帮助
				{
					showHelpDialog();
				}
				else if(position == 6)//后台进度
				{
					//showUpdateDialog();
				}
			}
		});
	}

	private void notifyFileChange()
	{
		if(fileInforFragment != null)
			fileInforFragment.notifyDataSetChanged();
	}
	private void filterFiles()
	{
		if(fileInforFragment != null)
			fileInforFragment.filterFiles();
	}
	
	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.btn_alert_dialog_confirm)//确定
		{
			clearCache();
			dismissDialog();
		}
		else if(v.getId() == R.id.btn_alert_dialog_cancel)//取消
		{
			dismissDialog();
		}
	}

	private void clearCache()
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				MyViewUtil.sendMessage(handler, MSG_CLEAR_CACHE_START);
				Global.imageWorker.mImageCache.clearCaches();
				MyViewUtil.sendMessage(handler, MSG_CLEAR_CACHE_FINISH);
			}
		}).start();
	}
	
	private void showHelpDialog()
	{
		inforDialog = new MyInforDialog(getActivity());
		inforDialog.setTitle(R.string.common_help);
		
		inforDialog.showAlertDialog(parentView); 
	}
	
	private void showClearCacheDialog()
	{
		alertDialog = new MyAlertDialog(getActivity());
		alertDialog.setTitle(R.string.common_clear_cache);
		alertDialog.setInfor(R.string.common_clear_cache_cinfirm);
		alertDialog.setOnClickListener(this);
		
		alertDialog.showAlertDialog(parentView); 
	}
	private void showUpdateDialog()
	{
		alertDialog = new MyAlertDialog(getActivity());
		alertDialog.setTitle(R.string.check_update);
		alertDialog.setInfor(R.string.check_update_searching);
		alertDialog.setOnClickListener(this);
		
		alertDialog.showProgressDialog(parentView); 
	}
	private void dismissDialog()
	{
		if(alertDialog != null && alertDialog.isShowing())
		{
			alertDialog.dismiss();
			alertDialog = null;
		}
	}
	
	class SettingAdapter extends BaseAdapter
	{
		private Context context = null;
		private LayoutInflater mInflater = null;
		private String[] infors = null;
		public SettingAdapter(Context context, String[] infors)
		{
			this.context = context;
			this.infors = infors;
			this.mInflater = LayoutInflater.from(context);;
		}
		/**
		*callbacks
		*/
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return settings.length;
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return settings[arg0];
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
				convertView = mInflater.inflate(R.layout.right_fragment_item, null);
				
				vh.tvSubInfor = (TextView)convertView.findViewById(R.id.tv_setting_item_sub_infor);
				vh.tvInfor = (TextView)convertView.findViewById(R.id.tv_setting_item_infor);
				vh.ivMore = (ImageView)convertView.findViewById(R.id.iv_setting_item_more);
				vh.tBtn = (ToggleButton)convertView.findViewById(R.id.tb_set_item);
				
				convertView.setTag(vh);
			}
			else
				vh = (ViewHolder)convertView.getTag();
			
			vh.tvInfor.setText(settings[position]);
			if(position == 1 || position == 2 || position == 3)
			{
				vh.ivMore.setVisibility(View.GONE);
				vh.tBtn.setVisibility(View.VISIBLE);
				setStatus(vh, position);
			}
			else
			{
				vh.ivMore.setVisibility(View.VISIBLE);
				vh.tBtn.setVisibility(View.GONE);
			}
			
			if(position == 3)
			{
				vh.tvSubInfor.setText(R.string.common_filter_pic);
				vh.tvSubInfor.setVisibility(View.VISIBLE);
			}
			
			if(position == 5)
			{
				vh.tvSubInfor.setText(R.string.common_clear_cahces);
				vh.tvSubInfor.setVisibility(View.VISIBLE);
			}
			if(position != 3 && position != 5)
				vh.tvSubInfor.setVisibility(View.GONE);
				
			return convertView;
		}
		
		private void setStatus(ViewHolder vh, int position)
		{
			if(position == 1)
				vh.tBtn.setChecked(Setting.isGridView(getActivity()));
			else if(position == 2)
				vh.tBtn.setChecked(Setting.isHide(getActivity()));
			else if(position == 3)
				vh.tBtn.setChecked(Setting.isFilter(getActivity()));
		}
		
		class ViewHolder
		{
			TextView tvSubInfor = null;
			TextView tvInfor = null;
			ImageView ivMore = null;
			ToggleButton tBtn = null;
		}
	}
}
