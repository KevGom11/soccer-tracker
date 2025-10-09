package com.kevin.soccertracker.web;

import com.kevin.soccertracker.domain.SessionToken;
import com.kevin.soccertracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

record RequestCodeDTO(String email) {}
record VerifyDTO(String email, String code) {}
record UserDTO(Long id, String email, String name) {}
record VerifyResponseDTO(String sessionToken, UserDTO user) {}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Accept both /request and /request-code to be tolerant of older frontends
    @PostMapping({"/request", "/request-code"})
    public ResponseEntity<Void> requestCode(@RequestBody RequestCodeDTO body) {
        authService.requestCode(body.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponseDTO> verify(@RequestBody VerifyDTO body) {
        SessionToken st = authService.verify(body.email(), body.code());
        var u = st.getUser();
        return ResponseEntity.ok(
                new VerifyResponseDTO(
                        st.getToken().toString(),
                        new UserDTO(u.getId(), u.getEmail(), u.getName())
                )
        );
    }
}
