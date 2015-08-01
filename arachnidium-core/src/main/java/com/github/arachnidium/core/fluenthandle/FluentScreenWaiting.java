package com.github.arachnidium.core.fluenthandle;

import io.appium.java_client.android.AndroidDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.ContextAware;
import org.openqa.selenium.WebDriver;

public class FluentScreenWaiting implements IFluentHandleWaiting {
	
	private static String getContextWhichMatchesToContextExpression(
			String contextRegExp, String currentContext) {
		Pattern p = Pattern.compile(contextRegExp);
		Matcher m = p.matcher(currentContext);
		if (m.find()) {
			return currentContext;
		} else
			return null;
	}

	private static String getContextWhichMatchesToActivities(String context,
			List<String> activitiesRegExps, String currentActivity) {
		for (String activity : activitiesRegExps) {
			Pattern p = Pattern.compile(activity);
			Matcher m = p.matcher(currentActivity);
	
			if (m.find()) {
				return context;
			}
		}
		return null;
	}
	
	private Function<WebDriver, String> getContextByIndexAndContextExpression(
			int contextIndex, String contextRegExp) {
		return getHandle(contextIndex).andThen(input -> {
			return getContextWhichMatchesToContextExpression(contextRegExp,
					input);
		});
	}
	
	private Function<WebDriver, String> getContextByIndexAndActivities(final WebDriver from,
			int contextIndex, List<String> activitiesRegExps) {
		return getHandle(contextIndex).andThen(input -> {
			ContextAware contextAware = ((ContextAware) from);
			String currentActivity = ((AndroidDriver<?>) contextAware.context(input)).currentActivity();
			
			return getContextWhichMatchesToActivities(input, activitiesRegExps, currentActivity);
		});
	}	

	private Function<WebDriver, String> getContextByContextExpressionAndActivities(final WebDriver from,
			List<String> activitiesRegExps, String contextRegExp) {
		return getHandle(contextRegExp).andThen(input -> {
			ContextAware contextAware = ((ContextAware) from);
			String currentActivity = ((AndroidDriver<?>) contextAware.context(input)).currentActivity();

			return getContextWhichMatchesToActivities(input, activitiesRegExps,
					currentActivity);
		});
	}	
	
	private Function<WebDriver, String> getContextByAllConditions(final WebDriver from,
			int contextIndex, List<String> activitiesRegExps, String contextRegExp) {
		return getContextByIndexAndContextExpression(contextIndex, contextRegExp).andThen(input -> {
			ContextAware contextAware = ((ContextAware) from);
			String currentActivity = ((AndroidDriver<?>) contextAware.context(input)).currentActivity();
			return getContextWhichMatchesToActivities(input, activitiesRegExps,
					currentActivity);
		});
		
	}	
	
	/**
	 * returns context that we have been waiting for
	 * specified time. The context is defined by index
	 * 
	 * 
	 * @see com.github.arachnidium.core.fluenthandle.IFluentHandleWaiting#getHandle(int)
	 */
	@Override
	public IFunctionalHandleCondition getHandle(int index) {
		return from -> {
			Set<String> handles = ((ContextAware) from).getContextHandles();
			if (handles.size() - 1 >= index) {
				((ContextAware) from).context(handles.toArray()[index].toString());
				return new ArrayList<String>(handles).get(index);
			} else
				return null;
		};
	}

	
	/**
     * returns context that we have been waiting for
	 * specified time.
	 * 
	 * The context should have defined name. We can specify part of a
	 * context name as a regular expression
	 * 
	 * @see com.github.arachnidium.core.fluenthandle.IFluentHandleWaiting#getHandle(java.lang.String)
	 */	
	@Override
	public IFunctionalHandleCondition getHandle(String contextRegExp) {
		return from -> {
			String resultHandle = null;
			ContextAware contextAware = ((ContextAware) from);
			Set<String> handles = contextAware.getContextHandles();
			for (String handle : handles) {
				resultHandle = getContextWhichMatchesToContextExpression(contextRegExp, 
						handle);
				if (resultHandle == null) {
					continue;
				}
				return resultHandle;
			}
			return resultHandle;
		};
	}

	/**
	 * returns context that we have been waiting for
	 * specified time. The context is defined by index
	 * 
	 * The context should have defined name. We can specify part of a
	 * context name as a regular expression
	 * 
	 * @see com.github.arachnidium.core.fluenthandle.IFluentHandleWaiting#getHandle(int,
	 *      java.lang.String)
	 */
	@Override
	public IFunctionalHandleCondition getHandle(int index, String contextRegExp) {
		return from -> getContextByIndexAndContextExpression(index, contextRegExp).apply(from);
	}

	/**
	 returns context that we have been waiting for
	 * specified time.
	 * 
     * There should be activity 
     * from the given list. Activities can be defined partially as regular expressions
	 * 
	 * @see com.github.arachnidium.core.fluenthandle.IFluentHandleWaiting#getHandle(java.util.List)
	 */	
	@Override
	public IFunctionalHandleCondition getHandle(List<String> activitiesRegExps) {
		return from -> {
			String resultHandle = null;
			ContextAware contextAware = ((ContextAware) from);
			Set<String> handles = contextAware.getContextHandles();
			for (String handle : handles) {
				String currentActivity = ((AndroidDriver<?>) contextAware.context(handle)).currentActivity();

				resultHandle = getContextWhichMatchesToActivities(handle, activitiesRegExps,
						currentActivity);
				if (resultHandle == null) {
					continue;
				}
				return resultHandle;
			}
			return resultHandle;
		};
	}

	/**
	 * returns context that we have been waiting for
	 * specified time. The context is defined by index.
	 * 
     * There should be activity 
     * from the given list. Activities can be defined partially as regular expressions
	 * 
	 * @see com.github.arachnidium.core.fluenthandle.IFluentHandleWaiting#getHandle(int,
	 *      java.util.List)
	 */
	@Override
	public IFunctionalHandleCondition getHandle(int index,
			List<String> activitiesRegExps) {
		return from -> getContextByIndexAndActivities(from, index, activitiesRegExps).apply(from);
	}

	/**
	 * returns context that we have been waiting for
	 * specified time.
	 * 
	 * The context should have defined name. We can specify part of a
	 * context name as a regular expression
	 * 
     * There should be activity 
     * from the given list. Activities can be defined partially as regular expressions
	 *
	 * @see com.github.arachnidium.core.fluenthandle.IFluentHandleWaiting#getHandle(java.lang.String,
	 *      java.util.List)
	 */
	@Override
	public IFunctionalHandleCondition getHandle(String contextRegExp,
			List<String> activitiesRegExps) {
		return from -> getContextByContextExpressionAndActivities(from, activitiesRegExps, contextRegExp).apply(from);
	}

	/**
     * returns context that we have been waiting for
	 * specified time. The context is defined by index.
	 * 
	 * The context should have defined name. We can specify part of a
	 * context name as a regular expression.
	 *    
	 * There should be activity 
     * from the given list. Activities can be defined partially as regular expressions
	 * 
	 * @see com.github.arachnidium.core.fluenthandle.IFluentHandleWaiting#getHandle(int,
	 *      java.lang.String, java.util.List)
	 */	
	@Override
	public IFunctionalHandleCondition getHandle(int index, String contextRegExp,
			List<String> activitiesRegExps) {
		return from -> getContextByAllConditions(from, index, activitiesRegExps, contextRegExp).apply(from);
	}

}
