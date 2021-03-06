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
package com.aerospike.restclient.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.aerospike.client.Key;
import com.aerospike.restclient.ASTestUtils;
import com.aerospike.restclient.ASTestUtils.KeyMatcher;
import com.aerospike.restclient.handlers.RecordHandler;
import com.aerospike.restclient.util.AerospikeAPIConstants.RecordKeyType;
import com.aerospike.restclient.util.RestClientErrors;

@RunWith(MockitoJUnitRunner.class)
public class RecordServiceV1DeleteRecordTest {
	private String ns = "ns";
	private String set = "set";
	private String strKey = "pk";

	private byte[] bytesKey = new byte[] {1,2,3,4};

	@InjectMocks AerospikeRecordServiceV1 recordService;
	@Mock RecordHandler handler;

	@Before
	/*
	 * We need to return true to indicate to the controller that we found the record, otherwise
	 * a record not found exception will be raised.
	 */
	public void setupMockReturn() {
		when(handler.deleteRecord(any(), any(Key.class))).thenReturn(true);
	}
	@Test
	public void testStringKeyNoType() {
		Key expectedKey = new Key(ns, set, strKey);
		KeyMatcher matcher = new ASTestUtils.KeyMatcher(expectedKey);
		recordService.deleteRecord(ns, set, strKey, null, null);

		verify(handler, Mockito.only()).deleteRecord(any(), argThat(matcher));
	}

	@Test
	public void testStringKeyStringType() {
		Key expectedKey = new Key(ns, set, strKey);
		KeyMatcher matcher = new ASTestUtils.KeyMatcher(expectedKey);
		recordService.deleteRecord(ns, set, strKey, RecordKeyType.STRING, null);

		verify(handler, Mockito.only()).deleteRecord(any(), argThat(matcher));
	}

	@Test
	public void testIntKey() {
		Key expectedKey = new Key(ns, set, 5);
		KeyMatcher matcher = new ASTestUtils.KeyMatcher(expectedKey);
		recordService.deleteRecord(ns, set, "5", RecordKeyType.INTEGER, null);

		verify(handler, Mockito.only()).deleteRecord(any(), argThat(matcher));
	}

	@Test
	public void testBytesKey() {
		Key expectedKey = new Key(ns, set, bytesKey);
		KeyMatcher matcher = new ASTestUtils.KeyMatcher(expectedKey);
		String bytesKeyStr = Base64.getUrlEncoder().encodeToString(bytesKey);
		recordService.deleteRecord(ns, set, bytesKeyStr, RecordKeyType.BYTES, null);
		verify(handler, Mockito.only()).deleteRecord(any(), argThat(matcher));
	}

	@Test
	public void testDigestKey() {
		byte[] testDigest = new byte[20];
		Key expectedKey = new Key(ns, testDigest, null, null);

		KeyMatcher matcher = new ASTestUtils.KeyMatcher(expectedKey);
		String digestStr = Base64.getUrlEncoder().encodeToString(testDigest);

		recordService.deleteRecord(ns, null, digestStr, RecordKeyType.DIGEST, null);
		verify(handler, Mockito.only()).deleteRecord(any(), argThat(matcher));
	}

	@Test
	public void testNoSetKey() {
		Key expectedKey = new Key(ns, null, strKey);
		KeyMatcher matcher = new ASTestUtils.KeyMatcher(expectedKey);
		recordService.deleteRecord(ns, null, strKey, null, null);
		verify(handler, Mockito.only()).deleteRecord(any(), argThat(matcher));
	}

	@Test(expected=RestClientErrors.RecordNotFoundError.class)
	public void testRecordNotFound() {
		when(handler.deleteRecord(any(), any(Key.class))).thenReturn(false);
		recordService.deleteRecord(ns, null, strKey, null, null);
	}
}
