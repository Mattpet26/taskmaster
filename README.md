# TaskMaster change log (I will delete it every other day, just to keep track of what I did the previous day)

* 11/3/2020
* Added image functionality to the add-task page. Images are saved to S3.
* Users can select a task, and see which task they selected.
* Task details page renders image selected from add-task.
* Images are unique per task!

* 11/4/2020
* Implemented SNS.
* Modified both build.gradle files + manifest
* Created PushListenerService.java.
* Downloaded the google-services.json, placed in app.

## How to run the app
* Clone the repository from the github
```
 git clone https://github.com/mattpet26/android_taskmaster.git
```

* Open the project using Android Studio

* Run the app using an emulator or on the android phone


## Screen shots of working app
* ![taskImage](screenshots/addTask.PNG)
* ![taskImage](screenshots/userloggedout.PNG)
* ![taskImage](screenshots/userlogged.PNG)
* ![taskImage](screenshots/recyclerWorking.PNG)
* ![taskImage](screenshots/taskdetails.PNG)
* ![taskImage](screenshots/detailsPage.PNG)
* ![taskImage](screenshots/allTask.PNG)
* ![taskImage](screenshots/submitWorking.PNG)
* ![taskImage](screenshots/dynamoDB.PNG)
* ![taskImage](screenshots/pinpointCap.PNG)
* ![taskImage](screenshots/firebirdTest.PNG)