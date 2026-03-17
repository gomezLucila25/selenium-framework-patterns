package com.epam.framework.pages;

import com.epam.framework.config.ConfigProvider;
import com.epam.framework.core.DriverManager;
import com.epam.framework.decorator.ClickAction;
import com.epam.framework.decorator.ElementAction;
import com.epam.framework.decorator.HighlightDecorator;
import com.epam.framework.decorator.LoggingDecorator;
import com.epam.framework.decorator.TypeAction;
import com.epam.framework.strategy.ExplicitWaitStrategy;
import com.epam.framework.strategy.WaitStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Abstract base for all Page Objects.
 *
 * PATTERN USAGE:
 *  - Decorator: click() and type() build a decorator chain:
 *      LoggingDecorator → HighlightDecorator → ClickAction/TypeAction
 *    Each concern (logging, highlighting, actual interaction) is isolated in its own class.
 *  - Strategy: waitStrategy controls how the page waits for element readiness.
 *    Defaults to ExplicitWaitStrategy; subclasses or tests can inject FluentWaitStrategy.
 *
 * SOLID fixes applied here:
 *  - SRP: BasePage no longer mixes waiting logic, highlight logic, and action logic.
 *    Each is handled by a dedicated class.
 *  - DIP: The WebDriver is retrieved once and passed down; HighlightDecorator receives
 *    it via constructor instead of reaching into DriverManager internally.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitStrategy waitStrategy;
    protected final Logger log = LogManager.getLogger(getClass());

    // Kept for URL-condition waits used in CartPage / CheckoutPage
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        int explicitWait = ConfigProvider.getInstance().getExplicitWait();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        this.waitStrategy = new ExplicitWaitStrategy(explicitWait);
        PageFactory.initElements(driver, this);
        log.debug("Page initialised: [{}]", getClass().getSimpleName());
    }

    /**
     * Clicks an element using the Decorator chain:
     * LoggingDecorator → HighlightDecorator → ClickAction
     */
    protected void click(WebElement element) {
        waitStrategy.waitForClickable(driver, element);

        ElementAction action = new ClickAction(driver);
        action = new HighlightDecorator(action, driver);
        action = new LoggingDecorator(action, "CLICK");
        action.perform(element);
    }

    /**
     * Types into an input using the Decorator chain:
     * LoggingDecorator → HighlightDecorator → TypeAction
     */
    protected void type(WebElement element, String text) {
        waitStrategy.waitForVisible(driver, element);

        ElementAction action = new TypeAction(driver, text);
        action = new HighlightDecorator(action, driver);
        action = new LoggingDecorator(action, "TYPE[" + text + "]");
        action.perform(element);
    }

    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        String text = element.getText();
        log.debug("getText — value: [{}]", text);
        return text;
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
