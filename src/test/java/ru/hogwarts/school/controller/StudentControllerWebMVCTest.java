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
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerWebMVCTest {
    private final Student mockStudent1 = new Student();
    private final Student mockStudent2 = new Student();

    {
        mockStudent1.setName("Ivan Ivanovich Ivanov");
        mockStudent1.setAge(17);

        mockStudent2.setName("Petr Petrovich Petrov");
        mockStudent2.setAge(19);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoSpyBean
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    public void shouldAddStudent_ThenReturnThatStudentId() throws Exception {
        JSONObject studentObject = new JSONObject();

        studentObject.put("name", mockStudent1.getName());
        studentObject.put("age", mockStudent1.getAge());

        mockStudent1.setId(1L);
        when(studentRepository.save(any(Student.class))).thenReturn(mockStudent1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/student")
                .content(studentObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(mockStudent1.getId()));

        verify(studentService, times(1)).addStudent(any(Student.class));
    }

    @Test
    public void shouldFindStudentById_ThenReturnThatStudent() throws Exception {
        mockStudent1.setId(2L);
        when(studentRepository.findById(mockStudent1.getId())).thenReturn(Optional.of(mockStudent1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + mockStudent1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockStudent1.getId()))
                .andExpect(jsonPath("$.name").value(mockStudent1.getName()))
                .andExpect(jsonPath("$.age").value(mockStudent1.getAge()));

        verify(studentService, times(1)).findStudent(mockStudent1.getId());
    }

    @Test
    public void shouldFindStudentById_WhenStudentNotExists_ThenThrowStudentNotFoundException() throws Exception {
        mockStudent1.setId(3L);
        when(studentRepository.findById(mockStudent1.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + mockStudent1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).findStudent(mockStudent1.getId());
    }

    @Test
    void shouldUpdateStudent_WhenStudentExists_ThenReturnThatStudent() throws Exception {
        JSONObject studentObject = new JSONObject();

        studentObject.put("name", mockStudent1.getName());
        studentObject.put("age", mockStudent1.getAge());

        mockStudent1.setId(4L);
        when(studentRepository.save(any(Student.class))).thenReturn(mockStudent1);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockStudent1.getId()))
                .andExpect(jsonPath("$.name").value(mockStudent1.getName()))
                .andExpect(jsonPath("$.age").value(mockStudent1.getAge()));

        verify(studentService, times(1)).updateStudent(any(Student.class));
    }

    @Test
    void shouldDeleteStudent_ThenReturnThatStudent() throws Exception {
        mockStudent1.setId(5L);

        Mockito.doNothing().when(studentRepository).deleteById(mockStudent1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/" + mockStudent1.getId())
                )
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(mockStudent1.getId());
    }

    @Test
    void shouldReturnAllStudents_ThenReturnTheseAllStudents() throws Exception {
        mockStudent1.setId(6L);
        mockStudent2.setId(7L);
        List<Student> mockStudentList = List.of(mockStudent1, mockStudent2);

        when(studentRepository.findAll()).thenReturn(mockStudentList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(mockStudent1.getId()))
                .andExpect(jsonPath("$[0].name").value(mockStudent1.getName()))
                .andExpect(jsonPath("$[0].age").value(mockStudent1.getAge()))
                .andExpect(jsonPath("$[1].id").value(mockStudent2.getId()))
                .andExpect(jsonPath("$[1].name").value(mockStudent2.getName()))
                .andExpect(jsonPath("$[1].age").value(mockStudent2.getAge()));

        verify(studentService, times(1)).getAllStudents();

    }

    @Test
    void shouldReturnStudentsByDefinedAge_ThenReturnTheseStudentsByCorrespondingAge() throws Exception {
        mockStudent1.setId(8L);
        mockStudent2.setId(9L);
        List<Student> mockStudentList = List.of(mockStudent2);

        when(studentRepository.findByAge(mockStudent2.getAge())).thenReturn(mockStudentList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/by-age?age=" + mockStudent2.getAge())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(mockStudent2.getId()))
                .andExpect(jsonPath("$[0].name").value(mockStudent2.getName()))
                .andExpect(jsonPath("$[0].age").value(mockStudent2.getAge()));

        verify(studentService, times(1)).getStudentsByAge(mockStudent2.getAge());
    }

    @Test
    void shouldReturnStudentsByDefinedAgeRange_ThenReturnTheseStudentsByCorrespondingAgeRange() throws Exception {
        mockStudent1.setId(10L);
        mockStudent2.setId(11L);
        List<Student> mockStudentList = List.of(mockStudent2);

        when(studentRepository.findByAgeBetween(18, 20)).thenReturn(mockStudentList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/by-age-range?min=18&max=20")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(mockStudent2.getId()))
                .andExpect(jsonPath("$[0].name").value(mockStudent2.getName()))
                .andExpect(jsonPath("$[0].age").value(mockStudent2.getAge()));

        verify(studentService, times(1)).getStudentsByAgeBetween(18, 20);
    }

    @Test
    void shouldReturnFacultyOfStudent_ThenReturnFacultyCorrespondToStudent() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + mockStudent1.getId() + "/faculty")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").value(mockFaculty.getId()))
                .andExpect(jsonPath("$.name").value(mockFaculty.getName()))
                .andExpect(jsonPath("$.color").value(mockFaculty.getColor()));

        verify(studentService, times(1)).getStudentFaculty(mockStudent1.getId());
    }

    @Test
    void shouldReturnFacultyOfStudent_WhenStudentNotExists_ThenThrowStudentNotFoundException() throws Exception {
        mockStudent2.setId(14L);

        when(studentRepository.findById(mockStudent2.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + mockStudent1.getId() + "/faculty")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).findStudent(mockStudent1.getId());
    }
}
