package com.study.configs;

import com.study.accounts.Account;
import com.study.accounts.AccountRole;
import com.study.accounts.AccountService;
import com.study.common.BaseControllerTest;
import com.study.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {

        Set<AccountRole> accountRoles = Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet());
        String username = "06007@sk.com";
        String password = "1234";
        
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(accountRoles)
                .build();
        
        accountService.saveAccount(account);
        
        String clientId = "myApp";
        String clientSecret = "pass";
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId,clientSecret))
                .param("username",username)
                .param("password",password)
                .param("grant_type","password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

    }

}