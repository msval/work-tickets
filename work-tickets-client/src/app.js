// src/app.js
import {inject} from "aurelia-framework";
import 'bootstrap';

export class App {
    configureRouter(config, router) {
        config.map([
            {route: ['', 'home'], name: 'home', moduleId: 'home', nav: false, title: 'Home'},
            {route: 'tickets', name: 'tickets', moduleId: 'tickets', nav: false, title: 'Tickets'}
        ]);

        this.router = router;
    }
}