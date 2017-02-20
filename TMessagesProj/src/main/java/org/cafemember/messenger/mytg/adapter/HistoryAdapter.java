package org.cafemember.messenger.mytg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;

/**
 * Created by Masoud on 6/2/2016.
 */
public class HistoryAdapter extends ArrayAdapter {


    private JSONArray history;
    public HistoryAdapter(Context context, int resource, JSONArray objects) {
        super(context, resource);
        history = objects;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final JSONObject coin = getItem(position);
        HistoryViewHolder viewHolder ;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_history, parent, false);
            viewHolder = new HistoryViewHolder();
            viewHolder.title = (TextView)v.findViewById(R.id.title);
            viewHolder.state = (TextView)v.findViewById(R.id.state);
            v.setTag(viewHolder);
        }

        else {
            viewHolder = (HistoryViewHolder)v.getTag();
        }
        try {
            final String name = coin.getString("name");
            int  type = Integer.parseInt(coin.getString("type"));
            int  total = Integer.parseInt(coin.getString("total"));
            int  done = Integer.parseInt(coin.getString("done"));
            int  left = Integer.parseInt(coin.getString("left"));
            int  status = Integer.parseInt(coin.getString("status"));
            String titleText = "سفارش "+total+" ";
            String stateText = done+" ";
            if(type == 1){
                titleText += "بازدید برای پستی از"+name;
                stateText += "نفر از پست بازدید کرده اند";
            }
            else {
                titleText += "عضو برای کانال "+name;
                stateText += "عضو به گروه اضافه شده, "+left+" عضو خارج شدند";
                String state ="";
                switch (status){
                    case 0:
                        state = "در حال اجرا";
                        break;
                    case 1:
                        state = "اتمام نمایش";
                        break;
                    case 2:
                        state = "مسدود";
                        break;
                }
                stateText += "\n"+state;
            }
            viewHolder.title.setText(titleText);
            viewHolder.state.setText(stateText);
        }catch (JSONException e){
            e.printStackTrace();
        }
        FontManager.instance().setTypefaceImmediate(v);
        return v;

    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return history.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return history.length();
    }

    public class HistoryViewHolder {

        TextView title;
        TextView state;
        ImageView image ;
        Button buy;
    }
}
