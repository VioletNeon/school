package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FacultyServiceTest {
    private final FacultyService out = new FacultyService();
    private final Faculty mockFaculty1 = new Faculty(1L, "Gryffindor", "red");
    private final Faculty mockFaculty2 = new Faculty(2L, "Slytherin", "green");

    @Test
    void shouldAddFaculty_ThenReturnThatFaculty() {
        Long result = out.addFaculty(mockFaculty1);
        Collection<Faculty> allFaculties = out.getAllFaculties();

        assertThat(result).isEqualTo(mockFaculty1.getId());
        assertThat(allFaculties).contains(mockFaculty1);
        assertThat(allFaculties).hasSize(1);
    }

    @Test
    void shouldFindFacultyById_ThenReturnThatFaculty() {
        Long expected = out.addFaculty(mockFaculty1);
        Faculty result = out.findFaculty(expected);

        assertThat(result).isEqualTo(mockFaculty1);
        assertThat(result.getId()).isEqualTo(expected);
    }

    @Test
    void shouldFindFacultyById_WhenFacultyNotExists_ThenThrowFacultyNotFoundException() {
        assertThatExceptionOfType(FacultyNotFoundException.class).isThrownBy(() -> out.findFaculty(mockFaculty1.getId()));
    }

    @Test
    void shouldUpdateFaculty_WhenFacultyExists_ThenReturnThatFaculty() {
        Long mockFaculty3Id = out.addFaculty(mockFaculty1);
        Faculty mockFaculty3 = new Faculty(mockFaculty3Id, "Ravenclaw", "blue");

        Faculty result = out.updateFaculty(mockFaculty3);

        assertThat(result).isEqualTo(mockFaculty3);
    }

    @Test
    void shouldUpdateFaculty_WhenFacultyNotExists_ThenThrowFacultyNotFoundException() {
        assertThatExceptionOfType(FacultyNotFoundException.class).isThrownBy(() -> out.updateFaculty(mockFaculty1));
    }

    @Test
    void shouldDeleteFaculty_ThenReturnThatFaculty() {
        Long expected = out.addFaculty(mockFaculty1);
        out.deleteFaculty(expected);
        Collection<Faculty> allFaculties = out.getAllFaculties();

        assertThat(allFaculties).hasSize(0);
    }

    @Test
    void shouldDeleteFaculty_WhenFacultyNotExists_ThenThrowFacultyNotFoundException() {
        assertThatExceptionOfType(FacultyNotFoundException.class).isThrownBy(() -> out.deleteFaculty(1L));
    }

    @Test
    void shouldReturnAllFaculties_ThenReturnTheseAllFaculties() {
        Long result1 = out.addFaculty(mockFaculty1);
        Long result2 = out.addFaculty(mockFaculty2);
        Collection<Faculty> allFaculties = out.getAllFaculties();

        assertThat(result1).isEqualTo(mockFaculty1.getId());
        assertThat(result2).isEqualTo(mockFaculty2.getId());
        assertThat(allFaculties).hasSize(2);
    }
}
