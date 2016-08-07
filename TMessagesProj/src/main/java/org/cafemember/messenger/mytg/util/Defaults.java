package org.cafemember.messenger.mytg.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;
import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.ApplicationLoader;
import org.cafemember.messenger.mytg.Channel;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.listeners.OnChannelReady;
import org.cafemember.messenger.mytg.listeners.OnJoinSuccess;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.tgnet.ConnectionsManager;
import org.cafemember.tgnet.RequestDelegate;
import org.cafemember.tgnet.TLObject;
import org.cafemember.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Masoud on 6/1/2016.
 */
public class Defaults {

    private static Defaults instance;
    private Context context;
    private static final String MY_TOKEN_KEY = "TOKEN";
    private static final String MY_CHANNEL_KEY = "CHANNEL";
    private static final String MY_CHANNEL_NAME_KEY = "CHANNEL_NAME";
    private static final String CHANNEL_SET_KEY = "CHANNEL_SET";
    private static final String SUPPORT_KEY = "SUPPORT";
    private static final String HEL_CHANNEL_KEY = "HELP";
    private static final String CHECK_JOIN_KEY = "OPEN_JOIN";
    private static final String JOIN_COIN_KEY = "JOIN_COIN";
    private static final String LAST_DAY_KEY = "LAST_DAY";
    private static final String PREF_NAME = ApplicationLoader.applicationContext.getPackageName()+".PREF";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor ;
    private Defaults(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

    }

    public final static String [] VIEWS_COUNT = {"500","1000","1500","2000","5000","10000","20000"};
    public final static String [] MEMBERS_COUNT = {"10","25","50","100","250","500","1000","2500"};
    public final static String [] MEMBERS_PRICE = {"20","48","95","180","450","900","1800","3500"};

    public static Defaults getInstance(){
        if(instance != null){
            return instance;
        }
        instance = new Defaults(ApplicationLoader.applicationContext);
        return instance;
    }
    public String getMyToken(){
        if(preferences != null){

            String token = preferences.getString(MY_TOKEN_KEY,"");
            return token;
        }
        return "";
    }

    public boolean setMyToken(String token){
        editor.putString(MY_TOKEN_KEY,token);
        boolean commited =  editor.commit();
        return commited;
    }

    public String getSupport(){
        /*if(preferences != null){

            String token = preferences.getString(SUPPORT_KEY,"cafemember_admin");
            return token;
        }*/
        return "cafemember_admin";
    }

    public boolean setSupport(String token){
        editor.putString(SUPPORT_KEY,token);
        boolean commited =  editor.commit();
        return commited;
    }
    public boolean isChannelSet(){
        if(preferences != null){

            boolean token = preferences.getBoolean(CHANNEL_SET_KEY,false);
            return token;
        }
        return false;
    }

    public boolean setChannelSet(boolean token){
        editor.putBoolean(CHANNEL_SET_KEY,token);
        boolean commited =  editor.commit();
        return commited;
    }

    public boolean openOnJoin(){
        if(preferences != null){

            boolean token = preferences.getBoolean(CHECK_JOIN_KEY,false);
            return token;
        }
        return false;
    }

    public boolean setOpenOnJoin(boolean token){
        editor.putBoolean(CHECK_JOIN_KEY,token);
        boolean commited =  editor.commit();
        return commited;
    }


    public boolean setMyChannelId(int id){
        editor.putInt(MY_CHANNEL_KEY,id);
        return editor.commit();
    }

    public long getMyChannelId(){
        return preferences.getInt(MY_CHANNEL_KEY,0);//1069024971);
    }

    public boolean setMyCoin(int id){
        editor.putInt(JOIN_COIN_KEY,id);
        return editor.commit();
    }

    public int getMyCoin(){
        return preferences.getInt(JOIN_COIN_KEY,0);//1069024971);
    }

    public boolean setLastDay(int id){
        editor.putInt(LAST_DAY_KEY,id);
        return editor.commit();
    }

    public int getLastDay(){
        return preferences.getInt(LAST_DAY_KEY,-1);//1069024971);
    }

    public boolean setHelpChannelId(int id){
        editor.putInt(HEL_CHANNEL_KEY,id);
        return editor.commit();
    }

    public long getHelpChannelId(){
        return preferences.getInt(HEL_CHANNEL_KEY,0);//1069024971);
    }
    public String getMyChannelName(){
        if(preferences != null){

            String token = preferences.getString(MY_CHANNEL_NAME_KEY,"");
            return token;
        }
        return "";
    }

    public boolean setMyChannelName(String token){
        editor.putString(MY_CHANNEL_NAME_KEY,token);
        boolean commited =  editor.commit();
        return commited;
    }

    public void loadMyChannel(final OnJoinSuccess onJoinSuccess){
        final Channel ch = new Channel(getMyChannelName(),getMyChannelId());
        loadChannel(ch, new OnChannelReady() {
            @Override
            public void onReady(Channel channel, boolean isOK) {
                if(isOK){
                    setMyChannelId(channel.inputChannel.channel_id);
                    Commands.join(ch, onJoinSuccess);
                }
                else {
                    onJoinSuccess.OnResponse(false);
                }
            }
        });
    }

    public void loadChannel(String channelName, final OnChannelReady channelReady){
        Channel ch = new Channel(channelName, 0);
        loadChannel(ch, channelReady);

    }

    public void loadChannel(final Channel currentChannel, final OnChannelReady channelReady){
        TLRPC.TL_contacts_search req = new TLRPC.TL_contacts_search();

        req.q = currentChannel.name;
        req.limit = 3;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error == null) {
                            TLRPC.TL_contacts_found res = (TLRPC.TL_contacts_found) response;
                            for (TLRPC.Chat chat: res.chats) {
                                if(chat.username.equals(currentChannel.name)){
                                    currentChannel.title = chat.title;
                                    currentChannel.id = chat.id;
                                    if(chat.photo != null){
                                        currentChannel.photo = chat.photo.photo_small;
                                    }
                                    TLRPC.InputChannel inputChat = new TLRPC.TL_inputChannel();
                                    inputChat.channel_id = chat.id;
                                    inputChat.access_hash = chat.access_hash;
                                    currentChannel.inputChannel = inputChat;
                                    channelReady.onReady(currentChannel, true);
                                    return;
                                }
                            }
                            channelReady.onReady(currentChannel, false);
                            Log.e("LOAD","Found But Not");

                        }
                        channelReady.onReady(currentChannel, false);
                        Log.e("LOAD","Really Not Found!");
                        if(error.text != null){
                            Log.e("LOAD",error.text);
                        }

                    }
                });
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);
    }

    public ArrayList<TLRPC.Dialog> getMyDialogs(ArrayList<TLRPC.Dialog> dialogs){
        ArrayList<TLRPC.Dialog> myDialogs = new ArrayList<>();
        for(TLRPC.Dialog dialog: dialogs){
            if(dialog.id == -getMyChannelId()){
                myDialogs.add(dialog);
                break;
            }
        }

        return myDialogs;
    }
}
