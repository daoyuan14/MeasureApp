package edu.nettester.db;

import android.provider.BaseColumns;

/**
 * The contract class allows you to use the same constants across all the other
 * classes in the same package. This lets you change a column name in one place
 * and have it propagate throughout your code.
 * 
 * @author Daoyuan
 * @see http://developer.android.com/training/basics/data-storage/databases.html
 */
public final class MeasureContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MeasureContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class MeasureLog implements BaseColumns {
        // TODO types need to be adjusted
        public static final String TABLE_NAME = "mlog";

        public static final String COLUMN_NAME_MID = "mid";

        public static final String COLUMN_NAME_TIME = "timestamp";

        public static final String COLUMN_NAME_RTT = "rtt";
        // TODO
    }
}
