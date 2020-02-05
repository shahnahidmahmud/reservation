package in.com.firstchoice.reservation.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

@Data
public class DateRangeDto implements Serializable {

    private static final long serialVersionUID = -8342843149807769400L;

    private String monthOfYear;

    private Set<Integer> datesOfMonths = new TreeSet<>();
}
