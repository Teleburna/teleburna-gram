package org.cafemember.messenger.mytg;

import org.cafemember.tgnet.TLRPC;

/**
 * Created by Masoud on 6/25/2016.
 */
public class Channel {
    public String name;
    public String title;
    public long id;
    public TLRPC.FileLocation photo;
    public TLRPC.InputChannel inputChannel;
    public Channel(String name, long id){
        this.name = name;
        this.id = id;
    }
}
