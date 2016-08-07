package org.cafemember.messenger.mytg.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.fragments.ChannelsFragment;
import org.cafemember.messenger.mytg.fragments.MyChannelFragment;
import org.cafemember.messenger.mytg.fragments.CoinFragment;
import org.cafemember.messenger.mytg.fragments.TgFragment;
import org.cafemember.ui.DialogsActivity;

/**
 * Created by Masoud on 6/2/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private View frameLayout;
    private TgFragment tgFragment;
    DialogsActivity dialogsActivity;
    private ChannelsFragment channelsFragment;
    private MyChannelFragment myChannelFragment;
    private CoinFragment coinFragment;
    public ViewPagerAdapter(FragmentManager fm, DialogsActivity dialogsActivity, View dialogsLayout) {
        super(fm);
        this.dialogsActivity = dialogsActivity;
        this.frameLayout = dialogsLayout;
        channelsFragment = new ChannelsFragment(dialogsActivity);
        myChannelFragment = new MyChannelFragment(dialogsActivity);
        coinFragment = new CoinFragment(false);

//        tgFragment = new TgFragment(frameLayout);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            /*case 0:
                FontManager.instance().setTypefaceImmediate(frameLayout);

                return new TgFragment(frameLayout);*/
            case 1:
                return channelsFragment;
            case 2:
                return myChannelFragment;
            case 0:
                return coinFragment;

        }
        return null;    // Which Fragment should be dislpayed by the viewpager for the given position
        // In my case we are showing up only one fragment in all the three tabs so we are
        // not worrying about the position and just returning the CoinFragment
    }

    @Override
    public int getCount() {
        return 3;           // As there are only 3 Tabs
    }

}
