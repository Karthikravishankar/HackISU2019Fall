package com.example.driveshare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class AI {
    JSONObject userPref;
    BigQueryHelper bigQueryHelper = new BigQueryHelper();
    public AI(JSONObject preferences){
        userPref = preferences;
    }

    public String getBestDriverUserName() throws JSONException {
        JSONObject jsonObject = bigQueryHelper.getAvailableDriver();
        Iterator<String> keys = jsonObject.keys();
        JSONObject best = new JSONObject();
        int bestVal =0;
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject curr = (JSONObject) jsonObject.get(key);
            if(getHeu(curr)>bestVal){
                bestVal = getHeu(curr);
                best = curr;
            }
        }
        return String.valueOf(best.get("User"));
    }

    public int getHeu(JSONObject driverPref) throws JSONException {
        Iterator<String> keys = driverPref.keys();
        int hue=0;
        while(keys.hasNext()) {
            String key = keys.next();
            if(key.equals("Driving")==false && key.equals("From") == false && key.equals("Destination") == false) {
                if (driverPref.get(key)== userPref.get(key)) {
                    hue=hue+10;
                }
            }
        }
        // if close by equation, add 50

        return hue;
    }
}
