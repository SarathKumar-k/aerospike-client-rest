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

import java.util.List;
import java.util.Map;

import com.aerospike.client.policy.InfoPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.restclient.domain.RestClientIndex;

public interface AerospikeSindexService {

	public List<RestClientIndex> getIndexList(String namespace, InfoPolicy policy);
	public void createIndex(RestClientIndex indexModel, Policy policy);
	public void dropIndex(String namespace, String indexName, Policy policy);
	public Map<String, String> indexStats(String namespace, String name, InfoPolicy policy);
}