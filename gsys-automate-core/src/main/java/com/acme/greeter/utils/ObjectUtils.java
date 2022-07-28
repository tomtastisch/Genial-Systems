package com.acme.greeter.utils;

import com.acme.greeter.utils.parameter.UtillityClass;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jeasy.random.ObjectCreationException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * UtilClass, which is used to create, edit and manipulate random objects within
 * the test environment. Basically, it serves to simplify the use of test
 * objects and instances of the objects and content to be tested.
 */
@NotThreadSafe
@NoArgsConstructor(access = AccessLevel.NONE)
public final class ObjectUtils extends UtillityClass {

    /**
     * Method to create a list containing objects of the given class.
     *
     * @param aClass -> Class to convert the elements
     * @return The cast version of a randomly created iteration, which is created
     * based on the passed class.
     * @see #randomObjectIterable(Class, int)
     * @see UtillityClass#LIST_SIZE
     */
    public static <T> List<T> randomObjectIterable(Class<T> aClass) {
        return randomObjectIterable(aClass, LIST_SIZE);
    }

    private static <T> List<T> randomObjectIterable(Class<T> aClass, int aSize) {
        return Arrays.stream(randomObjects(aClass, aSize)).map(aClass::cast).collect(Collectors.toList());
    }

    /**
     * Method to create a set containing objects of the given class.
     *
     * @param aClass -> Class to convert the elements
     * @return a set of objects
     */
    private static <T> Object[] randomObjects(Class<T> aClass, int aSize) {
        return Arrays.stream(new Object[aSize]).map(object -> nextObject(aClass)).toArray(Object[]::new);
    }

    /**
     * Creates an instance based on the passed class parameter, which is filled and
     * rendered using a recursive call with random variables and instances
     *
     * @param aClass -> To be created
     * @return The created instance of the specified class parameter
     */
    @NonNull
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T nextObject(Class<T> aClass) {
        try {
            // Creates a random instance with variables, which
            // must be basically primitive
            return RANDOM.nextObject(aClass);
        } catch (ObjectCreationException aObjectCreationException) {

            // Creating a new class instance
            final @NonNull T lMocked = aClass.getDeclaredConstructor().newInstance();

            // Recursively search all contained fields and populate the field
            // variables using the self-call or re-instantiation of the objects
            FieldUtils.getAllFieldsList(aClass).forEach(field -> {
                Object lObject;
                try { // Checking the field whether it corresponds to an enum
                    if (field.getType().isEnum()) {
                        // Random playback of an item from the given enumeration
                        lObject = nextEnumConstant((Class<? super Enum<?>>) field.getType());
                    } else { // Creating a new class instance
                        lObject = field.getType().getDeclaredConstructor().newInstance();
                    }
                } catch (Exception aException) {
                    // Recursive call if an error has occurred during normal
                    // re-instantiation of the object
                    lObject = nextObject(field.getType());
                }
                // Setting the field variable using the instantiated object
                ReflectionTestUtils.setField(lMocked, field.getName(), lObject);
            });
            return lMocked;
        }
    }

    @NonNull
    public static Date randomDate() {
        return new Date(ThreadLocalRandom.current().nextInt() * 1000L);
    }

    /**
     * This method is used to randomly return a variable from the passed enum.
     *
     * @param aClass -> Enum from which a random variable is to be played back
     * @return a random instance of a given enum
     * @see #nextEnumConstant(Class)
     */
    @NonNull
    public static <T extends Enum<?>> T nextConstant(Class<T> aClass) {
        return aClass.cast(nextEnumConstant(aClass));
    }

    /**
     * Used in combination {@link Class#getEnumConstants()} and
     * {@link SecureRandom#nextInt()} to get a random variable from the passed enum
     *
     * @param aClass -> Enum from which a random variable is to be played back
     * @return a random value of the given enum class
     */
    @NonNull
    private static <T extends Enum<?>> Object nextEnumConstant(Class<? super T> aClass) {
        return aClass.getEnumConstants()[new SecureRandom().nextInt(aClass.getEnumConstants().length)];
    }

    public static int randomId() {
        return RandomUtils.nextInt();
    }

    /**
     * Create a byte array, which has a size of {@link #MAX_VALUE}
     *
     * @return a random byte array
     */
    public static byte[] randomByteArray() {
        return RandomUtils.nextBytes(MAX_VALUE);
    }

    /**
     * Creates a {@link #randomStringValue() random password} which is then
     * {@link DigestUtils#digestAsHex(byte[]) hashed} and rendered as a
     * {@link String string}
     *
     * @return a new hashed password
     * @see MessageDigestAlgorithms#SHA_256
     */
    @NonNull
    @SneakyThrows
    public static String randomHashedPassword() {
        return new DigestUtils(MessageDigestAlgorithms.SHA_256).digestAsHex(randomStringValue().getBytes());
    }

    /**
     * @return A random alphanumeric string
     * @see RandomStringUtils#random(int, boolean, boolean)
     * @see RandomStringUtils#randomAlphanumeric(int)
     * @see #MAX_VALUE
     */
    @NonNull
    public static String randomStringValue() {
        return RandomStringUtils.random(MAX_VALUE, true, true);
    }

    /**
     * @return a random boolean value
     * @see SecureRandom#nextBoolean()
     */
    public static boolean randomBoolean() {
        return new SecureRandom().nextBoolean();
    }

    /**
     * The transferred object is copied and re-instantiated to obtain an identical
     * object with the same content characteristics.
     *
     * @param aObject -> The object to be instantiated
     * @return The newly created copy of the object as its own independent instance
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> T deepCopyObject(T aObject) {
        return deepCopyObject(aObject, (Class<T>) aObject.getClass());
    }

    /**
     * The transferred object is copied and re-instantiated to obtain an identical
     * object with the same content characteristics. Here it is possible to choose a
     * class for reinstantiation, which either originates from the class of the
     * passed object or corresponds directly to the class of the object.
     *
     * @param aObject -> The object to be instantiated
     * @param aClass  -> The class of the new object to be instantiated
     * @return The newly created copy of the object as its own independent instance
     */
    @NonNull
    private static <T> T deepCopyObject(T aObject, Class<? extends T> aClass) {
        // Creates a json object of the passed object and formats it back into memory as
        // its own instance. First the json object is instantiated as such and then
        // reformatted to the desired class.
        return aClass.cast(GSON.fromJson(json(aObject), aClass));
    }

    /**
     * Converts a given object into a string format using reflections, which
     * corresponds to the standard format and contains all contained and filled
     * fields, including their variables.
     *
     * @param aObject -> The object to be formatted
     * @return The object as {@link #toString() String}
     */
    @NonNull
    public static String toString(@Nullable Object aObject, String... aFieldNames) {

        String lClassName;
        String lReturnValue;

        if (Objects.isNull(aObject)) {
            lClassName = JAVA_OBJECT + "(%s)";
            lReturnValue = StringUtils.EMPTY;
        } else {
            lClassName = aObject.getClass().getSimpleName() + "(%s)";

            final @Nullable Class<? super Collection<?>> lCls = ObjectReflectiveAssertion
                    .compareParentClasses(aObject.getClass(), Collection.class, false);

            if (Objects.nonNull(lCls) && lCls.isAssignableFrom(Iterable.class)) {
                // Assembling the iterable object into a string
                lReturnValue = StringUtils.join(aObject);

            } else {
                try { // Create a list of all fields contained within the object and their contents.
                    lReturnValue = StringUtils.join(FieldUtils.getAllFieldsList(aObject.getClass()).stream()
                            // Remove all fields which should be sorted out
                            .filter(field -> !Arrays.asList(aFieldNames).contains(field.getName()))
                            // Format each value in its name and content as a string to be able to
                            // concatenate them later on.
                            .map(field -> field.getName() + "=" + getValue(aObject, field.getName()))
                            // Merge the result into a list and then convert it into a string.
                            .collect(Collectors.toList()));
                } catch (Exception aException) {
                    lReturnValue = aObject.toString();
                }
            }
        }
        // Replace all unneeded character variables
        lReturnValue = lReturnValue.replaceAll(REGEX_GENERAL_SYNTAX, "")
                // Remove the zero indexed values
                .replaceAll(Pattern.quote("null"), "");

        return format(lClassName, lReturnValue);
    }

    /**
     * Fetches the variable of the field from the object and makes it available.
     *
     * @param aObject    -> Object from which the content of the field is to be read
     * @param aFieldName -> Name of the field to be read
     * @return The content of the field within the object
     */
    @Nullable
    @SneakyThrows
    static <T> Object getValue(@NonNull T aObject, @NonNull String aFieldName) {
        return org.springframework.security.util.FieldUtils.getFieldValue(aObject, aFieldName);
    }

    /**
     * For values of field with the following types: <br>
     * <code>PUBLIC | STATIC | FINAL</code><br>
     * If the modifiers are not all explicitly present, the method returns
     * <code>null</code>.
     *
     * @param aClass     -> Class to which the test refers and in which the field is
     *                   to be searched for
     * @param aFieldName -> Name of the field whose value is to be determined
     * @return Value of the given field
     */
    @Nullable
    @SneakyThrows
    public static <T> Object getValue(@NonNull Class<T> aClass, @NonNull String aFieldName) {
        final @NonNull Field lField = aClass.getField(aFieldName);
        int lMods = lField.getModifiers();

        Object lObject = null;
        // Checks the field if all given modifiers are present
        if (Modifier.isPublic(lMods) && Modifier.isStatic(lMods) && Modifier.isFinal(lMods)) {
            lObject = lField.get(null);
        }
        return lObject;
    }

    /**
     * Create a json over the passed object without including the fields which were
     * passed as additional parameters.
     *
     * @param aObject              -> The object to be formatted
     * @param aRemovableProperties -> Array of fields to be removed before creating
     *                             the json-string of the object
     * @return The object as a separate json string
     */
    @NonNull
    static String json(@NonNull Object aObject, String... aRemovableProperties) {
        // Formatting the object into its own instance as a json object
        final @NonNull JsonObject lJsonObject = GSON.toJsonTree(aObject).getAsJsonObject();
        // Remove the unwanted fields within the object
        Arrays.asList(aRemovableProperties).forEach(lJsonObject::remove);
        // Creating a json string using the created object and its remaining content
        return GSON.toJson(lJsonObject);
    }
}
