package iam.bookme;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractContainerTest {

	public static final String POSTGRES_IMAGE = "postgres:16.1";
	public static final String DATABASE_NAME = "booking_db";

	protected static PostgreSQLContainer<?> postgresContainer;

	static {
		postgresContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE)
				.withDatabaseName(DATABASE_NAME);
		postgresContainer.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresContainer::getUsername);
		registry.add("spring.datasource.password", postgresContainer::getPassword);
	}

}
