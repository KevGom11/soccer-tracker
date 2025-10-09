package com.kevin.soccertracker.security;

import com.kevin.soccertracker.domain.User;
import com.kevin.soccertracker.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CurrentUserResolver implements HandlerInterceptor {
    private final AuthService authService;
    private static final ThreadLocal<User> TL = new ThreadLocal<>();

    public CurrentUserResolver(AuthService authService) { this.authService = authService; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        String token = req.getHeader("X-Session-Token");
        User user = authService.resolveUserFromToken(token);
        TL.set(user);
        return true;
    }

    public static User get() { return TL.get(); }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TL.remove();
    }
}
