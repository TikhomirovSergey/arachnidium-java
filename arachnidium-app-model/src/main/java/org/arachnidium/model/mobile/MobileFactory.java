package org.arachnidium.model.mobile;

import io.appium.java_client.remote.MobileCapabilityType;

import java.net.URL;
import java.util.ArrayList;

import org.arachnidium.core.ScreenManager;
import org.arachnidium.core.settings.AlertIsPresentTimeOut;
import org.arachnidium.core.settings.CapabilitySettings;
import org.arachnidium.core.settings.ChromeDriverServerBin;
import org.arachnidium.core.settings.HandleWaitingTimeOut;
import org.arachnidium.core.settings.IEDriverServerBin;
import org.arachnidium.core.settings.PhantomJSDriverBin;
import org.arachnidium.core.settings.ScreenShots;
import org.arachnidium.core.settings.WebDriverSettings;
import org.arachnidium.core.settings.WebDriverTimeOuts;
import org.arachnidium.core.settings.supported.ESupportedDrivers;
import org.arachnidium.model.common.Application;
import org.arachnidium.model.common.DefaultApplicationFactory;
import org.arachnidium.util.configuration.Configuration;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;


public final class MobileFactory extends DefaultApplicationFactory {
	private static MobileFactory MOBILE_FACTORY_OBJECT = new MobileFactory();
	
	/**
	 * Common method that creates an instance of a mobile application using
	 * default configuration It is important: URL to remote server and
	 * capabilities should be defined in default configuration
	 * 
	 * <b>Attention, please!</b> 
	 * {@link ESupportedDrivers#ANDROID_APP} or {@link ESupportedDrivers#IOS_APP} should
	 * be defined in the {@link Configuration#byDefault}.<br/><br/> {@link Capabilities} should be 
	 * defined.  Required capabilities:<br/>
	 * - {@link MobileCapabilityType#APP}<br/>
	 * - {@link MobileCapabilityType#PLATFORM_VERSION} - for {@link ESupportedDrivers#IOS_APP}<br/>
	 * - {@link MobileCapabilityType#DEVICE_NAME}<br/>   
	 * 
	 * @see 
	 * {@link Configuration}<br/>
	 * {@link WebDriverSettings}<br/>
	 * {@link CapabilitySettings}<br/>
	 * {@link WebDriverTimeOuts}<br/>
	 * {@link HandleWaitingTimeOut}<br/>
	 * {@link AlertIsPresentTimeOut}<br/>
	 * {@link ChromeDriverServerBin}<br/>
	 * {@link IEDriverServerBin}<br/>
	 * {@link PhantomJSDriverBin}<br/>
	 * {@link ScreenShots}<br/>	 
	 */
	public static <T extends Application<?, ?>> T getApplication(
			Class<T> appClass) {
		return getApplication(appClass, Configuration.byDefault);
	}

	/**
	 * Common method that creates an instance of a mobile application using
	 * defined address of remote server and capabilities.
	 * 
	 * Available supported {@link WebDriver} designations:<br/>
	 * - {@link ESupportedDrivers#ANDROID_APP}<br/>
	 * - {@link ESupportedDrivers#IOS_APP}<br/>
	 * <br/><br/>
	 * These capabilities should be defined:<br/>
	 * - {@link MobileCapabilityType#APP}<br/>
	 * - {@link MobileCapabilityType#PLATFORM_VERSION} - for {@link ESupportedDrivers#IOS_APP}<br/>
	 * - {@link MobileCapabilityType#DEVICE_NAME}<br/> 
	 * 
	 */
	public static <T extends Application<?, ?>> T getApplication(
			ESupportedDrivers supportedDriver,
			Class<T> appClass, Capabilities capabilities, URL remoteAddress) {
		return getApplication(ScreenManager.class, appClass,
				supportedDriver, capabilities, remoteAddress,
				new MobileApplicationInterceptor(), 
				MOBILE_FACTORY_OBJECT);
	}

	/**
	 * Common method that creates an instance of a mobile application using
	 * defined configuration It is important: URL to remote server and
	 * capabilities should be defined in configuration<br/>
	 * <br/>
	 * <br/>
	 * <b>Attention, please!</b> 
	 * {@link ESupportedDrivers#ANDROID_APP} or {@link ESupportedDrivers#IOS_APP} should
	 * be defined in the given {@link Configuration}.<br/><br/>  {@link Capabilities} should be 
	 * defined.  Required capabilities:<br/>
	 * - {@link MobileCapabilityType#APP}<br/>
	 * - {@link MobileCapabilityType#PLATFORM_VERSION} - for {@link ESupportedDrivers#IOS_APP}<br/>
	 * - {@link MobileCapabilityType#DEVICE_NAME}<br/> 
	 * 
	 * @see 
	 * {@link Configuration}<br/>
	 * {@link WebDriverSettings}<br/>
	 * {@link CapabilitySettings}<br/>
	 * {@link WebDriverTimeOuts}<br/>
	 * {@link HandleWaitingTimeOut}<br/>
	 * {@link AlertIsPresentTimeOut}<br/>
	 * {@link ChromeDriverServerBin}<br/>
	 * {@link IEDriverServerBin}<br/>
	 * {@link PhantomJSDriverBin}<br/>
	 * {@link ScreenShots}<br/>	 
	 */
	public static <T extends Application<?, ?>> T getApplication(
			Class<T> appClass, Configuration configuration) {
		T result = getApplication(ScreenManager.class, appClass,
				configuration, new MobileApplicationInterceptor(), 
				MOBILE_FACTORY_OBJECT);
		return result;
	}

	private MobileFactory() {
		super(new ArrayList<ESupportedDrivers>(){
			private static final long serialVersionUID = 1L;
			{
				add(ESupportedDrivers.ANDROID_APP);
				add(ESupportedDrivers.IOS_APP);
			}
		});
	}

}
