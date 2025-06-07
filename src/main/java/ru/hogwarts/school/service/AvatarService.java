package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentService studentService;

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    public List<Avatar> getAllAvatars(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        logger.info("Was invoked method to get all avatars");

        return avatarRepository.findAll(pageRequest).getContent();
    }

    public Avatar findAvatar(long studentId) {
        logger.info("Was invoked method to find avatar by student id");

        return avatarRepository.findByStudentId(studentId).orElseThrow(() -> {
            logger.error("There is not avatar with student id = {}", studentId);

            return new StudentNotFoundException(studentId);
        });
    }

    public ResponseEntity<Resource> downloadAvatar(Long studentId) throws IOException {
        Avatar avatar = findAvatar(studentId);

        Path path = Path.of(avatar.getFilePath());
        Resource resource = new UrlResource(path.toUri());
        logger.info("Was invoked method to download avatar by student id");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .contentLength(avatar.getFileSize())
                .body(resource);
    }

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        if (avatarFile.getSize() > 1024 * 300) {
            logger.error("The avatar file size must not exceed big size");

            throw new AvatarNotAllowedBigFileSizeException(avatarFile.getSize());
        }

        logger.info("Was invoked method to upload avatar");
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
        logger.info("Was invoked method to delete avatar from db");

        avatarRepository.deleteById(avatar.getId());
    }

    private String getExtensions(String fileName) {
        logger.debug("Was invoked method to get avatar file extension by file name");

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
