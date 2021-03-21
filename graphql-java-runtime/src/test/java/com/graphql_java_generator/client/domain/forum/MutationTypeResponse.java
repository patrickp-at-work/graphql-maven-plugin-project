package com.graphql_java_generator.client.domain.forum;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.GraphQLField;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLQuery;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.response.GraphQLRequestObject;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 *      "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLQuery(name = "MutationTypeResponse", type = RequestType.mutation)
public class MutationTypeResponse implements GraphQLRequestObject {

	@GraphQLInputParameters(names = { "name", "publiclyAvailable" }, types = { "String", "Boolean" })
	@JsonProperty("createBoard")
	@GraphQLNonScalar(fieldName = "createBoard", graphQLTypeSimpleName = "Board", javaClass = Board.class)
	Board createBoard;

	@GraphQLInputParameters(names = { "topic" }, types = { "TopicInput" })
	@JsonProperty("createTopic")
	@GraphQLNonScalar(fieldName = "createTopic", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	Topic createTopic;

	@GraphQLInputParameters(names = { "post" }, types = { "PostInput" })
	@JsonProperty("createPost")
	@GraphQLNonScalar(fieldName = "createPost", graphQLTypeSimpleName = "Post", javaClass = Post.class)
	Post createPost;

	@GraphQLInputParameters(names = { "spam" }, types = { "PostInput" })
	@JsonProperty("createPosts")
	@JsonDeserialize(contentAs = Post.class)
	@GraphQLNonScalar(fieldName = "createPosts", graphQLTypeSimpleName = "Post", javaClass = Post.class)
	List<Post> createPosts;

	public void setCreateBoard(Board createBoard) {
		this.createBoard = createBoard;
	}

	public Board getCreateBoard() {
		return createBoard;
	}

	public void setCreateTopic(Topic createTopic) {
		this.createTopic = createTopic;
	}

	public Topic getCreateTopic() {
		return createTopic;
	}

	public void setCreatePost(Post createPost) {
		this.createPost = createPost;
	}

	public Post getCreatePost() {
		return createPost;
	}

	public void setCreatePosts(List<Post> createPosts) {
		this.createPosts = createPosts;
	}

	public List<Post> getCreatePosts() {
		return createPosts;
	}

	@Override
	public String toString() {
		return "MutationType {" + "createBoard: " + createBoard + ", " + "createTopic: " + createTopic + ", "
				+ "createPost: " + createPost + ", " + "createPosts: " + createPosts + "}";
	}

	/**
	 * Enum of field names
	 */
	public static enum Field implements GraphQLField {
		CreateBoard("createBoard"), CreateTopic("createTopic"), CreatePost("createPost"), CreatePosts("createPosts");

		private String fieldName;

		Field(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String getFieldName() {
			return fieldName;
		}

		@Override
		public Class<?> getGraphQLType() {
			return this.getClass().getDeclaringClass();
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder
	 */
	public static class Builder {
		private Board createBoard;
		private Topic createTopic;
		private Post createPost;
		private List<Post> createPosts;

		public Builder withCreateBoard(Board createBoard) {
			this.createBoard = createBoard;
			return this;
		}

		public Builder withCreateTopic(Topic createTopic) {
			this.createTopic = createTopic;
			return this;
		}

		public Builder withCreatePost(Post createPost) {
			this.createPost = createPost;
			return this;
		}

		public Builder withCreatePosts(List<Post> createPosts) {
			this.createPosts = createPosts;
			return this;
		}

		public MutationTypeResponse build() {
			MutationTypeResponse object = new MutationTypeResponse();
			object.setCreateBoard(createBoard);
			object.setCreateTopic(createTopic);
			object.setCreatePost(createPost);
			object.setCreatePosts(createPosts);
			return object;
		}
	}

	@Override
	public void setExtensions(JsonNode extensions) {
		// TODO Auto-generated method stub

	}
}
