package com.study.event;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
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
    @Parameters(method = "paramsForTestFree")
    public void testFree(int basePrice, int maxPrice , boolean isFree){
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        event.update();

        assertThat(event.isFree()).isEqualTo(isFree);

    }

    private Object[] paramsForTestFree(){
        return new Object[]{
                new Object[] {0,0,true},
                new Object[] {100,0,false},
                new Object[] {0,100,false},
                new Object[] {100,200,false}
        };
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