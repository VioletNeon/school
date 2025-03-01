package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.AvatarNotAllowedBigFileSizeException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentService studentService;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    public Avatar findAvatar(long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    public ResponseEntity<Resource> downloadAvatar(Long studentId) throws IOException {
        Avatar avatar = findAvatar(studentId);

        Path path = Path.of(avatar.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .contentLength(avatar.getFileSize())
                .body(resource);
    }

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        if (avatarFile.getSize() > 1024 * 300) {
            throw new AvatarNotAllowedBigFileSizeException(avatarFile.getSize());
        }

        Student student = studentService.findStudent(studentId);
        Path filePath = Path.of(avatarsDir, student.getId() + "." + getExtensions(Objects.requireNonNull(avatarFile.getOriginalFilename())));

        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findById(studentId).orElseGet(Avatar::new);

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());

        avatarRepository.save(avatar);
    }

    public void deleteAvatar(long studentId) throws IOException {
        Avatar avatar = findAvatar(studentId);
        Path filePath = Path.of(avatar.getFilePath());

        Files.deleteIfExists(filePath);

        avatarRepository.deleteById(avatar.getId());
    }

    private String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
