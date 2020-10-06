package tacos;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@TestInstance(PER_CLASS)
public class HomePageBrowserTest {

    @Autowired
    private WebApplicationContext wac;

    private HtmlUnitDriver browser;

    @BeforeAll
    public void init() {
        browser = MockMvcHtmlUnitDriverBuilder.webAppContextSetup(wac).build();
    }

    @AfterAll
    public void close() {
        browser.close();
    }

    @Test
    public void testHomePage() throws IOException {
        String url = "http://localhost";
        browser.get(url);

        String titleText = browser.getTitle();
        assertEquals("Taco Cloud", titleText);

        String h1Text = browser.findElementByTagName("h1").getText();
        assertEquals("Welcome to...", h1Text);

        String imgSrc = browser.findElementByTagName("img").getAttribute("src");
        assertEquals(url + "/images/TacoCloud.png", imgSrc);
    }
}
