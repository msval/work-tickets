CREATE KEYSPACE tickets WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 3};

USE tickets;

CREATE TABLE ticket(id TEXT, name TEXT, description TEXT, PRIMARY KEY (id));

CREATE TABLE tickets(project TEXT, ticket_id TEXT, PRIMARY KEY(project, ticket_id));
