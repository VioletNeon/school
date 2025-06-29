package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacultyServiceTest {
    private final Faculty mockFaculty1 = new Faculty();
    private final Faculty mockFaculty2 = new Faculty();
    private final Faculty mockFaculty3 = new Faculty();

    {
        mockFaculty1.setName("Gryffindor");
        mockFaculty1.setColor("red");

        mockFaculty2.setName("Slytherin");
        mockFaculty2.setColor("green");

        mockFaculty3.setName("Raven claw");
        mockFaculty3.setColor("blue");
    }

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyService facultyService;

    @Test
    void shouldAddFaculty_ThenReturnThatFaculty() {
        mockFaculty1.setId(1L);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(mockFaculty1);

        long result = facultyService.addFaculty(mockFaculty1);

        assertThat(result).isEqualTo(mockFaculty1.getId());

        verify(facultyRepository, times(1)).save(eq(mockFaculty1));
    }

    @Test
    void shouldFindFacultyById_ThenReturnThatFaculty() {
        mockFaculty1.setId(2L);
        when(facultyRepository.findById(mockFaculty1.getId())).thenReturn(Optional.of(mockFaculty1));

        Faculty result = facultyService.findFaculty(mockFaculty1.getId());

        assertThat(result).isEqualTo(mockFaculty1);

        verify(facultyRepository, times(1)).findById(eq(mockFaculty1.getId()));
    }

    @Test
    void shouldFindFacultyById_WhenFacultyNotExists_ThenThrowFacultyNotFoundException() {
        mockFaculty1.setId(3L);
        when(facultyRepository.findById(mockFaculty1.getId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(FacultyNotFoundException.class).isThrownBy(() -> facultyService.findFaculty(mockFaculty1.getId()));

        verify(facultyRepository, times(1)).findById(eq(mockFaculty1.getId()));
    }

    @Test
    void shouldUpdateFaculty_WhenFacultyExists_ThenReturnThatFaculty() {
        mockFaculty1.setId(4L);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(mockFaculty1);

        Faculty result = facultyService.updateFaculty(mockFaculty1);

        assertThat(result).isEqualTo(mockFaculty1);

        verify(facultyRepository, times(1)).save(eq(mockFaculty1));
    }

    @Test
    void shouldDeleteFaculty_ThenReturnThatFaculty() {
        mockFaculty1.setId(5L);

        facultyService.deleteFaculty(mockFaculty1.getId());

        verify(facultyRepository, times(1)).deleteById(eq(mockFaculty1.getId()));
    }

    @Test
    void shouldReturnAllFaculties_ThenReturnTheseAllFaculties() {
        mockFaculty1.setId(6L);
        mockFaculty2.setId(7L);
        List<Faculty> mockFacultyList = List.of(mockFaculty1, mockFaculty2);

        when(facultyRepository.findAll()).thenReturn(mockFacultyList);

        Collection<Faculty> result = facultyService.getAllFaculties();

        assertThat(result).isEqualTo(mockFacultyList);
    }

    @Test
    void shouldReturnFacultiesByDefinedNameOrColor_WhenNameArgumentIsPassed_ThenReturnTheseFacultiesByCorrespondingName() {
        mockFaculty1.setId(8L);
        mockFaculty2.setId(9L);
        List<Faculty> mockFacultyList = List.of(mockFaculty2);

        when(facultyRepository.findByNameOrColorIgnoreCase(mockFaculty2.getName(), null)).thenReturn(mockFacultyList);

        Collection<Faculty> result = facultyService.getFacultiesByNameOrColor(mockFaculty2.getName(), null);

        assertThat(result).isEqualTo(mockFacultyList);
    }

    @Test
    void shouldReturnFacultiesByDefinedNameOrColor_WhenColorArgumentIsPassed_ThenReturnTheseFacultiesByCorrespondingColor() {
        mockFaculty1.setId(10L);
        mockFaculty2.setId(11L);
        List<Faculty> mockFacultyList = List.of(mockFaculty1);

        when(facultyRepository.findByNameOrColorIgnoreCase(null, mockFaculty1.getColor())).thenReturn(mockFacultyList);

        Collection<Faculty> result = facultyService.getFacultiesByNameOrColor(null, mockFaculty1.getColor());

        assertThat(result).isEqualTo(mockFacultyList);
    }

    @Test
    void shouldReturnFacultiesByDefinedNameOrColor_WhenColorAndNameArgumentsArePassed_ThenReturnTheseFacultiesByCorrespondingNameAndColor() {
        mockFaculty1.setId(12L);
        mockFaculty2.setId(13L);
        mockFaculty3.setId(14L);
        List<Faculty> mockFacultyList = List.of(mockFaculty1, mockFaculty3);

        when(facultyRepository.findByNameOrColorIgnoreCase(mockFaculty3.getName(), mockFaculty1.getColor())).thenReturn(mockFacultyList);

        Collection<Faculty> result = facultyService.getFacultiesByNameOrColor(mockFaculty3.getName(), mockFaculty1.getColor());

        assertThat(result).isEqualTo(mockFacultyList);
    }

    @Test
    void shouldReturnStudentsOfFaculty_ThenReturnStudentsCorrespondToFaculty() {
        Student mockStudent1 = new Student();
        mockStudent1.setAge(15);
        mockStudent1.setName("Fedor Fedorovich Fedorov");
        mockStudent1.setId(1L);
        mockStudent1.setFaculty(mockFaculty1);

        Student mockStudent2 = new Student();
        mockStudent2.setAge(13);
        mockStudent2.setName("Semen Semenovich Semenov");
        mockStudent2.setId(2L);
        mockStudent2.setFaculty(mockFaculty1);

        List<Student> mockStudentList = List.of(mockStudent1, mockStudent2);

        mockFaculty1.setId(15L);
        mockFaculty1.setStudents(mockStudentList);

        when(facultyRepository.findById(mockFaculty1.getId())).thenReturn(Optional.of(mockFaculty1));

        Collection<Student> result = facultyService.getFacultyStudents(mockFaculty1.getId());

        assertThat(result).isEqualTo(mockStudentList);

        verify(facultyRepository, times(1)).findById(eq(mockFaculty1.getId()));
    }

    @Test
    void shouldReturnFacultyOfStudent_WhenStudentNotExists_ThenThrowStudentNotFoundException() {
        mockFaculty2.setId(16L);

        when(facultyRepository.findById(mockFaculty2.getId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(FacultyNotFoundException.class).isThrownBy(() -> facultyService.getFacultyStudents(mockFaculty2.getId()));

        verify(facultyRepository, times(1)).findById(eq(mockFaculty2.getId()));
    }

    @Test
    void shouldFindTheLongestFacultiesName_ThenReturnThatFacultyName() {
        mockFaculty1.setId(17L);
        mockFaculty2.setId(18L);
        List<Faculty> mockFacultyList = List.of(mockFaculty1, mockFaculty2);

        when(facultyRepository.findAll()).thenReturn(mockFacultyList);

        String result = facultyService.getLongestFacultiesName();

        assertThat(result).isEqualTo(mockFaculty1.getName());
    }

    @Test
    void shouldReturnSumFromZeroToOneMillion_ThenReturnThatCalculatedSum() {
        Integer result = facultyService.getCalculatedSum();

        assertThat(result).isEqualTo(1784293664);
    }
}
