package com.example.driveshare;

import com.twilio.rest.api.v2010.account.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/sms")
public class SendSMS {

    private static final String ACCOUNT_SID = "AC4179a57415f7e84d95e05d4277fc7dd1";
    private static final String AUTH_TOKEN = "9c12ec9e0270c35bfb80fd32bdf212bd";
    private static final String driveshare_no = "+15154003939";

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    private void sendMsg(HttpServletRequest request) throws IOException {
        twilioInit();
        BigQueryHelper bg = new BigQueryHelper();
        String driverName = request.getParameter("username");
        String customerName = request.getParameter("customername");
        int code = ThreadLocalRandom.current().nextInt(100000,1000000);
        //get customer phone number here
        PhoneNumber to = new PhoneNumber("+15157153773");
        PhoneNumber from = new PhoneNumber(driveshare_no);
        String message = "Dear "+customerName+",\nYour driver "+driverName+
               " is here. Here is your verification code:\n"+Integer.toString(code);
        MessageCreator creator = Message.creator(to, from, message);
        creator.create();
    }

    private void twilioInit() {
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);
        System.out.println("Twilio Auth Success.");
    }
}
