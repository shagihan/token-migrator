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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DriverLoader extends URLClassLoader {

    private DriverLoader(URL[] urls) {
        super(urls);
        File driverFolder = new File("driver");
        File[] files = driverFolder.listFiles();
        for (File file : files) {
            try {
                addURL(file.toURI().toURL());
            } catch (MalformedURLException e) {
            }
        }
    }


    private static DriverLoader driverLoader;


    public static void load(String driverClassName) throws ClassNotFoundException {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException ex) {
            if (driverLoader == null) {
                URL urls[] = {};
                driverLoader = new DriverLoader(urls);
            }
            driverLoader.loadClass(driverClassName);
        }
    }
}
