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

import org.cafemember.messenger.mytg.util.Defaults;
import org.cafemember.ui.DialogsActivity;
import org.json.JSONObject;
import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.ImageReceiver;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Channel;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.fragments.ChannelsFragment;
import org.cafemember.messenger.mytg.listeners.OnJoinSuccess;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.tgnet.TLRPC;
import org.cafemember.ui.Components.AvatarDrawable;

import de.hdodenhof.circleimageview.*;
import java.util.ArrayList;

/**
 * Created by Masoud on 6/2/2016.
 */
public class ChannelsAdapter extends ArrayAdapter {


    private final ChannelsFragment channelsFragment;
    private final DialogsActivity dialogsActivity;
    private ArrayList<Channel> channels;
    public ChannelsAdapter(Context context, int resource, ArrayList<Channel> objects, ChannelsFragment channelsFragment, DialogsActivity dialogsActivity) {
        super(context, resource, objects);
        channels = objects;
        this.channelsFragment = channelsFragment;
        this.dialogsActivity = dialogsActivity;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Channel channel = getItem(position);
        ChannelViewHolder viewHolder ;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.channel_item, parent, false);
            viewHolder = new ChannelViewHolder();
            viewHolder.name = (TextView)v.findViewById(R.id.name);
            viewHolder.title = (TextView)v.findViewById(R.id.title);
            viewHolder.image = (CircleImageView)v.findViewById(R.id.image);
            viewHolder.join = (Button)v.findViewById(R.id.join);
            viewHolder.report = (Button)v.findViewById(R.id.report);
            v.setTag(viewHolder);
        }

        else {
            viewHolder = (ChannelViewHolder)v.getTag();
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
        viewHolder.name.setText("@"+channel.name);
        viewHolder.title.setText(channel.title);
        viewHolder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("گزارش کانال");

// Set up the input
                final EditText input = new EditText(getContext());
                input.setHint("دلیل گزارش");
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        Commands.report((int) channel.id, m_Text,new OnResponseReadyListener() {
                            @Override
                            public void OnResponseReady(boolean error, JSONObject data, String message) {
                                Toast.makeText(getContext(),error?"خطا در گزارش کانال":"کانال گزارش شد",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
        viewHolder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelsFragment.setLoader(View.VISIBLE);
                Commands.join(channel, new OnJoinSuccess() {
                    @Override
                    public void OnResponse(boolean ok) {
                        channelsFragment.setLoader(View.GONE);
                        if(ok){
                            remove(channel);
                            notifyDataSetChanged();
                            Log.e("COUNT",getCount()+"");
                            if(getCount() == 0){
//                                channelsFragment.loadMore();
                                Toast.makeText(getContext(),"فعلا کانالی برای نمایش وجود نداره لطفا دقایقی دیگر مراجعه کنید",Toast.LENGTH_LONG).show();
                            }
                            if(Defaults.getInstance().openOnJoin()){
                                dialogsActivity.showChannel(channel.id);
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "خطا در عضویت کانال",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        FontManager.instance().setTypefaceImmediate(v);
        return v;

    }

    @Override
    public Channel getItem(int position) {
        return channels.get(position);
    }

    public class ChannelViewHolder {

        TextView name ;
        CircleImageView image ;
        Button join;
        Button report;
        TextView title ;
        ImageReceiver avatarImage;
        AvatarDrawable avatarDrawable;

    }
}
