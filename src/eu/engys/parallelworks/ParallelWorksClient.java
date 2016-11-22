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

import static eu.engys.parallelworks.ParallelWorksData.PW_SITE;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import eu.engys.parallelworks.json.Dataset;
import eu.engys.parallelworks.json.Histories;
import eu.engys.parallelworks.json.HistoryEntry;
import eu.engys.parallelworks.json.JobId;
import eu.engys.parallelworks.json.JobInfo;
import eu.engys.parallelworks.json.JobState;
import eu.engys.parallelworks.json.Resource;
import eu.engys.parallelworks.json.Resources;
import eu.engys.parallelworks.json.StartJobInputs;
import eu.engys.parallelworks.json.StartJobResponse;
import eu.engys.parallelworks.json.UploadDatasetResponse;
import eu.engys.util.PrefUtil;

public class ParallelWorksClient {

    /*
     * TODO: it would be nice to add some more checks/error messages
     * e.g. if you insert an invalid workspace_id a meaningless message is shown instead of something like: invalid connection parameters or, if possible, workspace id not found
     */

    private static ParallelWorksClient instance;

    private static String apiKey;
    private static Client client;
    private static WebTarget baseTarget;

    private JobId jobID;
    private String datasetID;

    private ParallelWorksClient() {
        apiKey = PrefUtil.getString(PrefUtil.PW_APIKEY);

        client = ClientBuilder.newClient();
        client.register(JacksonJsonProvider.class);
        client.register(MultiPartWriter.class);
        // client.register(new LoggingFilter());
        baseTarget = client.target(PW_SITE).path("api");
    }

    public static ParallelWorksClient getInstance() {
        if (instance == null) {
            instance = new ParallelWorksClient();
        }
        return instance;
    }

    public String getWorkspaceID(String name) {
        Histories histories = getHistories();
        for (HistoryEntry e : histories) {
            if (name.equals(e.getName())) {
                return e.getId();
            }
        }

        return null;
    }

    public void uploadDataset(String workspaceID, File archivedCase) throws IOException {
        FileDataBodyPart filePart = new FileDataBodyPart("files_0|file_data", archivedCase);
        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("key", apiKey);
        formData.field("tool_id", "upload1");
        formData.field("workspace_id", workspaceID);
        formData.bodyPart(filePart);

        Response response = baseTarget.path("tools").request().post(Entity.entity(formData, formData.getMediaType()));
        formData.close();
        this.datasetID = response.readEntity(UploadDatasetResponse.class).getOutputs().get(0).getId();
    }

    public Histories getHistories() {
        return baseTarget.path("histories").queryParam("key", apiKey).request().get(Histories.class);
    }

    public String getDatasetState() {
        if (datasetID != null) {
            return baseTarget.path("datasets").path(datasetID).queryParam("key", apiKey).request().get(Dataset.class).getState();
        }
        return null;
    }

    public boolean isPoolOn() {
        Resources res = baseTarget.path("resources").queryParam("key", apiKey).request().get(Resources.class);
        for (Resource resource : res) {
            if (resource.getStatus().equals("on")) {
                return true;
            }
        }
        return false;
    }

    public String getWorkflowName(String workflow) {
        return client.target(PW_SITE).path("workflow_name").path(workflow).queryParam("key", apiKey).request().get(String.class);
    }

    /*
     * JOBS
     */

    public void startJob(String command) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        StartJobInputs inputs = new StartJobInputs(datasetID, command);

        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("key", apiKey);
        formData.field("tool_id", getWorkflowName(PrefUtil.getString(PrefUtil.PW_WORKFLOW)));
        formData.field("workspace_id", getWorkspaceID(PrefUtil.getString(PrefUtil.PW_WORKSPACE)));
        formData.close();
        try {
            formData.field("inputs", mapper.writeValueAsString(inputs));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Response resp = baseTarget.path("tools").request().post(Entity.entity(formData, formData.getMediaType()));
        StartJobResponse sjr = resp.readEntity(StartJobResponse.class);

        this.jobID = new JobId(sjr.getJobs().get(0).getId(), sjr.getDecodedJobId());
    }

    public JobState getJobState() {
        // galaxy is sending the JSON back as text/html
        try {
            String resp = baseTarget.path("jobs").path(jobID.getId()).path("state").queryParam("key", apiKey).request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(resp, JobState.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getJobTail(int lastLine) {
        if (jobID != null) {
            return client.target(PW_SITE).path("tail_change").path(jobID.getDecodedId()).queryParam("file", "_stdout_1.txt").queryParam("line", lastLine).request().get(String.class);
        } else {
            return "No JOB";
        }
    }

    public String getDownloadUrl() {
        String resID = baseTarget.path("jobs").path(jobID.getId()).queryParam("key", apiKey).request().get(JobInfo.class).getOutput("results").getId();
        Dataset dataset = baseTarget.path("datasets").path(resID).queryParam("key", apiKey).request().get(Dataset.class);
        return PW_SITE + dataset.getDownloadUrl();
    }

    public String uploadFileToJob(File file) throws IOException {
        FileDataBodyPart filePart = new FileDataBodyPart("file", file);
        FormDataMultiPart formData = new FormDataMultiPart();
        formData.bodyPart(filePart);

        Response response = baseTarget.path("jobs").path(jobID.getId()).path("upload").queryParam("key", apiKey).request().post(Entity.entity(formData, formData.getMediaType()));
        formData.close();
        return response.readEntity(String.class);
    }

    public String cancelJob() {
        if (jobID != null) {
            return baseTarget.path("jobs").path(jobID.getId()).path("cancel").queryParam("key", apiKey).request().get(String.class);
        }
        return null;
    }

    public JobId getJobID() {
        return jobID;
    }

    public String getDatasetID() {
        return datasetID;
    }

}
