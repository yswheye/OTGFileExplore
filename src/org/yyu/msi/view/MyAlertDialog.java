/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-4 下午6:30:18 
* @Version V2.0   
*/ 

package org.yyu.msi.view; 

import org.yyu.msi.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/** 
 * @ClassName: MyAlertDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-4 下午6:30:18  
 */
public class MyAlertDialog extends PopupWindow
{

	private View parentView = null;
	private View contentView = null;
	private View buttonView = null;
	private View inforView = null;
	private Button btnConfirm = null;
	private Button btnCancel = null;
	private TextView tvTitle = null;
	private TextView tvInfor = null;
	private ImageView ivProgress = null;
	private Button btnProgressCancel = null;
	private EditText etInput = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public MyAlertDialog(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		
		parentView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
		contentView = parentView.findViewById(R.id.layout_dialog_alert_content);
		buttonView = parentView.findViewById(R.id.layout_dialog_alert_button);
		inforView = parentView.findViewById(R.id.layout_dialog_alert_infor);
		btnConfirm = (Button)parentView.findViewById(R.id.btn_alert_dialog_confirm);
		btnCancel = (Button)parentView.findViewById(R.id.btn_alert_dialog_cancel);
		btnProgressCancel = (Button)parentView.findViewById(R.id.btn_progress_dialog_cancel);
		tvTitle = (TextView)parentView.findViewById(R.id.tv_dialog_alert_title);
		tvInfor = (TextView)parentView.findViewById(R.id.tv_dialog_alert_infor);
		ivProgress = (ImageView)parentView.findViewById(R.id.iv_progress_dialog_img);
		etInput = (EditText)parentView.findViewById(R.id.et_alert_dialog_input);
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
		
		parentView.setOnTouchListener(new OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event)
			{
				// TODO Auto-generated method stub
				int top = contentView.getTop();
				int bottom = contentView.getBottom();
				int y = (int) event.getY();
				//int x = (int) event.getX();
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					/*if(y < top || y > bottom)
						dismiss();*/
				}
				return true;
			}
		});
	}
	
	
	
	private void showAnimation()
	{
		Animation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setRepeatCount(Animation.INFINITE); // 设置INFINITE，对应值-1，代表重复次数为无穷次
        anim.setDuration(1000);                  // 设置该动画的持续时间，毫秒单位
        anim.setInterpolator(new LinearInterpolator());	// 设置一个插入器，或叫补间器，用于完成从动画的一个起始到结束中间的补间部分
        ivProgress.startAnimation(anim);
	}
	
	public void showAlertDialog(View rootView)
	{
		btnProgressCancel.setVisibility(View.GONE);
		ivProgress.setVisibility(View.GONE);
		showAtLocation(rootView, Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0); 
	}
	public void showInputDialog(View rootView)
	{
		btnProgressCancel.setVisibility(View.GONE);
		inforView.setVisibility(View.GONE);
		etInput.setVisibility(View.VISIBLE);
		showAtLocation(rootView, Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0); 
	}
	public void showProgressDialog(View rootView)
	{
		btnProgressCancel.setVisibility(View.GONE);
		buttonView.setVisibility(View.GONE);
		ivProgress.setVisibility(View.VISIBLE);
		showAtLocation(rootView, Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
		showAnimation();
	}
	public void showCancelProgressDialog(View rootView)
	{
		btnProgressCancel.setVisibility(View.VISIBLE);
		buttonView.setVisibility(View.GONE);
		ivProgress.setVisibility(View.VISIBLE);
		showAtLocation(rootView, Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0); 
		showAnimation();
	}
	
	public void setConfirmInfor(String infor)
	{
		btnConfirm.setText(infor);
	}
	public void setConfirmInfor(int infor)
	{
		btnConfirm.setText(infor);
	}
	public void setCancelInfor(int infor)
	{
		btnCancel.setText(infor);
	}
	public void setCancelInfor(String infor)
	{
		btnCancel.setText(infor);
	}
	
	public void setTitle(String title)
	{
		tvTitle.setText(title);
	}
	public void setTitle(int title)
	{
		tvTitle.setText(title);
	}
	
	public void setInfor(String infor)
	{
		tvInfor.setText(infor);
	}
	public void setInfor(int infor)
	{
		tvInfor.setText(infor);
	}
	public void setInputInfor(String infor)
	{
		etInput.setText(infor);
	}
	public void setInputInfor(int infor)
	{
		etInput.setText(infor);
		etInput.selectAll();
	}
	
	
	public String getInputInfor()
	{
		return etInput.getText().toString().trim();
	}
	
	public void setOnClickListener(OnClickListener onClickListener)
	{
		btnConfirm.setOnClickListener(onClickListener);
		btnCancel.setOnClickListener(onClickListener);
		btnProgressCancel.setOnClickListener(onClickListener);
	}
}
 
