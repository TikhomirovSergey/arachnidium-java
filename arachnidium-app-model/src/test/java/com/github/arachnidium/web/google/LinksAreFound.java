package com.github.arachnidium.web.google;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import com.github.arachnidium.core.Handle;
import com.github.arachnidium.model.common.FunctionalPart;
import com.github.arachnidium.model.common.Static;
import com.github.arachnidium.core.HowToGetByFrames;
import com.github.arachnidium.model.support.annotations.ExpectedURL;
import com.github.arachnidium.model.support.annotations.rootelements.RootElement;

@RootElement(chain = {@FindBy(id = "test_id"),@FindBy(id = "test_id")})
@RootElement(chain = {@FindBy(id = "search"), @FindBy(id = "ires")})
@RootElement(chain = {@FindBy(id = "ires")})
public class LinksAreFound<T extends Handle> extends FunctionalPart<T> implements ILinkList {
	@FindBys({@FindBy(className = "r"), @FindBy(tagName = "a")})
	private List<WebElement> links;
	
	@Static
	public List<FoundLink> foundLinks1;
	
	@Static
	public List<FoundLink2> foundLinks2;
	
	@Static
	@RootElement(chain = {@FindBy(className="rc")})
	public List<FoundLink> foundLinks3;
	
	@Static
	@RootElement(chain = {@FindBy(className="rc")}, index = 4)
	public List<FoundLink> foundLinks4;
	
	@Static
	@RootElement(chain = {@FindBy(className="rc")}, index = 4)
	public List<FoundLink2> foundLinks5;
	
	@Static
	@RootElement(chain = {@FindBy(className="fake")}, index = 4)
	public List<FoundLink> foundLinks6;
	
	@Static
	@RootElement(chain = {@FindBy(className="fake")}, index = 4)
	public List<FoundLink2> foundLinks7;
	
	@Static
	@ExpectedURL(regExp = "fake")
	@RootElement(chain = {@FindBy(className="rc")})
	public List<FoundLink2> foundLinks8;
	
	protected LinksAreFound(T handle, HowToGetByFrames path, By by) {
		super(handle, path, by);
	}

	@InteractiveMethod
	public void openLinkByIndex(int index) {
		String reference = links.get(index - 1).getAttribute("href");
		scriptExecutor.executeScript("window.open('" + reference + "');");
	}

	@InteractiveMethod
	public int getLinkCount() {
		return links.size();
	}

	@InteractiveMethod
	public void clickOnLinkByIndex(int index) {
		Actions clickAction = new Actions(getWrappedDriver());
		clickAction.click(links.get(index - 1));
		clickAction.perform();
	}

}
