package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public long addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty).getId();
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public Faculty updateFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> getFacultiesByNameOrColor(String name, String color) {
        return facultyRepository.findByNameOrColorIgnoreCase(name, color);
    }

    public Collection<Student> getFacultyStudents(long id) {
        return this.findFaculty(id).getStudents();
    }
}
