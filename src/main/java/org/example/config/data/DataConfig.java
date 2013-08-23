package org.example.config.data;

import javax.sql.DataSource;

import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Provides database configuration information, based on the current {@link Environment}
 * 
 * @author dylants
 * 
 */
public interface DataConfig {

    /**
     * Returns the {@link DataSource} for this application
     * 
     * @return The {@link DataSource} for this application
     */
    public DataSource dataSource();

    /**
     * Creates and configures the EntityManager factory bean, and uses it to scan for repositories
     * within this package and child packages.
     * 
     * @return The {@link LocalContainerEntityManagerFactoryBean} for this application
     */
    public LocalContainerEntityManagerFactoryBean entityManagerFactory();
}
