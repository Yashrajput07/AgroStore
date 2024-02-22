package com.project.agrostore.transport.listener;

import com.project.agrostore.transport.model.VehicleModel;

public interface AdminListener {

    void performOnCardClickAction(VehicleModel vehicleModel);

    void performEditAction(VehicleModel vehicleModel);

    void performDeleteAction(VehicleModel vehicleModel);

}
