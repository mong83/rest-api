package com.study.event;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("rest api")
                .description("rest api with spring")
                .build();
        assertThat(event).isNotNull();

    }

    @Test
    public void javaBean(){

        String name = "Event";
        String description = "spring";

        Event event = new Event();

        event.setName(name);
        event.setDescription(description);

         assertThat(event.getName()).isEqualTo(name);
         assertThat(event.getName()).isEqualTo(description);



    }

}