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

@RestController
@RequestMapping("/sms")
public class SendSMS {

    private static final String ACCOUNT_SID = "AC4179a57415f7e84d95e05d4277fc7dd1";
    private static final String AUTH_TOKEN = "9c12ec9e0270c35bfb80fd32bdf212bd";
    private static final String driveshare_no = "+15154003939";

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    private void sendMsg(HttpServletRequest request) throws IOException {
        twilioInit();
        PhoneNumber to = new PhoneNumber(request.getParameter("phoneNumber"));
        PhoneNumber from = new PhoneNumber(driveshare_no);
        String message = request.getParameter("message");
        MessageCreator creator = Message.creator(to, from, message);
        creator.create();
        System.out.println("Message sent.");
    }

    private void twilioInit() {
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);
        System.out.println("Twilio Auth Success.");
    }
}
