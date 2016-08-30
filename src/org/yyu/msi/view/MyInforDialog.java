/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-10 上午11:37:15 
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
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

/** 
 * @ClassName: MyInforDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-10 上午11:37:15  
 */
public class MyInforDialog extends PopupWindow
{
	
	private View parentView = null;
	private View contentView = null;
	private TextView tvTitle = null;
	private Button btnConfirm = null;
	private ScrollView scrollView = null;
	private TextView contentView7 = null;
	private TextView contentView1 = null;

	private boolean shouldDismiss = false;
	private int yPos = 0;
	private int maxYPos = 0;
	private int bottomPos = 0;
	
	public MyInforDialog(Context context)
	{
		super(context);
		
		parentView = LayoutInflater.from(context).inflate(R.layout.dialog_infor, null);
		contentView = parentView.findViewById(R.id.dialog_infor_content);
		tvTitle = (TextView)parentView.findViewById(R.id.tv_dialog_infor_title);
		btnConfirm = (Button)parentView.findViewById(R.id.btn_dialog_infor_confirm);
		scrollView = (ScrollView)parentView.findViewById(R.id.sv_dialog_infor);
		
		contentView1 = (TextView)parentView.findViewById(R.id.tv_dialog_infor_content1);
		contentView7 = (TextView)parentView.findViewById(R.id.tv_dialog_infor_content7);
		
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
					if(y < top || y > bottom)
						dismiss();
				}
				return true;
			}
		});
		btnConfirm.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				bottomPos = contentView7.getBottom();
				int h = scrollView.getHeight();
				if(maxYPos == 0)
					maxYPos = bottomPos - h;
				if(shouldDismiss)
				{
					dismiss();
				}
				else
				{
					yPos += 20;
					scrollView.scrollTo(0, yPos);
					
					if(yPos >= maxYPos)
					{
						btnConfirm.setText(R.string.common_confirm);
						shouldDismiss = true;
					}
				}
			}
		});
		scrollView.setOnTouchListener(new OnTouchListener() 
		{
            @Override
            public boolean onTouch(View v, MotionEvent event) 
            {
                if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    if(scrollView.getScrollY() >= contentView7.getBottom() - scrollView.getHeight())
                    {
                         //TODO
                    	//滑动到了底部，然后做你要做的事
                    	btnConfirm.setText(R.string.common_confirm);
						shouldDismiss = true;
                    }
                }
                return false;
            }
        });
	}
	
	public void showAlertDialog(View rootView)
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
}
 
