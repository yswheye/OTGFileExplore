package org.yyu.msi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.yyu.msi.R;
import org.yyu.msi.common.Setting;
import org.yyu.msi.entity.DiskInfor;
import org.yyu.msi.entity.FileDeleteEntity;
import org.yyu.msi.entity.FileTransferEntity;
import org.yyu.msi.entity.FileType;
import org.yyu.msi.entity.FileZipEntity;
import org.yyu.msi.entity.Global;
import org.yyu.msi.entity.ScanFileEntity;
import org.yyu.msi.listener.IOperationListener;
import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.utils.MyFileUtil;
import org.yyu.msi.utils.MyStringUtil;
import org.yyu.msi.utils.MySystemUtil;
import org.yyu.msi.utils.MyToast;
import org.yyu.msi.utils.MyViewUtil;
import org.yyu.msi.view.FileAdapter;
import org.yyu.msi.view.FileAdapter.AdapterViewHolder;
import org.yyu.msi.view.FileEditView;
import org.yyu.msi.view.FileInforDialog;
import org.yyu.msi.view.MyAlertDialog;
import org.yyu.msi.view.MyProgressDialog;
import org.yyu.msi.view.PopListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.yyu.msi.slide.SlidingMenu;


@SuppressLint("ValidFragment")
public class FileInforFragment extends BaseFragment implements OnClickListener, 
OnDismissListener, IOperationListener, OnScrollListener
{

	private View bgView = null;
	private GridView gridView = null;
	private TextView mTitleName = null;
	private TextView tvCurInfor = null;
	protected static SlidingMenu mSlidingMenu;
	private ImageButton ibTitleLeft = null;
	private ImageButton ibTitleRight = null;
	private TextView tvSelected = null;
	private PopListView popListView = null;
	private View parentView = null;
	private FileEditView fileEditView = null;//底部文件操作集合
	private Context context = null;
	private FileAdapter fileAdapter = null;
	private MyToast myToast = null;
	private ArrayList<MyFileInfor> fileList = new ArrayList<MyFileInfor>();
	private ArrayList<MyFileInfor> dirInforList = new ArrayList<MyFileInfor>();
	private ArrayList<MyFileInfor> operationList = new ArrayList<MyFileInfor>();
	private DiskInfor diskInfor = null;
	private MyFileInfor rootFileInfor = null;
	private MyFileInfor lastFileInfor = null;
	private MyAlertDialog myAlertDialog = null;
	private FileInforDialog fileInforDialog = null;
	private MyProgressDialog progressDialog = null;
	private FileTransferEntity fileTransfer = null;
	private FileDeleteEntity fileDelete = null;
	private FileZipEntity fileZip = null;
	private ScanFileEntity scanFileEntity = null;
	
	private boolean isEdit = false;
	private boolean isFolderSelect = false;
	private boolean isDelete = false;
	private boolean isCopy = false;
	private boolean isCut = false;
	private boolean isZip = false;
	private boolean isCreate = false;
	private boolean isRename = false;
	private boolean isSearch = false;
	private boolean isSelectAll = false;
	private String curDirPath = null;
	private String rootDir = null;
	private long totalSize = 0;
	private int mFirstVisibleItem = 0;
	private int mVisibleItemCount  = 0;
	private boolean isFirstEnter = true;
	
	private final int MSG_GET_FILE_START = 0;//扫描文件开始
	private final int MSG_GET_FILE_FINISH = 1;//扫描文件完成
	private final int OPERATION_START = 2;//操作开始
	private final int OPERATION_FINISH = 3;//操作结束
	private final int OPERATION_PROGRESS = 4;//操作进度
	private final int OPERATION_SINGLE_START = 5;//单个开始
	private final int OPERATION_SINGLE_FINISH = 6;//单个结束
	private final int OPERATION_NO_SPACE = 7;//空间不够
	private final int OPERATION_WRITE_PERMISSION = 8;//没有写权限
	private final int OPERATION_DELETE_ONE = 9;//删除单个
	private final int OPERATION_FILE_NOT_FIND = 10;//文件不存在
	private final int OPERATION_PREPARE = 11;//准备中
	
	@SuppressLint("ValidFragment")
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MSG_GET_FILE_START)
			{
				
			}
			else if(msg.what == MSG_GET_FILE_FINISH)
			{
				List<MyFileInfor> tempList = (List<MyFileInfor>)msg.obj;
				
				if(tempList.size() > 0)
				{
					gridView.setVisibility(View.VISIBLE);
					fileList.clear();
					fileList.addAll(tempList);
					
					notifyDataSetChanged();
					
					tempList.clear();
					
					showSelectedCount();
				}
				else
				{
					fileList.clear();
					gridView.setVisibility(View.GONE);
				}
				tvCurInfor.setText("文件:" + scanFileEntity.getFileCount() 
						 + "    文件夹:" + scanFileEntity.getFolderCount());
			}
			else if(msg.what == OPERATION_PREPARE)//操作准备
			{
				if(isZip)
					showzipDialogprogress();
				else if(isDelete)
					showDeleteprogress();
				else if(isCut)
					showCutprogress();
				else if(isCopy)
					showCopyprogress();
				if(progressDialog != null)
					progressDialog.setTitle(R.string.preparing);
			}
			else if(msg.what == OPERATION_START)//操作开始
			{
				if(isZip)
					progressDialog.setTitle(R.string.zip_compressing);
				else if(isDelete)
					progressDialog.setTitle(R.string.edit_delete_working);
				else if(isCopy || isCut)
					progressDialog.setTitle(R.string.transfer_is_working);
				
				totalSize = (Long)msg.obj;
			}
			else if(msg.what == OPERATION_FINISH)//操作结束
			{
				if(isDelete)
				{
					deleteFinish();
				}
				if(isCopy || isCut)
				{
					copyFinish();
				}
				if(isZip)
				{
					
				}
				dismissDialog();
			}
			else if(msg.what == OPERATION_PROGRESS)//操作进度
			{
				showProgress(msg);
			}
			else if(msg.what == OPERATION_SINGLE_START)//单个操作开始
			{
				if(progressDialog != null)
					progressDialog.setOneProgress((String)msg.obj);
			}
			else if(msg.what == OPERATION_SINGLE_FINISH)//单个操作结束
			{
				if(MyStringUtil.getHeadByTag("/", (String)msg.obj).equals(curDirPath))
					getFileList(curDirPath);
			}
			else if(msg.what == OPERATION_NO_SPACE)//空间不足
			{
				myToast.show(R.string.common_no_space);
			}
			else if(msg.what == OPERATION_WRITE_PERMISSION)//没有写权限
			{
				myToast.show(R.string.common_write_permission);
			}
			else if(msg.what == OPERATION_DELETE_ONE)//单个删除
			{
				if(isDelete)
					deleteProgress(msg);
			}
			else if(msg.what == OPERATION_FILE_NOT_FIND)//文件不存在
			{
				myToast.show(R.string.file_not_exsit);
				dismissDialog();
			}
		};
	};
	
	/**
	* @Description: 更新删除文件进度
	* @param @param msg   
	* @return void 
	* @throws
	 */
	private void deleteProgress(Message msg)
	{
		Bundle bundle = (Bundle)msg.obj;
		long deleteFinish = bundle.getLong("PROGRESS");
		String deleteFile = bundle.getString("FILE_URL");
		
		String sizePercent = getString(R.string.finished) + MyStringUtil.getFormatSize(deleteFinish) + "    "
				+ getString(R.string.total) + MyStringUtil.getFormatSize(totalSize);
		if(progressDialog != null)
		{
			progressDialog.setOneProgress(deleteFile);
			progressDialog.setProgress(sizePercent, MyStringUtil.getProgress(deleteFinish, totalSize));
		}
	}
	/**
	* @Description: 删除完成
	* @param    
	* @return void 
	* @throws
	 */
	private void deleteFinish()
	{
		myToast.show(R.string.delete_success);
		operationList.clear();
		reset();
	}
	
	/**
	* @Description: 更新复制文件进度
	* @param @param msg   
	* @return void 
	* @throws
	 */
	private void showProgress(Message msg)
	{
		long progress = (Long)msg.obj;
		String sizePercent = getString(R.string.finished) + MyStringUtil.getFormatSize(progress) + "    "
				+ getString(R.string.total) + MyStringUtil.getFormatSize(totalSize);
		if(progressDialog != null)
		{
			int temp = MyStringUtil.getProgress(progress, totalSize);
			//if(temp % 5 == 0)
			progressDialog.setProgress(sizePercent, temp);
		}
	}
	/**
	* @Description: 复制完成
	* @param    
	* @return void 
	* @throws
	 */
	private void copyFinish()
	{
		if(isCut)
		{
			operationList.clear();
			showSelectedCount();
		}
		getFileList(curDirPath);
		myToast.show(R.string.finished);
	}
	
	public FileInforFragment(DiskInfor diskInfor)
	{
		this.diskInfor = diskInfor;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		
		parentView = inflater.inflate(R.layout.file_infor_fragment, container,false);
		
		myToast = new MyToast(getActivity());
		
		initView(parentView);
		
		return parentView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		context = getActivity();
		
		BaseFragmentActivity baseFragment = (BaseFragmentActivity) getActivity();
		mSlidingMenu = baseFragment.getSlidingMenu();
		mSlidingMenu.showMenu();
		context = getActivity();
		
		fileTransfer = new FileTransferEntity();
		fileDelete = new FileDeleteEntity();
		fileZip = new FileZipEntity();
		
		fileAdapter = new FileAdapter(context, fileList);
		gridView.setAdapter(fileAdapter);
		
		gridView.setOnScrollListener(this);
		
		initData(diskInfor);
	}
	
	public void initData(DiskInfor diskInfor)
	{
		
		lastFileInfor = null;
		
		if(isEdit)
			operationList.clear();
		
		dirInforList.clear();
		
		mTitleName.setText(diskInfor.getDiskName());
		
		rootDir = diskInfor.getDiskDir();
		
		//添加根目录
		rootFileInfor = new MyFileInfor();
		rootFileInfor.setFileUrl(rootDir);
		rootFileInfor.setFileName(diskInfor.getDiskName());
		
		getFileList(diskInfor.getDiskDir());
		
		if(isFolderSelect)
			showFolderSelect(false);
		else
			hideFolderSelect(false);
		if(isEdit)
			showEditState();
		else
			cancelEdit(false);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		
		if(popListView != null)
			popListView.dismiss();
		dismissDialog();
		
	}
	
	private void initView(View view) 
	{
		bgView = view.findViewById(R.id.bg_file_infor_fragment);
		ibTitleLeft = ((ImageButton) view.findViewById(R.id.ivTitleBtnLeft));
		ibTitleRight = ((ImageButton) view.findViewById(R.id.ivTitleBtnRigh));
		fileEditView = (FileEditView)view.findViewById(R.id.file_edit_view_bottom);
		
		mTitleName = (TextView) view.findViewById(R.id.ivTitleName);
		tvCurInfor = (TextView) view.findViewById(R.id.tv_file_infor_fragment_infor);
		tvSelected = (TextView)view.findViewById(R.id.tv_file_infor_fragment_select);
		
		
		ibTitleRight.setOnClickListener(this);
		ibTitleLeft.setOnClickListener(this);
		fileEditView.setOnClickListener(this);
		
		popListView = new PopListView(getActivity());
		popListView.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss()
			{
				// TODO Auto-generated method stub
				bgView.setVisibility(View.GONE);
				Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
				bgView.setAnimation(animation);
			}
		});
		popListView.getListView().setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3)
			{
				// TODO Auto-generated method stub
				MyFileInfor tempInfor = dirInforList.get(position);
				
				mTitleName.setText(tempInfor.getFileName());
				
				popListView.dismiss();
				
				lastFileInfor = tempInfor;//记录上次点击的目录
				
				curDirPath = tempInfor.getFileUrl();
				
				removeEndirs(position);
				
				reset();
			}
		});
		
		gridView = (GridView)view.findViewById(R.id.gv_file_infor_fragment);
		
		gridView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3)
			{
				// TODO Auto-generated method stub
				MyFileInfor tempInfor = fileList.get(position);
				if(isEdit)
				{
					AdapterViewHolder vh = (AdapterViewHolder)view.getTag();
					tempInfor.setIsChecked(!vh.checkBox.isChecked());
					fileAdapter.notifyDataSetChanged(isEdit);
					if(!vh.checkBox.isChecked())
						operationList.add(tempInfor);
					else
						operationList.remove(tempInfor);
					showSelectedCount();
				}
				else
				{
					if(tempInfor.isFolder())
					{

						if(dirInforList.size() == 0)
							dirInforList.add(rootFileInfor);
						
						if(lastFileInfor != null)
						{
							dirInforList.add(lastFileInfor);
						}
						
						lastFileInfor = tempInfor;//记录上次点击的目录
						
						mTitleName.setText(tempInfor.getFileName());
						curDirPath = tempInfor.getFileUrl();
						getFileList(curDirPath);
					}
					else
					{
						if(isFolderSelect)
							showToast(R.string.please_select_folder);
						else
						{
							if(MyFileUtil.isPicture(tempInfor.getFileUrl()))
							{
								Bundle bundle = new Bundle();
								bundle.putString("FILE_DIR", tempInfor.getFileUrl());
								MyViewUtil.startActivity(getActivity(), ImageViewActivity.class, bundle);
							}
							else if(MyFileUtil.isVideo(tempInfor.getFileUrl()))
							{
								Bundle bundle = new Bundle();
								bundle.putString("FILE_DIR", tempInfor.getFileUrl());
								MyViewUtil.startActivity(getActivity(), VideoPlayActivity.class, bundle);
							}
							else
							{
								Intent intent = MyFileUtil.openFile(tempInfor.getFileUrl());
								startActivity(intent);
							}
						}
					}
				}
			}
		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long arg3)
			{
				// TODO Auto-generated method stub
				
				showFileDetailDialog(fileList.get(position));
				
				return false;
			}
		});
		
	}
	
	public void notifyDataSetChanged()
	{
		isFirstEnter = true; 
		boolean isGridView = Setting.isGridView(context);
		gridView.setNumColumns(isGridView?3:1);
		
		gridView.setAdapter(fileAdapter);
		fileAdapter.notifyDataSetChanged(isEdit);
	}
	public void filterFiles()
	{
		getFileList(curDirPath);
	}
	
	private void showSelectedCount()
	{
		if(operationList.size() > 0)
		{
			tvSelected.setVisibility(View.VISIBLE);
			tvSelected.setText(getString(R.string.common_selected) + operationList.size());
		}
		else
			tvSelected.setVisibility(View.GONE);
	}
	
	/**
	* @Description: 移除指定位置后的目录
	* @param @param position   
	* @return void 
	* @throws
	 */
	private void removeEndirs(int position)
	{
		int size = dirInforList.size();
		for(int i = position;i<size;i++)
		{
			lastFileInfor = dirInforList.get(position);
			dirInforList.remove(position);
		}
		if(dirInforList.isEmpty())
			lastFileInfor = null;
		popListView.notifyDatasetChanged();
	}
	private void getFileList(final String dirPath)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				MyViewUtil.sendMessage(handler, MSG_GET_FILE_START);
				boolean isHide = Setting.isHide(context);
				boolean isFilter = Setting.isFilter(context);
				scanFileEntity = MyFileUtil.getLocalFiles(context, dirPath, 
						isHide, isFilter);
				curDirPath = dirPath;
				
				
				MyViewUtil.sendMessage(handler, MSG_GET_FILE_FINISH, scanFileEntity.getList());
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ivTitleBtnLeft)
		{
			if(dirInforList.size() == 0)
				mSlidingMenu.showMenu(true);
			else
			{
				popListView.showPopViewList(ibTitleLeft, dirInforList, MySystemUtil.getScreenSize(getActivity())[0]*2 / 3, 0);
				bgView.setVisibility(View.VISIBLE);
				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
				bgView.setAnimation(animation);
			}
		}
		else if(v.getId() == R.id.ivTitleBtnRigh)
		{
			mSlidingMenu.showSecondaryMenu(true);
		}
		else if(v.getId() == R.id.btn_alert_progress_cancel)
		{
			if(isDelete)
				fileDelete.stop();
			else if(isCopy || isCut)
				fileTransfer.stop();
			else if(isZip)
				fileZip.stop();
			dismissDialog();
		}
		else if(v.getId() == R.id.btn_alert_progress_hide)//隐藏进度,后台运行
		{
			dismissDialog();
		}
		else if(v.getId() == R.id.btn_alert_dialog_confirm)//确定
		{
			if(isDelete)//删除
				deleteFiles();
			else if(isCreate)//创建文件夹
			{
				String dirName = myAlertDialog.getInputInfor();
				File file = new File(curDirPath + "/" + dirName);
				if(file.exists())
					myToast.show(R.string.folder_has_exist);
				else
				{
					if(file.mkdirs())
					{
						myToast.show(R.string.create_success);
						getFileList(curDirPath);
						dismissDialog();
					}
					else
						myToast.show(R.string.create_fail);
				}
			}
			else if(isZip)//压缩文件
			{
				zip();
			}
		}
		else if(v.getId() == R.id.btn_alert_dialog_cancel)//取消
		{
			if(isZip)//选择压缩的目标文件夹
			{
				showFolderSelect(true);
			}
			dismissDialog();
		}
		else if(v.getId() == R.id.btn_progress_dialog_cancel)//取消当前操作
		{
			if(isZip)
				Global.util.getZipUtil().stop();
			dismissDialog();
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_create)//创建文件夹
		{
			showCreateFolderDialog();
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_search)//查找文件
		{
			Bundle bundle = new Bundle();
			bundle.putString("dir_path", curDirPath);
			MyViewUtil.startActivity(getActivity(), FileSearchActivity.class, bundle);
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_return)//返回上一级目录
		{
			if(dirInforList.size() > 0)
			{
				int pos = dirInforList.size() - 1;
				MyFileInfor infor = dirInforList.get(pos);
				getFileList(infor.getFileUrl());
				mTitleName.setText(infor.getFileName());
				removeEndirs(pos);
			}
			else
				mSlidingMenu.showMenu(true);
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_delete)//删除文件
		{
			if(operationList.size() > 0)
			{
				resetStatus();
				isDelete = true;
				showDeleteFileDialog();
			}
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_copy)//复制
		{
			
			if(operationList.size() > 0)
			{
				resetStatus();
				isCopy = true;
				fileTransfer.setSrcDir(curDirPath);
				showFolderSelect(true);
			}
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_cut)//剪切
		{
			if(operationList.size() > 0)
			{
				resetStatus();
				isCut = true;
				fileTransfer.setSrcDir(curDirPath);
				showFolderSelect(true);
			}
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_zip)//压缩
		{
			if(operationList.size() > 0)
			{
				resetStatus();
				isZip = true;
				showZipDialog();
			}
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_edit)//编辑
		{
			showEditState();
			
			fileEditView.startAnimation1(getActivity());
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_select_all)//全选/取消全选
		{
			if(fileList.size() > 0)
			{
				if(isSelectAll)
					unSelectAll();
				else
					selectAll();
			}
		}
		else if(v.getId() == R.id.btn_file_infor_fragment_cancel)
		{
			unSelectAll();
			cancelEdit(true);
			operationList.clear();
		}
		else if(v.getId() == R.id.btn_copy_cut_edit_paste)//粘贴
		{
			if(operationList.size() > 0)
				transfer();
		}
		else if(v.getId() == R.id.btn_copy_cut_edit_cancel)//取消粘贴
		{
			hideFolderSelect(true);
			showEditState();
			operationList.clear();
			showSelectedCount();
			unSelectAll();
		}
		else if(v.getId() == R.id.btn_copy_cut_edit_new)//新建
		{
			showCreateFolderDialog();
		}
	}
	
	private void selectAll()
	{
		isSelectAll = true;
		for(int i=0;i<fileList.size();i++)
		{
			MyFileInfor tempInfor = fileList.get(i);
			tempInfor.setIsChecked(true);
			fileList.set(i, tempInfor);
		}
		fileAdapter.notifyDataSetChanged();
		operationList.clear();
		operationList.addAll(fileList);
		showSelectedCount();
		fileEditView.updateSelectBtn(R.string.select_all_cancel);
	}
	private void unSelectAll()
	{
		isSelectAll = false;
		for(int i=0;i<fileList.size();i++)
		{
			MyFileInfor tempInfor = fileList.get(i);
			tempInfor.setIsChecked(false);
			fileList.set(i, tempInfor);
		}
		fileAdapter.notifyDataSetChanged();
		operationList.clear();
		showSelectedCount();
		fileEditView.updateSelectBtn(R.string.select_all);
	}
	
	/**
	* @Description: 显示粘贴状态
	* @param    
	* @return void 
	* @throws
	 */
	private void showFolderSelect(boolean isAnim)
	{
		isFolderSelect = true;
		isEdit = false;
		fileAdapter.notifyDataSetChanged(isEdit);
		
		fileEditView.showFolderSelect(isAnim);
	}
	/**
	* @Description: 隐藏粘贴状态
	* @param    
	* @return void 
	* @throws
	 */
	private void hideFolderSelect(boolean isAnim)
	{
		if(isFolderSelect)
			isEdit = true;
		isFolderSelect = false;
		fileAdapter.notifyDataSetChanged(isEdit);
		
		fileEditView.hideFolderSelect(isAnim);
	}
	
	/**
	* @Description: 显示编辑状态
	* @param    
	* @return void 
	* @throws
	 */
	private void showEditState()
	{
		isEdit = true;
		fileAdapter.notifyDataSetChanged(isEdit);
		fileEditView.showEditState();
	}
	/**
	* @Description: 取消编辑状态
	* @param    
	* @return void 
	* @throws
	 */
	private void cancelEdit(boolean isAnim)
	{
		isEdit = false;
		fileAdapter.notifyDataSetChanged(isEdit);
		fileEditView.cancelEdit();
		tvSelected.setVisibility(View.GONE);
		if(isAnim)
		{
			fileEditView.startAnimation1(getActivity());
		}
	}
	
	/**
	* @Description: 显示删除对话框
	* @param    
	* @return void 
	* @throws
	 */
	private void showDeleteFileDialog()
	{
		dismissDialog();
		myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle(R.string.edit_delete);
		myAlertDialog.setInfor(getString(R.string.delete_files_1) 
				+ operationList.size() + getString(R.string.delete_files_2));
		myAlertDialog.setOnClickListener(this);
		myAlertDialog.showAlertDialog(parentView); 
	}
	
	/**
	* @Description: 显示创建文件夹对话框
	* @param    
	* @return void 
	* @throws
	 */
	private void showCreateFolderDialog()
	{
		isCreate = true;
		dismissDialog();
		myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle(R.string.common_create_dir);
		myAlertDialog.setInputInfor(R.string.common_create_dir);
		myAlertDialog.setOnClickListener(this);
		myAlertDialog.showInputDialog(parentView); 
	}
	private void showRenameDialog(MyFileInfor fileInfor)
	{
		isRename = true;
		dismissDialog();
		myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle(R.string.edit_rename);
		myAlertDialog.setInputInfor(fileInfor.getFileName());
		myAlertDialog.setOnClickListener(this);
		myAlertDialog.showInputDialog(parentView); 
	}
	
	/**
	* @Description: 显示压缩文件夹对话框
	* @param    
	* @return void 
	* @throws
	 */
	private void showZipDialog()
	{
		dismissDialog();
		myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle(R.string.edit_zip);
		if(operationList.size() > 1)
			myAlertDialog.setInputInfor(MyStringUtil.getLastByTag("/", curDirPath));
		else if(operationList.size() == 1)
			myAlertDialog.setInputInfor(operationList.get(0).getFileName());
		myAlertDialog.setConfirmInfor(R.string.zip_save);
		myAlertDialog.setCancelInfor(R.string.zip_save_to);
		myAlertDialog.setOnClickListener(this);
		myAlertDialog.showInputDialog(parentView); 
	}
	
	/**
	* @Description: 显示粘贴进度
	* @param    
	* @return void 
	* @throws
	 */
	private void showCopyprogress()
	{
		dismissDialog();
		progressDialog = new MyProgressDialog(getActivity());
		progressDialog.setOnClickListener(this);
		progressDialog.setOnDismissListener(this);
		progressDialog.setFromDir(getString(R.string.from_dir) + fileTransfer.getSrcDir());
		progressDialog.setToDir(getString(R.string.to_dir) + fileTransfer.getDstDir());
		progressDialog.showDialog(parentView);
	}
	private void showCutprogress()
	{
		dismissDialog();
		progressDialog = new MyProgressDialog(getActivity());
		progressDialog.setOnClickListener(this);
		progressDialog.setOnDismissListener(this);
		progressDialog.setFromDir(getString(R.string.from_dir) + fileTransfer.getSrcDir());
		progressDialog.setToDir(getString(R.string.to_dir) + fileTransfer.getDstDir());
		progressDialog.showDialog(parentView);
	}
	private void showDeleteprogress()
	{
		dismissDialog();
		progressDialog = new MyProgressDialog(getActivity());
		progressDialog.hideDir();
		progressDialog.setOnClickListener(this);
		progressDialog.setOnDismissListener(this);
		progressDialog.showDialog(parentView);
	}
	private void showzipDialogprogress()
	{
		dismissDialog();
		progressDialog = new MyProgressDialog(getActivity());
		progressDialog.hideDir();
		progressDialog.setOnClickListener(this);
		progressDialog.setOnDismissListener(this);
		progressDialog.showDialog(parentView);
	}
	
	private void showFileDetailDialog(final MyFileInfor fileInfor)
	{
		operationList.clear();
		operationList.add(fileInfor);
		
		fileInforDialog = new FileInforDialog(getActivity());
		fileInforDialog.setTitle(fileInfor.getFileName());
		//fileInforDialog.setOnDismissListener(this);
		fileInforDialog.showDetailDialog(parentView); 
		
		fileInforDialog.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3)
			{
				// TODO Auto-generated method stub
				operationList.add(fileInfor);
				fileInforDialog.dismiss();
				if(position == 0)//删除
				{
					resetStatus();
					isDelete = true;
					showDeleteFileDialog();
				}
				else if(position == 1)//复制
				{
					resetStatus();
					isCopy = true;
					fileTransfer.setSrcDir(curDirPath);
					showFolderSelect(true);
				}
				else if(position == 2)//剪切
				{
					resetStatus();
					isCut = true;
					fileTransfer.setSrcDir(curDirPath);
					showFolderSelect(true);
				}
				else if(position == 3)//压缩
				{
					resetStatus();
					isZip = true;
					showZipDialog();
				}
				else if(position == 4)//重命名
				{
					resetStatus();
					showRenameDialog(fileInfor);
				}
				else if(position == 5)//详情
				{
					if(fileInfor.getFileType() == FileType.TYPE_FOLDER)
						fileInforDialog.showFolderDetailDialog(getActivity(), parentView, fileInfor);
					else
						fileInforDialog.showFileDetailDialog(getActivity(), parentView, fileInfor);
				}
				
			}
			
		});
	}
	
	private void dismissDialog()
	{
		if(myAlertDialog != null && myAlertDialog.isShowing())
		{
			myAlertDialog.dismiss();
			myAlertDialog = null;
		}
		if(progressDialog != null && progressDialog.isShowing())
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	
	/**
	* @Description: 删除文件
	* @param    
	* @return void 
	* @throws
	 */
	private void deleteFiles()
	{
		isDelete = true;
		fileDelete.setOperateList(operationList);
		fileDelete.setOperationListener(this);
		fileDelete.startDelete();
	}
	private void zip()
	{
		isZip = true;
		fileZip.setOperateList(operationList);
		fileZip.setOperationListener(this);
		String name = myAlertDialog.getInputInfor();
		fileZip.startZip(curDirPath + "/" + name);
	}
	private void transfer()
	{
		fileTransfer.setDstDir(curDirPath);
		fileTransfer.setOperateList(operationList);
		fileTransfer.setOperationListener(this);
		fileTransfer.startTransfer(true);
	}
	
	private void reset()
	{
		getFileList(curDirPath);
		
		showSelectedCount();
		dismissDialog();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onDismiss()
	{
		// TODO Auto-generated method stub
		resetStatus();
		operationList.clear();
		showSelectedCount();
		cancelEdit(true);
	}

	private void resetStatus()
	{
		isDelete = false;
		isZip = false;
		isCopy = false;
		isCut = false;
		isCreate = false;
		isRename = false;
	}
	
	public void onKeyBack()
	{
		// TODO Auto-generated method stub
		if(isEdit)
		{
			cancelEdit(true);
		}
		else if(dirInforList.size() > 0)
		{
			int pos = dirInforList.size() - 1;
			MyFileInfor infor = dirInforList.get(pos);
			getFileList(infor.getFileUrl());
			mTitleName.setText(infor.getFileName());
			removeEndirs(pos);
		}
		else if(dirInforList.size() == 0)
		{
			mSlidingMenu.showMenu();
		}
	}
	public boolean isExit()
	{
		if(isEdit)
			return false;
		if(isSearch)
			return false;
		if(dirInforList.size() > 0)
			return false;
		return true;
	}

	/**
	*callbacks
	*/
	@Override
	public void onDeleteOne(String fileUrl, long progress)
	{
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putString("FILE_URL", fileUrl);
		bundle.putLong("PROGRESS", progress);
		MyViewUtil.sendMessage(handler, OPERATION_DELETE_ONE, bundle);
	}

	/**
	*callbacks
	*/
	@Override
	public void onFileNotFind(String fileUrl)
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_FILE_NOT_FIND, fileUrl);
	}

	/**
	*callbacks
	*/
	@Override
	public void onStart(long totalSize)
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_START, totalSize);
	}

	/**
	*callbacks
	*/
	@Override
	public void onFinish(boolean cancel)
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_FINISH, cancel);
	}

	/**
	*callbacks
	*/
	@Override
	public void onProgress(long progress)
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_PROGRESS, progress);
	}

	/**
	*callbacks
	*/
	@Override
	public void onSingleStart(String fileUrl)
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_SINGLE_START, fileUrl);
	}

	/**
	*callbacks
	*/
	@Override
	public void onSingleFinish(String fileUrl)
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_SINGLE_FINISH, fileUrl);
	}

	/**
	*callbacks
	*/
	@Override
	public void onNoSpace()
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_NO_SPACE);
	}

	/**
	*callbacks
	*/
	@Override
	public void onWritePermission()
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_WRITE_PERMISSION);
	}
	/**
	*callbacks
	*/
	@Override
	public void onPrepare()
	{
		// TODO Auto-generated method stub
		MyViewUtil.sendMessage(handler, OPERATION_PREPARE);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE )
		{
			fileAdapter.restartThreadPool();
			fileAdapter.loadFiles(mFirstVisibleItem, mVisibleItemCount);  
		}
		else if(scrollState == OnScrollListener.SCROLL_STATE_FLING)
		{
			fileAdapter.shutDownThreadPool();
			isFirstEnter = false;  
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		mFirstVisibleItem = firstVisibleItem;  
        mVisibleItemCount = visibleItemCount;  
        // 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，  
        // 因此在这里为首次进入程序开启下载任务。  
        if (isFirstEnter && visibleItemCount > 0) 
        {  
        	fileAdapter.loadFiles(firstVisibleItem, visibleItemCount);  
        }  
	}
}
