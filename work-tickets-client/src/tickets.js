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
        this.isUpdate = false;
        this.isAdd = false
        this.addOrEditTitle = '';
        this.ticketName = '';
        this.ticketDescription = '';
        this.ticketId = '';
    }

    fetchTickets() {
        return this.http.fetch('http://localhost:8080/projects/WT/tickets')
            .then(response => response.json())
            .then(data => this.tickets = data)
    }

    activate() {
        this.fetchTickets();
    }

    goToHome() {
        this.router.navigateToRoute('home')
    }

    addNew() {
        this.isAdd = true;
        this.addOrEditTitle = 'Add';
        this.ticketName = '';
        this.ticketDescription = '';
        this.ticketId = '';
    }

    edit(ticketId, ticketName, ticketDescription) {
        this.isUpdate = true;
        this.addOrEditTitle = 'Update';
        this.ticketId = ticketId;
        this.ticketName = ticketName;
        this.ticketDescription = ticketDescription;
    }

    cancelAddOrEdit() {
        this.isAdd = false;
        this.isUpdate = false;
    }

    doAdd() {
        this.http.fetch('http://localhost:8080/projects/WT/tickets', {
            method: 'put',
            body: $.param({
                "ticketName": this.ticketName,
                "ticketDescription": this.ticketDescription
            }),
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
        .then(response => response.json())
        .then(data => {
            this.fetchTickets();
            toastr.info('Created ticket ' + data.id);
            this.cancelAddOrEdit();
        })
        .catch(error => {
            toastr.error('Error Creating ticket', error);
        });
    }

    doUpdate() {
        this.http.fetch('http://localhost:8080/projects/WT/tickets', {
            method: 'post',
            body: $.param({
                "ticketName": this.ticketName,
                "ticketDescription": this.ticketDescription,
                "ticketId": this.ticketId
            }),
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
            .then(response => response.json())
            .then(data => {
                this.fetchTickets();
                toastr.info('Updated ticket ' + data.id);
                this.cancelAddOrEdit();
            })
            .catch(error => {
                toastr.error('Error Updating ticket', error);
            });
    }
}