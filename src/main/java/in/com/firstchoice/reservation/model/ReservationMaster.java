package in.com.firstchoice.reservation.model;

import in.com.firstchoice.reservation.common.Schedule;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReservationMaster implements Serializable {

    private static final long serialVersionUID = 98076543456789L;

    private String id;
    private List<String> cancellationPolicy = new ArrayList<>();
    private List<String> inclusion = new ArrayList<>();
    private List<String> exclusion = new ArrayList<>();
    private List<String> policies=new ArrayList<>();
    private List<ReservationSchedule> reservationSchedules=new ArrayList<>();

    @Data
    public static class ReservationSchedule {

        private String month;
        private List<Schedule> schedules = new ArrayList<>();
    }


}
