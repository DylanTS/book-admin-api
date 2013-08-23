package org.example;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.example.config.data.DataConfig;
import org.example.config.exception.IncludeMessageSourceExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.init.JacksonRepositoryPopulatorFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
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
 * @author dylants
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
        return new String[]{"/api/*"};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // set the default profile to "development"
        servletContext.setInitParameter("spring.profiles.default", "development");
        super.onStartup(servletContext);
    }

    /**
     * This is the application's root configuration, which will be available in the root context.
     * This configuration's responsibility is to enable JPA and create the JPA beans.
     * 
     * @author dylants
     * 
     */
    @Configuration
    @ComponentScan(includeFilters = @Filter({Service.class, Configuration.class}), useDefaultFilters = false)
    @EnableJpaRepositories
    public static class RootContextConfiguration {

        /**
         * Part of the JPA configuration is done based on the {@link Environment}, and will be
         * supplied via this {@link DataConfig} bean
         */
        @Autowired
        private DataConfig dataConfig;

        /**
         * Use Spring's Transaction Manager
         * 
         * @return The {@link PlatformTransactionManager} for this application
         */
        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(this.dataConfig.entityManagerFactory()
                    .getObject());
            return transactionManager;
        }

        /**
         * Pre-populate the database with the information specified in the data/data.json file
         * 
         * @return The {@link JacksonRepositoryPopulatorFactoryBean} which will perform the
         *         operations necessary to pre-populate the database
         */
        @Bean
        public JacksonRepositoryPopulatorFactoryBean repositoryPopulator() {
            Resource sourceData = new ClassPathResource("data/data.json");

            JacksonRepositoryPopulatorFactoryBean factory = new JacksonRepositoryPopulatorFactoryBean();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            factory.setMapper(mapper);
            factory.setResources(new Resource[]{sourceData});
            return factory;
        }
    }

    /**
     * The configuration for our web application servlet, enabling Spring's Web MVC support while
     * also scanning for {@link Controller}s and adding those to this servlet context.
     * 
     * @author dylants
     * 
     */
    @Configuration
    @EnableWebMvc
    @ComponentScan(includeFilters = @Filter(Controller.class), useDefaultFilters = false)
    public static class ServletContextConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void configureHandlerExceptionResolvers(
                List<HandlerExceptionResolver> exceptionResolvers) {
            /*
             * Our servlet creates APIs which sometimes require validation on the inputs. To better
             * inform the user on errors that might occur during validation, we've included error
             * messages, which can be retrieved via a MessageSource. The new exception resolver
             * we're using below includes this message in the response.
             */
            IncludeMessageSourceExceptionResolver exceptionResolver = new IncludeMessageSourceExceptionResolver(
                    messageSource());
            exceptionResolvers.add(exceptionResolver);
        }

        @Bean
        public ReloadableResourceBundleMessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:messages/errors");
            messageSource.setCacheSeconds(10);
            return messageSource;
        }

    }
}