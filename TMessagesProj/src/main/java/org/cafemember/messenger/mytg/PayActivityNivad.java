package org.cafemember.messenger.mytg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.cafemember.messenger.mytg.listeners.OnJoinSuccess;
import org.cafemember.messenger.mytg.util.paytool.IabHelper;
import org.cafemember.messenger.mytg.util.paytool.IabResult;
import org.cafemember.messenger.mytg.util.paytool.Purchase;

import io.nivad.iab.BillingProcessor;
import io.nivad.iab.TransactionDetails;

public class PayActivityNivad extends Activity {


	static final int RC_REQUEST = 10001;

	private static final String NIVAD_APP_ID = "c7b6ed70-2853-4551-b405-24eababfb4f8";
    private static final String NIVAD_APP_SECRET = "OsrppVcttGu4Sbvwt6D0HqYkhmNMWbHlmciGqGsRuq6XiL6hOodZxgmH3iEWurwf";
    final String BAZAAR_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCv9ZefYrRfZovotKAf9xxi4jbx2i0NhYUtIix6f8N1FptTTueSheSp4r/qjTsDmqpSJW63taxyVBq9X9SByUyvYGglxp+4X/wfVE+RmA+WEb/DE0JisWXunEir1RvmIntaBlV6GWksIKmA2iJIJ41crreyawFRHujmZpWKwAYf0b4274KtW5j4Hjocv12kCYZDQZZ0D8/+KbiFI0g+UVK4eb0Ny05b424AmXE9mA0CAwEAAQ==";

	IabHelper mHelper;

	ListView listView;
	Dialog progressDialog;
	private String sku;
	private BillingProcessor mNivadBilling;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sku = getIntent().getStringExtra("sku");
		mNivadBilling = new BillingProcessor(this, BAZAAR_KEY, NIVAD_APP_ID, NIVAD_APP_SECRET, mBillingMethods);


	}


	private BillingProcessor.IBillingHandler mBillingMethods = new BillingProcessor.IBillingHandler() {

		@Override
		public void onBillingInitialized() {
			// این متد زمانی که سرویس پرداخت درون برنامه‌ای آماده‌ی کار می‌شود فراخوانی می‌شود
            mNivadBilling.purchase(PayActivityNivad.this, sku);

        }


		@Override
		public void onProductPurchased(String sku, final TransactionDetails details) {
			// این متد پس از خرید موفق فراخوانی می‌شود
            Commands.checkBoughtItem(sku, new OnJoinSuccess() {
                @Override
                public void OnResponse(boolean ok) {
                    if (ok) {
                        if (mNivadBilling.consumePurchase(details.productId)) {
                            Toast.makeText(PayActivityNivad.this,"خرید انجام شد",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PayActivityNivad.this,"خرید انجام شد ولی بازار نفهمید!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                }
            });
		}

		@Override
		public void onBillingError(int code, Throwable error) {
			// این متد زمانی که اشکالی در فرایند پرداخت به وجود بیاید فراخوانی می‌شود
            Toast.makeText(PayActivityNivad.this,"خطا "+code,Toast.LENGTH_SHORT).show();
        finish();
        }


		@Override
		public void onPurchaseHistoryRestored() {
			// این متد زمانی فراخوانی می‌شود که لیست محصولاتی که کاربر خریده اما هنوز مصرف نشده‌اند از بازار دریافت شده اند
		}
	};

	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (!mNivadBilling.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onDestroy() {
        if (mNivadBilling != null)
            mNivadBilling.release();
    	super.onDestroy();

    }
	
    

    
    
     
}
