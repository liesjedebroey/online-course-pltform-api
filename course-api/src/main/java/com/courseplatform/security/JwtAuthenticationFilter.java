package com.courseplatform.security;

import com.courseplatform.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;

        // 1. Snelle check: Is er een Bearer header? Zo nee -> doortrappen naar volgende filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Haal de token eruit en maak hem schoon
            jwt = authHeader.replace("Bearer ", "").trim();

            // DEBUG LOGS (Deze zie je in je IntelliJ console)
            System.out.println("--- FILTER DEBUG ---");
            System.out.println("Token ontvangen: " + jwt.substring(0, 10) + "...");

            // 3. Haal username uit de token
            userName = jwtService.extractUsername(jwt);
            System.out.println("Username uit token: " + userName);

            // 4. Als er een username is en we zijn nog niet ingelogd in deze sessie
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

                // 5. Check of de token echt geldig is voor deze gebruiker
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. DE CRUCIALE STAP: Zet de gebruiker in de SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Systeem: Gebruiker " + userName + " succesvol geauthenticeerd.");
                }
            }
        } catch (Exception e) {
            // Vang fouten op (zoals MalformedJwt of ExpiredJwt) zodat de app niet crasht
            System.out.println("Systeem: JWT Validatie mislukt: " + e.getMessage());
        }

        // 7. Ga door naar het volgende filter (of de Controller)
        filterChain.doFilter(request, response);
    }
}