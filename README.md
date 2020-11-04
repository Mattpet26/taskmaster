# TaskMaster change log (I will delete it every other day, just to keep track of what I did the previous day)

* 11/2/2020
* Added 3 new classes, Signin/SignUp/SignupConfirmation
* Users can now sign up with a username, password and email. (confirmation code is sent to email)
* After signing up, users are directed to confirmation page.
* Upon confirmation, users are directed to signin.
* After the user signs in, they are directed to home page. Users can see who they are logged in as. Logout button appears to logged in users.
* Users can log out. Logged out users do not see logout button.

* 11/3/2020
* Added image functionality to the add-task page. Images are saved to S3
* Users can select a task, and see which task they selected
* Task details page renders image selected from add-task
* TODO: Retrieve image key for taskDetails

## How to run the app
* Clone the repository from the github
```
 git clone https://github.com/mattpet26/android_taskmaster.git
```

* Open the project using Android Studio

* Run the app using an emulator or on the android phone


## Screen shots of working app -- see last screenshot for dynamoDB results
* ![taskImage](screenshots/addTask.PNG)
* ![taskImage](screenshots/userloggedout.PNG)
* ![taskImage](screenshots/userlogged.PNG)
* ![taskImage](screenshots/recyclerWorking.PNG)
* ![taskImage](screenshots/detailsPage.PNG)
* ![taskImage](screenshots/allTask.PNG)
* ![taskImage](screenshots/submitWorking.PNG)
* ![taskImage](screenshots/dynamoDB.PNG)