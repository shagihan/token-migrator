/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.wso2.tokenmigrator.Entities;

import java.util.List;

/**
 * Migration config entity.
 * This will generate according to the configs.xml file.
 */
public class MigrationConfigs {

    /**
     * Location of userstore.jks file.
     */
    public String userStoreLocation;

    /**
     * Password of user store.
     */
    public String userStorePassword;

    /**
     * Database configs.
     */
    public DBConfigs dbConfig;

    /**
     * List of migration configs.
     */
    public List<MigrationConfig> migrationConfigList;
}

