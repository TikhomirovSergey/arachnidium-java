package com.github.arachnidium.core;

import java.util.Set;
import java.util.logging.Level;

import com.github.arachnidium.util.logging.Log;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import com.github.arachnidium.core.interfaces.IDestroyable;
import com.github.arachnidium.core.interfaces.IHasHandle;
import com.github.arachnidium.core.interfaces.ISwitchesToItself;
import com.github.arachnidium.core.interfaces.ITakesPictureOfItSelf;

/**z
 * Represents objects that have handles e.g.
 * browser window and mobile context/screen
 */
public abstract class Handle implements IHasHandle, ISwitchesToItself,
ITakesPictureOfItSelf, IDestroyable {

	static IHasHandle isInitiated(String handle, Manager<?,?> manager) {
		return manager.getHandleReceptionist().isInstantiated(handle);
	}

	final String handle;
	public final WebDriverEncapsulation driverEncapsulation;
	public final Manager<?,?> nativeManager;
	final By by;
	final HowToGetByFrames howToGetByFramesStrategy;

	private final HandleReceptionist receptionist;

	Handle(String handle, Manager<?,?> manager, 
			By by, HowToGetByFrames howToGetByFramesStrategy) {
		this.nativeManager = manager;
		this.driverEncapsulation = manager.getWebDriverEncapsulation();
		this.handle = handle;
		this.receptionist = nativeManager.getHandleReceptionist();
		this.by = by;
		this.howToGetByFramesStrategy = howToGetByFramesStrategy;
	}

	@Override
	public void destroy() {
		receptionist.remove(this);
	}

	/**
	 * @return flag of the handle existing
	 */
	public synchronized boolean exists() {
		if (!nativeManager.isAlive())
			return false;
		try {
			Set<String> handles = nativeManager.getHandles();
			return handles.contains(handle);
		} catch (WebDriverException e) { // if there is no handle
			return false;
		}
	}

	/**
	 * @return Window string handle/mobile context name
	 * 
	 * @see com.github.arachnidium.core.interfaces.IHasHandle#getHandle()
	 */
	@Override
	public String getHandle() {
		return handle;
	}

	/**
	 * Sets focus to itself
	 */
	@Override
	public synchronized void switchToMe() {
		nativeManager.switchTo(handle);
	}

	/**
	 * Takes a picture of itself.
	 * It creates FINE {@link Level} {@link Log} message with 
	 * attached picture (optionally)
	 */
	@Override
	public synchronized void takeAPictureOfAFine(String comment) {
		nativeManager.takeAPictureOfAFine(handle, comment);
	}

	/**
	 * Takes a picture of itself.
	 * It creates INFO {@link Level} {@link Log} message with 
	 * attached picture (optionally)
	 */	
	@Override
	public synchronized void takeAPictureOfAnInfo(String comment) {
		nativeManager.takeAPictureOfAnInfo(handle, comment);
	}

	/**
	 * Takes a picture of itself.
	 * It creates SEVERE {@link Level} {@link Log} message with 
	 * attached picture (optionally)
	 */		
	@Override
	public synchronized void takeAPictureOfASevere(String comment) {
		nativeManager.takeAPictureOfASevere(handle, comment);
	}

	/**
	 * Takes a picture of itself.
	 * It creates WARN {@link Level} {@link Log} message with 
	 * attached picture (optionally)
	 */		
	@Override
	public synchronized void takeAPictureOfAWarning(String comment) {
		nativeManager.takeAPictureOfAWarning(handle, comment);
	}

}
