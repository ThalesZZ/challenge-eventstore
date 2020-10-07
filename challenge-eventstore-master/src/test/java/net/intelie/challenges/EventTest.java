package net.intelie.challenges;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {

    /*
     * Generates N random events then asserts if all events were inserted/stored into EventHandler
     */
    @Test
    public void eventStoreInsert(){
        EventHandler handler = new EventHandler();
        final int nEvents = 100;

        for(Event evt : Auxiliary.generateNEvents(nEvents))
            handler.insert(evt);

        assertEquals(nEvents, handler.length());
    }

    /*
     * Inserts 70 events type_0 and 30 events type_1, remove all events type_1
     * then asserts if there are 70 events handled by handler
     */
    @Test
    public void eventStoreRemoveAll(){
        EventHandler handler = new EventHandler();
        final int nEvents = 100;
        final int nRemoves = 30;

        for(int i = 0; i < nEvents - nRemoves; i++){
            Event evt = new Event(Auxiliary.eventTypes[0], Auxiliary.randomTimestamp());
            handler.insert(evt);
        }

        for(int i = 0; i < nRemoves; i++){
            Event evt = new Event(Auxiliary.eventTypes[1], Auxiliary.randomTimestamp());
            handler.insert(evt);
        }

        handler.removeAll(Auxiliary.eventTypes[1]);

        assertEquals(nEvents - nRemoves, handler.length());
    }

    /*
     * Query requests an iterator for handler then asserts moveNext() method behavior
     */
    @Test
    public void eventIteratorMoveNext(){
        final int nEvents = 100;
        EventHandler handler = new EventHandler();
        for(Event evt : Auxiliary.generateNEvents(nEvents))
            handler.insert(evt);

        // Event type and timestamp limits for query
        final String type = Auxiliary.eventTypes[0];
        final long startTime = 0;
        final long endTime = Auxiliary.randomTimestamp();

        // Query between timestamp 0 and a random timestamp
        EventIterator iterator = handler.query(type, startTime, endTime);

        System.out.println("The following warning exception is from eventIteratorMoveNext() test " +
                "when iterator tries to call his current() but moveNext() wasn't called yet. " +
                "It's part of the test and may be not considered as an error or failure.");
        // Asserts if current is null, since moveNext() wasn't called before
        assertNull(iterator.current());

        // Iterate events
        while(iterator.moveNext()){
            Event currEvt = iterator.current();

            // Asserts if current event isn't null anymore while moveNext() returns true
            assertNotNull(currEvt);
            // Asserts if all events from iterator have the same type
            assertEquals(currEvt.type(), type);
            // Asserts timestamp interval condition
            assertTrue(currEvt.timestamp() >= startTime);
            assertTrue(currEvt.timestamp() < endTime);
        }

        // Asserts if moveNext() returns false, once it has reached the end of iterator
        assertFalse(iterator.moveNext());
    }

    /*
     * Asserts if timestamp limits are being correctly informed, returning a null iterator otherwise
     */
    @Test
    public void eventIteratorTimestampLimits(){
        final int nEvents = 100;
        EventHandler handler = new EventHandler();
        for(Event evt : Auxiliary.generateNEvents(nEvents))
            handler.insert(evt);

        // Event type and timestamp limits for query
        final String type = Auxiliary.eventTypes[0];
        final long startTime = 0;
        final long endTime = Auxiliary.randomTimestamp();

        // Query between timestamp 0 and a random timestamp
        EventIterator iterator = handler.query(type, startTime, endTime);

        // If null, timestamp limits were incorrectly informed to iterator
        assertNotNull(iterator);

        // So the following line should fail:
        // assertNotNull( handler.query(type, endTIme, startTime) );
    }

    /*
     * Asserts if remove() method from EventIterator is correctly removing current event from it was stored
     */
    @Test
    public void eventIteratorRemove(){
        final int nEvents = 100;
        EventHandler handler = new EventHandler();
        for(Event evt : Auxiliary.generateNEvents(nEvents))
            handler.insert(evt);

        // Event type and timestamp limits for query
        final String type = Auxiliary.eventTypes[0];
        final long startTime = 0;
        final long endTime = Auxiliary.randomTimestamp();

        // Query between timestamp 0 and a random timestamp
        EventIterator iterator = handler.query(type, startTime, endTime);

        // Jumps iterator to first event
        iterator.moveNext();

        // Store current event into a variable before take it off from the handler where it's stored
        Event currEvt = iterator.current();

        // Remove it
        iterator.remove();
        // Query iterator again to reset
        iterator = handler.query(type, startTime, endTime);

        while(iterator.moveNext()){
            Event evt = iterator.current();

            // Asserts if removed event aren't stored anymore
            assertNotSame(currEvt, evt);
        }

        // Asserts iterator has reached the end
        assertFalse(iterator.moveNext());
    }

    @Test
    public void thisIsAWarning() throws Exception {
        Event event = new Event("some_type", 123L);

        //THIS IS A WARNING:
        //Some of us (not everyone) are coverage freaks.
        assertEquals(123L, event.timestamp());
        assertEquals("some_type", event.type());
    }
}