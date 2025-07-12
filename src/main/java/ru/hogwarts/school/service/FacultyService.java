package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public long addFaculty(Faculty faculty) {
        logger.info("Was invoked method to add faculty");

        return facultyRepository.save(faculty).getId();
    }

    public Faculty findFaculty(long id) {
        logger.info("Was invoked method to find faculty");

        return facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("There is not faculty with id = {}", id);

            return new FacultyNotFoundException(id);
        });
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method to update info about faculty");

        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method to delete faculty from db");
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        logger.info("Was invoked method to get all faculties");

        return facultyRepository.findAll();
    }

    public String getLongestFacultiesName() {
        logger.info("Was invoked method to get the longest faculties name");

        return facultyRepository
                .findAll()
                .stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    public Integer getCalculatedSum() {
        logger.info("Was invoked method to get calculated sum from 1 to 1 000 000");

        return IntStream.rangeClosed(1, 1_000_000).parallel().sum();
    }

    public List<Faculty> getFacultiesByNameOrColor(String name, String color) {
        logger.info("Was invoked method to get faculties by name and/or color properties");

        return facultyRepository.findByNameOrColorIgnoreCase(name, color);
    }

    public List<Student> getFacultyStudents(long id) {
        logger.info("Was invoked method to get students on faculty by id property");

        return this.findFaculty(id).getStudents();
    }
}
