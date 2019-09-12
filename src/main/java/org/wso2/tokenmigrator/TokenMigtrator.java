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
import org.json.JSONObject;
import org.wso2.tokenmigrator.Entities.DBConfigs;
import org.wso2.tokenmigrator.Entities.MigrationConfig;

import java.security.Security;
import java.sql.ResultSet;
import java.io.FileWriter;

import org.apache.axiom.om.util.Base64;

import java.util.List;

/**
 * Token migrator class.
 */
public class TokenMigtrator {

    /**
     * Manin methord.
     *
     * @param args command line arguments.
     * @throws Exception When configs are wrong.
     */
    public static void main(String[] args) throws Exception {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        Security.addProvider(new BouncyCastleProvider());
        ConfigFileLoader configFileLoader = new ConfigFileLoader();
        List<MigrationConfig> migrationConfigsList = configFileLoader.getMigrationConfigList();
        DBConfigs databaseConfigs = configFileLoader.getDatabaseConfigs();
        String userStoreLocation = configFileLoader.userStoreLocation();
        String userStorePassword = configFileLoader.userStorePassword();
        DBUtils dbUtils = new DBUtils(databaseConfigs);
        EncryptDecryptUtils encryptDecryptUtils = new EncryptDecryptUtils(userStoreLocation, userStorePassword);
        String tempToken;
        String tempDecodedToken;
        FileWriter fw=new FileWriter("./testout.txt");
        byte[] tempEcodedToken;
        int count = 0;
        for (MigrationConfig config : migrationConfigsList) {
            ResultSet listOfTokens = dbUtils.getTokensInDatabase(config.tableName, config.columnName);
            System.out.println("Key migration started for table " + config.tableName + " Column " + config.columnName);
            while (listOfTokens.next()) {
                tempToken = listOfTokens.getString(config.columnName);
                String tempToken2 = listOfTokens.getString("REFRESH_TOKEN");
                tempDecodedToken = encryptDecryptUtils.decrypt(Base64.decode(tempToken), config.decryptionAlgorhythm, config.alias, config.keyPass);
                tempToken2 = encryptDecryptUtils.decrypt(Base64.decode(tempToken2), config.decryptionAlgorhythm, config.alias, config.keyPass);
                StringBuilder sb = new StringBuilder();
                String oldTHash = listOfTokens.getString("ACCESS_TOKEN_HASH");
                String oldRTHash = listOfTokens.getString("REFRESH_TOKEN_HASH");
               sb.append("------------------------------------------");
                sb.append("-------ACCESS_TOKEN_HASH : "+ oldTHash+"\n");
                sb.append("-------REFRESH_TOKEN_HASH : "+ oldRTHash + "\n");
                sb.append("-------GRANT_TYPE : "+ listOfTokens.getString("GRANT_TYPE")+ "\n");
                sb.append("-------TOKEN_STATE : "+ listOfTokens.getString("TOKEN_STATE")+ "\n");
                sb.append("-------New ACCESS_TOKEN_HASH : "+ HashGenerator.getHash(tempDecodedToken)+ "\n");
                sb.append("-------New REFRESH_TOKEN_HASH : "+ HashGenerator.getHash(tempToken2)+ "\n" );
                sb.append("-------TOKEN_ID : "+ listOfTokens.getString("TOKEN_ID")+ "\n");
                sb.append("-------CONSUMER_KEY_ID : "+ listOfTokens.getString("CONSUMER_KEY_ID")+ "\n");
                sb.append("-------AUTHZ_USER : "+ listOfTokens.getString("AUTHZ_USER")+ "\n");
                sb.append("-------TENANT_ID : "+ listOfTokens.getString("TENANT_ID")+ "\n");
                sb.append("-------USER_DOMAIN : "+ listOfTokens.getString("USER_DOMAIN")+ "\n");
                sb.append("-------USER_TYPE : "+ listOfTokens.getString("USER_TYPE")+ "\n");
                sb.append("-------TIME_CREATED : "+ listOfTokens.getString("TIME_CREATED")+ "\n");
                sb.append("-------REFRESH_TOKEN_TIME_CREATED : "+ listOfTokens.getString("REFRESH_TOKEN_TIME_CREATED")+ "\n");
                sb.append("-------VALIDITY_PERIOD : "+ listOfTokens.getString("VALIDITY_PERIOD")+ "\n");
                sb.append("-------REFRESH_TOKEN_VALIDITY_PERIOD : "+ listOfTokens.getString("REFRESH_TOKEN_VALIDITY_PERIOD")+ "\n");
                sb.append("-------TOKEN_SCOPE_HASH : "+ listOfTokens.getString("TOKEN_SCOPE_HASH")+ "\n");
                sb.append("-------SUBJECT_IDENTIFIER : "+ listOfTokens.getString("SUBJECT_IDENTIFIER")+ "\n");
                JSONObject tokenHashJson = new JSONObject(oldTHash);
                JSONObject tokenrHashJson = new JSONObject(oldRTHash);
                String newHash = HashGenerator.getHash(tempDecodedToken);
                String newrHash = HashGenerator.getHash(tempToken2);
                JSONObject newTokenHashJson = new JSONObject(newHash);
                JSONObject newTokenrHashJson = new JSONObject(newrHash);

                if( !tokenHashJson.get("hash").toString().trim().equals(newTokenHashJson.get("hash").toString().trim())) {
                    System.out.println("Mismatched token Hash found");
                    System.out.println("OLD: " + oldTHash);
                    System.out.println("NEW: " + newHash);
                    System.out.println(" ----Query---1 : " + "UPDATE IDN_OAUTH2_ACCESS_TOKEN set ACCESS_TOKEN_HASH ='" + HashGenerator.getHash(tempDecodedToken) + "' where TOKEN_ID='" + listOfTokens.getString("TOKEN_ID") + "'");
                    System.out.println("\n\n");
                    sb.append("------------Mismatched token Hash found-----------------");
                }
                if( !tokenrHashJson.get("hash").toString().trim().equals(newTokenrHashJson.get("hash").toString().trim())) {
                    System.out.println("Mismatched refresh token Hash found");
                    System.out.println("OLD: " + oldRTHash);
                    System.out.println("NEW: " + newrHash);
                    System.out.println(" ----Query---2 : " + "UPDATE IDN_OAUTH2_ACCESS_TOKEN set REFRESH_TOKEN_HASH ='" + HashGenerator.getHash(tempToken2) + "' where TOKEN_ID='" + listOfTokens.getString("TOKEN_ID") + "'");
                    System.out.println("\n\n");
                    sb.append("------------Mismatched refresh token Hash found-----------------");
                }
                sb.append("------------------------------------------");
                fw.append(sb.toString());
                count++;
            }
            System.out.println("Number of tokens checked : " + count);
        }
        dbUtils.closeConnection();
        fw.close();
    }

}
