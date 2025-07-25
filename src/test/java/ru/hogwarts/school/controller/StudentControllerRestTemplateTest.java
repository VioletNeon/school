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
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerRestTemplateTest {
    private Student mockStudent1 = new Student();
    private Student mockStudent2 = new Student();
    private Student mockStudent3 = new Student();
    private Student mockStudent4 = new Student();
    private Student mockStudent5 = new Student();
    private Student mockStudent6 = new Student();
    private Student mockStudent7 = new Student();
    private Student mockStudent8 = new Student();

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

        mockStudent3 = new Student();
        mockStudent3.setName("Sergey Sergeevich Sergeev");
        mockStudent3.setAge(16);

        mockStudent4 = new Student();
        mockStudent4.setName("Anton Antonovich Antonov");
        mockStudent4.setAge(17);

        mockStudent5 = new Student();
        mockStudent5.setName("Oleg Olegovich Olegov");
        mockStudent5.setAge(19);

        mockStudent6 = new Student();
        mockStudent6.setName("Semen Semenovich Semenov");
        mockStudent6.setAge(16);

        mockStudent7 = new Student();
        mockStudent7.setName("Artem Artemovich Artemov");
        mockStudent7.setAge(14);

        mockStudent8 = new Student();
        mockStudent8.setName("Fedor Fedorovich Fedorov");
        mockStudent8.setAge(15);
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

    @Test
    void shouldReturnCountOfAllStudents_ThenReturnStudentsCount() throws StudentNotFoundException {
        studentController.addStudent(mockStudent1);
        studentController.addStudent(mockStudent2);

        ResponseEntity<Long> response = this.restTemplate.getForEntity("http://localhost:" + port + "/student/count", Long.class);

        assertThat(response.getBody()).isEqualTo(2);
    }

    @Test
    void shouldReturnAverageAgeOfAllStudents_ThenReturnAverageAgeOfAllStudents() throws StudentNotFoundException {
        long student1Id = studentController.addStudent(mockStudent1);
        long student3Id = studentController.addStudent(mockStudent3);
        long student4Id = studentController.addStudent(mockStudent4);
        long student5Id = studentController.addStudent(mockStudent5);
        long student6Id = studentController.addStudent(mockStudent6);

        mockStudent1.setId(student1Id);
        mockStudent3.setId(student3Id);
        mockStudent4.setId(student4Id);
        mockStudent5.setId(student5Id);
        mockStudent6.setId(student6Id);

        ResponseEntity<Long> response = this.restTemplate.getForEntity("http://localhost:" + port + "/student/average-age", Long.class);

        assertThat(response.getBody()).isEqualTo(17);
    }

    @Test
    void shouldReturnLastFiveStudents_ThenReturnLastFiveStudentsList() throws StudentNotFoundException {
        long student1Id = studentController.addStudent(mockStudent1);
        long student2Id = studentController.addStudent(mockStudent2);
        long student3Id = studentController.addStudent(mockStudent3);
        long student4Id = studentController.addStudent(mockStudent4);
        long student5Id = studentController.addStudent(mockStudent5);
        long student6Id = studentController.addStudent(mockStudent6);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);
        mockStudent3.setId(student3Id);
        mockStudent4.setId(student4Id);
        mockStudent5.setId(student5Id);
        mockStudent6.setId(student6Id);

        ResponseEntity<List<Student>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/student/last",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        List<Student> expectedStudentList = List.of(mockStudent6, mockStudent5, mockStudent4, mockStudent3, mockStudent2);

        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(5);
        assertThat(response.getBody()).isEqualTo(expectedStudentList);
    }

    @Test
    void shouldFindStudentsWithNamesStartWithA_ThenReturnThatStudentsListNames() {
        long student1Id = studentController.addStudent(mockStudent7);
        long student2Id = studentController.addStudent(mockStudent8);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);

        ResponseEntity<List<String>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/student/starts-with-a",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );

        List<String> result = response.getBody();

        List<String> expectedNamesList = List.of(mockStudent7.getName().toUpperCase());

        assertThat(result).isEqualTo(expectedNamesList);
    }

    @Test
    void shouldFindStudentsAverageAgeUsingStreamAPI_ThenReturnThatStudentsAverageAge() {
        long student1Id = studentController.addStudent(mockStudent7);
        long student2Id = studentController.addStudent(mockStudent8);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);

        ResponseEntity<Integer> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/student/stream-average-age",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Integer>() {}
        );

        Integer result = response.getBody();

        Integer expectedAge = (mockStudent7.getAge() + mockStudent8.getAge()) / 2;

        assertThat(result).isEqualTo(expectedAge);
    }

    @Test
    void getPrintParallel_RestTemplate_ShouldCallService() {
        long student1Id = studentController.addStudent(mockStudent1);
        long student2Id = studentController.addStudent(mockStudent2);
        long student3Id = studentController.addStudent(mockStudent3);
        long student4Id = studentController.addStudent(mockStudent4);
        long student5Id = studentController.addStudent(mockStudent5);
        long student6Id = studentController.addStudent(mockStudent6);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);
        mockStudent3.setId(student3Id);
        mockStudent4.setId(student4Id);
        mockStudent5.setId(student5Id);
        mockStudent6.setId(student6Id);

        ResponseEntity<Void> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/students/print-parallel",
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getSynchronizedPrint_RestTemplate_ShouldCallService() {
        long student1Id = studentController.addStudent(mockStudent1);
        long student2Id = studentController.addStudent(mockStudent2);
        long student3Id = studentController.addStudent(mockStudent3);
        long student4Id = studentController.addStudent(mockStudent4);
        long student5Id = studentController.addStudent(mockStudent5);
        long student6Id = studentController.addStudent(mockStudent6);

        mockStudent1.setId(student1Id);
        mockStudent2.setId(student2Id);
        mockStudent3.setId(student3Id);
        mockStudent4.setId(student4Id);
        mockStudent5.setId(student5Id);
        mockStudent6.setId(student6Id);

        ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:" + port + "/student/students/print-synchronized", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
