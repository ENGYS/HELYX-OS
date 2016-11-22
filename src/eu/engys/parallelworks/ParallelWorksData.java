/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/
package eu.engys.parallelworks;

import eu.engys.util.PrefUtil;

public class ParallelWorksData {

    public static final String PW_SITE = "https://eval.parallel.works";

    public static final String CASE_ENV_NAME = "case.env";
    public static final String PROJECT_JOB_FILE_NAME = "cloud.job";
    public static final String PROJECT_URL_FILE_NAME = "cloud.out";
    public static final String PROJECT_ARCHIVE_FILE_NAME = "cloud.tar.gz";
    public static final String INPUTS_ARCHIVE_FILE_NAME = "inputs.tgz";

    public static final String LOCALHOST = "Localhost";
    public static final String PARALLEL_WORKS = "Parallel Works";

    public static final String ENTER_EXECUTABLE_HERE = "Enter executable here";
    public static final String ENTER_API_KEY_HERE = "Enter API key here";
    public static final String DEFAULT_WORKSPACE = "helyxos_workspace";
    public static final String DEFAULT_WORKFLOW = "helyxos_runner";

    private String type;
    private String key;
    private String workflow;
    private String workspace;
    private boolean pullResults;

    public static ParallelWorksData fromPrefences() {
        String type = PrefUtil.getString(PrefUtil.PW_DRIVER, LOCALHOST);
        String key = PrefUtil.getString(PrefUtil.PW_APIKEY, ENTER_API_KEY_HERE);
        String workspace = PrefUtil.getString(PrefUtil.PW_WORKSPACE, DEFAULT_WORKSPACE);
        String workflow = PrefUtil.getString(PrefUtil.PW_WORKFLOW, DEFAULT_WORKFLOW);
        boolean pullResults = PrefUtil.getBoolean(PrefUtil.PW_PULL_RESULTS, true);

        ParallelWorksData data = new ParallelWorksData();
        data.setType(type);
        data.setKey(key);
        data.setWorkspace(workspace);
        data.setWorkflow(workflow);
        data.setPullResults(pullResults);
        return data;
    }

    public static void toPreferences(ParallelWorksData data) {
        PrefUtil.putString(PrefUtil.PW_DRIVER, data.getType());
        PrefUtil.putString(PrefUtil.PW_APIKEY, data.getKey());
        PrefUtil.putString(PrefUtil.PW_WORKSPACE, data.getWorkspace());
        PrefUtil.putString(PrefUtil.PW_WORKFLOW, data.getWorkflow());
        PrefUtil.putBoolean(PrefUtil.PW_PULL_RESULTS, data.isPullResults());
    }

    public boolean isLocalhost() {
        return type.equals(LOCALHOST);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
    
    public String getWorkflow() {
        return workflow;
    }
    
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public boolean isPullResults() {
        return pullResults;
    }

    public void setPullResults(boolean pullResults) {
        this.pullResults = pullResults;
    }

}
