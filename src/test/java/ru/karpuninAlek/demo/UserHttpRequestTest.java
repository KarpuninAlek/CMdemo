package ru.karpuninAlek.demo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
    public void postingShouldReturnCreatredUser() throws Exception {
        User sample = new User("uniqueAlek");
        sample.setName("Alek K ");
        String password = "StrongPassword5";
        sample.setPassword(password);
        User returnUser = this.restTemplate.postForObject(usersUrl(), sample, User.class);
        assertThat(returnUser)
                .isEqualToComparingFieldByField(sample);
    }

    @Test
    @Order(3)
    public void shouldReturnCreatredPreviouslyUser() throws Exception {
        String login = "uniqueAlek";
        User sample = new User(login);
        sample.setName("Alek K ");
        String password = "StrongPassword5";
        sample.setPassword(password);
        User returnUser = this.restTemplate.getForObject(usersUrl() + login, User.class);
        assertThat(returnUser)
                .isEqualToComparingFieldByField(sample);
    }

    @Test
    @Order(4)
    public void shouldReturnChangedUser() throws Exception {
        String login = "uniqueAlek";
        User sample = new User(login);
        sample.setName("Alex N");
        String password = "StrongPassword6";
        sample.setPassword(password);
        this.restTemplate.put(usersUrl() + login, sample);
        User returnUser =this.restTemplate.getForObject(usersUrl() + login, User.class);
        assertThat(returnUser)
                .isEqualToComparingFieldByField(sample);
    }

    @Test
    @Order(5)
    public void shouldReturnEmptiedPreviouslyUsers() throws Exception {
        String login = "uniqueAlek";
        User sample = new User(login);
        sample.setName("Alek K ");
        String password = "StrongPassword5";
        sample.setPassword(password);
        this.restTemplate.delete(usersUrl() + login);
        returnsEmptyUsersArray();
    }
}
