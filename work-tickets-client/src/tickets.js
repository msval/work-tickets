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
        this.router = router;
        this.isAddOrUpdate = false;
        this.addOrEditTitle = '';
        this.ticketName = '';
        this.ticketDescription = '';
        this.ticketId = '';
    }

    activate() {
        return this.http.fetch('http://localhost:8080/projects/WT/tickets')
            .then(response => response.json())
            .then(data => this.tickets = data)
    }

    goToHome() {
        this.router.navigateToRoute('home')
    }

    addNew() {
        this.isAddOrUpdate = true;
        this.addOrEditTitle = 'Add';
        this.ticketName = '';
        this.ticketDescription = '';
        this.ticketId = '';
    }

    edit(ticketId, ticketName, ticketDescription) {
        this.isAddOrUpdate = true;
        this.addOrEditTitle = 'Update';
        this.ticketId = ticketId;
        this.ticketName = ticketName;
        this.ticketDescription = ticketDescription;
    }

    cancelAddOrEdit() {
        this.isAddOrUpdate = false;
    }
}