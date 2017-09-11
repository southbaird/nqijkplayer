package com.nq_ijkplayer;

import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import tv.danmaku.ijk.media.example.media.IRenderView;
import tv.danmaku.ijk.media.example.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {

    private Uri data;
    private String url5 = "http://183.62.114.171/v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4?wsiphost=local";
    private String url6 = "rtsp://192.168.1.254/xxxx.mov";
    private String url7 = "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
    private String url8 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
    private IjkVideoView videoView;

    private SeekBar seekbar;
    private TextView mTextTime, mTotalTime;

    static boolean bLive = false;
    static int nTimeCurrent = 0;

    Handler handlerRecord = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    startTime = true;
                    handlerRecord.postDelayed(run, 0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniUri();
        Uri uri = getIntent().getData();
        videoView = (IjkVideoView) findViewById(R.id.video_view);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        mTextTime = (TextView) findViewById(R.id.video_player_time);
        mTotalTime = (TextView) findViewById(R.id.video_total_time);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        if (uri != null) {
            videoView.setVideoURI(uri);
        } else {
            videoView.setVideoURI(Uri.parse(url8));
//            videoView.setVideoURI(data);
        }
//        videoView.start();
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                videoView.start();
                handlerRecord.postDelayed(run, 1000);
                startTime = true;
            }
        });

        seekbar.setProgress(0);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                try {
                    if (videoView != null) {
                        long dest = seekBar.getProgress();
                        long mMax = videoView.getDuration();
                        long sMax = seekbar.getMax();
                        videoView.seekTo((int) (mMax * dest / sMax));
                        Log.e("nq", "进度条"+ mMax);
                    } else {
                        seekbar.setProgress(0);
                    }
                } catch (Exception ignored) {

                }
            }
        });

    }

    private void iniUri() {
//        data = Uri.parse(Environment.getExternalStorageDirectory()
//                .getPath() + "/PapaGo/PapaGo/2017_0101_020502_139A.MOV");
//        data = Uri.parse(Environment.getExternalStorageDirectory()
//                .getPath() + "/PapaGo/PapaGo/2017_0805_123904_145A.MP4");
//        data = Uri.parse(Environment.getExternalStorageDirectory()   //xiao
//                .getPath() + "/PapaGo/PapaGo/2017_0101_000006_002A.MOV");
//        Log.e("nq", String.valueOf(data));
//        data = Uri.parse(Environment.getExternalStorageDirectory()   //2k
//                .getPath() + "/PapaGo/PapaGo/135505AA.MP4");
        data = Uri.parse(Environment.getExternalStorageDirectory()   //4k
                .getPath() + "/PapaGo/PapaGo/160539AA.MP4");
//        data = Uri.parse(Environment.getExternalStorageDirectory()   //1080p
//                .getPath() + "/PapaGo/PapaGo/163022AA.MP4");
    }

    private boolean startTime = false;
    private Runnable run = new Runnable() {
        int buffer, currentPosition, duration;
        @Override
        public void run() {
            // 获得当前播放时间和当前视频的长度
            currentPosition = videoView.getCurrentPosition();
            duration = videoView.getDuration();
            int time = ((currentPosition * 100) / duration);
            Log.e("nq", "播放进度=" + currentPosition + "/" + duration + "---time=" + time);
            // 设置进度条的主要进度，表示当前的播放时间
            showVideoTime(currentPosition, duration);
            if (startTime) {
                handlerRecord.postDelayed(run, 1000);
            }
        }
    };

    private void showVideoTime(int t, int l) {
        Log.e("nq", "--------t = " + String.valueOf(t) + ", l = " + String.valueOf(l));

        if ((!bLive) && (t > 0) && (l > 0)) {
            nTimeCurrent = 0;
            mTextTime.setText(millisToString(t));
            mTextTime.setVisibility(View.VISIBLE);

            mTotalTime.setText(millisToString(l));
            mTotalTime.setVisibility(View.VISIBLE);

            SetProgressBar(t, l);
        } else if ((!bLive) && (t <= 0) && (l > 0)) {
            nTimeCurrent = nTimeCurrent + 500;
            t = nTimeCurrent;
            if (t < 5000) {
                return;
            } else {
                t = t - 5000;
            }
            mTextTime.setText(millisToString(t));
            mTextTime.setVisibility(View.VISIBLE);

            mTotalTime.setText(millisToString(l));
            mTotalTime.setVisibility(View.VISIBLE);
            SetProgressBar(t, l);
        } else {
            nTimeCurrent = 0;
        }
    }

    public static String millisToString(long millis) {
        boolean negative = millis < 0;
        millis = java.lang.Math.abs(millis);
        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;
        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat
                .getInstance(Locale.US);
        format.applyPattern("00");
        if (millis > 0) {
            time = (negative ? "-" : "") + hours + ":" + format.format(min)
                    + ":" + format.format(sec);
        } else {
            time = (negative ? "-" : "") + min + ":" + format.format(sec);
        }
        return time;
    }

    void SetProgressBar(int t, int l) {
        if (videoView == null) {
            seekbar.setProgress(0);
        } else {
            try {
                int position = t;
                int mMax = l;
                if (mMax == 0) {
                    seekbar.setProgress(0);
                    return;
                }
                int sMax = seekbar.getMax();
                seekbar.setProgress(position * sMax / mMax);
            } catch (IllegalStateException itse) {
                seekbar.setProgress(0);
            }
        }
    }

}
