package perfecto;

import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public abstract class PerfectoUtils {


	public static void textToSpeech(RemoteWebDriver driver, String targetFileRepositoryKey, String text) {
		Map<String, Object> params = new HashMap<>();

		params.put("repositoryFile", targetFileRepositoryKey);
		params.put("text", text);
		driver.executeScript("mobile:text:audio", params);
	}
    public static void speechToTextValidate(RemoteWebDriver driver, String audioFileURL, String text) {
		if (null == audioFileURL || audioFileURL.length() < 1) return ;
        Map<String, Object> params = new HashMap<>();
        params.put("content", text);
        params.put("deviceAudio", audioFileURL);
        params.put("match", "contain");
        Object result = driver.executeScript("mobile:audio-text:validation", params);
    }

    public static String speechToText(RemoteWebDriver driver, String audioFileURL) {
        Map<String, Object> params = new HashMap<>();

        if (null == audioFileURL || audioFileURL.length() < 1) return null;
        params.put("deviceAudio", audioFileURL);
        Object result = driver.executeScript("mobile:audio:text", params);
        if (null != result)
            return result.toString();
        else
            return null;
    }


    // http://developers.perfectomobile.com/pages/viewpage.action?pageId=13893806
    public static void injectAudio(RemoteWebDriver driver, String repositoryKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", repositoryKey);
        driver.executeScript("mobile:audio:inject", params);
    }
    // http://developers.perfectomobile.com/pages/viewpage.action?pageId=14878149
    public static String startAudioRecording(RemoteWebDriver driver) {
        Map<String, Object> params = new HashMap<>();

        String audioFileURL = (String) driver.executeScript("mobile:audio.recording:start", params);
        return audioFileURL;
    }

    // http://developers.perfectomobile.com/pages/viewpage.action?pageId=14878153
    public static void stopAudioRecording(RemoteWebDriver driver) {
        Map<String, Object> params = new HashMap<>();

        driver.executeScript("mobile:audio.recording:stop", params);

    }

    // presskey http://developers.perfectomobile.com/pages/viewpage.action?pageId=13893814
	// Sequence can be "HOME" or "HOME:5000"
	public static void pressKey(RemoteWebDriver driver, String sequence){
		Map<String, Object> params1 = new HashMap<>();
		params1.put("keySequence", sequence);
		driver.executeScript("mobile:presskey", params1);
	}

	public static enum DeviceInfo {manufacturer , model , phoneNumber , deviceId , resolution ,
		resolutionWidth , resolutionHeight , os , osVersion , firmware , location , network , distributer , language , imsi , nativeImei , wifiMacAddress ,
		cradleId , status , inUse , description , position , method , rotation , locked ,
		currentActivity ,
		currentPackage , all};

	// device info http://developers.perfectomobile.com/pages/viewpage.action?pageId=13893798
	public static String getDeviceInfo(RemoteWebDriver driver, DeviceInfo info){
		Map<String, Object> params = new HashMap<>();
		params.put("property", info.name());
		String response = (String) driver.executeScript("mobile:device:info", params);
		return response;
	}

	public static enum NetworkVirtualizationProfiles {NV_2G_GPRS_Good, NV_2G_GPRS_Average,NV_2G_GPRS_Poor, NV_2G_Edge_Good,
		NV_2G_Edge_Average, NV_2G_Edge_Poor, NV_3G_UMTS_Good, NV_3G_UMTS_Average, NV_3G_UMTS_Poor,
		NV_3_5G_HSPA_Good, NV_3_5G_HSPA_Average, NV_3_5G_HSPA_Poor, NV_3_5G_HSPA_PLUS_Good, NV_3_5G_HSPA_PLUS_Average, NV_3_5G_HSPA_PLUS_Poor,
		NV_4G_LTE_Good, NV_4G_LTE_Average, NV_4G_LTE_Poor, NV_4G_LTE_Advanced_Good, NV_4G_LTE_Advanced_Average, NV_4G_LTE_Advanced_Poor};


	public static void startNetworkVirtualization(RemoteWebDriver driver, NetworkVirtualizationProfiles profile, String recordHARFile) {
		Map<String, Object> params = new HashMap<>();
		if (null != profile)
			params.put("profile", translateNVProfileToName(profile));
		if (null != recordHARFile)
		    params.put("generateHarFile", recordHARFile.toLowerCase());
		driver.executeScript("mobile:vnetwork:start", params);
	}
	public static void updateNetworkVirtualization(RemoteWebDriver driver, NetworkVirtualizationProfiles profile, boolean recordHARFile) {
		Map<String, Object> params = new HashMap<>();
		if (null != profile)
			params.put("profile", translateNVProfileToName(profile));
		params.put("generateHarFile", recordHARFile);
		driver.executeScript("mobile:vnetwork:update", params);
	}

    public static void stopNetworkVirtualization(RemoteWebDriver driver, String collectPCAPFile) {
		Map<String, Object> params = new HashMap<>();
        if (null != collectPCAPFile)
		    params.put("pcapFile",collectPCAPFile.toLowerCase());
		driver.executeScript("mobile:vnetwork:stop", params);
	}

	private static String translateNVProfileToName(NetworkVirtualizationProfiles profile){
		if (null == profile) return "";
		String profileName = profile.name().toLowerCase().substring(3);
		if (profile.name().toLowerCase().contains("3_5"))
			profileName = profileName.replace("3_5", "3.5");
		return profileName;
	}

    public static void vitalsCollect(RemoteWebDriver driver, String source){

        Map<String, Object> params = new HashMap<>();
        List<String> vitals = new ArrayList<>();
        vitals.add("all");
        params.put("vitals",vitals);
        List<String> sources = new ArrayList<>();
        sources.add(source);
        params.put("sources",sources);
        driver.executeScript("mobile:monitor:start", params);

    }
    public static void vitalsStop(RemoteWebDriver driver){

        Map<String, Object> params = new HashMap<>();
        driver.executeScript("mobile:monitor:stop", params);

    }


	public static void generateAndroid2AndroidCall(RemoteWebDriver originatingDevice, RemoteWebDriver recievingDevice) {
		String originatingPhoneNumber, recievingPhoneNumber;
			originatingPhoneNumber = getDeviceInfo(originatingDevice, DeviceInfo.phoneNumber);	
			recievingPhoneNumber = getDeviceInfo(recievingDevice, DeviceInfo.phoneNumber);
			assert(null == originatingPhoneNumber || null == recievingPhoneNumber);
			androidCall(originatingDevice, recievingPhoneNumber);
	        ocrTextCheck(recievingDevice, originatingPhoneNumber.substring(originatingPhoneNumber.length()-4), 90, 30);
	        // accept call
	        acceptAndroidCall(recievingDevice);
	}
	
	public static void disconnectAndroidCall(RemoteWebDriver driver) {
		sendADBCommand(driver, "input keyevent KEYCODE_ENDCALL");
	}
	
	public static void acceptAndroidCall(RemoteWebDriver driver) {
		sendADBCommand(driver,"input keyevent KEYCODE_CALL");
	}
	public static void androidCall(RemoteWebDriver driver, String callTo) {
		sendADBCommand(driver, "am start -a android.intent.action.CALL -d tel:"+callTo);
	}
	
	public static void sendADBCommand(RemoteWebDriver driver, String cmd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("command", cmd);

	    driver.executeScript("mobile:handset:shell", params);
	}

	// Swipe
	public static void swipe(RemoteWebDriver driver, String startX, String startY, String endX, String endY){
		String command = "mobile:touch:swipe";
		Map<String, String> params = new HashMap<>();
		params.put("start", startX+","+startY);
		params.put("end", endX+","+endY);
		
		driver.executeScript(command, params);

	}
	
	// get Screenshot
	public static void screenshot(RemoteWebDriver driver){
		String command = "mobile:screen:image";
		Map<String, String> params = new HashMap<>();
		driver.executeScript(command, params);

	}

	// Set device to home screen
	public static void home(RemoteWebDriver driver){
		String command = "mobile:handset:ready";
		Map<String, String> params = new HashMap<>();
		driver.executeScript(command, params);

	}

	// Start collecting device log
	public static void startDeviceLog(RemoteWebDriver driver){
		String command = "mobile:logs:start";
		Map<String, String> params = new HashMap<>();
		driver.executeScript(command, params);

	}

	// Start collecting device log
	public static void stopDeviceLog(RemoteWebDriver driver){
		Map<String, Object> params = new HashMap<>();
		String command = "mobile:logs:stop";
		driver.executeScript(command, params);
	}

	// Gets the requested timer
	public static long timerGet(RemoteWebDriver driver, String timerType) {
		String command = "mobile:timer:info";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", timerType);
		long result = (long) driver.executeScript(command, params);
		return result;
	}

	//returns ux timer
	public static long getUXTimer(RemoteWebDriver driver) {
		return timerGet(driver, "ux");
	}
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	//Switched driver context
	public static void switchToContext(RemoteWebDriver driver, String context) {
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", context);
		executeMethod.execute(DriverCommand.SWITCH_TO_CONTEXT, params);
	}

	//Gets current context
	public static String getCurrentContextHandle(RemoteWebDriver driver) {
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		String context = (String) executeMethod.execute(
				DriverCommand.GET_CURRENT_CONTEXT_HANDLE, null);
		return context;
	}

	//Get available context
	public static List<String> getContextHandles(RemoteWebDriver driver) {
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		List<String> contexts = (List<String>) executeMethod.execute(
				DriverCommand.GET_CONTEXT_HANDLES, null);
		return contexts;
	}

    //Replace text visually
    public static void ocrTextEdit(RemoteWebDriver driver, String originalText, String replaceText){
        Map<String, Object> params = new HashMap<>();
        params.put("label", originalText);
        params.put("text", replaceText);
        driver.executeScript("mobile:edit-text:set", params);
    }
	//Perform text check ocr function
	public static String ocrTextCheck(RemoteWebDriver driver, String text, int threshold, int timeout) {
		return ocrTextCheck(driver, text, threshold, timeout, false);
	}
	//Perform text check ocr function
	public static String ocrTextCheck(RemoteWebDriver driver, String text, int threshold, int timeout, boolean scroll) {
		// Verify that arrived at the correct page, look for the Header Text
		Map<String, Object> params = new HashMap<>();
		params.put("content", text);
		params.put("measurement", "accurate");
	    params.put("source", "camera");
		params.put("analysis", "automatic");
		if (threshold>0)
			params.put("threshold", Integer.toString(threshold));
		if (false != scroll)
			params.put("scrolling", "scroll");
		else
            params.put("timeout", Integer.toString(timeout));

		return (String) driver.executeScript("mobile:checkpoint:text", params);

	}


	//Performs text click ocr function
	public static String ocrTextClick(RemoteWebDriver driver, String text, int threshold, int timeout) {
		Map<String, Object> params = new HashMap<>();
		params.put("content", text);
		params.put("timeout", Integer.toString(timeout));
		
		if (threshold>0)
			params.put("threshold", Integer.toString(threshold));
		return (String) driver.executeScript("mobile:text:select", params);
	}
    //Performs text click ocr function
    public static String ocrTextClickIndex(RemoteWebDriver driver, String text, int threshold, int timeout, int index) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", text);
        params.put("timeout", Integer.toString(timeout));
        params.put("index", index);
        if (threshold>0)
            params.put("threshold", Integer.toString(threshold));
        return (String) driver.executeScript("mobile:text:select", params);
    }


	//Performs text click ocr function
	public static String ocrTextClick(RemoteWebDriver driver, String text, int threshold, int timeout, int haystackTop, int haystackHeight, int haystackLeft, int haystackWidth) {
		Map<String, Object> params = new HashMap<>();
		params.put("content", text);
		params.put("timeout", Integer.toString(timeout));
		if (haystackTop > -1)
			params.put("screen.top", Integer.toString(haystackTop)+"%");
		else
			params.put("screen.top", "0%");
			
		if (haystackHeight > -1)
			params.put("screen.height", Integer.toString(haystackHeight)+"%");
		else
			params.put("screen.height", "100%");
			
		if (haystackLeft > -1)
			params.put("screen.left", Integer.toString(haystackLeft)+"%");
		else
			params.put("screen.left", "0%");
			
		if (haystackWidth > -1)
			params.put("screen.width", Integer.toString(haystackWidth)+"%");
		else
			params.put("screen.width", "100%");
		
		if (threshold>0)
			params.put("threshold", Integer.toString(threshold));
		return (String) driver.executeScript("mobile:text:select", params);
	}

	
	//Performs image click ocr function
	public static String ocrImageSelect(RemoteWebDriver driver, String img) {
		Map<String, Object> params = new HashMap<>();
		params.put("content", img);
		params.put("screen.top", "0%");
		params.put("screen.height", "100%");
		params.put("screen.left", "0%");
		params.put("screen.width", "100%");
		return (String) driver.executeScript("mobile:image:select", params);
	}
	
	//Performs image click ocr function
		public static String ocrImageCheck(RemoteWebDriver driver, String img, int timeout) {
			Map<String, Object> params = new HashMap<>();
			params.put("content", img);
			params.put("screen.top", "0%");
			params.put("screen.height", "100%");
			params.put("screen.left", "0%");
			params.put("screen.width", "100%");
			params.put("timeout", Integer.toString(timeout));
			return (String) driver.executeScript("mobile:checkpoint:image", params);
		}

	//Launches application
	public static String launchApp(RemoteWebDriver driver, String app) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", app);
		return (String) driver.executeScript("mobile:application:open", params);
	}

	//Launches application
	public void executeShellCommand(RemoteWebDriver driver, String command) {
		Map<String, Object> params = new HashMap<>();
		params.put("command", command);
		driver.executeScript("mobile:handset:shell", params);
	}

	
	//Closes application
	public static String closeApp(RemoteWebDriver driver, String app) {
		String result = "false";
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("name", app);
			result = (String) driver.executeScript("mobile:application:close",
					params);
		} catch (Exception ex) {		
		}
		return result;
	}
	
	//Add a comment
	public static String comment(RemoteWebDriver driver, String comment) {
		Map<String, Object> params = new HashMap<>();
		params.put("text", comment);
		return (String) driver.executeScript("mobile:comment", params);
	}
	


	// checks if element exists
	public static Boolean elementExists(RemoteWebDriver driver,String xPath) {

		try {
			driver.findElementByXPath(xPath);
			return true;
		} catch (Exception ex) {
			return false;
		}

	}

}