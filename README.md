# Glitchy
Glitchy is an App to glitch your photo developed using Kotlin language.

Idea was to develop a simple App to improve my knowledge about Kotlin Programming Language, but, later I decided to make it pretty and publish it, very soon, on the Play Store!

## Design Project 
There are three packages inside the project:
 - App package
 - Library package
 - App core package

Before start of talking about the packages, it's important to emphasize that I uses MVP pattern.
  
### App package
In this package I put everything concerns Android App: 
eg:
 - Activities 
 - Dialogs
 - Fragments
 - Adapters

In other words, the views of MVP pattern

### Library package
Here, I put just classes to create glitch (and other effects) from Bitmap and Byte Array

#### Two type of Effects
I have distinguished two types of effects: 
 - JPEG effect: effects that work on jpeg compression
 - Canvas effect: effects that work on bitmap (don't mind compression)

### App Core package
Presenters and Logic (my own mvp implementation contains logic aka model) are in this package.

Pay attention that there is a custom ImageView component that implements History and Glitch Views (of mvp). This component is critical to application.

## App Name
Glitchy name is temporary, but I think that it'll be the final one

## App Design
It's too early talk about design. App misses main icon and color palette. So, I think is useless to show some screenshot of the application.

