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
package com.aerospike.restclient.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aerospike.client.AerospikeException;
import com.aerospike.restclient.controllers.KeyValueController;
import com.aerospike.restclient.service.AerospikeRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
/*
 * Test that exceptions thrown further in the chain are propagated back through these controllers
 *
 */
public class KVControllerV1PutPostUpdateErrorTests {

	private String ns = "test";
	private String set = "set";
	private String key = "key";

	private Map<String, Object> dummyBins;
	private Map<String, String> queryParams;
	private AerospikeException expectedException = new AerospikeException("test exception");
	private byte[] msgpackBins;
	private ObjectMapper mpMapper = new ObjectMapper(new MessagePackFactory());

	@Autowired KeyValueController controller;
	@MockBean AerospikeRecordService recordService;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws JsonProcessingException {
		dummyBins = new HashMap<String, Object>();
		dummyBins.put("bin", "a");
		msgpackBins = mpMapper.writeValueAsBytes(dummyBins);
		queryParams = new HashMap<String, String>();
		Mockito.doThrow(expectedException)
		.when(recordService)
		.storeRecord(anyString(), any(), anyString(), any(dummyBins.getClass()), any(), any());
	}


	/* Create/Post */
	@Test(expected=AerospikeException.class)
	public void testCreateNSSetKey() {
		controller.createRecordNamespaceSetKey(ns, set, key, dummyBins, queryParams);
	}

	@Test(expected=AerospikeException.class)

	public void testCreateNSKey() {
		controller.createRecordNamespaceKey(ns, key, dummyBins, queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testCreateNSSetKeyMP() {
		controller.createRecordNamespaceSetKeyMP(ns, set, key, new ByteArrayInputStream(msgpackBins), queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testCreateNSKeyMP() {
		controller.createRecordNamespaceKeyMP(ns, key, new ByteArrayInputStream(msgpackBins), queryParams);
	}

	/* Update/Patch */
	@Test(expected=AerospikeException.class)
	public void testUpdateNSSetKey() {
		controller.updateRecordNamespaceSetKey(ns, set, key, dummyBins, queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testUpdateNSKey() {
		controller.updateRecordNamespaceKey(ns, key, dummyBins, queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testUpdateNSSetKeyMP() {
		controller.updateRecordNamespaceSetKeyMP(ns, set, key, new ByteArrayInputStream(msgpackBins), queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testUpdateNSKeyMP() {
		controller.updateRecordNamespaceKeyMP(ns, key, new ByteArrayInputStream(msgpackBins), queryParams);
	}

	/* Replace/Put */
	@Test(expected=AerospikeException.class)
	public void testReplaceNSSetKey() {
		controller.replaceRecordNamespaceSetKey(ns, set, key, dummyBins, queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testReplaceNSKey() {
		controller.replaceRecordNamespaceKey(ns, key, dummyBins, queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testReplaceNSSetKeyMP() {
		controller.replaceRecordNamespaceSetKeyMP(ns, set, key, new ByteArrayInputStream(msgpackBins), queryParams);
	}

	@Test(expected=AerospikeException.class)
	public void testReplaceNSKeyMP() {
		controller.replaceRecordNamespaceKeyMP(ns, key, new ByteArrayInputStream(msgpackBins), queryParams);
	}

}
