package com.rest.session;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class SessionManager {

    static SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory() {

        try {
            PropertiesConfiguration config = new PropertiesConfiguration("database.properties");
            Properties props = new Properties();
            props.setProperty("hibernate.connection.password", config.getString("database.password"));
            props.setProperty("hibernate.connection.username", config.getString("database.username"));
            props.setProperty("hibernate.connection.url", "jdbc:postgresql://" + config.getString("database.host") + "/" + config.getString("database.name"));
            if (sessionFactory == null) {
                sessionFactory = new Configuration().addProperties(props).configure()
                        .buildSessionFactory();
            }
            return sessionFactory;
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
