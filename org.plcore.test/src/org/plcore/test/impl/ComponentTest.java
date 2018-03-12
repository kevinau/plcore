package org.plcore.test.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.plcore.test.After;
import org.plcore.test.Before;
import org.plcore.test.IResultSet;
import org.plcore.test.Test;


public class ComponentTest {

  private static void validateTestMethod(Class<?> klass, Method method) {
    if (!method.getReturnType().equals(void.class)) {
      throw new IllegalArgumentException(
          "Test method '" + klass.getCanonicalName() + "." + method.getName() + "' should be a void method");
    }
  }

  private static final Object[] noArgs = new Object[0];


  private static void invoke(Method method, Object component, Object[] args, IResultSet result) {
    try {
      method.setAccessible(true);
      method.invoke(component, args);
    } catch (IllegalAccessException | IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      result.setFailure(ex.getCause());
    }
  }


  public static void test(Object component, IResultSet resultSet) {
    Class<?> klass = component.getClass();
    resultSet = resultSet.getResult(klass.getPackage().toString());
    resultSet = resultSet.getResult(klass.getSimpleName());
    Method[] methods = klass.getDeclaredMethods();

    // Collect @Before and @After methods
    List<Method> befores = new ArrayList<>();
    List<Method> afters = new ArrayList<>();

    for (Method method : methods) {
      Before beforeAnn = method.getAnnotation(Before.class);
      if (beforeAnn != null) {
        validateTestMethod(klass, method);
        befores.add(method);
      }
      After afterAnn = method.getAnnotation(After.class);
      if (afterAnn != null) {
        validateTestMethod(klass, method);
        afters.add(method);
      }
    }

    // IResultSet result1 = resultSet.getResult(method.getName());
    // try {
    // method.setAccessible(true);
    // method.invoke(component);
    // } catch (IllegalAccessException | IllegalArgumentException ex) {
    // throw new RuntimeException(ex);
    // } catch (InvocationTargetException ex) {
    // result1.setFailure(ex.getCause());
    // skipAll = true;
    // }

    // Run test methods
    for (Method method : methods) {
      Test testAnn = method.getAnnotation(Test.class);
      if (testAnn != null) {
        validateTestMethod(klass, method);
        IResultSet result1 = resultSet.getResult(method.getName());
        for (Method before : befores) {
          invoke(before, component, noArgs, result1);
          if (result1.isFailure()) {
            // An error here causes the following test (and any afters) are skipped
            continue;
          }
        }

        String dataProvider = testAnn.dataProvider();
        if (dataProvider.length() > 0) {
          IResultSet resultSet1 = resultSet.getResult(dataProvider);
          Object[][] args = new Object[0][0]; // getArguments(dataProvider);
          for (int i = 0; i < args.length; i++) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(i);
            for (int j = 0; j < args[i].length; j++) {
              buffer.append(' ');
              buffer.append(args[i][j]);
            }
            IResultSet result2 = resultSet1.getResult(i + ":" + buffer);
            invoke(method, component, args[i], result2);
          }
        } else {
          invoke(method, component, noArgs, result1);
        }
      }
    }
  }

}
