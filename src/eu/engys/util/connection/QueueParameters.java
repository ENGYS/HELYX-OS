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
package eu.engys.util.connection;

import java.io.Serializable;

public class QueueParameters implements Serializable{

    public static final String QUEUE_NODES = "numberOfNodes";
	public static final String QUEUE_CPUS = "cpuPerNode";
	public static final String QUEUE_TIMEOUT = "timeout";
	public static final String QUEUE_FEATURE = "feature";
	public static final String QUEUE_NAMES = "nodeNames";
	
	private int numberOfNodes = 1;
	private String nodeNames = "";
	private int cpuPerNode = 1;
	private int timeout = 500;
	private String feature = "";
	
	private String submit = "qsub";
	private String delete = "qdel";
	private String statistic = "qstat";
	private String nodes = "qnodes";
	
	public int getNumberOfNodes() {
		return numberOfNodes;
	}
	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}
	public int getCpuPerNode() {
		return cpuPerNode;
	}
	public void setCpuPerNode(int cpuPerNode) {
		this.cpuPerNode = cpuPerNode;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
    public String getFeature() {
        return feature;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
    public void setNodeNames(String nodeNames) {
        this.nodeNames = nodeNames;
    }
    public String getNodeNames() {
        return nodeNames;
    }
	
	@Override
	public String toString() {
	    return "Queue Parameters [ Number Of Nodes: " + numberOfNodes + " - Cpu Per Node: " + cpuPerNode + " - Feature: " + feature + " - Timeout: " + timeout + " ]";
	}
	
	
}