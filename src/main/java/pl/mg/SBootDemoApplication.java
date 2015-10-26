package pl.mg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class SBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SBootDemoApplication.class, args);
    }

    /**
     * Health indicator - pokazuje wartość po podłączeniu przez jsoncole
     * @return
     */
    @Bean
    HealthIndicator healthIndicator(){
        return new HealthIndicator() {
            @Override
            public Health health() {
                return Health.status("my own health status").build();
            }
        };
    }


    @Bean
    CommandLineRunner runner(ReservationRepository rr) {

        return args -> {
            Arrays.asList("Les,Josh,Phil,Sasha,Peter".split(","))
                    .forEach(
                            n -> rr.save(new Reservation(n))
                    );

            rr.findAll().forEach(System.out::println);

            rr.findByReservationName("Les").forEach(System.out::println);
        };
    }


}

/**
 * Klasa z pakietu hateoas pozwalająca na przetwarzanie resource w locie i dodawanie np. linków hateoas - definiowanych samodzielnie
 */
@Component
class ReservationResourceProcessor implements ResourceProcessor<Resource<Reservation>> {

    @Override
    public Resource<Reservation> process(Resource<Reservation> reservationResource) {
        System.out.println("reservationProcessing");
        reservationResource.add(new Link("http://s3.com/imgs/" + reservationResource.getContent().getId() + ".jpg", "profile-photo"));

        return reservationResource;
    }
}

@Controller
class ReservationMvcController {

    @Autowired
    private ReservationRepository reservationRepository;

    @RequestMapping("/reservations.php")
    String reservations(Model model) {
        model.addAttribute("reservations", this.reservationRepository.findAll());
        return "reservations";
    }
}


//JPA
interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // select * from reservation where reservation_name = :rn test
    Collection<Reservation> findByReservationName(String rn);
}

@Entity
class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    private String reservationName;

    public Reservation() {

    }

    public Reservation(String reservationName) {
        this.reservationName = reservationName;
    }

    public String getReservationName() {
        return reservationName;
    }

    public void setReservationName(String reservationName) {
        this.reservationName = reservationName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationName='" + reservationName + '\'' +
                '}';
    }
}
//JPA

@RestController
class ReservationRestController {
    @RequestMapping("/reservations")
    Collection<Reservation> reservations() {
        return this.reservationRepository.findAll();
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    @Autowired
    public void setReservationRepository(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    private ReservationRepository reservationRepository;

}