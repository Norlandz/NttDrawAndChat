package com.redfrog.note.util;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

//____________________________________________________________________________________________________
public class MethodReferenceUtils {

  @FunctionalInterface
  public static interface MethodRefWith1Arg<T, A1> { void call(T t, A1 a1); }

  @FunctionalInterface
  public static interface MethodRefWith2Arg<T, A1, A2> { void call(T t, A1 a1, A2 a2); }

  @FunctionalInterface
  public static interface MethodRefWith3Arg<T, A1, A2, A3> { void call(T t, A1 a1, A2 a2, A3 a3); }

  @FunctionalInterface
  public static interface MethodRefWith4Arg<T, A1, A2, A3, A4> { void call(T t, A1 a1, A2 a2, A3 a3, A4 a4); }

  @FunctionalInterface
  public static interface MethodRefWith5Arg<T, A1, A2, A3, A4, A5> { void call(T t, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5); }

  public static <T, A1> Method getReferencedMethod(Class<T> clazz, MethodRefWith1Arg<T, A1> methodRef) { return findReferencedMethod(clazz, t -> methodRef.call(t, null)); }

  public static <T, A1, A2> Method getReferencedMethod(Class<T> clazz, MethodRefWith2Arg<T, A1, A2> methodRef) { return findReferencedMethod(clazz, t -> methodRef.call(t, null, null)); }

  public static <T, A1, A2, A3> Method getReferencedMethod(Class<T> clazz, MethodRefWith3Arg<T, A1, A2, A3> methodRef) { return findReferencedMethod(clazz, t -> methodRef.call(t, null, null, null)); }

  public static <T, A1, A2, A3, A4> Method getReferencedMethod(Class<T> clazz, MethodRefWith4Arg<T, A1, A2, A3, A4> methodRef) { return findReferencedMethod(clazz, t -> methodRef.call(t, null, null, null, null)); }

  public static <T, A1, A2, A3, A4, A5> Method getReferencedMethod(Class<T> clazz, MethodRefWith5Arg<T, A1, A2, A3, A4, A5> methodRef) { return findReferencedMethod(clazz, t -> methodRef.call(t, null, null, null, null, null)); }

  @SuppressWarnings("unchecked")
  private static <T> Method findReferencedMethod(Class<T> clazz, Consumer<T> invoker) {
    AtomicReference<Method> ref = new AtomicReference<>();
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(clazz);
    enhancer.setCallback(
                         new MethodInterceptor()
                           {
                             @Override
                             public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                               ref.set(method);
                               return null;
                             }
                           });
    try {
      invoker.accept((T) enhancer.create());
    } catch (ClassCastException e) {
      throw new IllegalArgumentException(String.format("Invalid method reference on class [%s]", clazz));
    }

    Method method = ref.get();
    if (method == null) {
      throw new IllegalArgumentException(String.format("Invalid method reference on class [%s]", clazz));
      
    }

    return method;
  }
}