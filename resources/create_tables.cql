-- initial version
CREATE KEYSPACE tickets WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 3};

USE tickets;

CREATE TABLE ticket(project TEXT, id TEXT, name TEXT, description TEXT, PRIMARY KEY(project, id)) WITH CLUSTERING ORDER BY(id DESC);

CREATE TABLE projects(bucket TEXT, project TEXT, description TEXT, PRIMARY KEY(bucket, project));

ALTER TABLE cycling.teams ALTER ID TYPE uuid;

--added state of ticket

ALTER TABLE tickets.ticket ADD state text;
ALTER TABLE tickets.ticket ADD changed_at timestamp;