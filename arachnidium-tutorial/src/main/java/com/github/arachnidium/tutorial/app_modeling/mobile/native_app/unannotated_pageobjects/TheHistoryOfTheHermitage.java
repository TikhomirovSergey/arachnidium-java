package com.github.arachnidium.tutorial.app_modeling.mobile.native_app.unannotated_pageobjects;

import io.appium.java_client.pagefactory.AndroidFindBy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;

import com.github.arachnidium.core.Handle;

public class TheHistoryOfTheHermitage extends InformationAboutTheHermitage{
	
	@FindBy(id = "some_image_id")/**<== Lets imagine that there is the similar 
	browser UI.*/
	@AndroidFindBy(className = "android.widget.Image") /**<==  It will 
	be used by Android*/
	private List<RemoteWebElement> pictures;
	
	@FindBy(id = "some_paragraph_id")
	@AndroidFindBy(className = "android.view.View")
	private List<RemoteWebElement> paragraphs;

	protected TheHistoryOfTheHermitage(Handle handle) {
		super(handle);
	}
	
	@InteractiveMethod /**<-- This annotations is useful for methods which simulate
	some interaction. By default the presence of it means that Webdriver should be focused
	on the window/context and switched to the required frame if currently this content is 
	placed inside iframe. All this will have been done before the annotated method
	will be invoked*/
	@WithImplicitlyWait(timeOut = 40, 
	timeUnit = TimeUnit.SECONDS)  /**The presence of @InteractiveMethod activates 
	some useful options like this*/
	public int getPictiresCount(){
		return pictures.size();
	}
	
	@InteractiveMethod
	@WithImplicitlyWait(timeOut = 40, timeUnit = TimeUnit.SECONDS)
	public int getParagraphCount(){
		return paragraphs.size();
	}
	
	/**Some more interesting things could be implemented below*/
	/**.................................*/
}
