package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.opentest4j.AssertionFailedError;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.language.Directive;
import com.graphql_java_generator.plugin.language.DirectiveLocation;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.MergeSchemaConfigurationTestHelper;

import merge.mavenplugin_notscannedbyspring.AbstractSpringConfiguration;
import merge.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration;

//@Disabled
@Execution(ExecutionMode.CONCURRENT)
class AddRelayConnectionsTest {

	AbstractApplicationContext ctx = null;
	MergeDocumentParser documentParser = null;
	AddRelayConnections addRelayConnections = null;
	CommonConfiguration configuration;

	@BeforeEach
	void setup() {
		// No action: the Spring Configuration to load may change, depending on the test. So each test creates itself
		// the Spring Context
	}

	@AfterEach
	void cleanup() {
		if (ctx != null) {
			ctx.close();
		}
	}

	private void loadSpringContext(Class<? extends AbstractSpringConfiguration> configurationClass) {
		ctx = new AnnotationConfigApplicationContext(configurationClass);
		((MergeSchemaConfigurationTestHelper) ctx.getBean(MergeSchemaConfiguration.class)).addRelayConnections = true;
		ctx.getBean(MergeSchemaConfiguration.class).logConfiguration();
		documentParser = ctx.getBean(MergeDocumentParser.class);
		addRelayConnections = ctx.getBean(AddRelayConnections.class);
		configuration = documentParser.configuration;

		// Go, go, go
		documentParser.parseDocuments();
	}

	@Test
	void test_getFieldInheritedFrom() {
		// Preparation
		loadSpringContext(AllGraphQLCases_Client_SpringConfiguration.class);
		List<Field> fields;

		// Interface field that is not inherited from an interface
		fields = addRelayConnections.getFieldInheritedFrom(getField("AllFieldCasesInterface", "name"));
		assertEquals(0, fields.size());

		// Type field that is not inherited from an interface
		fields = addRelayConnections.getFieldInheritedFrom(getField("Human", "homePlanet"));
		assertEquals(0, fields.size());

		// Type field that is not inherited from one interface
		fields = addRelayConnections.getFieldInheritedFrom(getField("Human", "comments"));
		assertEquals(1, fields.size());
		assertEquals("comments", fields.get(0).getName());
		assertEquals("Commented", fields.get(0).getOwningType().getName());

		// Type field that is not inherited from one interface
		fields = addRelayConnections.getFieldInheritedFrom(getField("Human", "id"));
		assertEquals(2, fields.size());
		assertEquals("id", fields.get(0).getName());
		assertEquals("Character", fields.get(0).getOwningType().getName());
		assertEquals("id", fields.get(1).getName());
		assertEquals("WithID", fields.get(1).getOwningType().getName());
	}

	@Test
	void test_addEdgeConnectionAndApplyNodeInterface_step2missingDirectiveOnInterfaceField() {
		// If a type's field is annotated by @RelayConnection, but this field is "inherited" from an interface, in which
		// is not inherited by this directive, then an error should be thrown.
		// Let's remove the @RelayConnection directive from the AllFieldCasesInterface.friends field, and check that
		loadSpringContext(AllGraphQLCases_Client_SpringConfiguration.class);
		FieldImpl f = (FieldImpl) getField("AllFieldCasesInterface", "friends");
		f.setAppliedDirectives(new ArrayList<>());
		Logger mockLogger = mock(Logger.class);
		((MergeSchemaConfigurationTestHelper) configuration).log = mockLogger;
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class,
				() -> addRelayConnections.addEdgeConnectionAndApplyNodeInterface());

		// Verification
		assertTrue(e.getMessage().contains("1 error(s) was(were) found"));
		verify(mockLogger).error(argument.capture());
		String errorMessage = argument.getValue();
		assertTrue(errorMessage.contains("AllFieldCasesInterface"));
		assertTrue(errorMessage.contains("friends"));
		assertTrue(errorMessage.contains(
				"interface AllFieldCasesInterface, in which this field doesn't have the directive @RelayConnection applied"));
	}

	@Test
	void test_addEdgeConnectionAndApplyNodeInterface_step3() {
		// Preparation
		loadSpringContext(AllGraphQLCases_Client_SpringConfiguration.class);
		Logger mockLogger = mock(Logger.class);
		((MergeSchemaConfigurationTestHelper) configuration).log = mockLogger;
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		// Go, go, go
		addRelayConnections.addEdgeConnectionAndApplyNodeInterface();

		// Verification
		verify(mockLogger).warn(argument.capture());
		String warningMessage = argument.getValue();
		assertTrue(warningMessage.contains("AllFieldCasesInterfaceType"));
		assertTrue(warningMessage.contains("friends"));
		assertTrue(warningMessage.contains(
				"implements (directly or indirectly) the AllFieldCasesInterface.friends field, but does not have the @RelayConnection directive"));
	}

	@Test
	void test_getInheritedFields() {
		loadSpringContext(AllGraphQLCases_Client_SpringConfiguration.class);
		List<Field> result;

		// The field is owned by an object (not an interface)
		result = addRelayConnections.getInheritedFields(getField("AllFieldCasesInterfaceType", "id"));
		assertEquals(0, result.size());

		// The field is owned by an interface, but never used (that is: the interface is never used)
		result = addRelayConnections.getInheritedFields(getField("UnusedInterface", "aNonUSedField"));
		assertEquals(0, result.size());

		// Standard case: the field is owned by an interface, and used by several types
		result = addRelayConnections.getInheritedFields(getField("WithID", "id"));
		assertEquals(4, result.size());
		// 4 items, which are:
		assertEquals(1,
				result.stream()
						.filter((f) -> f.getOwningType().getName().equals("AllFieldCases") && f.getName().equals("id"))
						.count());
		assertEquals(1, result.stream().filter(
				(f) -> f.getOwningType().getName().equals("AllFieldCasesInterfaceType") && f.getName().equals("id"))
				.count());
		assertEquals(1, result.stream()
				.filter((f) -> f.getOwningType().getName().equals("Human") && f.getName().equals("id")).count());
		assertEquals(1, result.stream()
				.filter((f) -> f.getOwningType().getName().equals("Droid") && f.getName().equals("id")).count());
	}

	@Disabled // Disabled, as graphql-java v14.0 (the current used version) doesn't accept interface that implements
				// interface. This test should be enabled, once upgraded
	@Test
	void test_getFieldInheritedFrom_interfaceThatImplementsInterface() {
		// Preparation
		loadSpringContext(AllGraphQLCases_Client_SpringConfiguration.class);
		Field f = getField("AllFieldCasesInterface", "id");

		// Go, go, go
		List<Field> fields = addRelayConnections.getFieldInheritedFrom(f);

		// Verification
		assertEquals(1, fields.size());
		assertEquals("id", fields.get(0).getName());
		assertEquals("WithID", fields.get(0).getOwningType().getName());
	}

	private Field getField(String typeName, String fieldName) {
		Type t = documentParser.getType(typeName);
		for (Field f : t.getFields()) {
			if (fieldName.equals(f.getName())) {
				return f;
			}
		}
		fail("Could not find the " + fieldName + " in the type " + typeName);
		return null;
	}

	/**
	 * Test of the addRelayConnections capabilities, on a schema that doesn't contain any stuff about Relay (out of the
	 * &#064;RelayConnection directive)
	 */
	@Test
	void testAddRelayConnections_schemaWithoutRelay() {
		// Preparation
		loadSpringContext(AllGraphQLCases_Client_SpringConfiguration.class);

		// Verification
		checkRelayConnectionDirective();
		checkNodeInterface();
		checkPageInfoType();
		checkNodeEdgeAndConnectionTypes();
	}

	@Test
	void testAddRelayConnections_schemaAlreadyRelayCompliant() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongRelayConnectionDirective() {
		// Preparation
		loadSpringContext(AllGraphQLCases_Client_SpringConfiguration.class);
		//
		Directive relayConnection = documentParser.getDirectiveDefinition("RelayConnection");
		relayConnection.getDirectiveLocations().remove(0);// Let's remove the only item in the list
		relayConnection.getDirectiveLocations().add(DirectiveLocation.ENUM); // Wrong location!
		documentParser.getDirectives().add(relayConnection);

		// Verification
		AssertionFailedError e = assertThrows(AssertionFailedError.class, () -> checkRelayConnectionDirective());
		assertTrue(e.getMessage().contains("ENUM"));
	}

	@Test
	void testAddRelayConnections_schemaWithWrongNode() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongPageInfo() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongEdge() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongConnection() {
		fail("Not yet implemented");
	}

	/** The <I>&#064;RelayConnection</I> directive may not be set on a field of an input type */
	@Test
	void testAddRelayConnections_relayConnectionOnInputTypeField() {
		fail("Not yet implemented");
	}

	private void checkRelayConnectionDirective() {
		final String RELAY_CONNECTION = "RelayConnection";
		boolean found = false;
		for (Directive d : documentParser.getDirectives()) {
			if (RELAY_CONNECTION.equals(d.getName())) {
				// The directive should exist only once, so we may not already have found it.
				if (found) {
					fail("There are two directives with '" + RELAY_CONNECTION + "' as a name");
				}
				// We've found it.
				found = true;
				// Let's check its properties
				assertEquals(0, d.getArguments().size(), "No arguments");
				assertEquals(1, d.getDirectiveLocations().size(), "Directive has one location");
				assertEquals(DirectiveLocation.FIELD_DEFINITION, d.getDirectiveLocations().get(0),
						"Directive locations may only be: FIELD_DEFINITION");
			}
		}

		if (!found) {
			fail("The directive " + RELAY_CONNECTION + " has not been found");
		}
	}

	private void checkNodeInterface() {
		final String NODE = "Node";
		boolean found = false;
		for (InterfaceType d : documentParser.getInterfaceTypes()) {
			if (NODE.equals(d.getName())) {
				// The interface should exist only once, so we may not already have found it.
				if (found) {
					fail("There are two interfaces with '" + NODE + "' as a name");
				}
				// We've found it.
				found = true;
				// Let's check its properties
				assertEquals(0, d.getMemberOfUnions().size(), "No unions");
				assertEquals(1, d.getFields().size(), "One field");
				assertEquals("id", d.getFields().get(0).getName(), "field is id");
				assertEquals("ID", d.getFields().get(0).getGraphQLTypeName(), "field'stype is ID");
				assertEquals(true, d.getFields().get(0).isId(), "field is an ID");
				assertEquals(false, d.getFields().get(0).isItemMandatory(), "field is not a list");
				assertEquals(false, d.getFields().get(0).isList(), "field is not a list");
				assertEquals(true, d.getFields().get(0).isMandatory(), "field is mandatory");
				assertEquals(null, d.getRequestType(), "not a query/mutation/subscription");
				assertEquals(false, d.isInputType(), "Not an input type");
			}
		}

		if (!found) {
			fail("The interface " + NODE + " has not been found");
		}
	}

	private void checkPageInfoType() {
		final String PAGE_INFO_TYPE = "PageInfo";
		boolean found = false;
		for (ObjectType o : documentParser.getObjectTypes()) {
			if (PAGE_INFO_TYPE.equals(o.getName())) {
				// The type should exist only once, so we may not already have found it.
				if (found) {
					fail("There are two types with '" + PAGE_INFO_TYPE + "' as a name");
				}
				// We've found it.
				found = true;
				// Let's check its properties
				assertEquals(0, o.getMemberOfUnions().size(), "No unions");
				int j = 0;
				// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname, nbParameters)
				checkField(o, j++, "hasNextPage", false, true, false, "Boolean", "java.lang.Boolean", 0);
				checkField(o, j++, "hasPreviousPage", false, true, false, "Boolean", "java.lang.Boolean", 0);
				checkField(o, j++, "startCursor", false, true, false, "String", "java.lang.String", 0);
				checkField(o, j++, "endCursor", false, true, false, "String", "java.lang.String", 0);
				//
				assertEquals(null, o.getRequestType(), "not a query/mutation/subscription");
				assertEquals(false, o.isInputType(), "Not an input type");
			}
		}

		if (!found) {
			fail("The type " + PAGE_INFO_TYPE + " has not been found");
		}
	}

	/**
	 * This method checks that the relevant Edge and Connection types have been created, and that each base type for
	 * them correctly implements the Node interface
	 */
	private void checkNodeEdgeAndConnectionTypes() {
		checkNodeEdgeAndConnectionForOneType("Character");
		checkNodeEdgeAndConnectionForOneType("Human");
	}

	/**
	 * This method checks for one base type, that it implements the Node interface, and its Edge and Connection types
	 * have been correctly created.
	 */
	private void checkNodeEdgeAndConnectionForOneType(String typeName) {
		assertTrue(documentParser.getType(typeName) instanceof ObjectType);
		ObjectType baseType = (ObjectType) documentParser.getType(typeName);

		// The base type must implement the Node interface
		boolean found = true;
		for (String i : baseType.getImplementz()) {
			if (i.equals("Node")) {
				found = true;
				break;
			}
		}
		assertTrue(found, "We should have found the Node interface");

		//////////////////////////////////////////////////////////////////////////////////////////////////
		// The XxxEdge must have been created, and be compliant with the Relay specification
		assertTrue(documentParser.getType(typeName + "Edge") instanceof ObjectType);
		ObjectType edge = (ObjectType) documentParser.getType(typeName + "Edge");
		assertEquals("", edge.getAnnotation());
		assertEquals(0, edge.getAppliedDirectives().size());
		//
		assertEquals(2, edge.getFields().size());
		int j = 0;
		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname, nbParameters)
		checkField(edge, j++, "node", false, false, false, typeName, configuration.getPackageName() + "." + typeName,
				0);
		checkField(edge, j++, "cursor", false, true, false, "String", "java.lang.String", 0);
		//
		assertEquals("id", edge.getIdentifier());
		assertEquals(0, edge.getImplementz());
		assertEquals(0, edge.getMemberOfUnions().size());
		assertEquals(null, edge.getRequestType());

		//////////////////////////////////////////////////////////////////////////////////////////////////
		// The XxxConnection must have been created, and be compliant with the Relay specification
		assertTrue(documentParser.getType(typeName + "Connection") instanceof ObjectType);
		ObjectType connection = (ObjectType) documentParser.getType(typeName + "Connection");
		assertEquals("", connection.getAnnotation());
		assertEquals(0, connection.getAppliedDirectives().size());
		//
		assertEquals(2, connection.getFields().size());
		j = 0;
		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname, nbParameters)
		checkField(connection, j++, "edges", true, true, false, typeName,
				configuration.getPackageName() + "." + typeName, 0);
		checkField(connection, j++, "pageInfo", false, true, false, "PageInfo",
				configuration.getPackageName() + ".PageInfo", 0);
		//
		assertEquals("id", connection.getIdentifier());
		assertEquals(0, connection.getImplementz());
		assertEquals(0, connection.getMemberOfUnions().size());
		assertEquals(null, connection.getRequestType());
	}

	private void checkField(ObjectType type, int j, String name, boolean list, boolean mandatory, Boolean itemMandatory,
			String typeName, String classname, int nbParameters) {
		Field field = type.getFields().get(j);
		String fieldDescForJUnitMessage = "Field n°" + j + " (" + name + ")";

		assertEquals(name, field.getName(), "field name is " + name + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(list, field.isList(), "field list is " + list + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(mandatory, field.isMandatory(),
				"field mandatory is " + mandatory + " (for " + fieldDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, field.isItemMandatory(),
					"field itemMandatory is " + itemMandatory + " (for " + fieldDescForJUnitMessage + ")");
		}

		Type fieldType = field.getType();
		assertEquals(typeName, fieldType.getName(),
				"type name is " + typeName + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(classname, fieldType.getClassFullName(),
				"Class for field type is " + classname + " (for " + fieldDescForJUnitMessage + ")");

		assertEquals(nbParameters, field.getInputParameters().size(), "Nb Input parameters");
	}
}