# NutriSmart
An NLP-powered android application that helps you analyze the calorie content from the description of a meal

## Application

NutriSmart is powered by the [Nutritionix API](https://www.nutritionix.com/business/api). It is implemented as an Android application that makes calls to a web servlet written using Apache TomEE. The servlet in turn makes calls to the Nutionix API and sends data back to the android application for displaying to the user. 

REST principles are followed in the development. All data processing (parsing, formatting, etc.) is done on the servlet to keep local CPU load at a minimum. 

Data is transerred between local storage and the web service i JSON format. 
 
## Dashboard

All query logs are maintained in a MongoDB Atlas database and used for displaying some interesting statistics about the application.

A monitoring dashboard that keeps track of requests made is accessible [here](https://enigmatic-citadel-60457.herokuapp.com/stats). 

## Usage

The user provides a description of a meal in a natural language format. Upon clicking submit, calorie information is fetched from the server (which fetches it from the Nutrinionx API) and displayed to the user.

### Demo Video

https://user-images.githubusercontent.com/85018020/147299220-d2770838-feaf-42d2-a2c3-6b63cc902f69.mp4

