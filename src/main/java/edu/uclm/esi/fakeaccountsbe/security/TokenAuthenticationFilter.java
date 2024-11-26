package edu.uclm.esi.fakeaccountsbe.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.User;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDao userDao;

    // Este método se ejecuta en cada solicitud para autenticar al usuario
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Obtener el token desde las cookies
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Validar el token y establecer atributos en la solicitud
        if (token != null) {
            User user = userDao.findByToken(token);
            if (user != null) {
                // Si el usuario existe, establecer el atributo en la solicitud para que los controladores lo utilicen
                request.setAttribute("user", user);
            }
        }

        // Continuar con la solicitud
        filterChain.doFilter(request, response);
    }
}
