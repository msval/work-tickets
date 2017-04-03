// src/app.js
import {inject} from "aurelia-framework";
import {HttpClient} from 'aurelia-fetch-client';
import { Router } from 'aurelia-router';

@inject(HttpClient,Router)
export class Home {

    constructor(http, router) {
        this.http = http;
        this.projects = [];
        this.year = new Date().getFullYear();
        this.router = router;
        this.messages = [
            'deal with it',
            'change is inevitable',
            '0 is you, 1 is the action'
        ];
        this.message = '';
    }

    activate() {
        this.inspireMe();

        return this.http.fetch('http://localhost:8080/projects')
            .then(response => response.json())
            .then(data => this.projects = data)
    }

    goToTickets() {
        this.router.navigateToRoute('tickets')
    }

    inspireMe() {
        this.message = this.messages[Math.floor(Math.random() * this.messages.length)];
    }
}