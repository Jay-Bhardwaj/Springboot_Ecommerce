package com.example.demo;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class smoketesting {

    @Test
    void openGoogle() {

        WebDriver driver = new ChromeDriver();

        driver.get("http://localhost:3000");
        driver.manage().window().maximize();

        System.out.println(driver.getTitle());

//        driver.quit();
    }
}