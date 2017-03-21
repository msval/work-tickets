// src/app.js
import {inject} from "aurelia-framework";
import {HttpClient} from 'aurelia-fetch-client';
import { Router } from 'aurelia-router';
import * as toastr from "toastr";

@inject(HttpClient, Router)
export class Home {

    constructor(http, router, toastr) {
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
        toastr.info('blah');
    }

    doAdd() {
        this.http.fetch('http://localhost:8080/projects/WT/tickets', {
            method: 'put',
            body: JSON.stringify({
                "ticketName": this.ticketName,
                "ticketDescription": this.ticketDescription
            })
        }).then(response => response.json())
    }
}