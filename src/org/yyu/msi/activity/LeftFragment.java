package org.yyu.msi.activity;

import java.util.ArrayList;
import java.util.List;

import org.yyu.msi.R;
import org.yyu.msi.common.StorageState;
import org.yyu.msi.entity.DiskInfor;
import org.yyu.msi.listener.FragmentKeyListener;
import org.yyu.msi.listener.IDiskScanListener;
import org.yyu.msi.utils.MySystemUtil;
import org.yyu.msi.utils.MyToast;
import org.yyu.msi.utils.MyViewUtil;
import org.yyu.msi.view.MyAlertDialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yyu.msi.slide.SlidingMenu;

public class LeftFragment extends BaseFragment implements OnClickListener, FragmentKeyListener
{
	private ListView lvDisk;
	private Button btnDiskRemove = null;
	private View view;
	private SlidingMenu mSlidingMenu;
	private DiskAdapter diskAdapter = null;
	private MyAlertDialog myAlertDialog = null;
	private MyToast toast = null;
	private FileInforFragment fileInforFragment = null; 
	private ArrayList<DiskInfor> diskInfoList = new ArrayList<DiskInfor>();
	private boolean isEdit = false;
	private boolean isRunning = false;
	private boolean isStorageEdit = false;
	private int selectedPosition = 0;
	private long touchTime = 0;
	
	private FragmentKeyListener fragListener = null;
	private IDiskScanListener scanListener = null;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == StorageState.INIT_START)
			{
				
			}
			else if(msg.what == StorageState.INIT_FINISH)
			{
				ArrayList<DiskInfor> tempInforList = (ArrayList<DiskInfor>)msg.obj;
				if(tempInforList.size() > 0)
				{
					lvDisk.setVisibility(View.VISIBLE);
					
					diskInfoList.clear();
					diskInfoList.addAll(tempInforList);
					diskAdapter.notifyDataSetChanged();
					
					loadFileInfor(0);
				}
				else
					lvDisk.setVisibility(View.GONE);
			}
			else if(msg.what == StorageState.MEDIA_MOUNTED)
			{
				//toast.show("MEDIA_MOUNTED-->"+msg.obj);
				if(!isStorageEdit)
					getDiskInforList(null);
			}
			else if(msg.what == StorageState.MEDIA_CHECKING)
			{
				//toast.show("MEDIA_CHECKING-->"+msg.obj);
			}
			else if(msg.what == StorageState.MEDIA_EJECT)
			{
				toast.show("MEDIA_EJECT-->"+msg.obj);
				if(!isStorageEdit)
					getDiskInforList(msg.obj.toString().replace("file://", ""));
			}
			else if(msg.what == StorageState.MEDIA_REMOVED)
			{
				toast.show("REMOVED-->"+msg.obj);
				/*if(!isStorageEdit)
					getDiskInforList(null);*/
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		view = inflater.inflate(R.layout.main_left_fragment, container,
				false);
		
		initView(view);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fragListener = this;
		
		BaseFragmentActivity baseFragment = (BaseFragmentActivity) getActivity();
		mSlidingMenu = baseFragment.getSlideMenu();
		
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if(isStorageEdit)
		{
			getDiskInforList(null);
			isStorageEdit = false;
		}
		
		if(fileInforFragment != null)
			fileInforFragment.notifyDataSetChanged();
	}
	
	public void initView(View view) 
	{
		
		toast = new MyToast(getActivity());
		// title
		TextView title1 = (TextView) view.findViewById(R.id.menu_title);
		title1.setText(R.string.local_disk);
		btnDiskRemove = (Button)view.findViewById(R.id.btn_left_fragment_remove);
		lvDisk = (ListView) view.findViewById(R.id.lv_main_left_fragment_disk);
		
		btnDiskRemove.setOnClickListener(this);
		
		diskAdapter = new DiskAdapter(getActivity(), diskInfoList);
		lvDisk.setAdapter(diskAdapter);
	
		getDiskInforList(null);
		
		lvDisk.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				
				selectedPosition = position;
				if(isEdit)
				{
					//diskInfor.setIsChecked(!diskInfor.isChecked());
					if(selectedPosition > 0)
						showRemoveDialog();
					else
						toast.show(R.string.can_not_remove_memstorage);
				}
				else
				{
					loadFileInfor(position);
				}
				diskAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	* @Description: 加载文件浏览
	* @param    
	* @return void 
	* @throws
	 */
	private void loadFileInfor(int position)
	{

		if(fileInforFragment == null)
		{
			fileInforFragment = new FileInforFragment(diskInfoList.get(position));
			FragmentTransaction mFragementTransaction = ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction();
			mFragementTransaction.replace(R.id.main_center_fragment, fileInforFragment);
			mFragementTransaction.commit();
			((BaseFragmentActivity) getActivity()).setFragmentKeyListener(fragListener);
		}
		else
			fileInforFragment.initData(diskInfoList.get(position));
		if(scanListener != null)
			scanListener.onScanFinish(fileInforFragment);
	}
	
	public Handler getHandler()
	{
		return handler;
	}
	
	public FileInforFragment getFileInforFragment()
	{
		return fileInforFragment;
	}
	public void setIDiskScanListener(IDiskScanListener scanListener)
	{
		this.scanListener = scanListener;
	}
	
	
	
	/**
	* @Description: 更新磁盘列表
	* @param    
	* @return void 
	* @throws
	 */
	private void getDiskInforList(final String name)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				
				if(isRunning)
					return;
				isRunning = true;
				
				ArrayList<String> diskList = MySystemUtil.getStorageDirectoriesArrayList();
				int size = diskList.size();
				ArrayList<DiskInfor> tempInfoList = new ArrayList<DiskInfor>();
				for(int i=0;i<size;i++)
				{
					DiskInfor diskInfor = MySystemUtil.getInforFromSD(diskList.get(i));
					if(name == null || !diskInfor.getDiskDir().equals(name))
					{
						if(i == 0)
							diskInfor.setDiskName(getString(R.string.local_disk_inside));
						tempInfoList.add(diskInfor);
					}
				}
				MyViewUtil.sendMessage(handler, StorageState.INIT_FINISH, tempInfoList);
				isRunning = false;
			}
		}).start();
		
	}
	
	class DiskAdapter extends BaseAdapter
	{

		private LayoutInflater mInflater = null;
		
		private List<DiskInfor> list = null;
		public DiskAdapter(Context context, List<DiskInfor> list)
		{
			mInflater = LayoutInflater.from(context);
			
			this.list = list;
		}
		/**
		*callbacks
		*/
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return list.size();
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
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
			LeftViewHolder vh = null;
			if(convertView == null)
			{
				vh = new LeftViewHolder();
				convertView = mInflater.inflate(R.layout.main_left_item, null);
				
				vh.tvName = (TextView)convertView.findViewById(R.id.tv_main_left_item_name);
				vh.tvSize = (TextView)convertView.findViewById(R.id.tv_main_left_item_size);
				vh.pbSize = (ProgressBar)convertView.findViewById(R.id.pb_main_left_item_size);
				vh.imageView = (ImageView)convertView.findViewById(R.id.iv_main_left_item);
				
				convertView.setTag(vh);
			}
			else
				vh = (LeftViewHolder)convertView.getTag();
			
			DiskInfor diskInfor = list.get(position);
			vh.tvName.setText(diskInfor.getDiskName());
			vh.tvSize.setText(diskInfor.getDiskRemain() + "/" + diskInfor.getDiskVolume());
			vh.pbSize.setProgress((int) diskInfor.getDiskProgress());
			
			if(isEdit)
			{
				if(position == 0)
					vh.imageView.setVisibility(View.GONE);
				else
					vh.imageView.setVisibility(View.VISIBLE);
				MyViewUtil.setViewSelcted(convertView, diskInfor.isChecked());
			}
			else
			{
				if(position == selectedPosition)
				{
					MyViewUtil.setViewSelcted(convertView, true);
				}
				else
				{
					MyViewUtil.setViewSelcted(convertView, false);
				}
				vh.imageView.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		
		class LeftViewHolder
		{
			TextView tvName = null;
			TextView tvSize = null;
			ImageView imageView = null;
			ProgressBar pbSize = null;
		}
	}

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == btnDiskRemove)//移除设备
		{
			if(MySystemUtil.haveRoot().equals("0"))//具有root权限,采用命令方式操作存储器
			{
				if(isEdit)
				{
					hideDiskEdit();
				}
				else
				{
					showDiskEdit(v);
				}
				
				diskAdapter.notifyDataSetChanged();
			}
			else//没有root权限就进入设置页面操作
				editStorage();
		}
		else if(v.getId() == R.id.btn_alert_dialog_confirm)//确定
		{
			
			if(isEdit)//内部命令移除设备
			{
				removeDisk(selectedPosition);
			}
			else//进入设置页面移除设备
			{
				Intent intent = null;
	            //判断手机系统的版本  即API大于10 就是3.0或以上版本 
	            if(android.os.Build.VERSION.SDK_INT > 10)
	            {
	                intent = new Intent(android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS);
	            }
	            else
	            {
	                intent = new Intent();
	                ComponentName component = new ComponentName("com.android.settings","com.android.settings.MediaFormat");
	                intent.setComponent(component);
	                intent.setAction("android.intent.action.VIEW");
	            }
	            startActivity(intent);
	            isStorageEdit = true;
	            dismissDialog();
			}
		}
		else if(v.getId() == R.id.btn_alert_dialog_cancel)//取消
		{
			dismissDialog();
			
			isStorageEdit = false;
		}
	}
	
	private void showDiskEdit(View v)
	{
		isEdit = true;
		btnDiskRemove.setText(R.string.common_cancel);
		btnDiskRemove.setBackgroundResource(R.drawable.view_item_selector_1);
	}
	private void hideDiskEdit()
	{
		isEdit = false;
		btnDiskRemove.setText(R.string.edit_safe);
		btnDiskRemove.setBackgroundResource(R.drawable.view_item_selector);
	}
	/**
	* @Description: 命令卸载存储设备
	* @param @param pos   
	* @return void 
	* @throws
	 */
	private void removeDisk(int pos)
	{
		String result = MySystemUtil.execRootCmdSilent("umount " + diskInfoList.get(pos).getDiskDir());
		if(result.equals("0"))
		{
			getDiskInforList(null);
		}
		else//有root权限，但是系统拒绝执行当前命令
		{
			toast.show(getActivity().getString(R.string.remove_fail) + "-->" + result);
			editStorage();
		}
		dismissDialog();
	}
	
	/**
	* @Description:  显示卸载对话框
	* @param @param context   
	* @return void 
	* @throws
	 */
    public void editStorage()
    {
         dismissDialog();
		 myAlertDialog = new MyAlertDialog(getActivity());
		 myAlertDialog.setTitle(R.string.storage_alert_title);
		 myAlertDialog.setInfor(R.string.storage_alert_infor);
		 myAlertDialog.setConfirmInfor(R.string.common_set);
		 myAlertDialog.setCancelInfor(R.string.common_return);
		 myAlertDialog.setOnClickListener(this);
		 myAlertDialog.showAlertDialog(view); 
     }
    public void showRemoveDialog()
    {
    	dismissDialog();
    	myAlertDialog = new MyAlertDialog(getActivity());
    	myAlertDialog.setTitle(R.string.remove_title);
    	myAlertDialog.setInfor(R.string.remove_infor);
    	myAlertDialog.setConfirmInfor(R.string.common_confirm);
    	myAlertDialog.setCancelInfor(R.string.common_cancel);
    	myAlertDialog.setOnClickListener(this);
    	myAlertDialog.showAlertDialog(view); 
    }
	
	 private void dismissDialog()
	 {
		 if(myAlertDialog != null && myAlertDialog.isShowing())
		 {
			 myAlertDialog.dismiss();
			 myAlertDialog = null;
		 }
	 }
	  
	/**
	*callbacks
	*/
	@Override
	public void onKeyBack()
	{
		// TODO Auto-generated method stub
		
		if(mSlidingMenu.isFirstMenuShowing())//!fileInforFragment.isExit() || 
		{
			if((System.currentTimeMillis() - touchTime) < 2000)
			 {
				 getActivity().finish();
		     		
				 android.os.Process.killProcess(android.os.Process.myPid());
				 System.exit(0);
			 }
			 else
			 {
				 Toast.makeText(getActivity(), R.string.common_exit_hint, Toast.LENGTH_LONG).show();
			 }
			 touchTime = System.currentTimeMillis();
		}
		else
			fileInforFragment.onKeyBack();
	}
}

