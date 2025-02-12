package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties;
    private Long lastId = 0L;

    public FacultyService() {
        this.faculties = new HashMap<>();
    }

    public Long addFaculty(Faculty faculty) {
        faculty.setId(++lastId);
        faculties.put(lastId, faculty);

        return faculty.getId();
    }

    public Faculty findFaculty(Long id) {
        Faculty faculty = faculties.get(id);

        if (faculty == null) {
            throw new FacultyNotFoundException(id);
        }

        return faculty;
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (!faculties.containsKey(faculty.getId())) {
            throw new FacultyNotFoundException(faculty.getId());
        }

        this.faculties.put(faculty.getId(), faculty);

        return faculty;
    }

    public void deleteFaculty(Long id) {
        if (!faculties.containsKey(id)) {
            throw new FacultyNotFoundException(id);
        }

        faculties.remove(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return faculties.values();
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        return faculties
                .values()
                .stream()
                .filter((item) -> item.getColor().equals(color))
                .collect(Collectors.toList());
    }
}
