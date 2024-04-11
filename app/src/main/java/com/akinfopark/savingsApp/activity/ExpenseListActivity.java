package com.akinfopark.savingsApp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyDateSelectedListener;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.expenses.AdapterExpenses;
import com.akinfopark.savingsApp.databinding.ActivityExpenseListBinding;
import com.akinfopark.savingsApp.databinding.DialogAddExpenseBinding;
import com.akinfopark.savingsApp.databinding.DialogSaveBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseListActivity extends AppCompatActivity {

    ActivityExpenseListBinding binding;
    Activity activity;
    AlertDialog dialogLoading, dialogAdd, dialogEdit;
    DialogSaveBinding edtBinding;
    DialogAddExpenseBinding addExpenseBinding;
    List<JSONObject> list = new ArrayList<>();
    AdapterExpenses adapterExpenses;
    String dateApi = "", timeApi = "";
    String expId = "";
    int page = 1;
    int currentLength = 0;
    int perPage = 10;

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, binding.Swipe);

            try {
                if (apiStatus == APIStatus.SUCCESS) {
                    if (tag.equalsIgnoreCase("list")) {
                        if (response.getBoolean("status")) {
                            list.clear();
                            CommonFunctions.setJSONArray(response.getJSONObject("result"), "Expenses", list, adapterExpenses);
                            if (list.size() < 1) {
                                binding.empty.setVisibility(View.VISIBLE);
                            } else {
                                binding.empty.setVisibility(View.GONE);
                            }

                            currentLength =response.getJSONObject("result").getInt("Count");
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("delete")) {
                        if (response.getBoolean("status")) {
                            callApi();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("add")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            dialogAdd.dismiss();
                            callApi();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("update")) {
                        dialogEdit.dismiss();
                        if (response.getBoolean("status")) {
                            callApi();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            } catch (Exception e) {

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        addExpenseBinding = DialogAddExpenseBinding.inflate(getLayoutInflater());
        dialogAdd = DialogUtils.getCustomAlertDialog(activity, addExpenseBinding.getRoot());

        edtBinding = DialogSaveBinding.inflate(getLayoutInflater());
        dialogEdit = DialogUtils.getCustomAlertDialog(activity, edtBinding.getRoot());

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        binding.Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearPage();
            }
        });

        binding.tvAddExp.setOnClickListener(v -> {
            dialogAdd.show();
        });


        binding.RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (CommonFunctions.isLastItemDisplayingLinear(recyclerView) && currentLength >= perPage) {
                    page++;
                    callNewApi();
                }
            }
        });


        callApi();

        // Get current date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(dateFormatter);
        dateApi = formattedDate;
        //System.out.println("Current Date: " + formattedDate);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(formattedDate, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        addExpenseBinding.edtDate.setText(date.format(outputFormatter));

        // Get current time
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(timeFormatter);
        //System.out.println("Current Time: " + formattedTime);
        timeApi = formattedTime;
        LocalTime currentTime1 = LocalTime.now();
        DateTimeFormatter timeFormatter1 = DateTimeFormatter.ofPattern("hh:mm a");
        String formattedTime1 = currentTime1.format(timeFormatter1);
        addExpenseBinding.edtTime.setText(formattedTime1);


        adapterExpenses = new AdapterExpenses(activity, list, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {
                JSONObject object = list.get(i);

                Log.i("objectExp", object.toString());

                expId = object.getString("ExpenseID");

                if (v.getId() == R.id.imgEdt) {
                    edtBinding.edtAmt.setText(object.getString("ExpenseAmount"));
                    edtBinding.edtName.setText(object.getString("ExpenseName"));
                    dialogEdit.show();
                    edtBinding.layName.setVisibility(View.VISIBLE);
                }

                if (v.getId() == R.id.ImgDelete) {
                    // Toast.makeText(activity, "asdasd", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.Confirmation);
                    builder.setMessage(R.string.ar_delete);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deleteExpense(expId);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

        binding.RecyclerView.setAdapter(adapterExpenses);

        edtBinding.submitBtn.setOnClickListener(v -> {

            String amt = edtBinding.edtAmt.getText().toString();
            String name = edtBinding.edtName.getText().toString();

            if (amt.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please Enter Amount", Toast.LENGTH_SHORT).show();
            } else if (name.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please Enter Name", Toast.LENGTH_SHORT).show();
            } else {
                updateExp(amt, name);
            }

            dialogEdit.dismiss();
        });

        addExpenseBinding.submitBtn.setOnClickListener(v -> {
            String name = addExpenseBinding.edtName.getText().toString();
            String amt = addExpenseBinding.edtSavAmt.getText().toString();

            if (name.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter expense name", Toast.LENGTH_SHORT).show();
            } else if (amt.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter expense amount", Toast.LENGTH_SHORT).show();
            } else {
                addApi(name, amt);
            }

        });

        addExpenseBinding.edtDate.setOnClickListener(v -> {
            Calendar calendarMax = Calendar.getInstance();
            calendarMax.add(Calendar.MONTH, 1);
            CommonFunctions.showDatePickerMinMaxDateNew(activity, "", "", "", "endDate", myDateSelectedListener);
        });

        addExpenseBinding.edtTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            // on below line we are getting our hour, minute.
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // on below line we are initializing our Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // on below line we are setting selected time
                            // in our text view.
                            String formattedTime = String.format("%02d:%02d:%02d", hourOfDay, minute, 0);
                            timeApi = formattedTime;
                            //addExpenseBinding.edtTime.setText(formattedTime);

                            int hour = hourOfDay;
                            int min = minute;
                            String timeSuffix;

                            if (hourOfDay >= 12) {
                                timeSuffix = "PM";
                                if (hour > 12) {
                                    hour -= 12;
                                }
                            } else {
                                timeSuffix = "AM";
                                if (hour == 0) {
                                    hour = 12;
                                }
                            }

                            String formattedTime1 = String.format("%02d:%02d %s", hour, min, timeSuffix);
                            addExpenseBinding.edtTime.setText(formattedTime1);

                        }
                    }, hour, minute, false);
            // at last we are calling show to
            // display our time picker dialog.
            timePickerDialog.show();
        });

    }

    MyDateSelectedListener myDateSelectedListener = new MyDateSelectedListener() {
        @Override
        public void onDateSet(String tag, Calendar calendar, Date data) {
            if (tag.equalsIgnoreCase("endDate")) {

                String inputVal = String.valueOf(data);

                // Parse the input date string into a Date object
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    Date date = inputFormat.parse(inputVal);

                    // Format the date into the output strings
                    SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    String enddate = outputFormat1.format(date);
                    dateApi = enddate;
                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    String outputDate2 = outputFormat2.format(date);

                    addExpenseBinding.edtDate.setText(outputDate2);

                    //Toast.makeText(activity, "" + enddate, Toast.LENGTH_SHORT).show();
                    // binding.tvEnDate.setText(outputDate2);
                    // Log.i("DateValueEn", enddate + "  " + outputDate2);

                } catch (ParseException e) {

                }
            }

        }
    };

    void clearPage() {
        page = 1;
        list.clear();
        // dialogLoading.show();
        adapterExpenses.notifyDataSetChanged();
        callApi();

    }

    void addApi(String name, String amt) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("expensename", name);
        map.put("amount", amt);
        map.put("datetime", dateApi + " " + timeApi);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addexpense", map, "add", new Bundle(), apiCallbacks);

    }

    void updateExp(String amt, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("expenseid", expId);
        map.put("expensename", name);
        map.put("amount", amt);
        map.put("datetime", dateApi + " " + timeApi);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "updateexpense", map, "update", new Bundle(), apiCallbacks);
    }

    void deleteExpense(String expeId) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("expenseid", expeId);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "deleteexpense", map, "delete", new Bundle(), apiCallbacks);
    }

    void callNewApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);
       // dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "expenses", map, "list", new Bundle(), apiCallbacks);
    }

    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "expenses", map, "list", new Bundle(), apiCallbacks);
    }

}