package com.study.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.accounts.Account;
import com.study.accounts.AccountRepository;
import com.study.accounts.AccountRole;
import com.study.accounts.AccountService;
import com.study.common.AppProperties;
import com.study.common.BaseControllerTest;
import com.study.common.RestDocsConfiguration;
import com.study.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseControllerTest {


    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void Setup(){
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }


    @Test
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("seoul")
                .build();


        mockMvc.perform(post("/api/events/")
                         .header(HttpHeaders.AUTHORIZATION,"Bearer" + getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                //.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of enrollment"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("endEventDateTime").description("date time of end of event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of enrollment"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("endEventDateTime").description("date time of end of event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of event"),
                                fieldWithPath("free").description("it tells if this event is free"),
                                fieldWithPath("offline").description("it tells if this event is offline"),
                                fieldWithPath("eventStatus").description("eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")

                        )
                ))
        ;
    }

    private String getBearerToken() throws Exception {

        Set<AccountRole> accountRoles = Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet());

        Account account = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(accountRoles)
                .build();
        accountService.saveAccount(account);

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))//head를 만든거고!
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));
        String responsebody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responsebody).get("access_token").toString();
    }

    @Test
    @TestDescription("입력받을수 없는 값을 사용한경우에 에러발생한케이스")
    public void createEvent_Bad_Request() throws Exception {

        Event event = Event.builder()
                .id(100) //입력받지 말아야 할 항목
                .name("spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(true) //입력받지 말아야 할 항목
                .offline(false) //입력받지 말아야 할 항목
                .build();


        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION,"Bearer" + getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())

        ;
    }

    /*
    @Test
    @TestDescription("입력값이 비어있는경우에 에러발생한케이스")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {

            EventDto eventDto  = EventDto.builder().build();

            this.mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());

    }
    */

    @Test
    @TestDescription("입력값이 잘못된 경우에 에러발생한케이스")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {

        EventDto eventDto  = EventDto.builder()
                .name("spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,27,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        this.mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION,"Bearer" + getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())

        ;

    }
    @Test
    @TestDescription("30개 이벤트를 10개씩 조회할때 , 2번째 페이지 조회하는 케이스 ")
    public  void queryEvents() throws  Exception{

        //event 30개 저장
        IntStream.range(0,30).forEach(i->{
            this.generateEvent(i);
        });

        this.mockMvc.perform(get("/api/events")
                     .param("page","1")
                     .param("size","10")
                     .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
                ;
    }

    @Test
    @TestDescription("30개 이벤트를 10개씩 조회할때 , 2번째 페이지 조회하는 케이스 with인증정보")
    public  void queryEventsWithAuthentication() throws  Exception{

        //event 30개 저장
        IntStream.range(0,30).forEach(i->{
            this.generateEvent(i);
        });

        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION,"Bearer" + getBearerToken())
                .param("page","1")
                .param("size","10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("seoul")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }

    @Test
    @TestDescription("기존 이벤트 하나 조회하기")
    public  void getEvent() throws  Exception{
        Event event  = this.generateEvent(100);

        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))

        ;

    }

    @Test
    @TestDescription("없는 이벤트 조회할때 404 응답받기")
    public  void getEvent404() throws  Exception{

        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트 수정하기")
    public  void updateEvent() throws  Exception{

        Event event  = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION,"Bearer" + getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"))
        ;

    }

    /*
    @Test
    @TestDescription("입력값이 비어있는경우에 이벤트 수정실패")
    public  void updateEvent400_Empty() throws  Exception{

        Event event  = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        ;
    }
   */

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정실패")
    public  void updateEvent400_Wrong() throws  Exception{

        Event event  = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION,"Bearer" + getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정실패")
    public  void updateEvent404() throws  Exception{

        Event event  = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/123123", event.getId())
                .header(HttpHeaders.AUTHORIZATION,"Bearer" + getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
        ;
    }
}

