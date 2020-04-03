package com.study.configs;

import com.study.accounts.Account;
import com.study.accounts.AccountRole;
import com.study.accounts.AccountService;
import com.study.common.AppProperties;
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

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {

       /*AppConfig에서 run할때 admin / user 생성하므로 해당 코드유지하면 pk dup발생
       Set<AccountRole> accountRoles = Stream.of(AccountRole.USER).collect(Collectors.toSet());

        Account account = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(accountRoles)
                .build();


       accountService.saveAccount(account);
       */
       mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret()))
                .param("username",appProperties.getUserUsername())
                .param("password",appProperties.getUserPassword())
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

    }

}