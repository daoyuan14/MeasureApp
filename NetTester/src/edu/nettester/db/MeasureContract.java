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

        public static final String MTIME = "timestamp";
        
        public static final String MUSER = "muser";
        public static final String MID = "mid";
        public static final String M_NET_INFO = "mnet";
        public static final String M_LOC_INFO = "mloc";
        public static final String M_TAR_SERVER = "mserver";
        public static final String M_DEVID = "mdevid";
        public static final String AVG_RTT = "avg_rtt";
        public static final String MEDIAN_RTT = "median_rtt";
        public static final String MIN_RTT = "min_rtt";
        public static final String MAX_RTT = "max_rtt";
        public static final String STDV_RTT = "stdv_rtt";
        public static final String UP_TP = "up_tp";
        public static final String DOWN_TP = "down_tp";
        public static final String UPFLG = "upflg";
        // TODO
    }
}
