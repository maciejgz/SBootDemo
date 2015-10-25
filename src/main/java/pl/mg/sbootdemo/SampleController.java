package pl.mg.sbootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by m on 2015-10-15.
 */
@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class SampleController {

    @RequestMapping("/")
    @ResponseBody
    String home(){
        return "sample";
    }

    public static void main(String[] args){
        System.out.println("starting boot");
        SpringApplication.run(SampleController.class, args);

    }

}
