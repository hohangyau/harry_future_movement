package com.example.futureMovement.service;

import com.example.futureMovement.model.Account;
import com.example.futureMovement.model.Product;
import com.example.futureMovement.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class ReportService {




    // TODO, store the parsing conditions into a CONSTANT file
    private final static String inputFile = "csv/Input.txt";

    private final static String outputFile = "csv/Output.csv";

    private static int clientTypeStartIndex = 3;
    private static int clientTypeLength = 4;
    private static int clientNumberStartIndex = 7;
    private static int clientNumberLength = 4;
    private static int accountNumberStartIndex = 11;
    private static int accountNumberLength = 4;
    private static int subAccountNumberStartIndex = 15;
    private static int subAccountNumberLength = 4;

    @Autowired
    private FileService fileService;


    public List<Transaction> generateReport() throws URISyntaxException, IOException {

        List<Transaction> result = Collections.emptyList();

        // Step 1: read from input file
        List<String> rawRecords = fileService.readFromFile(inputFile);

        if(rawRecords == null || rawRecords.isEmpty()){
            //LOGGER.error()
            System.out.println("Nothing to be processed");
            return result;
        }

        // Step 2: convert raw data into POJO
        List<Transaction> allTransactions = new ArrayList<>();
        for(String str : rawRecords ){
            allTransactions.add(toTransaction(str));
        }


        // Step 3: process data
        result = processData(allTransactions);


        // Step 4: write data into csv file
        fileService.writer(result, outputFile);

        return result;


    }

    public Transaction toTransaction(String record){

        if(null == record || record.isBlank()) return null;

        Transaction transaction;
        Account account;
        Product product;

        String clientType = record.substring(clientTypeStartIndex, clientTypeStartIndex + clientTypeLength).trim();
        String clientNumber = record.substring(clientNumberStartIndex, clientNumberStartIndex + clientNumberLength).trim();
        String accountNumber = record.substring(accountNumberStartIndex, accountNumberStartIndex + accountNumberLength).trim();
        String subAccountNumber = record.substring(subAccountNumberStartIndex, subAccountNumberStartIndex + subAccountNumberLength).trim();

        account = new Account(clientType, clientNumber, accountNumber, subAccountNumber);

        // TODO should store the parsing info into CONSTANT File
        String exchangeCode = record.substring(27, 27 + 4).trim();
        String productGroupCode = record.substring(25, 25 + 2).trim();
        String symbol = record.substring(31, 31 + 6).trim();
        String expirationDae = record.substring(37, 37 + 8).trim();

        product = new Product(exchangeCode, productGroupCode, symbol, expirationDae);

        String quantityLongStr = record.substring(52, 52 + 10).trim();
        String quantityShortStr = record.substring(63, 63 + 10).trim();

        BigDecimal quantityLong = new BigDecimal(quantityLongStr);
        BigDecimal quantityShort = new BigDecimal(quantityShortStr);

        BigDecimal netTotal = quantityLong.subtract(quantityShort);

        transaction = new Transaction(account, product, quantityLong, quantityShort, netTotal);
        //System.out.println(transaction);
        return transaction;

    }

    public List<Transaction> processData(List<Transaction> transactions){


        if(transactions == null || transactions.isEmpty()) return new ArrayList<>();

        Map<String, Transaction> uniqueTransactions = new HashMap<>();

        for(Transaction transaction : transactions){
            String key = toKey(transaction);
            if(uniqueTransactions.containsKey(key)){
                Transaction oldValue = uniqueTransactions.get(key);
                BigDecimal newLongPosition = oldValue.longPosition().add(transaction.longPosition());
                BigDecimal newShortPosition = oldValue.shortPosition().add(transaction.shortPosition());
                BigDecimal newNetTotal = newLongPosition.subtract(newShortPosition);
                //BigDecimal newNetAmount = oldValue.netTotal().add(transaction.longPosition()).subtract(transaction.shortPosition());
                uniqueTransactions.put(key,new Transaction(transaction.account(), transaction.product(), newLongPosition, newShortPosition, newNetTotal));
            }else{
                uniqueTransactions.put(key, transaction);
            }
        }

        return uniqueTransactions.values().stream().toList();
    }


    private String toKey(Transaction transaction){
        return transaction.account().clientNumber() +
                transaction.account().clientType() +
                transaction.account().accountNumber() +
                transaction.account().subAccountNumber() +
                transaction.product().exchangeCode() +
                transaction.product().productGroupCode() +
                transaction.product().symbol();
    }


}
