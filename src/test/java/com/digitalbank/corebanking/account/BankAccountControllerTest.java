package com.digitalbank.corebanking.account;

import com.digitalbank.corebanking.account.dto.AccountResponse;
import com.digitalbank.corebanking.common.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BankAccountControllerTest {
    BankAccountService service; MockMvc mvc;
    @BeforeEach void setup() {
        service=mock(BankAccountService.class); LocalValidatorFactoryBean validator=new LocalValidatorFactoryBean(); validator.afterPropertiesSet();
        mvc=MockMvcBuilders.standaloneSetup(new BankAccountController(service)).setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();
    }
    @Test void firstRequestReturns201() throws Exception {
        var response=response(); when(service.create(eq("KEY-1"),any())).thenReturn(new BankAccountService.CreateAccountResult(response,true));
        mvc.perform(post("/api/accounts").header("Idempotency-Key","KEY-1").contentType(MediaType.APPLICATION_JSON).content(body(response.applicationId())))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.accountNumber").value("ACC-1"));
    }
    @Test void replayReturns200() throws Exception {
        var response=response(); when(service.create(eq("KEY-1"),any())).thenReturn(new BankAccountService.CreateAccountResult(response,false));
        mvc.perform(post("/api/accounts").header("Idempotency-Key","KEY-1").contentType(MediaType.APPLICATION_JSON).content(body(response.applicationId())))
                .andExpect(status().isOk());
    }
    @Test void missingIdempotencyKeyReturns400() throws Exception {
        mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(body(UUID.randomUUID())))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
    @Test void blankIdempotencyKeyReturns400() throws Exception {
        mvc.perform(post("/api/accounts").header("Idempotency-Key"," ").contentType(MediaType.APPLICATION_JSON).content(body(UUID.randomUUID())))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
    @Test void invalidRequestReturns400() throws Exception {
        mvc.perform(post("/api/accounts").header("Idempotency-Key","KEY").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
    private AccountResponse response(){return new AccountResponse(UUID.randomUUID(),"ACC-1",UUID.randomUUID(),"CUS001","P",AccountStatus.ACTIVE,OffsetDateTime.now());}
    private String body(UUID id){return "{\"applicationId\":\""+id+"\",\"customerId\":\"CUS001\",\"productCode\":\"P\"}";}
}
