/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-19 下午3:44:54 
* @Version V1.0   
*/ 

package org.yyu.msi.view; 

import org.yyu.msi.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

/** 
 * @ClassName: MyProgressDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-19 下午3:44:54  
 */
public class MyProgressDialog extends PopupWindow
{

	private View parentView = null;
	private View contentView = null;
	private TextView tvTitle = null;
	private TextView tvFrom = null;
	private TextView tvTo = null;
	private TextView tvProgress = null;
	private TextView tvOne = null;
	private TextView tvPercent = null;
	private ProgressBar pb = null;
	private Button btnCancel = null;
	private Button btnHide = null;
	
	public MyProgressDialog(Context context)
	{
		
		parentView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
		contentView = parentView.findViewById(R.id.layout_dialog_progress_content);
		tvTitle = (TextView)parentView.findViewById(R.id.tv_dialog_progress_title);
		tvFrom = (TextView)parentView.findViewById(R.id.tv_dialog_progress_from);
		tvTo = (TextView)parentView.findViewById(R.id.tv_dialog_progress_to);
		tvProgress = (TextView)parentView.findViewById(R.id.tv_dialog_progress_progress);
		tvPercent = (TextView)parentView.findViewById(R.id.tv_dialog_progress_percent);
		tvOne = (TextView)parentView.findViewById(R.id.tv_dialog_progress_one);
		btnCancel = (Button)parentView.findViewById(R.id.btn_alert_progress_cancel);
		btnHide = (Button)parentView.findViewById(R.id.btn_alert_progress_hide);
		pb = (ProgressBar)parentView.findViewById(R.id.pb_dialog_progress_size);
		
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
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		//设置弹出窗体的背景
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(true);
	}
	
	public void setTitle(String text)
	{
		tvTitle.setText(text);
	}
	public void setTitle(int text)
	{
		tvTitle.setText(text);
	}
	
	public void setFromDir(String text)
	{
		tvFrom.setText(text);
	}
	
	public void setToDir(String text)
	{
		tvTo.setText(text);
	}
	
	public void setProgress(String sizePercent, int progress)
	{
		tvProgress.setText(sizePercent);
		tvPercent.setText(progress + "%");
		pb.setProgress(progress);
	}
	
	public void setOneProgress(String text)
	{
		tvOne.setText(text);
	}
	
	public void hideDir()
	{
		tvFrom.setVisibility(View.GONE);
		tvTo.setVisibility(View.GONE);
	}
	
	public void setOnClickListener(OnClickListener onClickListener)
	{
		btnHide.setOnClickListener(onClickListener);
		btnCancel.setOnClickListener(onClickListener);
	}
	
	public void showDialog(View rootView)
	{
		showAtLocation(rootView, Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0); 
	}
}
 
