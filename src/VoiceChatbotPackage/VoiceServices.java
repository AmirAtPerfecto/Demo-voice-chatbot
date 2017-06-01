package VoiceChatbotPackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import perfecto.PerfectoLabUtils;
import perfecto.PerfectoUtils;

public abstract class VoiceServices{
	private static SpeechToText S2Tservice = null;
	private static TextToSpeech T2Sservice = null;
	private static void instantiateT2S(){
		if (null != T2Sservice) return;
		T2Sservice = new TextToSpeech();
		T2Sservice.setUsernameAndPassword(System.getenv().get("WATSON_T2S_USERNAME"), System.getenv().get("WATSON_T2S_PASSWORD"));

	}
	private static void instantiateS2T(){
		if (null != S2Tservice) return;
		S2Tservice = new SpeechToText();
		S2Tservice.setUsernameAndPassword(System.getenv().get("WATSON_S2T_USERNAME"), System.getenv().get("WATSON_S2T_PASSWORD"));

	}
	public static String speechToText(String speechFileURL) throws IOException{
		instantiateS2T();
		Path filePath = download(speechFileURL);  
		File audio = new File(filePath.toString());
		//File audio = new File(file);
		SpeechResults transcript = S2Tservice.recognize(audio).execute();
		System.out.println(transcript);
		return transcript.toString();		
	}

	public static void textToSpeech(String text, String filePath, boolean uploadToPerfectoCloud, String repositoryKey){
		textToSpeech(text, filePath, uploadToPerfectoCloud, repositoryKey, Voice.EN_ALLISON, true);
	}
	public static void textToSpeech(String text, String filePath, boolean uploadToPerfectoCloud, String repositoryKey, Voice voiceType, boolean addNoise){
		instantiateT2S();
		try {
			InputStream stream = T2Sservice.synthesize(text, voiceType, 
					AudioFormat.WAV).execute();
			InputStream in = WaveUtils.reWriteWaveHeader(stream);
			OutputStream out = new FileOutputStream(filePath);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				if (addNoise)	{
				}
				out.write(buffer, 0, length);
			}
			out.close();
			in.close();
			stream.close();

			if (uploadToPerfectoCloud){
				PerfectoLabUtils.uploadMedia(System.getenv().get("PERFECTO_CLOUD"), System.getenv().get("PERFECTO_CLOUD_USERNAME"), System.getenv().get("PERFECTO_CLOUD_PASSWORD"), filePath, repositoryKey);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String listen(RemoteWebDriver driver, boolean waitForVisualValidation, String visualValidation, int seconds) throws IOException, InterruptedException{

		String response = "";
		try {
			String audioFileRecording = PerfectoUtils.startAudioRecording(driver);
			if (waitForVisualValidation){
				PerfectoUtils.ocrTextCheck(driver, visualValidation, 90, 20);
				long UXtimer = PerfectoUtils.getUXTimer(driver);
				System.out.println("Expected visual response: "+ visualValidation + " Took: " + UXtimer+ " milliseconds");
			} else
				Thread.sleep(seconds * 1000);
			PerfectoUtils.stopAudioRecording(driver);
			return audioFileRecording;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;

	}

	private static Path download(String sourceURL) throws IOException
	{
		URL url = new URL(sourceURL);
		String fileName = sourceURL.substring(sourceURL.lastIndexOf('/') + 1, sourceURL.length());
		Path targetPath = new File(System.getenv().get("Project_Path")+"/media/" + fileName).toPath();
		Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

		return targetPath;
	}

}