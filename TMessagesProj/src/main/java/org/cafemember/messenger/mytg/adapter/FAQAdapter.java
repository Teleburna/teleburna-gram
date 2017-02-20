package org.cafemember.messenger.mytg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.FontManager;

/**
 * Created by Masoud on 6/2/2016.
 */
public class FAQAdapter extends ArrayAdapter {


    private JSONArray history;
    public FAQAdapter(Context context, int resource, JSONArray objects) {
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
            viewHolder.question = (TextView)v.findViewById(R.id.title);
            viewHolder.answer = (TextView)v.findViewById(R.id.state);
            viewHolder.stateTitle = (TextView)v.findViewById(R.id.state_title);
            v.setTag(viewHolder);
        }

        else {
            viewHolder = (HistoryViewHolder)v.getTag();
        }
        viewHolder.stateTitle.setWidth(0);
        try {
            final String question = coin.getString("q");
            final String answer = coin.getString("a");
            viewHolder.question.setText(question);
            viewHolder.answer.setText(answer);
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

        TextView question;
        TextView answer;
        TextView stateTitle;
    }
}
