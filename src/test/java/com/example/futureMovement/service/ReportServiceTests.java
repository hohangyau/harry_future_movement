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
import com.example.futureMovement.service.FileService;
import com.example.futureMovement.service.ReportService;
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
public class ReportServiceTests {


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
	public void canParseToTransactionPojo() throws Exception {

		Transaction actual = reportService.toTransaction(dummyRecordLong);

		assert actual != null;

		assert Objects.equals(actual.account().clientType(), "CL");
		assert Objects.equals(actual.account().clientNumber(), "4321");
		assert Objects.equals(actual.account().accountNumber(), "0002");
		assert Objects.equals(actual.account().subAccountNumber(), "0001");


		assert Objects.equals(actual.product().exchangeCode(), "SGX");
		assert Objects.equals(actual.product().productGroupCode(), "FU");
		assert Objects.equals(actual.product().symbol(), "NK");
		assert Objects.equals(actual.product().expirationDate(), "20100910");

		assert Objects.equals(actual.longPosition(), BigDecimal.valueOf(1));
		assert Objects.equals(actual.shortPosition(), BigDecimal.valueOf(0));

		assert Objects.equals(actual.netTotal(), BigDecimal.valueOf(1));


	}

	@Test
	public void canProcessData(){
		Transaction t = reportService.toTransaction(dummyRecordLong);


		List<Transaction> transactionList = new ArrayList<>();
		transactionList.add(t);
		transactionList.add(t);
		transactionList.add(t);

		List<Transaction> actual = reportService.processData(transactionList);

		System.out.println(actual.get(0));
		assert !actual.isEmpty();
		assert Objects.equals(actual.get(0).netTotal(), BigDecimal.valueOf(3));

	}

	@Test
	public void canProcessDataWithShortPosition(){
		Transaction t1 = reportService.toTransaction(dummyRecordLong);
		Transaction t2 = reportService.toTransaction(dummyRecordShort);


		List<Transaction> transactionList = new ArrayList<>();
		transactionList.add(t1);
		transactionList.add(t1);
		transactionList.add(t1);
		transactionList.add(t2);


		List<Transaction> actual = reportService.processData(transactionList);

		System.out.println(actual.get(0));
		assert !actual.isEmpty();
		assert Objects.equals(actual.get(0).netTotal(), BigDecimal.valueOf(2)); // 3 long - 1 short

	}

	@Test
	public void canProcessDataWithDifferentClientIds(){
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

		for(Transaction transaction : actual){
			System.out.println(transaction);
			if("4321".equals(transaction.account().clientNumber())){
				assert Objects.equals(transaction.netTotal(), BigDecimal.valueOf(2)); // 3 long - 1 short
			}else{
				assert Objects.equals(transaction.netTotal(), BigDecimal.valueOf(-1));
			}
		}


	}



}
