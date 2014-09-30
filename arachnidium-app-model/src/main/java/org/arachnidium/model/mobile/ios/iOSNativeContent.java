package org.arachnidium.model.mobile.ios;

import io.appium.java_client.ios.GetsNamedTextField;
import io.appium.java_client.ios.IOSDeviceActionShortcuts;
import io.appium.java_client.ios.IOSDriver;

import org.arachnidium.core.MobileScreen;
import org.arachnidium.model.common.FunctionalPart;
import org.arachnidium.model.mobile.MobileApplication;
import org.arachnidium.model.mobile.NativeContent;
import org.arachnidium.model.support.HowToGetByFrames;
import org.openqa.selenium.WebElement;

/**
 * It is the same as {@link MobileApplication} with some capabilities of
 * iOS. It works only with {@link IOSDriver}
 */
public abstract class iOSNativeContent extends NativeContent implements IOSDeviceActionShortcuts, 
   GetsNamedTextField{
	/**
	 * @see FunctionalPart#FunctionalPart(FunctionalPart)
	 * 
	 * @see NativeContent#Screen(FunctionalPart)
	 */
	protected iOSNativeContent(NativeContent parent) {
		super(parent);
	}

	/**
	 * @see FunctionalPart#FunctionalPart(FunctionalPart, HowToGetByFrames)
	 * 
	 * @see NativeContent#Screen(FunctionalPart, HowToGetByFrames)
	 */	
	protected iOSNativeContent(MobileScreen context) {
		super(context);
	}

	@Override
	@InteractiveMethod
	public void hideKeyboard(String keyName) {
		((IOSDriver) getWrappedDriver()).hideKeyboard(keyName);		
	}

	@Override
	@InteractiveMethod
	public void hideKeyboard(String strategy, String keyName) {
		((IOSDriver) getWrappedDriver()).hideKeyboard(strategy, keyName);		
	}

	@Override
	@InteractiveMethod
	public void shake() {
		((IOSDriver) getWrappedDriver()).shake();		
	}

	@Override
	@InteractiveMethod
	public WebElement getNamedTextField(String name) {
		return ((IOSDriver) getWrappedDriver()).getNamedTextField(name);
	}
}
