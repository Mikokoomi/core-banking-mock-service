package com.digitalbank.corebanking.account;

import com.digitalbank.corebanking.common.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BankAccountControllerTest {
    @Test void invalidRequestReturns400() throws Exception {
        LocalValidatorFactoryBean validator=new LocalValidatorFactoryBean(); validator.afterPropertiesSet();
        MockMvc mvc=MockMvcBuilders.standaloneSetup(new BankAccountController(org.mockito.Mockito.mock(BankAccountService.class)))
                .setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();
        mvc.perform(post("/api/accounts").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
}
