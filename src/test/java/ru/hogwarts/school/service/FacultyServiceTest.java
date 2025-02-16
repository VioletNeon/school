package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
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

    {
        mockFaculty1.setName("Gryffindor");
        mockFaculty1.setColor("red");

        mockFaculty2.setName("Slytherin");
        mockFaculty2.setColor("green");
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
    void shouldReturnFacultiesByDefinedColor_ThenReturnTheseFacultiesByCorrespondingColor() {
        mockFaculty1.setId(8L);
        mockFaculty2.setId(9L);
        List<Faculty> mockFacultyList = List.of(mockFaculty2);

        when(facultyRepository.findByColor(mockFaculty2.getColor())).thenReturn(mockFacultyList);

        Collection<Faculty> result = facultyService.getFacultiesByColor(mockFaculty2.getColor());

        assertThat(result).isEqualTo(mockFacultyList);
    }
}
