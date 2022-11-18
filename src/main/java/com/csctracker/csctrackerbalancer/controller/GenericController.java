package com.csctracker.csctrackerbalancer.controller;

import com.csctracker.csctrackerbalancer.service.GenericService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GenericController {

    private final GenericService genericService;

    public GenericController(GenericService genericService) {
        this.genericService = genericService;
    }

    @GetMapping(value = "/{service}/{object}", produces = "application/json")
    public ResponseEntity<String> list(@PathVariable String service, @PathVariable String object) {
        try {
            return ResponseEntity.ok(genericService.get(service, object));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(value = "/{service}/{object}/{sub}", produces = "application/json")
    public ResponseEntity<String> list(@PathVariable String service, @PathVariable String object, @PathVariable String sub) {
        try {
            return ResponseEntity.ok(genericService.get(service, object + "/" + sub));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/{service}/{object}", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> save(@RequestBody String body, @PathVariable String service, @PathVariable String object) {
        try {
            return new ResponseEntity<>(genericService.save(service, object), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/{service}/{object}/{sub}", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> save(@RequestBody String body, @PathVariable String service, @PathVariable String object, @PathVariable String sub) {
        try{
            return new ResponseEntity<>(genericService.save(service, object + "/" + sub), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{service}/{object}", produces = "application/json")
    public ResponseEntity<String> delete(@RequestBody String body, @PathVariable String service, @PathVariable String object) {
        try {
            return ResponseEntity.ok(genericService.delete(service, object));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
