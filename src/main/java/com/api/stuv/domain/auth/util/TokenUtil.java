package com.api.stuv.domain.auth.util;

import com.api.stuv.domain.auth.dto.CustomUserDetails;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.InvalidUserRoleException;
import com.api.stuv.global.exception.UserNotAuthenticatedException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Component
public class TokenUtil {

    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw new UserNotAuthenticatedException(ErrorCode.UNAUTHORIZED);
        }

        return customUserDetails.getUsername();
    }

    public Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw new UserNotAuthenticatedException(ErrorCode.UNAUTHORIZED);
        }

        return customUserDetails.getUserId();
    }

    public String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UserNotAuthenticatedException(ErrorCode.UNAUTHORIZED);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.isEmpty()) {
            throw new InvalidUserRoleException(ErrorCode.FORBIDDEN);
        }

        return authorities.iterator().next().getAuthority();
    }
}
