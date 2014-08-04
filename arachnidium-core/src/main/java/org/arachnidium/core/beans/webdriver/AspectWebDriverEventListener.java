package org.arachnidium.core.beans.webdriver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.arachnidium.core.WebElementHighLighter;
import org.arachnidium.core.aspect.AbstractAspect;
import org.arachnidium.core.eventlisteners.IWebDriverEventListener;
import org.arachnidium.core.interfaces.IWebElementHighlighter;
import org.arachnidium.util.configuration.interfaces.IConfigurationWrapper;
import org.arachnidium.util.logging.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ContextAware;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.springframework.context.support.AbstractApplicationContext;

@Aspect
class AspectWebDriverEventListener extends AbstractAspect implements
		IWebDriverEventListener {

	private static enum HowToHighLightElement {
		INFO {
			@Override
			void highLight(IWebElementHighlighter highlighter,
					WebDriver driver, WebElement element, String message) {
				highlighter.highlightAsInfo(driver, element, message);
			}
		},
		DEBUG {
			@Override
			void highLight(IWebElementHighlighter highlighter,
					WebDriver driver, WebElement element, String message) {
				highlighter.highlightAsFine(driver, element, message);
			}
		};

		void highLight(IWebElementHighlighter highlighter, WebDriver driver,
				WebElement element, String message) {
			// does nothing
		}
	}

	private static final List<Class<?>> listenable = new ArrayList<Class<?>>(){
		private static final long serialVersionUID = 1L;
		{
			add(WebDriver.class);
			add(WebElement.class);
			add(Navigation.class);
			add(TargetLocator.class);
			add(ContextAware.class);
			add(Alert.class);
			add(List.class);
			add(Options.class);
		}
	};
	
	private static final String EXECUTION_VALUE = "execution(* org.openqa.selenium.WebDriver.*(..) || "
			+ "org.openqa.selenium.WebElement.*(..) || "
			+ "org.openqa.selenium.JavascriptExecutor.*(..) || "
			+ "org.openqa.selenium.WebDriver.Navigation.*(..) || "
			+ "org.openqa.selenium.WebDriver.Options.*(..) || "
			+ "org.openqa.selenium.WebDriver.TargetLocator.*(..) || "
			+ "org.openqa.selenium.ContextAware.*(..) || "
			+ "org.openqa.selenium.Alert.*(..) || "
			+ "java.util.List.*(..))";
	
	private final IConfigurationWrapper configurationWrapper;
	@SupportField
	WebDriver driver;
	private final WebElementHighLighter highLighter = new WebElementHighLighter();
	private final AbstractApplicationContext context;
	
	

	private final List<IWebDriverEventListener> additionalListeners = new ArrayList<IWebDriverEventListener>() {
		private static final long serialVersionUID = 1L;
		{
			// it is filled using SPI
			Iterator<?> providers = ServiceLoader.load(
					IWebDriverEventListener.class).iterator();
			while (providers.hasNext())
				add((IWebDriverEventListener) providers.next());
		}
	};

	private final IWebDriverEventListener proxyListener = (IWebDriverEventListener) Proxy
			.newProxyInstance(IWebDriverEventListener.class.getClassLoader(),
					new Class[] { IWebDriverEventListener.class }, (proxy,
							method, args) -> {
						additionalListeners.forEach((eventListener) -> {
							try {
								method.invoke(eventListener, args);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}

						});
						return null;
					});

	public AspectWebDriverEventListener(final WebDriver driver,
			IConfigurationWrapper configurationWrapper, AbstractApplicationContext context) {
		super(new ArrayList<Object>() {
			private static final long serialVersionUID = 1L;
			{
				add(driver);
			}
		});
		this.configurationWrapper = configurationWrapper;
		this.context = context;
	}

	@Before(EXECUTION_VALUE)
	public void beforeTarget(JoinPoint joinPoint) {
		launchMethod(joinPoint, this, WhenLaunch.BEFORE);
	}
	
	@After(EXECUTION_VALUE)
	public void afterTarget(JoinPoint joinPoint) {
		launchMethod(joinPoint, this, WhenLaunch.AFTER);
	}	
	
	@SuppressWarnings("unchecked")
	private <T> T getListenable(Object object){
		for (Class<?> c : listenable){
			if (!c.isAssignableFrom(object.getClass())){
				continue;
			}
			return (T) c.cast(object);
		}
		return null;
	}
	
	@AfterReturning(
			pointcut = EXECUTION_VALUE,
			returning= "result")
	public void afterReturn(JoinPoint joinPoint, Object result) {	
		Object o = getListenable(result);
		if (o != null) {
			result = context.getBean(WebDriverBeanConfiguration.COMPONENT_BEAN, result);
		}
	}
	
	@AfterThrowing(EXECUTION_VALUE)
	public void afterThrowing(JoinPoint joinPoint, Throwable throwable){
		onException(throwable, driver);
	}	

	@BeforeTarget(targetClass = WebDriver.class, targetMethod = "get")
	@BeforeTarget(targetClass = Navigation.class, targetMethod = "to")
	// url can be an instance of String of URL
	public void beforeNavigateTo(@UseParameter(number = 0) Object url,
			@SupportParam WebDriver driver) {
		beforeNavigateTo(String.valueOf(url), driver);
	}

	@AfterTarget(targetClass = WebDriver.class, targetMethod = "get")
	@AfterTarget(targetClass = Navigation.class, targetMethod = "to")
	// url can be an instance of String of URL
	public void afterNavigateTo(@UseParameter(number = 0) Object url,
			@SupportParam WebDriver driver) {
		afterNavigateTo(String.valueOf(url), driver);
	}
	
	@Override
	public void beforeNavigateTo(String url, WebDriver driver) {
		Log.message("Attempt to navigate to another url. Required url is "
				+ url);
		proxyListener.beforeNavigateTo(url, driver);
	}

	@Override
	public void afterNavigateTo(String url, WebDriver driver) {
		Log.message("Current URL is " + driver.getCurrentUrl());
		proxyListener.afterNavigateTo(url, driver);
	}

	@BeforeTarget(targetClass = Navigation.class, targetMethod = "back")
	@Override
	public void beforeNavigateBack(@SupportParam WebDriver driver) {
		Log.message("Attempt to navigate to previous url. Current url is "
				+ driver.getCurrentUrl());
		proxyListener.beforeNavigateBack(driver);
	}

	@AfterTarget(targetClass = Navigation.class, targetMethod = "back")
	@Override
	public void afterNavigateBack(@SupportParam WebDriver driver) {
		Log.message("Current URL is  " + driver.getCurrentUrl());
		proxyListener.afterNavigateBack(driver);

	}

	@BeforeTarget(targetClass = Navigation.class, targetMethod = "forward")
	@Override
	public void beforeNavigateForward(@SupportParam WebDriver driver) {
		Log.message("Attempt to navigate to next url. Current url is "
				+ driver.getCurrentUrl());
		proxyListener.beforeNavigateForward(driver);
	}

	@AfterTarget(targetClass = Navigation.class, targetMethod = "forward")
	@Override
	public void afterNavigateForward(@SupportParam WebDriver driver) {
		Log.message("Current URL is  " + driver.getCurrentUrl());
		proxyListener.afterNavigateForward(driver);
	}

	@BeforeTarget(targetClass = WebDriver.class, targetMethod = "findElement")
	@BeforeTarget(targetClass = WebDriver.class, targetMethod = "findElements")
	@BeforeTarget(targetClass = WebElement.class, targetMethod = "findElement")
	@BeforeTarget(targetClass = WebElement.class, targetMethod = "findElements")
	@Override
	public void beforeFindBy(@UseParameter(number = 0) By by,
			@TargetParam WebElement element,
			@TargetParam @SupportParam WebDriver driver) {
		Log.debug("Searching for element by locator " + by.toString()
				+ " has been started");
		if (element != null) {
			highlightElementAndLogAction(element, "Using root element",
					HowToHighLightElement.DEBUG);
		}
		proxyListener.beforeFindBy(by, element, driver);
	}

	@AfterTarget(targetClass = WebDriver.class, targetMethod = "findElement")
	@AfterTarget(targetClass = WebDriver.class, targetMethod = "findElements")
	@AfterTarget(targetClass = WebElement.class, targetMethod = "findElement")
	@AfterTarget(targetClass = WebElement.class, targetMethod = "findElements")
	@Override
	public void afterFindBy(@UseParameter(number = 0) By by,
			@TargetParam WebElement element,
			@TargetParam @SupportParam WebDriver driver) {
		Log.debug("Searching for web element has been finished. Locator is "
				+ by.toString());
		if (element != null) {
			highlightElementAndLogAction(element, "Root element was used",
					HowToHighLightElement.DEBUG);
		}
		proxyListener.afterFindBy(by, element, driver);
	}

	@BeforeTarget(targetClass = WebElement.class, targetMethod = "click")
	@Override
	public void beforeClickOn(@TargetParam WebElement element,
			@SupportParam WebDriver driver) {
		highlightElementAndLogAction(element,
				"State before element will be clicked on.",
				HowToHighLightElement.INFO);
		proxyListener.beforeClickOn(element, driver);
	}

	@AfterTarget(targetClass = WebElement.class, targetMethod = "click")
	@Override
	public void afterClickOn(@TargetParam WebElement element,
			@SupportParam WebDriver driver) {
		Log.message("Click on element has been successfully performed!");
		proxyListener.afterClickOn(element, driver);
	}

	@BeforeTarget(targetClass = WebElement.class, targetMethod = "sendKeys")
	@BeforeTarget(targetClass = WebElement.class, targetMethod = "clear")
	@BeforeTarget(targetClass = MobileElement.class, targetMethod = "setValue")
	@Override
	public void beforeChangeValueOf(@TargetParam WebElement element,
			@SupportParam WebDriver driver) {
		highlightElementAndLogAction(element,
				"State before element value will be changed.",
				HowToHighLightElement.INFO);
		proxyListener.beforeChangeValueOf(element, driver);
	}

	@AfterTarget(targetClass = WebElement.class, targetMethod = "sendKeys")
	@AfterTarget(targetClass = WebElement.class, targetMethod = "clear")
	@AfterTarget(targetClass = MobileElement.class, targetMethod = "setValue")
	@Override
	public void afterChangeValueOf(@TargetParam WebElement element,
			@SupportParam WebDriver driver) {
		highlightElementAndLogAction(element,
				"State after element value was changed.",
				HowToHighLightElement.INFO);
		proxyListener.afterChangeValueOf(element, driver);
	}

	@BeforeTarget(targetClass = JavascriptExecutor.class, targetMethod = "executeAsyncScript")
	@BeforeTarget(targetClass = JavascriptExecutor.class, targetMethod = "executeScript")
	@Override
	public void beforeScript(@UseParameter(number = 0) String script,
			@SupportParam WebDriver driver) {
		Log.debug("Javascript execution has been started " + script);
		proxyListener.beforeScript(script, driver);
	}

	@AfterTarget(targetClass = JavascriptExecutor.class, targetMethod = "executeAsyncScript")
	@AfterTarget(targetClass = JavascriptExecutor.class, targetMethod = "executeScript")
	@Override
	public void afterScript(@UseParameter(number = 0) String script,
			@SupportParam WebDriver driver) {
		Log.debug("Javascript  " + script + " has been executed successfully!");
		proxyListener.afterScript(script, driver);
	}

	@Override
	public void onException(Throwable throwable, WebDriver driver) {
		Log.debug("An exception has been caught out."
				+ throwable.getClass().getName() + ":" + throwable.getMessage());
		proxyListener.onException(throwable, driver);
	}

	@AfterTarget(targetClass = Alert.class, targetMethod = "accept")
	@Override
	public void afterAlertAccept(@SupportParam WebDriver driver,
			@TargetParam Alert alert) {
		Log.message("Alert has been accepted");
		proxyListener.afterAlertAccept(driver, alert);
	}

	@AfterTarget(targetClass = Alert.class, targetMethod = "dismiss")
	@Override
	public void afterAlertDismiss(@SupportParam WebDriver driver,
			@TargetParam Alert alert) {
		Log.message("Alert has been dismissed");
		proxyListener.afterAlertDismiss(driver, alert);
	}

	@AfterTarget(targetClass = Alert.class, targetMethod = "sendKeys")
	@Override
	public void afterAlertSendKeys(@SupportParam WebDriver driver,
			@TargetParam Alert alert, @UseParameter(number = 0) String keys) {
		Log.message("String " + keys + " has been sent to alert");
		proxyListener.afterAlertSendKeys(driver, alert, keys);
	}

	@AfterTarget(targetClass = WebElement.class, targetMethod = "submit")
	@Override
	public void afterSubmit(@SupportParam WebDriver driver,
			@TargetParam WebElement element) {
		Log.message("Submit has been performed successfully");
		proxyListener.afterSubmit(driver, element);
	}

	@BeforeTarget(targetClass = Alert.class, targetMethod = "accept")
	@Override
	public void beforeAlertAccept(@SupportParam WebDriver driver,
			@TargetParam Alert alert) {
		Log.message("Attempt to accept alert...");
		proxyListener.beforeAlertAccept(driver, alert);
	}

	@BeforeTarget(targetClass = Alert.class, targetMethod = "dissmiss")
	@Override
	public void beforeAlertDismiss(@SupportParam WebDriver driver,
			@TargetParam Alert alert) {
		Log.message("Attempt to dismiss the alert...");
		proxyListener.beforeAlertDismiss(driver, alert);
	}

	@BeforeTarget(targetClass = Alert.class, targetMethod = "sendKeys")
	@Override
	public void beforeAlertSendKeys(@SupportParam WebDriver driver,
			@TargetParam Alert alert, String keys) {
		Log.message("Attemt to send string " + keys + " to alert...");
		proxyListener.beforeAlertSendKeys(driver, alert, keys);
	}

	@BeforeTarget(targetClass = WebElement.class, targetMethod = "submit")
	@Override
	public void beforeSubmit(@SupportParam WebDriver driver,
			@TargetParam WebElement element) {
		highlightElementAndLogAction(element,
				"State before submit will be performed by element: ",
				HowToHighLightElement.INFO);
		proxyListener.beforeSubmit(driver, element);
	}
	
	@BeforeTarget(targetClass = AppiumDriver.class, targetMethod = "findElementByAndroidUIAutomator")
	@BeforeTarget(targetClass = AppiumDriver.class, targetMethod = "findElementsByAndroidUIAutomator")
	@BeforeTarget(targetClass = AppiumDriver.class, targetMethod = "findElementByIosUIAutomation")
	@BeforeTarget(targetClass = AppiumDriver.class, targetMethod = "findElementsByIosUIAutomation")
	@BeforeTarget(targetClass = AppiumDriver.class, targetMethod = "findElementByAccessibilityId")
	@BeforeTarget(targetClass = AppiumDriver.class, targetMethod = "findElementsByAccessibilityId")	
	@BeforeTarget(targetClass = MobileElement.class, targetMethod = "findElementByAndroidUIAutomator")
	@BeforeTarget(targetClass = MobileElement.class, targetMethod = "findElementsByAndroidUIAutomator")
	@BeforeTarget(targetClass = MobileElement.class, targetMethod = "findElementByIosUIAutomation")
	@BeforeTarget(targetClass = MobileElement.class, targetMethod = "findElementsByIosUIAutomation")
	@BeforeTarget(targetClass = MobileElement.class, targetMethod = "findElementByAccessibilityId")
	@BeforeTarget(targetClass = MobileElement.class, targetMethod = "findElementsByAccessibilityId")		
	@Override
	public void beforeFindBy(@UseParameter(number = 0) String byString,
			@TargetParam WebElement element,
			@TargetParam @SupportParam WebDriver driver) {
		Log.debug("Searching for element by locator " + byString
				+ " has been started");
		if (element != null) {
			highlightElementAndLogAction(element, "Using root element",
					HowToHighLightElement.DEBUG);
		}
		proxyListener.beforeFindBy(byString, element, driver);
	}
	
	
	@AfterTarget(targetClass = AppiumDriver.class, targetMethod = "findElementByAndroidUIAutomator")
	@AfterTarget(targetClass = AppiumDriver.class, targetMethod = "findElementsByAndroidUIAutomator")
	@AfterTarget(targetClass = AppiumDriver.class, targetMethod = "findElementByIosUIAutomation")
	@AfterTarget(targetClass = AppiumDriver.class, targetMethod = "findElementsByIosUIAutomation")
	@AfterTarget(targetClass = AppiumDriver.class, targetMethod = "findElementByAccessibilityId")
	@AfterTarget(targetClass = AppiumDriver.class, targetMethod = "findElementsByAccessibilityId")	
	@AfterTarget(targetClass = MobileElement.class, targetMethod = "findElementByAndroidUIAutomator")
	@AfterTarget(targetClass = MobileElement.class, targetMethod = "findElementsByAndroidUIAutomator")
	@AfterTarget(targetClass = MobileElement.class, targetMethod = "findElementByIosUIAutomation")
	@AfterTarget(targetClass = MobileElement.class, targetMethod = "findElementsByIosUIAutomation")
	@AfterTarget(targetClass = MobileElement.class, targetMethod = "findElementByAccessibilityId")
	@AfterTarget(targetClass = MobileElement.class, targetMethod = "findElementsByAccessibilityId")	
	@Override
	public void afterFindBy(@UseParameter(number = 0) String byString,
			@TargetParam WebElement element,
			@TargetParam @SupportParam WebDriver driver) {
		Log.debug("Searching for web element has been finished. Locator is "
				+ byString);
		if (element != null) {
			highlightElementAndLogAction(element, "Root element was used",
					HowToHighLightElement.DEBUG);
		}
		proxyListener.afterFindBy(byString, element, driver);		
	}

	private String addToDescription(WebElement element, String attribute,
			String description) {
		try {
			if (element.getAttribute(attribute) == null)
				return description;
			if (element.getAttribute(attribute).equals(""))
				return description;
			description += " " + attribute + ": "
					+ String.valueOf(element.getAttribute(attribute));
		} catch (Exception e) {
		}
		return description;
	}

	private String elementDescription(WebElement element) {
		String description = "";
		if (element == null)
			return description;

		if (!String.valueOf(element.getTagName()).equals(""))
			description += "tag:" + String.valueOf(element.getTagName());
		description = addToDescription(element, "id", description);
		description = addToDescription(element, "name", description);
		if (!"".equals(element.getText()))
			description += " ('" + String.valueOf(element.getText()) + "')";
		if (!description.equals(""))
			description = " Element is: " + description;

		return description;
	}

	private void highlightElementAndLogAction(WebElement element,
			String logMessage, HowToHighLightElement howToHighLightElement) {
		String elementDescription = elementDescription(element);
		highLighter.resetAccordingTo(configurationWrapper
				.getWrappedConfiguration());
		howToHighLightElement.highLight(highLighter, driver, element,
				logMessage + elementDescription);
	}

}
