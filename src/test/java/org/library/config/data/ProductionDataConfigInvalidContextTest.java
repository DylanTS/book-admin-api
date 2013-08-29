package org.library.config.data;

import javax.naming.NamingException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class ProductionDataConfigInvalidContextTest {

    @BeforeClass
    public static void setupBeforeClass() throws NamingException {
        SimpleNamingContextBuilder builder = SimpleNamingContextBuilder
                .emptyActivatedContextBuilder();
        builder.bind("java:comp/env/jdbc/bad",
                new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build());
    }

    @AfterClass
    public static void tearDownAfterClass() throws NamingException {
        SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    }

    private ProductionDataConfig dataConfig = null;

    @Before
    public void setupBefore() {
        this.dataConfig = new ProductionDataConfig();
    }

    @Test(expected = RuntimeException.class)
    public void testDataSource() {
        this.dataConfig.dataSource();
    }
}