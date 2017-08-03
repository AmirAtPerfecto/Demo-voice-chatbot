package perfecto;


import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

//import org.openqa.selenium.chrome.ChromeOptions;

public class Utils {

	// Appium
		public static RemoteWebDriver getRemoteWebDriver(String platformName, String platformVersion, String manufacturer,
				String model, String deviceName, String appID) throws IOException {
	        System.out.println("Run started");

	        String browserName = "mobileOS";
	        DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		    
			// Set cloud host and credentials values from CI, else use local values
			String PERFECTO_HOST = System.getenv().get("PERFECTO_CLOUD");
			String PERFECTO_SECURITY_TOKEN = System.getenv().get("PERFECTO_CLOUD_SECURITY_TOKEN");
			
			String host = PERFECTO_HOST;
			capabilities.setCapability("securityToken", PERFECTO_SECURITY_TOKEN);
	        //TODO: Change your device ID
	        capabilities.setCapability("platformName", platformName);
	        capabilities.setCapability("platformVersion", platformVersion);
	        capabilities.setCapability("manufacturer", manufacturer);
	        capabilities.setCapability("model", model);
//	        if (null != appType)
//	        	capabilities.setCapability(appType, appID); //app id
//	        capabilities.setCapability("winAppId", "Microsoft.MicrosoftEdge_8wekyb3d8bbwe!App"); //app id
	        capabilities.setCapability("deviceName", deviceName);
	        // Use the automationName capability to define the required framework - Appium (this is the default) or PerfectoMobile.
	        capabilities.setCapability("automationName", "Appium");
	        //TODO: Audio setup
	        capabilities.setCapability("audioPlayback", true);
	        // Call this method if you want the script to share the devices with the Perfecto Lab plugin.
	        PerfectoLabUtils.setExecutionIdCapability(capabilities, host);

	        // Application settings examples.
	        // capabilities.setCapability("app", "PRIVATE:applications/Errands.ipa");
	        // For Android:
	        // capabilities.setCapability("appPackage", "com.google.android.keep");
	        // capabilities.setCapability("appActivity", ".activities.BrowseActivity");
	        // For iOS:
	         //capabilities.setCapability("bundleId", appID);

	        // Add a persona to your script (see https://community.perfectomobile.com/posts/1048047-available-personas)
	        //capabilities.setCapability(main.WindTunnelUtils.WIND_TUNNEL_PERSONA_CAPABILITY, main.WindTunnelUtils.GEORGIA);

	        // Name your script
	        // capabilities.setCapability("scriptName", "AppiumTest");

	        //AndroidDriver driver = new AndroidDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
//	         IOSDriver driver = new IOSDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
	        RemoteWebDriver driver = new RemoteWebDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
	        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

			return driver;

		}

		
// Selenium
	public static RemoteWebDriver getRemoteWebDriver(String platformName, String platformVersion, String browserName,
			String browserVersion, String screenResolution) throws MalformedURLException {
	    
		// Set cloud host and credentials values from CI, else use local values
		String PERFECTO_HOST = System.getenv().get("PERFECTO_CLOUD");
//		String PERFECTO_USER = System.getenv().get("PERFECTO_CLOUD_USERNAME");
//		String PERFECTO_PASSWORD = System.getenv().get("PERFECTO_CLOUD_PASSWORD");
		String PERFECTO_SECURITY_TOKEN = System.getenv().get("PERFECTO_CLOUD_SECURITY_TOKEN");
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		//capabilities.setCapability("user", PERFECTO_USER);
		//capabilities.setCapability("password", PERFECTO_PASSWORD);
		capabilities.setCapability("securityToken", PERFECTO_SECURITY_TOKEN);
		capabilities.setCapability("platformName", platformName);
		capabilities.setCapability("platformVersion", platformVersion);
		capabilities.setCapability("browserName", browserName);
		capabilities.setCapability("browserVersion", browserVersion);
// Invoke the WAVE toolbar on browser launch
//		ChromeOptions options = new ChromeOptions();
//        options.addArguments("load-extension=C:\\Users\\perfecto\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\jbbplnpkjmmeebjpijfedlgcdilocofh\\1.0.1_0");
//        capabilities.setCapability(ChromeOptions.CAPABILITY, options);


		if (!screenResolution.isEmpty()) {
			capabilities.setCapability("resolution", screenResolution);
			System.out.println("Creating Remote WebDriver on: " + platformName + " " + platformVersion + ", " + browserName + " " + browserVersion + ", " + screenResolution);
		}
		else {
			if (!platformName.isEmpty())
				System.out.println("Creating Remote WebDriver on: " + platformName + " " + platformVersion);
			else
				System.out.println("Creating Remote WebDriver on: " + browserName);
		}

		RemoteWebDriver webdriver = new RemoteWebDriver(
				new URL("https://" + PERFECTO_HOST + "/nexperience/perfectomobile/wd/hub"), capabilities);

		// Define RemoteWebDriver timeouts
		webdriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		//webdriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

		// Maximize browser window on Desktop
		if (!screenResolution.isEmpty()) {
		//	webdriver.manage().window().maximize();
		}

		return webdriver;
	}
}
