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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import org.json.JSONObject;
import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.ApplicationLoader;
import org.cafemember.messenger.FileLog;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.MessagesController;
import org.cafemember.messenger.MessagesStorage;
import org.cafemember.messenger.NotificationCenter;
import org.cafemember.messenger.R;
import org.cafemember.messenger.UserConfig;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.tgnet.ConnectionsManager;
import org.cafemember.tgnet.RequestDelegate;
import org.cafemember.tgnet.TLObject;
import org.cafemember.tgnet.TLRPC;
import org.cafemember.ui.ActionBar.ActionBar;
import org.cafemember.ui.ActionBar.ActionBarMenu;
import org.cafemember.ui.ActionBar.BaseFragment;
import org.cafemember.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class AddRefActivity extends BaseFragment {

    private EditText phoneField;
    private View contactsButton;
    private View doneButton;
    private TextView checkTextView;
    private int checkReqId = 0;
    private String lastCheckName = null;
    private Runnable checkRunnable = null;
    private boolean lastNameAvailable = false;
    private Context context;

    private final static int done_button = 1;
    private final static int contact_button = 2;
    private final int PICK_CONTACT = 7;

    @Override
    public View createView(Context context) {
        this.context = context;
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("RefTitle", R.string.RefTitle));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == done_button) {
                    saveName();
                }else if (id == contact_button) {
                    getContacts();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        contactsButton = menu.addItemWithWidth(contact_button, R.drawable.user_profile, AndroidUtilities.dp(56));
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));




        fragmentView = new LinearLayout(context);
        ((LinearLayout) fragmentView).setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        phoneField = new EditText(context);
        phoneField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        phoneField.setHintTextColor(0xff979797);
        phoneField.setTextColor(0xff212121);
        phoneField.setMaxLines(1);
        phoneField.setLines(1);
        phoneField.setPadding(0, 0, 0, 0);
        phoneField.setSingleLine(true);
        phoneField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        phoneField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        phoneField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        phoneField.setHint(LocaleController.getString("RefHint", R.string.RefHint));
        AndroidUtilities.clearCursorDrawable(phoneField);
        phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE && doneButton != null) {
                    doneButton.performClick();
                    return true;
                }
                return false;
            }
        });

        ((LinearLayout) fragmentView).addView(phoneField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));



        checkTextView = new TextView(context);
        checkTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        checkTextView.setGravity(Gravity.RIGHT );
        ((LinearLayout) fragmentView).addView(checkTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, 24, 12, 24, 0));

        TextView helpTextView = new TextView(context);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(0xff6d6d72);
        helpTextView.setGravity(Gravity.RIGHT);
        helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("RefHelp", R.string.RefHelp)));
        ((LinearLayout) fragmentView).addView(helpTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));

        phoneField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                checkUserName(phoneField.getText().toString(), false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkTextView.setVisibility(View.GONE);
        FontManager.instance().setTypefaceImmediate(fragmentView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean animations = preferences.getBoolean("view_animations", true);
        if (!animations) {
            phoneField.requestFocus();
            AndroidUtilities.showKeyboard(phoneField);
        }
    }

    private void showErrorAlert(String error) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(error);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    private boolean checkUserName(final String name, boolean alert) {
        if (name != null && name.length() > 0) {
            checkTextView.setVisibility(View.VISIBLE);
        } else {
            checkTextView.setVisibility(View.GONE);
        }
        if (alert && name.length() == 0) {
            return true;
        }
        lastNameAvailable = false;
        if (name != null) {
            for (int a = 0; a < name.length(); a++) {
                char ch = name.charAt(a);
                /*if (a == 0 && ch != '9') {
                    if (alert) {
                        showErrorAlert(LocaleController.getString("RefNameError", R.string.RefNameError));
                    } else {
                        checkTextView.setText(LocaleController.getString("RefNameError", R.string.RefNameError));
                        checkTextView.setTextColor(0xffcf3030);
                    }
                    return false;
                }
                else if (a == 1 && ch != '9') {
                    if (alert) {
                        showErrorAlert(LocaleController.getString("RefNameError", R.string.RefNameError));
                    } else {
                        checkTextView.setText(LocaleController.getString("RefNameError", R.string.RefNameError));
                        checkTextView.setTextColor(0xffcf3030);
                    }
                    return false;
                }*/
                if (!(ch >= '0' && ch <= '9' )) {
                    if (alert) {
                        showErrorAlert(LocaleController.getString("RefNameError", R.string.RefNameError));
                    } else {
                        checkTextView.setText(LocaleController.getString("RefNameError", R.string.RefNameError));
                        checkTextView.setTextColor(0xffcf3030);
                    }
                    return false;
                }
            }
        }
        if (name == null || name.length() != 12 ) {
            if (alert) {
                showErrorAlert(LocaleController.getString("RefNameError", R.string.RefNameError));
            } else {
                checkTextView.setText(LocaleController.getString("RefNameError", R.string.RefNameError));
                checkTextView.setTextColor(0xffcf3030);
            }
            return false;
        }
        checkTextView.setVisibility(View.GONE);
        return true;
    }


    private void saveName() {
        if (!checkUserName(phoneField.getText().toString(), true)) {
            return;
        }
        if (getParentActivity() == null ) {
            return;
        }
        String newName = phoneField.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setCancelable(false);
        progressDialog.show();
        Commands.ref(newName, new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                progressDialog.dismiss();
                if(error){
                    showErrorAlert(message);
                }
                else {
                    finishFragment();
                }
            }
        });
    }
    private void getContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResultFragment(int reqCode, int resultCode, Intent data) {
        super.onActivityResultFragment(reqCode, resultCode, data);
        if (reqCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {

                Uri contactData = data.getData();
                Cursor c = context.getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {

                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = context.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null);
                        phones.moveToFirst();
                        String cNumber = phones.getString(phones.getColumnIndex("data1"));
//                        System.out.println("number is:" + cNumber);
                        if(cNumber != null )
                        {

                            if(cNumber.length() >= 10 ){
                                cNumber = "98"+cNumber.substring(cNumber.length()-10);
                            }
                            phoneField.setText(cNumber);
                        }
                    }
//                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                }
            }
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            phoneField.requestFocus();
            AndroidUtilities.showKeyboard(phoneField);
        }
    }
}
