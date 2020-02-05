package in.com.firstchoice.reservation.controller;


import in.com.firstchoice.reservation.common.DateCalculation;
import in.com.firstchoice.reservation.common.Price;
import in.com.firstchoice.reservation.common.ResponseDetails;
import in.com.firstchoice.reservation.common.Schedule;
import in.com.firstchoice.reservation.model.ReservationBookingMaster;
import in.com.firstchoice.reservation.model.ReservationMaster;
import in.com.firstchoice.reservation.request.CancelBooking;
import in.com.firstchoice.reservation.request.DateRangeDto;
import in.com.firstchoice.reservation.request.ReservationReq;
import in.com.firstchoice.reservation.request.UpdateReservationReq;
import in.com.firstchoice.reservation.response.AvailabilityRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/reservation")
@Slf4j
public class ReservationController {

    @Autowired
    private static ReservationMaster reservationMaster;

    @Autowired
    private static List<ReservationBookingMaster> reservationBookingMasterList = new ArrayList<>();

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/getAvailability")
    public ResponseEntity fetchReservationAvailability(HttpServletRequest request,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate) {

        try {
            log.debug("Get Reservation Availability For the Date {}", DateTime.now());

            DateCalculation dateCalculation = new DateCalculation();
            List<DateRangeDto> detailsBetweenTwoDates = dateCalculation.getMonthDetailsBetweenTwoDates(DateTime.parse(startDate), DateTime.parse(endDate), new ArrayList<>());

            List<AvailabilityRes> availabilityResList = new ArrayList<>();
            detailsBetweenTwoDates.stream().forEach(dateRangeDto -> {
                AvailabilityRes availabilityRes = new AvailabilityRes();
                reservationMaster.getReservationSchedules().stream().forEach(reservationSchedule -> {
                    if (dateRangeDto.getMonthOfYear().equals(reservationSchedule.getMonth())) {
                        availabilityRes.setMonth(reservationSchedule.getMonth());
                        dateRangeDto.getDatesOfMonths().stream().forEach(date -> {
                            reservationSchedule.getSchedules().stream().forEach(schedule -> {
                                if (schedule.getDate() == Integer.valueOf(date).intValue()) {
                                    Schedule schedule1 = new Schedule();
                                    schedule1.setAvailability(schedule.getAvailability());
                                    schedule1.setDate(schedule.getDate());
                                    schedule1.setPriceList(schedule.getPriceList());
                                    schedule1.setTotalInventory(schedule.getTotalInventory());
                                    availabilityRes.getSchedules().add(schedule1);
                                }
                            });

                        });
                    }
                });
                availabilityResList.add(availabilityRes);
            });
            return ResponseEntity.ok(availabilityResList);
        } catch (NullPointerException e) {
            return ResponseDetails.pleaseTryagainResponse();
        } catch (Exception e) {
            return ResponseDetails.pleaseTryagainResponse();
        }
    }

    @PostMapping("/savereservation")
    public ResponseEntity saveReservation(HttpServletRequest request, @RequestBody ReservationReq reservationReq) {
        log.debug("Save Reservation Details");
        try {
            if(null==reservationMaster) {
                loadData();
            }
            long daysBetween = ChronoUnit.DAYS.between(reservationReq.getStartDate(), reservationReq.getEndDate());
            if (daysBetween > 3) {
                return ResponseDetails.unsucessfullResponse("Max allowed stay is 3 days");
            }

            long bookingDate = ChronoUnit.DAYS.between(LocalDate.now(), reservationReq.getStartDate());
            if (bookingDate < 1) {
                return ResponseDetails.unsucessfullResponse("Booking cannot be done on the Departure Date");
            } else if (bookingDate > 30) {
                return ResponseDetails.unsucessfullResponse("Booking is allowed within 30 days only");
            } else {
                DateCalculation dateCalculation = new DateCalculation();

                SimpleDateFormat startDateString = new SimpleDateFormat("yyyy-MM-dd");
                ZoneId defaultZoneId = ZoneId.systemDefault();
                List<DateRangeDto> detailsBetweenTwoDates =
                        dateCalculation.getMonthDetailsBetweenTwoDates(DateTime.parse(startDateString.format(Date.from(reservationReq.getStartDate().atStartOfDay(defaultZoneId).toInstant()))),
                                DateTime.parse(startDateString.format(Date.from(reservationReq.getEndDate().atStartOfDay(defaultZoneId).toInstant()))), new ArrayList<>());


                final Boolean[] inventoryAvailable = {Boolean.FALSE};
                detailsBetweenTwoDates.stream().forEach(dateRangeDto -> {
                    reservationReq.getPaxDetails().stream().forEach(paxDetails -> {
                        reservationMaster.getReservationSchedules().stream().forEach(reservationSchedule -> {
                            if (dateRangeDto.getMonthOfYear().equals(reservationSchedule.getMonth())) {
                                dateRangeDto.getDatesOfMonths().stream().forEach(date -> {
                                    reservationSchedule.getSchedules().stream().forEach(schedule -> {
                                        if (schedule.getDate() == Integer.valueOf(date).intValue()) {
                                            if (schedule.getAvailability() >= Integer.valueOf(paxDetails.getNumberOfPax())) {
                                                schedule.setAvailability(schedule.getAvailability() - Integer.valueOf(paxDetails.getNumberOfPax()));
                                            } else if (schedule.getAvailability() < Integer.valueOf(paxDetails.getNumberOfPax())) {
                                                inventoryAvailable[0] = Boolean.TRUE;
                                            }
                                        }
                                    });
                                });
                            }
                        });
                    });
                });
                if (inventoryAvailable[0]) {
                    return ResponseDetails.unsucessfullResponse("Inventory unavailable");
                } else {
                    ModelMapper modelMapper=new ModelMapper();
                    ReservationBookingMaster reservationBookingMaster = modelMapper.map(reservationReq, ReservationBookingMaster.class);
                    reservationBookingMaster.setStatus("Confirmed");
                    String randomAlphabetic = RandomStringUtils.randomAlphabetic(6);
                    reservationBookingMaster.setBookingId(randomAlphabetic);
                    reservationBookingMasterList.add(reservationBookingMaster);
                    return ResponseDetails.successResponse("Booking is successfull ,Booking Id - "+randomAlphabetic);
                }
            }
        } catch (NullPointerException e) {
            return ResponseDetails.pleaseTryagainResponse();

        } catch (Exception e) {
            return ResponseDetails.pleaseTryagainResponse();
        }
    }

    @PostMapping("/cancelBooking")
    public ResponseEntity cancelBooking(HttpServletRequest request, @RequestBody CancelBooking cancelBooking) {

        DateCalculation dateCalculation = new DateCalculation();
        reservationBookingMasterList.stream().forEach(reservationBookingMaster -> {
            if (reservationBookingMaster.getBookingId().equals(cancelBooking.getBookingId())) {
                reservationBookingMaster.setStatus("Cancelled");
                SimpleDateFormat startDateString = new SimpleDateFormat("yyyy-MM-dd");
                ZoneId defaultZoneId = ZoneId.systemDefault();
                List<DateRangeDto> detailsBetweenTwoDates =
                        dateCalculation.getMonthDetailsBetweenTwoDates(DateTime.parse(startDateString.format(Date.from(reservationBookingMaster.getStartDate().atStartOfDay(defaultZoneId).toInstant()))),
                                DateTime.parse(startDateString.format(Date.from(reservationBookingMaster.getEndDate().atStartOfDay(defaultZoneId).toInstant()))), new ArrayList<>());

                final Boolean[] inventoryAvailable = {Boolean.FALSE};
                detailsBetweenTwoDates.stream().forEach(dateRangeDto -> {
                    reservationBookingMaster.getPaxDetails().stream().forEach(paxDetails -> {
                        reservationMaster.getReservationSchedules().stream().forEach(reservationSchedule -> {
                            if (dateRangeDto.getMonthOfYear().equals(reservationSchedule.getMonth())) {
                                dateRangeDto.getDatesOfMonths().stream().forEach(date -> {
                                    reservationSchedule.getSchedules().stream().forEach(schedule -> {
                                        if (schedule.getDate() == Integer.valueOf(date).intValue()) {
                                            schedule.setAvailability(schedule.getAvailability() + Integer.valueOf(paxDetails.getNumberOfPax()));
                                        }
                                    });
                                });
                            }
                        });
                    });
                });
            }

        });
        return ResponseDetails.successResponse("Booking has been cancelled for booking id " + cancelBooking.getBookingId());
    }


    @PostMapping("/modifyreservation")
    public ResponseEntity modifyReservation(HttpServletRequest request, @RequestBody UpdateReservationReq reservationReq) {

        final ReservationBookingMaster[] toRemove = {new ReservationBookingMaster()};
        reservationBookingMasterList.stream().forEach(reservationBookingMaster -> {
            if (reservationBookingMaster.getBookingId().equals(reservationReq.getBookingId())) {
                toRemove[0] = reservationBookingMaster;
                DateCalculation dateCalculation = new DateCalculation();
                SimpleDateFormat startDateString = new SimpleDateFormat("yyyy-MM-dd");
                ZoneId defaultZoneId = ZoneId.systemDefault();
                List<DateRangeDto> detailsBetweenTwoDates =
                        dateCalculation.getMonthDetailsBetweenTwoDates(DateTime.parse(startDateString.format(Date.from(reservationBookingMaster.getStartDate().atStartOfDay(defaultZoneId).toInstant()))),
                                DateTime.parse(startDateString.format(Date.from(reservationBookingMaster.getEndDate().atStartOfDay(defaultZoneId).toInstant()))), new ArrayList<>());

//Add Inventory Back
                detailsBetweenTwoDates.stream().forEach(dateRangeDto -> {
                    reservationBookingMaster.getPaxDetails().stream().forEach(paxDetails -> {
                        reservationMaster.getReservationSchedules().stream().forEach(reservationSchedule -> {
                            if (dateRangeDto.getMonthOfYear().equals(reservationSchedule.getMonth())) {
                                dateRangeDto.getDatesOfMonths().stream().forEach(date -> {
                                    reservationSchedule.getSchedules().stream().forEach(schedule -> {
                                        if (schedule.getDate() == Integer.valueOf(date).intValue()) {
                                            schedule.setAvailability(schedule.getAvailability() + Integer.valueOf(paxDetails.getNumberOfPax()));
                                        }
                                    });
                                });
                            }
                        });
                    });
                });



            }
        });
        reservationBookingMasterList.remove(toRemove[0]);
//        Remove From inventory
        DateCalculation dateCalculation = new DateCalculation();

        SimpleDateFormat startDateString = new SimpleDateFormat("yyyy-MM-dd");
        ZoneId defaultZoneId = ZoneId.systemDefault();
        List<DateRangeDto> detailsBetweenTwoDates =
                dateCalculation.getMonthDetailsBetweenTwoDates(DateTime.parse(startDateString.format(Date.from(reservationReq.getStartDate().atStartOfDay(defaultZoneId).toInstant()))),
                        DateTime.parse(startDateString.format(Date.from(reservationReq.getEndDate().atStartOfDay(defaultZoneId).toInstant()))), new ArrayList<>());

        final Boolean[] inventoryAvailable = {Boolean.FALSE};
        detailsBetweenTwoDates.stream().forEach(dateRangeDto -> {
            reservationReq.getPaxDetails().stream().forEach(paxDetails -> {
                reservationMaster.getReservationSchedules().stream().forEach(reservationSchedule -> {
                    if (dateRangeDto.getMonthOfYear().equals(reservationSchedule.getMonth())) {
                        dateRangeDto.getDatesOfMonths().stream().forEach(date -> {
                            reservationSchedule.getSchedules().stream().forEach(schedule -> {
                                if (schedule.getDate() == Integer.valueOf(date).intValue()) {
                                    if (schedule.getAvailability() >= Integer.valueOf(paxDetails.getNumberOfPax())) {
                                        schedule.setAvailability(schedule.getAvailability() - Integer.valueOf(paxDetails.getNumberOfPax()));
                                    } else if (schedule.getAvailability() < Integer.valueOf(paxDetails.getNumberOfPax())) {
                                        inventoryAvailable[0] = Boolean.TRUE;
                                    }
                                }
                            });
                        });
                    }
                });
            });
        });
        if (inventoryAvailable[0]) {
            return ResponseDetails.unsucessfullResponse("Inventory unavailable");
        } else {
            ModelMapper modelMapper=new ModelMapper();
            ReservationBookingMaster bookingMaster = modelMapper.map(reservationReq, ReservationBookingMaster.class);
            bookingMaster.setStatus("Confirmed");
            reservationBookingMasterList.add(bookingMaster);
            return ResponseDetails.successResponse("Modified booking successfully for booking id - " + bookingMaster.getBookingId());
        }
    }

    @GetMapping("/loadData")
    public static void loadData() {

        reservationMaster = new ReservationMaster();
//        Cancelltion Policy
        reservationMaster.getCancellationPolicy().add("Till 72 hours before departure time: 10% of basic fare");
        reservationMaster.getCancellationPolicy().add("Between 72 hours and up to 24 hours before departure time: 25% of basic fare.");
        reservationMaster.getCancellationPolicy().add("Between 24 hours and up to 30 minutes from departure time: 50% of basic fare");

//        Exclusion
        reservationMaster.getExclusion().add("Package price does not include Gala dinner charges applicable on Christmas and New Year's Eve");

//        Polices
        reservationMaster.getPolicies().add("\n" +
                "Please note that these packages are customizable, which means that you will be able to make changes to the itinerary/activity " +
                "if you so desire. The final payment will be calculated as per the activities reflecting on the website which will be outlined in the " +
                "confirmatory e-mail sent to you");


        DateCalculation dateCalculation = new DateCalculation();
        List<DateRangeDto> detailsBetweenTwoDates = dateCalculation.getMonthDetailsBetweenTwoDates(DateTime.now(), DateTime.now().plusDays(90), new ArrayList<>());

        detailsBetweenTwoDates.stream().forEach(dateRangeDto -> {
            ReservationMaster.ReservationSchedule reservationSchedule = new ReservationMaster.ReservationSchedule();
            reservationSchedule.setMonth(dateRangeDto.getMonthOfYear());
            dateRangeDto.getDatesOfMonths().stream().forEach(date -> {

                Schedule schedule = new Schedule();
                schedule.setAvailability(30);
                schedule.setDate(date);
                schedule.setTotalInventory(30);

                Price price = new Price();
                price.setLabel("Adult");
                price.setPrice("1000");

                Price price1 = new Price();
                price1.setLabel("Child");
                price1.setPrice("500");

                schedule.getPriceList().add(price);
                schedule.getPriceList().add(price1);
                reservationSchedule.getSchedules().add(schedule);

            });
            reservationMaster.getReservationSchedules().add(reservationSchedule);
        });
    }
}
