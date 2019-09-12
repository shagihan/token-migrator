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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.tokenmigrator.Entities.DBConfigs;
import org.wso2.tokenmigrator.Entities.MigrationConfig;
import org.wso2.tokenmigrator.Entities.MigrationConfigs;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read configuration from migration.xml file and store in an object.
 */
public class ConfigFileLoader {

    private MigrationConfigs migrationConfigs;
    private File fXmlFile = null;
    private DocumentBuilderFactory dbFactory = null;
    private DocumentBuilder dBuilder = null;
    private Document document = null;

    public ConfigFileLoader() {
        migrationConfigs = new MigrationConfigs();
        try {
            fXmlFile = new File("resources/migration.xml");
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(fXmlFile);
            setMigrationConfigs();
        } catch (ParserConfigurationException e) {
            System.out.println("Invelid XML file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.printf("Unable to read XML file");
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("Invalid XML file");
            e.printStackTrace();
        }
    }

    /**
     * Read file and retrive relevent data.
     */
    private void setMigrationConfigs() {
        // User Store Related congigs reading
        Node userstoreConfig = document.getElementsByTagName("UserStore").item(0);
        if (userstoreConfig.getNodeType() == Node.ELEMENT_NODE) {
            this.migrationConfigs.userStoreLocation = ((Element) userstoreConfig).getElementsByTagName("Location").item(0).getTextContent();
            this.migrationConfigs.userStorePassword = ((Element) userstoreConfig).getElementsByTagName("Password").item(0).getTextContent();
        }

        // Database related Configurations reading
        migrationConfigs.migrationConfigList = new ArrayList<MigrationConfig>();
        Node databaseConfig = document.getElementsByTagName("DataSource").item(0);
        if (databaseConfig.getNodeType() == Node.ELEMENT_NODE) {
            DBConfigs dbConfigs = new DBConfigs();
            dbConfigs.url = ((Element) databaseConfig).getElementsByTagName("URL").item(0).getTextContent();
            dbConfigs.username = ((Element) databaseConfig).getElementsByTagName("UserName").item(0).getTextContent();
            dbConfigs.password = ((Element) databaseConfig).getElementsByTagName("Password").item(0).getTextContent();
            dbConfigs.driverclass = ((Element) databaseConfig).getElementsByTagName("DriverClass").item(0).getTextContent();
            dbConfigs.jarlocation = ((Element) databaseConfig).getElementsByTagName("DriverJARLocation").item(0).getTextContent();
            this.migrationConfigs.dbConfig = dbConfigs;
        }

        // Migration related configurations reading
        NodeList migrationConfigsdocList = document.getElementsByTagName("MigratingDetail");
        for (int tempindex = 0; tempindex < migrationConfigsdocList.getLength(); tempindex++) {
            MigrationConfig migrationConfig = new MigrationConfig();
            Node tempNode = migrationConfigsdocList.item(tempindex);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) tempNode;
                migrationConfig.columnName = element.getElementsByTagName("columnName").item(0).getTextContent();
                migrationConfig.tableName = element.getElementsByTagName("tableName").item(0).getTextContent();
                migrationConfig.decryptionAlgorhythm = element.getElementsByTagName("decryptAlgorhythm").item(0).getTextContent();
                migrationConfig.encryptionAlgorhythm = element.getElementsByTagName("encryptAlgorhythm").item(0).getTextContent();
                migrationConfig.alias = element.getElementsByTagName("alias").item(0).getTextContent();
                migrationConfig.keyPass = element.getElementsByTagName("keyPass").item(0).getTextContent();
            }
            migrationConfigs.migrationConfigList.add(migrationConfig);
        }
    }

    /**
     * @return database related migration configs
     */
    public DBConfigs getDatabaseConfigs() {
        return this.migrationConfigs.dbConfig;
    }

    /**
     * @return migration details
     */
    public List<MigrationConfig> getMigrationConfigList() {
        return this.migrationConfigs.migrationConfigList;
    }

    /**
     * @return userstore location
     */
    public String userStoreLocation() {
        return this.migrationConfigs.userStoreLocation;
    }

    /**
     * @return userstore password
     */
    public String userStorePassword() {
        return this.migrationConfigs.userStorePassword;
    }
}
