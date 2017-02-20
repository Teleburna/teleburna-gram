/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.cafemember.messenger.mytg.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.cafemember.messenger.mytg.listeners.Refrashable;
import org.cafemember.ui.DialogsActivity;
import org.json.JSONObject;
import org.cafemember.messenger.AndroidUtilities;
import org.cafemember.messenger.ApplicationLoader;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.ui.ActionBar.ActionBar;
import org.cafemember.ui.ActionBar.ActionBarMenu;
import org.cafemember.ui.ActionBar.BaseFragment;
import org.cafemember.ui.Components.LayoutHelper;
@SuppressLint("ValidFragment")

public class TransfareActivity extends Fragment implements Refrashable, SwipeRefreshLayout.OnRefreshListener {

    private EditText phoneField;
    private EditText amountField;
    private RadioGroup radioGroup;
    private RadioButton typeJoin;
    private RadioButton typeView;
    private View contactsButton;
    private View doneButton;
    private TextView checkTextView;
    private int checkReqId = 0;
    private String lastCheckName = null;
    private Runnable checkRunnable = null;
    private boolean lastNameAvailable = false;
    private Context context;
    private final DialogsActivity dialogsActivity;

    private final static int done_button = 1;
    private final static int contact_button = 2;
    private final int PICK_CONTACT = 7;

    @SuppressLint("ValidFragment")
    public TransfareActivity(DialogsActivity dialogsActivity){
        this.dialogsActivity = dialogsActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
//        View layout = inflater.inflate(R.layout.channels_layout, null);
        /*this.context = context;
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("MenuTransfare", R.string.MenuTransfare));
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
*/



        LinearLayout fragmentView = new LinearLayout(context);
        ((LinearLayout) fragmentView).setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        TextView ruleTextView = new TextView(context);
        ruleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        ruleTextView.setTextColor(0xff212121);
        ruleTextView.setGravity(Gravity.RIGHT);
//        ruleTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ShareText", R.string.ShareText)));
        ruleTextView.setText(LocaleController.getString("transfare_rule", R.string.transfare_rule));
        ((LinearLayout) fragmentView).addView(ruleTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 10, 24, 30));

        phoneField = new EditText(context);
        phoneField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        phoneField.setHintTextColor(0xff979797);
        phoneField.setTextColor(0xff212121);
        phoneField.setMaxLines(1);
        phoneField.setLines(1);
        phoneField.setGravity(Gravity.RIGHT);
        phoneField.setPadding(0, 0, 0, 0);
        phoneField.setSingleLine(true);
        phoneField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        phoneField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        phoneField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        phoneField.setHint(LocaleController.getString("TransfareUserHint", R.string.TransfareUserHint));
        AndroidUtilities.clearCursorDrawable(phoneField);

        amountField = new EditText(context);
        amountField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        amountField.setHintTextColor(0xff979797);
        amountField.setTextColor(0xff212121);
        amountField.setMaxLines(1);
        amountField.setGravity(Gravity.RIGHT);
        amountField.setLines(1);
        amountField.setPadding(0, 0, 0, 0);
        amountField.setSingleLine(true);
        amountField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        amountField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        amountField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        amountField.setHint(LocaleController.getString("TransfareAmountHint", R.string.TransfareAmountHint));
        AndroidUtilities.clearCursorDrawable(amountField);

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

        Button button = new Button(context);

        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        button.setTextColor(0xff6d6d72);
        button.setGravity(Gravity.CENTER);
        TextView helpTextView = new TextView(context);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(0xff6d6d72);
        helpTextView.setGravity(Gravity.RIGHT);
        helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("RefHelp", R.string.RefHelp)));
        button.setText("ارسال");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveName();
            }
        });
        ((LinearLayout) fragmentView).addView(helpTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));
        ((LinearLayout) fragmentView).addView(amountField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));
        ((LinearLayout) fragmentView).addView(button, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 24, 24, 0));


        /*View v = LayoutInflater.from(context).inflate(R.layout.transfare_type, null);
        typeJoin = (RadioButton)v.findViewById(R.id.joinRadio);
        typeView = (RadioButton)v.findViewById(R.id.viewRadio);
        ((LinearLayout) fragmentView).addView(v, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));*/
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
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(error);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        dialogsActivity.showDialog(builder.create());
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
        if (getContext() == null ) {
            return;
        }
        String newName = phoneField.getText().toString();
        String amount = amountField.getText().toString();

        if(amount == null || amount.length() == 0){
            showErrorAlert("مقدار انتقال را مشخص کنید");
            return;
        }
        int amountInt;
        try {
            amountInt = Integer.parseInt(amount);
        }catch (Exception e){
            showErrorAlert("مقدار انتقال باید عدد باشد");
            return;
        }
        if(amountInt < 100){
            showErrorAlert("مقدار انتقال باید بیشتر از 100 باشد");
            return;
        }
        int transfareType = 1;
        /*if(typeJoin.isChecked()){
            transfareType = 1;
        }
        else if(typeView.isChecked()){
            transfareType = 2;
        }*/
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setCancelable(false);
        progressDialog.show();
        Commands.transfare(newName,amountInt,transfareType, new OnResponseReadyListener() {
            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                progressDialog.dismiss();
                if(error){
                    showErrorAlert(message);
                }
                else {
                    Toast.makeText(context,"انتقال با موفقیت انجام شد",Toast.LENGTH_SHORT).show();
//                    finishFragment();
                }
            }
        });
    }
    private void getContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

//    @Override
    public void onActivityResultFragment(int reqCode, int resultCode, Intent data) {
//        super.onActivityResultFragment(reqCode, resultCode, data);
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

//    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            phoneField.requestFocus();
            AndroidUtilities.showKeyboard(phoneField);
        }
    }

    @Override
    public void refresh() {
//        loadMore();
    }

    @Override
    public void onRefresh() {
//        loadMore();
    }
}
