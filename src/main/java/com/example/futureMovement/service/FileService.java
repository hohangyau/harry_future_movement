package com.example.futureMovement.service;

import com.example.futureMovement.model.Transaction;
import com.opencsv.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FileService {


    //TODO: store into CONSTANT file


    private final static String split = "\\|";

    private final static String OUT_PUT_FILE_HEADER = "Client_Information|Product_Information|Total_Transaction_Amount";


    public List<String> readFromFile(String inputFile) throws URISyntaxException {
        Path path = Paths.get(
                ClassLoader.getSystemResource(inputFile).toURI()
        );

        System.out.println("About to read records into file at: " + inputFile);

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(
                    path,
                    StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("ERROR in reading input file at: " + path);
            e.printStackTrace();
        }

        System.out.println("Total number of transactions: " + lines.size());

        return lines;
    }


    public void writer(List<Transaction> transactions, String outputFile) throws IOException, URISyntaxException {
        List<String[]> toBeWritten = new ArrayList<>();
        String[] header = OUT_PUT_FILE_HEADER.split(split);
        toBeWritten.add(header);

        for(Transaction transaction : transactions){
            String[] temp = new String[3];
            temp[0] = transaction.account().clientType() + transaction.account().clientNumber() + transaction.account().accountNumber() + transaction.account().subAccountNumber();
            temp[1] = transaction.product().exchangeCode() + transaction.product().productGroupCode() + transaction.product().symbol() + transaction.product().expirationDate();
            temp[2] = transaction.netTotal().toString();

            toBeWritten.add(temp);

        }

        writeToFile(toBeWritten, outputFile);



    }

    public void writeToFile(List<String[]> records, String outputFile) throws IOException, URISyntaxException {

        System.out.println("About to write records into file at: " + outputFile);
        System.out.println("Total records: " + records.size());


        Path path = Paths.get(
                ClassLoader.getSystemResource(outputFile).toURI()
        );

        try(Writer writer = Files.newBufferedWriter(path);
            ICSVWriter icsvWriter = new CSVWriterBuilder(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withEscapeChar(CSVWriter.DEFAULT_SEPARATOR)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END).build();){

            icsvWriter.writeAll(records);

            } catch (Exception ex){

            System.out.println("ERROR in writing records into csv file");
        }

    }
}
