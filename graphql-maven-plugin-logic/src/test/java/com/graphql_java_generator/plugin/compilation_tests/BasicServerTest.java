package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin_notscannedbyspring.Basic_Server_SpringConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Basic_Server_SpringConfiguration.class })
@Disabled // these tests are included in the other ones
class BasicServerTest extends AbstractIntegrationTest {

	// Everything is in the AbstractIntegrationTest class.

	// The only aim of this class, is to have its own Spring Configuration

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("basic.graphqls");
	}

}
