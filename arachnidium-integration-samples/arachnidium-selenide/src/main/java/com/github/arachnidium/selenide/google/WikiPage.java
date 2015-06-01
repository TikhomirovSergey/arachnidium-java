package com.github.arachnidium.selenide.google;

import com.github.arachnidium.core.BrowserWindow;
import com.github.arachnidium.model.browser.BrowserPage;
import com.github.arachnidium.model.support.annotations.ExpectedPageTitle;
import com.github.arachnidium.model.support.annotations.ExpectedURL;

@ExpectedURL(regExp = "https://ru.wikipedia.org/wiki")
@ExpectedURL(regExp = "wikipedia.org")
@ExpectedPageTitle(regExp = "^*[?[Hello]\\?[world]]")
public class WikiPage extends BrowserPage {

	public WikiPage(BrowserWindow browserWindow){
		super(browserWindow);
	}
}
