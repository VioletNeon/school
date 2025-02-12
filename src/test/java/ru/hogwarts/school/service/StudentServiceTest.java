package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StudentServiceTest {
    private final StudentService out = new StudentService();
    private final Student mockStudent1 = new Student(1L, "Ivan Ivanovich Ivanov", 17);
    private final Student mockStudent2 = new Student(2L, "Petr Petrovich Petrov", 19);

    @Test
    void shouldAddStudent_ThenReturnThatStudent() {
        Long result = out.addStudent(mockStudent1);
        Collection<Student> allStudents = out.getAllStudents();

        assertThat(result).isEqualTo(mockStudent1.getId());
        assertThat(allStudents).contains(mockStudent1);
        assertThat(allStudents).hasSize(1);
    }

    @Test
    void shouldFindStudentById_ThenReturnThatStudent() {
        Long expected = out.addStudent(mockStudent1);
        Student result = out.findStudent(expected);

        assertThat(result).isEqualTo(mockStudent1);
        assertThat(result.getId()).isEqualTo(expected);
    }

    @Test
    void shouldFindStudentById_WhenStudentNotExists_ThenThrowStudentNotFoundException() {
        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> out.findStudent(mockStudent1.getId()));
    }

    @Test
    void shouldUpdateStudent_WhenStudentExists_ThenReturnThatStudent() {
        Long mockStudent3Id = out.addStudent(mockStudent1);
        Student mockStudent3 = new Student(mockStudent3Id, "Sidr Sidorovich Sidorov", 25);

        Student result = out.updateStudent(mockStudent3);

        assertThat(result).isEqualTo(mockStudent3);
    }

    @Test
    void shouldUpdateStudent_WhenStudentNotExists_ThenThrowStudentNotFoundException() {
        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> out.updateStudent(mockStudent1));
    }

    @Test
    void shouldDeleteStudent_ThenReturnThatStudent() {
        Long expected = out.addStudent(mockStudent1);
        out.deleteStudent(expected);
        Collection<Student> allStudents = out.getAllStudents();

        assertThat(allStudents).hasSize(0);
    }

    @Test
    void shouldDeleteStudent_WhenStudentNotExists_ThenThrowStudentNotFoundException() {
        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> out.deleteStudent(1L));
    }

    @Test
    void shouldReturnAllStudents_ThenReturnTheseAllStudents() {
        Long result1 = out.addStudent(mockStudent1);
        Long result2 = out.addStudent(mockStudent2);
        Collection<Student> allStudents = out.getAllStudents();

        assertThat(result1).isEqualTo(mockStudent1.getId());
        assertThat(result2).isEqualTo(mockStudent2.getId());
        assertThat(allStudents).hasSize(2);
    }
}
