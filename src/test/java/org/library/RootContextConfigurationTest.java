package org.library;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.library.config.data.DevelopmentDataConfig;
import org.library.config.data.ProductionDataConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.init.JacksonRepositoryPopulatorFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebApplicationInitializer.RootContextConfiguration.class,
        DevelopmentDataConfig.class, ProductionDataConfig.class})
@ActiveProfiles("development")
@WebAppConfiguration
public class RootContextConfigurationTest {

    @Autowired
    public PlatformTransactionManager transactionManager;
    @Autowired
    public JacksonRepositoryPopulatorFactoryBean repositoryPopulator;

    @Test
    public void testTransactionManager() {
        Assert.assertNotNull("transaction manager must not be null", transactionManager);
    }

    @Test
    public void testRepositoryPopulator() {
        Assert.assertNotNull("repositoryPopulator must not be null", repositoryPopulator);
    }

}