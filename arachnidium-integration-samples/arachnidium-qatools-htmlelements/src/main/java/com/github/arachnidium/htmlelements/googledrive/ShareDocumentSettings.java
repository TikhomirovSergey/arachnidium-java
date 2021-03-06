package com.github.arachnidium.htmlelements.googledrive;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.github.arachnidium.core.Handle;
import com.github.arachnidium.model.common.FunctionalPart;
import com.github.arachnidium.model.support.annotations.Frame;

@Frame(howToGet = How.CLASS_NAME, locator = "share-client-content-iframe")
public class ShareDocumentSettings<S extends Handle> extends FunctionalPart<S> {

	@FindBy(xpath = "//*[contains(@id,'simpleInviter')]//*[@class='simple-inviter-recipient-area']//textarea")
	private TextInput invite;
	@FindBy(xpath = ".//*[contains(@id,'close')]")
	private Button done;	
	@FindBy(xpath = ".//*[contains(@id,'cancel')]")
	private Button cancel;
	@FindBy(className = "simple-sharing-manage-permissions-link")
	private Button managePermissions;
	
	protected ShareDocumentSettings(S handle) {
		super(handle);
		// (!!!)
		HtmlElementLoader.populatePageObject(this,
								getWrappedDriver());
	}	
	
	@InteractiveMethod
	public void invite(String eMail){
		Actions a = new Actions(getWrappedDriver());
		invite.getWrappedElement().click();
		a.sendKeys(invite.getWrappedElement(), eMail, Keys.ENTER);
		a.perform();
	}
	
	@InteractiveMethod
	public void clickDone(){
		done.click();
	}
	
	@InteractiveMethod
	public void clickCancel(){
		cancel.click();
	}
	
	@InteractiveMethod
	public void clickOnManagePermissions(){
		managePermissions.click();
	}
}
