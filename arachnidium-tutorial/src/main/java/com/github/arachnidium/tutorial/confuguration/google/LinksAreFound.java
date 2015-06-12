package com.github.arachnidium.tutorial.confuguration.google;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;

import com.github.arachnidium.core.Handle;
import com.github.arachnidium.model.common.FunctionalPart;

class LinksAreFound<T extends Handle> extends FunctionalPart<T> implements ILinkList {
	
	@FindBy(id = "ires")
	private RemoteWebElement result;
	
	protected LinksAreFound(T handle) {
		super(handle);
	}

	@InteractiveMethod
	public int getLinkCount() {
		return result.findElements(By.className("r")).size();
	}

	@InteractiveMethod
	public void openLinkByIndex(int index) {
		String reference = result.findElements(By.className("r")).get(index-1).
				findElement(By.tagName("a")).getAttribute("href");
		scriptExecutor.executeScript("window.open('" + reference + "');");
	}

}
