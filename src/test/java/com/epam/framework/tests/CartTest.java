package com.epam.framework.tests;

import com.epam.framework.pages.CartPage;
import com.epam.framework.pages.InventoryPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartTest extends BaseTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    @Test(groups = {"smoke", "regression"}, description = "Add product to cart updates cart badge")
    public void testAddProductToCart() {
        log.info("TEST: adding product to cart increments badge count");

        InventoryPage inventoryPage = openLoginPage()
                .loginAs(getStandardUser())
                .verifyPageLoaded();

        inventoryPage.addProductToCart(PRODUCT_NAME);

        assertThat(inventoryPage.getCartItemCount()).isEqualTo(1);
        log.info("TEST ASSERTION PASSED: cart badge shows 1");
    }

    @Test(groups = {"regression"}, description = "Cart page shows added product")
    public void testCartContainsAddedProduct() {
        log.info("TEST: cart page displays the product that was added");

        CartPage cartPage = openLoginPage()
                .loginAs(getStandardUser())
                .addProductToCart(PRODUCT_NAME)
                .goToCart()
                .verifyPageLoaded();

        assertThat(cartPage.getCartItemCount()).isEqualTo(1);
        assertThat(cartPage.getCartItemNames()).contains(PRODUCT_NAME);
        log.info("TEST ASSERTION PASSED: cart contains [{}]", PRODUCT_NAME);
    }

    @Test(groups = {"regression"}, description = "Add multiple products to cart")
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
