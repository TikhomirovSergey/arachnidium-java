package com.github.arachnidium.core.bean;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.openqa.selenium.Alert;
import org.openqa.selenium.ContextAware;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import com.github.arachnidium.core.interfaces.IDestroyable;
import com.github.arachnidium.util.configuration.interfaces.IConfigurationWrapper;
import com.github.arachnidium.util.reflect.executable.ExecutableUtil;


/**
 * This is {@link AnnotationConfigApplicationContext}
 * 
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WebDriverBeanConfiguration {
	private IConfigurationWrapper wrapper;
	private AbstractApplicationContext context;
	private IDestroyable destroyable;
	
	public final static String COMPONENT_BEAN = "component";
	public final static String WEBDRIVER_BEAN = "webdriver";
	private WebDriver driver;
	
	/**
	 * Creates {@link WebDriver} instance and makes it listenable.
	 * 
	 * @param context instantiated {@link AbstractApplicationContext} 
	 * which is used by {@link AspectWebDriver}
	 * @param configurationWrapper something that wraps {@link Configuration}
	 * {@link AspectWebDriver} needs it
	 * @param destroyable Something that implements {@link IDestroyable}
	 * @param required Class of {@link WebDriver} implementor
	 * @param paramClasses  Are constructor parameters
	 * @param paramValues Are constructor parameter values
	 * 
	 * @return A listenable {@link WebDriver} instance
	 */
	@SuppressWarnings("unchecked")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean(name = WEBDRIVER_BEAN)
	public <T extends WebDriver> T getWebdriver(AbstractApplicationContext context, 
			IConfigurationWrapper configurationWrapper,
			IDestroyable destroyable,
			Class<T> required,
			Object[] paramValues) {
		try {
            Constructor<?> c = ExecutableUtil.getRelevantConstructor(required, paramValues);
			
			if (c == null){
				throw new NoSuchMethodException(required.getName() + " has no constructor that matches " +
						"given parameters " + Arrays.asList(paramValues).toString());
			}
			T result = (T) c.newInstance(paramValues);
			return (T) populate(context, configurationWrapper, destroyable, result);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	private WebDriver populate(AbstractApplicationContext context, 
			IConfigurationWrapper configurationWrapper,
			IDestroyable destroyable,
			WebDriver driver){
		this.driver = driver;
		wrapper = configurationWrapper;
		this.destroyable = destroyable;
		this.context = context;
		return driver;
	}
	
	/**
	 * It return listenable {@link WebDriver} components
	 * 
	 * @see WebDriver
	 * @see WebElement
	 * @see Navigation
	 * @see Options
	 * @see TargetLocator
	 * @see JavascriptExecutor
	 * @see ContextAware
	 * @see Alert
	 * @see MobileElement
	 * @see AppiumDriver
	 *  
	 * @param component It is an object of types above
	 * @return The listenable object of types above
	 * 
	 * @see Bean
	 */
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean(name = COMPONENT_BEAN)
	Object  getComponent(Object component) {
		return component;
	}	
	
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean(name = "webdriverAspect")
	AspectWebDriver getWebdriverAspect(){
		return new AspectWebDriver(driver, wrapper, destroyable, context);
	}
}
