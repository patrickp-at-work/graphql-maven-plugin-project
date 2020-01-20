package com.graphql_java_generator.client.domain.starwars;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * @author generated by graphql-java-generator
 * @See https://github.com/graphql-java-generator/graphql-java-generator
 */

public class CharacterImpl implements Character {

	@GraphQLScalar(graphQLTypeName = "ID", javaClass = String.class)
	String id;

	@GraphQLScalar(graphQLTypeName = "String", javaClass = String.class)
	String name;

	@GraphQLNonScalar(graphQLTypeName = "Character", javaClass = Character.class)
	@JsonDeserialize(contentAs = CharacterImpl.class)
	List<Character> friends;

	@GraphQLScalar(graphQLTypeName = "Episode", javaClass = Episode.class)
	@JsonDeserialize(contentAs = Episode.class)
	List<Episode> appearsIn;

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setFriends(List<Character> friends) {
		this.friends = friends;
	}

	@Override
	public List<Character> getFriends() {
		return friends;
	}

	@Override
	public void setAppearsIn(List<Episode> appearsIn) {
		this.appearsIn = appearsIn;
	}

	@Override
	public List<Episode> getAppearsIn() {
		return appearsIn;
	}

	@Override
	public String toString() {
		return "CharacterImpl {" + "id: " + id + ", " + "name: " + name + ", " + "friends: " + friends + ", "
				+ "appearsIn: " + appearsIn + "}";
	}
}
