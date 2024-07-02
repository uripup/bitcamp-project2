package vo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarPrint {

    public void printCalendar() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd E");
        System.out.println(df.format(cal.getTime()));
        System.out.println("SU MO TU WE TH FR SA");

        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DATE, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < dayOfWeek; i++) {
            System.out.print("   ");
        }
        for (int i = 1; i <= lastDay; i++) {
            System.out.printf("%02d ", i);
            if (dayOfWeek % 7 == 0) {
                System.out.println();
            }
            dayOfWeek++;
        }
        System.out.println();
    }
}