package net.ttddyy.dsproxy.proxy;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3.1
 */
public class ParameterSetOperationTest {

    //setNull from PreparedStatement: void setNull(int parameterIndex, int sqlType) throws SQLException;
    private static Method SET_NULL_METHOD = ReflectionUtils.findMethod(PreparedStatement.class, "setNull", new Class[]{int.class, int.class});

    //setInt from PreparedStatement: void setInt(int parameterIndex, int x) throws SQLException;
    private static Method SET_INT_METHOD = ReflectionUtils.findMethod(PreparedStatement.class, "setInt", new Class[]{int.class, int.class});

    //setLong from CallableStatement: void setLong(String parameterName, long x) throws SQLException;
    private static Method SET_LONG_METHOD = ReflectionUtils.findMethod(CallableStatement.class, "setLong", new Class[]{String.class, long.class});

    @Test
    public void getParameterNameOrIndexAsStringWithInt() {
        ParameterSetOperation operation = new ParameterSetOperation(SET_INT_METHOD, new Object[]{1, 100});
        assertThat(operation.getParameterNameOrIndexAsString()).isEqualTo("1");
    }

    @Test
    public void getParameterNameOrIndexAsStringWithString() {
        ParameterSetOperation operation = new ParameterSetOperation(SET_LONG_METHOD, new Object[]{"parameterName", 50L});
        assertThat(operation.getParameterNameOrIndexAsString()).isEqualTo("parameterName");
    }

    @Test
    public void getParameterValueWithSetNull() {
        ParameterSetOperation operation;

        operation = new ParameterSetOperation(SET_NULL_METHOD, new Object[]{1, Types.VARCHAR});
        assertThat(operation.getParameterValue()).isEqualTo("NULL(VARCHAR)");

        // with unrecognized type
        operation = new ParameterSetOperation(SET_NULL_METHOD, new Object[]{1, 999});
        assertThat(operation.getParameterValue()).isEqualTo("NULL");
    }

    @Test
    public void getParameterValueWithNonSetNullMethod() {
        ParameterSetOperation operation;

        operation = new ParameterSetOperation(SET_INT_METHOD, new Object[]{1, 100});
        assertThat(operation.getParameterValue()).isEqualTo(100);
    }

}