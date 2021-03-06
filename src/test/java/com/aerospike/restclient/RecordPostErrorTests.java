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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
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

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Parameterized.class)
@SpringBootTest
public class RecordPostErrorTests {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMVC;

	@Autowired
	private AerospikeClient client;

	@Autowired
	private WebApplicationContext wac;

	@Parameters
	public static Object[] getParams() {
		return new Object[] {true, false};
	}

	private String nonExistentNSEndpoint = null;
	private String existingRecordEndpoint = null;
	private Key testKey = null;

	public RecordPostErrorTests(boolean useSet) {
		if (useSet) {
			nonExistentNSEndpoint = ASTestUtils.buildEndpoint("kvs", "fakeNS", "demo", "1");
			existingRecordEndpoint = ASTestUtils.buildEndpoint("kvs", "test", "junit", "getput");
			testKey = new Key("test", "junit", "getput");
		} else {
			nonExistentNSEndpoint = ASTestUtils.buildEndpoint("kvs", "fakeNS", "1");
			existingRecordEndpoint = ASTestUtils.buildEndpoint("kvs", "test", "getput");
			testKey = new Key("test", null, "getput");
		}
	}

	@Before
	public void setup() {
		mockMVC = MockMvcBuilders.webAppContextSetup(wac).build();
		Bin baseBin = new Bin("initial", "bin");
		client.put(null, testKey, baseBin);
	}

	@After
	public void clean() {
		client.delete(null, testKey);
	}

	@Test
	public void PostRecordToInvalidNamespace() throws Exception {
		Map<String, Object> binMap = new HashMap<String, Object>();
		binMap.put("integer", 12345);

		mockMVC.perform(post(nonExistentNSEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(binMap)))
		.andExpect(status().isInternalServerError());
	}

	@Test
	public void PostRecordToExistingKey() throws Exception {
		Map<String, Object> binMap = new HashMap<String, Object>();

		binMap.put("string", "Aerospike");

		mockMVC.perform(post(existingRecordEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(binMap))
				).andExpect(status().isConflict());

	}


}
