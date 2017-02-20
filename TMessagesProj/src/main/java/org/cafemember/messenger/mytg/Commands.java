package org.cafemember.messenger.mytg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import org.cafemember.messenger.mytg.adapter.ReserveAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.ApplicationLoader;
import org.cafemember.messenger.FileLog;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.MessageObject;
import org.cafemember.messenger.MessagesController;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.listeners.OnChannelReady;
import org.cafemember.messenger.mytg.listeners.OnCoinsReady;
import org.cafemember.messenger.mytg.listeners.OnJoinSuccess;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.messenger.mytg.util.API;
import org.cafemember.messenger.mytg.util.Defaults;
import org.cafemember.tgnet.ConnectionsManager;
import org.cafemember.tgnet.RequestDelegate;
import org.cafemember.tgnet.TLObject;
import org.cafemember.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Masoud on 6/1/2016.
 */
public class Commands {

    private static Context context = ApplicationLoader.applicationContext;
    private static int lastMessage = 0;
    private static AlertDialog visibleDialog;

    public static JSONArray JoinCoins;
    public static JSONArray ViewCoins;
    private static int x = 0;

    public static void view(final int id){

        if(lastMessage == id){
            return;
        }
        lastMessage = id;
//        Toast.makeText(ApplicationLoader.applicationContext, id+" Marked.",Toast.LENGTH_SHORT).show();
        API.getInstance().run(String.format(Locale.ENGLISH, "/posts/view/%d", id), new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                if(!error){
                    loadCoins(data);
                }
                else {
//                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void join(final Channel channel, final OnJoinSuccess joinSuccess){
        TLRPC.TL_channels_joinChannel req = new TLRPC.TL_channels_joinChannel();
        req.channel = channel.inputChannel;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error == null) {
                            TLRPC.Updates res = (TLRPC.Updates) response;

                            if(channel.id == Defaults.getInstance().getMyChannelId()){
                                joinSuccess.OnResponse(true);
                                return;
                            }
                            API.getInstance().post(String.format(Locale.ENGLISH, "/channels/join/%d", channel.id), "", new OnResponseReadyListener() {
                                @Override
                                public void OnResponseReady(boolean error, JSONObject data, String message) {
                                    if(!error){
                                        loadCoins(data);
                                        joinSuccess.OnResponse(true);
                                    }
                                    else {
                                        joinSuccess.OnResponse(false);
                                    }
                                }
                            });
                        }
                        else {
                            joinSuccess.OnResponse(false);
                        }
                    }
                });
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);


    }

    public static void left(){

    }

    public static void report(int id, String reason,OnResponseReadyListener onResponseReadyListener){
        API.getInstance().post(String.format(Locale.ENGLISH, "/channels/report/%d",id), "{\"reason\":\""+reason+"\"}", onResponseReadyListener);
    }

    public static void coinsPrice(final OnResponseReadyListener onResponseReadyListener){
        API.getInstance().run(String.format(Locale.ENGLISH, "/coin/getCoinsPrice"), onResponseReadyListener);
    }

    public static void defaultCoins(final OnJoinSuccess onJoinSuccess){
        if(JoinCoins == null || ViewCoins == null) {
            API.getInstance().run(String.format(Locale.ENGLISH, "/coin/getDefaultCoins"), new OnResponseReadyListener() {
                @Override
                public void OnResponseReady(boolean error, JSONObject data, String message) {
                    if(!error){
                        try {
                            data = data.getJSONObject("data");
                            ViewCoins = data.getJSONArray("viewCoins");
                            JoinCoins = data.getJSONArray("joinCoins");
                            onJoinSuccess.OnResponse(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        onJoinSuccess.OnResponse(false);
                    }
                }
            });
        }
        else {
            onJoinSuccess.OnResponse(true);
        }
    }

    public static void buy(String id){
        Intent i = new Intent(context,PayActivityNivad.class);
        i.putExtra("sku",id);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void checkBoughtItem(String id,final OnJoinSuccess success){
        API.getInstance().post(String.format(Locale.ENGLISH, "/coin/buyCoin/%s",id),"", new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                if(!error){

                    loadCoins(data);
                    success.OnResponse(true);
                }
                else {
                    success.OnResponse(false);
                    Toast.makeText(ApplicationLoader.applicationContext,message,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void transfare(String user, int amount, int type, final OnResponseReadyListener onResponseReadyListener){
        API.getInstance().post(String.format(Locale.ENGLISH, "/coin/transfare/%d/%s/%d", type,user,amount), "", new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                if(!error){
                    loadCoins(data);

                }
                onResponseReadyListener.OnResponseReady(error,data,message);
            }
        });

    }

    public static void login(String phone, final OnResponseReadyListener onResponseReadyListener){
        if(phone != null )
        {

            if(phone.length() >= 10 ){
                phone = "98"+phone.substring(phone.length()-10);
            }
        }
        API.getInstance().post(String.format(Locale.ENGLISH, "/user/login/%s", phone), "", new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                if(!error){
                    try {
                        loadCoins(data);
                        data = data.getJSONObject("data");
                        String token = data.getString("token");

                        if(data.has("channel_name")){
                            String channelName = data.getString("channel_name");
                            Defaults.getInstance().setMyChannelName(channelName);
                        }
                        if(data.has("support")){
                            String channelName = data.getString("support");
                            Defaults.getInstance().setSupport(channelName);
                        }
                        if(data.has("help_channel_id")){
                            int  helpcChannelId = data.getInt("help_channel_id");
                            Defaults.getInstance().setHelpChannelId(helpcChannelId);
                        }
                        if(data.has("channel_id")){
                            int  channelId = data.getInt("channel_id");
                            Defaults.getInstance().setMyChannelId(channelId);
                        }
                        Defaults.getInstance().setMyToken(token);

                        final Defaults def = Defaults.getInstance();
                        if(!def.isChannelSet()) {
                            def.loadMyChannel(new OnJoinSuccess() {
                                @Override
                                public void OnResponse(boolean ok) {
                                    if(ok){
                                        def.setChannelSet(true);
                                    }
                                    else {
                                        AlertDialog.Builder builder = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                            builder = new AlertDialog.Builder(context, R.style.MyDialog);
                                        }
                                        else {
                                            builder = new AlertDialog.Builder(context);
                                        }
                                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        builder.setMessage(LocaleController.getString("MyChannelError", R.string.MyChannelError)+"  \n@"+def.getMyChannelName());
                                        builder.setNegativeButton(LocaleController.getString("MyCancel", R.string.MyCancel), null);
                                        showAlertDialog(builder.create());
                                    }

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(onResponseReadyListener != null){
                    onResponseReadyListener.OnResponseReady(error, data, message);
                }
            }
        });
    }

    public static void ref(String phone, final OnResponseReadyListener onResponseReadyListener){
        API.getInstance().post(String.format(Locale.ENGLISH, "/user/ref/%s", phone), "", new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                if(!error){
//                    Toast.makeText(context, "معرف با موفقیت ثبت شد",Toast.LENGTH_LONG).show();
                    onResponseReadyListener.OnResponseReady(error, data, "معرف با موفقیت ثبت شد");
                }
                else {
                    onResponseReadyListener.OnResponseReady(error, data, message);
//                    Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void checkChannels(final ArrayList<TLRPC.Dialog> dialogs){

    if(dialogs != null && dialogs.size() > 0)
        getJoinedChannels(new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                if(!error){
                    JSONArray channelsId = null;
                    try {
                        channelsId = data.getJSONArray("data");
                    int size = channelsId.length();
                    HashMap<Long,Channel> lastChannels = new HashMap<Long, Channel>();
                    for(int i=0 ; i < size ; i++) {
                        JSONObject item = channelsId.getJSONObject(i);
                        Channel currentChannel = new Channel(item.getString("name"), item.getInt("tg_id"));
                        lastChannels.put(currentChannel.id,currentChannel);
                    }
                    for (TLRPC.Dialog dialog:dialogs){
                        if(dialog instanceof TLRPC.TL_dialogChannel){
                            long id = -dialog.id;
//                            TLRPC.TL_channel channel = (TLRPC.TL_channel)MessagesController.getInstance().getChat((int)id);

                            lastChannels.remove(id);
                        }
                    }
                        if(lastChannels.size() > 0) {
                            String [] items = new String[lastChannels.size()];
                            final ArrayList<Channel> ids = new ArrayList<>();
                            int i = 0;
                            for(Channel channel:lastChannels.values()){
                                items[i] = channel.name;
                                ids.add(channel);
                                i++;
                            }
                            final boolean[] checkedItems = new boolean[items.length];
                            AlertDialog.Builder builder = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                builder = new AlertDialog.Builder(context, R.style.MyDialog);
                            }
                            else {
                                builder = new AlertDialog.Builder(context);
                            }
                            builder.setTitle(LocaleController.getString("LeftChannels", R.string.LeftChannels));
                            builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    checkedItems[which] = isChecked;
                                }
                            });


                            builder.setPositiveButton(LocaleController.getString("ReJoin", R.string.ReJoin), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reJoin(ids, checkedItems, new OnJoinSuccess() {
                                        @Override
                                        public void OnResponse(boolean ok) {
                                            if (visibleDialog != null) {
                                                visibleDialog.dismiss();
                                                visibleDialog = null;
                                            }
                                        }
                                    });
                                }
                            });
                            /*builder.setNeutralButton(LocaleController.getString("SelectAll", R.string.SelectAll), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int id) {
                                    *//*for (int j = 0 ; j < checkedItems.length ; j++){
                                        checkedItems[j]=true;
                                    }*//*
                                    ListView list = ((AlertDialog) dialogInterface).getListView();
                                    for (int i=0; i < list.getCount(); i++) {
                                        list.setItemChecked(i, true);
                                    }
                                }
                            });*/
                            builder.setNegativeButton(LocaleController.getString("LeftAll", R.string.LeftAll), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int j = 0 ; j < checkedItems.length ; j++){
                                        checkedItems[j]=false;
                                    }
                                    reJoin(ids, checkedItems, new OnJoinSuccess() {
                                        @Override
                                        public void OnResponse(boolean ok) {
                                            if (visibleDialog != null) {
                                                visibleDialog.dismiss();
                                                visibleDialog = null;
                                            }
                                        }
                                    });
                                }
                            });
                            builder.setCancelable(false);


                            builder.setOnKeyListener(new Dialog.OnKeyListener() {

                                @Override
                                public boolean onKey(DialogInterface arg0, int keyCode,
                                                     KeyEvent event) {
                                    // TODO Auto-generated method stub
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        if (x < 1) {
                                            Toast.makeText(context, LocaleController.getString("backClickAgain", R.string.backClickAgain), Toast.LENGTH_SHORT).show();
                                            x++;
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    x = 0;
                                                }
                                            }, 1000);
                                            return true;

                                        } else {
                                            if(context instanceof Activity){
                                                ((Activity)context).finish();
                                            }
                                            return true;
                                        }

                                    }
                                    return false;
                                }
                            });


                            showAlertDialog(builder.create());
                            /*AlertDialog dialog = builder.show();

                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            dialog.show();*/
//                            System.out.println("Left Channels: "+ Arrays.toString(lastChannels.toArray()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public static void reJoin(ArrayList<Channel> ids, boolean[] checkedItems,final OnJoinSuccess onJoinSuccess ){
        final JSONArray channelsToLeft = new JSONArray();
        ArrayList<Channel> channelsToReJoin = new ArrayList<>();
        int size = ids.size();
        int reJoinSize = 0;
        for(int i = 0 ; i<size ; i++){
            if(checkedItems[i]){
                channelsToReJoin.add(ids.get(i));
                reJoinSize++;
            }
            else {

                channelsToLeft.put(ids.get(i).id);
            }
        }
            final int joins = reJoinSize;
            for(final Channel ch: channelsToReJoin){
                Defaults.getInstance().loadChannel(ch, new OnChannelReady() {


                    @Override
                    public void onReady(Channel channel, boolean isOk) {

                        if(isOk) {
                            join(ch, new OnJoinSuccess() {
                                int joinCompletes = 0;
                                int steps = 0;
                                @Override
                                public void OnResponse(boolean ok) {
                                    steps ++;
                                    if(ok){
                                        joinCompletes++;

                                    }
                                    if (steps == joins) {

                                        if(joinCompletes == joins) {
                                            try {
                                                API.getInstance().post("/channels/leftAll", channelsToLeft.toString(2), new OnResponseReadyListener() {
                                                    @Override
                                                    public void OnResponseReady(boolean error, JSONObject data, String message) {
                                                        if (!error) {
                                                            loadCoins(data);
                                                        }
                                                        onJoinSuccess.OnResponse(!error);
                                                    }
                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else {
                                            onJoinSuccess.OnResponse(false);
                                        }

                                    }
                                }
                            });

                        }

                    }
                });
            }

        if(reJoinSize == 0){
            try {
                API.getInstance().post("/channels/leftAll", channelsToLeft.toString(2), new OnResponseReadyListener() {
                    @Override
                    public void OnResponseReady(boolean error, JSONObject data, String message) {
                        if(!error){
                            loadCoins(data);
                        }
                        onJoinSuccess.OnResponse(!error);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static void addChannel(final TLRPC.Chat channel, int count){
        int channelId = channel.id;
            API.getInstance().post(String.format(Locale.ENGLISH, "/channels/add/%d/%s/%d", channelId, channel.username, count), "", new OnResponseReadyListener() {
                @Override
                public void OnResponseReady(boolean error, JSONObject data, String message) {
                    if(error){
                        Toast.makeText(context, message,Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context, channel.username+" اضافه شد",Toast.LENGTH_LONG).show();
                        loadCoins(data);

                    }
                }
            });


    }

    public static void addMyChannel(final Channel channel,final OnResponseReadyListener onJoinSuccess){
        int channelId = (int)channel.id;
        API.getInstance().post(String.format(Locale.ENGLISH, "/channels/addMy/%d/%s", channelId, channel.name), "", onJoinSuccess);


    }


    public static void addChannel(final Channel channel, int count){
        int channelId = (int)channel.id;
        API.getInstance().post(String.format(Locale.ENGLISH, "/channels/add/%d/%s/%d", channelId, channel.name, count), "", new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                if(error){
                    Toast.makeText(context, message,Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, channel.title+" اضافه شد",Toast.LENGTH_LONG).show();
                    loadCoins(data);

                }
            }
        });


    }
    public static void addChannel(final Channel channel){
        Log.d("COMMAND","AddChannel Triggerd");
        final int channelId = (int) channel.id;



        AlertDialog.Builder builder ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        }
//        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            builder = new AlertDialog.Builder(context, R.style.MyDialog);
//        }
        else {
            builder = new AlertDialog.Builder(context);
        }


        builder.setTitle(LocaleController.getString("MemberBegirTitle", R.string.MemberBegirTitle));

                            /*builder.setItems(Defaults.MEMBERS_COUNT , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Commands.addChannel(chat,Integer.parseInt(Defaults.MEMBERS_COUNT[which]));
                                }
                            });*/
        ReserveAdapter reserveAdapter = new ReserveAdapter(context,R.layout.adapter_buy_coin,channel);
        reserveAdapter.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("COMMAND","OnClick Triggerd 2");
                final int count = Integer.parseInt(Defaults.MEMBERS_COUNT[which]);
                Commands.addChannel(channel,count);
                if (visibleDialog != null) {
                    visibleDialog.dismiss();
                    visibleDialog = null;
                }
            }
        });
        builder.setAdapter(reserveAdapter, null);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setCancelable(true);
        showAlertDialog(builder.create());




    }

    public static void removeChannel(final Channel channel,final OnJoinSuccess onJoinSuccess){
        int channelId = (int) channel.id;
        API.getInstance().post(String.format(Locale.ENGLISH, "/channels/remove/%d", channelId), "", new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                onJoinSuccess.OnResponse(!error);
                if(error){
                    Toast.makeText(context, message,Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, channel.title+" حذف شد",Toast.LENGTH_LONG).show();
                    loadCoins(data);

                }
            }
        });


    }


    public static void addPost(MessageObject message, int count){

        int id = message.getId();
        int channel_id = message.messageOwner.to_id.channel_id;
        TLRPC.Chat chat = MessagesController.getInstance().getChat(channel_id);
//        long access_hash = chat.access_hash;
        CharSequence text = message.caption != null? message.caption:(message.messageText != null ?message.messageText:"None");
        String data = "{\"text\":\""+text+"\"}";
        final String channel_name = chat.username;

            API.getInstance().post(String.format(Locale.ENGLISH,"/posts/add/%s/%d/%d",channel_name,id,count),data, new OnResponseReadyListener() {
                @Override
                public void OnResponseReady(boolean error, JSONObject data, String message) {
                    if(error){
                        Toast.makeText(context, message,Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context, "پست شما از "+channel_name+" اضافه شد",Toast.LENGTH_LONG).show();
                        loadCoins(data);
                    }
                }
            });



    }

    private static void loadCoins(JSONObject data){
        boolean isView = false;
        String key = "joinCoins";
        try {
            data = data.getJSONObject("data");
            if(data.has("viewCoins")){
                key = "viewCoins";
                isView = true;
            }
            else if(data.has("joinCoins")){
                key = "joinCoins";
                isView = false;
            }
            else {
                return;
            }
            int coins = data.getInt(key);
            if(isView){
                ApplicationLoader.setViewCoins(coins);
            }
            else{
                ApplicationLoader.setJoinCoins(coins);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void loadCoins(final OnCoinsReady onCoinsReady){

        AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(context, R.style.MyDialog);
        }
        else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("MyChannelError", R.string.MyChannelError)+"  \n@");
        builder.setNegativeButton(LocaleController.getString("MyCancel", R.string.MyCancel), null);
        showAlertDialog(builder.create());


//        if( ApplicationLoader.joinCoins == 0){
            ApplicationLoader.setJoinCoins(Defaults.getInstance().getMyCoin(),true);
            API.getInstance().run(String.format(Locale.ENGLISH,"/coin"), new OnResponseReadyListener() {
                @Override
                public void OnResponseReady(boolean error, JSONObject data, String message) {
                    if(error){
                        onCoinsReady.onCoins(0,0);
                    }
                    else {
                        try {
                            data = data.getJSONObject("data");
                            int viewCoins = data.getInt("viewCoins");
                            int joinCoins = data.getInt("joinCoins");
                            Defaults.getInstance().setMyCoin(joinCoins);

//                            ApplicationLoader.setViewCoins(viewCoins);
//                            ApplicationLoader.setJoinCoins(joinCoins);
                            onCoinsReady.onCoins(viewCoins, joinCoins);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        /*}
        else {
            ApplicationLoader.setJoinCoins(Defaults.getInstance().getMyCoin(),true);
            onCoinsReady.onCoins(ApplicationLoader.viewCoins,ApplicationLoader.joinCoins);
        }*/


    }

    public static void getNewChannels(OnResponseReadyListener listener){
        API.getInstance().run(String.format(Locale.ENGLISH,"/channels"),listener);
    }

    public static void getMyChannels(OnResponseReadyListener listener){
        API.getInstance().run(String.format(Locale.ENGLISH,"/channels/getMy"),listener);
    }

    public static void getJoinedChannels(OnResponseReadyListener listener){
        API.getInstance().run(String.format(Locale.ENGLISH,"/channels/self"),listener);
    }

    public static void getHistory(OnResponseReadyListener listener){
        API.getInstance().run(String.format(Locale.ENGLISH,"/user/history"),listener);
    }

    public static Dialog showAlertDialog(AlertDialog dialog) {
        Log.d("COMMAND","Show Alert");
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        try {
            visibleDialog = dialog;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                visibleDialog.getListView().setDivider(context.getDrawable(R.drawable.transparent));
            }
            else {
                visibleDialog.getListView().setDivider(context.getResources().getDrawable(R.drawable.transparent));
            }
//            visibleDialog.getActionBar().set
//            FontManager.instance().setTypefaceImmediate(visibleDialog.getWindow().getDecorView());
            visibleDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            visibleDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            visibleDialog.setCanceledOnTouchOutside(false);
            visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    visibleDialog = null;
                }
            });
            FontManager.instance().setTypefaceImmediate(visibleDialog.getCurrentFocus());
            visibleDialog.show();
            return visibleDialog;
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        return null;
    }

}
