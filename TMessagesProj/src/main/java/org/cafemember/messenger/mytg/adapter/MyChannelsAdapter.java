package org.cafemember.messenger.mytg.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.ImageReceiver;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Channel;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.fragments.MyChannelFragment;
import org.cafemember.messenger.mytg.listeners.OnJoinSuccess;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.tgnet.TLRPC;
import org.cafemember.ui.Components.AvatarDrawable;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Masoud on 6/2/2016.
 */
public class MyChannelsAdapter extends ArrayAdapter {


    private final MyChannelFragment myChannelFragment;
    private ArrayList<Channel> channels;
    public MyChannelsAdapter(Context context, int resource, ArrayList<Channel> objects, MyChannelFragment dialogsActivity) {
        super(context, resource, objects);
        channels = objects;
        this.myChannelFragment = dialogsActivity;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Channel channel = getItem(position);
        MyChannelViewHolder viewHolder ;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.my_channel_item, parent, false);
            viewHolder = new MyChannelViewHolder();
            viewHolder.name = (TextView)v.findViewById(R.id.name);
            viewHolder.title = (TextView)v.findViewById(R.id.title);
            viewHolder.image = (CircleImageView)v.findViewById(R.id.image);
            viewHolder.add = (Button)v.findViewById(R.id.reserve);
            viewHolder.delete = (Button)v.findViewById(R.id.delete);
            v.setTag(viewHolder);
        }

        else {
            viewHolder = (MyChannelViewHolder)v.getTag();
        }


        viewHolder.avatarImage = new ImageReceiver(v);
        viewHolder.avatarDrawable = new AvatarDrawable();
        viewHolder.avatarImage.setRoundRadius(AndroidUtilities.dp(26));
        int avatarLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13 : 9);
        int avatarTop = AndroidUtilities.dp(10);
        viewHolder.avatarImage.setImageCoords(avatarLeft, avatarTop, AndroidUtilities.dp(52), AndroidUtilities.dp(52));
        viewHolder.avatarDrawable.setInfo((int)channel.id, channel.name, null, channel.id < 0);
        if(channel.photo != null) {
            TLRPC.FileLocation photo = null;
            photo = channel.photo;

            viewHolder.avatarImage.setImage(photo, "50_50", viewHolder.avatarDrawable, null, false);

        }/*
        else {
            viewHolder.image.setImageResource(R.drawable.contact_green);
        }*/
        Drawable img = viewHolder.avatarImage.getDrawable();
        if(img != null){
            viewHolder.image.setImageDrawable(img);
        }
        else
        {
            viewHolder.image.setImageResource(R.drawable.default_channel_icon);
        }
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("https://telegram.me/"+channel.name));
                getContext().startActivity(telegram);
            }
        });
        viewHolder.name.setText(channel.name);
        viewHolder.title.setText(channel.title);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myChannelFragment.setLoader(View.VISIBLE);
                Commands.removeChannel(channel, new OnJoinSuccess() {
                    @Override
                    public void OnResponse(boolean ok) {
                        myChannelFragment.setLoader(View.GONE);
                        if(ok){
                            remove(channel);
                            notifyDataSetChanged();
                        }
                    }
                });

            }
        });
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyChannel","OnClick Triggerd");
                Commands.addChannel(channel);
            }
        });
        FontManager.instance().setTypefaceImmediate(v);
        return v;

    }

    @Override
    public Channel getItem(int position) {
        return channels.get(position);
    }

    public class MyChannelViewHolder {

        TextView name ;
        CircleImageView image ;
        Button add;
        Button delete;
        TextView title ;
        ImageReceiver avatarImage;
        AvatarDrawable avatarDrawable;

    }
}
