package com.example.futureMovement.model;

import java.math.BigDecimal;

public record Transaction(Account account, Product product, BigDecimal longPosition, BigDecimal shortPosition, BigDecimal netTotal) {}
