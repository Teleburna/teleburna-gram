package org.cafemember.messenger.mytg.listeners;

import org.json.JSONObject;


/**
 * Created by Masoud on 6/1/2016.
 */
public interface OnResponseReadyListener {

    void OnResponseReady(boolean error, JSONObject data, String message);
}
