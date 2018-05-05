/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.fileinstall.internal;

/**
 * -DirectoryWatcher-
 *
 * This class runs a background task that checks a directory for new files or
 * removed files. These files can be configuration files or jars.
 * For jar files, its behavior is defined below:
 * - If there are new jar files, it installs them and optionally starts them.
 *    - If it fails to install a jar, it does not try to install it again until
 *      the jar has been modified.
 *    - If it fail to start a bundle, it attempts to start it in following
 *      iterations until it succeeds or the corresponding jar is uninstalled.
 * - If some jar files have been deleted, it uninstalls them.
 * - If some jar files have been updated, it updates them.
 *    - If it fails to update a bundle, it tries to update it in following
 *      iterations until it is successful.
 * - If any bundle gets updated or uninstalled, it refreshes the framework
 *   for the changes to take effect.
 * - If it detects any new installations, uninstallations or updations,
 *   it tries to start all the managed bundle unless it has been configured
 *   to only install bundles.
 *
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class DirectoryWatcher 
{
    public final static String FILENAME = "felix.fileinstall.filename";
    public final static String POLL = "felix.fileinstall.poll";
    public final static String DIR = "felix.fileinstall.dir";
    public final static String LOG_LEVEL = "felix.fileinstall.log.level";
    public final static String LOG_DEFAULT = "felix.fileinstall.log.default";
    public final static String TMPDIR = "felix.fileinstall.tmpdir";
    public final static String FILTER = "felix.fileinstall.filter";
    public final static String START_NEW_BUNDLES = "felix.fileinstall.bundles.new.start";
    public final static String USE_START_TRANSIENT = "felix.fileinstall.bundles.startTransient";
    public final static String USE_START_ACTIVATION_POLICY = "felix.fileinstall.bundles.startActivationPolicy";
    public final static String NO_INITIAL_DELAY = "felix.fileinstall.noInitialDelay";
    public final static String DISABLE_CONFIG_SAVE = "felix.fileinstall.disableConfigSave";
    public final static String ENABLE_CONFIG_SAVE = "felix.fileinstall.enableConfigSave";
    public final static String START_LEVEL = "felix.fileinstall.start.level";
    public final static String ACTIVE_LEVEL = "felix.fileinstall.active.level";
    public final static String UPDATE_WITH_LISTENERS = "felix.fileinstall.bundles.updateWithListeners";
    public final static String OPTIONAL_SCOPE = "felix.fileinstall.optionalImportRefreshScope";
    public final static String FRAGMENT_SCOPE = "felix.fileinstall.fragmentRefreshScope";
    public final static String DISABLE_NIO2 = "felix.fileinstall.disableNio2";

    public final static String SCOPE_NONE = "none";
    public final static String SCOPE_MANAGED = "managed";
    public final static String SCOPE_ALL = "all";

    public final static String LOG_STDOUT = "stdout";
    public final static String LOG_JUL = "jul";
    
    // Remainder of this class has been removed
    
}
