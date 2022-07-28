package com.acme.greeter.utils;

import com.acme.greeter.utils.exceptions.ConversionFailedException;
import com.acme.greeter.utils.exceptions.ObjectFormatterException;
import com.acme.greeter.utils.parameter.UtillityClass;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility to support test case creation regarding mapper class. An individual
 * mapper is provided, which creates an explicit output for a 1:1 control, which
 * can be used to compare the mapped object.
 */
@NotThreadSafe
@NoArgsConstructor(access = AccessLevel.NONE)
public class MapperUtils extends UtillityClass {

    /**
     * Auxiliary method used to translate class contents within the passed object to
     * the class specified as the second parameter. Here it is possible to mention
     * the same class as the second parameter, which directly corresponds to that of
     * the object. In this case a {@link ObjectUtils#deepCopyObject(Object) deep
     * copy} of the object is created and returned. <br>
     * <br>
     * Final fields, which are filled within the constructors and can therefore no
     * longer be changed within the class itself, are displayed as empty fields in
     * the newly instantiated object.
     *
     * @param aObj      -> The object to be translated
     * @param aCls      -> Class into which the passed object is to be translated
     * @param accessors -> Accessors of the main object to be adjusted during
     *                  comparison. The following letter after the accessor must be
     *                  capitalized, otherwise the accessor is always skipped and
     *                  not removed.
     * @return Instance of the given translation class with the same variables that
     * match the given object.
     * @see ObjectUtils#json(Object, String...)
     * @see Gson#fromJson(com.google.gson.JsonElement, Class)
     */
    @API(status = Status.STABLE)
    public static <T, R> R mapTo(@NonNull T aObj, @NonNull Class<R> aCls, String... accessors)
            throws ConversionFailedException {

        // Check whether the class of the passed object corresponds
        // to the same class as specified as the second parameter.
        if (aCls.isAssignableFrom(aObj.getClass())) {
            // Rendering a new instance of the passed object as a deep copy
            return aCls.cast(ObjectUtils.deepCopyObject(aObj));
        }

        try {
            // Create a list containing all fields to be skipped during conversion
            String[] lRemovableProperties = IterableUtils
                    .toList(IterableUtils.chainedIterable(FieldUtils.getAllFieldsList(aCls).stream()
                                    // Filtering the content using a list of all fields of the passed object
                                    .filter(field ->
                                            // Filtering of the remaining fields within the stream, according to matching
                                            // field types. Then the number of fields is checked and a boolean value is
                                            // returned, which is TRUE if the count of fields is greater than 0.
                                            FieldUtils.getAllFieldsList(aObj.getClass()).stream()
                                                    // Name matching and filtering of the fields, so that only all name-matching
                                                    // fields are contained within the stream that is to be run through further
                                                    .filter(fld -> field.getName().equals(fld.getName())).map(Field::getType)
                                                    .anyMatch(type -> !field.getType().isAssignableFrom(type)))
                                    // Rewriting the list to the names of the functions themselves
                                    .map(Field::getName).collect(Collectors.toList()),
                            // Create a list of names of all existing fields in the class to be created.
                            FieldUtils.getAllFieldsList(aCls).stream().map(Field::getName).map(String::toLowerCase)
                                    // Filtering the list according to all fields, including those in the
                                    // transferred object, with the same name identifier.
                                    .filter(name -> !FieldUtils.getAllFieldsList(aObj.getClass()).stream()
                                            .map(Field::getName).map(String::toLowerCase)
                                            // Remove the accessors as a whole
                                            .map(e -> e.replace(StringUtils.join(accessors).toLowerCase(), ""))
                                            .collect(Collectors.toList()).contains(name))
                                    // Combination into an iterable object
                                    .collect(Collectors.toList())))
                    .toArray(String[]::new);

            // Checking the merged collection for the number of elements. If the number of
            // elements is the same size as the number of fields of the class to be created,
            // it means that the class to be created has no matching fields with the passed
            // object and would cause an error.
            final int lSize = FieldUtils.getAllFields(aCls).length;
            if (lSize != 0 && lSize == lRemovableProperties.length) {
                throw new ClassCastException(ERR_HAS_NO_CONTENT);
            }

            // Create a new class instance based on a json object, which re-instantiates the
            // object itself.
            return instantiate(aObj, aCls, lRemovableProperties);
        } catch (Exception aException) {
            // Get the class of the given object
            final @NonNull String lCls = aObj.getClass().getSimpleName();
            // Adaptation of the error message on the basis of the two classes which are
            // replaced within the pointer in the string
            final @NonNull String lMsg = format(ERR_CAN_NOT_CAST, lCls, aCls.getSimpleName());
            // If the object passed and the class it is to be converted to are incompatible,
            // the current operation is aborted and an error is thrown
            throw new ConversionFailedException(lMsg, aException);
        }
    }

    /**
     * Function to instantiate a class which should include the passed values of the
     * passed object and use them to fill its own fields.
     *
     * @param aObject    -> Object, which is to be used to read out and transfer the
     *                   contained fields.
     * @param aClass     -> Class, which is to be instantiated and must take over
     *                   the provided fields of the transferred object.
     * @param aRemovable -> Fields to be removed, which are to be ignored during
     *                   re-instantiation
     * @return The newly instantiated object of the passed class
     */
    @NonNull
    static <T> T instantiate(Object aObject, Class<T> aClass, String[] aRemovable) {

        // List of all included fields of the object
        final @NonNull List<Field> lFields = FieldUtils.getAllFieldsList(aObject.getClass());
        // Sorting out the fields to be ignored from the determined list
        final Object[] lObjects = CollectionUtils.removeAll(lFields, Arrays.asList(aRemovable)).stream()
                // Reformatting the individual fields in their contents
                .map(field -> ObjectUtils.getValue(aObject, field.getName()))
                // Summary to an iterable object (array)
                .toArray(Object[]::new);

        T lInstance;
        try {
            // Create an iterable object that contains all constructors of the class and
            // checks them for consistency one after the other. Subsequently, the result is
            // to be cast into the class to be instantiated.
            lInstance = aClass.cast(Objects.requireNonNull(Arrays.stream(aClass.getConstructors())
                            // Sorting out constructors that do not contain the correct count of parameters
                            .filter(constructor -> constructor.getParameters().length == lObjects.length)
                            // Checking the parameters of the respective constructor for their type-based
                            // correctness and conformity with the parameters to be transferred
                            .filter(constructor -> Arrays.equals(constructor.getParameterTypes(), lObjects))
                            // Merge all remaining results and return the searched constructor. Due to the
                            // fact that it is not possible to have two constructors with the same passing
                            // parameters, the result is unique here.
                            .findAny().orElse(null))
                    // Instantiation of the returned constructor as a new separate object
                    .newInstance(lObjects));
        } catch (ReflectiveOperationException | NoSuchElementException aException) {
            // This method deserializes the specified Json into an object of the specified
            // class. It is not suitable to use if the specified class is a generic type
            // since it will not have the generic type information because of the Type
            // Erasure feature of Java.
            lInstance = GSON.fromJson(ObjectUtils.json(aObject, aRemovable), aClass);
        }
        return lInstance;
    }

    static <T> T instantiateJson(Object aObject, Class<T> aClass, String[] aRemovable) {
        try { // Generates a json of the given object and formatted the json-object into the class
            return GSON.fromJson(ObjectUtils.json(aObject, aRemovable), aClass);
        } catch (JsonParseException aException) {
            throw new ObjectFormatterException(aObject, ERR_OBJECT_TO_JSON, aException);
        }
    }
}
