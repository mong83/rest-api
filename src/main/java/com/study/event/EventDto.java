package com.study.event;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NonNull
    private LocalDateTime beginEnrollmentDateTime;
    @NonNull
    private LocalDateTime closeEnrollmentDateTime;
    @NonNull
    private LocalDateTime beginEventDateTime;
    @NonNull
    private LocalDateTime endEventDateTime;

    private String location;
    @Min(0)
    private int basePrice;
    @Min(0)
    private int maxPrice;
    @Min(0)
    private int limitOfEnrollment;

}
