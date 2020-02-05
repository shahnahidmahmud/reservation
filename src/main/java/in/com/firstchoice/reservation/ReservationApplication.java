package in.com.firstchoice.reservation;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:application.properties")
public class ReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationApplication.class, args);
	}

	@Bean
	public ModelMapper modelmapper(){
		return new ModelMapper();
	}

}
