package ru.karpuninAlek.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.model.User;
import ru.karpuninAlek.demo.repositories.UserRepository;
import ru.karpuninAlek.demo.repositories.UserService;
import ru.karpuninAlek.demo.web.UserController;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

@SpringBootTest
class TestApplicationTests {

	@Autowired
	UserController userController;

	@Autowired
	UserRepository userRepository;

	private static int usersAdded = 0;

	private static final List<UserDTO> correctUsers = new ArrayList<>();
	private static final Map<UserDTO, List<String>> faultyUsers = new HashMap<>();

	private void resultResponseShouldBeUnprocessableAndContainErrors(ResponseEntity<ResultResponse> response, String[] errors){
		assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
		assertThat(userRepository.findAllBy().size(), is(usersAdded));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().isSuccess(), is(false));
		assertThat(response.getBody().getErrors(), containsInAnyOrder(errors));
		assertThat(response.getBody().getErrors().size(), is(errors.length));
	}

	private void resultResponseShouldBeBadRequestAndContainErrors(ResponseEntity<ResultResponse> response, String[] errors){
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
		assertThat(userRepository.findAllBy().size(), is(usersAdded));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().isSuccess(), is(false));
		assertThat(response.getBody().getErrors(), containsInAnyOrder(errors));
		assertThat(response.getBody().getErrors().size(), is(errors.length));
	}

	private void resultResponseShouldBeOK(ResponseEntity<ResultResponse> response){
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(userRepository.findAllBy().size(), is(usersAdded));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().isSuccess(), is(true));
		assertThat(response.getBody().getErrors(), is(nullValue()));
	}

	//region Fields with errors generators
	private static Map<String, List<String>> loginsWithErrors() {
		return new HashMap<>() {{
			put(null, Collections.singletonList(User.NULL_LOGIN)); //Arrays.asList(User.NULL_LOGIN)
			put("", Collections.singletonList(User.EMPTY_LOGIN));
			put("Alek", null);
			put("Al ek", Collections.singletonList(User.SPACE_LOGIN));
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Collections.singletonList(User.LONG_LOGIN));
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Arrays.asList(User.LONG_LOGIN, User.SPACE_LOGIN));
		}};
	}

	private static Map<String, String> namesWithErrors() {
		return new HashMap<>() {{
			put(null, User.NULL_NAME);
			put("", User.EMPTY_NAME);
			put("Alek", null);
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", User.LONG_NAME);
		}};
	}

	private static Map<String, List<String>> passwordsWithErrors() {
		return new HashMap<>() {{
			put(null, Collections.singletonList(User.NULL_PASSWORD));
			put("", Collections.singletonList(User.EMPTY_PASSWORD));
			put("weak", Collections.singletonList(User.WEAK_PASSWORD));
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Arrays.asList(User.WEAK_PASSWORD, User.LONG_PASSWORD));
			put("weak5", Collections.singletonList(User.WEAK_PASSWORD));
			put("Weak", Collections.singletonList(User.WEAK_PASSWORD));
			put("5Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Collections.singletonList(User.LONG_PASSWORD));
			put("StrongPassword5", null);
		}};
	}

	@BeforeAll
	static void setUp(){
		Map<String, List<String>> logins = loginsWithErrors();

		Map<String, String> names = namesWithErrors();

		Map<String, List<String>> passwords = passwordsWithErrors();

		logins.forEach((login, loginErrors) -> {
			names.forEach((name, nameError) -> {
				passwords.forEach((password, passwordErrors) -> {
					UserDTO sample = new UserDTO(login, name, password);
					List<String> errors = new ArrayList<>();
					if(loginErrors != null) {
						errors.addAll(loginErrors);
					}
					if (nameError != null) {
						errors.add(nameError);
					}
					if (passwordErrors != null) {
						errors.addAll(passwordErrors);
					}
					if (errors.size() > 0) {
						faultyUsers.put(sample, errors);
					} else  {
						correctUsers.add(sample);
					}
				});
			});
		});
	}
	//endregion

	void postShouldReturnSuccess(UserDTO sample) throws Exception {
		usersAdded++;
		resultResponseShouldBeOK(userController.setUser(sample));
	}

	//region Tests
	@Test
	void contextLoads() throws Exception {
		assertThat(userController, is(notNullValue()));
	}

	@Test
	void postShouldReturnUnprocessableErrors() throws Exception {
		faultyUsers.forEach((sample, errors) -> resultResponseShouldBeUnprocessableAndContainErrors(userController.setUser(sample), errors.toArray(new String[0])));

		resultResponseShouldBeBadRequestAndContainErrors(userController.setUser(null), new String[] {UserService.NULL_DTO});
	}

	@Test
	void postShouldReturnBadRequestErrors() throws Exception {
		resultResponseShouldBeBadRequestAndContainErrors(userController.setUser(null), new String[] {UserService.NULL_DTO});
	}

	@Test
	void postShouldReturnSuccess() throws Exception {
		for (UserDTO sample : correctUsers) {
			postShouldReturnSuccess(sample);
		}
	}
	//endregion

}
