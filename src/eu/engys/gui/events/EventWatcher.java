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

package eu.engys.gui.events;

import eu.engys.gui.events.EventManager.Condition;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.EventManager.GenericEventListener;

public class EventWatcher {

	private boolean triggered;
	private final Object lockObject;
	private Class<? extends Event> eventClass;
	private Condition condition;
	private GenericEventListener eventListener;
	private Object eventPayload;
	private EventManager eventManager;
	
	public EventWatcher(EventManager eventManager, Class<? extends Event> eventClass) {
		this(eventManager, eventClass, null);
	}

	public EventWatcher(EventManager eventManager, Class<? extends Event> eventClass, Condition condition) {
		triggered = false;
		lockObject = new Object();
		this.eventManager = eventManager;
		this.eventClass = eventClass;
		this.condition = condition;
		registerEventListener(eventClass, condition);
	}

	private synchronized void registerEventListener(Class<? extends Event> eventClass, Condition condition) {
		createEventListener();
		if (condition != null)
			EventManager.registerEventListener(eventListener, eventClass, condition);
		else
			EventManager.registerEventListener(eventListener, eventClass);
	}

	private synchronized void createEventListener() {
		eventListener = new GenericEventListener() {

			public synchronized void eventTriggered(Object sender, Event event) {
				synchronized (lockObject) {
					eventPayload = event.getPayload();
					triggered = true;
					lockObject.notifyAll();
				}
			}
		};
	}

	public synchronized void unregisterEvent() {
		eventManager.unregisterEventListener(eventListener, eventClass);
	}

	public boolean hasBeenTriggered() {
		if (triggered) {
			triggered = false;
			return true;
		}
		return false;
	}

	public boolean waitUntilTriggeredThenUnregister(long timeout) {
		boolean result = waitUntilTriggered(timeout);
		unregisterEvent();
		return result;
	}

	public boolean waitUntilTriggered(long timeout) {
		if (hasBeenTriggered())
			return true;
		try {
			synchronized (lockObject) {
				lockObject.wait(timeout);
			}
		} catch (InterruptedException e) {
		}
		return hasBeenTriggered();
	}

	public void reEnableEventWatcher() {
		triggered = false;
		registerEventListener(eventClass, condition);
	}

	public Object getEventPayload() {
		return eventPayload;
	}

	public Class<?> getEvent() {
		return eventClass;
	}

}
