package graphql.java.client.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author generated by graphql-maven-plugin
 */
public abstract class QueryType {

	/** Logger for this class */
	private static Logger logger = LogManager.getLogger();

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).
	 */
	public Character hero(Episode episode) {
		if (logger.isTraceEnabled()) {
			logger.trace("Executing of query 'hero' with parameters: {} ", episode);
		} else if (logger.isDebugEnabled()) {
			logger.debug("Executing of query 'hero'");
		}

		return doHero(episode);
	}

	/**
	 * This method is called when this query is called. The implementation code of the server part should create a class
	 * extending this query {@link QueryType), and implement the actual query.
	 */
	abstract protected Character doHero(Episode episode);

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).
	 */
	public Human human(String id) {
		if (logger.isTraceEnabled()) {
			logger.trace("Executing of query 'human' with parameters: {} ", id);
		} else if (logger.isDebugEnabled()) {
			logger.debug("Executing of query 'human'");
		}

		return doHuman(id);
	}

	/**
	 * This method is called when this query is called. The implementation code of the server part should create a class
	 * extending this query {@link QueryType), and implement the actual query.
	 */
	abstract protected Human doHuman(String id);

	/**
	 * This method is expected by the graphql-java framework. It will be called when this query is called. It offers a
	 * logging of the call (if in debug mode), or of the call and its parameters (if in trace mode).
	 */
	public Droid droid(String id) {
		if (logger.isTraceEnabled()) {
			logger.trace("Executing of query 'droid' with parameters: {} ", id);
		} else if (logger.isDebugEnabled()) {
			logger.debug("Executing of query 'droid'");
		}

		return doDroid(id);
	}

	/**
	 * This method is called when this query is called. The implementation code of the server part should create a class
	 * extending this query {@link QueryType), and implement the actual query.
	 */
	abstract protected Droid doDroid(String id);

}