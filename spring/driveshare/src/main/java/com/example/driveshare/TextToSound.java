package com.example.driveshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.File;

import javax.print.attribute.standard.Media;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import net.sourceforge.javaflacencoder.FLAC_FileEncoder;

public class TextToSound {

	
	public static byte[] getSound(String text , String type) throws IOException
	{
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
		      // Set the text input to be synthesized
		      SynthesisInput input = SynthesisInput.newBuilder()
		            .setText(text)
		            .build();

		      // Build the voice request, select the language code ("en-US") and the ssml voice gender
		      // ("neutral")
		      VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
		          .setLanguageCode(type)
		          .setSsmlGender(SsmlVoiceGender.NEUTRAL)
		          .build();

		      // Select the type of audio file you want returned
		      AudioConfig audioConfig = AudioConfig.newBuilder()
		          .setAudioEncoding(AudioEncoding.MP3)
		          .build();

		      // Perform the text-to-speech request on the text input with the selected voice parameters and
		      // audio file type
		      SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
		          audioConfig);

		      // Get the audio contents from the response
		      ByteString audioContents = response.getAudioContent();

		      // Write the response to the output file.
		      try (OutputStream out = new FileOutputStream("output.mp3")) {
		        out.write(audioContents.toByteArray());

		      }
		      		      
		      return audioContents.toByteArray();
		      
		    }
	}
	

	
	
}
