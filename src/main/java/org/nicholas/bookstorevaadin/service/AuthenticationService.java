package org.nicholas.bookstorevaadin.service;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.nicholas.bookstorevaadin.security.details.StoreUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    AuthenticationContext authenticationContext;

    public AuthenticationService(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public UserDetails getCurrentPrincipal() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return principal.equals("anonymousUser") ? null : (UserDetails) principal;
        return authenticationContext.getAuthenticatedUser(UserDetails.class).orElse(null);
    }

    public void logout() {
        authenticationContext.logout();
    }
}
