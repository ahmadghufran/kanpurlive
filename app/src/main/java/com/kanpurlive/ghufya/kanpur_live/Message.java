package com.kanpurlive.ghufya.kanpur_live;

/**
 * Created by Ghufya on 28/03/2018.
 */

public class Message {

    private long timestamp;
    private long negatedTimestamp;
    private long dayTimestamp;
    private String body;
    private String from;
    private String to;
    private FileModel file;

    public Message(long timestamp, long negatedTimestamp, long dayTimestamp, String body, String from, String to, FileModel file) {
        this.timestamp = timestamp;
        this.negatedTimestamp = negatedTimestamp;
        this.dayTimestamp = dayTimestamp;
        this.body = body;
        this.from = from;
        this.to = to;
        this.file = file;
    }

    public Message() {
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel file) {
        this.file = file;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getNegatedTimestamp() {
        return negatedTimestamp;
    }

    public String getTo() {
        return to;
    }

    public long getDayTimestamp() {
        return dayTimestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }
}