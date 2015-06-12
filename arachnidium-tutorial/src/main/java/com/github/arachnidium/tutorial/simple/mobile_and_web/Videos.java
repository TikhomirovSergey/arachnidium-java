package com.github.arachnidium.tutorial.simple.mobile_and_web;

import io.appium.java_client.pagefactory.AndroidFindBy;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.github.arachnidium.core.Handle;

public class Videos<S extends Handle> extends HasSearchField<S> {

	protected Videos(S handle) {
		super(handle);
	}

	@FindBy(className = "video_image_div")
	@AndroidFindBy(id = "com.vkontakte.android:id/album_thumb")
	private List<WebElement> videos;
	
	@InteractiveMethod
	public int getVideosCount(){
		return videos.size();
	}
	
	@InteractiveMethod
	public void playVideo(int index){
		videos.get(index).click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
