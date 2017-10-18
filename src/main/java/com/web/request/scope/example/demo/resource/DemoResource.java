package com.web.request.scope.example.demo.resource;

import com.web.request.scope.example.demo.dto.*;
import com.web.request.scope.example.demo.service.DemoService;
import groovy.lang.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(method = RequestMethod.DELETE, path = "/clear-db", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public void clearDb() {
        demoService.clearDb();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/bindings", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public List<BindingResponseDto> getThreadTransactionIdBindings() {
        return demoService.getThreadTransactionIdBindings();
    }
}
