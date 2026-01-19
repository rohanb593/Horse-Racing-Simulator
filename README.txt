**Status:** ✅ Completed

## Project Description

The Horse Racing Simulator is a Java-based application that simulates horse racing with two distinct implementations: a console-based textual version (Part 1) and a feature-rich graphical user interface version (Part 2). This project demonstrates object-oriented programming principles, GUI development with Java Swing, and game mechanics design.

### Part 1: Textual Console Version
A simple command-line simulation that displays horse races in ASCII format. The program features three default horses (Thunder, Lightning, and Storm) racing along a track, with probability-based movement mechanics. Horses have confidence levels that affect their performance, and they can fall during the race. The simulation automatically progresses and displays real-time race status with visual track representation.

### Part 2: Graphical User Interface Version
An advanced GUI application built with Java Swing that provides a comprehensive horse racing experience. The interface includes:

- **Race Visualization**: Animated horses represented as colorful shapes (rectangles, circles, triangles, diamonds, stars) racing along a visual track
- **Weather System**: Four weather conditions (Sunny, Rainy, Muddy, Icy) that affect track appearance and horse performance
- **Betting System**: Place bets on horses with a starting budget of $1000, with dynamic odds based on horse confidence and performance history
- **Performance Metrics**: Track detailed statistics including win ratios, average speeds, race history, and weather-specific performance data
- **Horse Management**: Add and customize up to 10 horses with unique names, colors, shapes, and confidence levels
- **Real-time Updates**: Live race progress with animated movement, fall notifications, and automatic result calculation

The simulator uses probability-based mechanics where horse movement is influenced by confidence levels, weather conditions modify speed and fall chances, and winning horses gain confidence boosts while fallen horses receive confidence penalties.

## Requirements

- **Java Development Kit (JDK) 8 or later
- Basic command line knowledge

## Running Part 1 (Textual Version)

### Step 1: Open Command Prompt/Terminal
- Windows: Press `Win+R`, type `cmd`, press Enter
- Mac/Linux: Open Terminal

### Step 2: Navigate to Part 1
cd path/to/HorseRacingSimulator/"Part 1"/src

### Step 3: Compile the Program
javac *.java

or 

javac Part1/src/*.java


### Step 4: Run the Simulation
The main class is found in Race.java.
java Race

### What to Expect:
A text-based horse race will begin automatically

Race ends when a horse wins or all fall

Results display in the console

### Running Part 2 (Graphical Version)

### Step 1: Open Command Prompt/Terminal
- Windows: Press `Win+R`, type `cmd`, press Enter
- Mac/Linux: Open Terminal

### Step 2: Navigate to Part 2
cd path/to/HorseRacingSimulator/"Part 2"/src

### Step 3: Compile the Program
javac *.java

or 

javac Part2/src/*.java

### Step 4: Run the Simulation
The main class is found in RaceGUI.java.
java RaceGUI

Using the GUI:
Main Window Components:
-----Left Tabs: Horse management, betting, and statistics    
-----Right Panel: Race visualization and controls

Starting a Race:
-----Select weather condition (Sunny/Rainy/Muddy/Icy)
-----Click "Start Race" button
-----Watch animated horses race

Key Features:
-----Add horses with custom shapes/colors
-----Place bets on horses
-----View performance statistics
-----Track race history


Features Overview

-- Part 1 (Textual Version)
-- Console-based simulation
-- 3 default horses (Thunder, Lightning, Storm)
-- Simple probability-based movement
-- Visual track with horse positions
-- Automatic race progression

-- Part 2 (Graphical Version)
-- Visual Elements:
-- Colorful horse shapes (rectangle, circle, triangle, etc.)
-- Weather effects (changes track color)
-- Animated movement

-- Game Systems:
-- Weather impacts speed and fall chance
-- Betting with calculated payouts
-- Confidence system affecting performance
-- Comprehensive statistics tracking

-- Customization:
-- Add/remove horses
-- Set custom names, colors, and shapes
-- Adjust starting confidence

Troubleshooting

-- Common Issues
-- "javac not recognized" error
-- Solution: Install JDK and add Java to PATH
-- Verify with javac -version

-- Blank window in Part 2
-- Ensure all files are in Part2/src
-- Check for error messages in console

-- Slow animation
-- Reduce window size
-- Close other applications

-- Missing horses in Part 2
-- Use "Horses" tab to add horses before racing

-- Error Messages
-- ClassNotFoundException: Recompile all files
-- NullPointerException: Restart the application
-- IllegalArgumentException: Check input values (especially confidence between 0.1-1.0)
-- If command lines do not work attempt to use an IDE, preffered IDE IntelliJ IDEA
