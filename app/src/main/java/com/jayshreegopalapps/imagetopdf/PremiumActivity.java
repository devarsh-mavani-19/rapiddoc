package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class PremiumActivity extends AppCompatActivity {
    Button btnBuy;
    BillingClient billingClient;
    SkuDetails skuDetails;
    AlertDialog dialog;
    SharedPreferences preferences;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        btnBuy = findViewById(R.id.btn_buy);
        textView = findViewById(R.id.text_view_premium);
        LoadSettings.load(PremiumActivity.this);
        LoadSettings.setViewTheme(btnBuy, PremiumActivity.this);

        dialog = new AlertDialog.Builder(PremiumActivity.this).setView(R.layout.layout_loading_dialog).setCancelable(false).create();
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                int responseCode = billingClient.launchBillingFlow(PremiumActivity.this, billingFlowParams).getResponseCode();
                if(responseCode != BillingClient.BillingResponseCode.OK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PremiumActivity.this, getString(R.string.failed_to_load_purchase), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        billingClient = BillingClient.newBuilder(PremiumActivity.this).setListener(new PurchasesUpdatedListener() {

            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && list != null) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                } else {
                    // Handle any other error codes.
                    Toast.makeText(PremiumActivity.this, getString(R.string.order_cancelled), Toast.LENGTH_SHORT).show();
                }
            }
        }).enablePendingPurchases().build();
        dialog.show();
        refresh();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    List<String> skuList = new ArrayList<>();
                    skuList.add("product_100_office_to_pdf_files");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    // Process the result.
                                    for(SkuDetails details : skuDetailsList) {
                                        skuDetails = details;
                                        System.out.println(details.getTitle() + " " + details.getPrice());
                                    }
                                    btnBuy.setText(("Buy for " + skuDetails.getPrice()));
                                    dialog.dismiss();
                                }
                            });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(PremiumActivity.this, getString(R.string.connection_lost), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    void refresh() {
        int credits = preferences.getInt("officetopdf", 0);
        textView.setText(("you have " + credits + " files left"));
    }
    private void handlePurchase(Purchase purchase) {
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            ConsumeResponseListener listener = new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // Handle the success of the consume operation.

                        Toast.makeText(PremiumActivity.this, getString(R.string.purchase_successful), Toast.LENGTH_SHORT).show();
                        int current = preferences.getInt("officetopdf", 0);
                        preferences.edit().putInt("officetopdf", (current + 100)).commit();
                        refresh();
                    }
                }
            };

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            billingClient.consumeAsync(consumeParams, listener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
