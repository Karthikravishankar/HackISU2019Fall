package com.example.driveshare;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/preferences")
public class Preferences {

    private FireBaseHelper firebaseHelper = new FireBaseHelper();


    @RequestMapping(value = "/storePreferences", method = RequestMethod.POST)
    public String storePreferences(HttpServletRequest request) throws IOException, InterruptedException {
        String Movies = request.getParameter("Movies");
        String Vacation = request.getParameter("Vacation");
        String Music = request.getParameter("Music");
        String Sports = request.getParameter("Sports");
        String Pets = request.getParameter("Pets");
        String Drink = request.getParameter("Drink");
        String Food = request.getParameter("Food");
        String Age = request.getParameter("Age");
        String Listener = request.getParameter("Listener");
        String Talker = request.getParameter("Talker");
        String User = request.getParameter("User");
        firebaseHelper.updateFirebaseData("preferences",User,"User", User);
        firebaseHelper.updateFirebaseData("preferences",User,"Talker", Talker);
        firebaseHelper.updateFirebaseData("preferences",User,"Listener", Listener);
        firebaseHelper.updateFirebaseData("preferences",User,"Age", Age);
        firebaseHelper.updateFirebaseData("preferences",User,"Food", Food);
        firebaseHelper.updateFirebaseData("preferences",User,"Drink", Drink);
        firebaseHelper.updateFirebaseData("preferences",User,"Pets", Pets);
        firebaseHelper.updateFirebaseData("preferences",User,"Sports", Sports);
        firebaseHelper.updateFirebaseData("preferences",User,"Music", Music);
        firebaseHelper.updateFirebaseData("preferences",User,"Movies", Movies);
        firebaseHelper.updateFirebaseData("preferences",User,"Vacation", Vacation);

        return "Success";
    }

    @RequestMapping(value = "/getPreferences", method = RequestMethod.POST)
    public String getPreferences(HttpServletRequest request) throws IOException, InterruptedException, JSONException {
        JSONObject jsonObject = new JSONObject();
        String User = request.getParameter("User");

        String Movies = firebaseHelper.getFirebaseData("preferences",User,"Movies");
        String Vacation = firebaseHelper.getFirebaseData("preferences",User,"Vacation");
        String Music = firebaseHelper.getFirebaseData("preferences",User,"Music");
        String Sports = firebaseHelper.getFirebaseData("preferences",User,"Music");
        String Pets = firebaseHelper.getFirebaseData("preferences",User,"Music");
        String Drink = firebaseHelper.getFirebaseData("preferences",User,"Music");
        String Food = firebaseHelper.getFirebaseData("preferences",User,"Music");
        String Age = firebaseHelper.getFirebaseData("preferences",User,"Music");
        String Listener = firebaseHelper.getFirebaseData("preferences",User,"Music");
        String Talker = firebaseHelper.getFirebaseData("preferences",User,"Music");

        jsonObject.put("Movies", Movies);
        jsonObject.put("Vacation", Vacation);
        jsonObject.put("Music", Music);
        jsonObject.put("Sports", Sports);
        jsonObject.put("Pets", Pets);
        jsonObject.put("Drink", Drink);
        jsonObject.put("Food", Food);
        jsonObject.put("Age", Age);
        jsonObject.put("Listener", Listener);
        jsonObject.put("Talker", Talker);
        return jsonObject.toString();
    }
}
