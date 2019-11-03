package com.example.driveshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class analytics extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private int spincheck;
    private Context context = null;
    private JSONObject userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        sendPostRequest();

        spinner = findViewById(R.id.spinnerAnalytics);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.analyticsOptions, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        context = getApplicationContext();

        // fill table

        spincheck = 0;
        LineChart lineChart = (LineChart) findViewById(R.id.line);
        lineChart.setVisibility(View.INVISIBLE);
    }

    private void sendPostRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/statistics/Stat";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            userInfo = new JSONObject(response);
                            fillTable();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(analytics.this, "Successfully added request", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println(error.fillInStackTrace());
                        Toast.makeText(analytics.this, "Failed to create request.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", getIntent().getStringExtra("username"));
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selcted = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), selcted, Toast.LENGTH_LONG).show();
        TableLayout tableLayout = (TableLayout) findViewById(R.id.Analytics_Table);
        LineChart lineChart = (LineChart) findViewById(R.id.line);
        if (selcted.equals("List")) {
            spincheck = 0;
            // fill table
            try {
                fillTable();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tableLayout.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.INVISIBLE);
        } else if (selcted.equals("Graph")) {
            spincheck = 1;
            tableLayout.setVisibility(View.INVISIBLE);
            lineChart.setVisibility(View.VISIBLE);
            // fill graph
            try {
                fillGraph();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void fillTable() throws JSONException {
        if (userInfo != null) {
            final TableLayout tableLayout = (TableLayout) findViewById(R.id.fixed_column);
            int count = tableLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = tableLayout.getChildAt(i);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }

            TextView textView = findViewById(R.id.wtftest1);
            TextView cost = findViewById(R.id.analyticsCost);
            TextView gasoline = findViewById(R.id.analyticsGasoline);
            Iterator<String> keys = userInfo.keys();
            double tcost = 0, tgasoline = 0;
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject cur = new JSONObject((String) userInfo.get(key));
                tcost = tcost + Double.parseDouble((String) cur.get("2"));
                tgasoline = tgasoline + Double.parseDouble((String) cur.get("3"));

                TableRow tableRow = new TableRow(this);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                tableRow.setLayoutParams(layoutParams);

                TextView text1 = new TextView(this);
                text1.setText(cur.get("0") + " | " + cur.get("1")  + " | Cost:" + cur.get("2") + " | Gasoline: " + cur.get("3"));
                text1.setBackgroundColor(Color.parseColor("#ffffcc"));
                text1.setGravity(Gravity.CENTER);
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                text1.setLayoutParams(params);
                tableRow.addView(text1, 0);
                tableLayout.addView(tableRow);
            }
            cost.setText("Total Cost Saved : " + tcost);
            gasoline.setText("Total Gasoline Saved : " + tgasoline);
        }
    }

    private void fillGraph() throws JSONException {
        if(userInfo!=null) {
            LineChart lineChart = (LineChart) findViewById(R.id.line);
            ArrayList<Entry> yAXESsin = new ArrayList<>();
            Float x, y;

            Iterator<String> keys = userInfo.keys();
            int count =1;
            while(keys.hasNext()) {
                String key = keys.next();
                JSONObject temper = new JSONObject((String) userInfo.get(key));
                String index = String.valueOf(count);
                String gasoline = String.valueOf(temper.get("3"));
                x = (float) Double.parseDouble(index);
                y = (float) Double.parseDouble(gasoline);
                yAXESsin.add(new Entry(x, y));
                count++;
            }

            ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
            LineDataSet lineDataSet1 = new LineDataSet(yAXESsin, "Gasoline Saved");
            lineDataSet1.setDrawCircles(false);
            lineDataSet1.setColor(Color.BLUE);
            lineDataSets.add(lineDataSet1);
            lineChart.setData(new LineData(lineDataSets));
            lineChart.setVisibleXRangeMaximum(65f);
        }
    }
}
