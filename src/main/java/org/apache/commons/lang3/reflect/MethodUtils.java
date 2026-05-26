/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.stream.LangCollectors;
import org.apache.commons.lang3.stream.Streams;

/**
 * Utility reflection methods focused on {@link Method}s, originally from Commons BeanUtils.
 * Differences from the BeanUtils version may be noted, especially where similar functionality
 * already existed within Lang.
 *
 * <h2>Known Limitations</h2>
 * <h3>Accessing Public Methods In A Default Access Superclass</h3>
 * <p>There is an issue when invoking {@code public} methods contained in a default access superclass on JREs prior to 1.4.
 * Reflection locates these methods fine and correctly assigns them as {@code public}.
 * However, an {@link IllegalAccessException} is thrown if the method is invoked.</p>
 *
 * <p>
 * {@link MethodUtils} contains a workaround for this situation.
 * It will attempt to call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} on this method.
 * If this call succeeds, then the method can be invoked as normal.
 * This call will only succeed when the application has sufficient security privileges.
 * If this call fails then the method may fail.
 * </p>
 *
 * @since 2.5
 */
public class MethodUtils {

    private static final Comparator<Method> METHOD_BY_SIGNATURE = Comparator.comparing(Method::toString);

    /**
     * Computes the aggregate number of inheritance hops between assignable argument class types.  Returns -1
     * if the arguments aren't assignable.  Fills a specific purpose for getMatchingMethod and is not generalized.
     *
     * @param fromClassArray the Class array to calculate the distance from.
     * @param toClassArray the Class array to calculate the distance to.
     * @return the aggregate number of inheritance hops between assignable argument class types.
     */
    private static int distance(final Class<?>[] fromClassArray, final Class<?>[] toClassArray) {
        int answer = 0;
        if (!ClassUtils.isAssignable(fromClassArray, toClassArray, true)) {
            return -1;
        }
        for (int offset = 0; offset < fromClassArray.length; offset++) {
            // Note InheritanceUtils.distance() uses different scoring system.
            final Class<?> aClass = fromClassArray[offset];
            final Class<?> toClass = toClassArray[offset];
            if (aClass == null || aClass.equals(toClass)) {
                continue;
            }
            if (ClassUtils.isAssignable(aClass, toClass, true) && !ClassUtils.isAssignable(aClass, toClass, false)) {
                answer++;
            } else {
                answer += 2;
            }
        }
        return answer;
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via reflection) that implements the specified Method. If no such method can be found, return
     * {@code null}.
     *
     * @param cls The implementing class, may be null.
     * @param method The method that we wish to call, may be null.
     * @return The accessible method or null.
     * @since 3.19.0
     */
    public static Method getAccessibleMethod(final Class<?> cls, final Method method) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via reflection) with given name and parameters. If no such method can be found, return
     * {@code null}. This is just a convenience wrapper for {@link #getAccessibleMethod(Method)}.
     *
     * @param cls            get method from this class.
     * @param methodName     get method with this name.
     * @param parameterTypes with these parameters types.
     * @return The accessible method.
     */
    public static Method getAccessibleMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via reflection) that implements the specified Method. If no such method can be found, return
     * {@code null}.
     *
     * @param method The method that we wish to call, may be null.
     * @return The accessible method
     */
    public static Method getAccessibleMethod(final Method method) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified method, by scanning through
     * all implemented interfaces and subinterfaces. If no such method
     * can be found, return {@code null}.
     *
     * <p>
     * There isn't any good reason why this method must be {@code private}.
     * It is because there doesn't seem any reason why other classes should
     * call this rather than the higher level methods.
     * </p>
     *
     * @param cls Parent class for the interfaces to be checked.
     * @param methodName Method name of the method we wish to call.
     * @param parameterTypes The parameter type signatures.
     * @return the accessible method or {@code null} if not found.
     */
    private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        // Search up the superclass chain
        for (; cls != null; cls = cls.getSuperclass()) {
            // Check the implemented interfaces of the parent class
            final Class<?>[] interfaces = cls.getInterfaces();
            for (final Class<?> anInterface : interfaces) {
                // Is this interface public?
                if (!ClassUtils.isPublic(anInterface)) {
                    continue;
                }
                // Does the method exist on this interface?
                try {
                    return anInterface.getDeclaredMethod(methodName, parameterTypes);
                } catch (final NoSuchMethodException ignored) {
                    /*
                     * Swallow, if no method is found after the loop then this method returns null.
                     */
                }
                // Recursively check our parent interfaces
                final Method method = getAccessibleMethodFromInterfaceNest(anInterface, methodName, parameterTypes);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via
     * reflection) by scanning through the superclasses. If no such method
     * can be found, return {@code null}.
     *
     * @param cls Class to be checked.
     * @param methodName Method name of the method we wish to call.
     * @param parameterTypes The parameter type signatures.
     * @return the accessible method or {@code null} if not found.
     */
    private static Method getAccessibleMethodFromSuperclass(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        Class<?> parentClass = cls.getSuperclass();
        while (parentClass != null) {
            if (ClassUtils.isPublic(parentClass)) {
                return getMethodObject(parentClass, methodName, parameterTypes);
            }
            parentClass = parentClass.getSuperclass();
        }
        return null;
    }

    /**
     * Gets a combination of {@link ClassUtils#getAllSuperclasses(Class)} and {@link ClassUtils#getAllInterfaces(Class)}, one from superclasses, one from
     * interfaces, and so on in a breadth first way.
     *
     * @param cls the class to look up, may be {@code null}.
     * @return the combined {@link List} of superclasses and interfaces in order going up from this one {@code null} if null input.
     */
    private static List<Class<?>> getAllSuperclassesAndInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final List<Class<?>> allSuperClassesAndInterfaces = new ArrayList<>();
        final List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(cls);
        int superClassIndex = 0;
        final List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(cls);
        int interfaceIndex = 0;
        while (interfaceIndex < allInterfaces.size() || superClassIndex < allSuperclasses.size()) {
            final Class<?> acls;
            if (interfaceIndex >= allInterfaces.size() || superClassIndex < allSuperclasses.size() && superClassIndex < interfaceIndex) {
                acls = allSuperclasses.get(superClassIndex++);
            } else {
                acls = allInterfaces.get(interfaceIndex++);
            }
            allSuperClassesAndInterfaces.add(acls);
        }
        return allSuperClassesAndInterfaces;
    }

    /**
     * Gets the annotation object with the given annotation type that is present on the given method or optionally on any equivalent method in super classes and
     * interfaces. Returns null if the annotation type was not present.
     *
     * <p>
     * Stops searching for an annotation once the first annotation of the specified type has been found. Additional annotations of the specified type will be
     * silently ignored.
     * </p>
     *
     * @param <A>           the annotation type.
     * @param method        the {@link Method} to query, may be null.
     * @param annotationCls the {@link Annotation} to check if is present on the method.
     * @param searchSupers  determines if a lookup in the entire inheritance hierarchy of the given class is performed if the annotation was not directly
     *                      present.
     * @param ignoreAccess  determines if underlying method has to be accessible.
     * @return the first matching annotation, or {@code null} if not found.
     * @throws NullPointerException if either the method or annotation class is {@code null}.
     * @throws SecurityException    if an underlying accessible object's method denies the request.
     * @see SecurityManager#checkPermission
     * @since 3.6
     */
    public static <A extends Annotation> A getAnnotation(final Method method, final Class<A> annotationCls, final boolean searchSupers, final boolean ignoreAccess) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static Method getInvokeMethod(final boolean forceAccess, final String methodName, final Class<?>[] parameterTypes, final Class<?> cls) {
        final Method method;
        if (forceAccess) {
            method = getMatchingMethod(cls, methodName, parameterTypes);
            AccessibleObjects.setAccessible(method);
        } else {
            method = getMatchingAccessibleMethod(cls, methodName, parameterTypes);
        }
        return method;
    }

    /**
     * Gets an accessible method that matches the given name and has compatible parameters. Compatible parameters mean that every method parameter is assignable
     * from the given parameters. In other words, it finds a method with the given name that will take the parameters given.
     *
     * <p>
     * This method is used by {@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.
     * </p>
     * <p>
     * This method can match primitive parameter by passing in wrapper classes. For example, a {@link Boolean} will match a primitive {@code boolean} parameter.
     * </p>
     *
     * @param cls            find method in this class.
     * @param methodName     find method with this name.
     * @param requestTypes find method with most compatible parameters.
     * @return The accessible method or null.
     * @throws SecurityException if an underlying accessible object's method denies the request.
     * @see SecurityManager#checkPermission
     */
    public static Method getMatchingAccessibleMethod(final Class<?> cls, final String methodName, final Class<?>... requestTypes) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a method whether or not it's accessible. If no such method can be found, return {@code null}.
     *
     * @param cls            The class that will be subjected to the method search.
     * @param methodName     The method that we wish to call.
     * @param parameterTypes Argument class types.
     * @throws IllegalStateException if there is no unique result.
     * @throws NullPointerException  if the class is {@code null}.
     * @return The method.
     * @since 3.5
     */
    public static Method getMatchingMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a Method, or {@code null} if a checked {@link Class#getMethod(String, Class...) } exception is thrown.
     *
     * @param cls            Receiver for {@link Class#getMethod(String, Class...)}.
     * @param name           the name of the method.
     * @param parameterTypes the list of parameters.
     * @return a Method or {@code null}.
     * @see SecurityManager#checkPermission
     * @see Class#getMethod(String, Class...)
     * @since 3.15.0
     */
    public static Method getMethodObject(final Class<?> cls, final String name, final Class<?>... parameterTypes) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets all class level public methods of the given class that are annotated with the given annotation.
     *
     * @param cls           the {@link Class} to query.
     * @param annotationCls the {@link Annotation} that must be present on a method to be matched.
     * @return a list of Methods (possibly empty).
     * @throws NullPointerException if the class or annotation are {@code null}.
     * @since 3.4
     */
    public static List<Method> getMethodsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets all methods of the given class that are annotated with the given annotation.
     *
     * @param cls           the {@link Class} to query.
     * @param annotationCls the {@link Annotation} that must be present on a method to be matched.
     * @param searchSupers  determines if a lookup in the entire inheritance hierarchy of the given class should be performed.
     * @param ignoreAccess  determines if non-public methods should be considered.
     * @return a list of Methods (possibly empty).
     * @throws NullPointerException if either the class or annotation class is {@code null}.
     * @since 3.6
     */
    public static List<Method> getMethodsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls, final boolean searchSupers, final boolean ignoreAccess) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets all class level public methods of the given class that are annotated with the given annotation.
     *
     * @param cls           the {@link Class} to query.
     * @param annotationCls the {@link java.lang.annotation.Annotation} that must be present on a method to be matched.
     * @return an array of Methods (possibly empty).
     * @throws NullPointerException if the class or annotation are {@code null}
     * @since 3.4
     */
    public static Method[] getMethodsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets all methods of the given class that are annotated with the given annotation.
     *
     * @param cls           the {@link Class} to query.
     * @param annotationCls the {@link java.lang.annotation.Annotation} that must be present on a method to be matched.
     * @param searchSupers  determines if a lookup in the entire inheritance hierarchy of the given class should be performed.
     * @param ignoreAccess  determines if non-public methods should be considered.
     * @return an array of Methods (possibly empty).
     * @throws NullPointerException if the class or annotation are {@code null}.
     * @since 3.6
     */
    public static Method[] getMethodsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls, final boolean searchSupers, final boolean ignoreAccess) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the hierarchy of overridden methods down to {@code result} respecting generics.
     *
     * @param method lowest to consider.
     * @param interfacesBehavior whether to search interfaces, {@code null} {@code implies} false.
     * @return a {@code Set<Method>} in ascending order from subclass to superclass.
     * @throws NullPointerException if the specified method is {@code null}.
     * @throws SecurityException if an underlying accessible object's method denies the request.
     * @see SecurityManager#checkPermission
     * @since 3.2
     */
    public static Set<Method> getOverrideHierarchy(final Method method, final Interfaces interfacesBehavior) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a method whose parameter types match exactly the object type.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@link #getAccessibleMethod(Class, String, Class[])}.
     * </p>
     *
     * @param object     invoke method on this object.
     * @param methodName get method with this name.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws NullPointerException        Thrown if the specified {@code object} is null.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     * @since 3.4
     */
    public static Object invokeExactMethod(final Object object, final String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a method whose parameter types match exactly the object types.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@link #getAccessibleMethod(Class, String, Class[])}.
     * </p>
     *
     * @param object     invoke method on this object.
     * @param methodName get method with this name.
     * @param args       use these arguments - treat null as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws NullPointerException        Thrown if the specified {@code object} is null.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     */
    public static Object invokeExactMethod(final Object object, final String methodName, final Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a method whose parameter types match exactly the parameter types given.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@link #getAccessibleMethod(Class, String, Class[])}.
     * </p>
     *
     * @param object         Invokes a method on this object.
     * @param methodName     Gets a method with this name.
     * @param args           Method arguments - treat null as empty array.
     * @param parameterTypes Match these parameters - treat {@code null} as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws NullPointerException        Thrown if the specified {@code object} is null.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     */
    public static Object invokeExactMethod(final Object object, final String methodName, final Object[] args, final Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a {@code static} method whose parameter types match exactly the object types.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@link #getAccessibleMethod(Class, String, Class[])}.
     * </p>
     *
     * @param cls        invoke static method on this class.
     * @param methodName get method with this name.
     * @param args       use these arguments - treat {@code null} as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     */
    public static Object invokeExactStaticMethod(final Class<?> cls, final String methodName, final Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a {@code static} method whose parameter types match exactly the parameter types given.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@link #getAccessibleMethod(Class, String, Class[])}.
     * </p>
     *
     * @param cls            invoke static method on this class.
     * @param methodName     get method with this name.
     * @param args           use these arguments - treat {@code null} as empty array.
     * @param parameterTypes match these parameters - treat {@code null} as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     */
    public static Object invokeExactStaticMethod(final Class<?> cls, final String methodName, final Object[] args, final Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named method without parameters.
     *
     * <p>
     * This is a convenient wrapper for
     * {@link #invokeMethod(Object object, boolean forceAccess, String methodName, Object[] args, Class[] parameterTypes)}.
     * </p>
     *
     * @param object invoke method on this object.
     * @param forceAccess force access to invoke method even if it's not accessible.
     * @param methodName get method with this name.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws NullPointerException        Thrown if the specified {@code object} is null.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     * @see SecurityManager#checkPermission
     * @since 3.5
     */
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named method whose parameter type matches the object type.
     *
     * <p>
     * This method supports calls to methods taking primitive parameters
     * via passing in wrapping classes. So, for example, a {@link Boolean} object
     * would match a {@code boolean} primitive.
     * </p>
     * <p>
     * This is a convenient wrapper for
     * {@link #invokeMethod(Object object, boolean forceAccess, String methodName, Object[] args, Class[] parameterTypes)}.
     * </p>
     *
     * @param object invoke method on this object.
     * @param forceAccess force access to invoke method even if it's not accessible.
     * @param methodName get method with this name.
     * @param args use these arguments - treat null as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws NullPointerException        Thrown if the specified {@code object} is null.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     * @see SecurityManager#checkPermission
     * @since 3.5
     */
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName, final Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named method whose parameter type matches the object type.
     *
     * <p>
     * This method supports calls to methods taking primitive parameters
     * via passing in wrapping classes. So, for example, a {@link Boolean} object
     * would match a {@code boolean} primitive.
     * </p>
     *
     * @param object invoke method on this object.
     * @param forceAccess force access to invoke method even if it's not accessible.
     * @param methodName get method with this name.
     * @param args use these arguments - treat null as empty array.
     * @param parameterTypes match these parameters - treat null as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws NullPointerException        Thrown if the specified {@code object} is null.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     * @see SecurityManager#checkPermission
     * @since 3.5
     */
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName, final Object[] args, final Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named method without parameters.
     *
     * <p>
     * This method delegates the method search to {@link #getMatchingAccessibleMethod(Class, String, Class[])}.
     * </p>
     * <p>
     * This is a convenient wrapper for
     * {@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.
     * </p>
     *
     * @param object invoke method on this object.
     * @param methodName get method with this name.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException if there is no such accessible method.
     * @throws InvocationTargetException wraps an exception thrown by the method invoked.
     * @throws IllegalAccessException if the requested method is not accessible via reflection.
     * @throws SecurityException if an underlying accessible object's method denies the request.
     * @see SecurityManager#checkPermission
     * @since 3.4
     */
    public static Object invokeMethod(final Object object, final String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named method whose parameter type matches the object type.
     *
     * <p>
     * This method delegates the method search to {@link #getMatchingAccessibleMethod(Class, String, Class[])}.
     * </p>
     * <p>
     * This method supports calls to methods taking primitive parameters
     * via passing in wrapping classes. So, for example, a {@link Boolean} object
     * would match a {@code boolean} primitive.
     * </p>
     * <p>
     * This is a convenient wrapper for
     * {@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.
     * </p>
     *
     * @param object invoke method on this object.
     * @param methodName get method with this name.
     * @param args use these arguments - treat null as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException if there is no such accessible method.
     * @throws InvocationTargetException wraps an exception thrown by the method invoked.
     * @throws IllegalAccessException if the requested method is not accessible via reflection.
     * @throws NullPointerException if the object or method name are {@code null}.
     * @throws SecurityException if an underlying accessible object's method denies the request.
     * @see SecurityManager#checkPermission
     */
    public static Object invokeMethod(final Object object, final String methodName, final Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named method whose parameter type matches the object type.
     *
     * <p>
     * This method delegates the method search to {@link #getMatchingAccessibleMethod(Class, String, Class[])}.
     * </p>
     * <p>
     * This method supports calls to methods taking primitive parameters
     * via passing in wrapping classes. So, for example, a {@link Boolean} object
     * would match a {@code boolean} primitive.
     * </p>
     *
     * @param object invoke method on this object.
     * @param methodName get method with this name.
     * @param args use these arguments - treat null as empty array.
     * @param parameterTypes match these parameters - treat null as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws NullPointerException        Thrown if the specified {@code object} is null.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     * @see SecurityManager#checkPermission
     */
    public static Object invokeMethod(final Object object, final String methodName, final Object[] args, final Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named {@code static} method whose parameter type matches the object type.
     *
     * <p>
     * This method delegates the method search to {@link #getMatchingAccessibleMethod(Class, String, Class[])}.
     * </p>
     * <p>
     * This method supports calls to methods taking primitive parameters
     * via passing in wrapping classes. So, for example, a {@link Boolean} class
     * would match a {@code boolean} primitive.
     * </p>
     * <p>
     * This is a convenient wrapper for
     * {@link #invokeStaticMethod(Class, String, Object[], Class[])}.
     * </p>
     *
     * @param cls invoke static method on this class.
     * @param methodName get method with this name.
     * @param args use these arguments - treat {@code null} as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     * @see SecurityManager#checkPermission
     */
    public static Object invokeStaticMethod(final Class<?> cls, final String methodName, final Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Invokes a named {@code static} method whose parameter type matches the object type.
     *
     * <p>
     * This method delegates the method search to {@link #getMatchingAccessibleMethod(Class, String, Class[])}.
     * </p>
     * <p>
     * This method supports calls to methods taking primitive parameters
     * via passing in wrapping classes. So, for example, a {@link Boolean} class
     * would match a {@code boolean} primitive.
     * </p>
     *
     * @param cls invoke static method on this class.
     * @param methodName get method with this name.
     * @param args use these arguments - treat {@code null} as empty array.
     * @param parameterTypes match these parameters - treat {@code null} as empty array.
     * @return The value returned by the invoked method.
     * @throws NoSuchMethodException       Thrown if there is no such accessible method.
     * @throws IllegalAccessException      Thrown if this found {@code Method} is enforcing Java language access control and the underlying method is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if:
     *                                     <ul>
     *                                     <li>the found {@code Method} is an instance method and the specified {@code object} argument is not an instance of
     *                                     the class or interface declaring the underlying method (or of a subclass or interface implementor);</li>
     *                                     <li>the number of actual and formal parameters differ;</li>
     *                                     <li>an unwrapping conversion for primitive arguments fails; or</li>
     *                                     <li>after possible unwrapping, a parameter value can't be converted to the corresponding formal parameter type by a
     *                                     method invocation conversion.</li>
     *                                     </ul>
     * @throws InvocationTargetException   Thrown if the underlying method throws an exception.
     * @throws ExceptionInInitializerError Thrown if the initialization provoked by this method fails.
     * @see SecurityManager#checkPermission
     */
    public static Object invokeStaticMethod(final Class<?> cls, final String methodName, final Object[] args, final Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static Method requireNonNull(final Method method, final Class<?> cls, final String methodName, final Class<?>[] parameterTypes) throws NoSuchMethodException {
        if (method == null) {
            throw new NoSuchMethodException(String.format("No method: %s.%s(%s)", ClassUtils.getName(cls), methodName, Streams.of(parameterTypes).map(ClassUtils::getName).collect(LangCollectors.joining(", "))));
        }
        return method;
    }

    static Object[] toVarArgs(final Executable executable, final Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets an array of arguments in the canonical form, given an arguments array passed to a varargs method, for example an array with the declared number of
     * parameters, and whose last parameter is an array of the varargs type.
     * <p>
     * We follow the <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html#jls-5.1.2">JLS 5.1.2. Widening Primitive Conversion</a> rules.
     * </p>
     *
     * @param args                 the array of arguments passed to the varags method.
     * @param methodParameterTypes the declared array of method parameter types.
     * @return an array of the variadic arguments passed to the method.
     * @throws NoSuchMethodException       Thrown if the constructor could not be found.
     * @throws IllegalAccessException      Thrown if this {@code Constructor} object is enforcing Java language access control and the underlying constructor is
     *                                     inaccessible.
     * @throws IllegalArgumentException    Thrown if the number of actual and formal parameters differ; if an unwrapping conversion for primitive arguments
     *                                     fails; or if, after possible unwrapping, a parameter value cannot be converted to the corresponding formal parameter
     *                                     type by a method invocation conversion; if this constructor pertains to an enum type.
     * @throws InstantiationException      Thrown if a class that declares the underlying constructor represents an abstract class.
     * @throws InvocationTargetException   Thrown if an underlying constructor throws an exception.
     * @throws ExceptionInInitializerError Thrown if an initialization provoked by this method fails.
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html#jls-5.1.2">JLS 5.1.2. Widening Primitive Conversion</a>
     */
    private static Object[] toVarArgs(final Object[] args, final Class<?>[] methodParameterTypes) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final int mptLength = methodParameterTypes.length;
        if (args.length == mptLength) {
            final Object lastArg = args[args.length - 1];
            if (lastArg == null || lastArg.getClass().equals(methodParameterTypes[mptLength - 1])) {
                // The args array is already in the canonical form for the method.
                return args;
            }
        }
        // Construct a new array matching the method's declared parameter types.
        // Copy the normal (non-varargs) parameters
        final Object[] newArgs = ArrayUtils.arraycopy(args, 0, 0, mptLength - 1, () -> new Object[mptLength]);
        // Construct a new array for the variadic parameters
        final Class<?> varArgComponentType = methodParameterTypes[mptLength - 1].getComponentType();
        final Class<?> varArgComponentWrappedType = ClassUtils.primitiveToWrapper(varArgComponentType);
        final int varArgLength = args.length - mptLength + 1;
        // Copy the variadic arguments into the varargs array, converting types if needed.
        Object varArgsArray = Array.newInstance(varArgComponentWrappedType, varArgLength);
        final boolean primitiveOrWrapper = ClassUtils.isPrimitiveOrWrapper(varArgComponentWrappedType);
        for (int i = 0; i < varArgLength; i++) {
            final Object arg = args[mptLength - 1 + i];
            try {
                Array.set(varArgsArray, i, primitiveOrWrapper ? varArgComponentWrappedType.getConstructor(ClassUtils.wrapperToPrimitive(varArgComponentWrappedType)).newInstance(arg) : varArgComponentWrappedType.cast(arg));
            } catch (final InstantiationException e) {
                throw new IllegalArgumentException("Cannot convert vararg #" + i, e);
            }
        }
        if (varArgComponentType.isPrimitive()) {
            // unbox from wrapper type to primitive type
            varArgsArray = ArrayUtils.toPrimitive(varArgsArray);
        }
        // Store the varargs array in the last position of the array to return
        newArgs[mptLength - 1] = varArgsArray;
        // Return the canonical varargs array.
        return newArgs;
    }

    /**
     * {@link MethodUtils} instances should NOT be constructed in standard programming. Instead, the class should be used as
     * {@code MethodUtils.getAccessibleMethod(method)}.
     *
     * <p>
     * This constructor is {@code public} to permit tools that require a JavaBean instance to operate.
     * </p>
     *
     * @deprecated TODO Make private in 4.0.
     */
    @Deprecated
    public MethodUtils() {
        // empty
    }
}
