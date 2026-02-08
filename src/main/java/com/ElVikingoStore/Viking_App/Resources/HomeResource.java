package com.ElVikingoStore.Viking_App.Resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Home Controller", description = "API para test de funcionamiento")
@RestController
@RequestMapping("/api")
public class HomeResource {
    @Operation(
            summary = "Saludo",
            description = "Mensaje de bienvenida"
    )
    @GetMapping("/")
    public String greeting() {
        return "If you see this message, Welcome to my api -- Status: Running";
    }
}
