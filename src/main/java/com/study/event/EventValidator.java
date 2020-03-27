package com.study.event;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors){

        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() !=0){
            errors.reject("wrongPrices","prices are wrong");//global error
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime())||
           endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())||
           endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){

            errors.rejectValue("endEventDateTime","wrongValue","endEventDateTime is wrong");
            //field error
        }

        //추가 검증필요 (BeginEventDateTime/CloseEnrollmentDateTime)

    }
}
