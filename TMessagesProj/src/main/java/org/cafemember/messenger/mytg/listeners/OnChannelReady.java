package org.cafemember.messenger.mytg.listeners;


import org.cafemember.messenger.mytg.Channel;

/**
 * Created by Masoud on 6/1/2016.
 */
public interface OnChannelReady {

    void onReady(Channel channel, boolean isOK);
}
