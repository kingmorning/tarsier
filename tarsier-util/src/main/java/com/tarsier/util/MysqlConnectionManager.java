package com.tarsier.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper to manage JDBC connections. This is just a place to keep the
 * connection management code out of the abstract sink class. Trying to keep
 * things cleaner.
 */
public class MysqlConnectionManager {

	private static final Logger LOG = LoggerFactory.getLogger(MysqlConnectionManager.class);
	private static final int CONNECTION_VALID_TIMEOUT = 500;
	private String sql;
	private String url;
	private String user;
	private String password;
	private Connection connection;
	private AtomicBoolean starting=new AtomicBoolean(false);
	
	
	public boolean isStarted(){
		return starting.get();
	}
	
	public MysqlConnectionManager(Map<String,String> map){
		this.url=map.get("mysql.url");
		this.user=map.get("mysql.user");
		this.password=map.get("mysql.password");
		this.sql=map.get("mysql.sql");
	}

	public  MysqlConnectionManager(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public Connection getConnection() {
		return connection;
	}

	/**
	 * Start the connection manager: load the driver and create a connection.
	 * Note that the sink counter must have been started before calling this
	 * method.
	 */
	public void start(){
		if(starting.compareAndSet(false, true)){
			LOG.info("start to load driver and create connection.");
			try {
				Class.forName("com.mysql.jdbc.Driver");
				createConnection();
			} catch (final Exception e) {
				LOG.error("Unable to create JDBC connection to {}.", url, e);
				closeConnection();
				starting.set(false);
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Ensure that the current JDBC connection is valid. If it's not, create
	 * one.
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void ensureConnectionValid() throws SQLException {
//		LOG.debug("Testing JDBC connection validity to: {}.", url);
		if (connection == null || !connection.isValid(CONNECTION_VALID_TIMEOUT)) {
			closeConnection();
			createConnection();
		}
	}

	/**
	 * Closes the current JDBC connection.
	 */
	public void closeConnection() {
		if (starting.compareAndSet(true, false) && connection != null) {
			LOG.debug("Closing JDBC connection to: {}.", url);
			try {
				connection.close();
			} catch (final SQLException e) {
				starting.set(true);
				LOG.warn("Unable to close JDBC connection to {}.", url, e);
			}
			connection = null;
		}
	}

	private void createConnection() throws SQLException {
		LOG.debug("Creating JDBC connection to: {}.", url);
		connection = DriverManager.getConnection(url, user, password);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}