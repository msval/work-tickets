CREATE KEYSPACE tickets WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 3};

USE tickets;

CREATE TABLE ticket(project TEXT, id TEXT, name TEXT, description TEXT, PRIMARY KEY(project, id));

CREATE TABLE projects(bucket TEXT, project TEXT, description TEXT, PRIMARY KEY(bucket, project));