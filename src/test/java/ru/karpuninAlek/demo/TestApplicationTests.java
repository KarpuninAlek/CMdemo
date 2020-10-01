package ru.karpuninAlek.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.karpuninAlek.demo.web.RoleController;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestApplicationTests {

	@Autowired
	private RoleController controller;

	@Test
	void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

}
