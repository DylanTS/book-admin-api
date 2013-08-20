package org.example;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * This somewhat takes the place of a traditional web.xml file, along with Spring's XML
 * configuration, allowing us to configure Spring's dispatcher servlet for our web app, supply
 * configuration for that servlet ( {@link WebApplicationInitializer.ServletContextConfiguration}),
 * while also supplying a root configuration (
 * {@link WebApplicationInitializer.RootContextConfiguration}). This uses Spring's
 * {@link AbstractAnnotationConfigDispatcherServletInitializer} to configure and initialize the
 * dispatcher servlet.
 * 
 * @author DylanTS
 * 
 */
public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootContextConfiguration.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{ServletContextConfiguration.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * This is the application's root configuration, which will be available in the root context.
     * This configuration's responsibility is to enable JPA and create the JPA beans.
     * 
     * @author DylanTS
     * 
     */
    @Configuration
    @ComponentScan(includeFilters = @Filter(Service.class), useDefaultFilters = false)
    @EnableJpaRepositories
    @EnableTransactionManagement
    public static class RootContextConfiguration {

        /**
         * Creates a {@link DataSource}
         * 
         * @return The {@link DataSource} to use in this application
         */
        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            // state the URL, username, and password of your MySQL database
            dataSource.setUrl("jdbc:mysql://localhost:3306/book-admin-api");
            dataSource.setUsername("root");
            dataSource.setPassword("password");
            return dataSource;
        }

        /**
         * Creates and configures the EntityManager factory bean, and uses it to scan for
         * repositories within this package and child packages.
         * 
         * @return The {@link LocalContainerEntityManagerFactoryBean} for this application
         */
        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            // use Hibernate
            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            vendorAdapter.setDatabase(Database.MYSQL);
            vendorAdapter.setGenerateDdl(true);

            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setJpaVendorAdapter(vendorAdapter);
            // scan from here for repository beans
            em.setPackagesToScan(getClass().getPackage().getName());
            // set the data source (database configuration)
            em.setDataSource(dataSource());

            // additional configuration properties
            em.setJpaProperties(new Properties() {
                private static final long serialVersionUID = 44378104567306969L;
                {
                    // Set the dialect to MySQL
                    setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
                    // on each start, this will wipe out existing tables and create new
                    // use "update" to only update existing tables and not start new
                    setProperty("hibernate.hbm2ddl.auto", "create");
                }
            });

            return em;
        }

        /**
         * Use Spring's Transaction Manager
         * 
         * @return The {@link PlatformTransactionManager} for this application
         */
        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
            return transactionManager;
        }
    }

    /**
     * The configuration for our web application servlet, enabling Spring's Web MVC support while
     * also scanning for {@link Controller}s and adding those to this servlet context.
     * 
     * @author DylanTS
     * 
     */
    @Configuration
    @EnableWebMvc
    @ComponentScan(includeFilters = @Filter(Controller.class), useDefaultFilters = false)
    public static class ServletContextConfiguration {

    }
}