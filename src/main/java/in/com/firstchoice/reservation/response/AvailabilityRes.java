package in.com.firstchoice.reservation.response;

import in.com.firstchoice.reservation.common.Schedule;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class AvailabilityRes implements Serializable {

    private String month;
    private List<Schedule> schedules = new ArrayList<>();
}
