package org.cafemember.messenger.mytg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.cafemember.messenger.mytg.FontManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Commands;

/**
 * Created by Masoud on 6/2/2016.
 */
public class CoinsAdapter extends ArrayAdapter {


    private JSONArray coins;
    public CoinsAdapter(Context context, int resource, JSONArray objects) {
        super(context, resource);
        coins = objects;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final JSONObject coin = getItem(position);
        CoinViewHolder viewHolder ;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_buy_coin, parent, false);
            viewHolder = new CoinViewHolder();
            viewHolder.description = (TextView)v.findViewById(R.id.description);
            viewHolder.off = (TextView)v.findViewById(R.id.off);
            viewHolder.image = (ImageView)v.findViewById(R.id.image);
            viewHolder.count = (TextView) v.findViewById(R.id.count);
            v.setTag(viewHolder);
        }

        else {
            viewHolder = (CoinViewHolder)v.getTag();
        }
        try {
            final String price = coin.getString("description");
            final String off = coin.getString("price");
            /*if(type.equals("1")){
                viewHolder.image.setImageResource(R.drawable.ic_profile_blue_64);
            }
            else {
                viewHolder.image.setImageResource(R.drawable.ic_eye_blue_64);
            }*/
            final String id = coin.getString("sku");
            String description = coin.getString("count");
            viewHolder.description.setText(price+" تومان");
            viewHolder.off.setText(off+" تومان");
            viewHolder.count.setText(description+" سکه");
            int offPrice = Integer.parseInt(off);
            if(offPrice == Integer.parseInt(price)){
                viewHolder.description.setVisibility(View.GONE);
            }
            else {
                viewHolder.description.setVisibility(View.VISIBLE);

            }
/*
            viewHolder.count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Commands.buy(id);
                }
            });
*/
        }catch (JSONException e){
            e.printStackTrace();
        }

        FontManager.instance().setTypefaceImmediate(v);
        return v;

    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return coins.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return coins.length();
    }

    public class CoinViewHolder {

        TextView description;
        TextView off;
        ImageView image ;
        TextView count;
    }
}
