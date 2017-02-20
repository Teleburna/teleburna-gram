package org.cafemember.messenger.mytg.fragments;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.cafemember.messenger.LocaleController;
import org.cafemember.messenger.R;
import org.cafemember.messenger.mytg.Commands;
import org.cafemember.messenger.mytg.FontManager;
import org.cafemember.messenger.mytg.adapter.FAQAdapter;
import org.cafemember.messenger.mytg.adapter.HistoryAdapter;
import org.cafemember.messenger.mytg.listeners.OnResponseReadyListener;
import org.cafemember.ui.ActionBar.ActionBar;
import org.cafemember.ui.ActionBar.ActionBarMenu;
import org.cafemember.ui.ActionBar.BaseFragment;
import org.cafemember.ui.Components.LayoutHelper;

import java.util.ArrayList;

/**
 * Created by Masoud on 7/19/2016.
 */
public class FAQActivity extends BaseFragment {
//    private Context context;

    /*private EditText firstNameField;
    private EditText lastNameField;
    private View headerLabelView;
    private View doneButton;*/

    private final static int done_button = 1;
    private EditText firstNameField;

    @Override
    public View createView(final Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("MenuFAQ", R.string.MenuFAQ));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
//        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));

        /*TLRPC.User user = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
        if (user == null) {
            user = UserConfig.getCurrentUser();
        }*/

        final ProgressBar loader = new ProgressBar(context);
        final ListView listView = new ListView(context);

        FrameLayout farme = new FrameLayout(context);
        fragmentView = farme;
        fragmentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        loader.setVisibility(View.VISIBLE);
        listView.setBackgroundResource(R.color.my_background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setDivider(context.getDrawable(R.drawable.transparent));
        }
        else {
            listView.setDivider(context.getResources().getDrawable(R.drawable.transparent));
        }
        farme.addView(loader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT,36, 24, 24, 24, 0));
        farme.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));


        /*fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/
        ArrayList<JSONObject> faqlist = new ArrayList<>();
        String [][] faqArray = {
                {
                        "1- اگه سکه خریداری کنم  ولی به حسابم اضافه نشود چکار کنم ؟\n",
                "در صورتی که خرید کرده اید اما سکه ای دریافت نکرده اید به منوی کشویی برنامه رفته و بر روی پشتیبانی کلیک کرده تا به مدیریت پشتیبانی برنامه  در تلگرام وصل شوید و مقدار خرید و توکن خرید خود را برای ما ارسال کنید در صورت صحت خرید در کمتر از 24ساعت سکه های شما اضافه خواهد شد.\n"
                },
                {
                        "2- اگه اعضایی که به کانالم اضافه میشوند بعد از گرفتن سکه کانالم را ترک کنن تکلیف چیست ؟\n",
"در صورتی که در 10 روز آینده کسی کانال رو ترک کنن تعداد 2 سکه از حسابشون کم میشه و کانال شما تا 10 روز به همان اندازه که لفت خورده دوباره نمایش داده خواهد شد تا لفت های شما جبران شود ( این سیستم در واقع انحصاری کافه ممبر هستش تا نیاز نباشه در مقابل هر لفت شما یک سکه دریافت کنید که هرگز جبران آن عضو را نخواهد کرد چرا که شما برای هر عضو دو سکه پرداخت کرده اید و یک سکه هرگز جبران آن را نخواهد کرد)"
                },
                {
                        "3- چرا سکه های من منفی شده است ؟\n",
                "در صورتی که قبل دو هفته کانالی که عضو شدین رو ترک کنین به ازای هر کانالی که ترک کنین 2 سکه از شما کم میشه \n"
                },
                {
                        "4- ممبرهایی که این برنامه اضافه میکند واقعی هستن ؟\n",
                "تمامی کاربرانی که عضو کانال شما میشوند کاربران واقعی همین برنامه هستند و کافه ممبر از هیچ گونه سیستم دیگر برا افزایش ممبر کانال ها استفاده نمیکند\n"
                },
                {
                        "5- چطور میتوانم اعضا را از ترک کانال منع کنم؟\n",
                "افزایش ممبر با کافه ممبر موجب افزایش اعتبار کانال شما خواهد شد و همین در دراز مدت منجر به جذب کاربران بیشتر خواهد شد چرا که کاربران به کانال هایی با اعضای بالا بیشتر اعتماد دارند و سریع تر عضو میشوند، همچنین برای حفظ کاربران موجود از محتوای مناسب و جذاب استفاده کنید.\n"
                },
                {
                        "6- چطوری میتوانم کانالم را در لیست درخواست ممبر قرار بدم؟\n",
                "وارد قسمت سفارش ممبر در صفحه اصلی برنامه بشین سپس میتوانید هر تعداد کانال را دوست دارید ثبت کنید و برای هر کانال تعداد ممبر درخواستی رو انتخاب کنین اگه موجودی کافی نداشته باشید از بخش خرید سکه اقدام به خرید سکه کنید و یا با عضویت در دیگر کانال ها سکه ی رایگان دریافت نمایید.\n"
                },
                {
                        "7- چرا هنگامی که در روز عضو کانال های زیادی میشوم برنامه پیغام میدهد و اجازه ی عضو شدن داده نمیشود ؟\n",
                "اگر بیش از 50 کانال توی یک ساعت عضو بشین تلگرام حداقل برای 10 ساعت اکانت شما رو محدود میکنه بنابراین فقط باید 10 ساعت یا نهایتا 1 روز صبر کنید تا محدودیت تون برطرف بشه\n"
                },
                {
                        "8- در صورت داشتن سوال و یا وجود مشکل چگونه با شما در ارتباط باشم ؟\n",
                "در صورتی که در برنامه مشکلی وجود دارد و یا برای شما سوالی مطرح شده و یا از برنامه انتقادی دارید و یا برنامه در گوشی شما مشکل دارد و یا سوال دیگر میتوانید از بخش پشتیبانی در نرم افزار به id پشتیبانی پیام دهید و در کمتر از 24 ساعت جواب خود را دریافت کنید .   "
                },
        };
        for(String [] object: faqArray){
            JSONObject js = new JSONObject();
            try {
                js.put("q",object[0]);
                js.put("a",object[1]);
                faqlist.add(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final JSONArray faq = new JSONArray(faqlist);
                        listView.setAdapter(new FAQAdapter(context,R.layout.adapter_history,faq));
                    loader.setVisibility(View.GONE);



        FontManager.instance().setTypefaceImmediate(fragmentView);

        return fragmentView;
    }



}
