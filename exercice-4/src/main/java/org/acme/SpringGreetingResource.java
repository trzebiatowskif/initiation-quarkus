package org.acme;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class SpringGreetingResource {
    @Autowired
    private GreetingService greetingService;

    @GetMapping(path = "/spring-hello")
    public String greeting() {
        return greetingService.hello();
    }
}
