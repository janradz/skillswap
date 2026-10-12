package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.dto.UserCreateRequestDTO;
import de.fhmuenster.skillswap.dto.UserResponseDTO;
import de.fhmuenster.skillswap.exception.HolidayException;
import de.fhmuenster.skillswap.service.HolidayService;
import de.fhmuenster.skillswap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final HolidayService holidayService;

    public UserController(UserService userService, HolidayService holidayService) {
        this.userService = userService;
        this.holidayService = holidayService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping("/TestHolidayException")
    public void ExceptionTest() throws Exception
    {
        if (holidayService.isHoliday(LocalDate.of(1999, 1, 1)))
        {
            throw new HolidayException(LocalDate.of(1999,1,1), "Feiertag");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserCreateRequestDTO request) {
        UserResponseDTO registeredUser = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
}