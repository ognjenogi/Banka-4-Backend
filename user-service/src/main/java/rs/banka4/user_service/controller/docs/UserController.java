package rs.banka4.user_service.controller.docs;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.banka4.rafeisen.common.dto.UserResponseDto;
import rs.banka4.user_service.service.impl.UserService;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * This is for OTC service to fetch username
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }
}
