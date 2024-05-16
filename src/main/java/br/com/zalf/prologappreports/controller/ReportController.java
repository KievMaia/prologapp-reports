package br.com.zalf.prologappreports.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/reports")
public class ReportController {
    @GetMapping(path = "/", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> helloWorld() {
        return null;
    }
}
