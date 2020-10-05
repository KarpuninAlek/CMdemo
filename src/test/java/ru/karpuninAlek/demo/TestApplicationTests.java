package ru.karpuninAlek.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.karpuninAlek.demo.model.DTOs.RoleDTO;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.model.User;
import ru.karpuninAlek.demo.model.Role;
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
	private static final int CORRECT_ROLES_COUNT = 50;
	private static final int FAULTY_ROLES_COUNT = 3;

	private static final List<UserDTO> correctUsers = new ArrayList<>();
	private static final List<RoleDTO> correctRoles = new ArrayList<>();
	private static final Map<UserDTO, List<String>> unprocessableUsers = new HashMap<>();
	private static final Map<UserDTO, List<String>> badUsers = new HashMap<>();
	private static final Map<RoleDTO, String> faultyRoles = new HashMap<>();

	private void resultResponseShouldBeUnprocessableAndContainErrors(ResponseEntity<ResultResponse> response, String[] errors){
		assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
		if (userRepository.findAllBy().size() != usersAdded) {
			var s = "";
		}
		assertThat(userRepository.findAllBy().size(), is(usersAdded));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().isSuccess(), is(false));
		assertThat(response.getBody().getErrors(), containsInAnyOrder(errors));
		assertThat(response.getBody().getErrors().size(), is(errors.length));
	}

	private void resultResponseShouldBeBadRequestAndContainErrors(ResponseEntity<ResultResponse> response, String[] errors){
		if (response.getStatusCode() != HttpStatus.BAD_REQUEST) {
			var s = "";
		}
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
		assertThat(userRepository.findAllBy().size(), is(usersAdded));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().isSuccess(), is(false));
		assertThat(response.getBody().getErrors(), containsInAnyOrder(errors));
		assertThat(response.getBody().getErrors().size(), is(errors.length));
	}

	private void resultResponseShouldBeOK(ResponseEntity<ResultResponse> response){
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR || userRepository.findAllBy().size() != usersAdded + 1) {
			var s = "";
		}
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(userRepository.findAllBy().size(), is(usersAdded + 1));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().isSuccess(), is(true));
		assertThat(response.getBody().getErrors(), is(nullValue()));
	}

	//region Fields with errors generators
	private static Map<String, List<String>> loginsWithErrors() {
		return new HashMap<>() {{
			put("Alek", null);
			put(null, Collections.singletonList(User.NULL_LOGIN)); //Arrays.asList(User.NULL_LOGIN)
			put("", Collections.singletonList(User.EMPTY_LOGIN));
			put("Al ek", Collections.singletonList(User.SPACE_LOGIN));
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Collections.singletonList(User.LONG_LOGIN));
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Arrays.asList(User.LONG_LOGIN, User.SPACE_LOGIN));
		}};
	}

	private static Map<String, String> namesWithErrors() {
		return new HashMap<>() {{
			put("Alek", null);
			put(null, User.NULL_NAME);
			put("", User.EMPTY_NAME);
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", User.LONG_NAME);
		}};
	}

	private static Map<String, List<String>> passwordsWithErrors() {
		return new HashMap<>() {{
			put("StrongPassword5", null);
			put(null, Collections.singletonList(User.NULL_PASSWORD));
			put("", Collections.singletonList(User.EMPTY_PASSWORD));
			put("weak", Collections.singletonList(User.WEAK_PASSWORD));
			put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Arrays.asList(User.WEAK_PASSWORD, User.LONG_PASSWORD));
			put("weak5", Collections.singletonList(User.WEAK_PASSWORD));
			put("Weak", Collections.singletonList(User.WEAK_PASSWORD));
			put("5Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Collections.singletonList(User.LONG_PASSWORD));
		}};
	}

	private static List<RoleDTO> correctRoles() {
		List<RoleDTO> roles = new ArrayList<>();
		for (int i = 0; i < CORRECT_ROLES_COUNT; i++) {
			roles.add(new RoleDTO("Role #" + i));
		}
		return roles;
	}

	private static Map<RoleDTO, String> faultyRolesWithErrors() {
		Map<RoleDTO, String> roles = new HashMap<>(){{
			put(new RoleDTO(null), Role.NULL_NAME);
			put(new RoleDTO(""), Role.EMPTY_NAME);
			put(new RoleDTO("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), Role.LONG_NAME);
		}};
		return roles;
	}

	@BeforeAll
	static void setUp(){
		Map<String, List<String>> logins = loginsWithErrors();

		Map<String, String> names = namesWithErrors();

		Map<String, List<String>> passwords = passwordsWithErrors();

		correctRoles.addAll(correctRoles());
		faultyRoles.putAll(faultyRolesWithErrors());

		Random random = new Random();

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
						boolean addCorrectRoles = random.nextBoolean();
						if (addCorrectRoles){
							for (int i = 0; i < random.nextInt(CORRECT_ROLES_COUNT); i++) {
								sample.roles.add(correctRoles.get(random.nextInt(CORRECT_ROLES_COUNT - 1)));
							}
						} else {
							for (int i = 0; i < random.nextInt(CORRECT_ROLES_COUNT); i++) {
								RoleDTO key = (RoleDTO) faultyRoles.keySet().toArray()[random.nextInt(FAULTY_ROLES_COUNT - 1)];
								sample.roles.add(key);
//								errors.add(faultyRoles.get(key));
							}
						}

						unprocessableUsers.put(sample, errors);
					} else  {
						for (int i = 0; i < 20; i++) {
							List<String> localErrors = new ArrayList<>(errors);
							UserDTO correctSample = new UserDTO(sample.login + i, sample.name + i, sample.password + i);
							boolean addCorrectRoles = random.nextBoolean();
							if (addCorrectRoles){
								for (int j = 0; j < random.nextInt(CORRECT_ROLES_COUNT); j++) {
									correctSample.roles.add(correctRoles.get(random.nextInt(CORRECT_ROLES_COUNT - 1)));
								}
								correctUsers.add(correctSample);
							} else {
								int faultyRolesCount = random.nextInt(FAULTY_ROLES_COUNT);
								if (faultyRolesCount > 0) {
									for (int j = 0; j < faultyRolesCount; j++) {
										RoleDTO key = (RoleDTO) faultyRoles.keySet().toArray()[random.nextInt(FAULTY_ROLES_COUNT - 1)];
										correctSample.roles.add(key);
										localErrors.add(faultyRoles.get(key));
									}
									badUsers.put(correctSample, localErrors);
								} else {
									correctUsers.add(correctSample);
								}

							}

						}
					}
				});
			});
		});
	}
	//endregion

	void postCorrectUser(UserDTO sample) throws Exception {
		resultResponseShouldBeOK(userController.setUser(sample));
		usersAdded++;
	}

	//region Tests
//	@Order(1)
	@Test
	void contextLoads() throws Exception {
		assertThat(userController, is(notNullValue()));
	}

//	@Order(1)
	@Test
	void postShouldReturnUnprocessableErrors() throws Exception {
		unprocessableUsers.forEach((sample, errors) -> resultResponseShouldBeUnprocessableAndContainErrors(userController.setUser(sample), errors.toArray(new String[0])));

		resultResponseShouldBeBadRequestAndContainErrors(userController.setUser(null), new String[] {UserService.NULL_DTO});
	}

//	@Order(1)
	@Test
	void postShouldReturnBadRequestErrors() throws Exception {
		resultResponseShouldBeBadRequestAndContainErrors(userController.setUser(null), new String[] {UserService.NULL_DTO});
	}

//	@Order(1)
	@Test
	void postShouldReturnSuccess() throws Exception {
		for (UserDTO sample : correctUsers) {
			postCorrectUser(sample);
		}
	}

//	@Order(5)
	@Test
	void postShouldReturnUserExists() throws Exception {
		List<User> addedUsers = userRepository.findAllBy();
		if (addedUsers.size() > 0) {
			UserDTO addedUser = new UserDTO(addedUsers.get(new Random().nextInt(addedUsers.size() - 1)));
			resultResponseShouldBeBadRequestAndContainErrors(userController.setUser(addedUser), new String[] {UserService.USER_EXISTS});
		}
	}
	//endregion

}
