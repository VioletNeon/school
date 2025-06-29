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
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {
    private Faculty mockFaculty1 = new Faculty();
    private Faculty mockFaculty2 = new Faculty();

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();

        mockFaculty1 = new Faculty();
        mockFaculty1.setName("Gryffindor");
        mockFaculty1.setColor("red");

        mockFaculty2 = new Faculty();
        mockFaculty2.setName("Slytherin");
        mockFaculty2.setColor("green");
    }

    @Test
    public void contextLoads() {
        assertThat(studentController).isNotNull();
        assertThat(facultyController).isNotNull();
    }

    @Test
    void shouldAddFaculty_ThenReturnThatFacultyIdAndFindThatFacultyById() throws JsonProcessingException {
        String jsonResponse = this.restTemplate.postForObject("http://localhost:" + port + "/faculty", mockFaculty1, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);

        long id = root.asLong();
        assertThat(id).isNotZero();

        mockFaculty1.setId(id);

        Faculty result = this.restTemplate.getForObject("http://localhost:" + port + "/faculty/" + mockFaculty1.getId(), Faculty.class);

        assertThat(result).isEqualTo(mockFaculty1);
    }

    @Test
    void shouldFindFacultyById_WhenFacultyNotExists_ThenThrowFacultyNotFoundException() throws FacultyNotFoundException {
        ResponseEntity<Faculty> response = this.restTemplate.getForEntity("http://localhost:" + port + "/faculty/" + 33L, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateFaculty_ThenReturnThatFaculty() throws JsonProcessingException {
        String jsonResponse = this.restTemplate.postForObject("http://localhost:" + port + "/faculty", mockFaculty1, String.class);

        ObjectMapper mapper = new ObjectMapper();
        long id = mapper.readTree(jsonResponse).asLong();

        Faculty result1 = this.restTemplate.getForObject("http://localhost:" + port + "/faculty/" + id, Faculty.class);

        mockFaculty1.setId(id);
        mockFaculty2.setId(id);

        assertThat(result1).isEqualTo(mockFaculty1);
        assertThat(result1).isNotEqualTo(mockFaculty2);

        this.restTemplate.put("http://localhost:" + port + "/faculty", mockFaculty2);

        Faculty result2 = this.restTemplate.getForObject("http://localhost:" + port + "/faculty/" + mockFaculty2.getId(), Faculty.class);

        assertThat(result2).isEqualTo(mockFaculty2);
        assertThat(result2).isNotEqualTo(mockFaculty1);
    }

    @Test
    void shouldDeleteFaculty() throws JsonProcessingException {
        String jsonResponse = this.restTemplate.postForObject("http://localhost:" + port + "/faculty", mockFaculty1, String.class);

        ObjectMapper mapper = new ObjectMapper();
        long id = mapper.readTree(jsonResponse).asLong();

        mockFaculty1.setId(id);

        Faculty result1 = this.restTemplate.getForObject("http://localhost:" + port + "/faculty/" + id, Faculty.class);

        assertThat(result1).isEqualTo(mockFaculty1);

        this.restTemplate.delete("http://localhost:" + port + "/faculty/" + id);

        ResponseEntity<Faculty> response = this.restTemplate.getForEntity("http://localhost:" + port + "/faculty/" + id, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFindFacultyStudents_ThenReturnThatFacultyStudents() {
        Student mockStudent = new Student();
        mockStudent.setName("Ivan Ivanovich Ivanov");
        mockStudent.setAge(17);

        long studentId = studentController.addStudent(mockStudent);

        mockStudent.setId(studentId);

        long facultyId = facultyController.addFaculty(mockFaculty1);

        mockFaculty1.setId(facultyId);

        mockStudent.setFaculty(mockFaculty1);

        List<Student> mockStudentList = List.of(mockStudent);
        mockFaculty1.setStudents(mockStudentList);

        studentController.updateStudent(mockStudent);
        facultyController.updateFaculty(mockFaculty1);

        ResponseEntity<List<Student>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + facultyId + "/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        List<Student> result = response.getBody();

        assertThat(result).isEqualTo(mockStudentList);
    }

    @Test
    void shouldFindTheLongestFacultiesName_ThenReturnThatFacultyName() {
        long faculty1Id = facultyController.addFaculty(mockFaculty1);
        long faculty2Id = facultyController.addFaculty(mockFaculty2);

        mockFaculty1.setId(faculty1Id);
        mockFaculty2.setId(faculty2Id);

        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/faculty/longest-name",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<String>() {}
        );

        String result = response.getBody();

        assertThat(result).isEqualTo(mockFaculty1.getName());
    }

    @Test
    void shouldReturnSumFromZeroToOneMillion_ThenReturnThatCalculatedSum() {
        ResponseEntity<Integer> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/faculty/sum",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Integer>() {}
        );

        Integer result = response.getBody();

        assertThat(result).isEqualTo(1784293664);
    }

    @Test
    void shouldFindFacultyCollectionFilteredByName_ThenReturnThatFacultyCollection() {
        long faculty1Id = facultyController.addFaculty(mockFaculty1);
        long faculty2Id = facultyController.addFaculty(mockFaculty2);

        mockFaculty1.setId(faculty1Id);
        mockFaculty2.setId(faculty2Id);

        ResponseEntity<List<Faculty>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/faculty/by-name-or-color?color=&name=" + mockFaculty1.getName(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        List<Faculty> result = response.getBody();

        List<Faculty> expectedFacultyList = List.of(mockFaculty1);

        assertThat(result).isEqualTo(expectedFacultyList);
    }

    @Test
    void shouldFindFacultyCollectionFilteredByColor_ThenReturnThatFacultyCollection() {
        long faculty1Id = facultyController.addFaculty(mockFaculty1);
        long faculty2Id = facultyController.addFaculty(mockFaculty2);

        mockFaculty1.setId(faculty1Id);
        mockFaculty2.setId(faculty2Id);

        ResponseEntity<List<Faculty>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/faculty/by-name-or-color?name=&color=" + mockFaculty2.getColor(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        List<Faculty> result = response.getBody();

        List<Faculty> expectedFacultyList = List.of(mockFaculty2);

        assertThat(result).isEqualTo(expectedFacultyList);
    }

    @Test
    void shouldFindFacultyCollectionFilteredByColorAndByName_ThenReturnThatFacultyCollection() {
        long faculty1Id = facultyController.addFaculty(mockFaculty1);
        long faculty2Id = facultyController.addFaculty(mockFaculty2);

        mockFaculty1.setId(faculty1Id);
        mockFaculty2.setId(faculty2Id);

        ResponseEntity<List<Faculty>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/faculty/by-name-or-color?name=" + mockFaculty1.getName() + "&color=" + mockFaculty2.getColor(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        List<Faculty> result = response.getBody();

        List<Faculty> expectedFacultyList = List.of(mockFaculty1, mockFaculty2);

        assertThat(result).isEqualTo(expectedFacultyList);
    }

    @Test
    void shouldReturnAllFacultyCollection_ThenReturnThatFacultyCollection() {
        long faculty1Id = facultyController.addFaculty(mockFaculty1);
        long faculty2Id = facultyController.addFaculty(mockFaculty2);

        mockFaculty1.setId(faculty1Id);
        mockFaculty2.setId(faculty2Id);

        ResponseEntity<List<Faculty>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        List<Faculty> result = response.getBody();

        List<Faculty> expectedStudentList = List.of(mockFaculty1, mockFaculty2);

        assertThat(result).isEqualTo(expectedStudentList);
    }
}
