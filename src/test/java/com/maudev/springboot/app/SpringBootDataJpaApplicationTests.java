package com.maudev.springboot.app;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//(classes = Object.class)
class SpringBootDataJpaApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Configuration
	public static class conf{
		
	}
}
