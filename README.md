# Glitchy
Glitchy is an App to glitch your photo developed using Kotlin language.

The idea was to develop a simple App to improve my knowledge about Kotlin Programming Language, but later I decided to make it pretty and publish it, very soon, on the Play Store!

## Project Design
There are three packages inside the project:
 - App package
 - Library package
 - App core package

Before start talking about the packages, it's important to point out that I used MVP pattern.
  
### App package
In this package I put everything concerning Android App: 
eg:
 - Activities 
 - Dialogs
 - Fragments
 - Adapters

In other words, the views of MVP pattern

### Library package
Here, I put just classes to create glitch (and other image effects) from Bitmap and Byte Array

#### Two type of Effects
I have distinguished two types of effects: 
 - JPEG effect: effects that work on jpeg compression
 - Canvas effect: effects that work on bitmap (don't mind compression)

### App Core package
Presenters and Logic (my own mvp implementation contains logic aka model) are in this package.

Pay attention that there is a custom ImageView component that implements History and Glitch Views (of mvp). This component is critical to the application.

## App Name
Glitchy name is temporary, but I think that it'll be the final one

## App Design
It's too early talk about design. App misses main icon and color palette. So, I think is useless to show some screenshot of the application.

# LICENSE
 Copyright 2017 Angelo Moroni

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

