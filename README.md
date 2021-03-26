## token-migrator

This tool can be use to decrypt encrypted values and re encrypt with some other algorithm from the database. This 

### How to build ?

- Make changes to the *migration.xml* file appropreately (in src/resources folder).
- Checkout the source and execute following command from the project home folder.

    ```mvn clean install```

### How to run

- use the following command to run the tool.
  ```java -jar <created jqr file in target>```

### migration.xml

```
<MigrationConfig>
    <UserStore>
        <Location>/home/shana/Tickets/SCANIASUB-36/tooltest/wum/wso2am-2.1.0/repository/resources/security/wso2carbon.jks</Location>
        <Password>wso2carbon</Password>
    </UserStore>
    <DataSource>
        <URL>jdbc:mysql://localhost:3306/wumtest?useSSL=true</URL>
        <UserName>root</UserName>
        <Password>1234</Password>
        <DriverClass>com.mysql.jdbc.Driver</DriverClass>
        <DriverJARLocation>/home/shana/Downloads/old shit/mysql-connector-java-5.1.44/mysql-connector-java-5.1.44-bin.jar</DriverJARLocation>
    </DataSource>
    <MigratingDetails>
        <MigratingDetail>
            <tableName>IDN_OAUTH2_ACCESS_TOKEN</tableName>
            <columnName>ACCESS_TOKEN</columnName>
            <decryptAlgorhythm></decryptAlgorhythm>
            <encryptAlgorhythm></encryptAlgorhythm>
        </MigratingDetail>
    </MigratingDetails>
</MigrationConfig>
```

### Sample result

```
Connecting To Database
Connected To Database
Key migration started for table IDN_OAUTH2_ACCESS_TOKEN WHERE TOKEN_STATE='ACTIVE' Column ACCESS_TOKEN
Mismatched token Hash found
OLD: {"hash":"68df1863aabe2d638b0c9abc630ba442b343d888eac610a270a2dc2d3e991855","algorithm":"SHA-256"}
NEW: {"hash":"67c4ae290ae2133569c30cfbd7bc710df3c15889592ed534126c9314f88e455b","algorithm":"SHA-256"}
 ----Query---1 : UPDATE IDN_OAUTH2_ACCESS_TOKEN set ACCESS_TOKEN_HASH ='{"hash":"67c4ae290ae2133569c30cfbd7bc710df3c15889592ed534126c9314f88e455b","algorithm":"SHA-256"}' where TOKEN_ID='077c9d74-8854-4f86-b1fd-d529efbe91d2'



Mismatched refresh token Hash found
OLD: {"hash":"1bc815c7ea08d104b616095b1f0796f96721d100285437d9526f7a3bae1f84ce","algorithm":"SHA-256"}
NEW: {"hash":"a47d2e1da0d764d1c2d518a603ee6c643921c2c3b87f57fccb5fbba9b01cc86b","algorithm":"SHA-256"}
 ----Query---2 : UPDATE IDN_OAUTH2_ACCESS_TOKEN set REFRESH_TOKEN_HASH ='{"hash":"a47d2e1da0d764d1c2d518a603ee6c643921c2c3b87f57fccb5fbba9b01cc86b","algorithm":"SHA-256"}' where TOKEN_ID='077c9d74-8854-4f86-b1fd-d529efbe91d2'



Mismatched token Hash found
OLD: {"hash":"e1c05786136941b31e04d947ee10e038fa0eb032be6cb0d0a41f265a6f1b6bbb","algorithm":"SHA-256"}
NEW: {"hash":"938945d9641ab2cad8488ef343dd82bbe2e9341332bdc69821392a49701ca9f1","algorithm":"SHA-256"}
 ----Query---1 : UPDATE IDN_OAUTH2_ACCESS_TOKEN set ACCESS_TOKEN_HASH ='{"hash":"938945d9641ab2cad8488ef343dd82bbe2e9341332bdc69821392a49701ca9f1","algorithm":"SHA-256"}' where TOKEN_ID='0af18457-5499-40d9-b90a-3d07422a2d11'



Mismatched refresh token Hash found
OLD: {"hash":"888aca065da081df6ca3decf509611f7fcb546e61fc1ef40539496a66baf03b1","algorithm":"SHA-256"}
NEW: {"hash":"b23e898422db623976be749ea9c2b4a2a273491b1a8b62d910ae9293b033b249","algorithm":"SHA-256"}
 ----Query---2 : UPDATE IDN_OAUTH2_ACCESS_TOKEN set REFRESH_TOKEN_HASH ='{"hash":"b23e898422db623976be749ea9c2b4a2a273491b1a8b62d910ae9293b033b249","algorithm":"SHA-256"}' where TOKEN_ID='0af18457-5499-40d9-b90a-3d07422a2d11'

```
