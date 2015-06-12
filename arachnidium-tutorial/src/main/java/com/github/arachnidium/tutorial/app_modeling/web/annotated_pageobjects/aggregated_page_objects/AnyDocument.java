package com.github.arachnidium.tutorial.app_modeling.web.annotated_pageobjects.aggregated_page_objects;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.github.arachnidium.core.BrowserWindow;
import com.github.arachnidium.model.browser.BrowserPage;
import com.github.arachnidium.model.common.Static;
import com.github.arachnidium.model.support.annotations.ExpectedURL;
import com.github.arachnidium.tutorial.app_modeling.web.annotated_pageobjects.ShareSettingsDialog;
/**<== */

@ExpectedURL(regExp = "//docs.google.com/")  /**<== Possible URLs that should be loaded can be declared by annotations*/
/**Each one @ExpectedURL means one more possible web address*/
/**Declaration is applied to subclasses till they are annotated by @ExpectedURL with 
another values. Also if the class is going to be instantiated by {@link Application#getPart(Class, com.github.arachnidium.core.fluenthandle.IHowToGetHandle)}
(where IHowToGetHandle is a {@link HowToGetPage} instance) then 
the values contained by given strategy will be used instead of declared by annotations*/

/**it is the example which demonstrates how to implement a page object
 * which is supposed to be something generalized. It can be a description of interaction
 * with the page/screen in general*/
public class AnyDocument extends BrowserPage  {/**<-- it is the example which demonstrates component which can be used 
   when only interaction with browser is needed*/

	@Static /**<== This annotation means that it is content which always/very frequently present
	and frequently used*/
	public ShareSettingsDialog shareSettingsDialog; /**It is not necessary to 
	make fields public. You can make them private and implement public getter 
	for each one. I made that just for visibility*/
	
	@FindBy(xpath=".//*[@id='docs-titlebar-share-client-button']/div")
	private WebElement shareButton;
	
	@FindBy(xpath = ".//*[contains(@href,'https://profiles.google.com/')]")
	private WebElement visibleProfile;
	
	protected AnyDocument(BrowserWindow window) {
		super(window);
	}
	
	@InteractiveMethod /**<-- This annotations is useful for methods which simulate
	some interaction. By default the presence of it means that Webdriver should be focused
	on the window/context and switched to the required frame if currently this content is 
	placed inside iframe. All this will have been done before the annotated method
	will be invoked*/
	
	@WithImplicitlyWait(timeOut = 25, /**The presence of @InteractiveMethod activates some useful */
	timeUnit = TimeUnit.SECONDS) /**options like this*/
	public void clickShareButton(){
		shareButton.click();
	}
	
	@InteractiveMethod
	public void clickOnVisibleProfile(){
		visibleProfile.click();
	}
	
	//Common logic of the working with any document in general could be implemented below
	//.........
}
