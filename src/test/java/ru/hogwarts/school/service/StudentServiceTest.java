package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    private final Student mockStudent1 = new Student();
    private final Student mockStudent2 = new Student();

    {
        mockStudent1.setName("Ivan Ivanovich Ivanov");
        mockStudent1.setAge(17);

        mockStudent2.setName("Petr Petrovich Petrov");
        mockStudent2.setAge(19);
    }

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void shouldAddStudent_ThenReturnThatStudent() {
        mockStudent1.setId(1L);
        when(studentRepository.save(any(Student.class))).thenReturn(mockStudent1);

        long result = studentService.addStudent(mockStudent1);

        assertThat(result).isEqualTo(mockStudent1.getId());

        verify(studentRepository, times(1)).save(eq(mockStudent1));
    }

    @Test
    void shouldFindStudentById_ThenReturnThatStudent() {
        mockStudent1.setId(2L);
        when(studentRepository.findById(mockStudent1.getId())).thenReturn(Optional.of(mockStudent1));

        Student result = studentService.findStudent(mockStudent1.getId());

        assertThat(result).isEqualTo(mockStudent1);

        verify(studentRepository, times(1)).findById(eq(mockStudent1.getId()));
    }

    @Test
    void shouldFindStudentById_WhenStudentNotExists_ThenThrowStudentNotFoundException() {
        mockStudent1.setId(3L);
        when(studentRepository.findById(mockStudent1.getId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> studentService.findStudent(mockStudent1.getId()));

        verify(studentRepository, times(1)).findById(eq(mockStudent1.getId()));
    }

    @Test
    void shouldUpdateStudent_WhenStudentExists_ThenReturnThatStudent() {
        mockStudent1.setId(4L);
        when(studentRepository.save(any(Student.class))).thenReturn(mockStudent1);

        Student result = studentService.updateStudent(mockStudent1);

        assertThat(result).isEqualTo(mockStudent1);

        verify(studentRepository, times(1)).save(eq(mockStudent1));
    }

    @Test
    void shouldDeleteStudent_ThenReturnThatStudent() {
        mockStudent1.setId(5L);

        studentService.deleteStudent(mockStudent1.getId());

        verify(studentRepository, times(1)).deleteById(eq(mockStudent1.getId()));
    }

    @Test
    void shouldReturnAllStudents_ThenReturnTheseAllStudents() {
        mockStudent1.setId(6L);
        mockStudent2.setId(7L);
        List<Student> mockStudentList = List.of(mockStudent1, mockStudent2);

        when(studentRepository.findAll()).thenReturn(mockStudentList);

        Collection<Student> result = studentService.getAllStudents();

        assertThat(result).isEqualTo(mockStudentList);
    }

    @Test
    void shouldReturnStudentsByDefinedAge_ThenReturnTheseStudentsByCorrespondingAge() {
        mockStudent1.setId(8L);
        mockStudent2.setId(9L);
        List<Student> mockStudentList = List.of(mockStudent2);

        when(studentRepository.findByAge(mockStudent2.getAge())).thenReturn(mockStudentList);

        Collection<Student> result = studentService.getStudentsByAge(mockStudent2.getAge());

        assertThat(result).isEqualTo(mockStudentList);
    }

    @Test
    void shouldReturnStudentsByDefinedAgeRange_ThenReturnTheseStudentsByCorrespondingAgeRange() {
        mockStudent1.setId(10L);
        mockStudent2.setId(11L);
        List<Student> mockStudentList = List.of(mockStudent2);

        when(studentRepository.findByAgeBetween(18, 20)).thenReturn(mockStudentList);

        Collection<Student> result = studentService.getStudentsByAgeBetween(18, 20);

        assertThat(result).isEqualTo(mockStudentList);
    }

    @Test
    void shouldReturnFacultyOfStudent_ThenReturnFacultyCorrespondToStudent() {
        mockStudent1.setId(12L);
        mockStudent2.setId(13L);

        List<Student> mockStudentList = List.of(mockStudent1);
        Faculty mockFaculty = new Faculty();
        mockFaculty.setColor("red");
        mockFaculty.setName("Gryffindor");
        mockFaculty.setId(1L);
        mockFaculty.setStudents(mockStudentList);

        mockStudent1.setFaculty(mockFaculty);

        when(studentRepository.findById(mockStudent1.getId())).thenReturn(Optional.of(mockStudent1));

        Faculty result = studentService.getStudentFaculty(mockStudent1.getId());

        assertThat(result).isEqualTo(mockFaculty);

        verify(studentRepository, times(1)).findById(eq(mockStudent1.getId()));
    }

    @Test
    void shouldReturnFacultyOfStudent_WhenStudentNotExists_ThenThrowStudentNotFoundException() {
        mockStudent2.setId(14L);

        when(studentRepository.findById(mockStudent2.getId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(StudentNotFoundException.class).isThrownBy(() -> studentService.getStudentFaculty(mockStudent2.getId()));

        verify(studentRepository, times(1)).findById(eq(mockStudent2.getId()));
    }
}
