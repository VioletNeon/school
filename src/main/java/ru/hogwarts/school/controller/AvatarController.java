package ru.hogwarts.school.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping
    public List<Avatar> getAllAvatars(@RequestParam("page") Integer pageNumber, @RequestParam("size") Integer pageSize) {
        return avatarService.getAllAvatars(pageNumber, pageSize);
    }

    @GetMapping(value = "/{studentId}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatarFromDb(@PathVariable Long studentId) {
        Avatar avatar = avatarService.findAvatar(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getFileSize());

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{studentId}/avatar")
    public ResponseEntity<Resource> downloadAvatar(@PathVariable Long studentId) throws IOException {
        return avatarService.downloadAvatar(studentId);
    }

    @PostMapping(value = "/{studentId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @PathVariable Long studentId,
            @RequestParam MultipartFile avatar
    ) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }

        avatarService.uploadAvatar(studentId, avatar);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{studentId}")
    public void deleteAvatar(@PathVariable long studentId) throws IOException {
        avatarService.deleteAvatar(studentId);
    }
}
