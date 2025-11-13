--
-- PostgreSQL database dump
--

\restrict cocTzcaqov418YTYdTIepDM7Scyofw0vkEQHtMNGQhEpA0CNsdtzsuIL6GjoqNq

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

-- Started on 2025-11-13 11:44:53

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 24780)
-- Name: airline; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA airline;


ALTER SCHEMA airline OWNER TO postgres;

--
-- TOC entry 5 (class 2615 OID 24652)
-- Name: userschema; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA userschema;


ALTER SCHEMA userschema OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 230 (class 1259 OID 24838)
-- Name: cancellation; Type: TABLE; Schema: airline; Owner: postgres
--

CREATE TABLE airline.cancellation (
    cancel_id integer NOT NULL,
    customer_id integer NOT NULL,
    flight_code character varying(10) NOT NULL,
    seat_no integer,
    days_left integer,
    hours_left integer,
    basic_amount numeric(10,2),
    cancel_amount numeric(10,2),
    refund_amount numeric(10,2)
);


ALTER TABLE airline.cancellation OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 24837)
-- Name: cancellation_cancel_id_seq; Type: SEQUENCE; Schema: airline; Owner: postgres
--

CREATE SEQUENCE airline.cancellation_cancel_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE airline.cancellation_cancel_id_seq OWNER TO postgres;

--
-- TOC entry 4863 (class 0 OID 0)
-- Dependencies: 229
-- Name: cancellation_cancel_id_seq; Type: SEQUENCE OWNED BY; Schema: airline; Owner: postgres
--

ALTER SEQUENCE airline.cancellation_cancel_id_seq OWNED BY airline.cancellation.cancel_id;


--
-- TOC entry 221 (class 1259 OID 24782)
-- Name: customer; Type: TABLE; Schema: airline; Owner: postgres
--

CREATE TABLE airline.customer (
    customer_id integer NOT NULL,
    t_date date,
    cust_name character varying(100) NOT NULL,
    father_name character varying(100),
    gender character varying(10),
    dob date,
    address text,
    tel_no character varying(15),
    profession character varying(50),
    security character varying(50),
    concession character varying(50)
);


ALTER TABLE airline.customer OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 24781)
-- Name: customer_customer_id_seq; Type: SEQUENCE; Schema: airline; Owner: postgres
--

CREATE SEQUENCE airline.customer_customer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE airline.customer_customer_id_seq OWNER TO postgres;

--
-- TOC entry 4864 (class 0 OID 0)
-- Dependencies: 220
-- Name: customer_customer_id_seq; Type: SEQUENCE OWNED BY; Schema: airline; Owner: postgres
--

ALTER SEQUENCE airline.customer_customer_id_seq OWNED BY airline.customer.customer_id;


--
-- TOC entry 226 (class 1259 OID 24808)
-- Name: fare; Type: TABLE; Schema: airline; Owner: postgres
--

CREATE TABLE airline.fare (
    fare_id integer NOT NULL,
    route_code character varying(10),
    s_place character varying(50),
    via character varying(50),
    d_place character varying(50),
    d_time timestamp without time zone,
    a_time timestamp without time zone,
    flight_code character varying(10),
    class_code character varying(10),
    fare numeric(10,2)
);


ALTER TABLE airline.fare OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24807)
-- Name: fare_fare_id_seq; Type: SEQUENCE; Schema: airline; Owner: postgres
--

CREATE SEQUENCE airline.fare_fare_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE airline.fare_fare_id_seq OWNER TO postgres;

--
-- TOC entry 4865 (class 0 OID 0)
-- Dependencies: 225
-- Name: fare_fare_id_seq; Type: SEQUENCE OWNED BY; Schema: airline; Owner: postgres
--

ALTER SEQUENCE airline.fare_fare_id_seq OWNED BY airline.fare.fare_id;


--
-- TOC entry 223 (class 1259 OID 24791)
-- Name: fleet; Type: TABLE; Schema: airline; Owner: postgres
--

CREATE TABLE airline.fleet (
    fleet_id integer NOT NULL,
    no_aircraft character varying(50) NOT NULL,
    club_pre_capacity integer,
    eco_capacity integer,
    engine_type character varying(50),
    cruise_speed character varying(20),
    air_length character varying(20),
    wing_span character varying(20)
);


ALTER TABLE airline.fleet OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 24790)
-- Name: fleet_fleet_id_seq; Type: SEQUENCE; Schema: airline; Owner: postgres
--

CREATE SEQUENCE airline.fleet_fleet_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE airline.fleet_fleet_id_seq OWNER TO postgres;

--
-- TOC entry 4866 (class 0 OID 0)
-- Dependencies: 222
-- Name: fleet_fleet_id_seq; Type: SEQUENCE OWNED BY; Schema: airline; Owner: postgres
--

ALTER SEQUENCE airline.fleet_fleet_id_seq OWNED BY airline.fleet.fleet_id;


--
-- TOC entry 224 (class 1259 OID 24797)
-- Name: flight; Type: TABLE; Schema: airline; Owner: postgres
--

CREATE TABLE airline.flight (
    flight_code character varying(10) NOT NULL,
    flight_name character varying(100) NOT NULL,
    class_code character varying(10),
    t_exe_seatno integer,
    t_eco_seatno integer,
    fleet_id integer
);


ALTER TABLE airline.flight OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 24820)
-- Name: reservation; Type: TABLE; Schema: airline; Owner: postgres
--

CREATE TABLE airline.reservation (
    reservation_id integer NOT NULL,
    customer_id integer NOT NULL,
    flight_code character varying(10) NOT NULL,
    travel_date date,
    seat_no integer,
    class character varying(10),
    waiting_no integer,
    status character varying(20) DEFAULT 'Pending'::character varying
);


ALTER TABLE airline.reservation OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 24819)
-- Name: reservation_reservation_id_seq; Type: SEQUENCE; Schema: airline; Owner: postgres
--

CREATE SEQUENCE airline.reservation_reservation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE airline.reservation_reservation_id_seq OWNER TO postgres;

--
-- TOC entry 4867 (class 0 OID 0)
-- Dependencies: 227
-- Name: reservation_reservation_id_seq; Type: SEQUENCE OWNED BY; Schema: airline; Owner: postgres
--

ALTER SEQUENCE airline.reservation_reservation_id_seq OWNED BY airline.reservation.reservation_id;


--
-- TOC entry 219 (class 1259 OID 24654)
-- Name: users; Type: TABLE; Schema: userschema; Owner: postgres
--

CREATE TABLE userschema.users (
    id integer NOT NULL,
    first_name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    address character varying(100),
    birth_date date,
    email character varying(100) NOT NULL,
    phone_number character varying(20),
    password character varying(100) NOT NULL
);


ALTER TABLE userschema.users OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 24653)
-- Name: users_id_seq; Type: SEQUENCE; Schema: userschema; Owner: postgres
--

CREATE SEQUENCE userschema.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE userschema.users_id_seq OWNER TO postgres;

--
-- TOC entry 4868 (class 0 OID 0)
-- Dependencies: 218
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: userschema; Owner: postgres
--

ALTER SEQUENCE userschema.users_id_seq OWNED BY userschema.users.id;


--
-- TOC entry 4677 (class 2604 OID 24841)
-- Name: cancellation cancel_id; Type: DEFAULT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.cancellation ALTER COLUMN cancel_id SET DEFAULT nextval('airline.cancellation_cancel_id_seq'::regclass);


--
-- TOC entry 4672 (class 2604 OID 24785)
-- Name: customer customer_id; Type: DEFAULT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.customer ALTER COLUMN customer_id SET DEFAULT nextval('airline.customer_customer_id_seq'::regclass);


--
-- TOC entry 4674 (class 2604 OID 24811)
-- Name: fare fare_id; Type: DEFAULT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.fare ALTER COLUMN fare_id SET DEFAULT nextval('airline.fare_fare_id_seq'::regclass);


--
-- TOC entry 4673 (class 2604 OID 24794)
-- Name: fleet fleet_id; Type: DEFAULT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.fleet ALTER COLUMN fleet_id SET DEFAULT nextval('airline.fleet_fleet_id_seq'::regclass);


--
-- TOC entry 4675 (class 2604 OID 24823)
-- Name: reservation reservation_id; Type: DEFAULT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.reservation ALTER COLUMN reservation_id SET DEFAULT nextval('airline.reservation_reservation_id_seq'::regclass);


--
-- TOC entry 4671 (class 2604 OID 24657)
-- Name: users id; Type: DEFAULT; Schema: userschema; Owner: postgres
--

ALTER TABLE ONLY userschema.users ALTER COLUMN id SET DEFAULT nextval('userschema.users_id_seq'::regclass);


--
-- TOC entry 4857 (class 0 OID 24838)
-- Dependencies: 230
-- Data for Name: cancellation; Type: TABLE DATA; Schema: airline; Owner: postgres
--

COPY airline.cancellation (cancel_id, customer_id, flight_code, seat_no, days_left, hours_left, basic_amount, cancel_amount, refund_amount) FROM stdin;
\.


--
-- TOC entry 4848 (class 0 OID 24782)
-- Dependencies: 221
-- Data for Name: customer; Type: TABLE DATA; Schema: airline; Owner: postgres
--

COPY airline.customer (customer_id, t_date, cust_name, father_name, gender, dob, address, tel_no, profession, security, concession) FROM stdin;
\.


--
-- TOC entry 4853 (class 0 OID 24808)
-- Dependencies: 226
-- Data for Name: fare; Type: TABLE DATA; Schema: airline; Owner: postgres
--

COPY airline.fare (fare_id, route_code, s_place, via, d_place, d_time, a_time, flight_code, class_code, fare) FROM stdin;
\.


--
-- TOC entry 4850 (class 0 OID 24791)
-- Dependencies: 223
-- Data for Name: fleet; Type: TABLE DATA; Schema: airline; Owner: postgres
--

COPY airline.fleet (fleet_id, no_aircraft, club_pre_capacity, eco_capacity, engine_type, cruise_speed, air_length, wing_span) FROM stdin;
\.


--
-- TOC entry 4851 (class 0 OID 24797)
-- Dependencies: 224
-- Data for Name: flight; Type: TABLE DATA; Schema: airline; Owner: postgres
--

COPY airline.flight (flight_code, flight_name, class_code, t_exe_seatno, t_eco_seatno, fleet_id) FROM stdin;
\.


--
-- TOC entry 4855 (class 0 OID 24820)
-- Dependencies: 228
-- Data for Name: reservation; Type: TABLE DATA; Schema: airline; Owner: postgres
--

COPY airline.reservation (reservation_id, customer_id, flight_code, travel_date, seat_no, class, waiting_no, status) FROM stdin;
\.


--
-- TOC entry 4846 (class 0 OID 24654)
-- Dependencies: 219
-- Data for Name: users; Type: TABLE DATA; Schema: userschema; Owner: postgres
--

COPY userschema.users (id, first_name, last_name, address, birth_date, email, phone_number, password) FROM stdin;
1	dxt	fxd	yrd	2002-10-30	fd@gmail.com	6546	123
\.


--
-- TOC entry 4869 (class 0 OID 0)
-- Dependencies: 229
-- Name: cancellation_cancel_id_seq; Type: SEQUENCE SET; Schema: airline; Owner: postgres
--

SELECT pg_catalog.setval('airline.cancellation_cancel_id_seq', 1, false);


--
-- TOC entry 4870 (class 0 OID 0)
-- Dependencies: 220
-- Name: customer_customer_id_seq; Type: SEQUENCE SET; Schema: airline; Owner: postgres
--

SELECT pg_catalog.setval('airline.customer_customer_id_seq', 1, false);


--
-- TOC entry 4871 (class 0 OID 0)
-- Dependencies: 225
-- Name: fare_fare_id_seq; Type: SEQUENCE SET; Schema: airline; Owner: postgres
--

SELECT pg_catalog.setval('airline.fare_fare_id_seq', 1, false);


--
-- TOC entry 4872 (class 0 OID 0)
-- Dependencies: 222
-- Name: fleet_fleet_id_seq; Type: SEQUENCE SET; Schema: airline; Owner: postgres
--

SELECT pg_catalog.setval('airline.fleet_fleet_id_seq', 1, false);


--
-- TOC entry 4873 (class 0 OID 0)
-- Dependencies: 227
-- Name: reservation_reservation_id_seq; Type: SEQUENCE SET; Schema: airline; Owner: postgres
--

SELECT pg_catalog.setval('airline.reservation_reservation_id_seq', 1, false);


--
-- TOC entry 4874 (class 0 OID 0)
-- Dependencies: 218
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: userschema; Owner: postgres
--

SELECT pg_catalog.setval('userschema.users_id_seq', 1, true);


--
-- TOC entry 4693 (class 2606 OID 24843)
-- Name: cancellation cancellation_pkey; Type: CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.cancellation
    ADD CONSTRAINT cancellation_pkey PRIMARY KEY (cancel_id);


--
-- TOC entry 4683 (class 2606 OID 24789)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customer_id);


--
-- TOC entry 4689 (class 2606 OID 24813)
-- Name: fare fare_pkey; Type: CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.fare
    ADD CONSTRAINT fare_pkey PRIMARY KEY (fare_id);


--
-- TOC entry 4685 (class 2606 OID 24796)
-- Name: fleet fleet_pkey; Type: CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.fleet
    ADD CONSTRAINT fleet_pkey PRIMARY KEY (fleet_id);


--
-- TOC entry 4687 (class 2606 OID 24801)
-- Name: flight flight_pkey; Type: CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.flight
    ADD CONSTRAINT flight_pkey PRIMARY KEY (flight_code);


--
-- TOC entry 4691 (class 2606 OID 24826)
-- Name: reservation reservation_pkey; Type: CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.reservation
    ADD CONSTRAINT reservation_pkey PRIMARY KEY (reservation_id);


--
-- TOC entry 4679 (class 2606 OID 24661)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: userschema; Owner: postgres
--

ALTER TABLE ONLY userschema.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 4681 (class 2606 OID 24659)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: userschema; Owner: postgres
--

ALTER TABLE ONLY userschema.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4698 (class 2606 OID 24844)
-- Name: cancellation cancellation_customer_id_fkey; Type: FK CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.cancellation
    ADD CONSTRAINT cancellation_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES airline.customer(customer_id) ON DELETE CASCADE;


--
-- TOC entry 4699 (class 2606 OID 24849)
-- Name: cancellation cancellation_flight_code_fkey; Type: FK CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.cancellation
    ADD CONSTRAINT cancellation_flight_code_fkey FOREIGN KEY (flight_code) REFERENCES airline.flight(flight_code) ON DELETE CASCADE;


--
-- TOC entry 4695 (class 2606 OID 24814)
-- Name: fare fare_flight_code_fkey; Type: FK CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.fare
    ADD CONSTRAINT fare_flight_code_fkey FOREIGN KEY (flight_code) REFERENCES airline.flight(flight_code) ON DELETE CASCADE;


--
-- TOC entry 4694 (class 2606 OID 24802)
-- Name: flight flight_fleet_id_fkey; Type: FK CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.flight
    ADD CONSTRAINT flight_fleet_id_fkey FOREIGN KEY (fleet_id) REFERENCES airline.fleet(fleet_id) ON DELETE SET NULL;


--
-- TOC entry 4696 (class 2606 OID 24827)
-- Name: reservation reservation_customer_id_fkey; Type: FK CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.reservation
    ADD CONSTRAINT reservation_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES airline.customer(customer_id) ON DELETE CASCADE;


--
-- TOC entry 4697 (class 2606 OID 24832)
-- Name: reservation reservation_flight_code_fkey; Type: FK CONSTRAINT; Schema: airline; Owner: postgres
--

ALTER TABLE ONLY airline.reservation
    ADD CONSTRAINT reservation_flight_code_fkey FOREIGN KEY (flight_code) REFERENCES airline.flight(flight_code) ON DELETE CASCADE;


-- Completed on 2025-11-13 11:44:58

--
-- PostgreSQL database dump complete
--

\unrestrict cocTzcaqov418YTYdTIepDM7Scyofw0vkEQHtMNGQhEpA0CNsdtzsuIL6GjoqNq

