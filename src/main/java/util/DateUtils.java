package util;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {
    private static final String PATTERN_DEFAULT = "yyyy-MM-dd hh:mm:ss";

    private static final String PATTERN_DATE = "yyyy-MM-dd";


    private static SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_DEFAULT);

    @Test
    public void test() {
        getNext7Days();
    }

    public static String fromTimeStampStrToDate(String timeStampStr) {
        return sdf.format(Long.valueOf(timeStampStr));
    }

    public static String fromTimeStampToDate(long timeStamp) {
        return sdf.format(timeStamp);
    }
    public static String getCurrent() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    public static List<String> getNext7Days() {
        List<String> next7days = new ArrayList<>();

        SimpleDateFormat sdf_date = new SimpleDateFormat(PATTERN_DATE);
        long today = System.currentTimeMillis();
        long oneDay = 24*60*60*1000l;
        for (int i = 0; i < 7; i++) {
            next7days.add(sdf_date.format(today + i * oneDay));
        }
        return next7days;
    }

    @Test
    public void test_dateTranfer() {
        System.out.println(dateTranfer("2022/04/22 周五"));
    }

    /**
     * 格式转换 "2022/04/22 周五" -> "2022.04.22 周五"
     * @param date
     * @return
     */
    public static String dateTranfer(String date) {
        String[] split = date.split("/");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s);
            sb.append(".");
        }
        String res = sb.substring(0, date.length());
        return res;
    }

}
