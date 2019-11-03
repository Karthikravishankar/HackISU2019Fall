package com.example.driveshare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class AI {
    private String latitude="",longitude="",destlatitude="",destlongitude="";
    private JSONObject userPref;
    private BigQueryHelper bigQueryHelper = new BigQueryHelper();
    private FireBaseHelper fireBaseHelper = new FireBaseHelper();
    private JSONObject hashMap= new JSONObject();
    public AI(JSONObject preferences,String userName,String latitude,String longitude,String destlatitude,String destlongitude) throws JSONException {
        this.latitude = latitude;
        this.longitude=longitude;
        this.destlatitude = destlatitude;
        this.destlongitude = destlongitude;
        userPref = preferences;
        hashMap = getBestDriverUserName(userName);
    }

    public JSONObject getHashMap(){
        return hashMap;
    }

    public JSONObject getBestDriverUserName(String userName) throws JSONException {
        JSONObject jsonObject =fireBaseHelper.getAvailableDriver(userName);
        System.out.println(jsonObject.toString());
//        Iterator<String> keys = jsonObject.keys();
//        HashMap<String, String> best = new HashMap<String, String>();
//        int bestVal =0;
//        while(keys.hasNext()) {
//            String key = keys.next();
//            HashMap<String, String> cur = (HashMap<String, String>) jsonObject.get(key);
//            if(getHeu(cur)>bestVal){
//                bestVal = getHeu(cur);
//                best = cur;
//            }
//        }
        JSONArray jsonArray = (JSONArray) jsonObject.get("result");
        JSONObject best= new JSONObject();
        int bestVal=0;
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject object =new JSONObject((String) jsonArray.get(i));
            // now do something with the Object
            double tempHue = getHeu(object);
            tempHue = tempHue +(50 - getShortestDistance(Double.parseDouble((String) object.get("from_lat")),
                    Double.parseDouble((String)object.get("from_lng")),Double.parseDouble((String)object.get("dest_lat")),
                    Double.parseDouble((String)object.get("dest_lng")), Double.parseDouble((String) this.latitude), Double.parseDouble(this.longitude)));

            tempHue = tempHue + (50 - getShortestDistance(Double.parseDouble((String) object.get("from_lat")),
                    Double.parseDouble((String)object.get("from_lng")),Double.parseDouble((String)object.get("dest_lat")),
                    Double.parseDouble((String)object.get("dest_lng")), Double.parseDouble((String) this.destlatitude), Double.parseDouble(this.destlongitude)));
            if(bestVal<tempHue){
                bestVal = (int)Math.round(tempHue);
                best = object;
            }
        }

        return best;
    }

    public int getHeu(JSONObject driverPref) throws JSONException {
        Iterator<String> keys = driverPref.keys();
        int hue=0;
        while(keys.hasNext()) {
            String key = keys.next();
        if(key.equals("Driving")==false && key.equals("From") == false && key.equals("Destination") == false
                && key.equals("User")==false && key.equals("from_lat")==false&& key.equals("from_lng")==false
                && key.equals("dest_lat")==false && key.equals("dest_lng")==false
                && driverPref.get(key).equals(userPref.get(key))) {//                if (driverPref.get(key)== userPref.get(key)) {
                    hue=hue+10;
                }
        }

        // if close by equation, add 40
//        for (Map.Entry<String, String> entry : driverPref.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            if(key.equals("Driving")==false && key.equals("From") == false && key.equals("Destination") == false
//            && key.equals("User")==false && key.equals("from_lat")==false&& key.equals("from_lng")==false
//                    && key.equals("dest_lat")==false && key.equals("dest_lng")==false
//            && value.equals(userPref.get(key))==false) {
//                if (value.equals(userPref.get(key))) {
//                    hue=hue+10;
//                }
//            }
//        }
        return hue;
    }

    private double getShortestDistance(double latFroma, double lngFroma, double latDestb, double lngDestb, double latUser, double lngUser){
        double AB = distance(latFroma, lngFroma, latDestb, lngDestb);
        double BC = distance(latDestb, lngDestb, latUser, lngUser);
        double AC = distance(latFroma, lngFroma, latUser, lngUser);

        double s = (AB + BC + AC) / 2;
        double area = (float) Math.sqrt(s * (s - AB) * (s - BC) * (s - AC));
        double AD = (2 * area) / BC;
        return AD;
    }

    //Miles
    public double distance(double lat1,
                                   double lon1,double lat2,
                                  double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 3956;

        // calculate the result
        return(c * r);
    }
}
