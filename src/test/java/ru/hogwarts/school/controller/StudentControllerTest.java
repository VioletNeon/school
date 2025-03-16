package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {
    private Student mockStudent1 = new Student();
    private Student mockStudent2 = new Student();

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();

        mockStudent1 = new Student();
        mockStudent1.setName("Ivan Ivanovich Ivanov");
        mockStudent1.setAge(17);

        mockStudent2 = new Student();
        mockStudent2.setName("Petr Petrovich Petrov");
        mockStudent2.setAge(19);
    }

    @Test
    public void contextLoads() {
        assertThat(studentController).isNotNull();
        assertThat(facultyController).isNotNull();
    }

    @Test
    void shouldAddStudent_ThenReturnThatStudentIdAndFindThatStudentById() throws JsonProcessingException {
        String jsonResponse = this.restTemplate.postForObject("http://localhost:" + port + "/student", mockStudent1, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);

        long id = root.asLong();
        assertThat(id).isNotZero();

        mockStudent1.setId(id);

        Student result = this.restTemplate.getForObject("http://localhost:" + port + "/student/" + mockStudent1.getId(), Student.class);

        assertThat(result).isEqualTo(mockStudent1);
    }

    @Test
    void shouldFindStudentById_WhenStudentNotExists_ThenThrowStudentNotFoundException() throws StudentNotFoundException {
        ResponseEntity<Student> response = this.restTemplate.getForEntity("http://localhost:" + port + "/student/" + 55L, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateStudent_ThenReturnThatStudent() throws JsonProcessingException {
        String jsonResponse = this.restTemplate.postForObject("http://localhost:" + port + "/student", mockStudent1, String.class);

        ObjectMapper mapper = new ObjectMapper();
        long id = mapper.readTree(jsonResponse).asLong();

        Student result1 = this.restTemplate.getForObject("http://localhost:" + port + "/student/" + id, Student.class);

        mockStudent1.setId(id);
        mockStudent2.setId(id);

        assertThat(result1).isEqualTo(mockStudent1);
        assertThat(result1).isNotEqualTo(mockStudent2);

        this.restTemplate.put("http://localhost:" + port + "/student", mockStudent2);

        Student result2 = this.restTemplate.getForObject("http://localhost:" + port + "/student/" + mockStudent2.getId(), Student.class);

        assertThat(result2).isEqualTo(mockStudent2);
        assertThat(result2).isNotEqualTo(mockStudent1);
    }

    @Test
    void shouldDeleteStudent() throws JsonProcessingException {
        String jsonResponse = this.restTemplate.postForObject("http://localhost:" + port + "/student", mockStudent1, String.class);

        ObjectMapper mapper = new ObjectMapper();
        long id = mapper.readTree(jsonResponse).asLong();

        mockStudent1.setId(id);

        Student result1 = this.restTemplate.getForObject("http://localhost:" + port + "/student/" + id, Student.class);

        assertThat(result1).isEqualTo(mockStudent1);

        this.restTemplate.delete("http://localhost:" + port + "/student/" + id);

        ResponseEntity<Student> response = this.restTemplate.getForEntity("http://localhost:" + port + "/student/" + id, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFindStudentFaculty_ThenReturnThatStudentFaculty() {
        Faculty mockFaculty = new Faculty();
        mockFaculty.setColor("red");
        mockFaculty.setName("Gryffindor");

        long studentId = studentController.addStudent(mockStudent1);

        mockStudent1.setId(studentId);

        List<Student> mockStudentList = List.of(mockStudent1);
        mockFaculty.setStudents(mockStudentList);

        long facultyId = facultyController.addFaculty(mockFaculty);

        mockFaculty.setId(facultyId);

        mockStudent1.setFaculty(mockFaculty);

        facultyController.updateFaculty(mockFaculty);
        studentController.updateStudent(mockStudent1);

        Faculty result = this.restTemplate.getForObject("http://localhost:" + port + "/student/" + studentId + "/faculty", Faculty.class);

        assertThat(result).isEqualTo(mockFaculty);
    }

    @Test
    void shouldFindStudentCollectionFilteredByAgeRange_ThenReturnThatStudentCollection() {
        long student1Id = studentController.addStudent(mockStudent1);
        long student2Id = studentController.addStudent(mockStudent2);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);

        ResponseEntity<List<Student>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/student/by-age-range?min=" + 16 + "&max=" + 18,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        List<Student> result = response.getBody();

        List<Student> expectedStudentList = List.of(mockStudent1);

        assertThat(result).isEqualTo(expectedStudentList);
    }

    @Test
    void shouldFindStudentCollectionFilteredByAge_ThenReturnThatStudentCollection() {
        long student1Id = studentController.addStudent(mockStudent1);
        long student2Id = studentController.addStudent(mockStudent2);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);

        ResponseEntity<List<Student>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/student/by-age?age=" + mockStudent2.getAge(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        List<Student> result = response.getBody();

        List<Student> expectedStudentList = List.of(mockStudent2);

        assertThat(result).isEqualTo(expectedStudentList);
    }

    @Test
    void shouldReturnAllStudentCollection_ThenReturnThatStudentCollection() {
        long student1Id = studentController.addStudent(mockStudent1);
        long student2Id = studentController.addStudent(mockStudent2);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);

        ResponseEntity<List<Student>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        List<Student> result = response.getBody();

        List<Student> expectedStudentList = List.of(mockStudent1, mockStudent2);

        assertThat(result).isEqualTo(expectedStudentList);
    }
}
