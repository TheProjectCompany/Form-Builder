package org.tpc.form_builder.security;

import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.constants.RoleConstants;
import org.tpc.form_builder.models.User;

import java.util.Collection;

@Component("permissionEvaluator")
@Log4j2
public class CustomPermissionEvaluator {

    public boolean hasClientPermission(Authentication authentication, @NotNull String clientId, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return RoleConstants.SUPER_ADMIN.equalsIgnoreCase(user.getRoleId()) || clientId.equalsIgnoreCase(user.getClientId());
        }

        return false;
    }

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
