package in.com.firstchoice.reservation.model;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReservationBookingMaster implements Serializable {

    private static final long serialVersion=4567898765454678L;

    private LocalDate startDate;

    private LocalDate endDate;

    private String numberOfDays;

    private String status;

    private List<PaxDetails> paxDetails=new ArrayList<>();

    private String bookingId;

    private Boolean cancelledBooking=Boolean.FALSE;

    private List<CustomerDetails> customerDetails=new ArrayList<>();

    @Data
    public static  final class PaxDetails{
        private String numberOfPax;
        private String label;
        private String price;
    }

    @Data
    public static final class CustomerDetails{

        private String firstName;
        private String lastName;
        private String mobileNumber;
        private String emailId;
        private String age;

    }
}
