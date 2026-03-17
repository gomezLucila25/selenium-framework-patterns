package com.epam.framework.tests;

import com.epam.framework.model.User;
import com.epam.framework.pages.InventoryPage;
import com.epam.framework.pages.LoginPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Valid login redirects to inventory page")
    public void testValidLogin() {
        log.info("TEST: valid login with standard user");

        InventoryPage inventoryPage = openLoginPage().loginAs(getStandardUser());

        inventoryPage.verifyPageLoaded();
        assertThat(inventoryPage.getPageTitle()).isEqualTo("Products");
        log.info("TEST ASSERTION PASSED: inventory page title is 'Products'");
    }

    @Test(groups = {"regression"}, description = "Locked user cannot login")
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

    @Test(groups = {"regression"}, description = "Empty credentials show error")
    public void testEmptyCredentials() {
        log.info("TEST: empty credentials trigger validation error");

        LoginPage loginPage = openLoginPage()
                .enterCredentials("", "")
                .clickLoginButton();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        log.info("TEST ASSERTION PASSED: error displayed for empty credentials");
    }

    @Test(groups = {"regression"}, description = "Wrong password shows error")
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

    @Test(groups = {"smoke", "regression"}, description = "Logout returns to login page")
    public void testLogout() {
        log.info("TEST: logout returns user to login page");

        LoginPage loginPage = openLoginPage()
                .loginAs(getStandardUser())
                .logout();

        assertThat(loginPage.isErrorDisplayed()).isFalse();
        log.info("TEST ASSERTION PASSED: user is back on login page after logout");
    }
}
