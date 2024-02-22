package com.project.agrostore.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.project.agrostore.R;
import com.project.agrostore.databinding.FragmentTransportBinding;
import com.project.agrostore.transport.adapter.OnVehicleCallClick;
import com.project.agrostore.transport.adapter.VehicleAdapter;
import com.project.agrostore.transport.model.VehicleModel;
import com.project.agrostore.ui.repository.agrostoreRepositoryImpl;
import com.project.agrostore.utils.Constants;
import com.project.agrostore.utils.Permissions;
import com.project.agrostore.viewmodel.AgroViewModel;
import com.project.agrostore.viewmodel.agrostoreViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TransportFragment extends Fragment implements OnVehicleCallClick {

    private final ArrayList<VehicleModel> vehicleItemList = new ArrayList<>();
    private FragmentTransportBinding binding;
    private VehicleAdapter vehicleAdapter;
    private AgroViewModel agroViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transport, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.hide();
        initializeagrostoreViewModel();
        if (Permissions.checkConnection(getContext())) {
            getVehicleListFromFirebase();
        } else {
            binding.shimmer.setVisibility(View.GONE);
            binding.recyclerViewVehicle.setVisibility(View.GONE);
        }

        binding.ivFilterData.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), binding.ivFilterData);
            popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.mnLowToHigh:
                        Comparator<VehicleModel> lowComparator = (item1, item2) -> {
                            if (Double.parseDouble(item1.getRates()) < Double.parseDouble(item2.getRates())) {
                                return -1;
                            } else if (Double.parseDouble(item1.getRates()) > Double.parseDouble(item2.getRates())) {
                                return 1;
                            } else {
                                return 0;
                            }
                        };
                        Collections.sort(vehicleItemList, lowComparator);
                        vehicleAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.mnHighToLow:
                        Comparator<VehicleModel> highComparator = (item1, item2) -> {
                            if (Double.parseDouble(item1.getRates()) > Double.parseDouble(item2.getRates())) {
                                return -1;
                            } else if (Double.parseDouble(item1.getRates()) < Double.parseDouble(item2.getRates())) {
                                return 1;
                            } else {
                                return 0;
                            }
                        };
                        Collections.sort(vehicleItemList, highComparator);
                        vehicleAdapter.notifyDataSetChanged();
                        return true;
                }
                return true;
            });
            popupMenu.show();
        });

        binding.ivFilterLocation.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), binding.ivFilterLocation);
            popupMenu.getMenuInflater().inflate(R.menu.location_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getTitle() != null) {
                    searchVehicle(menuItem.getTitle().toString());
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void searchVehicle(String location) {
        ArrayList<VehicleModel> searchProductList = new ArrayList<>();
        for (int i = 0; i < vehicleItemList.size(); i++) {
            if (vehicleItemList.get(i).getAddress().toLowerCase().contains(location.toLowerCase())) {
                searchProductList.add(vehicleItemList.get(i));
            }
        }
        if (searchProductList.isEmpty()) {
            binding.recyclerViewVehicle.setVisibility(View.GONE);
            binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
            binding.tvNoDataFoundErr.setText(getString(R.string.vehicle_not_found_location) + " " + location + " Location");
        } else {
            binding.recyclerViewVehicle.setVisibility(View.VISIBLE);
            binding.tvNoDataFoundErr.setVisibility(View.GONE);
            vehicleAdapter.searchInVehicleList(searchProductList);
        }
    }

    private void getVehicleListFromFirebase() {
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();
        agroViewModel.getVehicleModelLivedata();
        agroViewModel.observeTransportResourceLiveData.observe(getViewLifecycleOwner(), vehicleModelResource -> {
            switch (vehicleModelResource.status) {
                case ERROR:
                    binding.shimmer.stopShimmer();
                    binding.shimmer.setVisibility(View.GONE);
                    binding.recyclerViewVehicle.setVisibility(View.GONE);
                    binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                    binding.tvNoDataFoundErr.setText(vehicleModelResource.message);
                    break;
                case LOADING:
                    binding.shimmer.startShimmer();
                    break;
                case SUCCESS:
                    if (vehicleModelResource.data != null) {
                        vehicleItemList.clear();
                        vehicleItemList.addAll(vehicleModelResource.data);
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.recyclerViewVehicle.setVisibility(View.VISIBLE);
                        binding.tvNoDataFoundErr.setVisibility(View.GONE);
                        setRecyclerView();
                    } else {
                        binding.shimmer.setVisibility(View.GONE);
                        binding.recyclerViewVehicle.setVisibility(View.GONE);
                        binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                        binding.tvNoDataFoundErr.setText(getString(R.string.no_data_found));
                    }
                    break;
            }
        });
    }

    public void initializeagrostoreViewModel() {
        agrostoreRepositoryImpl agrostoreRepository = new agrostoreRepositoryImpl();
        agroViewModel = ViewModelProviders.of(this, new agrostoreViewModelFactory(agrostoreRepository, getContext())).get(AgroViewModel.class);
    }

    private void setRecyclerView() {
        vehicleAdapter = new VehicleAdapter(vehicleItemList, TransportFragment.this, 1);
        binding.recyclerViewVehicle.setAdapter(vehicleAdapter);
        binding.recyclerViewVehicle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewVehicle.setHasFixedSize(true);
    }

    @Override
    public void callVehicleOwner(VehicleModel vehicleModel) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + vehicleModel.getContact()));
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && Permissions.checkConnection(getContext())) {
            getVehicleListFromFirebase();
        }
    }
}
