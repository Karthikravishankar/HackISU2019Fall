package com.example.driveshare;


import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SoundToText {

  /**
   * Demonstrates using the Speech API to transcribe an audio file.
   */
  public static String trans(byte[] data , String type) throws Exception {
    // Instantiates a client
    try (SpeechClient speechClient = SpeechClient.create()) {

        	
      // The path to the audio file to transcribe

      ByteString audioBytes = ByteString.copyFrom(data);

      // Builds the sync recognize request
      RecognitionConfig config = RecognitionConfig.newBuilder()
          .setEncoding(AudioEncoding.FLAC)
          .setEnableAutomaticPunctuation(true)
          .setSampleRateHertz(24000)
          .setLanguageCode(type)
          .build();
      RecognitionAudio audio = RecognitionAudio.newBuilder()
          .setContent(audioBytes)
          .build();

      // Performs speech recognition on the audio file
      RecognizeResponse response = speechClient.recognize(config, audio);
      List<SpeechRecognitionResult> results = response.getResultsList();

      
      for (SpeechRecognitionResult result : results) {
        // There can be several alternative transcripts for a given chunk of speech. Just use the
        // first (most likely) one here.
        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
        System.out.printf("Transcription: %s%n", alternative.getTranscript());
      }
      
      
    }
	return null;
  }
  
  public static String testTran() throws IOException
  {
	  try (SpeechClient speechClient = SpeechClient.create()) {

      	
	      // The path to the audio file to transcribe

		  String fileName = "output.flac";

	      // Reads the audio file into memory
	      Path path = Paths.get(fileName);
	      byte[] data = Files.readAllBytes(path);
	      ByteString audioBytes = ByteString.copyFrom(data);

	      // Builds the sync recognize request
	      RecognitionConfig config = RecognitionConfig.newBuilder()
	          .setEncoding(AudioEncoding.FLAC)
	          .setEnableAutomaticPunctuation(true)
	          .setSampleRateHertz(24000)
	          .setLanguageCode("en")
	          .build();
	      RecognitionAudio audio = RecognitionAudio.newBuilder()
	          .setContent(audioBytes)
	          .build();

	      // Performs speech recognition on the audio file
	      RecognizeResponse response = speechClient.recognize(config, audio);
	      List<SpeechRecognitionResult> results = response.getResultsList();

	      
	      for (SpeechRecognitionResult result : results) {
	        // There can be several alternative transcripts for a given chunk of speech. Just use the
	        // first (most likely) one here.
	        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
	        System.out.printf("Transcription: %s%n", alternative.getTranscript());
	      }
	      
	      
	    }
		return null;
  }
  
  
  
}