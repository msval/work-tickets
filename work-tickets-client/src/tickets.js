// src/app.js
import {inject} from "aurelia-framework";
import {HttpClient} from 'aurelia-fetch-client';
import { Router } from 'aurelia-router';
import * as toastr from "toastr";
import {Enum} from 'enumify';

@inject(HttpClient, Router)
export class Tickets {

    constructor(http, router, toastr) {
        this.http = http;
        this.tickets = [];
        this.year = new Date().getFullYear();
        this.router = router;
        this.isUpdate = false;
        this.isAdd = false;
        this.addOrEditTitle = '';
        this.ticketName = '';
        this.ticketDescription = '';
        this.ticketId = '';
        this.ticketStates = ['waiting', 'in_progress', 'done', 'canceled'];
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

    updateState(ticketId, ticketName, ticketDescription, state) {
        this.ticketId = ticketId;
        this.ticketName = ticketName;
        this.ticketDescription = ticketDescription;

        this.doUpdate(state);
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

    doUpdate(state) {
        let updateParams = {
            "ticketName": this.ticketName,
            "ticketDescription": this.ticketDescription,
            "ticketId": this.ticketId,
        };

        if (state) {
            updateParams["ticketState"] = state;
        }

        this.http.fetch('http://localhost:8080/projects/WT/tickets', {
            method: 'post',
            body: $.param(updateParams),
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

    confirmWithDelete(ticketId) {
        console.log(this.http);
        if (confirm("Are you sure you want to delete ticket " + ticketId)) {
            this.http.fetch('http://localhost:8080/projects/WT/tickets/' + ticketId, {
                method: 'delete'
            })
            .then(() => {
                this.fetchTickets();
                toastr.info('Deleted ticket ' + ticketId);
            })
            .catch(error => {
                toastr.error('Error deleting ticket', error);
            });
        }
    }
}