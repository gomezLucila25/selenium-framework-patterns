package com.epam.framework.tests;

import com.epam.framework.model.User;
import com.epam.framework.pages.InventoryPage;
import com.epam.framework.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.openqa.selenium.WindowType;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Authentication")
public class LoginTest extends BaseTest {

    @Story("Valid login")
    @Description("Standard user logs in, verifies inventory page, sorts products and navigates back")
    @Test(groups = {"smoke", "regression"})
    public void testValidLogin() {
        log.info("TEST: valid login with standard user — full scenario");

        // Step 1: Open login page
        LoginPage loginPage = openLoginPage();

        // Step 2: Verify login page logo text (xpath locator)
        assertThat(loginPage.getLoginLogoText()).contains("Swag Labs");
        log.info("TEST ASSERTION PASSED: login logo text verified");

        // Step 3: Login as standard user
        InventoryPage inventoryPage = loginPage.loginAs(getStandardUser());

        // Step 4: Verify inventory page is loaded
        inventoryPage.verifyPageLoaded();

        // Step 5: Verify page title
        assertThat(inventoryPage.getPageTitle()).isEqualTo("Products");
        log.info("TEST ASSERTION PASSED: page title is 'Products'");

        // Step 6: Verify URL contains "inventory"
        assertThat(getDriver().getCurrentUrl()).contains("inventory");
        log.info("TEST ASSERTION PASSED: URL contains 'inventory'");

        // Step 7: Verify cart is initially empty
        assertThat(inventoryPage.getCartItemCount()).isEqualTo(0);
        log.info("TEST ASSERTION PASSED: cart is empty on login");

        // Step 8: Sort products by Name (Z to A) — uses className locator
        inventoryPage.sortBy("Name (Z to A)");

        // Step 9: Verify selected sort option is applied
        assertThat(inventoryPage.getSelectedSortOption()).isEqualTo("Name (Z to A)");
        log.info("TEST ASSERTION PASSED: sort option 'Name (Z to A)' applied");

        // Step 10: Navigate back using WebDriver navigate() API
        getDriver().navigate().back();
        assertThat(getDriver().getCurrentUrl()).doesNotContain("inventory");
        log.info("TEST ASSERTION PASSED: navigated back to login page");
    }

    @Story("Locked user")
    @Description("Locked user sees an error message and cannot proceed")
    @Test(groups = {"regression"})
    public void testLockedUserLogin() {
        log.info("TEST: locked user receives error message on login");

        LoginPage loginPage = openLoginPage();
        loginPage.enterCredentials(
                config.get("locked.user"),
                config.getPassword()
        ).clickLoginButton();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Sorry, this user has been locked out");
        log.info("TEST ASSERTION PASSED: locked user error message displayed");
    }

    @Story("Invalid credentials")
    @Description("Empty credentials trigger a validation error")
    @Test(groups = {"regression"})
    public void testEmptyCredentials() {
        log.info("TEST: empty credentials trigger validation error");

        LoginPage loginPage = openLoginPage()
                .enterCredentials("", "")
                .clickLoginButton();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        log.info("TEST ASSERTION PASSED: error displayed for empty credentials");
    }

    @Story("Invalid credentials")
    @Description("Wrong password triggers a credentials mismatch error")
    @Test(groups = {"regression"})
    public void testWrongPassword() {
        log.info("TEST: wrong password triggers error");
        User badUser = new User(config.getStandardUser(), "wrong_password");

        LoginPage loginPage = openLoginPage()
                .enterCredentials(badUser.getUsername(), badUser.getPassword())
                .clickLoginButton();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username and password do not match");
        log.info("TEST ASSERTION PASSED: wrong password error message displayed");
    }

    @Story("Logout")
    @Description("Authenticated user can logout and return to the login page")
    @Test(groups = {"smoke", "regression"})
    public void testLogout() {
        log.info("TEST: logout returns user to login page");

        LoginPage loginPage = openLoginPage()
                .loginAs(getStandardUser())
                .logout();

        assertThat(loginPage.isErrorDisplayed()).isFalse();
        log.info("TEST ASSERTION PASSED: user is back on login page after logout");
    }

    @Story("Window handling")
    @Description("Demonstrates switchTo() API: open new tab, switch between windows, close tab")
    @Test(groups = {"regression"})
    public void testWindowSwitching() {
        log.info("TEST: switchTo() — window/tab switching");

        // Step 1: Login and reach inventory page
        InventoryPage inventoryPage = openLoginPage().loginAs(getStandardUser());
        inventoryPage.verifyPageLoaded();

        // Step 2: Store original window handle
        String originalWindow = getDriver().getWindowHandle();
        assertThat(getDriver().getWindowHandles()).hasSize(1);
        log.info("TEST ASSERTION PASSED: 1 window open initially");

        // Step 3: Open a new tab using switchTo().newWindow()
        getDriver().switchTo().newWindow(WindowType.TAB);
        assertThat(getDriver().getWindowHandles()).hasSize(2);
        log.info("TEST ASSERTION PASSED: new tab opened — 2 windows total");

        // Step 4: Navigate to SauceDemo in the new tab
        getDriver().get(config.getBaseUrl());
        assertThat(getDriver().getCurrentUrl()).contains("saucedemo");
        log.info("TEST ASSERTION PASSED: navigated to SauceDemo in new tab");

        // Step 5: Close new tab and switch back to original window
        getDriver().close();
        getDriver().switchTo().window(originalWindow);

        // Step 6: Verify we are back on the inventory page
        assertThat(getDriver().getCurrentUrl()).contains("inventory");
        log.info("TEST ASSERTION PASSED: switched back to original window — inventory page");
    }
}
