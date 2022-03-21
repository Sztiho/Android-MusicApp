package com.itcast.musicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.LocalMusicViewHolder> {
    Context context;
    List<MusicBean> mData;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public MusicAdapter(Context context, List<MusicBean> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        LocalMusicViewHolder holder = new LocalMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MusicBean musicBean = mData.get(position);
        holder.idTv.setText(musicBean.getId());
        holder.songTv.setText(musicBean.getSong());
        holder.singerTv.setText(musicBean.getSinger());
        holder.albumTv.setText(musicBean.getAlbum());
        holder.timeTv.setText(musicBean.getTime());

        holder.itemView.setOnClickListener(v -> onItemClickListener.OnItemClick(v, position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class LocalMusicViewHolder extends RecyclerView.ViewHolder {
        TextView idTv, songTv, singerTv, albumTv, timeTv;
        public LocalMusicViewHolder(View itemView) {
            super(itemView);
            idTv = itemView.findViewById(R.id.item_num);
            songTv = itemView.findViewById(R.id.item_song);
            singerTv = itemView.findViewById(R.id.item_singer);
            albumTv = itemView.findViewById(R.id.item_album);
            timeTv = itemView.findViewById(R.id.item_durtion);
        }
    }
}
