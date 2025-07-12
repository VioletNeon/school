package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/by-age")
    public List<Student> findStudentsByAge(@RequestParam int age) {
        return studentService.getStudentsByAge(age);
    }

    @GetMapping("/by-age-range")
    public List<Student> getStudentByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.getStudentsByAgeBetween(min, max);
    }

    @GetMapping("{id}/faculty")
    public Faculty getStudentFaculty(@PathVariable long id) {
        return studentService.getStudentFaculty(id);
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable long id) {
        return studentService.findStudent(id);
    }

    @GetMapping("/count")
    public Long getAllStudentsCount() {
        return studentService.getAllStudentsCount();
    }

    @GetMapping("/average-age")
    public int getStudentsAverageAge() {
        return studentService.getStudentsAverageAge();
    }

    @GetMapping("/last")
    public List<Student> getLastStudentsInList() {
        return studentService.getLastStudentsInList();
    }

    @GetMapping("/starts-with-a")
    public List<String> getStudentsStartWithA() {
        return studentService.getStudentsStartWithCharA();
    }

    @GetMapping("/stream-average-age")
    public Integer getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/students/print-parallel")
    public void getPrintParallel() {
        studentService.getPrintParallel();
    }

    @GetMapping("/students/print-synchronized")
    public void getSynchronizedPrint() {
        studentService.getSynchronizedPrint();
    }

    @PostMapping
    public long addStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("{id}")
    public void deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
    }
}
