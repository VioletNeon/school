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
    private final Student mockStudent3 = new Student();
    private final Student mockStudent4 = new Student();
    private final Student mockStudent5 = new Student();
    private final Student mockStudent6 = new Student();

    {
        mockStudent1.setName("Ivan Ivanovich Ivanov");
        mockStudent1.setAge(17);

        mockStudent2.setName("Petr Petrovich Petrov");
        mockStudent2.setAge(19);

        mockStudent3.setName("Sergey Sergeevich Sergeev");
        mockStudent3.setAge(16);

        mockStudent4.setName("Anton Antonovich Antonov");
        mockStudent4.setAge(17);

        mockStudent5.setName("Oleg Olegovich Olegov");
        mockStudent5.setAge(19);

        mockStudent5.setName("Fedor Fedorovich Fedorov");
        mockStudent5.setAge(18);
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

    @Test
    void shouldReturnCountOfAllStudents_ThenReturnStudentsCount() {
        when(studentRepository.getStudentsCount()).thenReturn(2L);

        Long result = studentService.getAllStudentsCount();

        assertThat(result).isEqualTo(2L);

        verify(studentRepository, times(1)).getStudentsCount();
    }

    @Test
    void shouldReturnAverageAgeOfAllStudents_ThenReturnAverageAgeOfAllStudents() {
        when(studentRepository.getAverageStudentsAge()).thenReturn(17);

        int result = studentService.getStudentsAverageAge();

        assertThat(result).isEqualTo(17);

        verify(studentRepository, times(1)).getAverageStudentsAge();
    }

    @Test
    void shouldReturnLastFiveStudents_ThenReturnLastFiveStudentsList() {
        mockStudent1.setId(17L);
        mockStudent2.setId(18L);
        mockStudent3.setId(19L);
        mockStudent4.setId(20L);
        mockStudent5.setId(21L);

        when(studentRepository.getLastStudentsInList()).thenReturn(List.of(mockStudent5, mockStudent4, mockStudent3, mockStudent2, mockStudent1));

        List<Student> result = studentService.getLastStudentsInList();

        assertThat(result).isEqualTo(List.of(mockStudent5, mockStudent4, mockStudent3, mockStudent2, mockStudent1));

        verify(studentRepository, times(1)).getLastStudentsInList();
    }

    @Test
    void shouldFindStudentsWithNamesStartWithA_ThenReturnThatStudentsListNames() {
        mockStudent4.setId(8L);
        mockStudent5.setId(9L);
        List<Student> mockStudentList = List.of(mockStudent4, mockStudent5);

        when(studentRepository.findAll()).thenReturn(mockStudentList);

        List<String> result = studentService.getStudentsStartWithCharA();

        assertThat(result).hasSize(1);
        assertThat(result).contains(mockStudent4.getName().toUpperCase());
    }

    @Test
    void shouldFindStudentsAverageAgeUsingStreamAPI_ThenReturnThatStudentsAverageAge() {
        mockStudent4.setId(8L);
        mockStudent5.setId(9L);
        List<Student> mockStudentList = List.of(mockStudent4, mockStudent5);

        when(studentRepository.findAll()).thenReturn(mockStudentList);

        Integer result = studentService.getAverageAge();

        assertThat(result).isEqualTo((mockStudent4.getAge() + mockStudent5.getAge()) / 2);
    }

    @Test
    void getPrintParallel_ShouldPrintStudentNames() {
        mockStudent1.setId(10L);
        mockStudent2.setId(11L);
        mockStudent3.setId(12L);
        mockStudent4.setId(13L);
        mockStudent5.setId(14L);
        mockStudent6.setId(15L);

        when(studentRepository.findAll()).thenReturn(List.of(
                mockStudent1,
                mockStudent2,
                mockStudent3,
                mockStudent4,
                mockStudent5,
                mockStudent6
        ));

        studentService.getPrintParallel();

        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getSynchronizedPrint_ShouldPrintStudentNamesSynchronized() {
        mockStudent1.setId(16L);
        mockStudent2.setId(17L);
        mockStudent3.setId(18L);
        mockStudent4.setId(19L);
        mockStudent5.setId(20L);
        mockStudent6.setId(21L);

        when(studentRepository.findAll()).thenReturn(List.of(
                mockStudent1,
                mockStudent2,
                mockStudent3,
                mockStudent4,
                mockStudent5,
                mockStudent6
        ));

        studentService.getSynchronizedPrint();

        verify(studentRepository, times(1)).findAll();
    }
}
