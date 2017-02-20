/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.cafemember.messenger.mytg.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cafemember.messenger.UserConfig;
import org.cafemember.messenger.mytg.util.Defaults;
import org.json.JSONObject;
import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.ApplicationLoader;
import org.cafemember.messenger.FileLog;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.ui.ActionBar.ActionBar;
import org.cafemember.ui.ActionBar.ActionBarMenu;
import org.cafemember.ui.ActionBar.BaseFragment;
import org.cafemember.ui.Components.LayoutHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareActivity extends BaseFragment {

    private View doneButton;
    private TextView checkTextView;
    private Context context;
    private final String TAG = "SHARE";

    private final String Share_Text2 = "دریافت سکه رایگان !!!\n" +
            "این برنامه  را به دوستانتان معرفی کنید و سکه رایگان دریافت کنید!\n" +
            "هر نفر که عضو برنامه شود و شما را به عنوان  معرف اعلام کند ، به شما 30 سکه رایگان تعلق میگیرد !!!" +
            "\n" +
            " شما میتوانید با زدن دکمه  زیر  ، دعوت نامه خود را برای دوستانتان در تمام  برنامه های اجتماعی ( تلگرام ، اینستاگرام ، sms و... ) ارسال کنید . در این دعوت نامه شناسه شما به عنوام معرف قرار دارد .";

/*
    private String Share_Text = "اگر صاحب یک کانال هستید با کافه ممبر کاملا رایگان مثل بمب کاربرانتون رو افزایش بدین و کانالتان را ۱۰۰ هزار  ممبری کنید ( دانلود کاملا رایگان )\n" +
            "لطفا در برنامه من را به عنوان معرف ، معرفی کنید .\n" ;
*/


    private String Share_Text = "بهترین نرم افزار عضو گیری + بازدید رایگان پست های شما با کاربران ایرانی" ;

    private final static int done_button = 1;

    @Override
    public View createView(Context context) {
        Log.d(TAG,"Create");
        this.context = context;
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("MenuShare", R.string.MenuShare));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();




        fragmentView = new LinearLayout(context);
        ((LinearLayout) fragmentView).setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
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

        helpTextView.setText(Share_Text2);
        ((LinearLayout) fragmentView).addView(helpTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 10, 24, 0));


        Button done = new Button(context);
        done.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        done.setTextColor(0xff212121);
        done.setText("ارسال با شماره شما به عنوان معرف");
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveName(true);
            }
        });
        ((LinearLayout) fragmentView).addView(done, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 30, 24, 0));

        Button done2 = new Button(context);
        done2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        done2.setTextColor(0xff212121);
        done2.setText("ارسال بدون شماره شما");
        done2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveName(false);
            }
        });
        ((LinearLayout) fragmentView).addView(done2, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 30, 24, 0));

        TextView helpTextView2 = new TextView(context);
        helpTextView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView2.setTextColor(0xff212121);
        helpTextView2.setGravity(Gravity.RIGHT);
//        helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ShareText", R.string.ShareText)));

        helpTextView2.setText(LocaleController.getString("shareB",R.string.shareB));
        ((LinearLayout) fragmentView).addView(helpTextView2, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 10, 24, 0));

        FontManager.instance().setTypefaceImmediate(fragmentView);

        return fragmentView;
    }



    private void saveName(boolean withTell) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
//            Uri pictureUri = Uri.parse("file://my_picture");
            Uri uri = Uri.parse("android.resource://org.cafemember.messenger/"
                    +R.drawable.cafe_share_icon);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.cafe_share_icon);
            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
//                        intent.setType("text/plain");
            String link = "http://cafebazaar.ir/app/ir.appmy.cafemember2";
            Log.d(TAG,link);
//        String myPhone = Defaults.getInstance().getMyToken().split("\\.")[0];
            String myPhone = UserConfig.getCurrentUser().phone;
            Log.d(TAG,myPhone);
            String text;
            if(withTell){

                text = Share_Text + "\nلطفا شماره من را به عنوان معرف انتخاب کنید  : \n"+myPhone+"\nلینک برنامه: "+link;
            }
            else {

                text = Share_Text + "\nلینک برنامه: "+link;
            }
//            Log.d(TAG,Share_Text);
            intent.putExtra(Intent.EXTRA_TEXT,text);
//            intent.putExtra(Intent.EXTRA_STREAM, uri);

            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteFriends", R.string.InviteFriends)), 500);
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

}
