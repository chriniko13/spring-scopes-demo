package com.web.request.scope.example.demo.resource;

import com.web.request.scope.example.demo.dto.DbHealthResponseDto;
import com.web.request.scope.example.demo.dto.DbResponseDto;
import com.web.request.scope.example.demo.dto.DemoRequestDto;
import com.web.request.scope.example.demo.dto.DemoResponseDto;
import com.web.request.scope.example.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
public class DemoResource {

    @Autowired
    private DemoService demoService;

    @RequestMapping(method = RequestMethod.POST, path = "/test-annotation", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public @ResponseBody
    DemoResponseDto demoAnnotation(@RequestBody DemoRequestDto demoRequestDto) {
        return demoService.testServiceAnnotation(demoRequestDto);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/test-threadlocal", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public @ResponseBody
    DemoResponseDto demoThreadLocal(@RequestBody DemoRequestDto demoRequestDto) {
        return demoService.testServiceThreadLocal(demoRequestDto);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ensure-healthy-db", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public @ResponseBody
    DbHealthResponseDto ensureHealthyDb() {
        return new DbHealthResponseDto(demoService.ensureHealthyDb());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/db-status", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public DbResponseDto db() {
        return demoService.getDbStatus();
    }

}
