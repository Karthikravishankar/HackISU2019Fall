package com.example.driveshare;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class Translator {
  public static String translatelanguage(String text , String type1 , String type2) throws Exception {
    // Instantiates a client
    Translate translate = TranslateOptions.getDefaultInstance().getService();

    // Translates some text into Russian
    Translation translation =
        translate.translate(
            text,
            TranslateOption.sourceLanguage(type1),
            TranslateOption.targetLanguage(type2));


   return translation.getTranslatedText();
  }
  
  public static String detect(String input)
  {
	  Translate translate = TranslateOptions.getDefaultInstance().getService();
	  List<String> texts = new LinkedList<>();
	  texts.add(input);
	  List<Detection> detections = translate.detect(texts);

	  System.out.println("Language(s) detected:");
	  for (Detection detection : detections) {
	    System.out.printf("\t%s\n", detection);
	    	return detection.getLanguage();
	  }

	  return null;
  }
  
}