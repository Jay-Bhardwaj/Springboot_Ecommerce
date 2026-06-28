package com.example.demo.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final long STEP_WAIT_MS = Long.getLong("selenium.step.wait.ms", 1000L);

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Click a button by its text
     */
    protected void clickButton(String text) {
        By locator = By.xpath("//button[normalize-space()='" + text + "']");
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        pauseForDemo();
    }

    /**
     * Type text into an input field by its label
     */
    protected void typeByLabel(String labelText, String value) {
        WebElement input = inputByLabel(labelText);
        input.clear();
        input.sendKeys(value);
        pauseForDemo();
    }

    /**
     * Get input element by its label text
     */
    protected WebElement inputByLabel(String labelText) {
        By locator = By.xpath("//label[.//span[normalize-space()='" + labelText + "']]//*[self::input or self::textarea]");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Get toast message element
     */
    protected WebElement toastMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".Toastify__toast")));
    }

    /**
     * Check if input is valid using HTML5 validation
     */
    protected boolean isInputValid(String labelText) {
        return (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", inputByLabel(labelText));
    }

    /**
     * Get validation message for an input
     */
    protected String validationMessageFor(String labelText) {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", inputByLabel(labelText));
    }

    /**
     * Pause for demo purposes
     */
    protected void pauseForDemo() {
        pauseForDemo(STEP_WAIT_MS);
    }

    /**
     * Pause for specified milliseconds
     */
    protected void pauseForDemo(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
