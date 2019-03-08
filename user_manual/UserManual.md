# **User Manual - *Student Carpooling***

## Group Members
  + Hannah O Connor - 16382283
  + Catherine Mooney - 16416052
  
 
# **Table of contents**


# **1. User Manual**
      
 - [1.1 Installation](#11-installation)
    
 - [1.2 Register](#12-register)
    
 - [1.3 Login](#13-login)
 
 - [1.4 Reset Password](#14-reset-password)
  
 - [1.5 Sign out](#15-sign-out)
 
 - [1.6 Delete Account](#16-delete-account)
 
 - [1.7 Contact Developers](#17-contact-developers)
 
 - [1.8 Notifications](#18-notifications)
 
 - [1.9 Help](#19-help)
 
 - [1.10 Profile](#110-profile)
 
 - [1.11 Viewing Other Profiles](#111-viewing-other-profiles)
 
 - [1.12 Report User](#112-report-user)
 
 - [1.13 Messaging System](#113-messaging-system)
 
 - [1.14 Search for Active Chats](#114-search-for-active-active-chats)
 
 - [1.15 Leave Chat](#115-leave-chat)
 
 - [1.16 Rating Users](#116-rating-users)
 

 # **2. Driver Manual**

 - [2.1 Create Trips](#21-create-trips)

 - [2.2Find Requests](#22-find-requests)

 - [2.3 Starting a Trip](#23-starting-a-trips)
 
 - [2.4 Passenger Requests](#23-passenger-requests)
  
 - [2.5 Removing a Passenger](#23-removing-a-passenger)
  
 - [2.6 Starting a Trip](#23-starting-a-trip)
   
 - [2.7 Calculating a Route](#23-calculating-a-route)
 
 - [2.8 Ending a Trip](#23-ending-a-trip)
 
 - [2.9 Cancelling a Trip](#23-cancelling-a-trip)
 
 - [2.10 Deleting a Trip](#23-deleting-a-trip)
 
 - [2.11 Search for Requested Trips](#23-search-for-requested-trips)
 
 # **3. Passenger Manual**

 - [3.1 Find Trips](#31-find-trips)

 - [3.2 My Trips](#32-my-trips)

 - [3.3 Requesting a Seat](#33-requesting-a-seat)
 
 - [3.4 Leaving a Trip](#34-leaving-a-trip)

 - [3.5 Deleting Trip History](#36-deleting-trip-history)

 - [3.6 Driver Tracking](#37-driver-tracking)

 - [3.7 Create Trip Request](#38-create-trip-request)

 - [3.8 My Trip Requests](#39-my-trip-requests)
 
 - [3.9 Deleting Trip Request](#39-deleting-trip-request)


# 0. Overview

**About Student Carpooling**

'Student Carpooling' is an exclusive carpooling android application for Irish University students. It's a free completely free to download and use, providing you have an active University Email address.

Student Carpooling is a secure way to find other students travelling to and from similar destinations. Public Transport is often unreliable, uncomfortable and unenjoyable, whereas carpooling, you can relax in the presence of your peers. We are providing you with an simple, straight forward way of getting to know new people while obtaining a comfortable ride to your destination. In turn, you are helping the environment by reducing the amount of cars on the road,resulting in less carbon, gasses and pollution in the air.

**Ethics**

The privacy of users is highly important within Student Carpooling. We have designed this application in such a way that each carpool has its own circle of trust. For example, you can only message users that have posted about a trip on the app. Users can also view each others profiles, with their name, college, username and rating being made available. User profiles and messaging is only available on the basis of a trip, as users are not searchable. .In addition, user emails will not be used for anything other than logging in or resetting a password. The app does not do anything without the users request, any actions made by the user are on their own accord. You will be asked first if the app can retrieve your current location before doing so and if it can access your camera roll.

User information is shared between passenger and driver, once a passenger is accepted to a trip. At first, the driver receives their name and approximate location. Once the trip starts, the driver receives the passengers exact location and the drivers location is tracked and made available to the passengers of said trip. We have ensured no unnecessary information is shared among users.

If a student wishes to delete their account on the carpooling app, they will be given the functionality in order to do so. By doing so, their email address will be removed from our Firebase authentication library and their corresponding information within the Firebase real time database will be removed also, according to their unique user ID(which is generated alongside their email).





 # **1. User Manual** 


## **1.1 Installation**

- Go to Google Play Store.
- Search 'Student Carpooling'
- Press **Install.**
- Click **Open** or go to your applications and press the icon to enter the app.

## **1.2  Register**

- Once you have opened the app, you will be presented with the choice to Login or Register.
- Press **Register.**
- You must enter all fields: Name, Surname, Username, University, Age, Email, password and gender
- The username chosen must be unique, the app will inform you if it is not.
- When entering your email, the domain of your email address( after the @ symbol) must match one of those within our list of university email domains. The completed prefix will be automatically shown as you type. You must choose one of these recognised domains in order to continue with the registration process.
- Press Register
- Check your mail inbox for the verification email from Student Carpooling
- Open this email and click on the link to verify your account.

## **1.3  Login**

- After you press Register, and have correctly verified your account, press Login
- A pop-up will appear, enter your email and password in the fields and select whether you are a passenger or a driver.
- Press **Login** to enter the app

## **1.4 Reset Password**

- If not already signed in, when you first open the app you will be shown the home screen.
- Below the login button, click '**Forgot your password?**'
- You will be redirected to the reset password form, enter your email address and press **Reset Password.**
- Check your mail inbox for the reset password email from Student Carpooling
- Open this email and click on the link, enter a new password and press **Save**.

## **1.5  Sign out**

- While you are logged in, open the navigation drawer by clicking on the drawer icon (icon with three lines) within the toolbar, which is located in the top left hand corner
- Once the drawer open, you'll be presented with different menu options depending on your user type.Press Sign Out

## **1.6  Delete Account**

- While you are logged in, press the options icon within the toolbar (show as 3 circles, located in the top right hand corner)
- A pop up menu will appear, choose &#39;Delete Account&#39;
- An alert dialog will be prompted, confirming if you wish to delete your account, press &#39;confirm&#39;

## **1.7  Contact Admins**

- While you are logged in, press the options icon within the toolbar (shown as 3 circles, located in the top right hand corner)
- A pop up menu will appear, choose &#39;Contact Admins&#39;
- An alert dialog will appear, ensuring that you wish to make contact, press &#39;yes&#39;.
- You will be redirected to a new chat activity with a member of the Student Carpooling teams and if there is no active member present, you will be sent an automated message in which you can reply to with your various queries.
- A notification will be sent to your device  when an admin member responds.

## **1.8  Notifications**

- If you receive a notification for various possible reasons, your app icon will show the number of notifications received,( those unchecked within the notification bar of your device)
- Press the icon and enter the app to remove this count.
- You will also receive the notification in the notification bar, under the heading Student Carpooling, with details of the notification.
- Click the notification to be taken into the app, and doing this will also remove the count from the app icon.

## **1.9  Help**

- While you are logged in, press the options icon within the toolbar (show as 3 circles, located in the top right hand corner)
- A pop up menu will appear, choose 'Help'
- You will then be redirected to the the help guide with a provided list of the apps basic functionality, in simple step-by-step terms for both type of user modes

## **1.10   Profile**

- A personal user profile will be automatically created based on your information from earlier registration.
- To locate your profile, open the navigation drawer ( 3 lines icon in top left corners), navigate to 'Profile' within the list of options.
- To change the default profile image shown, click on the image.
- Your devices' gallery will be open and you must select a pre-existing image.
- The newly selecting profile picture will be shown and to confirm this update, press **Confirm** below.
- To switch your user mode, click on the text **'Switch User Mode'** at the bottom of the screen.

## **1.11 Viewing Other Profiles**

- Whenever the person icon is shown beside another Users' handle, you can click it and examine their profile.
- The scenarios in which viewing another others profile is possible include:

- ❖❖Passenger/Driver/fellow carpooler of your trip
- ❖❖Requesting a seat of your trip
- ❖❖Viewing the driver details of a potential trip to join
- ❖❖Viewing requested trips

## **1.12 Report User**

- When you are viewing another users' profile, press on the button &quot; **Report User**&quot; at the bottom of their profile.
- When clicked, a pop up form is shown where you must enter the details of your reasoning for reporting this user and press **Submit.**

## **1.13  Messaging System (How to send a message)**

- Whenever the message icon is shown beside another Users&#39; handle, you can click it and start a private chat with that user.
- The scenarios in which you can start a chat with another user include:

- ❖❖Passenger/Driver/fellow carpooler of your trip
- ❖❖Requesting a seat of your trip
- ❖❖Viewing the driver details of a potential trip to join
- ❖❖Viewing requested trips

- When the new chat is launched, you can send a new message through typing within the bottom message bar and pressing the send icon.
- Once a message between either party is sent, this new chat will be created and will now be searchable within you account (see 1.14)

- To exit the current chat, press the left facing arrow in the top left corner within the top bar of the current chat.

## **1.14  Search for Active Chats**

- Open the navigation drawer by pressing on it&#39;s icon (3 lines in top left corner). When opened, click **Messages**
- To search for a chat with a particular user, click on bar saying 'Search a user..'
- Once the keyboard is displayed, type in the users&#39; handle to display all matching active chats
- To revert back to the original display, remove all the entered text within the search bar

## **1.15   Leave Chat**

- Within an active chat, you can choose to leave and stop further communication for the time being.
- Click on the leave icon within the top right hand corner of the chat&#39;s toolbar
- An alert dialog will be shown, confirming your decision to leave this chat, press **Yes**.
- Communication with this user can be restarted within the future on the basis of a new trip and the previous chat will be reloaded.
- If you wish to stop all future communication entirely, either report this user on their profile or contact admins (See 1.7 and 1.12).

## **1.16 Rating Users**

- There are some scenarios in which you can review another carpooler of a trip. These include:

- ❖❖After the completion of a trip
- ❖❖After the cancellation of a trip
- ❖❖If Trip started and not marked as completed

- When a trip arrives at the destination or is the driver presses **End Trip** , a form containing all the carpoolers of given trip will be shown to each, with a rating bar under their names.
- The user can click on a star, leaving a rating from 1 - 5 stars, or simply ignore this.
- If a star rating is selected, the carpooler must click **Submit.**

 # **2. Driver Manual** 

## **2.1 Create a Trip**

- After signing in as a driver, navigate to the &quot;Create Trip&quot; within the options inside the navigation drawer.
- A form will be presented you, all fields must be entered
- By clicking on the Starting/Destination fields, a google address search bar will pop up, here you must enter and select a recognised location from the automatically completed results.
- When clicking on Time/Date field, a dialog will pop up for each
- When selecting the date, there will be a left and right facing arrow, move them accordingly to change the month, and then select the day of the month below.
- When date is chosen, press **OK**.
- When selecting the time, the current time will automatically be selected.
-  Click on the hours and when highlighted, move the clock around accordingly to select the hour.
- Then, click on the minutes, and move the clock accordingly.
- If you wish to type the time instead, click the keyboard icon at the bottom left of the time dialog.
- When time has been chosen, press **OK**.
- To select the number of the seats, click on the down facing arrow.
- A list from 1-4 will be shown, chose one of these numbers.
- To select if your car will have luggage space or not, click the Yes or No buttons as appropriate.
- To enter an additional note to your future passengers and any preferences (no smoking,etc), click on the note box and enter your text.
- Once all fields are chosen, press **Create.**

## **2.2 Find Requests**

- Navigate to the 'Find Requests' within the options inside the navigation drawer.
- When opened, you will be shown all of the requested trips made by passengers and use this as a way of recruiting more passengers to your own carpool, or possibly starting a new carpool to suit one of said requests.

## **2.3 My Trips**

- Navigate to 'My Trips' within the navigation drawer.
- You can click either past, today or future trips. This is shown within a tabbed layout.
- To either view the history or details of a trip, click on the specific trip

## **2.4 Passenger Requests**

- Navigate to the particular trip within 'My Trips' and click on it
- Click **View Requests**
- To accept a passenger, click on the tick
- To decline a passenger, click on the X
- If the trip has no new requests, you will be informed and guided to examine other passengers requested trip forms
 
## **2.5 Removing a Passenger**

-  Navigate to the particular trip within 'My Trips' and click on it
-  Under the title 'Passengers' you will see the list for the trip
-  Click on the X beside the username
-  An alert dialog will be shown, confirming your wish to delete this passenger, click **Yes**
-  The passenger will now be removed from the trip and will be notified

## **2.6 Starting a Trip**

- Navigate to 'My Trips' and click **Start Trip,** on the specific trip.
- An alert dialog will be displayed to confirm you wish to start the trip, press **Yes**
- If you attempt to start the trip before it's scheduled time, an alert dialog will be shown confirming you decision to start. If so, press **Yes**

## **2.7 Calculating a Route**

- Once a trip has been started, click on either one of your passengers or destination marker
- When a marker is click, a window will show either 'Your destination' or 'Username X pick up location'
- When that marker's window is clicked, an alert dialog will be shown asking if  you wish to calculate the route to that marker, press **Yes**
- All potential routes from your current location to that marker will be shown and when one is clicked, the duration and kilometres to that marker like appear
- To remove the current route from the screen, click on the refresh icon in the bottom left corner to reset the map

## **2.8 Ending a Trip**

- Your trip will automatically be ended once you have reached your destination, otherwise, reopen the map by clicking **Open Map**
- Click **End Trip** within the top right hand corner
- If you are over a certain number of kilometres from your destination, an alert dialog will be shown to confirm. If so, press **Yes** to continue

## **2.9 Cancelling a Trip**

- Navigate to and click on the particular trip within 'My Trips' and click **Cancel Trip**
- An alert dialog will be shown confirming your decision to cancel, press **Yes**
- The Delete Icon will now be displayed in the top right hand corner

## **2.10 Deleting a Trip**

- A trip can be deleted, if:

- ❖❖A trip has expired (was never started by driver)
- ❖❖A trip was cancelled
- ❖❖A trip was completed successfully

- Navigate to and click on the particular trip within 'My Trips' and click on the bin icon within the top right hand corner
- An alert dialog will be shown to confirm, press **Yes**

## **2.11 Search for Requested Trips**

- Open the navigation drawer and click '**Find Requests**'
- Within the details of the requested trip shows the passenger who created it, beside their name provides the option to either message them or view their profile
- Here, you can find potential passengers to include as part of your carpool and message one to invite them to join your trip



 # **3. Passenger Manual** 

## **  3.1 Find Trips**

- Open up your navigation drawer, and click 'Find Trips'
- Once clicked, a form will be presented to you where you can enter in the details of the carpool your seeking, which include: The starting point, destination, date and whether you require luggage space or not.
- Select a luggage space, or else it will be selected by default.
- You can chose to not enter the starting and destination points. If so, you will either see all upcoming trips on for the selected day, all trip departing from the inputted starting point or all trips for that given destination.
- You must be chose a date.
- Otherwise, you can search trips based solely on entering the driver&#39;s valid username where it states 'Enter driver username' and ignore all other fields
- To retrieve the results, click **Find**

## **  3.2 Requesting a Seat**

- After searching for a trip and successfully yielding results, you must click on the trip you wish to join
- At the bottom of the pop up, press **Request Seat**
- A map will then be shown with a field at the top where you must enter your pick up address
- Your entered text will be automatically completely and must be one of the recognised results
- The map will then zoom into your pickup point to confirm its correct location
- Press **Request**
- You will be notified whether the corresponding driver accepts/declines your request

## **  3.3 My Trips**

- Open up the navigation drawer through clicking on the icon with the 3 parallel lines
- Click My Trips to view all your upcoming trips.

## **3.4 Leaving a Trip**

- Navigate to 'My Trips' and click on the exact trip you wish to leave
- Click on the **'Leave Trip'** button located at the bottom of the screen,
- An alert dialog will be shown to confirm this decision, press **Yes**

## **3.5 Deleting Trip History**

- Navigate to 'My Trips' and click on the trip you wish to delete (The trip selected must be one that either is cancelled, expired or completed)
- A bin icon will appear in the trip hand corner within the details of the trip, click that icon
- An alert dialog will be shown to confirm this deletion, press **Yes**

## **3.6 Driver Tracking**

- Navigate to 'My Trips' and search for the trip and click it
- Click **Track Driver** (if the driver has started the trip)

## **3.7 Create Trip Requests**

- Navigate to 'Trip Requests' within the navigation drawer
- You will be shown two tabs, Click **Create Request**
- You will be required to enter your values into the shown fields
- Click **Submit**

## **3.8 My Trip Requests**

- Navigate to 'Trip Requests' within the navigation drawer
- You will be shown two tabs, Click **My Requests**

## **3.9 Deleting Trip Request**

- Navigate to 'Trip Requests' within the navigation drawer
- You will be shown two tabs, Click **My Requests**
- A bin icon will be shown in the top right hand corner of each trip request card within your list of trip requests, Click the icon
- An alert dialog will be shown confirming the deletion of this trip, click **Yes**