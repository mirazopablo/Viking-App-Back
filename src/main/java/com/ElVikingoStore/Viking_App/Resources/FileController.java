package com.ElVikingoStore.Viking_App.Resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ElVikingoStore.Viking_App.Repositories.StorageInterface;

@Tag(name = "File Controller", description = "API para la gestión de archivos")
@RestController
@RequestMapping("/auth")
public class FileController {

    @Autowired
    private StorageInterface storageService;

    @Operation(summary = "Cargar archivo", description = "Carga un archivo")
    @GetMapping("/uploads/{filename:.+}")
    public Resource serveFile(@PathVariable String filename) {
        return storageService.loadResource(filename);
    }
}
