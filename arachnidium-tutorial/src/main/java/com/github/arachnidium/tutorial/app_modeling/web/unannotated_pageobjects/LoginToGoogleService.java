package com.github.arachnidium.tutorial.app_modeling.web.unannotated_pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.github.arachnidium.core.Handle;
import com.github.arachnidium.model.common.FunctionalPart;
import com.github.arachnidium.core.HowToGetByFrames;


/**it is the example which demonstrates how to implement a page object
 * which is supposed to be something generalized. It can be a description of interaction
 * with the page/screen in general*/
public class LoginToGoogleService<T extends Handle> extends FunctionalPart<T> {/**<-- it is the example which demonstrates 
       component which can be used 
	   when interaction with browser and native/webview content is needed*/
	
	@FindBy(name = "Email")
	private WebElement eMail;	
	@FindBy(id="Passwd")
	private WebElement password;
	@FindBy(name="PersistentCookie")
	private WebElement persistentCookie;
	@FindBy(name="signIn")
	private WebElement singIn;
	
	/**
	 * If it is implemented as something general
	 * (general page/screen description) then it 
	 * should have constructor like this:
	 * 
	 * {@link FunctionalPart##FunctionalPart(Handle, com.github.arachnidium.model.support.HowToGetByFrames, org.openqa.selenium.By)}
	 */
	protected LoginToGoogleService(T handle, HowToGetByFrames path, By by) {/**<-- it is the example which demonstrates 
	       component which can be used 
		   when interaction with browser and native/webview content is needed*/
		super(handle, path, by);
	}
	
	@InteractiveMethod /**<-- This annotations is useful for methods which simulate
	some interaction. By default the presence of it means that Webdriver should be focused
	on the window/context and switched to the required frame if currently this content is 
	placed inside iframe. All this will have been done before the annotated method
	will be invoked*/
	public void clickOnPersistentCookie(){
		persistentCookie.click();
	}
	
	@InteractiveMethod
	public void setEmail(String eMail){
		this.eMail.sendKeys(eMail);
	}
	
	@InteractiveMethod
	public void setPassword(String password){
		this.password.sendKeys(password);
	}
	
	@InteractiveMethod
	public void singIn(){
		singIn.submit();
	}

}
