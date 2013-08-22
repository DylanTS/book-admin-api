package org.example.config;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * A {@link DataConfig} for the production {@link Environment}
 * 
 * @author dylants
 * 
 */
@Configuration
@Profile("production")
public class ProductionDataConfig implements DataConfig {

    @Override
    @Bean
    public DataSource dataSource() {
        try {
            // attempt to load the DataSource via JNDI
            Context ctx = new InitialContext();
            return (DataSource) ctx.lookup("java:comp/env/jdbc/web");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // use Hibernate
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(vendorAdapter);
        // scan from here for repository beans
        em.setPackagesToScan("org.example");
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
}