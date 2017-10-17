package com.web.request.scope.example.demo.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DbResponseDto {

    private Map<String, List<String>> db;
}
