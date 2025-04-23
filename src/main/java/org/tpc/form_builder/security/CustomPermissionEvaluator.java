package org.tpc.form_builder.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("permissionEvaluator")
@Log4j2
public class CustomPermissionEvaluator {

    public boolean hasProfilePermission(Authentication authentication, String type, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String requiredPermission = type + ":" + permission; // Example: PROCESS.CREATE

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredPermission::equals);
    }
}
