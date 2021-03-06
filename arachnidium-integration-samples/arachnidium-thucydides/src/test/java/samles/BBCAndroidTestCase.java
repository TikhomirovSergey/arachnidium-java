package samles;

import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.arachnidium.thucydides.steps.AndroidBBCSteps;
import com.github.arachnidium.util.configuration.Configuration;

/**
 * 
 * Prerequisites: Appium server should be launched.
 * Android emulator should be prepared. Required API Level is 19 (4.4)  
 *
 */
@RunWith(ThucydidesRunner.class)
public class BBCAndroidTestCase {
	
	@Steps
	AndroidBBCSteps steps;
	private static String RESOURCE_PATH = "src/test/resources/";
	private static final String SETTIGS_OF_BBC = "android_bbc.json";

	@Before
	public void setUp() throws Exception {
		steps.insatantiateApp(Configuration.get(RESOURCE_PATH + SETTIGS_OF_BBC));
	}

	@After
	public void tearDown() throws Exception {
		steps.quit();
	}

	@Test
	public void sampleTestOnAndroidBBC() {
		steps.makeSureThatArticlesArePresent();
		steps.selectArticle(1);
		steps.makeShureThatArticleIsHere();
		steps.edit();
		steps.setTopicIsChecked("LATIN AMERICA", true);
		steps.setTopicIsChecked("UK", true);
		steps.select();
		
		steps.edit();
		steps.setTopicIsChecked("LATIN AMERICA", false);
		steps.setTopicIsChecked("UK", false);
		steps.select();
	}

}
