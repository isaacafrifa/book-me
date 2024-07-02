package iam.bookme;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AbstractContainerTest {

	public static PostgreSQLContainer POSTGRESQL_CONTAINER;

	static {
		POSTGRESQL_CONTAINER = new PostgreSQLContainer("postgres:16.1")
//                .withInitScript("config/INIT.sql")
				.withDatabaseName("booking_db");
		POSTGRESQL_CONTAINER.start();
	}



}
