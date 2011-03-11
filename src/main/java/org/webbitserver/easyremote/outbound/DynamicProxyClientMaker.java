package org.webbitserver.easyremote.outbound;

import org.webbitserver.CometConnection;
import org.webbitserver.easyremote.Remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Generates an implementation of an outbound interface using a dynamic proxy.
 */
public abstract class DynamicProxyClientMaker implements ClientMaker {

    protected abstract String createMessage(Method method, Object[] args);

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T implement(Class<T> type, CometConnection connection) {
        validateType(type);
        return (T) Proxy.newProxyInstance(classLoader(),
                new Class<?>[]{type},
                createInvocationHandler(type, connection));
    }

    protected void validateType(Class<?> type) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException(type.getName() + " is not an interface");
        }
        if (type.getAnnotation(Remote.class) == null) {
            throw new IllegalArgumentException("Interface " + type.getName() + " not marked with " + Remote.class.getName() + " annotation");
        }
    }

    protected ClassLoader classLoader() {
        return getClass().getClassLoader();
    }

    @SuppressWarnings({"UnusedDeclaration"}) // Type not used here, but made available to subclasses.
    protected InvocationHandler createInvocationHandler(final Class<?> type,
                                                        final CometConnection connection) {
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(connection, args);
                } else {
                    String msg = createMessage(method, args);
                    connection.send(msg);
                    return null;
                }
            }
        };
    }


}
