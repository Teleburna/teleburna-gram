/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.cafemember.messenger.mytg.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.cafemember.messenger.FileLog;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.ui.ActionBar.ActionBar;
import org.cafemember.ui.ActionBar.ActionBarMenu;
import org.cafemember.ui.ActionBar.BaseFragment;
import org.cafemember.ui.Components.LayoutHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RulesActivity extends BaseFragment {

    private View doneButton;
    private TextView checkTextView;
    private Context context;
    final String Share_Text = "دریافت سکه رایگان !!!\n" +
            "این برنامه  را به دوستانتان معرفی کنید و سکه رایگان دریافت کنید!\n" +
            "هر نفر که عضو برنامه شود و شما را به عنوان  معرف اعلام کند ، به شما 1000 سکه ویو  رایگان تعلق میگیرد.\n" +
            "\n" +
            " شما میتوانید با زدن دکمه  زیر  ، دعوت نامه خود را برای دوستانتان در تمام  برنامه های اجتماعی ( تلگرام ، اینستاگرام ، sms و... ) ارسال کنید . در این دعوت نامه شناسه شما به عنوام معرف قرار دارد .";

    private final static int done_button = 1;

    @Override
    public View createView(Context context) {
        this.context = context;
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("MenuHelp", R.string.MenuHelp));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();




        fragmentView = new ScrollView(context);
        LinearLayout layout = new LinearLayout(context);
        ((LinearLayout) layout).setOrientation(LinearLayout.VERTICAL);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        TextView helpTextView = new TextView(context);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(0xff212121);
        helpTextView.setGravity(Gravity.RIGHT);
//        helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ShareText", R.string.ShareText)));
        helpTextView.setText(LocaleController.getString("AppRules", R.string.AppRules));
//        helpTextView.setText(Share_Text);
        layout.addView(helpTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 10, 24, 0));
        ((ScrollView) fragmentView).addView(layout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER, 0,0,0,0));


        FontManager.instance().setTypefaceImmediate(fragmentView);

        return fragmentView;
    }




}
