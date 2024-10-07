package com.example.Brokage.service;

import com.example.Brokage.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String customerId) throws UsernameNotFoundException {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    logger.trace("User not found , customerId:{}", customerId);
                    return new UsernameNotFoundException("User not found");
                });
    }
}
