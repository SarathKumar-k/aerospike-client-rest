/*
 * Copyright 2019 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.aerospike.restclient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Parameterized.class)
@SpringBootTest
public class RecordPutErrorTests {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Parameters
	public static Object[] getParams() {
		return new Object[] {true, false};
	}

	private String nonExistentNSEndpoint = null;
	private String nonExistentRecordEndpoint = null;

	public RecordPutErrorTests(boolean useSet) {
		if (useSet) {
			nonExistentNSEndpoint = ASTestUtils.buildEndpoint("kvs", "fakeNS", "demo", "1");
			nonExistentRecordEndpoint = ASTestUtils.buildEndpoint("kvs", "test", "demo", "thisisnotarealkeyforarecord");
		} else {
			nonExistentNSEndpoint = ASTestUtils.buildEndpoint("kvs", "fakeNS", "1");
			nonExistentRecordEndpoint = ASTestUtils.buildEndpoint("kvs", "test", "thisisnotarealkeyforarecord");
		}
	}

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMVC;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() {
		mockMVC = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void putToNonExistentNS() throws Exception {
		Map<String, Object> binMap = new HashMap<String, Object>();
		binMap.put("integer", 12345);

		mockMVC.perform(put(nonExistentNSEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(binMap)))
		.andExpect(status().isInternalServerError());
	}

	@Test
	public void putToNonExistentRecord() throws Exception {
		Map<String, Object> binMap = new HashMap<String, Object>();

		binMap.put("string", "Aerospike");

		mockMVC.perform(put(nonExistentRecordEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(binMap))
				).andExpect(status().isNotFound());

	}


}
