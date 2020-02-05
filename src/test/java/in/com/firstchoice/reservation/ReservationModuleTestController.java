package in.com.firstchoice.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.com.firstchoice.reservation.common.ReadResponseDetail;
import in.com.firstchoice.reservation.controller.ReservationController;
import in.com.firstchoice.reservation.request.CancelBooking;
import in.com.firstchoice.reservation.request.ReservationReq;
import in.com.firstchoice.reservation.request.UpdateReservationReq;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationModuleTestController {
    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    public void testCreateSignupFormInvalidUser() throws Exception {
        this.mockMvc.perform(get("/reservation/loadData")).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testtoGetInventory() throws Exception {
        this.mockMvc.perform(get("/reservation/getAvailability?startDate=2020-02-15&endDate=2020-02-20")).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testtoSaveModifyAndCancel() throws Exception {


//		Save Booking
        ReservationReq reservationReq =new ReservationReq();
        reservationReq.setStartDate(LocalDate.parse("2020-02-15"));
        reservationReq.setEndDate(LocalDate.parse("2020-02-18"));
        reservationReq.setNumberOfDays("3");
        ReservationReq.CustomerDetails customerDetails=new ReservationReq.CustomerDetails();
        customerDetails.setAge("20");
        customerDetails.setEmailId("resevation@test.com");
        customerDetails.setFirstName("Reservation1");
        customerDetails.setLastName("Test1");
        customerDetails.setMobileNumber("0985432221");
        reservationReq.getCustomerDetails().add(customerDetails);

        ReservationReq.PaxDetails paxDetails=new ReservationReq.PaxDetails();
        paxDetails.setLabel("Adult");
        paxDetails.setNumberOfPax("3");
        paxDetails.setPrice("1000");
        reservationReq.getPaxDetails().add(paxDetails);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(reservationReq);;
        MvcResult mvcResult = mockMvc.perform(post("/reservation/savereservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();
        ReadResponseDetail readResponseDetail1 = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ReadResponseDetail.class);
        String[] split = readResponseDetail1.getMessage().split("-");


//		Modify Booking
        UpdateReservationReq updateReservationReq =new UpdateReservationReq();
        updateReservationReq.setStartDate(LocalDate.parse("2020-02-16"));
        updateReservationReq.setEndDate(LocalDate.parse("2020-02-19"));
        updateReservationReq.setNumberOfDays("3");
        updateReservationReq.setBookingId(split[1].trim());
        UpdateReservationReq.CustomerDetails customerDetails1=new UpdateReservationReq.CustomerDetails();
        customerDetails1.setAge("20");
        customerDetails1.setEmailId("resevation@test.com");
        customerDetails1.setFirstName("Reservation1");
        customerDetails1.setLastName("Test1");
        customerDetails1.setMobileNumber("0987654321");
        updateReservationReq.getCustomerDetails().add(customerDetails1);

        UpdateReservationReq.PaxDetails paxDetails1=new UpdateReservationReq.PaxDetails();
        paxDetails1.setLabel("Adult");
        paxDetails1.setNumberOfPax("3");
        paxDetails1.setPrice("1000");
        updateReservationReq.getPaxDetails().add(paxDetails1);
        String json1 = objectMapper.writeValueAsString(updateReservationReq);;

        MvcResult mvcResult1 = mockMvc.perform(post("/reservation/modifyreservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();


//		Cancel Booking
        CancelBooking cancelBooking=new CancelBooking();
        cancelBooking.setBookingId(split[1].trim());
        String json11 = objectMapper.writeValueAsString(cancelBooking);;

        MvcResult mvcResult11 = mockMvc.perform(post("/reservation/cancelBooking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json11)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    public void testtoSaveReservation_ForErrorMessage() throws Exception {


//		Save Booking
        ReservationReq reservationReq =new ReservationReq();
        reservationReq.setStartDate(LocalDate.parse("2020-04-15"));
        reservationReq.setEndDate(LocalDate.parse("2020-04-18"));
        reservationReq.setNumberOfDays("3");
        ReservationReq.CustomerDetails customerDetails=new ReservationReq.CustomerDetails();
        customerDetails.setAge("20");
        customerDetails.setEmailId("resevation@test.com");
        customerDetails.setFirstName("Reservation1");
        customerDetails.setLastName("Test1");
        customerDetails.setMobileNumber("0985432221");
        reservationReq.getCustomerDetails().add(customerDetails);

        ReservationReq.PaxDetails paxDetails=new ReservationReq.PaxDetails();
        paxDetails.setLabel("Adult");
        paxDetails.setNumberOfPax("3");
        paxDetails.setPrice("1000");
        reservationReq.getPaxDetails().add(paxDetails);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(reservationReq);;
        MvcResult mvcResult = mockMvc.perform(post("/reservation/savereservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andExpect(status().is(400))
                .andReturn();
    }
    @Test
    public void testtoSaveReservation_ForBookingMade_MoreThan_three_days() throws Exception {


//		Save Booking
        ReservationReq reservationReq =new ReservationReq();
        reservationReq.setStartDate(LocalDate.parse("2020-02-03"));
        reservationReq.setEndDate(LocalDate.parse("2020-02-08"));
        reservationReq.setNumberOfDays("3");
        ReservationReq.CustomerDetails customerDetails=new ReservationReq.CustomerDetails();
        customerDetails.setAge("20");
        customerDetails.setEmailId("resevation@test.com");
        customerDetails.setFirstName("Reservation1");
        customerDetails.setLastName("Test1");
        customerDetails.setMobileNumber("0985432221");
        reservationReq.getCustomerDetails().add(customerDetails);

        ReservationReq.PaxDetails paxDetails=new ReservationReq.PaxDetails();
        paxDetails.setLabel("Adult");
        paxDetails.setNumberOfPax("3");
        paxDetails.setPrice("1000");
        reservationReq.getPaxDetails().add(paxDetails);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(reservationReq);;
        MvcResult mvcResult = mockMvc.perform(post("/reservation/savereservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    public void testtoSaveReservation_ForBookingMade_present_day() throws Exception {


//		Save Booking
        ReservationReq reservationReq =new ReservationReq();
        reservationReq.setStartDate(LocalDate.parse("2020-02-03"));
        reservationReq.setEndDate(LocalDate.parse("2020-02-04"));
        reservationReq.setNumberOfDays("3");
        ReservationReq.CustomerDetails customerDetails=new ReservationReq.CustomerDetails();
        customerDetails.setAge("20");
        customerDetails.setEmailId("resevation@test.com");
        customerDetails.setFirstName("Reservation1");
        customerDetails.setLastName("Test1");
        customerDetails.setMobileNumber("0985432221");
        reservationReq.getCustomerDetails().add(customerDetails);

        ReservationReq.PaxDetails paxDetails=new ReservationReq.PaxDetails();
        paxDetails.setLabel("Adult");
        paxDetails.setNumberOfPax("3");
        paxDetails.setPrice("1000");
        reservationReq.getPaxDetails().add(paxDetails);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(reservationReq);;
        MvcResult mvcResult = mockMvc.perform(post("/reservation/savereservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andExpect(status().is(400))
                .andReturn();
    }

}
