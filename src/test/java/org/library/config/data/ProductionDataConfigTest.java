package org.library.config.data;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProductionDataConfig.class)
@ActiveProfiles("production")
public class ProductionDataConfigTest {

    @Autowired
    private DataConfig dataConfig;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @BeforeClass
    public static void setupBeforeClass() throws NamingException {
        SimpleNamingContextBuilder builder = SimpleNamingContextBuilder
                .emptyActivatedContextBuilder();
        builder.bind("java:comp/env/jdbc/web",
                new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build());
    }

    @AfterClass
    public static void tearDownAfterClass() throws NamingException {
        SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    }

    @Test
    public void testDataConfig() {
        Assert.assertNotNull("data config must not be null", dataConfig);
        Assert.assertNotNull("DataSource must not be null", dataConfig.dataSource());
        Assert.assertNotNull("EntityManagerFactory must not be null",
                dataConfig.entityManagerFactory());
    }

    @Test
    public void testDataSource() throws SQLException {
        Assert.assertNotNull("DataSource must not be null", dataSource);
    }

    @Test
    public void testEntityManagerFactory() {
        Assert.assertNotNull("EntityManagerFactory must not be null", entityManagerFactory);
        Assert.assertEquals("data sources must match", dataSource,
                entityManagerFactory.getDataSource());
    }
}