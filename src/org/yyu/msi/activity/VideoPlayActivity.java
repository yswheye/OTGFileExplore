/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-24 下午4:11:34 
* @Version V1.0   
*/ 

package org.yyu.msi.activity; 

import org.yyu.msi.R;
import org.yyu.msi.entity.Global;
import org.yyu.msi.view.PowerImageView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

/** 
 * @ClassName: VideoPlayActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-24 下午4:11:34  
 */
public class VideoPlayActivity extends Activity implements OnClickListener
{ 
	private VideoView videoView = null;
	private PowerImageView ivPlay = null;
	private Button btnPlay = null;
	
	private String fileUrl = null;

	/**
	*callbacks
	*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_video_play);
		
		videoView = (VideoView)findViewById(R.id.view_video_play);
		ivPlay = (PowerImageView)findViewById(R.id.iv_video_play);
		btnPlay = (Button)findViewById(R.id.btn_video_play);
		
		btnPlay.setOnClickListener(this);
		
		fileUrl = getIntent().getStringExtra("FILE_DIR");
		
		videoView.setOnCompletionListener(new OnCompletionListener()
		{
			
			@Override
			public void onCompletion(MediaPlayer mp)
			{
				// TODO Auto-generated method stub
				ivPlay.setVisibility(View.VISIBLE);   
				btnPlay.setVisibility(View.VISIBLE);   
			}
		});
		videoView.setOnPreparedListener(new OnPreparedListener()
		{
			
			@Override
			public void onPrepared(MediaPlayer mp)
			{
				// TODO Auto-generated method stub
				ivPlay.setVisibility(View.GONE); 
				btnPlay.setVisibility(View.GONE); 
			}
		});
	}

	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		Global.imageWorker.openImageWorker(0);
		Global.imageWorker.loadBitmap(fileUrl, ivPlay);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		
		Global.imageWorker.closeImageWorker();
	}
	
    private void playVideo(String strPath)
    {  
        if (strPath != null) 
        {  
        	videoView.setVideoURI(Uri.parse(strPath));  
  
        	videoView.setMediaController(new MediaController(VideoPlayActivity.this));  
  
        	videoView.requestFocus();  
  
        	videoView.start();  
        }  
    }  
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == btnPlay)
		{
			playVideo(fileUrl);
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		
		videoView.destroyDrawingCache();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
 
