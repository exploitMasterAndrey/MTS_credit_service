package com.example.creditservice.controller;

import com.example.creditservice.controller.exceptionHandler.ExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface AuthController {
    @Operation(
            tags = "Войти в систему",
            summary = "Вход в систему",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = AuthResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "408",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    ResponseEntity<?> login(AuthRequest authRequest);

    record AuthResponse(String access_token){}

    record AuthRequest(String username, String password){}
}
