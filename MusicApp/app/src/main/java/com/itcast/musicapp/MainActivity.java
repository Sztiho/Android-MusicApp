package com.itcast.musicapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView nextIv, playIv, lastIv, albumIv;
    TextView singerTv, songTv;
    RecyclerView musicRv;
    List<MusicBean> mData;
    private MusicAdapter adapter;
    int currentPlayPosition = -1;//记录当前正在播放的音乐的位置
    int currentPausePositionInSong = 0;//记录暂停音乐时进度条的位置
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mediaPlayer = new MediaPlayer();
        mData = new ArrayList<>();
        adapter = new MusicAdapter(this, mData);
        musicRv.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);//布局管理器
        musicRv.setLayoutManager(layoutManager);
        loadLocalMusicData();//加载本地数据源
        setEventListener();//设置每一项的点击事件
    }

    //点击事件
    private void setEventListener() {
        adapter.setOnItemClickListener((view, position) -> {
            currentPlayPosition = position;
            MusicBean musicBean = mData.get(position);
            playMusicInMusicBean(musicBean);
        }
        );
    }

    public void playMusicInMusicBean(MusicBean musicBean) {//根据传入对象播放音乐，设置底部显示的歌手名称和歌曲名

        singerTv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());
        stopMusic();
        mediaPlayer.reset();//重置播放器
        try {//设置新的播放路径
            mediaPlayer.setDataSource(musicBean.getPath());
            String albumArt = musicBean.getAlbumArt();
            Log.i("lsh123", "playMusicInMusicBean: albums==" + albumArt);
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            Log.i("lsh123", "playMusicInMusicBean: bm==" + bm);
            albumIv.setImageBitmap(bm);
            playMusic();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {//播放音乐
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (currentPausePositionInSong == 0) {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            playIv.setImageResource(R.drawable.icon_pause);
        }
    }

    private void pauseMusic() {//暂停音乐
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            playIv.setImageResource(R.drawable.icon_play);
        }
    }

    private void stopMusic() {//停止音乐的函数
        if (mediaPlayer != null) {
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            playIv.setImageResource(R.drawable.icon_play);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    private void loadLocalMusicData() {// 加载本地的音乐
        ContentResolver resolver = getContentResolver();//获取ContentResolver对象
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//获取本地音乐存储的Uri地址
        Cursor cursor = resolver.query(uri, null, null, null, null);//开始查询地址
        int id = 0;//遍历Cursor
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            @SuppressLint("Range") String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            id++;
            String sid = String.valueOf(id);
            @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            @SuppressLint("Range") long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));
            @SuppressLint("Range") String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String albumArt = getAlbumArt(album_id);
            MusicBean bean = new MusicBean(sid, song, singer, album, time, path, albumArt);//数据封装到对象
            mData.add(bean);
        }
        adapter.notifyDataSetChanged();//数据源变化，更新适配器
    }

    private String getAlbumArt(String album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = this.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + album_id),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }

    private void initView() {
        nextIv = findViewById(R.id.bottom_next);
        playIv = findViewById(R.id.bottom_play);
        lastIv = findViewById(R.id.bottom_last);
        albumIv = findViewById(R.id.bottom_icon);
        singerTv = findViewById(R.id.bottom_singer);
        songTv = findViewById(R.id.bottom_song);
        musicRv = findViewById(R.id.music_rv);
        nextIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_last:
                if (currentPlayPosition <= 0) {
                    Toast.makeText(this, "已经是第一首了，没有上一曲！", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition - 1;
                MusicBean lastBean = mData.get(currentPlayPosition);
                playMusicInMusicBean(lastBean);
                break;
            case R.id.bottom_next:
                if (currentPlayPosition == mData.size() - 1) {
                    Toast.makeText(this, "已经是最后一首了，没有下一曲！", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition + 1;
                MusicBean nextBean = mData.get(currentPlayPosition);
                playMusicInMusicBean(nextBean);
                break;
            case R.id.bottom_play:
                if (currentPlayPosition == -1) {
                    Toast.makeText(this, "请选择想要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
                break;
        }
    }
}




