# GymCompanionApp
GymCompanionApp is an Android app for creating your own workouts, out of your own custom made exercises, complete with a video and tips for correct execution. It also has a feature for interval training with built-in, customizable timers.

## Requirements
- Android 5.1+

## Installation
- Download the lates apk file from [here](https://pc.cd/dpw7)
- Click to install
- Make sure you have enabled 3rd party installs in your device's settings

## Usage
### Welcome Screen
It has 4 buttons on it:

- Warm-ups
- Exercises
- Countdown
- Settings (located in top bar)

### Settings
At the top of it, are the fields for the time values for interval training (exercise and recovery time).
Below are 2 buttons, one for importing exercises (from properly formatted zip files), and one for exporting all loaded exercises into one properly formatted zip file.

### Warm-up Screen
On top bar has a new warm-up button, that takes you to the exercise information screen, where you can create your very own warm-up exercise.
It displays an scrollable list of all of the loaded warm-up exercises, divided into the different categories.
Each exercise is a button, with 2 functions. A simple click will register the exercise as chosen. A long press will take you to the exercise template screen with the corresponding information loaded.
At the bottom, there's the start button. This button takes you to the circuit trainer screen, with the chosen exercises. If no exercises are marked as chosen, all exericses will load up into the trainer screen.

### Exercise Screen
On top bar has a new warm-up button, that takes you to the exercise information screen, where you can create your very own exercise.
It displays an scrollable list of all of the loaded exercises, divided into the different categories.
Each exercise is a button, with 2 functions. A simple click will register the exercise as chosen. A long press will take you to the exercise template screen with the corresponding information loaded.
At the bottom, there are two buttons. The circuit button takes you to the circuit trainer screen. The interval button takes you to the interval trainer. This buttons won't function with no exercises chosen.

### Circuit Trainer Screen
It displays (from top to bottom):

- Exercise name
- Exercise sample video view
- Exercise category
- Exercise's tips [3]
- Buttons: previous /next

### Interval Trainer
It displays (from top to bottom):

- Exercise name
- Exercise sample video view
- Exercise category
- Exercise's tips [3]

At the bottom of the screen, is the timer. It begins by loading the set recovery-time value and starting a countdown. On finish, an alert dialog will inform you and play a horn.
Clicking ok will set the value for the set exercise-time and start the countdown.
On finish, an alert dialog will inform you and a countinous siren will sound.
Clicking ok will load the next exercise data, set the timer value to recovery time, and start the countdown.

### Exercise Template
This is where one registers new exercises (from the + button on the top bar of the warmups and exercises screens).
It has 4 fields:
- Exercise Name
- Exercise category dropdown menu
- 3 practical exercise tips
- Button to load video file

At the bottom there are 3 buttons:
- New button: enabled only when clicking through the new exercise/warmup
- Modify button: enabled when long-press of loaded exercise/warmup. Saves the info into the existing exercise
- Delete button: enabled when long-press of loaded exercise/warmup. Deletes all exercise information

## Credits
This project uses the following resources:

- Airhorn sound by: [Mike Koenig](http://soundbible.com/1542-Air-Horn.html)


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[GPLv2](https://choosealicense.com/licenses/gpl-2.0/)
