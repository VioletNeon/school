package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> students;
    private Long lastId = 0L;

    public StudentService() {
        this.students = new HashMap<>();
    }

    public Long addStudent(Student student) {
        student.setId(++lastId);
        students.put(lastId, student);

        return student.getId();
    }

    public Student findStudent(Long id) {
        Student student = students.get(id);

        if (student == null) {
            throw new StudentNotFoundException(id);
        }

        return student;
    }

    public Student updateStudent(Student student) {
        if (!students.containsKey(student.getId())) {
            throw new StudentNotFoundException(student.getId());
        }

        this.students.put(student.getId(), student);

        return student;
    }

    public void deleteStudent(Long id) {
        if (!students.containsKey(id)) {
            throw new StudentNotFoundException(id);
        }

        students.remove(id);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }

    public Collection<Student> getStudentsByAge(int age) {
        return students
                .values()
                .stream()
                .filter((item) -> item.getAge() == age)
                .collect(Collectors.toList());
    }
}
