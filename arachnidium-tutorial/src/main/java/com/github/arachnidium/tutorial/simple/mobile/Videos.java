package com.github.arachnidium.tutorial.simple.mobile;

import io.appium.java_client.pagefactory.AndroidFindBy;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.github.arachnidium.core.Handle;

public class Videos<S extends Handle> extends HasSearchField<S> {

	protected Videos(S handle) {
		super(handle);
	}

	@AndroidFindBy(id = "com.vkontakte.android:id/album_thumb")
	private List<WebElement> videos;
	
	@InteractiveMethod
	public int getVideosCount(){
		return videos.size();
	}
	
	@InteractiveMethod
	public void playVideo(int index){
		videos.get(index).click();
	}

}
