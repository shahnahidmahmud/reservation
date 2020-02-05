package in.com.firstchoice.reservation.common;


import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseDetails {
    
    public static ResponseEntity successResponse(String message){
        JSONObject object = new JSONObject();

        object.put("message", message);
        object.put("status", "Success");
        return new ResponseEntity(object, HttpStatus.valueOf(200));
    }

    public static ResponseEntity unsucessfullResponse(String message){
        JSONObject object = new JSONObject();

        object.put("message", message);
        object.put("status", "Fail");
        return new ResponseEntity(object, HttpStatus.valueOf(400));
    }

    public static ResponseEntity pleaseTryagainResponse(){
        JSONObject object = new JSONObject();

        object.put("message", "Please try again");
        object.put("status", "Fail");
        return new ResponseEntity(object, HttpStatus.valueOf(400));
    }
}
