package com.github.arachnidium.web.googledrive;

import org.openqa.selenium.By;

import com.github.arachnidium.core.Handle;
import com.github.arachnidium.core.HowToGetByFrames;

/**
 * Uses annotations of superclass
 */
public class Document<T extends Handle> extends AnyDocument<T> {

	protected Document(T handle, HowToGetByFrames path, By by) {
		super(handle, path, by);
	}

}
