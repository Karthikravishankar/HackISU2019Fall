package com.example.driveshare;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class Translator {
  public static String translatelanguage(String text , String type) throws Exception {
    // Instantiates a client
    Translate translate = TranslateOptions.getDefaultInstance().getService();

    // Translates some text into Russian
    Translation translation =
        translate.translate(
            text,
            TranslateOption.sourceLanguage(type),
            TranslateOption.targetLanguage("en"));


   return translation.getTranslatedText();
  }
}