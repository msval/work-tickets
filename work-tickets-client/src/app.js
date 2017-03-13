// src/app.js
import {inject} from "aurelia-framework";
import {HttpClient} from 'aurelia-fetch-client';

@inject(HttpClient)
export class App {
    projects = [];

    constructor(http) {
        this.http = http;
        this.projects = []
    }

    activate() {
        return this.http.fetch('http://localhost:8080/projects').then(response => response.json()).then(data => this.projects = data)
    }
}
