package com.example.futureMovement.controller;

import com.example.futureMovement.model.Transaction;
import com.example.futureMovement.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


@RestController
class ReportController {


    @Autowired
    private ReportService reportService;

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transaction> generateReport() throws URISyntaxException, IOException {
        return reportService.generateReport();
    }





}

