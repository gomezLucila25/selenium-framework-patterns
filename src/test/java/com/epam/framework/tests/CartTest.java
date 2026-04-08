package com.epam.framework.tests;

import com.epam.framework.pages.CartPage;
import com.epam.framework.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Shopping Cart")
public class CartTest extends BaseTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    @Story("Add to cart")
    @Description("Adding a product updates the cart badge counter")
    @Test(groups = {"smoke", "regression"})
    public void testAddProductToCart() {
        log.info("TEST: adding product to cart increments badge count");

        InventoryPage inventoryPage = openLoginPage()
                .loginAs(getStandardUser())
                .verifyPageLoaded();

        inventoryPage.addProductToCart(PRODUCT_NAME);

        assertThat(inventoryPage.getCartItemCount()).isEqualTo(1);
        log.info("TEST ASSERTION PASSED: cart badge shows 1");
    }

    @Story("Cart contents")
    @Description("Full cart scenario: login, verify page, sort products, add item, navigate to cart and verify contents")
    @Test(groups = {"regression"})
    public void testCartContainsAddedProduct() {
        log.info("TEST: full cart scenario — login to cart verification");

        // Step 1: Open login page and login
        InventoryPage inventoryPage = openLoginPage().loginAs(getStandardUser());

        // Step 2: Verify inventory page is loaded
        inventoryPage.verifyPageLoaded();

        // Step 3: Verify page title
        assertThat(inventoryPage.getPageTitle()).isEqualTo("Products");
        log.info("TEST ASSERTION PASSED: inventory page title is 'Products'");

        // Step 4: Verify current URL
        assertThat(getDriver().getCurrentUrl()).contains("inventory");
        log.info("TEST ASSERTION PASSED: URL contains 'inventory'");

        // Step 5: Sort products by price (low to high) — uses className locator
        inventoryPage.sortBy("Price (low to high)");
        assertThat(inventoryPage.getSelectedSortOption()).isEqualTo("Price (low to high)");
        log.info("TEST ASSERTION PASSED: sort option applied");

        // Step 6: Add product to cart
        inventoryPage.addProductToCart(PRODUCT_NAME);

        // Step 7: Verify cart badge shows 1
        assertThat(inventoryPage.getCartItemCount()).isEqualTo(1);
        log.info("TEST ASSERTION PASSED: cart badge shows 1 after adding product");

        // Step 8: Navigate to cart page
        CartPage cartPage = inventoryPage.goToCart();
        cartPage.verifyPageLoaded();

        // Step 9: Assert exactly 1 item in cart
        assertThat(cartPage.getCartItemCount()).isEqualTo(1);
        log.info("TEST ASSERTION PASSED: cart page contains 1 item");

        // Step 10: Assert correct product is in cart
        assertThat(cartPage.getCartItemNames()).contains(PRODUCT_NAME);
        log.info("TEST ASSERTION PASSED: cart contains [{}]", PRODUCT_NAME);
    }

    @Story("Add to cart")
    @Description("Adding two products increments cart badge to 2")
    @Test(groups = {"regression"})
    public void testAddMultipleProducts() {
        log.info("TEST: adding two products increments cart badge to 2");

        InventoryPage inventoryPage = openLoginPage().loginAs(getStandardUser());
        inventoryPage
                .addProductToCart("Sauce Labs Backpack")
                .addProductToCart("Sauce Labs Bike Light");

        assertThat(inventoryPage.getCartItemCount()).isEqualTo(2);
        log.info("TEST ASSERTION PASSED: cart badge shows 2 after adding 2 products");
    }
}
