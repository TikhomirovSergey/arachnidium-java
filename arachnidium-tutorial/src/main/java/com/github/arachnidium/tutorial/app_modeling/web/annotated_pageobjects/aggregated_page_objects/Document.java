package com.github.arachnidium.tutorial.app_modeling.web.annotated_pageobjects.aggregated_page_objects;

import org.openqa.selenium.By;

import com.github.arachnidium.core.BrowserWindow;
import com.github.arachnidium.core.HowToGetPage;
import com.github.arachnidium.model.common.Application;
import com.github.arachnidium.model.common.FunctionalPart;
import com.github.arachnidium.core.HowToGetByFrames;
import com.github.arachnidium.model.support.annotations.ExpectedURL;


@SuppressWarnings("unused")
@ExpectedURL(regExp = "//some/more/url/") /**<== Possible URLs that should be loaded can be declared by annotations*/
@ExpectedURL(regExp = "//docs.google.com/document/") /**Each one @ExpectedURL means one more possible web address*/
/**Declaration is applied to subclasses till they are annotated by @ExpectedURL with 
another values. Also if the class is going to be instantiated by {@link Application#getPart(Class, com.github.arachnidium.core.fluenthandle.IHowToGetHandle)}
(where IHowToGetHandle is a {@link HowToGetPage} instance) then 
the values contained by given strategy will be used instead of declared by annotations*/


/**it is the example which demonstrates how to implement a page object
 * which is supposed to be something generalized. It can be a description of interaction
 * with the page/screen in general*/
public class Document extends AnyDocument {

	protected Document(BrowserWindow window) {
		super(window);
	}
	
	//Specific actions which perform the interaction with the text editor could be implemented here
	//.......

}
