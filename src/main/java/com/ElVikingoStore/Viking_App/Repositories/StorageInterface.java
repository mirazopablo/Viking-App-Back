package com.ElVikingoStore.Viking_App.Repositories;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

public interface StorageInterface {
    @Operation(summary = "Inicializa el almacenamiento")
    void init();

    @Operation(summary = "Almacena un archivo")
    String store(MultipartFile file);

    @Operation(summary = "Almacena un archivo en un subdirectorio")
    String store(MultipartFile file, String subDirectory);

    @Operation(summary = "Carga un archivo")
    org.springframework.core.io.Resource loadResource(String filename);
}
