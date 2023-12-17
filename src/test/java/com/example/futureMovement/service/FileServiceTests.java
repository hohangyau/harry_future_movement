/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.futureMovement.service;

import com.example.futureMovement.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@SpringBootTest
@AutoConfigureMockMvc
public class FileServiceTests {

	private final static String inputFile = "csv/InputDummy.txt";

	private final static String outputFile = "csv/OutputDummy.csv";
	private static final String dummyRecordLong  = "315CL  432100020001SGXDC FUSGX NK    20100910JPY01B 0000000001 0000000000000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000092500000000             O";
	private static final String dummyRecordShort = "315CL  432100020001SGXDC FUSGX NK    20100910JPY01B 0000000000 0000000001000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000092500000000             O";
	private static final String dummyRecord1234  = "315CL  123400020001SGXDC FUSGX NK    20100910JPY01B 0000000000 0000000001000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000092500000000             O";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ReportService reportService;

	@Autowired
	private FileService fileService;



	@Test
	public void canWriteToCSVFile() throws IOException, URISyntaxException {
		Transaction t1 = reportService.toTransaction(dummyRecordLong);
		Transaction t2 = reportService.toTransaction(dummyRecordShort);
		Transaction t3 = reportService.toTransaction(dummyRecord1234);


		List<Transaction> transactionList = new ArrayList<>();
		transactionList.add(t1);
		transactionList.add(t1);
		transactionList.add(t1);
		transactionList.add(t2);
		transactionList.add(t3);


		List<Transaction> actual = reportService.processData(transactionList);

		fileService.writer(actual, outputFile);

	}

	@Test
	public void canReadFromTxtFile() throws URISyntaxException {
		List<String> lines = fileService.readFromFile(inputFile);

		String expected = "315CL  432100020001SGXDC FUSGX NK    20100910JPY01B 0000000001 0000000000000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000092500000000             O";
		assert Objects.equals(lines.get(0), expected);

	}


}
