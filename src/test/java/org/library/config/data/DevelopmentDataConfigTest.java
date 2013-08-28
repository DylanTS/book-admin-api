package org.library.config.data;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.library.WebApplicationInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebApplicationInitializer.RootContextConfiguration.class,
        DevelopmentDataConfig.class})
@ActiveProfiles("development")
@WebAppConfiguration
public class DevelopmentDataConfigTest {

    @Autowired
    private DataConfig dataConfig;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Test
    public void testDataConfig() {
        Assert.assertNotNull("DataConfig must not be null", dataConfig);
        Assert.assertNotNull("DataSource must not be null", dataConfig.dataSource());
        Assert.assertNotNull("EntityManagerFactory must not be null",
                dataConfig.entityManagerFactory());
    }

    @Test
    public void testDataSource() throws SQLException {
        Assert.assertNotNull("DataSource must not be null", dataSource);
        Assert.assertTrue("database driver must be HSQL", dataSource.getConnection().getMetaData()
                .getDriverName().indexOf("HSQL") != -1);
    }

    @Test
    public void testEntityManagerFactory() {
        Assert.assertNotNull("EntityManagerFactory must not be null", entityManagerFactory);
        Assert.assertEquals("data sources must match", dataSource,
                entityManagerFactory.getDataSource());
    }

}