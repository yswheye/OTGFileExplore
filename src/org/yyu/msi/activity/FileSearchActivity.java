/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-6 下午3:31:40 
* @Version V2.0   
*/ 

package org.yyu.msi.activity; 

import java.util.ArrayList;
import java.util.List;

import org.yyu.msi.R;
import org.yyu.msi.common.Setting;
import org.yyu.msi.entity.ScanFileEntity;
import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.utils.MyFileUtil;
import org.yyu.msi.utils.MyStringUtil;
import org.yyu.msi.utils.MyViewUtil;
import org.yyu.msi.view.FileAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;


/** 
 * @ClassName: FileSearchFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-6 下午3:31:40  
 */
public class FileSearchActivity extends Activity implements OnClickListener, OnQueryTextListener
{

	private ListView lvSearch = null;
	private ImageButton ibTitleLeft = null;
	private ImageButton ibTitleRight = null;
	private TextView tvTitle = null;
	private SearchView searchView = null;
	private TextView tvSearch = null;
	private View bgSearchView = null;
	private ProgressBar pbSearch = null;
	private FileAdapter adapter = null;
	private ArrayList<MyFileInfor> list = new ArrayList<MyFileInfor>();
	
	private String dirPath = null;
	
	
	private final int MSG_GET_FILE_START = 0;
	private final int MSG_GET_FILE_FINISH = 1;
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
				list.clear();
				list.addAll(tempList);
				tempList.clear();
				
				adapter.notifyDataSetChanged();
			}
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.file_search_activity);
		
		lvSearch = (ListView)findViewById(R.id.lv_file_search_infor);
		ibTitleLeft = (ImageButton)findViewById(R.id.ivTitleBtnLeft);
		ibTitleRight = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
		searchView = (SearchView)findViewById(R.id.sv_file_search_name);
		tvTitle = (TextView)findViewById(R.id.ivTitleName);
		tvSearch = (TextView)findViewById(R.id.tv_file_search_result);
		bgSearchView = findViewById(R.id.layout_file_search_result);
		pbSearch = (ProgressBar)findViewById(R.id.pb_file_search_progress);
		
		ibTitleLeft.setOnClickListener(this);
		ibTitleLeft.setBackgroundResource(R.drawable.selector_btn_return);
		ibTitleRight.setVisibility(View.GONE);
		
		lvSearch.setTextFilterEnabled(true);
		searchView.setOnQueryTextListener(this);
		searchView.setSubmitButtonEnabled(true);
		
		tvTitle.setText(R.string.common_search);
		
		dirPath = getIntent().getStringExtra("dir_path");
		adapter = new FileAdapter(getApplicationContext(), list);
		adapter.setViews(tvSearch, pbSearch, bgSearchView);
		lvSearch.setAdapter(adapter);
		getFileList(dirPath);
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
				boolean isHide = Setting.isHide(getApplicationContext());
				boolean isFilter = Setting.isFilter(getApplicationContext());
				ScanFileEntity entity = MyFileUtil.getLocalFiles(getApplicationContext(), dirPath, 
						isHide, isFilter);
				MyViewUtil.sendMessage(handler, MSG_GET_FILE_FINISH, entity.getList());
			}
		}).start();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == ibTitleLeft)
		{
			finish();
		}
	}

	/**
	*callbacks
	*/
	@Override
	public boolean onQueryTextChange(String newText)
	{
		// TODO Auto-generated method stub
		 if (MyStringUtil.isEmpty(newText)) 
		 {
			 // Clear the text filter.
			 lvSearch.clearTextFilter();
		 } 
		 else 
		 {
			 adapter.setCurPath(dirPath);
			 // Sets the initial value for the text filter.
			 //lvSearch.setFilterText(newText.toString());
		 }
			 
		 return false;
	}

	/**
	*callbacks
	*/
	@Override
	public boolean onQueryTextSubmit(String arg0)
	{
		// TODO Auto-generated method stub
		lvSearch.setFilterText(arg0.trim());
		return false;
	}
}
 
