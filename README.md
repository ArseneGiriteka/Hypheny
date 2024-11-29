# Hypheny (Front End)

This repo contains android code of a messaging app wrote in Kotlin,<br>
its layouts were made with XML, its requests were handled with OkHttp3.<br>
Results are recieved a json String from a flask API.<br>
This app also handle user sessions login/logout with a Json Web Token<br>
got when user logged in successfuly.

## Architecture

### User Experience Architecture
This app run on 4 activities:
- A welcome activity
- A user login activity
- A user register activity
- A user logged activity

Every activity is a simple screen that will show the same layouts on runtime<br>
except "A user logged activity" which has many fragments, bottom sheets,<br>
pop-ups that may appear based on what the user asked.
