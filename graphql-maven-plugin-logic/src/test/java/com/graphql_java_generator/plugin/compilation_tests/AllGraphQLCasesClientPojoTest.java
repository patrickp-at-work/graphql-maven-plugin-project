package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_ClientPojo_SpringConfiguration;

class AllGraphQLCasesClientPojoTest extends AbstractIntegrationTest {

	public AllGraphQLCasesClientPojoTest() {
		super(AllGraphQLCases_ClientPojo_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases*.graphqls");
	}

	@Override
	protected void checkNbGeneratedClasses(int nbGeneratedClasses) {
		assertEquals(37, nbGeneratedClasses);
	}
}
