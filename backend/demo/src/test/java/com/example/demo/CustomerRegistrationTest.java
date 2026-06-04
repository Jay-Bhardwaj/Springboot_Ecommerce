package com.example.demo;

import org.junit.jupiter.api.Test;

class CustomerRegistrationTest extends SeleniumCustomerTestBase {

    @Test
    void customerCanRegisterWithValidInputsAndThenLogin() {
//        TestCustomer customer = newCustomer("Registration");

         
         
          TestCustomer customer = new TestCustomer(
                  "Your Name",
                  "yourgmail@gmail.com",
                  "Your@123"
          );
         

        openApp();
        openCustomerRegister();
        registerCustomerFromUi(customer);

        openCustomerLogin();
        loginCustomerFromUi(customer);
        assertCustomerDashboardVisible();

        System.out.println("Customer registration test passed for: " + customer.email());
    }
}
