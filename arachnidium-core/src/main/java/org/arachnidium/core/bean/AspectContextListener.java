package org.arachnidium.core.bean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.arachnidium.core.eventlisteners.IContextListener;
import org.arachnidium.core.interfaces.IContext;
import org.arachnidium.core.interfaces.IHasActivity;
import org.arachnidium.core.interfaces.IHasHandle;
import org.arachnidium.core.interfaces.ITakesPictureOfItSelf;
import org.arachnidium.core.settings.ScreenShots;
import org.arachnidium.util.configuration.interfaces.IConfigurationWrapper;
import org.arachnidium.util.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openqa.selenium.ScreenOrientation;

/**
 * @author s.tihomirov Implementation of @link{IContextListener} by default
 *         Listens to mobile context events
 */
@Aspect
public class AspectContextListener extends DefaultHandleListener implements
		IContextListener {

	private final List<IContextListener> contextEventListeners = new ArrayList<IContextListener>() {
		private static final long serialVersionUID = 1L;
		{ // SPI
			Iterator<?> providers = ServiceLoader.load(IContextListener.class)
					.iterator();
			while (providers.hasNext())
				add((IContextListener) providers.next());
		}
	};
	private final InvocationHandler contextListenerInvocationHandler = (proxy,
			method, args) -> {
		contextEventListeners.forEach((eventListener) -> {
			try {
				method.invoke(eventListener, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			;
		});
		return null;
	};
	/**
	 * It listens to window events and invokes listener methods
	 */
	private final IContextListener windowListenerProxy = (IContextListener) Proxy
			.newProxyInstance(IContextListener.class.getClassLoader(),
					new Class[] { IContextListener.class },
					contextListenerInvocationHandler);

	public AspectContextListener(IConfigurationWrapper configurationWrapper) {
		super(configurationWrapper);
	}

	@Override
	@BeforeTarget(targetClass = IContext.class, targetMethod = "switchToMe")
	public void beforeIsSwitchedOn(@TargetParam IHasHandle handle) {
		Log.debug("Attempt to switch to context " + handle.getHandle());
		windowListenerProxy.beforeIsSwitchedOn(handle);
	}

	@Override
	@AfterTarget(targetClass = IContext.class, targetMethod = "switchToMe")
	public void whenIsSwitchedOn(@TargetParam IHasHandle handle) {
		Log.message("Current context is " + handle.getHandle()
				+ getActivityDescription(handle));
		windowListenerProxy.whenIsSwitchedOn(handle);
	}

	@Override
	@AfterTarget(targetClass = IContext.class, targetMethod = "whenIsCreated")
	public void whenNewHandleIsAppeared(@TargetParam IHasHandle handle) {
		String message = "A new context " + handle.getHandle()
				+ getActivityDescription(handle);
		if (configurationWrapper.getWrappedConfiguration()
				.getSection(ScreenShots.class).getToDoScreenShotsOfNewHandles()) {
			((ITakesPictureOfItSelf) handle).takeAPictureOfAnInfo(message);
		} else {
			Log.message(message);
		}
		windowListenerProxy.whenNewHandleIsAppeared(handle);
	}

	private String getActivityDescription(IHasHandle handle) {
		String activity = String.valueOf(((IHasActivity) handle)
				.currentActivity());
		if ("".equals(activity)) {
			return activity;
		}
		return " Activity is " + activity;
	}

	@Override
	@Around("execution(* org.arachnidium.core.interfaces.IHasHandle.*(..)) || "
			+ "execution(* org.arachnidium.core.interfaces.ISwitchesToItself.*(..)) || "
			+ "execution(* org.openqa.selenium.Rotatable.*(..))")
	public Object doAround(ProceedingJoinPoint point) throws Throwable {
		launchMethod(point, this, WhenLaunch.BEFORE);
		Object result = null;
		try {
			result = point.proceed();
		} catch (Exception e) {
			throw e;
		}
		launchMethod(point, this, WhenLaunch.AFTER);
		return result;
	}

	@Override
	@BeforeTarget(targetClass = IContext.class, targetMethod = "rotate")
	public void beforeIsRotated(@TargetParam IHasHandle handle,
			@UseParameter(number = 0) ScreenOrientation orientation) {
		Log.debug("Attempt to rotate screen. Context is " + handle.getHandle()
				+ getActivityDescription(handle) + ", new orientation is "
				+ orientation.toString());
		windowListenerProxy.beforeIsRotated(handle, orientation);
	}

	@Override
	@AfterTarget(targetClass = IContext.class, targetMethod = "rotate")
	public void whenIsRotated(@TargetParam IHasHandle handle,
			@UseParameter(number = 0) ScreenOrientation orientation) {
		Log.debug("Screen was rotated. Context is " + handle.getHandle()
				+ getActivityDescription(handle) + ", new orientation is "
				+ orientation.toString());
		windowListenerProxy.whenIsRotated(handle, orientation);
	}
}