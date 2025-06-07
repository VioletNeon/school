package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public long addStudent(Student student) {
        logger.info("Was invoked method to add student");

        return studentRepository.save(student).getId();
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method to find student");

        return studentRepository.findById(id).orElseThrow(() -> {
            logger.error("There is not student with id = {}", id);

            return new StudentNotFoundException(id);
        });
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method to update info about student");

        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method to delete student from db");
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        logger.info("Was invoked method to get all students");

        return studentRepository.findAll();
    }

    public List<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method to get student by age property");

        return studentRepository.findByAge(age);
    }

    public List<Student> getStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method to get students by min and max age range property");

        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getStudentFaculty(long id) {
        logger.info("Was invoked method to get student faculty by student id property");

        return this.findStudent(id).getFaculty();
    }

    public Long getAllStudentsCount() {
        logger.info("Was invoked method to get a count of all students");

        return studentRepository.getStudentsCount();
    }

    public int getStudentsAverageAge() {
        logger.info("Was invoked method to get students average age");

        return studentRepository.getAverageStudentsAge();
    }

    public List<Student> getLastStudentsInList() {
        logger.info("Was invoked method to get a list of the last students");

        return studentRepository.getLastStudentsInList();
    }
}
