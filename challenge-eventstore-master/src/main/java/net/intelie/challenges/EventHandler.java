package net.intelie.challenges;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventHandler implements EventStore {

    /*
     * The List is the simplest data structure to store Event objects.
     */
    private List<Event> events;

    public EventHandler() {
        this.events = new ArrayList<>();
    }

    /*
     * The insert() method is simple to write, since we have to just
     * add an Event element object into ArrayList.
     */
    @Override
    public void insert(Event event) {
        this.events.add(event);
    }

    /*
     * The removeAll() method reads each element of ArrayList, removing those which has
     * the requested type to be removed
     */
    @Override
    public void removeAll(String type) {
        this.events.removeIf(evt -> evt.type().equals(type));
    }

    /*
     * query() method builds a runtime EventIterator object implementing methods from
     * EventIterator interface, but first asserts if requested timestamp limits are corrects,
     * throwing an IllegalStateException and returning null if it's not.
     * See more details below.
     */
    @Override
    public EventIterator query(String type, long startTime, long endTime) {

        EventIterator eventIterator = null;

        try {

            // Timestamp limits assertion
            if (startTime > endTime)
                throw new IllegalStateException();

            eventIterator = new EventIterator() {

                private boolean exceptionFlag; // True when has to warn if moveNext() was never called or iterator has reached end
                private Event currentEvent; // Stores the current element of iterator
                private Iterator<Event> iterator; // Iterator as itself

                // This "fake" constructor has to be called once to filter events of one single type from handler to iterator
                @Override
                public final EventIterator constructor(String type, long startTime, long endTime) {
                    this.exceptionFlag = true;
                    this.currentEvent = null;

                    // Filter events with type different of requested type
                    this.iterator = events.stream()
                            .filter(evt ->
                                    evt.type().equals(type) &&
                                            evt.timestamp() >= startTime &&
                                            evt.timestamp() < endTime)
                            .iterator();

                    return this;
                }

                /*
                 * This method changes exceptionFlag value when it has to be,
                 * also iterates along the events from query, updating the
                 * currentEvent value
                 */
                @Override
                public boolean moveNext() {
                    this.exceptionFlag = !iterator.hasNext();

                    if (!exceptionFlag)
                        this.currentEvent = iterator.next();

                    return (!this.exceptionFlag);
                }

                /*
                 * Returns the currentEvent, throwing an exception and
                 * returning null if exceptionFlag is true
                 */
                @Override
                public Event current() {
                    Event curr = null;

                    try {
                        if (exceptionFlag)
                            throw new IllegalStateException();
                        else
                            curr = this.currentEvent;
                    } catch (IllegalStateException ex) {
                        System.out.println("IllegalStateException - current() returned null from EventIterator");
                    }

                    return curr;
                }

                /*
                 * Removes directly the current object from EventHandler's ArrayList,
                 * or throws exception if exceptionFlag is true
                 */
                @Override
                public void remove() {
                    try {
                        if (exceptionFlag)
                            throw new IllegalStateException();
                        else
                            events.remove(this.currentEvent);
                    } catch (IllegalStateException ex) {
                        System.out.println("IllegalStateException - remove() from EventIterator");
                    }
                }

                @Override
                public void close() throws Exception {
                }
            };

            // Calls "fake constructor" to filter iterator before returns it
            eventIterator.constructor(type, startTime, endTime);

        } catch (IllegalStateException ex) {

            // Sets null to iterator warning the exception
            eventIterator = null;
            System.out.println("IllegalStateException - @param endTime must to be greater than @param startTime - returned null");

        } finally {

            return eventIterator;

        }
    }

    // Methods to support coding time and assertion tests

    @Override
    public String toString() {
        String items = "Event Handler\tN.Events: " + this.length() + "\n";

        for (Event evt : this.events) {
            items += evt.toString() + "\n";
        }

        return items;
    }

    public int length() {
        return this.events.size();
    }

}
