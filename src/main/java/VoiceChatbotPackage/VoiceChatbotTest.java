package VoiceChatbotPackage;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import perfecto.AppiumTest;
import perfecto.PerfectoUtils;
import perfecto.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class VoiceChatbotTest {
	RemoteWebDriver driver;
	PerfectoExecutionContext perfectoExecutionContext;
	ReportiumClient reportiumClient;
	
	
	@Parameters({ "platformName", "platformVersion", "manufacturer", "model", "deviceName", "appID" })
	@BeforeTest
	public void beforeTest(String platformName, String platformVersion, String manufacturer, String model, String deviceName, String appID) throws IOException {
		driver = Utils.getRemoteWebDriver(platformName, platformVersion, manufacturer, model, deviceName, appID );        
      PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
              .withProject(new Project("My Project", "1.0"))
              .withJob(new Job("My Job", 45))
              .withContextTags("tag1")
              .withWebDriver(driver)
              .build();
       reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
	} 
	  @Test
	  public void test() {
		  ArrayList<String> sentences = new ArrayList<String>(); 
		  ArrayList<String> expectedVocalResponsesGoogle = new ArrayList<String>();
		  ArrayList<String> expectedVisualResponsesGoogle = new ArrayList<String>();
		  ArrayList<String> expectedVocalResponsesSiri = new ArrayList<String>();
		  ArrayList<String> expectedVisualResponsesSiri = new ArrayList<String>();
	      try {
	    	  reportiumClient.testStart("Voice Chatbot test Test", new TestContext("tag2", "tag3"));
	    	  System.out.println("Yay");
	          // write your code here
	    	  setDictionary(driver, sentences, expectedVocalResponsesGoogle, expectedVocalResponsesSiri, expectedVisualResponsesGoogle, expectedVisualResponsesSiri );
	    	  if (PerfectoUtils.getDeviceInfo(driver, PerfectoUtils.DeviceInfo.os).toLowerCase().contains("android"))
	    		  testChatbotFlowViaT2S(driver, sentences, expectedVocalResponsesGoogle, expectedVisualResponsesGoogle);
	    	  else
	    		  testChatbotFlowViaT2S(driver, sentences, expectedVocalResponsesSiri, expectedVisualResponsesSiri);
	    		  
	    	  

	          // reportiumClient.testStep("step1"); // this is a logical step for reporting
	          // reportiumClient.testStep("step2");
	          reportiumClient.testStop(TestResultFactory.createSuccess());
	      } catch (Exception e) {
	          //reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
	          e.printStackTrace();
	      }
	  }

	  @AfterTest
	  public void afterTest() {
	      try {
	          // Retrieve the URL of the Single Test Report, can be saved to your execution summary and used to download the report at a later point
	          String reportURL = reportiumClient.getReportUrl();

	          // For documentation on how to export reporting PDF, see https://github.com/perfectocode/samples/wiki/reporting
	          // String reportPdfUrl = (String)(driver.getCapabilities().getCapability("reportPdfUrl"));

	          driver.close();
	          System.out.println("Report: "+ reportURL);


	          // In case you want to download the report or the report attachments, do it here.
	          // PerfectoLabUtils.downloadAttachment(driver, "video", "C:\\test\\report\\video", "flv");
	          // PerfectoLabUtils.downloadAttachment(driver, "image", "C:\\test\\report\\images", "jpg");

	      } catch (Exception e) {
	          e.printStackTrace();
	      }

	      driver.quit();
	  }

	  private static void setDictionary(RemoteWebDriver driver, ArrayList<String> sentences, ArrayList<String> expectedVocalResponsesGoogle, ArrayList<String> expectedVocalResponsesSiri, ArrayList<String> expectedVisualResponsesGoogle, ArrayList<String> expectedVisualResponsesSiri){
		  String fileName = "";
		  sentences.add("hello");
		  expectedVocalResponsesGoogle.add("can I");
		  expectedVisualResponsesGoogle.add("can I");
		  expectedVocalResponsesSiri.add("hey");
		  expectedVisualResponsesSiri.add("hey");
		  sentences.add("what is the time now");
		  expectedVocalResponsesGoogle.add("the time");
		  expectedVisualResponsesGoogle.add("Wakefield");
		  expectedVocalResponsesSiri.add("good morning");
		  expectedVisualResponsesSiri.add("Wakefield");
		  sentences.add("find a bank of america branch near lexington massachussets please");
		  expectedVocalResponsesGoogle.add("address for bank of America");
		  expectedVisualResponsesGoogle.add("1761");
		  expectedVocalResponsesSiri.add("what I found");
		  expectedVisualResponsesSiri.add("1761");
	  
		  // Siri is likely to loose context here..
		  sentences.add("what are the opening hours tomorrow");
		  expectedVocalResponsesGoogle.add("open tomorrow from");
		  expectedVisualResponsesGoogle.add("hours");
		  expectedVocalResponsesSiri.add("");
		  expectedVisualResponsesSiri.add("hours");
		  sentences.add("show me the phone number for that branch");
		  expectedVocalResponsesGoogle.add("phone numbers for bank of america");
		  expectedVisualResponsesGoogle.add("781");
		  expectedVocalResponsesSiri.add("phone numbers for bank of america");
		  expectedVisualResponsesSiri.add("781");
		  // prep and upload the audio prompts
		  for (String s:sentences){
			  fileName = s.toLowerCase().replace(' ', '_')+".wav";
			  PerfectoUtils.textToSpeech(driver, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+fileName, s);
		  }
	  }
	  private static void testChatbotFlowViaT2S(RemoteWebDriver driver, ArrayList<String> sentences, ArrayList<String> expectedVocalResponses, ArrayList<String> expectedVisualResponses) throws InterruptedException, IOException{
		  ArrayList<String> recordings = new ArrayList<String>();
		  int index = 0;
		  String fileName = "",
				  expectedVisualResponse = "",
				  expectedVocalResponse = "";
		  boolean isAndroid = true;
		  if (!PerfectoUtils.getDeviceInfo(driver, PerfectoUtils.DeviceInfo.os).toLowerCase().contains("android"))
			  isAndroid = false;

		  try {
			// Wake up google assistant/Siri
			  PerfectoUtils.home(driver);  
			  if (isAndroid)
				  PerfectoUtils.pressKey(driver, "HOME:5000");		  
			  else {
				  PerfectoUtils.ocrImageSelect(driver, "PUBLIC:Amir/iOSAccessibilityIcon.png");
				  PerfectoUtils.ocrTextCheck(driver, "Siri", 95, 20);
				  PerfectoUtils.ocrTextClick(driver, "Siri", 95, 20);
			  }
			  Thread.sleep(1000);

			  for (String s:sentences){
				  expectedVisualResponse = expectedVisualResponses.get(index);
				  fileName = s.toLowerCase().replace(' ', '_')+".wav";//+"_noise.wav";
				  PerfectoUtils.injectAudio(driver, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+fileName);	  
				  recordings.add(listen(driver, true, expectedVisualResponse, 15));

				  index = index +1;
				  if (index < sentences.size())
					  if (isAndroid){
						  AppiumTest.switchToContext(driver, "NATIVE_APP");
						  driver.findElementByXPath("//*[@resource-id=\"com.google.android.googlequicksearchbox:id/logo_view\"]").click();
					  }else{
						  try {
							PerfectoUtils.ocrImageSelect(driver, "PUBLIC:Amir/iOSSiriMicrophone.png");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					  }
			  }
			  index = 0;
			  Thread.sleep(10000);
			  for (String s:recordings) {
				  try {
					  expectedVocalResponse = expectedVocalResponses.get(index);
					  String temp = PerfectoUtils.speechToText(driver, s);
					  System.out.println("decoded text: " + temp);
					  PerfectoUtils.speechToTextValidate(driver, s, expectedVocalResponse);
					  if (null != temp && null != expectedVocalResponse){
                          System.out.println("vocal response string: " + temp );
                          System.out.println("expected vocal string: " + expectedVocalResponse );
                          if (temp.toLowerCase().contains(expectedVocalResponse.toLowerCase()))
                              System.out.println("MATCH!!! ");

                      }
				  } catch (Exception e) {
					  e.printStackTrace();
				  }
				  index = index +1;
			  }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  }
	private static String listen(RemoteWebDriver driver, boolean waitForVisualValidation, String visualValidation, int seconds) throws IOException, InterruptedException{

		String response = "";
		try {
			String audioFileRecording = PerfectoUtils.startAudioRecording(driver);
			System.out.println("Audio recording: " + audioFileRecording);
            if (waitForVisualValidation) try{
                PerfectoUtils.ocrTextCheck(driver, visualValidation, 90, 20);
                long UXtimer = PerfectoUtils.getUXTimer(driver);
                System.out.println("Expected visual response: "+ visualValidation + " Took: " + UXtimer+ " milliseconds");
            } catch (Exception e) { System.out.println("OCR failed looking for: " + visualValidation);}
            else
                Thread.sleep(seconds * 1000);
			PerfectoUtils.stopAudioRecording(driver);
			return audioFileRecording;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;

	}

}
