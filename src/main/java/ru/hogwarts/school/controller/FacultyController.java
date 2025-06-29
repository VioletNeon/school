package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @GetMapping("/by-name-or-color")
    public List<Faculty> findFacultiesByNameOrColor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color
    ) {
        return facultyService.getFacultiesByNameOrColor(name, color);
    }

    @GetMapping("{id}/student")
    public List<Student> getFacultyStudents(@PathVariable long id) {
        return facultyService.getFacultyStudents(id);
    }

    @GetMapping("{id}")
    public Faculty getFaculty(@PathVariable long id) {
        return facultyService.findFaculty(id);
    }

    @GetMapping("/longest-name")
    public String getLongestFacultiesName() {
        return facultyService.getLongestFacultiesName();
    }

    @GetMapping("/sum")
    public Integer getCalculatedSum() {
        return facultyService.getCalculatedSum();
    }

    @PostMapping
    public long addFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @PutMapping
    public Faculty updateFaculty(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
    }

    @DeleteMapping("{id}")
    public void deleteFaculty(@PathVariable long id) {
        facultyService.deleteFaculty(id);
    }
}
