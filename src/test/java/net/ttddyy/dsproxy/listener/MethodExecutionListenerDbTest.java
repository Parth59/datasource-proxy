package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.DbResourceCleaner;
import net.ttddyy.dsproxy.DatabaseTest;
import net.ttddyy.dsproxy.DbTestUtils;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
@DatabaseTest
public class MethodExecutionListenerDbTest {

    private DataSource jdbcDataSource;

    private DbResourceCleaner cleaner;

    public MethodExecutionListenerDbTest(DbResourceCleaner cleaner) {
        this.cleaner = cleaner;
    }

    @BeforeEach
    public void setup() throws Exception {
        // real datasource
        jdbcDataSource = DbTestUtils.getDataSourceWithData();
    }

    @AfterEach
    public void teardown() throws Exception {
        DbTestUtils.shutdown(jdbcDataSource);
    }


    @Test
    public void replaceMethodArgument() throws Throwable {
        ProxyDataSourceListener methodListener = new ProxyDataSourceListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {

                // replace query to find id=2
                if ("executeQuery".equals(executionContext.getMethod().getName())) {
                    executionContext.setMethodArgs(new Object[]{"select * from emp where id=2"});
                }
            }
        };

        ProxyDataSource ds = ProxyDataSourceBuilder.create(this.jdbcDataSource).methodListener(methodListener).build();
        Connection conn = ds.getConnection();
        Statement statement = conn.createStatement();
        this.cleaner.add(conn);
        this.cleaner.add(statement);

        ResultSet rs = statement.executeQuery("select * from emp where id=1");
        rs.next();
        assertThat(rs.getInt("id")).isEqualTo(2);
        assertThat(rs.getString("name")).isEqualTo("bar");

    }
}
