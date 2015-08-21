package com.github.arachnidium.core;

import org.openqa.selenium.By;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;

import com.github.arachnidium.core.components.mobile.Rotator;
import com.github.arachnidium.core.interfaces.IContext;

/**
 * It is the representation of a mobile screen/context.
 */
public class MobileScreen extends Handle implements IContext {

	private final Rotator rotator;

	MobileScreen(String context, ScreenManager manager, By by, 
			HowToGetByFrames howToGetByFramesStrategy) {
		super(context, manager, by, howToGetByFramesStrategy);
		rotator = driverEncapsulation.getComponent(Rotator.class);
	}

	/**
	 * @see org.openqa.selenium.Rotatable#getOrientation()
	 */
	@Override
	public synchronized ScreenOrientation getOrientation() {
		return rotator.getOrientation();
	}

	/**
	 * @see org.openqa.selenium.Rotatable#rotate(org.openqa.selenium.ScreenOrientation)
	 */
	@Override
	public synchronized void rotate(ScreenOrientation orientation) {
		rotator.rotate(orientation);
	}

	@Override
	public WebDriver getWrappedDriver() {
		return driverEncapsulation.getWrappedDriver();
	}
	
	@Override
	public synchronized void switchToMe() {
		String handle = getHandle();
		if (getHandle().contains(MobileContextNamePatterns.NATIVE)){
			nativeManager.switchTo(handle);
			return;
		}
		super.switchToMe();
	}
}
