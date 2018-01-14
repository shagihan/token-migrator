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

package org.wso2.tokenmigrator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wso2.tokenmigrator.Entities.DBConfigs;
import org.wso2.tokenmigrator.Entities.MigrationConfig;

import java.security.Security;
import java.sql.ResultSet;
import org.apache.axiom.om.util.Base64;
import java.util.List;

/**
 * Token migrator class.
 */
public class TokenMigtrator {

    /**
     * Manin methord.
     * @param args command line arguments.
     * @throws Exception When configs are wrong.
     */
    public static void main(String[] args) throws Exception {
        //Security.insertProviderAt(new BouncyCastleProvider(), 1);
        Security.addProvider(new BouncyCastleProvider());
        ConfigFileLoader configFileLoader = new ConfigFileLoader();
        List<MigrationConfig> migrationConfigsList = configFileLoader.getMigrationConfigList();
        DBConfigs databaseConfigs = configFileLoader.getDatabaseConfigs();
        String userStoreLocation = configFileLoader.userStoreLocation();
        String userStorePassword = configFileLoader.userStorePassword();
        DBUtils dbUtils = new DBUtils(databaseConfigs);
        EncryptDecryptUtils encryptDecryptUtils = new EncryptDecryptUtils(userStoreLocation,userStorePassword);
        String tempToken;
        String tempDecodedToken;
        byte[] tempEcodedToken;
        int count = 0;
        for (MigrationConfig config:migrationConfigsList) {
            ResultSet listOfTokens = dbUtils.getTokensInDatabase(config.tableName, config.columnName);
            System.out.println("Key migration started for table " + config.tableName + " Column " + config.columnName);
            while (listOfTokens.next()) {
                tempToken = listOfTokens.getString(config.columnName);
                tempDecodedToken = encryptDecryptUtils.decrypt(Base64.decode(tempToken), config.decryptionAlgorhythm);
                tempEcodedToken = encryptDecryptUtils.encrypt(tempDecodedToken, config.encryptionAlgorhythm);
                dbUtils.updateTable(config.tableName, config.columnName, Base64.encode(tempEcodedToken), tempToken);
                count++;
            }
            System.out.println("Key migration finisged for table " + config.tableName + " Column " + config.columnName);
            System.out.printf("Total of " + count + " entries migrated");
        }
        dbUtils.closeConnection();
    }
}
