package com.github.arachnidium.model.support.annotations.rootelements;

import io.appium.java_client.MobileBy;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;

import com.github.arachnidium.core.settings.supported.ESupportedDrivers;
import com.github.arachnidium.model.support.ByNumbered;
import com.github.arachnidium.model.support.annotations.ClassDeclarationReader;

public class ElementReaderForMobilePlatforms implements IRootElementReader {
	private static final String UI_AUTOMATOR = "uiAutomator";
	private static final String ACCESSIBILITY = "accessibility";
	private static final String CLASSNAME = "className";
	private static final String ID =  "id";
	private static final String TAG_NAME = "tagName";
	private static final String NAME = "name";
	private static final String XPATH = "xpath";
	private static final String LINK_TEXT = "linkText";
	private static final String PARTIAL_LINK_TEXT = "partialLinkText";
	
	private static final String CHAIN = "chain";
	
	private static Class<?>[] emptyParams = new Class[]{};
	private static Object[] emptyValues = new Object[]{};
	
	@SuppressWarnings("unchecked")
	private static <T> T getValueFromAnnotation(Annotation annotation, String methodName){
		try {
			Method m = annotation.getClass().getDeclaredMethod(methodName, emptyParams);
			return (T) m.invoke(annotation, emptyValues);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static By getBy(Annotation annotation, ESupportedDrivers supportedDriver){
		String value = getValueFromAnnotation(annotation, ACCESSIBILITY);
		if (value != null && !"".equals(value)){
			return MobileBy.AccessibilityId(value);
		}
		
		value = getValueFromAnnotation(annotation, CLASSNAME);
		if (value != null && !"".equals(value)){
			return MobileBy.className(value);
		}
		
		value = getValueFromAnnotation(annotation, ID);
		if (value != null && !"".equals(value)){
			return MobileBy.id(value);
		}
		
		value = getValueFromAnnotation(annotation, TAG_NAME);
		if (value != null && !"".equals(value)){
			return MobileBy.tagName(value);
		}
		
		value = getValueFromAnnotation(annotation, NAME);
		if (value != null && !"".equals(value)){
			return MobileBy.name(value);
		}
		
		value = getValueFromAnnotation(annotation, XPATH);
		if (!"".equals(value)){
			return MobileBy.xpath(value);
		}	
		
		value = getValueFromAnnotation(annotation, UI_AUTOMATOR);
		if (value != null && !"".equals(value)){
			if (supportedDriver.equals(ESupportedDrivers.ANDROID_APP)){
				return MobileBy.AndroidUIAutomator(value);
			}
			return MobileBy.IosUIAutomation(value);
		}	
		
		value = getValueFromAnnotation(annotation, LINK_TEXT);
		if (value != null && !"".equals(value)){
			return MobileBy.linkText(value);
		}	
		
		value = getValueFromAnnotation(annotation, PARTIAL_LINK_TEXT);
		if (value != null && !"".equals(value)){
			return MobileBy.partialLinkText(value);
		}	
		
		throw new IllegalArgumentException("No one known locator strategy was defined!");
	}
	
	private static By getPossibleChain(Annotation annotation, ESupportedDrivers supportedDriver){
		List<By> result = new ArrayList<>();		
		Annotation[] bies = getValueFromAnnotation(annotation, CHAIN);
		
		for (Annotation chainElement: bies) {
			By by = getBy(chainElement, supportedDriver);
			result.add(by);
		}
		return new ByNumbered(new ByChained(result.toArray(new By[]{})), ClassDeclarationReader.getIndex(annotation));
	}	

	@Override
	public By readClassAndGetBy(AnnotatedElement annotatedTarget, ESupportedDrivers supportedDriver) {
		List<By> result = new ArrayList<>();
		Annotation[] possibleRoots = null;		
		if (supportedDriver.equals(ESupportedDrivers.ANDROID_APP)){
			possibleRoots = getAnnotations(RootAndroidElement.class, annotatedTarget);
		}
		
		if (supportedDriver.equals(ESupportedDrivers.SELENDROID_APP)){
			possibleRoots = getAnnotations(RootSelendroidElement.class, annotatedTarget);
		}
		
		if (supportedDriver.equals(ESupportedDrivers.IOS_APP)){
			possibleRoots = getAnnotations(RootIOSElement.class, annotatedTarget);
		}
		
		for (Annotation chain: possibleRoots) {
			result.add(getPossibleChain(chain, supportedDriver));
		}	
		
		//this is an attempt to get By strategy
		//by present @FindBy annotations
		if (result.size() == 0)
			return new CommonRootElementReader().readClassAndGetBy(annotatedTarget, supportedDriver);
		return new ByAll(result.toArray(new By[]{}));
	}

}
