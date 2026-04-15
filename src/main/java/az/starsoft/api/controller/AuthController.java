package az.starsoft.api.controller;

import az.starsoft.api.config.RateLimiter;
import az.starsoft.api.dto.AuthRequest;
import az.starsoft.api.dto.AuthResponse;
import az.starsoft.api.security.CustomUserDetails;
import az.starsoft.api.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RateLimiter rateLimiter;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        String clientKey = clientIp(httpRequest);
        if (!rateLimiter.allowLogin(clientKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "rate_limited",
                    "message", "Çox cəhd. Bir dəqiqədən sonra yenidən cəhd edin."
            ));
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(
                ((CustomUserDetails) authentication.getPrincipal()).getUsername());
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new AuthResponse(
                token,
                refreshToken,
                user.getUsername(),
                user.getFullName(),
                user.getAuthorities().iterator().next().getAuthority()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !tokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "invalid_refresh_token"));
        }
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        String newToken = tokenProvider.generateTokenForUsername(username);
        String newRefresh = tokenProvider.generateRefreshToken(username);
        return ResponseEntity.ok(Map.of("token", newToken, "refreshToken", newRefresh));
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
