package io.f12.notionlinkedblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotionLinkedBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotionLinkedBlogApplication.class, args);
	}

}
