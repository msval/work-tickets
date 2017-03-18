// src/app.js
import {inject} from "aurelia-framework";
import {HttpClient} from 'aurelia-fetch-client';
import { Router } from 'aurelia-router';

@inject(HttpClient, Router)
export class Home {

    constructor(http, router) {
        this.http = http;
        this.tickets = [];
        this.year = new Date().getFullYear();
        this.router = router
    }

    activate() {
        return this.http.fetch('http://localhost:8080/projects/WT/tickets')
            .then(response => response.json())
            .then(data => this.tickets = data)
    }

    goToHome() {
        this.router.navigateToRoute('home')
    }
}