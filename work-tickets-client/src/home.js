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
            'hello',
            'what is your focus for today',
            'oh well'
        ];
        this.message = '';
        this.messageIndex = 0;
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
        this.messageIndex = (this.messageIndex + 1) % this.messages.length;
        this.message = this.messages[this.messageIndex];
    }
}