package com.acme.greeter.utils;

import com.acme.greeter.utils.parameter.UtillityClass;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.assertj.core.api.Assert;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Assertion class, which provides assertion methods that extend beyond the
 * standardized capabilities of junit5. This is a kind of bridge class between
 * the use of junit5 and the extension to hamcrest, which does not need to be
 * explicitly implemented.
 */
@API(status = Status.INTERNAL)
@NoArgsConstructor(access = AccessLevel.NONE)
public class ObjectReflectiveAssertion extends UtillityClass {

    /**
     * @param aObject       -> The object from which the fields are considered for
     *                      verification
     * @param aObjectEquals -> The object from which the fields under consideration
     *                      are to be checked.
     * @see #assertReflectionEquals(Object, Object, boolean, char...)
     */
    @API(status = Status.EXPERIMENTAL)
    public static void assertReflectionEquals(final @NonNull Object aObject, final @NonNull Object aObjectEquals) {
        assertReflectionEquals(aObject, aObjectEquals, false);
    }

    /**
     * Checks the fields of the object passed as the second parameter for their
     * content and compares them with the contents of the fields of the first
     * object. Here, only the fields of the first object are considered and used as
     * the basis for the check. These fields are then searched for within the second
     * object and then compared.
     *
     * @param aObject       -> The object from which the fields are considered for
     *                      verification
     * @param aObjectEquals -> The object from which the fields under consideration
     *                      are to be checked.
     * @param aInterfaces   -> Specifies whether interfaces are to be included in
     *                      the comparison or whether only superclasses are to be
     *                      used for the comparison operation.
     * @param accessors     -> Accessors of the main object to be adjusted during
     *                      comparison. The following letter after the accessor must
     *                      be capitalized, otherwise the accessor is always skipped
     *                      and not removed.
     * @see Assert
     */
    @API(status = Status.EXPERIMENTAL)
    public static void assertReflectionEquals(final @NonNull Object aObject, final @NonNull Object aObjectEquals,
                                              boolean aInterfaces, char... accessors) {

        final @NonNull String[] lAccessors = Stream.of(accessors).map(String::valueOf).toArray(String[]::new);

        // List of all contained fields within the object
        final @NonNull List<String> lFields = FieldUtils.getAllFieldsList(aObject.getClass()).stream()
                .map(Field::getName).map(name -> {
                    for (String accessor : lAccessors) {
                        name = name.replaceFirst(accessor, "");
                    }
                    return name;
                }).collect(Collectors.toList());

        // Creation of a second list, which contains all fields of the second object
        final @NonNull List<String> lEqualsFields = FieldUtils.getAllFieldsList(aObjectEquals.getClass()).stream()
                .map(Field::getName).collect(Collectors.toList());

        // Check if all fields within the list exist
        if (CollectionUtils.containsAll(lEqualsFields, lFields)) {
            // Loop through all field labels within the generated list
            for (final @NonNull String lField : lFields) {
                try {// Value, which is basically expected
                    final Object lValExp = ObjectUtils.getValue(aObject, StringUtils.join(lAccessors).concat(lField));
                    // Value, which is determined on the basis of the expected value
                    final Object lVal = ObjectUtils.getValue(aObjectEquals,
                            // Checking of the received list, whether the searched field even exists within
                            // the object and can be compared.
                            lEqualsFields.get(lEqualsFields.stream().map(String::toLowerCase)
                                    // Checking of the received list, whether the searched field even exists within
                                    // the object and can be compared.
                                    .collect(Collectors.toList()).indexOf(lField.toLowerCase())));
                    if (Objects.nonNull(lVal) && Objects.nonNull(lValExp)) {
                        final @NonNull Class<?> lCls = lValExp.getClass();
                        final @NonNull Class<?> lClsCompare = lVal.getClass();
                        // Determine the common parent class of the two variables to allow a safe
                        // comparison and to be able to specify the two objects to the same parent class
                        final Class<?> lClass = compareParentClasses(lCls, lClsCompare, aInterfaces);
                        if (Objects.nonNull(lClass)) {
                            // Regression of both variables to their parent class and subsequent comparison
                            assertEquals(ObjectUtils.toString(lClass.cast(lValExp)),
                                    ObjectUtils.toString(lClass.cast(lVal)), lField);
                        } else { // Compare the objects in their original object format
                            assertEquals(aObject, aObjectEquals, lField);
                        }
                    }
                } catch (IllegalStateException aException) {
                    fail(aException.getMessage().replace(JAVA_OBJECT, aObjectEquals.getClass().getSimpleName()));
                }
            }
        } else {// Intentional call with false to provoke an error roll
            fail(INFO_NON_MATCH + StringUtils
                    // Merging the collection of non-existent fields within the class to be compared
                    .join(CollectionUtils.removeAll(
                                    // Collection of all fields within the main class, which serves as a basis
                                    lFields.stream().map(String::toLowerCase).collect(Collectors.toList()),
                                    // Fields to be determined based on the comparison class are compared to the
                                    // main class and deleted within the first collection.
                                    lEqualsFields.stream().map(String::toLowerCase).collect(Collectors.toList())).stream()
                            // Adding the accessors to be able to specify the field uniquely
                            .map(name -> StringUtils.join(lAccessors).concat(name)).collect(Collectors.toList()))
                    // Remove unneeded or unwanted characters
                    .replaceAll(REGEX_ALPHANUMERIC, ""));
        }
        assert true;
    }

    /**
     * @param aClass       -> Class, the return of which is to follow as a basic set
     *                     and then be used for comparison
     * @param aClassEquals -> Class with which the subsequent return and comparison
     *                     of existing original classes is to take place
     * @return The parent class, which serves as the top level and also identical
     * level of both passed classes
     * @see #compareParentClasses(Class, Class, boolean)
     */
    @SuppressWarnings("unchecked")
    static <P, E extends P, T extends P> Class<? super P> compareParentClasses(
            @NonNull Class<? super E> aClass, @NonNull Class<? super T> aClassEquals) {
        return compareParentClasses(aClass, aClassEquals, false);
    }

    /**
     * Origin determination of all classes and (if the Boolean value is true) also
     * their interfaces implemented at the respective class level. Subsequently, a
     * comparison of the two objects and their hyrachies is performed, which
     * determines the highest, common and identical parent class.
     *
     * @param <P>           -> Common origin class
     * @param aClass        -> Class, the return of which is to follow as a basic
     *                      set and then be used for comparison
     * @param aClassEquals  -> Class with which the subsequent return and comparison
     *                      of existing original classes is to take place
     * @param aIncInterface -> Specifies whether interfaces are to be included in
     *                      the comparison or whether only superclasses are to be
     *                      used for the comparison operation.
     * @return The parent class, which serves as the top level and also identical
     * level of both passed classes
     */
    @Nullable
    @SneakyThrows
    @SuppressWarnings("unchecked")
    static <P, E extends P, T extends P> Class<? super P> compareParentClasses(
            @NonNull Class<? super E> aClass, @NonNull Class<? super T> aClassEquals, boolean aIncInterface) {

        final @NonNull List<? super Class<?>> aParent = originPreservation(aClass);
        final @NonNull List<? super Class<?>> aEquals = originPreservation(aClassEquals);

        return (Class<? super P>) aParent.stream()
                .filter(Objects::nonNull).map(cls -> (Class<?>) cls)
                .filter(cls -> (aIncInterface && aEquals.contains(cls)) | cls.equals(aClassEquals))
                .findAny().orElse(null);
    }

    /**
     * Determination method to create a class origin hyrachy (listing of parent
     * classes and their respective interfaces). Used to create a kind of family
     * tree of the class.
     *
     * @param aClass        -> Class whose family tree is to be determined
     * @return List of all parent classes identified within the class hierarchy and
     * interfaces, if applicable.
     */
    @NonNull
    private static List<Class<?>> originPreservation(final @NonNull Class<?> aClass) {

        @NonNull
        Map<Class<?>, List<? super Class<?>>> lMappedClasses = new HashMap<>();
        MapUtils.populateMap(lMappedClasses, Lists.newArrayList(aClass),
                // Transform Key
                cls -> (Class<?>) cls,
                // Transform values
                e -> Lists.newArrayList(getInterfaces(aClass)));

        Class<?> lClass = aClass;
        // Loop to detect all existing parent classes
        while ((lClass = lClass.getSuperclass()) != null) {
            // Adding the determined class as an element of the list
            lMappedClasses.put(lClass, Lists.newArrayList(getInterfaces(lClass)));
        }

        // Creating a summary of keys
        List<Class<?>> lKeys = lMappedClasses.keySet().stream()
                // Casting the objects into the correct format
                .map(obj -> (Class<?>) obj)
                // Rendering of a list in which all elements are unique
                .distinct().collect(Collectors.toList());

        // Creating a summary of values
        List<Class<?>> lValues = Arrays.asList(lMappedClasses.values()
                // Merging the lists into a one-dimensional array
                .stream().flatMap(Stream::of).findAny()
                // Return to an optional object and its content or null if this is a null object.
                .orElseGet(List::of)
                // Remove redundant objects and merge all content into one complete list
                .stream().distinct().toArray(Class<?>[]::new));

        return Lists.newArrayList(IterableUtils.chainedIterable(lKeys, lValues));
    }

    /**
     * Checks all contained interfaces of a class and their respective inherited
     * interfaces. Returns these as a list.
     *
     * @param aClass -> Class whose interfaces are to be traversed
     * @return list of interfaces
     */
    @NonNull
    static Iterable<? super Class<?>> getInterfaces(Class<?> aClass) {
        // Create and merge all class level interfaces and their parent interfaces.
        return IterableUtils.chainedIterable((aClass.isInterface() ? List.of(aClass) : new ArrayList<>()),
                // Create the list of all parent interfaces
                Arrays.asList(aClass.getInterfaces()), Lists.newArrayList(aClass.getInterfaces()).stream()
                        // Mapping of interfaces to their parent interfaces (recursive call)
                        .map(cls -> Lists.newArrayList(getInterfaces(cls)).toArray(Object[]::new))
                        // Filter for non-empty content
                        .filter(classes -> !Arrays.asList(classes).isEmpty()).flatMap(Stream::of)
                        .collect(Collectors.toList()));
    }
}
