/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-23 下午3:10:39 
* @Version V1.0   
*/ 

package org.yyu.msi.view; 

import org.yyu.msi.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/** 
 * @ClassName: FileEditView 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-23 下午3:10:39  
 */
public class FileEditView extends LinearLayout
{
	
	private View rootView = null;
	private LinearLayout viewEdit1 = null;
	private LinearLayout viewEdit2 = null;
	private View folderSelectView = null;
	private View fileSelectView = null;
	private HorizontalScrollView scrollView = null;
	private Button btnEdit = null;//编辑按钮
	private Button btnCreate = null;//创建文件夹
	private Button btnSearch = null;//查询
	private Button btnReturn = null;//返回上一级目录
	private Button btnDelete = null;//删除
	private Button btnCopy = null;//复制
	private Button btnCut = null;//剪切
	private Button btnZip = null;//压缩
	private Button btnCancel = null;//取消
	private Button btnPaste = null;//粘贴
	private Button btnPasteCancel = null;//取消粘贴
	private Button btnPasteCheck = null;//查看所选文件
	private Button btnSelectAll = null;//全选
	

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs
	* @param defStyle 
	*/
	public FileEditView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	public FileEditView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	public FileEditView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	/**
	*callbacks
	*/
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		
		//scrollView.scrollBy(720, 0);
	}
	
	private void initView(Context context)
	{
		rootView = LayoutInflater.from(context).inflate(R.layout.view_file_edit, this, true);
		viewEdit1 = (LinearLayout)rootView.findViewById(R.id.layout_file_infor_fragment_bottom1);
		viewEdit2 = (LinearLayout)rootView.findViewById(R.id.layout_file_infor_fragment_bottom2);
		folderSelectView = rootView.findViewById(R.id.bottom_folder_select);
		fileSelectView = rootView.findViewById(R.id.bottom_file_edit);
		scrollView = (HorizontalScrollView)rootView.findViewById(R.id.hs_file_infor_fagment_edit);
		btnEdit = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_edit);
		btnCreate = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_create);
		btnSearch = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_search);
		btnReturn = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_return);
		btnDelete = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_delete);
		btnCopy = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_copy);
		btnCut = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_cut);
		btnZip = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_zip);
		btnCancel = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_cancel);
		btnPaste = (Button)rootView.findViewById(R.id.btn_copy_cut_edit_paste);
		btnPasteCancel = (Button)rootView.findViewById(R.id.btn_copy_cut_edit_cancel);
		btnPasteCheck = (Button)rootView.findViewById(R.id.btn_copy_cut_edit_new);
		btnSelectAll = (Button)rootView.findViewById(R.id.btn_file_infor_fragment_select_all);
		
	}
	
	/**
	*callbacks
	*/
	@Override
	public void setOnClickListener(OnClickListener l)
	{
		setListaener(l);
		// TODO Auto-generated method stub
		super.setOnClickListener(l);
	}
	
	private void setListaener(OnClickListener listener)
	{
		btnEdit.setOnClickListener(listener);
		btnCreate.setOnClickListener(listener);
		btnSearch.setOnClickListener(listener);
		btnReturn.setOnClickListener(listener);
		btnDelete.setOnClickListener(listener);
		btnCopy.setOnClickListener(listener);
		btnCut.setOnClickListener(listener);
		btnZip.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);
		btnPaste.setOnClickListener(listener);
		btnPasteCancel.setOnClickListener(listener);
		btnPasteCheck.setOnClickListener(listener);
		btnSelectAll.setOnClickListener(listener);
	}
	
	public void showEditState()
	{
		viewEdit1.setVisibility(View.GONE);
		viewEdit2.setVisibility(View.VISIBLE);
		btnSelectAll.setText(R.string.select_all);
		scrollView.scrollTo(btnSelectAll.getLeft(), 0);
	}
	public void cancelEdit()
	{
		viewEdit1.setVisibility(View.VISIBLE);
		viewEdit2.setVisibility(View.GONE);
	}
	public void showFolderSelect(boolean isAnim)
	{
		folderSelectView.setVisibility(View.VISIBLE);
		fileSelectView.setVisibility(View.GONE);
		if(isAnim)
			startAnimation2(getContext());
	}
	public void hideFolderSelect(boolean isAnim)
	{
		fileSelectView.setVisibility(View.VISIBLE);
		folderSelectView.setVisibility(View.GONE);
		if(isAnim)
			startAnimation2(getContext());
	}
	
	public void updateSelectBtn(int res)
	{
		btnSelectAll.setText(res);
	}
	
	public void startAnimation1(Context context)
	{
		setViewAnimation(context, viewEdit1, viewEdit2);
	}
	public void startAnimation2(Context context)
	{
		setViewAnimation(context, fileSelectView, folderSelectView);
	}
	private void setViewAnimation(Context context, View view1, View view2)
	{
		if(view1 != null && view2 != null)
		{
			if(view1.isShown())
			{
				Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
				animation1.setDuration(600);
				view1.startAnimation(animation1);
				
				Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
				animation2.setDuration(600);
				view2.startAnimation(animation2);
			}
			else
			{
				Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
				animation1.setDuration(600);
				view1.startAnimation(animation1);
				
				Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
				animation2.setDuration(600);
				view2.startAnimation(animation2);
			}
		}
	}
}
 
