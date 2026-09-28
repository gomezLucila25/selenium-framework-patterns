# Web Test Automation Framework — Selenium · Java · Design Patterns

![Java](https://img.shields.io/badge/Java-17-orange) ![Selenium](https://img.shields.io/badge/Selenium-4.18-43B02A) ![TestNG](https://img.shields.io/badge/TestNG-7.9-red) ![Cucumber](https://img.shields.io/badge/Cucumber-7.15-23D96C) ![Allure](https://img.shields.io/badge/Allure-2.25-yellow) ![Jenkins](https://img.shields.io/badge/CI-Jenkins-D24939)

A UI test automation framework for an e-commerce web app ([SauceDemo](https://www.saucedemo.com)). I built it step by step during the EPAM Test Automation program, and it is the most complete version of that work.

It covers the full loop a QA automation engineer owns: **framework design → tests → BDD specs → parallel execution → CI pipeline → reporting**.

---

## Highlights

- **Design patterns applied on purpose.** Each one fixes a concrete SOLID problem, documented in the code:
  - **Factory Method.** One `WebDriverCreator` per browser: Chrome, Chrome headless, Firefox and Edge. Adding a browser means adding a class, with no `switch` to edit (OCP).
  - **Singleton + ThreadLocal.** `DriverManager` gives one WebDriver per thread, so tests run safely in parallel.
  - **Thread-safe Singleton.** `ConfigProvider` uses double-checked locking with `volatile` and loads `qa` or `dev` environments.
  - **Decorator.** Element actions are wrapped with logging and visual highlighting, and the driver is injected (DIP).
  - **Strategy.** Wait behaviour can be swapped at runtime: `ExplicitWaitStrategy` or `FluentWaitStrategy`.
  - **Page Object + PageFactory** with a fluent API, plus **Business Objects** (`User`, `Product`, `Order`).
- **Two test styles on the same framework:**
  - **TestNG** suites (smoke and regression) with groups and parallel methods;
  - **Cucumber BDD** features written in Gherkin, including a `Scenario Outline` with data tables.
- **Readable end-to-end flows:**
  ```java
  openLoginPage()
      .loginAs(getStandardUser())
      .addProductToCart("Sauce Labs Backpack")
      .goToCart()
      .proceedToCheckout()
      .fillShippingInfo(order)
      .clickContinue();
  ```
- **Allure reporting.** Tests are annotated with `@Feature`, `@Story` and `@Step`, and a screenshot is attached automatically on failure.
- **Jenkins CI.** A parameterized job lets you pick the browser, environment and suite, and runs on a dedicated agent node. It publishes JUnit results and archives screenshots and logs as artifacts.
- **Selenide comparison.** The same login scenarios are also written in Selenide (`SelenideLoginTest`) to compare the two approaches.
- **Logging.** Log4j2 writes to the console and to a rolling file.

## Architecture

```
src/main/java/com/epam/framework/
├── config/      ConfigProvider              → Singleton, env-based properties (qa / dev)
├── core/        DriverManager               → ThreadLocal driver lifecycle
│                DriverFactory + browser/*   → Factory Method (Chrome, Headless, Firefox, Edge)
├── decorator/   Logging / Highlight         → Decorator over element actions
├── strategy/    Explicit / Fluent waits     → Strategy
├── model/       User, Product, Order        → Business Objects
├── pages/       Login, Inventory, Cart,     → Page Objects (fluent)
│                Checkout, BasePage
└── utils/       Screenshot, Highlight

src/test/
├── java/.../tests/       Login, Cart, Checkout, Selenide  (TestNG)
├── java/.../cucumber/    runner, steps, hooks             (BDD)
├── java/.../listeners/   TestListener → screenshot + Allure attachment on failure
└── resources/
    ├── features/         login.feature, cart.feature
    └── suites/           smoke.xml, regression.xml (parallel), cucumber.xml
```

## What is tested

| Area | Scenarios |
|---|---|
| Authentication | Valid login, logout, locked-out user, wrong password, invalid user (data-driven) |
| Shopping cart | Add single or multiple products, badge counter, cart contents |
| Checkout | Full end-to-end purchase with order confirmation, required-field validation |

## How to run

```bash
# Default: Chrome, QA environment, smoke suite
mvn clean test

# Choose browser / environment / suite
mvn clean test -Dbrowser=firefox -Denv=dev -Dsuite=regression

# Headless (CI)
mvn clean test -Dbrowser=chrome-headless

# Allure report
mvn allure:serve
```

## Screenshots

**Allure report**

![Allure overview](jenkins-screenshots/allure/01_allure-overview.png)
![Allure behaviors](jenkins-screenshots/allure/03_allure-behaviors.png)

**Jenkins: parameterized job and builds**

![Jenkins parameters](jenkins-screenshots/16_job2-parameters.png)
![Jenkins builds](jenkins-screenshots/13_job2-builds.png)

## Tech stack

Java 17 · Selenium WebDriver 4 · TestNG · Cucumber-JVM · Selenide · AssertJ · Allure · Log4j2 · Maven · Jenkins

---

Built by **Lucila Gómez** · [LinkedIn](https://www.linkedin.com/in/lucila-g%C3%B3mez-71252423a/) · Other QA work: [API tests with REST Assured](https://github.com/gomezLucila25/ApiAutomation) · [Gmail E2E suite](https://github.com/gomezLucila25/WebDriver-Java-TestNG) · [Unit testing a third-party library](https://github.com/gomezLucila25/calculator)
