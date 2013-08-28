package org.library.config.data;

import javax.sql.DataSource;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.JacksonRepositoryPopulatorFactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * A {@link DataConfig} for the development {@link Environment}
 * 
 * @author dylants
 * 
 */
@Configuration
@Profile("development")
public class DevelopmentDataConfig implements DataConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @Bean
    public DataSource dataSource() {
        logger.info("for development, using an HSQL DataSource");
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // use Hibernate
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.HSQL);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(vendorAdapter);
        // scan from root package for repository beans
        em.setPackagesToScan("org.library");
        // set the data source (database configuration)
        em.setDataSource(dataSource());

        return em;
    }

    /**
     * Pre-populate the database with the information specified in the data/data.json file
     * 
     * @return The {@link JacksonRepositoryPopulatorFactoryBean} which will perform the operations
     *         necessary to pre-populate the database
     */
    @Bean
    public JacksonRepositoryPopulatorFactoryBean repositoryPopulator() {
        String dataLocation = "data/data.json";
        logger.info("populating the repository with the data found in {}", dataLocation);
        Resource sourceData = new ClassPathResource(dataLocation);

        JacksonRepositoryPopulatorFactoryBean factory = new JacksonRepositoryPopulatorFactoryBean();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        factory.setMapper(mapper);
        factory.setResources(new Resource[]{sourceData});
        return factory;
    }
}