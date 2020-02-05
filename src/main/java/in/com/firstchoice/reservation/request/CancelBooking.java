package in.com.firstchoice.reservation.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CancelBooking implements Serializable {

    private String bookingId;

}
