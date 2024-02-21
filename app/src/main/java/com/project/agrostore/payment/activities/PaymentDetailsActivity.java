package com.project.agrostore.payment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.agrostore.BuildConfig;
import com.project.agrostore.R;
import com.project.agrostore.databinding.ActivityPaymentDetailsBinding;
import com.project.agrostore.payment.model.PaymentModel;
import com.project.agrostore.shopping.model.ProductModel;
import com.project.agrostore.ui.repository.agrostoreRepositoryImpl;
import com.project.agrostore.utils.Constants;
import com.project.agrostore.viewmodel.AgroViewModel;
import com.project.agrostore.viewmodel.agrostoreViewModelFactory;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentDetailsActivity extends AppCompatActivity implements PaymentResultWithDataListener, ExternalWalletListener {

    ActivityPaymentDetailsBinding binding;
    String address;
    StringBuilder stringBuilder = new StringBuilder();
    FirebaseAuth auth;
    FirebaseUser user;
    int countBackPressed;
    private ArrayList<ProductModel> productCartList = new ArrayList<>();
    private AlertDialog.Builder alertDialogBuilder;
    private AgroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_details);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initializeagrostoreViewModel();
        Checkout.preload(getApplicationContext());
        Intent intent = getIntent();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        productCartList = (ArrayList<ProductModel>) intent.getSerializableExtra("productItemList");
        address = intent.getStringExtra("address");

        String totalAmount = intent.getStringExtra("totalAmount");
        String[] amountArr = totalAmount.split(" ");
        double amount = Double.parseDouble(amountArr[1]);

        for (int product = 0; product < productCartList.size(); product++) {
            stringBuilder.append(productCartList.get(product).getTitle()).append(" ₹ ").append(productCartList.get(product).getPrice()).append(" Q ").append(productCartList.get(product).getQuantity()).append("\n");
        }
        binding.tvProductNameAndPrice.setText("Product list- \n" + stringBuilder);
        binding.tvAddress.setText("Address- " + address);
        binding.tvPaymentDate.setText("Date- " + date);
        binding.tvTotalPayAmt.setText("Total- " + totalAmount);

        alertDialogBuilder = new AlertDialog.Builder(PaymentDetailsActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");

        binding.btnProceed.setOnClickListener(v -> {
            if (amount != 0.0) {
                startPayment((int) (amount * 100));
            }
        });
    }

    public void startPayment(int amount) {
        printLog("Amount - " + amount);
        final Activity activity = this;
        final Checkout checkout = new Checkout();
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Agro Store");
            options.put("description", "Food Shopping");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);

            options.put("currency", "INR");
            options.put("amount", amount);
            options.put("payment_capture", 1);
            JSONObject preFill = new JSONObject();
            preFill.put("email", user.getEmail());
            preFill.put("contact", user.getPhoneNumber());
            options.put("prefill", preFill);
            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        try {
            printLog(paymentData.getData().toString());
            uploadTransactionDetailToFirebase(paymentData);
            showAlertMessageWithStatus(paymentData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            printLog(paymentData.getData().toString());
            uploadTransactionDetailToFirebase(paymentData);
            showAlertMessageWithStatus(paymentData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeagrostoreViewModel() {
        agrostoreRepositoryImpl agrostoreRepository = new agrostoreRepositoryImpl();
        viewModel = ViewModelProviders.of(this, new agrostoreViewModelFactory(agrostoreRepository, this)).get(AgroViewModel.class);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try {
            printLog(paymentData.getData().toString());
            uploadTransactionDetailToFirebase(paymentData);
            showAlertMessageWithStatus(paymentData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void uploadTransactionDetailToFirebase(PaymentData paymentData) {
        PaymentModel paymentModel;
        if (paymentData.getPaymentId() != null) {
            binding.tvPaymentStatus.setText(getString(R.string.payment_success));
            for (int product = 0; product < productCartList.size(); product++) {
                paymentModel = new PaymentModel(
                        productCartList.get(product).getTitle(),
                        productCartList.get(product).getImageUrl(),
                        productCartList.get(product).getPrice(),
                        "true",
                        paymentData.getPaymentId() + "-" + product
                );
                viewModel.uploadTransaction(paymentModel, Constants.plainStringEmail(user.getEmail()));
            }
        } else {
            binding.tvPaymentStatus.setText(getString(R.string.payment_failed));

        }
    }

    public void showAlertMessageWithStatus(PaymentData paymentData) {
        if (paymentData.getPaymentId() != null) {
            viewModel.deleteCartData(Constants.plainStringEmail(user.getEmail()));
            alertDialogBuilder.setMessage(
                    "Payment Success"
                            + "\nPayment ID: " + paymentData.getPaymentId()
                            + "\nSignature: " + paymentData.getSignature()
                            + "\nContact: " + paymentData.getUserContact()
                            + "\nEmail: " + paymentData.getUserEmail()
                            + "\nExternalWallet: " + paymentData.getExternalWallet()
                            + "\nPayment Data: " + paymentData.getData()
            );
            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                startActivity(new Intent(this, PaymentHistoryActivity.class));
                finish();
            });
            alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialogBuilder.show();
        } else {
            alertDialogBuilder.setMessage(
                    "Payment Failed"
                            + "\nPayment ID: " + paymentData.getPaymentId()
                            + "\nSignature: " + paymentData.getSignature()
                            + "\nPayment Data: " + paymentData.getData()

            );
            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                finish();
            });
            alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialogBuilder.show();
        }
    }

    @Override
    public void onBackPressed() {
        countBackPressed++;
        if (countBackPressed == 1) {
            Constants.showToast(this, getString(R.string.exit_message));
        } else {
            finish();
            countBackPressed = 0;
            super.onBackPressed();
        }
    }

    private void printLog(String message) {
        Log.d("paymentActivity", message);
    }
}