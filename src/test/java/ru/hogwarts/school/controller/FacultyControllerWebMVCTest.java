package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FacultyControllerWebMVCTest {
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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoSpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    public void shouldAddFaculty_ThenReturnThatFacultyId() throws Exception {
        JSONObject facultyObject = new JSONObject();

        facultyObject.put("name", mockFaculty1.getName());
        facultyObject.put("color", mockFaculty1.getColor());

        mockFaculty1.setId(1L);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(mockFaculty1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/faculty")
                .content(facultyObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(mockFaculty1.getId()));

        verify(facultyService, times(1)).addFaculty(any(Faculty.class));
    }

    @Test
    public void shouldFindFacultyById_ThenReturnThatFaculty() throws Exception {
        mockFaculty1.setId(2L);
        when(facultyRepository.findById(mockFaculty1.getId())).thenReturn(Optional.of(mockFaculty1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + mockFaculty1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockFaculty1.getId()))
                .andExpect(jsonPath("$.name").value(mockFaculty1.getName()))
                .andExpect(jsonPath("$.color").value(mockFaculty1.getColor()));

        verify(facultyService, times(1)).findFaculty(mockFaculty1.getId());
    }

    @Test
    public void shouldFindFacultyById_WhenFacultyNotExists_ThenThrowFacultyNotFoundException() throws Exception {
        mockFaculty1.setId(3L);
        when(facultyRepository.findById(mockFaculty1.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + mockFaculty1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(facultyService, times(1)).findFaculty(mockFaculty1.getId());
    }

    @Test
    void shouldUpdateFaculty_WhenFacultyExists_ThenReturnThatFaculty() throws Exception {
        JSONObject studentObject = new JSONObject();

        studentObject.put("name", mockFaculty1.getName());
        studentObject.put("color", mockFaculty1.getColor());

        mockFaculty1.setId(4L);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(mockFaculty1);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockFaculty1.getId()))
                .andExpect(jsonPath("$.name").value(mockFaculty1.getName()))
                .andExpect(jsonPath("$.color").value(mockFaculty1.getColor()));

        verify(facultyService, times(1)).updateFaculty(any(Faculty.class));
    }

    @Test
    void shouldDeleteFaculty_ThenReturnThatFaculty() throws Exception {
        mockFaculty1.setId(5L);

        Mockito.doNothing().when(facultyRepository).deleteById(mockFaculty1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + mockFaculty1.getId())
                )
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFaculty(mockFaculty1.getId());
    }

    @Test
    void shouldReturnAllFaculties_ThenReturnTheseAllFaculties() throws Exception {
        mockFaculty1.setId(6L);
        mockFaculty2.setId(7L);
        List<Faculty> mockFacultyList = List.of(mockFaculty1, mockFaculty2);

        when(facultyRepository.findAll()).thenReturn(mockFacultyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(mockFaculty1.getId()))
                .andExpect(jsonPath("$[0].name").value(mockFaculty1.getName()))
                .andExpect(jsonPath("$[0].color").value(mockFaculty1.getColor()))
                .andExpect(jsonPath("$[1].id").value(mockFaculty2.getId()))
                .andExpect(jsonPath("$[1].name").value(mockFaculty2.getName()))
                .andExpect(jsonPath("$[1].color").value(mockFaculty2.getColor()));

        verify(facultyService, times(1)).getAllFaculties();

    }

    @Test
    void shouldReturnFacultiesByDefinedNameOrColor_WhenNameArgumentIsPassed_ThenReturnTheseFacultiesByCorrespondingName() throws Exception {
        mockFaculty1.setId(8L);
        mockFaculty2.setId(9L);
        List<Faculty> mockFacultyList = List.of(mockFaculty2);

        when(facultyRepository.findByNameOrColorIgnoreCase(mockFaculty2.getName(), null)).thenReturn(mockFacultyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-name-or-color?name=" + mockFaculty2.getName())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(mockFaculty2.getId()))
                .andExpect(jsonPath("$[0].name").value(mockFaculty2.getName()))
                .andExpect(jsonPath("$[0].color").value(mockFaculty2.getColor()));

        verify(facultyService, times(1)).getFacultiesByNameOrColor(mockFaculty2.getName(), null);
    }

    @Test
    void shouldReturnFacultiesByDefinedNameOrColor_WhenColorArgumentIsPassed_ThenReturnTheseFacultiesByCorrespondingColor() throws Exception {
        mockFaculty1.setId(10L);
        mockFaculty2.setId(11L);
        List<Faculty> mockFacultyList = List.of(mockFaculty1);

        when(facultyRepository.findByNameOrColorIgnoreCase(null, mockFaculty1.getColor())).thenReturn(mockFacultyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-name-or-color?color=" + mockFaculty1.getColor())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(mockFaculty1.getId()))
                .andExpect(jsonPath("$[0].name").value(mockFaculty1.getName()))
                .andExpect(jsonPath("$[0].color").value(mockFaculty1.getColor()));

        verify(facultyService, times(1)).getFacultiesByNameOrColor(null, mockFaculty1.getColor());
    }

    @Test
    void shouldReturnFacultiesByDefinedNameOrColor_WhenColorAndNameArgumentsArePassed_ThenReturnTheseFacultiesByCorrespondingNameAndColor() throws Exception {
        mockFaculty1.setId(12L);
        mockFaculty2.setId(13L);
        mockFaculty3.setId(14L);
        List<Faculty> mockFacultyList = List.of(mockFaculty1, mockFaculty3);

        when(facultyRepository.findByNameOrColorIgnoreCase(mockFaculty3.getName(), mockFaculty1.getColor())).thenReturn(mockFacultyList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-name-or-color?color=" + mockFaculty1.getColor() + "&name=" + mockFaculty3.getName())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(mockFaculty1.getId()))
                .andExpect(jsonPath("$[0].name").value(mockFaculty1.getName()))
                .andExpect(jsonPath("$[0].color").value(mockFaculty1.getColor()))
                .andExpect(jsonPath("$[1].id").value(mockFaculty3.getId()))
                .andExpect(jsonPath("$[1].name").value(mockFaculty3.getName()))
                .andExpect(jsonPath("$[1].color").value(mockFaculty3.getColor()));

        verify(facultyService, times(1)).getFacultiesByNameOrColor(mockFaculty3.getName(), mockFaculty1.getColor());

    }

    @Test
    void shouldReturnStudentsOfFaculty_ThenReturnStudentsCorrespondToFaculty() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + mockFaculty1.getId() + "/student")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(mockStudent1.getId()))
                .andExpect(jsonPath("$[0].name").value(mockStudent1.getName()))
                .andExpect(jsonPath("$[0].age").value(mockStudent1.getAge()))
                .andExpect(jsonPath("$[1].id").value(mockStudent2.getId()))
                .andExpect(jsonPath("$[1].name").value(mockStudent2.getName()))
                .andExpect(jsonPath("$[1].age").value(mockStudent2.getAge()));

        verify(facultyService, times(1)).getFacultyStudents(mockFaculty1.getId());
    }

    @Test
    void shouldReturnFacultyOfStudent_WhenStudentNotExists_ThenThrowStudentNotFoundException() throws Exception {
        mockFaculty2.setId(16L);

        when(facultyRepository.findById(mockFaculty2.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + mockFaculty1.getId() + "/student")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(facultyService, times(1)).findFaculty(mockFaculty1.getId());
    }
}
