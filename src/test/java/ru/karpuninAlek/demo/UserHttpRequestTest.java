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
import ru.karpuninAlek.demo.model.DTOs.RoleDTO;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.repositories.RoleRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserHttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    private String usersUrl() {
        return "http://localhost:" + port + "/users/";
    }

    private UserDTO getSampleUser(){
        String password = "StrongPassword5";
        UserDTO sample = new UserDTO("uniqueAlek", "Alek K ", password);
//        sample.setRoles(new RoleDTO[0]);
//        sample.addRole(new RoleDTO("Admin"));
        sample.roles = new ArrayList<>();
        sample.roles.add(new RoleDTO("Admin"));
//        sample.addRole(new RoleDTO("System Admin"));
        return sample;
    }

    private List<RoleDTO> getRoleSamples(){
        List<RoleDTO> roles = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            roles.add(new RoleDTO("Role #" + i));
        }
        return roles;
    }

    private List<UserDTO> getCorrectUserSamples(){
        List<UserDTO> users = new ArrayList<>();
        List<RoleDTO> roles = getRoleSamples();
        for (int i = 0; i < 10; i++) {
            UserDTO user = new UserDTO("unique" + i, "Alek K"+ i, "StrongPassword" + i);
            for (int j = i; j < i * 2; j++) {
                user.roles.add(roles.get(j));
            }
            users.add(user);
        }
        return users;
    }

    private List<UserDTO> getCorrectUserSamplesNoRoles(){
        List<UserDTO> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserDTO dto = new UserDTO("unique" + i, "Alek K"+ i, "StrongPassword" + i);
            dto.roles = null;
            users.add(dto);
        }
        return users;
    }

    private void assertThatResponseIsSuccessfulWithNotNullBody(ResponseEntity<?> response){
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(response.getBody(), is(notNullValue()));
    }

    private <T> void assertThatResponseIsSuccessfulWithEmptyBody(ResponseEntity<T[]> response){
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody().length, is(equalTo(0)));
    }
    private <T> void assertThatResponseIsSuccessfulWithNotEmptyBody(ResponseEntity<T[]> response){
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody().length, is(greaterThan(0)));
    }
    private void assertThatResponseIsSuccessfulWithSuccessResult(ResponseEntity<ResultResponse> response){
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody().isSuccess(), is(true));
    }

    @Test
    @Order(1)
    public void returnsEmptyUsersArray() throws Exception {
        ResponseEntity<UserDTO[]> response = this.restTemplate.getForEntity(usersUrl(), UserDTO[].class);
        assertThatResponseIsSuccessfulWithEmptyBody(response);
    }

    @Test
    @Order(2)
    public void postingShouldReturnSuccess() throws Exception {
        UserDTO sample = getSampleUser();
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThatResponseIsSuccessfulWithSuccessResult(response);
    }

    @Test
    @Order(3)
    public void shouldReturnCreatedPreviouslyUser() throws Exception {
        UserDTO sample = getSampleUser();
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + sample.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(equalTo(sample)));
    }

    @Test
    @Order(4)
    public void returnsPreviouslyAddedUsersArray() throws Exception {
        ResponseEntity<UserDTO[]> response = this.restTemplate.getForEntity(usersUrl(), UserDTO[].class);
        assertThatResponseIsSuccessfulWithNotEmptyBody(response);
        UserDTO[] expected = { getSampleUser() };
        expected[0].roles = null;
        assertThat(response.getBody(), is(expected));
    }

    @Test
    @Order(5)
    public void putShouldReturnSuccessAndUserShouldBeChanged() throws Exception {
        String password = "StrongPassword6";
        UserDTO sample = getSampleUser();
        sample.password = password;
        this.restTemplate.put(usersUrl() + sample.login, sample);
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + sample.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(sample));
    }

    @Test
    @Order(6)
    public void shouldReturnEmptiedPreviouslyUsers() throws Exception {
        UserDTO sample = getSampleUser();
        this.restTemplate.delete(usersUrl() + sample.login);
        returnsEmptyUsersArray();
    }

    @Test
    @Order(7)
    public void postingCorrectUsersShouldReturnSuccess() throws Exception {
        List<UserDTO> samples = getCorrectUserSamples();
        samples.forEach(sample -> {
            ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
            assertThatResponseIsSuccessfulWithSuccessResult(response);
        });
    }

    @Test
    @Order(8)
    public void returnsPreviouslyAddedUsersArray2() throws Exception {
        ResponseEntity<UserDTO[]> response = this.restTemplate.getForEntity(usersUrl(), UserDTO[].class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody().length, is(greaterThan(0)));
        UserDTO[] expected = getCorrectUserSamplesNoRoles().toArray(new UserDTO[0]);
        assertThat(response.getBody(), is(expected));
    }

    @Test
    @Order(9)
    public void shouldReturnNoPreviouslyDeletedUsers() throws Exception {
        List<UserDTO> samples = getCorrectUserSamplesNoRoles();
        this.restTemplate.delete(usersUrl() + samples.get(3).login);
        this.restTemplate.delete(usersUrl() + samples.get(4).login);
        samples.remove(4);
        samples.remove(3);
        ResponseEntity<UserDTO[]> response = this.restTemplate.getForEntity(usersUrl(), UserDTO[].class);
        assertThatResponseIsSuccessfulWithNotEmptyBody(response);
        UserDTO[] expected = samples.toArray(new UserDTO[0]);
        assertThat(response.getBody(), is(expected));
        assertThat(roleRepository.existsByName("Role #" + 4), is(false));
    }

    @Test
    @Order(10)
    public void shouldReturnChangedUser() throws Exception {
        List<UserDTO> samples = getCorrectUserSamplesNoRoles();
        List<RoleDTO> roleSamples = getRoleSamples();
        UserDTO changed = samples.get(5);
        changed.roles = new ArrayList<>();
        changed.roles.add(roleSamples.get(2));
        changed.roles.add(roleSamples.get(9));
        changed.roles.add(roleSamples.get(10));
        changed.name = "changed for no reason name";
        this.restTemplate.put(usersUrl() + changed.login, changed);
        ResponseEntity<UserDTO> userResponse = this.restTemplate.getForEntity(usersUrl() + changed.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(userResponse);
        assertThat(userResponse.getBody(), is(changed));
    }

    @Test
    @Order(11)
    public void shouldReturnNotChangedUser() throws Exception {
        List<UserDTO> samples = getCorrectUserSamplesNoRoles();
        UserDTO changed = samples.get(6);
        changed.roles = null;
        this.restTemplate.put(usersUrl() + changed.login, changed);
        ResponseEntity<UserDTO> userResponse = this.restTemplate.getForEntity(usersUrl() + changed.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(userResponse);
        assertThat(userResponse.getBody().roles, is(not(empty())));
    }

    //region Empty field post
    @Test
    @Order(12)
    public void postingShouldReturnEmptyPasswordError() throws Exception {
        UserDTO sample = new UserDTO("log", "nam", "");
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getErrors(), contains("Password can't be empty"));
    }

    @Test
    @Order(13)
    public void postingShouldReturnEmptyNameError() throws Exception {
        UserDTO sample = new UserDTO("log", "", "Pass5");
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getErrors(), contains("Name can't be empty"));
    }

    @Test
    @Order(14)
    public void postingShouldReturnEmptyLoginError() throws Exception {
        UserDTO sample = new UserDTO("", "nam", "Pass5");
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getErrors(), contains("Login can't be empty"));
    }
    //endregion

    //region Null field post
    @Test
    @Order(15)
    public void postingShouldReturnNullPasswordError() throws Exception {
        UserDTO sample = new UserDTO("log", "nam", null);
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getErrors(), contains("Password can't be null"));
    }

    @Test
    @Order(16)
    public void postingShouldReturnNullNameError() throws Exception {
        UserDTO sample = new UserDTO("log", null, "Pass5");
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getErrors(), contains("Name can't be null"));
    }

    @Test
    @Order(17)
    public void postingShouldReturnNullLoginError() throws Exception {
        UserDTO sample = new UserDTO(null, "nam", "Pass5");
        ResponseEntity<ResultResponse> response = this.restTemplate.postForEntity(usersUrl(), sample, ResultResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getErrors(), contains("Login can't be null"));
    }
    //endregion

    @Test
    @Order(18)
    public void puttingShouldReturnEmptyPasswordErrorNoChange() throws Exception {
        UserDTO sample = new UserDTO("log", "nam", "");
        List<UserDTO> samples = getCorrectUserSamples();
        UserDTO dto = samples.get(2);
        this.restTemplate.put(usersUrl() + dto.login, sample);
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + dto.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(dto));
    }

    @Test
    @Order(19)
    public void puttingShouldReturnEmptyNameErrorNoChange() throws Exception {
        UserDTO sample = new UserDTO("log", "", "Pass5");
        List<UserDTO> samples = getCorrectUserSamples();
        UserDTO dto = samples.get(2);
        this.restTemplate.put(usersUrl() + dto.login, sample);
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + dto.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(dto));
    }

    @Test
    @Order(20)
    public void puttingShouldReturnEmptyLoginErrorNoChange() throws Exception {
        UserDTO sample = new UserDTO("", "nam", "Pass5");
        List<UserDTO> samples = getCorrectUserSamples();
        UserDTO dto = samples.get(2);
        this.restTemplate.put(usersUrl() + dto.login, sample);
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + dto.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(dto));
    }

    @Test
    @Order(21)
    public void puttingShouldReturnNullPasswordErrorNoChange() throws Exception {
        UserDTO sample = new UserDTO("log", "nam", null);
        List<UserDTO> samples = getCorrectUserSamples();
        UserDTO dto = samples.get(2);
        this.restTemplate.put(usersUrl() + dto.login, sample);
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + dto.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(dto));
    }

    @Test
    @Order(22)
    public void puttingShouldReturnNullNameErrorNoChange() throws Exception {
        UserDTO sample = new UserDTO("log", null, "Pass5");
        List<UserDTO> samples = getCorrectUserSamples();
        UserDTO dto = samples.get(2);
        this.restTemplate.put(usersUrl() + dto.login, sample);
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + dto.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(dto));
    }

    @Test
    @Order(23)
    public void puttingShouldReturnNullLoginErrorNoChange() throws Exception {
        UserDTO sample = new UserDTO(null, "nam", "Pass5");
        List<UserDTO> samples = getCorrectUserSamples();
        UserDTO dto = samples.get(2);
        this.restTemplate.put(usersUrl() + dto.login, sample);
        ResponseEntity<UserDTO> response = this.restTemplate.getForEntity(usersUrl() + dto.login, UserDTO.class);
        assertThatResponseIsSuccessfulWithNotNullBody(response);
        assertThat(response.getBody(), is(dto));
    }

}
