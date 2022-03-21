package com.itcast.musicapp;

public class MusicBean {
    private String id;
    private String song;
    private String singer;
    private String album;
    private String time;
    private String path;
    private String albumArt;

    public MusicBean(String id, String song, String singer, String album, String duration, String path, String albumArt) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.time = duration;
        this.path = path;
        this.albumArt = albumArt;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getId() {
        return id;
    }


    public String getSong() {
        return song;
    }


    public String getSinger() {
        return singer;
    }


    public String getAlbum() {
        return album;
    }


    public String getTime() {
        return time;
    }


    public String getPath() {
        return path;
    }

}
