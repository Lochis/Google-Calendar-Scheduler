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
import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
    // Load client secrets
    String client_id = "";
    String project_id = "";
    String client_secret = "";
    String redirect_uris = "";

    String credentials = new String("{" +
    "\"installed\": {" +
        "\"client_id\": \"" + client_id + "\"," +
        "\"project_id\": \"" + project_id + "\"," +
        "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\"," +
        "\"token_uri\": \"https://oauth2.googleapis.com/token\"," +
        "\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\"," +
        "\"client_secret\": \"" + client_secret + "\"," +
        "\"redirect_uris\": [" +
            "\"" + redirect_uris + "\"" +
        "]" +
    "}");

    InputStream in = new ByteArrayInputStream(credentials.getBytes());
        //CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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

            //DateTime startDateTime = new DateTime(System.currentTimeMillis());
            //CreateFullDayEvent(service, "primary", "Lucas", "Full", startDateTime);

            //listEvents(service, "primary");
            tests(service, "primary", "Lucas", "Full");
    }

    // 1. Create a new event
    // Calendar, String, String, String, String, DateTime
    public static void CreateFullDayEvent(Calendar service, String calendarId, String Name, String type, DateTime startDateTime) throws IOException{
        DateTime endDateTime = new DateTime (startDateTime.getValue() + 86400000);

        Event event = new Event()
            .setSummary(Name + " (" + type + ")");
            //.setDescription("");

        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        //DateTime endDateTime = new DateTime("2024-02-13T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }

    // 2. Delete an event
    //Calendar, String, String 
    public static void deleteEvent(Calendar service, String calendarId, String eventId) throws IOException{
           service.events().delete(calendarId, eventId).execute(); 
    }

    // 3. Edit an existing event




    // 4. Return a list of calendar events based on start and end date
    // Returns: HashMap
    //Calendar, String
    public static HashMap listEvents(Calendar service, String calendarId) throws IOException{

        HashMap<String, String> eventMap = new HashMap <String, String>();

            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("updated")
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
                eventMap.put(event.getId(), event.getSummary());
                System.out.printf("%s (%s)\n", event.getId() + " - " + event.getSummary(), start);
              }
            }

        return eventMap;
    }

    public static void tests(Calendar service, String calendarId, String Name, String type) throws IOException{
        DateTime startDateTime1 = new DateTime(System.currentTimeMillis());
        DateTime startDateTime2 = new DateTime(System.currentTimeMillis()+ 86400000);
        DateTime startDateTime3 = new DateTime(System.currentTimeMillis() + (2*86400000));
        DateTime startDateTime4 = new DateTime(System.currentTimeMillis() + (3*86400000));
    
        HashMap<String, String> eMap = new HashMap<String, String>();

        // Create 4 events
        CreateFullDayEvent(service, calendarId, "Lucas", "Testing", startDateTime1);
        CreateFullDayEvent(service, calendarId, "Alex", "PM", startDateTime2);
        CreateFullDayEvent(service, calendarId, "Lucas", "Full", startDateTime3);
        CreateFullDayEvent(service, calendarId, "Alex", "Full", startDateTime4);

        // Get List Events
        eMap = listEvents(service, calendarId);
        
        //get the first event from the map of events
        String eventId = eMap.keySet().iterator().next();

        //Delete first event
        deleteEvent(service, calendarId, eventId);
    }
}

