package th.in.ffc.util;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionIdGenerator {
    private static final String PREFIX = "PHS";
    private static final AtomicInteger sequence = new AtomicInteger(1);

//    public static synchronized String generateTransId() {
//        int year = LocalDateTime.now().getYear() % 100;
//        int currentSequence = sequence.getAndIncrement();
//
//        if (sequence.get() > 999999) {
//            sequence.set(1);
//        }
//
//        return String.format("%s%02d%06d", PREFIX, year, currentSequence);
//    }
        public static String generateTransId() {
            LocalDateTime now = LocalDateTime.now();
            return String.format("%02d%02d%02d%02d%02d%02d",
                    now.getYear() % 100,
                    now.getMonthValue(),
                    now.getDayOfMonth(),
                    now.getHour(),
                    now.getMinute(),
                    now.getSecond());
}
}
