package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public long addStudent(Student student) {
        return studentRepository.save(student).getId();
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Collection<Student> getStudentsByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getStudentFaculty(long id) {
        return this.findStudent(id).getFaculty();
    }
}
