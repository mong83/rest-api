package com.study.event;

import com.study.accounts.Account;
import com.study.accounts.AccountAdpater;
import com.study.accounts.CurrentUser;
import com.study.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events" , produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto ,
                                      Errors errors,
                                      @CurrentUser Account currentUser){

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto,errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }


        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(currentUser);

        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri =  selfLinkBuilder.toUri();

        EventResource eventResource = new EventResource(event);

        eventResource.add(linkTo(EventController.class).withRel("query-events") );
       // eventResource.add(selfLinkBuilder.withSelfRel()); eventresource 만들때 넣어주도록 개선
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable ,
                                      PagedResourcesAssembler<Event> assembler,
                                      @CurrentUser Account account){
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> pagedResources = assembler.toModel(page, e-> new EventResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        if(account != null){
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }

        return  ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser){

        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if(optionalEvent.isPresent()==false){
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));

        if(event.getManager().equals(currentUser)){
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);

    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id ,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isPresent() ==false){
            return ResponseEntity.notFound().build();
        }

        //DTO 의 validation check 예: not null
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        //biz 로직검사를 통한 validation check
        this.eventValidator.validate(eventDto,errors);

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();

        if(!existingEvent.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto,existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}

