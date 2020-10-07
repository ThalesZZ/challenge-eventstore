package net.intelie.challenges;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Auxiliary {

    // Some arbitrary types
    static final String[] eventTypes = {"type_0", "type_1", "type_2", "type_3", "type_4"};

    // Returns a List of random events
    static List<Event> generateNEvents(int n){
        List<Event> events = new ArrayList<>();

        for(int i = 0; i < n; i++) {
            int index = new Random().nextInt(eventTypes.length);
            String type = eventTypes[index];
            long timestamp = randomTimestamp();

            Event evt = new Event(type, timestamp);
            events.add(evt);
        }

        return events;
    }

    // Returns a random positive long number as timestamp
    static long randomTimestamp(){
        return Math.abs(new Random().nextLong());
    }

}
