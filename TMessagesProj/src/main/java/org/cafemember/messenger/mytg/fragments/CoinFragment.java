package org.cafemember.messenger.mytg.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.cafemember.messenger.mytg.listeners.OnCoinsReady;
import org.cafemember.messenger.mytg.listeners.Refrashable;
import org.cafemember.messenger.mytg.util.Defaults;
import org.json.JSONException;
import org.json.JSONObject;
import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.adapter.CoinsAdapter;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.ui.ActionBar.ActionBar;

/**
 * Created by Masoud on 6/2/2016.
 */
@SuppressLint("ValidFragment")
public class CoinFragment extends Fragment implements Refrashable {

    View view;
    TextView total;
    ListView listViewView, listViewJoin;
    CoinsAdapter /*viewAdapter,*/ joinAdapter;
    int totalCoins;
    private boolean viewCoinMod = false;
    public CoinFragment(boolean viewCoinMod){
        this.viewCoinMod = viewCoinMod;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.coins_layout,null);

        FontManager.instance().setTypefaceImmediate(view);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {

        final ProgressBar loading = (ProgressBar)view.findViewById(R.id.progressBar2);
        loading.setVisibility(View.VISIBLE);
        listViewJoin = (ListView) view.findViewById(R.id.listJoin);
        total = (TextView) view.findViewById(R.id.total);
        totalCoins = Defaults.getInstance().getMyCoin();
        total.setText(totalCoins+"");
        Commands.loadCoins(new OnCoinsReady() {
            @Override
            public void onCoins(int viewCoins, int joinCoins) {
                total.setText(joinCoins+"");
            }
        });
        Commands.coinsPrice(new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                loading.setVisibility(View.GONE);
                if(!error){
                    try {
                        data = data.getJSONObject("data");
                        int len = 0;
                        joinAdapter = new CoinsAdapter(getActivity(),R.layout.adapter_buy_coin,data.getJSONArray("joinCoins"));
                        listViewJoin.setAdapter(joinAdapter);
                        listViewJoin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    Log.d("COIN","Clicked");
                                    JSONObject js = joinAdapter.getItem(position);
                                    if(Integer.parseInt(js.getString("price")) > 10000){
                                        Toast.makeText(getContext(),"در آپدیت بعدی این بسته فعال خواهد شد",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String sku = js.getString("sku");
                                    Log.d("COIN","SKU: "+sku);
                                    Commands.buy(sku);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    view.findViewById(R.id.error).setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
