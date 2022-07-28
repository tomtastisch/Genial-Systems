package com.acme.greeter.utils.parameter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import org.assertj.core.util.Arrays;
import org.jeasy.random.EasyRandom;

/**
 * General support features of all util classes
 */
public class UtillityClass {
    /* ---- GENERAL OBJECTS---- */
    /**
     * @see Gson
     */
    protected final static @NonNull Gson GSON = new GsonBuilder().create();
    /**
     * @see EasyRandom
     */
    protected final static @NonNull EasyRandom RANDOM = new EasyRandom();

    /* ---- GENERAL STRING OBJECTS ---- */
    protected final static @NonNull String JAVA_OBJECT = "java.lang.Object";

    protected final static @NonNull String REGEX_ALPHANUMERIC = "[^A-Za-z\\\\d]";

    protected final static @NonNull String REGEX_GENERAL_SYNTAX = "[^A-Za-zöÖäÄüÜß\\d@.*?,=]";

    /* ---- ERROR MESSAGES ---- */
    protected final static String ERR_CAN_NOT_CAST = "Can not cast %1$s.class to %2$s.class";
    protected final static String ERR_HAS_NO_CONTENT = "The class has no content, which can be derived from the parent object.";
    protected final static String ERR_OBJECT_TO_JSON = "The class could not be read into a json or formatted back from such a file";
    protected final static String INFO_NON_MATCH = "Non-matching field[/s]: ";

    /* ---- SIZE CONTROLS ---- */
    /**
     * @see Byte#MAX_VALUE
     */
    protected final static int MAX_VALUE = Byte.MAX_VALUE;
    protected final static int LIST_SIZE = 3;

    protected static String format(String aMessage, Object... args) {
        if (aMessage.contains("%1$s") || aMessage.contains("%s")) {
            aMessage = String.format(aMessage, Arrays.isNullOrEmpty(args) ? null : args);
        }
        return aMessage;
    }
}
