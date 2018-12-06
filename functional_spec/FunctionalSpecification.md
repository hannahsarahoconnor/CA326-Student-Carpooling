# **Functional Specification - Student Carpooling**

## Group Members

  * Hannah O Connor
  * Catherine Mooney
  

## **Table of contents**

## 1. Introduction  
                                    
     [Overview](#overview)
    
     1.2 Business Context
    
     1.3 Glossary

## 2. General Description

     2.1 Product / System Functions

     2.2 User Characteristics and Objectives

     2.3 Operational Scenarios

     2.4 Constraints

## 3.  Functional Requirements

     3.1 Registration

     3.2 Login

     3.3 Selecting User Instance

     3.4 Profile

     3.5 Creating a Trip Request

     3.6 Creating a Trip

     3.7 Searching for a Trip (Driver)

     3.8 Searching for a Trip (Passenger)

     3.9 Requesting a Trip

     3.10 Accepting a Request

     3.11 Trip Details

     3.12 Starting a Journey

     3.13 Calculating Passenger Route

     3.14 Tracking the Driver

     3.15 Messaging

     3.16 Rating System

     3.17 Logout

## 4. System Architecture

    4.1 System Architecture Diagram

## 5. High-Level Design

    5.1 Context Diagram

    5.2  Data Flow Diagram

    5.3  Logical Diagram

    5.4 Use Case Diagram

## 6. Preliminary Schedule

    6.1  Task List

    6.2  Gantt Diagram

## 7. Appendices



# **1. Introduction**

## Overview

The project we're working on is an exclusive, student carpooling, android, mobile application. This application takes a twist on conventional ride sharing, with the main goal being  to help irish students with commuting to college. The primary functionality of this app is to allow for the organisation of traveling via shared vehicles, to various colleges, among students. Student Carpooling will have two different users, &quot;Passenger&quot; and &quot;Driver&quot;, with different functionality for each. It will allow for any college student to set up an account and benefit from the features that this app can offer. They can search for a suitable driver and, through the in app messaging feature, our users can message one another.

Our objective is to make the lives of students easier in terms of travelling and costs. Not only learning to drive is expensive, but also having to fill a car with petrol each week for a long commute can add up, but many students don&#39;t have that luxury of being able to get the most out of public transport and/or their leap card. Our goal is to normalise the idea of car sharing with your peers and meet those who you may not have had the chance to before. It is often difficult to find people from your area, or along your route, to add to your carpool, without help. It lead to the only people car sharing being those who know each other quite well. The app can provide a more flexible option for finding commuters and make their student lives a little bit easier.

The main function of this application is to aid the organisation of traveling via shared vehicles to various colleges, among students.Our App will allow verified students to set up an account.Once verified, the users can either create a ride or join a ride, whether being classed as a Driver or Passenger. This is allowing for a safe way for students to get involved in their university and college communities.

Whether it being commuting to college during the week or driving home for the weekend, students can utilise the fact that many other students are travelling the same journeys, often in the same time frames. Part of a students routine is travelling to and from college, so carpooling regularly provides an alternative for connecting students, while helping the environment, along with saving time and money.

For example, if you are a student who lives in Monaghan but you stay in Dublin during the week, as many do, you can check the app to see if anyone from Monaghan is making that journey and you can request to join it. Also students who live in nearby towns to your college, who make the journey everyday, could car share and this would be made possible through this app.

Public transport can be very inconvenient for students, as they must plan their day around those times, and there is no flexibility involved. Often, students spend multiple hours a day just waiting on their different modes of transport alone. Our app solves this problem, with the built in messaging service you never have to stand outside waiting for a bus, which is unreliable and you are not guaranteed a seat. Students travelling to and from college often have a routine, and if any changes are to occur they have a way of letting you know as early as possible. Car-sharing means students spend less time traveling to college everyday and less time waiting around, which grants them the ability to put there time to good use. As for the driver, carpooling saves them lots of time in the morning as there is less cars on the road and fewer cars in the carpark, so the driver would not have to leave so early to ensure they get parked, as parking on university campuses is often limited.

The reason behind this app was because one member of our group often uses  public transport to college which can be unreliable, expensive and is often late. The other member, who drives, stated that they could carshare only they do not travel the same route. This sparked the idea of a carpool app for students as it became clear that this is a problem for many people and limiting it to only students leaves a level of reassurance among passengers. As students ourselves who live at home and not on campus we can see the benefits in which this application could bring, with one who commutes via a car and the other by public transport, we can understand it from two different perspectives.

One of the major downfalls of carpooling nowadays is that people are uncomfortable with sharing their car with strangers, our app on some scale, eliminates this problem. Students are safe in the knowledge that they are only travelling with people from their university. Also, there will be a rating system so that after each journey, fellow passengers can rate each other. If a driver drives carelessly, or is not punctual, passengers can then give them a low rating. Likewise, if a passenger is distracting to the driver, etc., the driver and other passengers can express this through the passenger rating. Introducing a rating system increases the level of safety of the app, and only passengers that have travelled together can rate each other following a journey.



## 1.2 Business Context

Here are few examples of what could be done with this application in terms of business:

- Universities could possibly make various instances of the app and sell our software, allowing each to create their own private networks for carpooling, or have the app simply just encouraged to be used by universities as a new means of travel.

- In relation to our project, a possible business context would be to potentially upload it  to the Google Play Store.

- Allow for advertisement within our app, targeted to students and young adults and generate revenue from that.



## 1.3 Glossary

- **Android OS** : Google's Linux-based open source operating system for mobile devices.
- **Android Studio:** The official integrated development environment for the Android application development.
- **Firebase** : is a cloud-based development platform owned by google, it acts as the server and provides tools in order to develop our app.
- **Google Maps API** : A service in order to enabled google maps within our application and allows for location markers too.
- **Geo-Fire** : GeoFire is an open-source library for Android that allows you to store and query a set of keys based on their geographic location using the Firebase realtime database.
- **Firebase Authentication:** A service that can authenticate our users using through email and passwords. When a new user signs up with an email, it sends a verification email to that address before storing to database. It handles sending password reset emails too.
- **Firebase Real-time Database:** No SQL, cloud hosted database that syncs and stores data across our app users within real time.
- **Google Play Services:** Required in order to use google APIs such as Google Maps within our app.
- **Place Autocomplete API:** A service that provide autocomplete functionality and predictions for string geographic searches
- **Google Directions API:** A service in which calculates directions between locations.
- **Firebase Cloud Storage:** FirebaseSDK to allow for the storage of user-generated content, such as photos.

# **2. General Description**

## 2.1 Product / System Functions

Displayed below are the main functions in which we plan to incorporate into our app, the functionality is subject to change over the course of development, however, for now this list is what we would like to feature. These functions will require a user to ,first, be registered successfully and have chosen their instance of user, as with our app having two types of user, each user to have different operations and permissions.

- Downloading the app and creating an account.
- Login (and Password Reset).
- Choosing an instance of user.
- Creating a trip.
- Creating a trip request.
- Requesting a trip.
- Viewing your trip(s).
- Starting a Journey.
- Real time Driver tracking
- Passenger(s) location and route automation.
- Rating System.
- User Profile.
- In-App messaging.
- Logging out.


## 2.2 User Characteristics and Objectives

The user community in which we plan to target is exclusively college students. We believe, with establishing the exclusivity of this app, students would be more comfortable car sharing with each other. This creates a circle of trust for our users, as they share a common association, reassuring them that those, in which they are travelling with, are not complete strangers. We believe this can in fact grow college communities, bringing more student bodies together within a single university and allow for friendships that may of not have grown/not occurred, if our app did not exist.

The User Interface will allow for a simple, yet appealing user friendly experience, where users can establish themselves as a driver or passenger. The driver can simply create a journey and allow for requests from passengers to join said journey. We expect the app to be straightforward and simple, to ensure that it is not time consuming to make or join journeys. It will be structured in such a way that upon use, it will be self-explanatory. Whether acquiring rich technology skills or not, this app will be accommodating for all.



## 2.3 Operational Scenarios

As the structure of Student Carpooling has two type of users, there will be different instances of each scenario and although they perform similarly, they are entirely separate. Our two types of user are Driver and Passenger.

**User signs up**

After successfully having downloaded the app, the user, whether driver or passenger, must be logged in before accessing any of the functionality of the app. They must then enter all the necessary fields in this form, especially a valid college email and they will then receive an email to this address where they will verify themself. They&#39;ll then be required to login with that same college email and chosen password. After the login screen, there user will be presented with their choice of user for the current login.

**Passenger creates a request**

The passenger can search through the list of already created trips from various drivers and apply filters accordingly, to only see those which are relevant to this passenger. If none of those displayed are a suitable option, the passenger can choose to fill out a form for a trip request which can then be searchable by drivers. If it matches a future trip for the driver, or they are willing to fulfil this request, they can message and organise this new trip between them as there will be a message option shown on the request information.

**Driver creates a trip**

A user with a car and full licensed can have the option to create a new trip. Here, they can specify all the necessary details, like seat numbers, time, date, and preferences- like no smoking. The driver will fill out a simple form for this action, which will then be searchable by any relevant passenger. If a passenger feels as if this trip is a match for them, they can proceed to tapping the request button, or firstly message this driver. If requested, it is kept within the multitude of passenger request which will be stored within the details of the trip. Before having to accept anyone, the driver can view the passengers profile to see their ratings and their inputted location upon request and let those factors influence their decision.

**Passenger joins a trip**

Once a passengers' request has been accepted by the driver, this trip will then appear within the 'My Trips' page. Here, the users can remind themselves of the details, if needed, and also examine the other users who are apart of this trip, who they can choose to message. Within the specific trip information there will also be an option to track the driver, yet this won&#39;t be made available until the driver &#39;starts&#39; the trip.

**Driver starts a journey and navigates Passengers**

Within 'My trips', a driver can allocate the certain trip they wish to start but tapping on the &#39;start&#39; button underneath it&#39;s details. This will notify all their passenger that the trip has started and it will open up google maps, with each of the passengers shown as markers on the map. When a driver taps on one of the passenger markers, they will be provided with the option to calculate the route to them. The various passengers will now be able to see the movements of their driver.

**Reviewing another user**

Upon finishing a journey, each user of that particular trip, including the driver, will have the option to rate each one of their fellow carpoolers, on a scale from 1 to 5 stars.

## 2.4 Constraints

- **Time constraint** : As we are quite restricted on our app development process due to the time, we will focus on implementing the basic functionality and ensure it all works accordingly, with sufficient testing conducted. Over time we can grow the app and add more features to it.
- **Firebase database constraint:**  With a limit set on our free account on Firebase, we're constrained on the amount that we can store and on the amount of simultaneous connections to our database. (The in-app messaging may be a concern, as it will take up the most of the space.)
- **Internet Constraint:** Users must acquire internet access in order to use our app and its functionality.
- **Mobile OS constraint:** This application will only be available to those with an Android OS mobile.

# **3. Functional Requirements**

**3.1 Registration**

* Description 

This is the first in the list of functionality of our app. As a first time user, it is a requirement to register before proceeding to login. The registration form requires the new user to enter a valid student email address. This step is crucial as it is how we will ensure student exclusivity. The domain of the inputted email must match our drop down list of Irish college and universities email domains. Other basic information is required, like name, college, area, age, etc. After the completion of this form, the user must then be verified through the verification email that will be sent to their inputted address. Within that email, there will be a link in which the user must click, to be added to the database, and proceed to login successfully.

* Criticality

This function is the most crucial of them all. Without having correctly registered users, the app would be unable to provide its services sufficiently. It&#39;s a way of safely keeping our circle of trust, through ensuring strict student exclusivity. Also, by inquiring the basic information of our users and sharing it amongst fellow carpoolers it can remove the suspense of not knowing who they&#39;ll be travelling with. We want to enforce that only one email can be tied to a single account.


* Technical issues

For the verification process we will be using the Firebase Authentication service. We are possibly considering allowing for a sign in option for facebook too, as some further education colleges don&#39;t issue emails to their students.

* Dependencies with other requirements

None.


**3.2  Login**

* Description 

Once successfully registered and the details are stored within the database, they can log in using those same details. When the user inputs into the fields, it will be checked against those stored and, if not correct, a message will appear on the screen informing the user. If the password is forgotten, there will be an option to reset it.

* Criticality

This feature is crucial for identification purposes, it is the entry point of the application to enable interaction and is a way of providing security, making sure that only registered user can log in and access the content.

* Technical issues

The entered details must match what is contained within our user database, we will query the database to ensure this. In the case where a user forgets their password, they will be sent an email in order to reset it. This activity will also use Firebase Authentication Service.

* Dependencies with other requirements

The user must have first completed all the necessary steps in the Registration process in order to use the login function.

**3.3 Selecting User Instance**

* Description 

After logging in, a user is presented with a page showing a switch, to select one of the two options. This is where it separates the two user types and their different functionalities. This is put in place as so no user of the app is confined to a certain type, and each time they log in, they can chose a different instance if they wish.

* Criticality

This step is highly important, as it is where the app can distinguish whether a user is a passenger, and allow them to join a trip, or they are a driver, who wants to create a trip.

* Technical issues

We plan on implementing this feature through using a switch button on the User Interface, with the &#39;Off&#39; side on the left side representing a Passenger and &#39;On&#39; representing a driver, and the user must slide the switch according the either &#39;Passenger&#39; or &#39;Driver&#39;.

* Dependencies with other requirements

Must be a logged in user.

**3.4 User Profile**

* Description 

Following registration, a profile will automatically be set up based on the user data, fetched from the database. Each user will have the option to change their information such as: Name, Age, Location, College, Degree. We also want to allow for users to have the option of adding a picture of themselves to their profile. Each profile will only be visible to those relevant, for example, if they are carpooling together or requesting to.

* Criticality

This is not completely essential to the overall functionality of the app, but it can provide a better experience for drivers and passengers by allow them to see information about each other before travelling together. Also, for the driver&#39;s sake, displaying a profile image could be very useful when picking up their passenger.

* Technical issues

We're aware by allowing users to upload pictures will cause storage space to be occupied very quickly, so we plan on using firebase cloud storage rather than our database. This step of uploading a photo will also require the owner of the mobile to allow our app to access their camera roll and gallery.

* Dependencies with other requirements

The user must be first registered correctly and logged in.

**3.5 .Creating a Trip Request**

* Description 

In the case where there is no suitable match for a passenger, they can create request for a trip and enter in the required details, via a form, in the hopes that a driver will search through and see the request.

* Criticality

We are still quite unsure about this feature, another possible alternative for this would be to allow the passengers to create an alert and be informed, through notifications, whenever a trip is created according to their preferences.

* Technical issues

The layout will be presented as a form in which the user must enter the fields for each part. This will then be stored in the database and will be extracted when a driver enter their &#39;Search for a Trip&#39; function. Filters can be applied to only show those relevant to the particular driver.

* Dependencies with other requirements

This requires the user to be logged in and have selected 'passenger' as their chosen user instance.

**3.6 Creating a Trip**

* Description 

This functionality is the core element of our app. It is the starting point of enabling carpooling among our users. If a driver decides they want to ride share, on a particular day at a given time, they must navigate to the &#39;Create a Trip&#39; function, where they&#39;ll be presented will a form that they must fill out, in order for their trip to be searchable. The driver must enter the necessary information such as: the starting and destination locations, the starting time, the number of seats and preferences regarding the trip, such as no smoking, luggage space, etc. This will be stored to our database and will be visible to passengers upon searching for a related trip.

* Criticality

This is a highly essential feature to the application.

* Technical issues

Upon entering in the addresses of where the driver will start and finish, it can cause inconsistencies across searches in terms of wrong spelling or inputting different versions of the same address. For that reason, we plan on using the Google Place Autocomplete API so that each users&#39; inputted string will be automatically completed and the user must select the matching result that is generated by this API.

* Dependencies with other requirements

This requires to be logged in and have selected 'driver' as the current user instance.

**3.7 Searching for a trip (Driver)**

* Description 

Upon creating a new trip, a driver may want to examine first if there is demand before doing so. The Driver can search through the list of trip requests based on their filters applied. If a request created by another passenger matches their trip, they can message to organise for the driver to create a trip which the passenger can join.

* Criticality

This function isn't necessary, however, it can help to create trips according to demand among passengers and for a driver to get an idea if the creation of a new trip on the app is necessary.

* Technical issues

Possibly, we want to implement a way that once a trip has been organised the passenger can then proceed to quickly search for the newly created trip based on the drivers username.

* Dependencies with other requirements

Requires a user to be logged in and choosing their instance as a driver.

**3.8 Searching for a trip (Passenger)**

* Description 

Passengers can search for a trip according to their preferences through applying filters and only viewing the results. Through searching for a trip, they can see the trips already created by drivers going the same direction. This search will query the database accordingly. If no results are shown it means no specified ride has been created yet, according to those filters applied. It is this moment where the passenger may decide to move on and create a trip request. If there is a matching trip, they will have the option to click &#39;request&#39;. This will be sent to the driver of that trip, who will then, ultimately, decide to allow or deny the user, and the passenger will be notified of the result.

* Criticality

This is essential for trips to gain passengers. We would also like to try include results, that show trips, which don't exactly include the same starting and destination points, but travel along the same route. It can be a valid option but the passenger themselves would have to message the driver, to request to see if they are willing to stop along the way. However, as of this moment, we are unsure of how we can implement this too.

* Technical issues

This search will query the database and return them to the passengers screen, in a scroll like view.

* Dependencies with other requirements

Requires trip to be first created by driver(s) and added to the database in order for a passenger to search the options.

**3.9 Requesting a Trip**

* Description 

This function is for when, after a passenger has searched through the list of matching results, of pre-created trips, and has found an appropriate match. They can then proceed to click on the Request button, in order to possibly be apart of the particular trip. The passenger must also input their location of where they wish to be collected from, if they have luggage and if they are willing to contribute towards petrol costs.

* Criticality

It is very much required in order for a trip to gain passengers and for a driver to ultimate decide if they want to accept or decline this passenger&#39;s request.

* Technical issues

The request button will be shown on the trip details. After clicking the button, the request function will open and the passenger will enter the required information. This will be saved and sent to the specific driver who owns the trip. It will be forwarded to the drivers request box, where they will have the power to view the information and also their profile, ratings, along with their location, provided to decide if it is a suitable match.

* Dependencies with other requirements

Requires Driver to user have create a trip and user instance is 'passenger'.

**3.10 Accepting an request**

* Description 

This function involves the driver accepting a request from a passenger, to be apart of their trip. It will be stored within their requests page, that will be located within the details of the trip. If the driver believes that this is a suitable match, they simply click accept and that passenger will be then added to the trip, and it will appear under &#39;passengers&#39;, when the driver examines the trip details. Otherwise, they can decline it. Either response will be sent back to the relevant passenger and notify them.

* Criticality

It is essential to the application, as it can determine, which passenger, the driver wishes to include within their journey. Providing the driver with the function of either accepting or declining can result in a better overall experience for them, and encourage them to continue carpooling. If this option isn&#39;t in place, it may result in a first come first serve basis, and lead to a driver confined to collecting a passenger who is not near their planned route.

* Technical issues

We are also considering to possibly have these requests pop up, whilst a driver is currently on the app, and a driver can click accept in that moment, as it is possible that the passenger requests may go unnoticed within the driver&#39;s request box.

* Dependencies with other requirements

**3.11 Trip details**

* Description 

This function allows for a both user types to view the details of their planned trips. It&#39;ll be displayed within &#39;My trips&#39; for both passengers and drivers. Upon clicking this function, the user will see a scroll like view of all of their collective trips. For a driver, there will be displayed a &#39;details&#39; and a &#39;start&#39; button. Within the details, the driver can review the details they inputted upon first creating the trip, and it will include a section showing all of the passengers involved. This information will also be made available to the passengers. The start button feature is only available for the driver of the trip, who can click this upon beginning their journey. For the passenger, there will be a &#39;view&#39; option, where they can view the map, examine if the driver has started the trip yet and if currently trackable.

* Criticality

This is an important function, on the drivers behalf, as it is here where the driver chooses to start the journey and to power up google maps and enabling the routes, and tracking. On the other hand, it is not as important for the passenger, however, it can improve their experience, while being made aware of who their are travelling with, and be presented with the ability to message each other. It can act as a reminder for both parties also.

* Technical issues

This will a display a scroll view of scheduled trips, each showing the destination, date and starting time along with a button 'details'. For drivers only, there will be a &#39;start&#39; button. Passengers will have a substitute for the &#39;start&#39; button, &#39;track&#39;, where they can view the real time location of the driver, once the driver hits &#39;start&#39;. Within &#39;details&#39;, the carpoolers will see the list of passengers, beside each name will be a quick link to view their location, profile and have the option to message each other.

* Dependencies with other requirements

It requires a trip to have been created firstly and have passenger included.

**3.12 Starting a Journey**

* Description 

This is a simple function, which the driver will interact with, in order to begin the process of conducting the trip. It will open up google maps, and will begin the function of allowing the driver to view the location of each of their passengers, and for passengers to track their driver. Notifications will be sent to all passengers involved, reminding them and keeping them updated, once triggered.

* Criticality

This isn't an essential function itself in the application, however it must be included in order to power other necessary function within the application.

* Technical issues

The start function is representation as a button in which the driver must select. This is shown within the trip details of the specific trip.

* Dependencies with other requirements

This depends on a trip being created in order for the trip to be shown within a user 'Trips' and for this function to be presented.

**3.13 Calculating Passenger Route**

* Description

  When a driver accepts a request from a passenger, the information of that passenger will be made available to them, within the details of the trip, along with the option to message, view their profile and view the current location set too. Once a trip has started, that current users location is shown on map, and a driver can form a route to them.

* Criticality

  This feature is quite important, as without this, it may be difficult to reach each passenger on the driver's own accord. With this way the driver will have the correct and accurate directions.

* Technical issues

  We will use google services location APIs, to get the last current location of each user and show that to the driver when they start the trip. In order to create the route from the driver to their passengers, we&#39;ll integrate Google directions API and display this on the map.

* Dependencies with other requirements**

  Depends on the trip being created and passengers joined.

**3.14. Tracking the Driver**

* Description

  With this feature, once a driver has clicked 'start' within the trip details of the specific trip, the locations of each of passengers of the trip, and the driver, will be shown as markers on the map. As the driver makes their way to each of the passengers this will be tracked and updated, in real time, for each of the passenger to see.

* Criticality

  This is not a required feature, however, it can provide passengers with reassurance of knowing that the driver is on their way and how close or far away.

* Technical issues

  This functionality will be implemented with google maps api, retrieving the drivers location with Google Play services location APIs and then using an API called GeoFire which uses the Firebase Realtime database to store and query keys  (the latitude and longitude) based on the geographic location of the driver as they move.

* Dependencies with other requirements

  A trip must be first created, passenger joined and the driver has click 'start' on the trip details.

**3.15 Messaging**

* Description

We want users of our app to have the ability to communicate with one another in the case of any enquiries they may have, regarding the journey, and to possibly get to know each other first, before organising to carpool together. Each user&#39;s messages will be stored and organised, within each individuals inbox. No searching option is available with messaging, users will only have the opportunity to message one another in certain cases. For example, if a user is apart of a drivers trip and they would like to communicate or if a user has enquiries about a drivers trip and wants to first message.It&#39;ll be displayed, like any other conventional messaging app, the user&#39;s themselves messages and the recipients replies organised in chronological order.

* Criticality

It is an important feature, as rather than users having to supply their phone number publicly on the app, they can communicate through this messaging system and create alerts for each time a user receives a message from another user.

* Technical issues

We are still unsure of how exactly we will go about creating this feature, as of this moment, we plan to take advantage of the Firebase realtime database, to store the message according to the users id, and return it to the recipient based on their id.

* Dependencies with other requirements

Require a login in user to have first interact with another on the basic of organising a trip.


**3.16 Rating System**

* Description

Passengers and drivers can rate one another, on the experience of their trip together. This rating system will allow users to rate those, in which they carpooled with, from 1 to 5 stars, with 1 being the lowest and 5 being the highest. We plan on implementing this feature within each of the users&#39; profiles, showing their overall average rating. The option to rate a user will be possible to do among tall the members of a particular trip, including the driver, once a trip has been finished.

* Criticality

This is not a key component of our application. A good user rating can provide a level of reassurance for those who are travelling with said user. It also ensures that each user has a good overall experience with the app **.** This rating system could be extremely useful for drivers in the situation where they may receive multiple requests, for a single seat.

* Technical issues

In order to create this, we plan on using the Rating bar widget available within android studio and, through using firebase database,  update the result of each rating to store the average result. We were also considering the possibility of deactivating a user with numerous low ratings.

* Dependencies with other requirements

This requires that a trip has been created first, which includes passengers and has been started by a driver, along with reaching the destination and the driver clicking &#39;finish&#39;.

**3.17 Log out**

* Description

With this function, a user is provided with the option to sign out of the app, if they wish. It is put in place so that no user is confined to any particular device. When they chose to log out, it will not affect their stored user information and they can sign back in at any given time.

* Criticality

With a user having the option to sign in, we must ensure that we provide them with the option of signing out. Also, this ensures that in the situation, where a user may acquire a new device, they&#39;ll be able to successfully log into that without their account being affected.

 * Technical issues

With this function, it means we will no longer be able to track the user. In the case of a trip being scheduled, within an hour, or currently running, and the user decided to log out, we would then try and prevent this action.

 * Dependencies with other requirements

Requires the user to have first been successfully logged in, before logging out.

## **4. System Architecture**
    ![System Architecture Diagram](/Images/SysArch.jpg)


The above diagram shows the architecture of our project.  The elements involved show the front end which is the android application, along with or backend, Firebase, which is responsible for hosting the application. Firebase includes the various SDKs required for the application to work successfully, such as the Authentication of our users (to provide student exclusivity), the real time database to store important content and cloud storage for allowing users to upload pictures for their profile. Our Application also integrates numerous APIs within, these are Google Maps, Directions, Places and Play Services. These are all required in order to enable the different functions of the Student Carpooling.


# **5. High-Level Design**

## 5.1 Context Diagram

    ![Context Diagram](/Images/ContextDiagram.png)

## 5.2 Data Flow Diagram

    ![Data Flow Diagram](/Images/DFD.jpg)

## 5.3 Logical Diagram

    ![Logical Diagram](/Images/LogicalDiagram.png)

## 5.4 Use Case Diagram

    ![Use Case Diagram](/Images/UseCase.jpg)

## **6. Preliminary Schedule**

    ## 6.1 Task List
  
       ![Task list](/Images/TaskList.png)

    ## 6.2 Gantt Diagram

       ![Gantt Diagram](/Images/Gantt.png)

# **7. Appendices**

[https://firebase.google.com](https://firebase.google.com)

[https://firebase.google.com/docs/storage/](https://firebase.google.com/docs/storage/)

[https://firebase.google.com/docs/database/](https://firebase.google.com/docs/database/)

[https://firebase.google.com/docs/auth/android/email-link-auth](https://firebase.google.com/docs/auth/android/email-link-auth)

[https://github.com/firebase/geofire-java](https://github.com/firebase/geofire-java)

[https://developers.google.com/maps/documentation/directions/start](https://developers.google.com/maps/documentation/directions/start)

[https://developers.google.com/maps/documentation/android-sdk/intro](https://developers.google.com/maps/documentation/android-sdk/intro)

[https://developers.google.com/places/web-service/intro](https://developers.google.com/places/web-service/intro)

[https://developers.google.com/places/web-service/autocomplete](https://developers.google.com/places/web-service/autocomplete)
