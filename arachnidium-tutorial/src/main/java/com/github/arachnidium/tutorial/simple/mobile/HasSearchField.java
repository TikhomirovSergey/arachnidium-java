package com.github.arachnidium.tutorial.simple.mobile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.github.arachnidium.core.Handle;
import com.github.arachnidium.model.common.FunctionalPart;
import com.github.arachnidium.core.HowToGetByFrames;

//
 // Object oriented programming is appreciated. So we can to define
 // default behavior and extend it further.
 // 
 // For example. Each opened section (Video, Music, Friend Line and so on)
 // has an ability to perform search. So this ability we declare once and extend it
 // in subclasses.
 //
public abstract class HasSearchField<S extends Handle> extends FunctionalPart<S> {

	@FindBy(id = "android:id/search_button")
	private WebElement searchButton;
	@FindBy(id = "android:id/search_src_text")
	private WebElement searchText;
	
	//
	// This constructor should present 
	// when an instance of the class is going to
	// be got from another.
	//
	// This instantiation means that described specific UI or the fragment is 
	// on the same window and inside the same frame  as the more generalized "parent".
	//
	// 
	// As for this example. When user is logged in he/she able no choose sections e.g Music,
	// Videos, Friend Line. We can describe all possible interactions by one {@link FunctionalPart}
	// subclass. I don't like god objects. So we can decompose these interactions (logically, for example)
	//
	// He/she can take Music, Videos, Friend Line as parts of his/her VK page which appear when he/she
	// chooses appropriate section. So we can model it.
	// 
	//@example
	//If this constructor is present then instance can got by this method (for example)
	//
	//someUIDescriptionInstance.getPart(someUIDescription.class)
	//
	//someUIDescription.class should have this constructor
	//Special things will be described in another
	// chapters.
	//
	//@param parent is considered as a more general UI or the part of client UI
	 /**
	 *@see 
	 *{@link Videos}
	 *{@link UserScreen}
	 */	
	protected HasSearchField(FunctionalPart<?> parent, HowToGetByFrames path, By by) {
		super(parent, path, by);
	}
	
	@InteractiveMethod
	public void clickSearchButton(){
		searchButton.click();
	}
	
	@InteractiveMethod
	public void enterSearchString(String searchString){
		searchText.sendKeys(searchString);
	}

}
