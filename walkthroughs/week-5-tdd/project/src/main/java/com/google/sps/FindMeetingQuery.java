// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Comparator;

/** 
  Finds options for times to schedule a meeting request.
*/
public final class FindMeetingQuery {

  /** Removes TimeRanges from times collection that are unavailable for the given attendees */
  private ArrayList<TimeRange> removeConflictingTimes(ArrayList<TimeRange> times, Collection<String> attendees, Collection<Event> events, long duration, boolean optimizing){

    for(Event event : events){

        boolean conflictingMeeting = false;

        // a conflicting event contains a person from the requested meeting
        for(String person : event.getAttendees()){
            if(attendees.contains(person)){
                conflictingMeeting = true;
            }
        }

        if(conflictingMeeting){

            // find options that overlap with the conflicting meeting
            Collection<TimeRange> overlapTimes = new ArrayList<>();
            for(TimeRange time : times){
                if(time.overlaps(event.getWhen())){
                    overlapTimes.add(time);
                }
            }

            for(TimeRange overlapTime : overlapTimes){

                // add a meeting time to 'times' list that occurs before the conflict
                if(overlapTime.start() < event.getWhen().start() && duration <= (event.getWhen().start() - overlapTime.start())){
                    TimeRange beforeEvent = TimeRange.fromStartEnd(overlapTime.start(), event.getWhen().start(), false);
                    times.add(beforeEvent);
                }
                // add a meeting time to 'times' list that occurs after the conflict
                if(overlapTime.end() > event.getWhen().end() && duration <= overlapTime.end() - event.getWhen().end()){
                    TimeRange afterEvent = TimeRange.fromStartEnd(event.getWhen().end(), overlapTime.end(), false);
                    times.add(afterEvent);
                }

                //remove overlap time
                if(!optimizing){
                    times.remove(overlapTime);
                }
            }
        }
    }

    return times;
  }

  /** 
    Given all events that occur in the day and a meeting request with attendees
    that may or may not have conflicting events, returns a set of non-overlapping time intervals 
    of duration at least the requested meeting length, during which all attendees are available.
  
    If there are optional attendees, return times that are available for both optional and 
    mandatory attendees. If there are no such times, return the times available
    for mandatory attendees and the greatest number of optional attendees. 
  */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // no options for too long of a request
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration())
    {
        Collection<TimeRange> noTimes = Arrays.asList();
        return noTimes;
    }

    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    // initialize options as the whole day
    ArrayList<TimeRange> times = new ArrayList<>();
    times.add(TimeRange.WHOLE_DAY);

    // if there are no attendees the meeting can be any time of the day 
    if(attendees.isEmpty() && optionalAttendees.isEmpty()){
        return times; 
    }
    
    times = removeConflictingTimes(times, attendees, events, request.getDuration(), false);

    // save the times before considering optional attendees in case optional attendees aren't available
    ArrayList<TimeRange> timesBeforeOptional = new ArrayList<>();
    for(TimeRange time : times){
        timesBeforeOptional.add(time);
    }

    // optional attendees
    // repeat the same process as the manatory attendees to narrow time options to fit optional attendees
    
    times = removeConflictingTimes(times, optionalAttendees, events, request.getDuration(), false);

    // If no time exists for all optional and mandatory attendees, find the time slot(s) 
    // that allow mandatory attendees and the greatest possible number of optional attendees to attend.
    if(times.isEmpty() && !(attendees.isEmpty())){

        times = timesBeforeOptional;

        // breaks times into smaller peices that are available for different optional attendees
        times = removeConflictingTimes(times, optionalAttendees, events, request.getDuration(), true);

        int lowestConflicts = optionalAttendees.size() - 1;
        ArrayList<TimeRange> mostPeopleAvailable = new ArrayList<>();

        for(TimeRange time : times){
        
            int conflicts = 0; 

            // for each event, if it overlaps with the possible time, count the amount of optional 
            // attendees that are going to that event. The final count of conflicts will be 
            // the number of people who conflict with that time 
            for(Event event : events){
                if((event.getWhen()).overlaps(time)){
                    for(String person : event.getAttendees()){
                        if(optionalAttendees.contains(person)){
                            conflicts++;
                        }
                    }
                }
            }

            // if conflicts is lower than the current lowest, replace it with that option
            // if it is equal, add it as an option
            if(conflicts < lowestConflicts){
                mostPeopleAvailable.clear();
                mostPeopleAvailable.add(time);
            }else if(conflicts == lowestConflicts){
                mostPeopleAvailable.add(time);
            }
        }

        if(!mostPeopleAvailable.isEmpty()){
            times = mostPeopleAvailable;
        }
    }

    Collections.sort(times, TimeRange.ORDER_BY_START);

    return times;
  }
}
