package com.project.agrostore.payment.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.agrostore.R;
import com.project.agrostore.databinding.ActivityPaymentHistoryBinding;
import com.project.agrostore.payment.adapter.HistoryAdapter;
import com.project.agrostore.payment.model.PaymentModel;
import com.project.agrostore.ui.repository.agrostoreRepositoryImpl;
import com.project.agrostore.utils.Constants;
import com.project.agrostore.utils.Permissions;
import com.project.agrostore.viewmodel.AgroViewModel;
import com.project.agrostore.viewmodel.agrostoreViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

public class PaymentHistoryActivity extends AppCompatActivity {

    private final ArrayList<PaymentModel> paymentModelArrayList = new ArrayList<>();
    ActivityPaymentHistoryBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    private AgroViewModel agroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_history);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Transaction History");
        actionBar.setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        initializeagrostoreViewModel();
        if (Permissions.checkConnection(this)) {
            binding.tvNoDataFoundErr.setVisibility(View.GONE);
            getTransactionHistoryList(Constants.plainStringEmail(Objects.requireNonNull(user.getEmail())));
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.shimmer.setVisibility(View.GONE);
            binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
            binding.tvNoDataFoundErr.setText(getString(R.string.check_internet_connection));
        }
    }

    private void getTransactionHistoryList(String email) {
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();
        agroViewModel.getTransactionList(email);
        agroViewModel.observePaymentHistoryLiveData.observe(this, paymentModelList -> {
            switch (paymentModelList.status) {
                case ERROR:
                    binding.shimmer.stopShimmer();
                    binding.shimmer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                    binding.tvNoDataFoundErr.setText(paymentModelList.message);
                    break;
                case LOADING:
                    break;
                case SUCCESS:
                    if (paymentModelList.data != null) {
                        paymentModelArrayList.clear();
                        paymentModelArrayList.addAll(paymentModelList.data);
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        setRecyclerView();
                    } else {
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                        binding.tvNoDataFoundErr.setText(getString(R.string.no_transaction_found));
                    }
                    break;
            }
        });
    }

    private void setRecyclerView() {
        HistoryAdapter historyAdapter = new HistoryAdapter(paymentModelArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(historyAdapter);
    }

    public void initializeagrostoreViewModel() {
        agrostoreRepositoryImpl agrostoreRepository = new agrostoreRepositoryImpl();
        agroViewModel = ViewModelProviders.of(this, new agrostoreViewModelFactory(agrostoreRepository, this)).get(AgroViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(Constants.REQUEST_CODE);
    }
}