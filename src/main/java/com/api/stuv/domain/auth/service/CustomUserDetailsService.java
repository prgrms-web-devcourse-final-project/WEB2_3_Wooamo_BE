package com.api.stuv.domain.auth.service;

import com.api.stuv.domain.auth.dto.CustomUserDetails;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userData = userRepository.findByEmail(email);

        if(userData != null){
            return new CustomUserDetails(userData);
        }

        throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
    }
}
