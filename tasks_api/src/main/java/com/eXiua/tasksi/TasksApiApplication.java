package com.eXiua.tasksi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.eXiua.tasksi.repository")
@EntityScan(basePackages = "com.eXiua.tasksi.model")
@ComponentScan(basePackages = "com.eXiua.tasksi")
public class TasksApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasksApiApplication.class, args);
	}

}
