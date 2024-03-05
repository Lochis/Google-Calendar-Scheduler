import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import java.util.Scanner;
import java.util.TimeZone;

/* class to demonstrate use of Calendar events list API */
public class CalendarQuickstart {
  /**
   * Application name.
   */
  private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  /**
   * Directory to store authorization tokens for this application.
   */
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  /**
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES =
      Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    //returns an authorized Credential object.
    return credential;
  }



    public static void main(String... args) throws IOException, GeneralSecurityException {

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
            new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

            DateTime startDateTime = new DateTime(System.currentTimeMillis());
            CreateFullDayEvent(service, "I am the name of this event", "Full", startDateTime);


        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
          System.out.println("No upcoming events found.");
        } else {
          System.out.println("Upcoming events");
          for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
              start = event.getStart().getDate();
            }
            System.out.printf("%s (%s)\n", event.getSummary(), start);
          }
        }
    }

// 1. Create a new event
// Needs: Name, Type (AM, PM or Full day), Start date, End date, Type
public static void CreateFullDayEvent(Calendar service, String Name, String type, DateTime startDateTime) throws IOException{
    DateTime endDateTime = new DateTime (startDateTime.getValue() + 86400000);
    if(type.equals("Full")){
        endDateTime = new DateTime (startDateTime.getValue() + 86400000);
    }else {

    }

    //endDate = new Date (startDate.getTime() + 86400000);

    Event event = new Event()
        .setSummary("Test")
        .setLocation("My HAWSE")
        .setDescription("Testing out this event creation");

    EventDateTime start = new EventDateTime()
        .setDateTime(startDateTime)
        .setTimeZone("America/Los_Angeles");
    event.setStart(start);

    //DateTime endDateTime = new DateTime("2024-02-13T17:00:00-07:00");
    EventDateTime end = new EventDateTime()
        .setDateTime(endDateTime)
        .setTimeZone("America/Los_Angeles");
    event.setEnd(end);

    /*String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
      event.setRecurrence(Arrays.asList(recurrence));

      EventAttendee[] attendees = new EventAttendee[] {
      new EventAttendee().setEmail("lpage@example.com"),
      new EventAttendee().setEmail("sbrin@example.com"),
      };
      event.setAttendees(Arrays.asList(attendees));

      EventReminder[] reminderOverrides = new EventReminder[] {
      new EventReminder().setMethod("email").setMinutes(24 * 60),
      new EventReminder().setMethod("popup").setMinutes(10),
      };
      Event.Reminders reminders = new Event.Reminders()
      .setUseDefault(false)
      .setOverrides(Arrays.asList(reminderOverrides));
      event.setReminders(reminders);
      */
    String calendarId = "primary";
    event = service.events().insert(calendarId, event).execute();
    System.out.printf("Event created: %s\n", event.getHtmlLink());
}
}

// 2. Delete an event
// Needs: Name, Start Date, End Date 
// If the end date that it is given is shorter than the end date than the multi-day event, delete and recreate a new event to keep the remaining dates

// 3. Edit an existing event

// 4. Return a list of calendar events based on start and end date
// Needs: Start date, End date


