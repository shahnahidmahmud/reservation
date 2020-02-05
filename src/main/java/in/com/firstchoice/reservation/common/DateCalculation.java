package in.com.firstchoice.reservation.common;

import in.com.firstchoice.reservation.request.DateRangeDto;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateCalculation implements Serializable {


    private static final long serialVersionUID = -5344186019050381754L;

    public List<DateRangeDto> getMonthDetailsBetweenTwoDates(DateTime startDate, DateTime endDate, List<String> repeatWeekdays) {

        // Get Months Count For A Given Date Rage
        Months m = Months.monthsBetween(startDate, endDate);
        List<DateRangeDto> dateRangeDtoList = new ArrayList<>();
        int monthCount = 0;
        SimpleDateFormat formatter1 = new SimpleDateFormat("MM_yyyy");
        String enDate = formatter1.format(endDate.toDate());
        Boolean startDateVa = Boolean.TRUE;
        String endDatesMonth = formatter1.format(startDate.plusMonths(m.getMonths()).toDate());
        String givenEndDate = formatter1.format(endDate.toDate());

        if (endDatesMonth.equals(givenEndDate)) {
            monthCount = m.getMonths();
        } else {
            monthCount = m.getMonths() + 1;
        }

//        Used To check if the start date starts from 1, if it does not, then decrement by 1 to pick current date.
        String statDate = formatter1.format(startDate.toDate());
        if (Integer.valueOf(statDate) == 1) {
            statDate = "0";
        } else {
            statDate = Integer.valueOf(statDate) - 1 + "";
        }

//        Pick The specified Date to populate the date values till this.
        SimpleDateFormat formatter111 = new SimpleDateFormat("dd");
        String enddDate = formatter111.format(endDate.toDate());

        boolean isStartedDateCompleted = Boolean.FALSE;
        for (int i = 0; i <= monthCount; i++) {
            int numberOfDays = 0;
            DateRangeDto dateRangeDto = new DateRangeDto();
            SimpleDateFormat formatter = new SimpleDateFormat("MM_yyyy");
            String monthOfYear = formatter.format(startDate.plusMonths(i).toDate());
            dateRangeDto.setMonthOfYear(monthOfYear);
            if (formatter.format(endDate.toDate()).equals(monthOfYear)) {
                numberOfDays = Integer.valueOf(formatter1.format(endDate.toDate()));
            } else {
                numberOfDays = startDate.plusMonths(i).dayOfMonth().getMaximumValue();
            }
            SimpleDateFormat formatter12 = new SimpleDateFormat("yyyy-MM");
            String strDate1 = formatter12.format(startDate.plusMonths(i).toDate());

            for (int j = Integer.valueOf(statDate); j < numberOfDays; j++) {

                DateTime dateTime = new DateTime(strDate1 + "-01").plusDays(j);
                DateTimeFormatter fmt = DateTimeFormat.forPattern("E"); // use 'E' for short abbreviation (Mon, Tues, etc)
                String weekDay = fmt.print(dateTime);
                if (null != repeatWeekdays && repeatWeekdays.size() > 0) {
                    if (repeatWeekdays.contains(weekDay)) {
                        dateRangeDto.getDatesOfMonths().add(dateTime.getDayOfMonth());
                    }
                } else {
                    dateRangeDto.getDatesOfMonths().add(dateTime.getDayOfMonth());
                }

            }
            statDate = "0";
            dateRangeDtoList.add(dateRangeDto);
        }
        return dateRangeDtoList;
    }


}

