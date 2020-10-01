package ru.karpuninAlek.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import ru.karpuninAlek.demo.model.Role;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleHttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void postingShouldReturnCreatredRole() throws Exception {
        String name = "Alek";
        Role returnRole = this.restTemplate.postForObject("http://localhost:" + port + "/roles", name, Role.class);
        assertThat(returnRole.getName())
                .isEqualTo(name);
    }

    @Test
    public void returnsRolesArray() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/roles", ArrayList.class))
                .isEmpty();
    }
}
