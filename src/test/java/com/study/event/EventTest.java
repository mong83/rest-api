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

    @Test
    public void  testFree(){
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        event.update();

        assertThat(event.isFree()).isTrue();


        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        event.update();

        assertThat(event.isFree()).isFalse();


        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        event.update();

        assertThat(event.isFree()).isFalse();
    }


    @Test
    public void  testOffline(){
        Event event = Event.builder()
                .location("강남역")
                .build();

        event.update();

        assertThat(event.isOffline()).isTrue();


        event = Event.builder()
                .build();

        event.update();

        assertThat(event.isOffline()).isFalse();

    }

}