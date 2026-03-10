# FocusFlow – Productivity App

FocusFlow is a full-stack productivity application that helps users manage tasks and track focused work sessions using the Pomodoro technique.

## Features

• Pomodoro focus timer
• Task management system
• REST API backend
• MySQL database persistence
• Clean productivity dashboard UI

## Tech Stack

Frontend

* HTML
* CSS
* JavaScript

Backend

* Java
* Spring Boot
* Spring Data JPA

Database

* MySQL

## Architecture

Frontend → REST API → Database

FocusFlow UI sends requests to a Spring Boot backend which stores tasks in MySQL and returns them via REST endpoints.

## API Example

GET /tasks

Returns list of tasks stored in database.

## Future Improvements

• User authentication
• Productivity analytics dashboard
• Weekly focus reports
• Cloud deployment
