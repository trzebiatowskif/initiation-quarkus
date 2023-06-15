package org.acme;


import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
@CustomQualifier
public class CustomGreetingService extends GreetingService {
    @Override
    public String hello() {
        return "override service!!!";
    }
}
