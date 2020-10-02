package ru.karpuninAlek.demo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.model.User;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserHttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String usersUrl() {
        return "http://localhost:" + port + "/users/";
    }

    @Test
    @Order(1)
    public void returnsEmptyUsersArray() throws Exception {
        assertThat(this.restTemplate.getForObject(usersUrl(), ArrayList.class))
                .isEmpty();
    }

    @Test
    @Order(2)
    public void postingShouldReturnSuccess() throws Exception {
        String password = "StrongPassword5";
        User sample = new User("uniqueAlek", "Alek K ", password);
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @Order(3)
    public void shouldReturnCreatredPreviouslyUser() throws Exception {
        String login = "uniqueAlek";
        String password = "StrongPassword5";
        User sample = new User(login, "Alek K ", password);
        ResponseEntity<User> response = this.restTemplate.getForEntity(usersUrl() + login, User.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualToComparingFieldByField(sample);
    }

    @Test
    @Order(4)
    public void putShouldReturnSuccessAndUserShouldBeChanged() throws Exception {
        String login = "uniqueAlek";
        String password = "StrongPassword6";
        User sample = new User(login, "Alex N", password);
        this.restTemplate.put(usersUrl() + login, sample);
        ResponseEntity<User> response = this.restTemplate.getForEntity(usersUrl() + login, User.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualToComparingFieldByField(sample);
    }

    @Test
    @Order(5)
    public void shouldReturnEmptiedPreviouslyUsers() throws Exception {
        String login = "uniqueAlek";
        String password = "StrongPassword5";
        User sample = new User(login, "Alek K ", password);
        this.restTemplate.delete(usersUrl() + login);
        returnsEmptyUsersArray();
    }
}
