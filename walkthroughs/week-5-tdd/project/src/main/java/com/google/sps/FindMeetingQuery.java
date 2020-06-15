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

/** Finds options for times to schedule a meeting request */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // no options for too long of a request
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration())
    {
        Collection<TimeRange> noTimes = Arrays.asList();
        return noTimes;
    }

    Collection<String> attendees = request.getAttendees();

    // initialize options as the whole day
    Collection<TimeRange> times = new ArrayList<>();
    times.add(TimeRange.WHOLE_DAY);

    // if there are no attendees the meeting can be any time of the day 
    if(attendees.isEmpty()){
        return times; 
    }
    
    for(Event event : events){

        Collection<String> eventAttendees = event.getAttendees();
        boolean relevantMeeting = false;

        // a relevant event contains a person from the requested meeting
        for(String person : eventAttendees){
            if(attendees.contains(person)){
                relevantMeeting = true;
            }
        }

        if(relevantMeeting){

            // find options that overlap with the relevant meeting
            Collection<TimeRange> overlapTimes = new ArrayList<>();
            for(TimeRange time : times){
                if(time.overlaps(event.getWhen())){
                    overlapTimes.add(time);
                }
            }

            for(TimeRange overlapTime : overlapTimes){

                // add an option for free time before the relevant meeting
                if(overlapTime.start() < event.getWhen().start() && request.getDuration() <= (event.getWhen().start() - overlapTime.start())){
                    TimeRange beforeEvent = TimeRange.fromStartEnd(overlapTime.start(), event.getWhen().start(), false);
                    times.add(beforeEvent);
                }
                // add an option for free time after the relevant meeting
                if(overlapTime.end() > event.getWhen().end() && request.getDuration() <= overlapTime.end() - event.getWhen().end()){
                    TimeRange afterEvent = TimeRange.fromStartEnd(event.getWhen().end(), overlapTime.end(), false);
                    times.add(afterEvent);
                }

                //remove overlap time
                times.remove(overlapTime); 
            }
        }
    }

    return times;
  }
}
