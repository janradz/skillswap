package de.fhmuenster.skillswap.security;

import de.fhmuenster.skillswap.model.Role;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class GuestAuthFilter extends OncePerRequestFilter
{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {

        String guestHeader = request.getHeader("X-Guest-Access");

        if (guestHeader != null && guestHeader.equals("true")) {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "guest",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + Role.GUEST.name()))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}