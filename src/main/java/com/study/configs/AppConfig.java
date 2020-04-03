package com.study.configs;

import com.study.accounts.Account;
import com.study.accounts.AccountRepository;
import com.study.accounts.AccountRole;
import com.study.accounts.AccountService;
import com.study.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class AppConfig
{

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;
            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Set<AccountRole> accountRoles = Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet());
                Account admin = Account.builder()
                        .email(appProperties.getAdminUsername())
                        .password(appProperties.getAdminPassword())
                        .roles(accountRoles)
                        .build();

               //  System.out.println("11111" + admin.getEmail());

                accountService.saveAccount(admin);

                Set<AccountRole> accountRole = Stream.of(AccountRole.USER).collect(Collectors.toSet());
                Account user = Account.builder()
                        .email(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword())
                        .roles(accountRole)
                        .build();

                System.out.println("11111" + user.getEmail() +" " + user.getRoles());

                accountService.saveAccount(user);
            }
        };
    }

}

