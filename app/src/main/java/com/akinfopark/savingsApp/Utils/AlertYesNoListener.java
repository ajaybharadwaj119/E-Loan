package com.akinfopark.savingsApp.Utils;

public interface AlertYesNoListener {

    void onYesClick( String requestCode);
    void onNoClick(String requestCode);
    void onNeutralClick(String requestCode);
}
