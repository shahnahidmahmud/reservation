package in.com.firstchoice.reservation.common;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;


@Data
public class Schedule {

    private int date;
    private List<Price> priceList = new ArrayList<>();
    private int totalInventory;
    private int availability;
}
