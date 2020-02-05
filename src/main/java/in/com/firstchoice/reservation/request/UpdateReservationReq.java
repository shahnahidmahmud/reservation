package in.com.firstchoice.reservation.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateReservationReq implements Serializable {

    private static final long serialVersion=4567898765454678L;

    private String bookingId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate;

    private String numberOfDays;

    private List<PaxDetails> paxDetails=new ArrayList<>();

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
