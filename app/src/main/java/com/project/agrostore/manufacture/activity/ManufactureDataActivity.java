package com.project.agrostore.manufacture.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.project.agrostore.R;
import com.project.agrostore.databinding.ActivityManufactureDataBinding;
import com.project.agrostore.manufacture.adapter.ProductAdapter;
import com.project.agrostore.manufacture.listener.ManufactureAdminListener;
import com.project.agrostore.shopping.activity.ProductDetailActivity;
import com.project.agrostore.shopping.model.ProductModel;
import com.project.agrostore.ui.repository.agrostoreRepositoryImpl;
import com.project.agrostore.utils.Constants;
import com.project.agrostore.utils.Permissions;
import com.project.agrostore.utils.Resource;
import com.project.agrostore.viewmodel.AgroViewModel;
import com.project.agrostore.viewmodel.agrostoreViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ManufactureDataActivity extends AppCompatActivity implements ManufactureAdminListener {

    private final ArrayList<ProductModel> productModelArrayList = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseUser user;
    private ActivityManufactureDataBinding binding;
    private DatabaseReference databaseReference;
    private ProductAdapter productAdapter;
    private AgroViewModel agroViewModel;
    private boolean isLocalizedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manufacture_data);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Intent intent = getIntent();
        isLocalizedData = intent.getBooleanExtra("localizedData", false);
        initializeagrostoreViewModel();
        if (Permissions.checkConnection(this)) {
            getProductListFromFirebase();
        }
        binding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvUsername.setVisibility(View.GONE);
                binding.ivSearch.setVisibility(View.GONE);
                binding.searchBar.setVisibility(View.VISIBLE);
            }
        });

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchBar.setVisibility(View.GONE);
                binding.tvUsername.setVisibility(View.VISIBLE);
                binding.ivSearch.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProduct(newText);
                return false;
            }
        });

    }

    private void getProductListFromFirebase() {
        LiveData<Resource<List<ProductModel>>> observeProductData;
        if (!isLocalizedData) {
            agroViewModel.getProductModelLivedata();
            observeProductData = agroViewModel.observeProductLivedata;
        } else {
            agroViewModel.getLocalizedProductDataList();
            observeProductData = agroViewModel.observeLocalizedProductLivedata;
        }

        observeProductData.observe(this, productModelResource -> {
            switch (productModelResource.status) {
                case ERROR:
                    binding.shimmer.stopShimmer();
                    binding.shimmer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                    binding.tvNoDataFoundErr.setText(productModelResource.message);
                    break;
                case LOADING:
                    binding.shimmer.startShimmer();
                    break;
                case SUCCESS:
                    if (productModelResource.data != null) {
                        productModelArrayList.clear();
                        productModelArrayList.addAll(productModelResource.data);
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.tvNoDataFoundErr.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        setRecyclerView();
                    } else {
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                        binding.tvNoDataFoundErr.setText(getText(R.string.no_data_found));
                        binding.recyclerView.setVisibility(View.GONE);
                    }
                    break;
            }
        });
    }

    public void initializeagrostoreViewModel() {
        agrostoreRepositoryImpl agrostoreRepository = new agrostoreRepositoryImpl();
        agroViewModel = ViewModelProviders.of(this, new agrostoreViewModelFactory(agrostoreRepository, this)).get(AgroViewModel.class);
    }

    private void setRecyclerView() {
        productAdapter = new ProductAdapter(this, productModelArrayList, this, 0);
        binding.recyclerView.setAdapter(productAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
    }

    private void performProductRemovalAction(ProductModel productModel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManufactureDataActivity.this);
        alertDialog.setTitle(R.string.remove_product_dialog_msg);
        alertDialog.setMessage(getString(R.string.product_remove_message));
        alertDialog.setIcon(R.drawable.app_icon4);

        alertDialog.setPositiveButton(R.string.remove, (dialog, which) -> {
            LiveData<Resource<String>> observeProductRemovalStatus;
            if (isLocalizedData) {
                agroViewModel.removeLocalizedProduct(productModel.getTitle());
                observeProductRemovalStatus = agroViewModel.observeLocalizedProductRemovalLivedata;
            } else {
                agroViewModel.removeProductFromFirebase(productModel.getTitle());
                observeProductRemovalStatus = agroViewModel.observeProductRemovalLivedata;
            }

            observeProductRemovalStatus.observe(this, stringResource -> {
                switch (stringResource.status) {
                    case ERROR:
                        Constants.showToast(this, getString(R.string.failed_to_remove_prodcut));
                        break;
                    case LOADING:
                        break;
                    case SUCCESS:
                        Constants.showToast(this, "Successfully removed item from list.. wait few sec more");
                        getProductListFromFirebase();
                }
            });

        });

        alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.show();
    }

    private void searchProduct(String query) {
        ArrayList<ProductModel> searchProductList = new ArrayList<ProductModel>();
        for (int i = 0; i < productModelArrayList.size(); i++) {
            if (productModelArrayList.get(i).getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchProductList.add(productModelArrayList.get(i));
            }
        }
        if (productModelArrayList.isEmpty()) {
            Constants.showToast(this, getString(R.string.no_data_found));
        } else {
            productAdapter.searchInProductList(searchProductList);
        }
    }

    @Override
    public void performOnCardClickAction(ProductModel productModel) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("productModel", productModel);
        startActivity(intent);
    }

    @Override
    public void performEditAction(ProductModel productModel, int position) {
        Intent intent = new Intent(this, ManufactureActivity.class);
        intent.putExtra("productModel", productModel);
        intent.putExtra("isLocalizedData", isLocalizedData);
        intent.putExtra("manufactureEditAction", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void performDeleteAction(ProductModel productModel, int position) {
        performProductRemovalAction(productModel);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}