drop table IF EXISTS hits CASCADE;
drop table IF EXISTS users CASCADE;
drop table IF EXISTS categories CASCADE;
drop table IF EXISTS events CASCADE;
drop table IF EXISTS locations CASCADE;
drop table IF EXISTS participation_requests CASCADE;
drop table IF EXISTS compilations CASCADE;
drop table IF EXISTS compilations_events CASCADE;
drop table IF EXISTS views CASCADE;


CREATE TABLE IF NOT EXISTS hits (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        app VARCHAR NOT NULL,
                                        uri VARCHAR NOT NULL,
                                        ip VARCHAR NOT NULL,
                                        time_from_request TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );

CREATE TABLE IF NOT EXISTS locations (
                                         id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                         description varchar,
                                         lat INTEGER,
                                         lon INTEGER,
                                         CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                          name VARCHAR(255) NOT NULL,
                                          CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events (
                                      id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                      category_id INTEGER NOT NULL,
                                      confirmed_requests INTEGER,
                                      created_on TIMESTAMP WITHOUT TIME ZONE,
                                      published_on TIMESTAMP WITHOUT TIME ZONE,
                                      user_id_initiator INTEGER NOT NULL,
                                      annotation text  NOT NULL,
                                      description text  NOT NULL,
                                      event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                      location_id INTEGER,
                                      paid boolean,
                                      participant_limit INTEGER,
                                      request_moderation boolean,
                                      title varchar,
                                      state varchar,
                                      views INTEGER,
                                      participation_available boolean,
                                      CONSTRAINT pk_events PRIMARY KEY (id),
                                      constraint LOCATION_ID_FK_EVENTS foreign key (location_id) references locations(id),
                                      constraint CATEGORY_ID_FK_EVENTS foreign key (category_id) references categories(id),
                                      constraint USER_ID_INITIATOR_FK_EVENTS foreign key (user_id_initiator) references users(id)
);

CREATE TABLE IF NOT EXISTS participation_requests (
                                      id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                      created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                      event_id  INTEGER NOT NULL,
                                      user_id INTEGER NOT NULL UNIQUE,
                                      status varchar,
                                      CONSTRAINT pk_participation_requests PRIMARY KEY (id),
                                      constraint EVENT_ID_FK_EVENTS foreign key (event_id) references events(id),
                                      constraint USER_ID_FK_USERS foreign key (user_id) references users(id)
);

CREATE TABLE IF NOT EXISTS compilations (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
event_id INTEGER,
pinned boolean,
title varchar,
CONSTRAINT compilations PRIMARY KEY (id),
constraint COMPILATION_ID_FK_EVENTS foreign key (event_id) references events(id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
compilation_id INTEGER,
event_id  INTEGER,
constraint CE_ID_FK_COMPILATIONS foreign key (compilation_id) references compilations(id),
constraint CE_ID_FK_EVENTS foreign key (event_id) references events(id),
constraint CE_ID_MAIN_COMPILATIONS primary key (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS views (
                                                   event_id INTEGER,
                                                   user_ip  varchar,
    constraint CE_ID_FK_VIEWS foreign key (event_id) references events(id),
    constraint CE_ID_VIEWS primary key (event_id, user_ip)
    );
