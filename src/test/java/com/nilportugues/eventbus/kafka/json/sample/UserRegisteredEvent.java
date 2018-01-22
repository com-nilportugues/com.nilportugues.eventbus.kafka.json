package com.nilportugues.eventbus.kafka.json.sample;

import java.io.Serializable;

public class UserRegisteredEvent implements Serializable {
    public String id;
    public String hello;
}
