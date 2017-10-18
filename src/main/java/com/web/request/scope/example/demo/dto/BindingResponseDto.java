package com.web.request.scope.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BindingResponseDto {

    private String threadName;
    private String transactionId;

}
