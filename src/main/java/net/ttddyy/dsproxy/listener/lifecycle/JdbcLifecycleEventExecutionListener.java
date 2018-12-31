package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Holder for {@link JdbcLifecycleEventListener} and adapt it to {@link ProxyDataSourceListener}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public class JdbcLifecycleEventExecutionListener implements ProxyDataSourceListener {

    private JdbcLifecycleEventListener delegate;

    public JdbcLifecycleEventExecutionListener(JdbcLifecycleEventListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        this.delegate.beforeMethod(executionContext);
        methodCallback(executionContext, true);
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        methodCallback(executionContext, false);
        this.delegate.afterMethod(executionContext);
    }

    @Override
    public void beforeQuery(QueryExecutionContext executionContext) {
        this.delegate.beforeQuery(executionContext);
    }

    @Override
    public void afterQuery(QueryExecutionContext executionContext) {
        this.delegate.afterQuery(executionContext);
    }

    private void methodCallback(MethodExecutionContext methodContext, boolean isBefore) {
        Method method = methodContext.getMethod();
        Object proxyTarget = methodContext.getTarget();

        // dynamically invoke corresponding callback method on JdbcLifecycleEventListener.
        Method lifecycleMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, proxyTarget, isBefore);
        try {
            lifecycleMethod.invoke(this.delegate, methodContext);
        } catch (InvocationTargetException ex) {
            throw new DataSourceProxyException(ex.getTargetException());
        } catch (Exception ex) {
            throw new DataSourceProxyException(ex);
        }

    }

    public void setDelegate(JdbcLifecycleEventListener delegate) {
        this.delegate = delegate;
    }

    public JdbcLifecycleEventListener getDelegate() {
        return delegate;
    }
}
