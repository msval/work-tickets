// src/app.js
import {inject} from "aurelia-framework";
import {HttpClient} from 'aurelia-fetch-client';
import 'bootstrap';

@inject(HttpClient)
export class App {

    constructor(http) {
        this.http = http;
        this.projects = [];
        this.year = new Date().getFullYear();
    }

    activate() {
        return this.http.fetch('http://localhost:8080/projects').then(response => response.json()).then(data => this.projects = data)
    }
}