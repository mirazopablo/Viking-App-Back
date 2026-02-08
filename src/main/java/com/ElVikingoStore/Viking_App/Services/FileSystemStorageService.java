package com.ElVikingoStore.Viking_App.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ElVikingoStore.Viking_App.Repositories.StorageInterface;

import jakarta.annotation.PostConstruct;

@Schema(name = "FileSystemStorageService", description = "Servicio para almacenamiento de archivos en el sistema de archivos")
@Service
public final class FileSystemStorageService implements StorageInterface {

    private final Path rootLocation;

    public FileSystemStorageService(@Value("${upload.path}") String uploadPath) {
        this.rootLocation = Paths.get(uploadPath);
        init(); // Inicializar el directorio si no existe
    }

    @Operation(summary = "Almacenar archivo", description = "Almacena un archivo en el sistema de archivos")
    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            // Asegurarse de que el archivo no esté fuera del directorio objetivo
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            // Copiar el archivo
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return file.getOriginalFilename();
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to store file. Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Almacenar archivo en subdirectorio", description = "Almacena un archivo en un subdirectorio del sistema de archivos")
    @Override
    public String store(MultipartFile file, String subDirectory) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            if (subDirectory.contains("..")) {
                throw new RuntimeException("Cannot store file with relative path outside current directory "
                        + subDirectory);
            }

            Path subDirPath = this.rootLocation.resolve(subDirectory);
            if (!Files.exists(subDirPath)) {
                Files.createDirectories(subDirPath);
            }

            Path destinationFile = subDirPath.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            // Copiar el archivo
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Devuelve la ruta relativa para uso en URL (asegurando separadores tipo URL)
            return subDirectory + "/" + file.getOriginalFilename();
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to store file. Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Inicializar almacenamiento", description = "Inicializa el directorio de almacenamiento si no existe")
    @Override
    @PostConstruct
    public void init() {
        try {
            // Crear el directorio si no existe
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage: " + e.getMessage());
        }
    }

    @Operation(summary = "Cargar archivo", description = "Carga un archivo del sistema de archivos")
    @Override
    public Resource loadResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new FileSystemResource(java.util.Objects.requireNonNull(file.toFile()));

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
