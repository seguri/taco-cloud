package tacos;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@TestInstance(PER_CLASS)
public class DesignAndOrderTacosBrowserTest {
    private static final String HOME_URL = "http://localhost/";
    private static final String DESIGN_URL = "http://localhost/design";
    private static final String ORDERS_URL = "http://localhost/orders";
    private static final String ORDERS_CURRENT_URL = "http://localhost/orders/current";

    @Autowired
    private WebApplicationContext wac;

    private HtmlUnitDriver browser;

    @BeforeAll
    public void init() {
        browser = MockMvcHtmlUnitDriverBuilder.webAppContextSetup(wac).build();
        browser.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterAll
    public void close() {
        browser.close();
    }

    @Test
    public void testDesignATacoPage_HappyPath() {
        startDesigningATaco();
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
        clickBuildAnotherTaco();
        buildAndSubmitATaco("Another Taco", "COTO", "CARN", "JACK", "LETC", "SRCR");
        fillInAndSubmitOrderForm();
        assertEquals(HOME_URL, browser.getCurrentUrl());
    }

    @Test
    public void testDesignATacoPage_EmptyOrderInfo() {
        startDesigningATaco();
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
        submitEmptyOrderForm();
        fillInAndSubmitOrderForm();
        assertEquals(HOME_URL, browser.getCurrentUrl());
    }

    @Test
    public void testDesignATacoPage_InvalidOrderInfo() {
        startDesigningATaco();
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
        submitInvalidOrderForm();
        fillInAndSubmitOrderForm();
        assertEquals(HOME_URL, browser.getCurrentUrl());
    }

    private void startDesigningATaco() {
        browser.get(HOME_URL);
        assertEquals(HOME_URL, browser.getCurrentUrl());
        browser.findElementByCssSelector("a[id='design']").click();
        assertEquals(DESIGN_URL, browser.getCurrentUrl());
    }

    private void assertDesignPageElements() {
        List<WebElement> ingredientGroups = browser.findElementsByClassName("ingredient-group");
        assertEquals(5, ingredientGroups.size());

        WebElement wrapGroup = browser.findElementByCssSelector("div.ingredient-group#wraps");
        List<WebElement> wraps = wrapGroup.findElements(By.tagName("div"));
        assertEquals(2, wraps.size());
        assertIngredient(wrapGroup, 0, "FLTO", "Flour Tortilla");
        assertIngredient(wrapGroup, 1, "COTO", "Corn Tortilla");

        WebElement proteinGroup = browser.findElementByCssSelector("div.ingredient-group#proteins");
        List<WebElement> proteins = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, proteins.size());
        assertIngredient(proteinGroup, 0, "GRBF", "Ground Beef");
        assertIngredient(proteinGroup, 1, "CARN", "Carnitas");

        WebElement cheeseGroup = browser.findElementByCssSelector("div.ingredient-group#cheeses");
        List<WebElement> cheeses = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, cheeses.size());
        assertIngredient(cheeseGroup, 0, "CHED", "Cheddar");
        assertIngredient(cheeseGroup, 1, "JACK", "Monterrey Jack");

        WebElement veggieGroup = browser.findElementByCssSelector("div.ingredient-group#veggies");
        List<WebElement> veggies = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, veggies.size());
        assertIngredient(veggieGroup, 0, "TMTO", "Diced Tomatoes");
        assertIngredient(veggieGroup, 1, "LETC", "Lettuce");

        WebElement sauceGroup = browser.findElementByCssSelector("div.ingredient-group#sauces");
        List<WebElement> sauces = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, sauces.size());
        assertIngredient(sauceGroup, 0, "SLSA", "Salsa");
        assertIngredient(sauceGroup, 1, "SRCR", "Sour Cream");
    }

    private void assertIngredient(WebElement ingredientGroup, int ingredientIdx, String id, String name) {
        List<WebElement> divs = ingredientGroup.findElements(By.tagName("div"));
        WebElement div = divs.get(ingredientIdx);
        assertEquals(id, div.findElement(By.tagName("input")).getAttribute("value"));
        assertEquals(name, div.findElement(By.tagName("span")).getText());
    }

    private void buildAndSubmitATaco(String name, String... ingredients) {
        assertDesignPageElements();
        for (String ingredient : ingredients) {
            browser.findElementByCssSelector("input[value='" + ingredient + "']").click();
        }
        browser.findElementByCssSelector("input#name").sendKeys(name);
        browser.findElementByCssSelector("form").submit();
    }

    private void clickBuildAnotherTaco() {
        assertTrue(browser.getCurrentUrl().startsWith(ORDERS_URL));
        browser.findElementByCssSelector("a[id='another']").click();
    }

    private void fillField(String fieldName, String value) {
        WebElement field = browser.findElementByCssSelector(fieldName);
        field.clear();
        field.sendKeys(value);
    }

    private void fillInAndSubmitOrderForm() {
        assertTrue(browser.getCurrentUrl().startsWith(ORDERS_URL));
        fillField("input#name", "Ima Hungry");
        fillField("input#street", "1234 Culinary Blvd.");
        fillField("input#city", "Foodsville");
        fillField("input#state", "CO");
        fillField("input#zip", "81019");
        fillField("input#ccNumber", "4111111111111111");
        fillField("input#ccExpiration", "10/19");
        fillField("input#ccCVV", "123");
        browser.findElementByCssSelector("form").submit();

        int[] a = new int[8];
        int sum = Arrays.stream(a).sum();
    }

    private void submitEmptyOrderForm() {
        assertEquals(ORDERS_CURRENT_URL, browser.getCurrentUrl());
        browser.findElementByCssSelector("form").submit();
        assertEquals(ORDERS_URL, browser.getCurrentUrl());

        List<String> validationErrors = getValidationErrorTexts();
        assertEquals(9, validationErrors.size());
        assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
        assertTrue(validationErrors.contains("Name is required"));
        assertTrue(validationErrors.contains("Street is required"));
        assertTrue(validationErrors.contains("City is required"));
        assertTrue(validationErrors.contains("State is required"));
        assertTrue(validationErrors.contains("Zip code is required"));
        assertTrue(validationErrors.contains("Not a valid credit card number"));
        assertTrue(validationErrors.contains("Must be formatted MM/YY"));
        assertTrue(validationErrors.contains("Invalid CVV"));
    }

    private void submitInvalidOrderForm() {
        assertTrue(browser.getCurrentUrl().startsWith(ORDERS_CURRENT_URL));
        fillField("input#name", "I");
        fillField("input#street", "1");
        fillField("input#city", "F");
        fillField("input#state", "C");
        fillField("input#zip", "8");
        fillField("input#ccNumber", "1234432112344322");
        fillField("input#ccExpiration", "14/91");
        fillField("input#ccCVV", "1234");
        browser.findElementByCssSelector("form").submit();
        assertEquals(ORDERS_URL, browser.getCurrentUrl());

        List<String> validationErrors = getValidationErrorTexts();
        assertEquals(4, validationErrors.size());
        assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
        assertTrue(validationErrors.contains("Not a valid credit card number"));
        assertTrue(validationErrors.contains("Must be formatted MM/YY"));
        assertTrue(validationErrors.contains("Invalid CVV"));
    }

    private List<String> getValidationErrorTexts() {
        return browser.findElementsByClassName("validationError").stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
