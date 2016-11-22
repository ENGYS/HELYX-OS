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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventManager {

    public interface Condition {
        public abstract boolean matches(Object obj, Event event, Object obj1);
    }

    public interface Event extends Serializable {
        public abstract Object getPayload();
    }

    public interface GenericEventListener extends EventListener {
        public abstract void eventTriggered(Object obj, Event event);
    }

    public interface EventExceptionHandler {
        public abstract void handleException(Exception exception);
    }

    public interface EventManagerExtension {
        public abstract void afterTriggerEvent(Object obj, Event event, Object obj1);

        public abstract void afterRegisterEventListener(GenericEventListener genericeventlistener, Class<?> class1, Condition condition, Map<?, ?> map);
    }

    private EventManagerExtension eventManagerExtension;
    private ExecutorService scheduler;
    private Timer timer;
    private EventExceptionHandler exceptionHandler;
    protected Map<String, Map<Integer, EventSubscription>> eventSubscriptionLists;
    private Map<Object, List<EventSubscription>> contextSubscriptionsMap;

    private static EventManager eventManager = new EventManager();

    private static boolean asynchronous = true;

    private EventManager() {
        scheduler = Executors.newCachedThreadPool();
        timer = new Timer();
        exceptionHandler = new EventExceptionHandler() {

            @Override
            public void handleException(Exception ex) {
                ex.printStackTrace(System.err);
            }
        };
        eventSubscriptionLists = new HashMap<String, Map<Integer, EventSubscription>>();
        contextSubscriptionsMap = new HashMap<Object, List<EventSubscription>>();
    }

    public static void registerEventListener(GenericEventListener receiver, Class<? extends Event> eventClass) {
        eventManager.registerEventListener(null, receiver, eventClass, null);
    }

    public static void registerEventListener(Object context, GenericEventListener receiver, Class<? extends Event> eventClass) {
        eventManager.registerEventListener(context, receiver, eventClass, null);
    }

    public static void unregisterAllEventSubscriptions() {
        eventManager.eventSubscriptionLists = new HashMap<String, Map<Integer, EventSubscription>>();
    }

    public static void unregisterEventSubscriptions(Class<? extends Event> eventClass) {
        eventManager.eventSubscriptionLists.remove(eventClass.getName());
    }

    public static void registerEventListener(GenericEventListener receiver, Class<? extends Event> eventClass, Condition condition) {
        eventManager.registerEventListener(null, receiver, eventClass, condition);
    }

    private synchronized void registerEventListener(Object context, GenericEventListener receiver, Class<? extends Event> eventClass, Condition condition) {
        Map<Integer, EventSubscription> subcriptionList = eventSubscriptionLists.get(eventClass.getName());
        if (subcriptionList == null) {
            subcriptionList = new HashMap<Integer, EventSubscription>();
            eventSubscriptionLists.put(eventClass.getName(), subcriptionList);
        }
        EventSubscription subscription = new EventSubscription(receiver, eventClass, condition);
        if (!subcriptionList.containsKey(Integer.valueOf(subscription.hashCode())))
            subcriptionList.put(Integer.valueOf(subscription.hashCode()), subscription);
        manageContext(context, subscription);
        if (eventManagerExtension != null)
            eventManagerExtension.afterRegisterEventListener(receiver, eventClass, condition, eventSubscriptionLists);
    }

    private void manageContext(Object context, EventSubscription subscription) {
        if (context != null) {
            List<EventSubscription> subscriptionsAssociatedWithContext = contextSubscriptionsMap.get(context);
            if (subscriptionsAssociatedWithContext == null) {
                subscriptionsAssociatedWithContext = new ArrayList<EventSubscription>();
                contextSubscriptionsMap.put(context, subscriptionsAssociatedWithContext);
            }
            subscriptionsAssociatedWithContext.add(subscription);
        }
    }

    public synchronized void unregisterEventListener(GenericEventListener receiver, Class<? extends Event> eventClass) {
        EventSubscription tempSubscription = new EventSubscription(receiver, eventClass, null);
        Map<Integer, EventSubscription> subcriptionList = eventSubscriptionLists.get(eventClass.getName());
        if (subcriptionList.containsKey(Integer.valueOf(tempSubscription.hashCode())))
            subcriptionList.remove(Integer.valueOf(tempSubscription.hashCode()));
    }

    public synchronized void unregisterAllEventListenersForContext(Object context) {
        List<EventSubscription> subscriptionsAssociatedWithContext = contextSubscriptionsMap.get(context);
        for (EventSubscription eventSubscription : subscriptionsAssociatedWithContext) {
            unregisterEventListener(eventSubscription.getReceiver(), eventSubscription.getEventClass());
        }

    }

    public static boolean waitUntilTriggered(Class<? extends Event> eventClass) {
        return eventManager.waitUntilTriggered(eventClass, 1000);
    }

    public boolean waitUntilTriggered(Class<? extends Event> eventClass, long timeout) {
        EventWatcher eventWatcher = new EventWatcher(this, eventClass);
        return eventWatcher.waitUntilTriggeredThenUnregister(timeout);
    }

    public boolean waitUntilTriggered(Class<? extends Event> eventClass, long timeout, Condition condition) {
        EventWatcher eventWatcher = new EventWatcher(this, eventClass, condition);
        return eventWatcher.waitUntilTriggeredThenUnregister(timeout);
    }

    public static void triggerEvent(Object sender, Event event) {
        eventManager.triggerEvent(sender, event, null);
    }

    public synchronized void triggerEvent(Object sender, Event event, Object conditionalExpression) {
        Map<Integer, EventSubscription> subscriptionList = eventSubscriptionListsForClass(event.getClass());
        if (subscriptionList != null && !subscriptionList.isEmpty()) {
            for (EventSubscription eventSubscription : subscriptionList.values()) {
                if (isASubscriptionForThatEvent(event, eventSubscription) && satisfyCondition(sender, event, conditionalExpression, eventSubscription)){
                    if(asynchronous){
                        invokeHandlerMethodAsynchronously(sender, event, eventSubscription.getReceiver());
                    } else {
                        invokeHandlerMethodSynchronously(sender, event, eventSubscription.getReceiver());
                    }
                }
            }
        }
        if (eventManagerExtension != null)
            eventManagerExtension.afterTriggerEvent(sender, event, conditionalExpression);
    }

    private boolean satisfyCondition(Object sender, Event event, Object conditionalExpression, EventSubscription eventSubscription) {
        return eventSubscription.getCondition() == null || conditionalExpression != null && eventSubscription.getCondition().matches(sender, event, conditionalExpression);
    }

    private boolean isASubscriptionForThatEvent(Event event, EventSubscription eventSubscription) {
        return eventSubscription.getEventClass().isAssignableFrom(event.getClass());
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, EventSubscription> eventSubscriptionListsForClass(Class<? extends Event> klass) {
        String className = klass.getName();
        // System.out.println("className: "+className);
        if (eventSubscriptionLists.containsKey(className))
            return eventSubscriptionLists.get(className);
        for (Class<?> interfaceClass : klass.getInterfaces()) {
            String interfaceClassName = interfaceClass.getName();
            // System.out.println("interfaceClassName: "+interfaceClassName);
            if (eventSubscriptionLists.containsKey(interfaceClassName))
                return eventSubscriptionLists.get(interfaceClassName);
        }
        if (klass.getSuperclass() != null) {
            return eventSubscriptionListsForClass((Class<? extends Event>) klass.getSuperclass());
        }
        return null;
    }

    public synchronized void triggerFutureEvent(Object sender, Event event, long delay, TimeUnit timeUnit) {
        triggerFutureEvent(sender, event, null, delay, timeUnit);
    }

    public synchronized void triggerFutureEvent(final Object sender, final Event event, final Object conditionalExpression, long delay, TimeUnit timeUnit) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                triggerEvent(sender, event, conditionalExpression);
            }

        }, TimeUnit.MILLISECONDS.convert(delay, timeUnit));
    }

    public synchronized void triggerPeriodicEvent(Object sender, Event event, long initialDelay, long delay, TimeUnit timeUnit) {
        triggerPeriodicEvent(sender, event, null, initialDelay, delay, timeUnit);
    }

    public synchronized void triggerPeriodicEvent(final Object sender, final Event event, final Object conditionalExpression, long initialDelay, long delay, TimeUnit timeUnit) {
        long delayInMs = TimeUnit.MILLISECONDS.convert(initialDelay, timeUnit);
        long periodInMs = TimeUnit.MILLISECONDS.convert(delay, timeUnit);
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                triggerEvent(sender, event, conditionalExpression);
            }

        }, delayInMs, periodInMs);
    }

    private void invokeHandlerMethodAsynchronously(final Object sender, final Event event, final GenericEventListener receiver) {
        scheduler.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    receiver.eventTriggered(sender, event);
                } catch (Exception ex) {
                    exceptionHandler.handleException(ex);
                }
            }
        });
    }

    private void invokeHandlerMethodSynchronously(final Object sender, final Event event, final GenericEventListener receiver) {
        try {
            receiver.eventTriggered(sender, event);
        } catch (Exception ex) {
            exceptionHandler.handleException(ex);
        }
    }

    public static void setAsynchronous(boolean asynchronous) {
        EventManager.asynchronous = asynchronous;
    }

    public EventManagerExtension getEventManagerExtension() {
        return eventManagerExtension;
    }

    public static void setEventManagerExtension(EventManagerExtension eventManagerExtension) {
        eventManager.eventManagerExtension = eventManagerExtension;
    }

    public void setEventExceptionHandler(EventExceptionHandler eventExceptionHandler) {
        exceptionHandler = eventExceptionHandler;
    }

    public EventExceptionHandler getEventExceptionHandler() {
        return exceptionHandler;
    }

    public void shutdown() {
        timer.cancel();
        scheduler.shutdownNow();
    }
}
