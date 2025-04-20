package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.AvatarNotAllowedBigFileSizeException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    @Mock
    private AvatarRepository avatarRepository;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private AvatarService avatarService;

    private Student testStudent1;
    private Student testStudent2;
    private final Avatar testAvatar1 = new Avatar();
    private final Avatar testAvatar2 = new Avatar();

    private final String avatarsDir = "./src/main/resources/images/avatars";

    @BeforeEach
    void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        testStudent1 = new Student();
        testStudent1.setId(1L);
        testStudent1.setName("Harry Potter");
        testStudent1.setAge(11);

        testStudent2 = new Student();
        testStudent2.setId(2L);
        testStudent2.setName("Ron Weasley");
        testStudent2.setAge(12);

        testAvatar1.setStudent(testStudent1);
        testAvatar1.setFilePath(avatarsDir + "/1.jpg");
        testAvatar1.setFileSize(1024L);
        testAvatar1.setMediaType("image/jpeg");
        testAvatar1.setData(new byte[1024]);

        testAvatar2.setStudent(testStudent2);
        testAvatar2.setFilePath(avatarsDir + "/2.jpg");
        testAvatar2.setFileSize(1024L);
        testAvatar2.setMediaType("image/jpeg");
        testAvatar2.setData(new byte[1024]);

        Field avatarsDirField = AvatarService.class.getDeclaredField("avatarsDir");
        avatarsDirField.setAccessible(true);
        avatarsDirField.set(avatarService, avatarsDir);

        Path avatarsPath = Path.of(avatarsDir);

        if (!Files.exists(avatarsPath)) {
            Files.createDirectories(avatarsPath);
        }
    }

    @Test
    void returnAllAvatarsByPageAndSizeParams_existingPageNumberAndPageSize_returnsAvatars() {
        testAvatar1.setId(1L);
        testAvatar2.setId(2L);
        PageRequest pageRequestMock = PageRequest.of(1, 1);
        Page<Avatar> page = new PageImpl<>(List.of(testAvatar2), pageRequestMock, 2);
        when(avatarRepository.findAll(pageRequestMock)).thenReturn(page);

        List<Avatar> result = avatarService.getAllAvatars(2, 1);

        assertThat(result).isEqualTo(page.getContent());
        verify(avatarRepository, times(1)).findAll(pageRequestMock);
    }

    @Test
    void findAvatar_existingId_returnsAvatar() {
        testAvatar1.setId(1L);
        when(avatarRepository.findByStudentId(1L)).thenReturn(Optional.of(testAvatar1));

        Avatar result = avatarService.findAvatar(1L);

        assertThat(result).isEqualTo(testAvatar1);
        verify(avatarRepository, times(1)).findByStudentId(1L);
    }

    @Test
    void findAvatar_nonExistingId_throwsAvatarNotFoundException() {
        testAvatar1.setId(2L);
        when(avatarRepository.findByStudentId(2L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> avatarService.findAvatar(2L));
        verify(avatarRepository, times(1)).findByStudentId(2L);
    }

    @Test
    void downloadAvatar_existingAvatar_returnsResponseEntity() throws IOException {
        testAvatar1.setId(3L);
        testStudent1.setId(3L);
        when(avatarRepository.findByStudentId(3L)).thenReturn(Optional.of(testAvatar1));

        Path testFilePath = Path.of(avatarsDir, "3.jpg");
        Files.createFile(testFilePath);

        ResponseEntity<Resource> responseEntity = avatarService.downloadAvatar(3L);

        assertThat(HttpStatus.OK).isEqualTo(responseEntity.getStatusCode());
        assertThat(testAvatar1.getFileSize()).isEqualTo(responseEntity.getHeaders().getContentLength());
        assertThat(testAvatar1.getMediaType()).isEqualTo(Objects.requireNonNull(responseEntity.getHeaders().getContentType()).toString());

        Files.deleteIfExists(testFilePath);

        verify(avatarRepository, times(1)).findByStudentId(3L);
    }

    @Test
    void downloadAvatar_avatarNotFound_throwsException() {
        testAvatar1.setId(4L);
        testStudent1.setId(4L);
        when(avatarRepository.findByStudentId(4L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> avatarService.downloadAvatar(4L));
        verify(avatarRepository, times(1)).findByStudentId(4L);
    }

    @Test
    void uploadAvatar_validInput_createsAvatar() throws IOException {
        testAvatar1.setId(5L);
        testStudent1.setId(5L);
        testAvatar1.setStudent(testStudent1);
        when(studentService.findStudent(5L)).thenReturn(testStudent1);
        when(avatarRepository.findById(5L)).thenReturn(Optional.empty());
        when(avatarRepository.save(any(Avatar.class))).thenReturn(testAvatar1);

        MultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "test data".getBytes());

        avatarService.uploadAvatar(5L, avatarFile);

        Path expectedFilePath = Path.of(avatarsDir, "5.jpg");
        assertThat(Files.exists(expectedFilePath)).isTrue();
        assertThat(avatarFile.getSize()).isEqualTo(Files.size(expectedFilePath));

        Files.deleteIfExists(expectedFilePath);

        verify(avatarRepository, times(1)).save(any(Avatar.class));
    }

    @Test
    void uploadAvatar_avatarFileIsTooBig_throwsException() {
        testAvatar1.setId(6L);
        testStudent1.setId(6L);
        testAvatar1.setStudent(testStudent1);

        long fileSizeInBytes = 1024 * 500;
        byte[] largeFileContent = new byte[(int) fileSizeInBytes];
        new Random().nextBytes(largeFileContent);

        MultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", largeFileContent);

        assertThatExceptionOfType(AvatarNotAllowedBigFileSizeException.class).isThrownBy(() -> avatarService.uploadAvatar(6L, avatarFile));
        verify(avatarRepository, never()).save(any(Avatar.class));
    }

    @Test
    void deleteAvatar_existingAvatar_deletesAvatarAndFile() throws IOException {
        testAvatar1.setId(7L);
        testStudent1.setId(7L);
        testAvatar1.setFilePath(avatarsDir + "/7.jpg");
        when(avatarRepository.findByStudentId(7L)).thenReturn(Optional.of(testAvatar1));
        doNothing().when(avatarRepository).deleteById(7L);

        Path testFilePath = Path.of(avatarsDir, "7.jpg");
        Files.createFile(testFilePath);
        assertThat(Files.exists(testFilePath)).isTrue();

        avatarService.deleteAvatar(7L);

        verify(avatarRepository, times(1)).deleteById(7L);
        assertThat(Files.exists(testFilePath)).isFalse();
    }

    @Test
    void deleteAvatar_avatarNotFound_throwsException() {
        testAvatar1.setId(8L);
        when(avatarRepository.findByStudentId(8L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> avatarService.deleteAvatar(8L));
        verify(avatarRepository, never()).deleteById(anyLong());
    }
}
