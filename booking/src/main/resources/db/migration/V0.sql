create table IF NOT EXISTS account_entity
(
	account_id varchar(255) not null
		constraint account_entity_pkey
			primary key,
	password varchar(255),
	user_name varchar(255)
);

create table IF NOT EXISTS association_value_entry
(
	id bigint not null
		constraint association_value_entry_pkey
			primary key,
	association_key varchar(255) not null,
	association_value varchar(255),
	saga_id varchar(255) not null,
	saga_type varchar(255)
);

create table IF NOT EXISTS payment_entity
(
	payment_id varchar(255) not null
		constraint payment_entity_pkey
			primary key,
	account_id uuid,
	payment_status varchar(255),
	total_amount numeric(19,2)
);

create table IF NOT EXISTS room_availability_entity
(
	room_number integer not null
		constraint room_availability_entity_pkey
			primary key,
	room_description varchar(255),
	room_status varchar(255)
);

create table IF NOT EXISTS room_availability_entity_bookings
(
	room_availability_entity_room_number integer not null
		constraint fkg3vnuf7y3hxnhcwqy8apocb6v
			references room_availability_entity,
	account_id varchar(255),
	end_date timestamp,
	id varchar(255),
	start_date timestamp
);

create table IF NOT EXISTS room_availability_entity_failed_bookings
(
	room_availability_entity_room_number integer not null
		constraint fkqtap9ekt40re2349vgclrk9d5
			references room_availability_entity,
	account_id varchar(255),
	end_date timestamp,
	id varchar(255),
	reason varchar(255),
	start_date timestamp
);

create table IF NOT EXISTS room_cleaning_schedule_entity
(
	room_number integer not null
		constraint room_cleaning_schedule_entity_pkey
			primary key
);

create table IF NOT EXISTS room_cleaning_schedule_entity_bookings
(
	room_cleaning_schedule_entity_room_number integer not null
		constraint fk51dqossuhi88vt4rt0t3g6eb
			references room_cleaning_schedule_entity,
	account_id varchar(255),
	end_date timestamp,
	id varchar(255),
	start_date timestamp
);

create table IF NOT EXISTS room_checkout_schedule_entity
(
	room_number integer not null
		constraint room_checkout_schedule_entity_pkey
			primary key,
	room_status varchar(255)
);

create table IF NOT EXISTS room_checkout_schedule_entity_bookings
(
	room_checkout_schedule_entity_room_number integer not null
		constraint fk51dqossuhi88vt4rt0t3g6ec
			references room_checkout_schedule_entity,
	account_id varchar(255),
	end_date timestamp,
	id varchar(255),
	start_date timestamp
);

create table IF NOT EXISTS saga_entry
(
	saga_id varchar(255) not null
		constraint saga_entry_pkey
			primary key,
	revision varchar(255),
	saga_type varchar(255),
	serialized_saga oid
);

create table IF NOT EXISTS token_entry
(
	processor_name varchar(255) not null,
	segment integer not null,
	owner varchar(255),
	timestamp varchar(255) not null,
	token oid,
	token_type varchar(255),
	constraint token_entry_pkey
		primary key (processor_name, segment)
);

CREATE SEQUENCE hibernate_sequence START 1;
