package org.arachnidium.core;

import java.util.Set;

import org.arachnidium.core.bean.MainBeanConfiguration;
import org.arachnidium.core.fluenthandle.FluentWindowWaiting;
import org.arachnidium.core.settings.WindowIsClosedTimeOut;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;

public final class WindowManager extends Manager<FluentWindowStrategy> {

	public WindowManager(WebDriverEncapsulation initialDriverEncapsulation) {
		super(initialDriverEncapsulation);
		handleWaiting = new FluentWindowWaiting();
	}

	@Override
	void changeActive(String handle) throws NoSuchWindowException,
			UnhandledAlertException {
		Set<String> handles = getHandles();
		if (!handles.contains(handle))
			throw new NoSuchWindowException("There is no window with handle "
					+ handle + "!");
		try {
			getWrappedDriver().switchTo().window(handle);
		} catch (UnhandledAlertException | NoSuchWindowException e) {
			throw e;
		}
	}

	synchronized void close(String handle) throws UnclosedWindowException,
			NoSuchWindowException, UnhandledAlertException,
			UnreachableBrowserException {
		long timeOut = getTimeOut(getWebDriverEncapsulation().configuration
				.getSection(WindowIsClosedTimeOut.class)
				.getWindowIsClosedTimeOutTimeOut());

		try {
			changeActive(handle);
			WebDriver driver = getWrappedDriver();
			driver.switchTo().window(handle).close();
		} catch (UnhandledAlertException | NoSuchWindowException e) {
			throw e;
		}

		try {
			awaiting.awaitCondition(timeOut, isClosed(handle));
		} catch (TimeoutException e) {
			throw new UnclosedWindowException("Window hasn't been closed!", e);
		}

		int actualWinCount = 0;
		try {
			actualWinCount = getHandles().size();
		} catch (WebDriverException e) { // if all windows are closed
			actualWinCount = 0;
		} finally {
			if (actualWinCount == 0) {
				destroy();
				getWebDriverEncapsulation().destroy();
			}
		}
	}

	/**
	 * returns window handle by it's index
	 */
	@Override
	public synchronized Handle getHandle(int index)
			throws NoSuchWindowException {
		String handle = this.getStringHandle(index);
		SingleWindow initedWindow = (SingleWindow) Handle.isInitiated(handle,
				this);
		if (initedWindow != null)
			return initedWindow;
		SingleWindow window = new SingleWindow(handle, this);
		return returnNewCreatedListenableHandle(window,
				MainBeanConfiguration.WINDOW_BEAN);
	}

	@Override
	/**
	 * returns window handle by it's index
	 */
	String getStringHandle(int windowIndex) throws NoSuchWindowException {
		Long time = getTimeOut(getHandleWaitingTimeOut()
				.getHandleWaitingTimeOut());
		FluentWindowStrategy f = new FluentWindowStrategy();
		f.setExpected(windowIndex);
		return getStringHandle(time, f);
	}

	@Override
	Set<String> getHandles() {
		return getWrappedDriver().getWindowHandles();
	}

	@Override
	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration
	 */
	String getStringHandle(FluentWindowStrategy strategy)
			throws NoSuchWindowException {
		Long time = getTimeOut(getHandleWaitingTimeOut()
				.getHandleWaitingTimeOut());
		return getStringHandle(time, strategy);
	}

	@Override
	public Handle getHandle(FluentWindowStrategy fluentHandleStrategy)
			throws NoSuchWindowException {
		SingleWindow window = new SingleWindow(
				getStringHandle(fluentHandleStrategy), this);
		return returnNewCreatedListenableHandle(window,
				MainBeanConfiguration.WINDOW_BEAN);
	}

	@Override
	public Handle getHandle(long timeOut,
			FluentWindowStrategy fluentHandleStrategy)
			throws NoSuchWindowException {
		SingleWindow window = new SingleWindow(getStringHandle(timeOut,
				fluentHandleStrategy), this);
		return returnNewCreatedListenableHandle(window,
				MainBeanConfiguration.WINDOW_BEAN);
	}

	@Override
	String getStringHandle(long timeOut,
			FluentWindowStrategy fluentHandleStrategy)
			throws NoSuchWindowException {
		FluentWindowStrategy clone = fluentHandleStrategy.cloneThis();
		try {
			return awaiting.awaitCondition(timeOut,
					clone.getExpectedCondition(handleWaiting));
		} catch (TimeoutException e) {
			throw new NoSuchWindowException("Can't find window! Condition is "
					+ clone.toString(), e);
		}
	}

	// is browser window closed?
	private static Boolean isClosed(final WebDriver from, String handle) {
		Set<String> handles;
		try {
			handles = from.getWindowHandles();
		} catch (WebDriverException e) { // if all windows are closed
			return true;
		}

		if (!handles.contains(handle))
			return true;
		else
			return null;
	}

	// fluent waiting for the result. See above
	public static ExpectedCondition<Boolean> isClosed(final String closingHandle) {
		return from -> isClosed(from, closingHandle);
	}
}