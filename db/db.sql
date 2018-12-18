--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.7
-- Dumped by pg_dump version 9.5.7

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: push_change(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION push_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        PERFORM pg_notify('update_queue', TG_TABLE_NAME);
        RETURN NEW;
    END;
$$;


ALTER FUNCTION public.push_change() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: instrument; Type: TABLE; Schema: public; Owner: app
--

CREATE TABLE instrument (
    "timestamp" date,
    instrument_id integer NOT NULL,
    name character varying(128) NOT NULL,
    description character varying(256) NOT NULL
);


ALTER TABLE instrument OWNER TO app;

--
-- Name: instrument_instrument_id_seq; Type: SEQUENCE; Schema: public; Owner: app
--

CREATE SEQUENCE instrument_instrument_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE instrument_instrument_id_seq OWNER TO app;

--
-- Name: instrument_instrument_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: app
--

ALTER SEQUENCE instrument_instrument_id_seq OWNED BY instrument.instrument_id;


--
-- Name: orders; Type: TABLE; Schema: public; Owner: app
--

CREATE TABLE orders (
    order_id integer NOT NULL,
    version bigint,
    inst_id integer NOT NULL,
    price real,
    quantity real,
    notes character varying(256) NOT NULL,
    side character(1),
    quantity_filled real,
    status character varying(1),
    "timestamp" timestamp(3) without time zone,
    fill_price real
);


ALTER TABLE orders OWNER TO app;

--
-- Name: orders_inst_id_seq; Type: SEQUENCE; Schema: public; Owner: app
--

CREATE SEQUENCE orders_inst_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE orders_inst_id_seq OWNER TO app;

--
-- Name: orders_inst_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: app
--

ALTER SEQUENCE orders_inst_id_seq OWNED BY orders.inst_id;


--
-- Name: orders_order_id_seq; Type: SEQUENCE; Schema: public; Owner: app
--

CREATE SEQUENCE orders_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE orders_order_id_seq OWNER TO app;

--
-- Name: orders_order_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: app
--

ALTER SEQUENCE orders_order_id_seq OWNED BY orders.order_id;


--
-- Name: trade; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trade (
    price real,
    quantity real,
    trade_id integer NOT NULL,
    "timestamp" timestamp(3) without time zone,
    resting_order_id integer NOT NULL,
    incoming_order_id integer NOT NULL
);


ALTER TABLE trade OWNER TO postgres;

--
-- Name: trade_incoming_order_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE trade_incoming_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE trade_incoming_order_id_seq OWNER TO postgres;

--
-- Name: trade_incoming_order_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE trade_incoming_order_id_seq OWNED BY trade.incoming_order_id;


--
-- Name: trade_resting_order_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE trade_resting_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE trade_resting_order_id_seq OWNER TO postgres;

--
-- Name: trade_resting_order_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE trade_resting_order_id_seq OWNED BY trade.resting_order_id;


--
-- Name: trade_trade_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE trade_trade_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE trade_trade_id_seq OWNER TO postgres;

--
-- Name: trade_trade_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE trade_trade_id_seq OWNED BY trade.trade_id;


--
-- Name: instrument_id; Type: DEFAULT; Schema: public; Owner: app
--

ALTER TABLE ONLY instrument ALTER COLUMN instrument_id SET DEFAULT nextval('instrument_instrument_id_seq'::regclass);


--
-- Name: order_id; Type: DEFAULT; Schema: public; Owner: app
--

ALTER TABLE ONLY orders ALTER COLUMN order_id SET DEFAULT nextval('orders_order_id_seq'::regclass);


--
-- Name: inst_id; Type: DEFAULT; Schema: public; Owner: app
--

ALTER TABLE ONLY orders ALTER COLUMN inst_id SET DEFAULT nextval('orders_inst_id_seq'::regclass);


--
-- Name: trade_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trade ALTER COLUMN trade_id SET DEFAULT nextval('trade_trade_id_seq'::regclass);


--
-- Name: resting_order_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trade ALTER COLUMN resting_order_id SET DEFAULT nextval('trade_resting_order_id_seq'::regclass);


--
-- Name: incoming_order_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trade ALTER COLUMN incoming_order_id SET DEFAULT nextval('trade_incoming_order_id_seq'::regclass);


--
-- Data for Name: instrument; Type: TABLE DATA; Schema: public; Owner: app
--

COPY instrument ("timestamp", instrument_id, name, description) FROM stdin;
2018-12-02	1	6758.T	Sony Co.Ltd.
2018-12-02	2	6753.T	Sharp Co.Ltd.
2018-12-02	3	6702.T	Fujitsu Co.Ltd.
\.


--
-- Name: instrument_instrument_id_seq; Type: SEQUENCE SET; Schema: public; Owner: app
--

SELECT pg_catalog.setval('instrument_instrument_id_seq', 3, true);


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: app
--

COPY orders (order_id, version, inst_id, price, quantity, notes, side, quantity_filled, status, "timestamp", fill_price) FROM stdin;
\.


--
-- Name: orders_inst_id_seq; Type: SEQUENCE SET; Schema: public; Owner: app
--

SELECT pg_catalog.setval('orders_inst_id_seq', 2, true);


--
-- Name: orders_order_id_seq; Type: SEQUENCE SET; Schema: public; Owner: app
--

SELECT pg_catalog.setval('orders_order_id_seq', 14038, true);


--
-- Data for Name: trade; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY trade (price, quantity, trade_id, "timestamp", resting_order_id, incoming_order_id) FROM stdin;
\.


--
-- Name: trade_incoming_order_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('trade_incoming_order_id_seq', 1, false);


--
-- Name: trade_resting_order_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('trade_resting_order_id_seq', 1, false);


--
-- Name: trade_trade_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('trade_trade_id_seq', 431, true);


--
-- Name: instrument_pkey; Type: CONSTRAINT; Schema: public; Owner: app
--

ALTER TABLE ONLY instrument
    ADD CONSTRAINT instrument_pkey PRIMARY KEY (instrument_id);


--
-- Name: orders_pkey; Type: CONSTRAINT; Schema: public; Owner: app
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (order_id);


--
-- Name: trade_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trade
    ADD CONSTRAINT trade_pkey PRIMARY KEY (trade_id);


--
-- Name: table_change; Type: TRIGGER; Schema: public; Owner: app
--

CREATE TRIGGER table_change AFTER INSERT OR DELETE OR UPDATE ON orders FOR EACH ROW EXECUTE PROCEDURE push_change();


--
-- Name: orders_inst_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: app
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_inst_id_fkey FOREIGN KEY (inst_id) REFERENCES instrument(instrument_id);


--
-- Name: trade_incoming_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trade
    ADD CONSTRAINT trade_incoming_order_id_fkey FOREIGN KEY (incoming_order_id) REFERENCES orders(order_id);


--
-- Name: trade_resting_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trade
    ADD CONSTRAINT trade_resting_order_id_fkey FOREIGN KEY (resting_order_id) REFERENCES orders(order_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: instrument; Type: ACL; Schema: public; Owner: app
--

REVOKE ALL ON TABLE instrument FROM PUBLIC;
REVOKE ALL ON TABLE instrument FROM app;
GRANT ALL ON TABLE instrument TO app;


--
-- Name: instrument_instrument_id_seq; Type: ACL; Schema: public; Owner: app
--

REVOKE ALL ON SEQUENCE instrument_instrument_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE instrument_instrument_id_seq FROM app;
GRANT ALL ON SEQUENCE instrument_instrument_id_seq TO app;


--
-- Name: orders; Type: ACL; Schema: public; Owner: app
--

REVOKE ALL ON TABLE orders FROM PUBLIC;
REVOKE ALL ON TABLE orders FROM app;
GRANT ALL ON TABLE orders TO app;


--
-- Name: orders_inst_id_seq; Type: ACL; Schema: public; Owner: app
--

REVOKE ALL ON SEQUENCE orders_inst_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE orders_inst_id_seq FROM app;
GRANT ALL ON SEQUENCE orders_inst_id_seq TO app;


--
-- Name: orders_order_id_seq; Type: ACL; Schema: public; Owner: app
--

REVOKE ALL ON SEQUENCE orders_order_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE orders_order_id_seq FROM app;
GRANT ALL ON SEQUENCE orders_order_id_seq TO app;


--
-- Name: trade; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE trade FROM PUBLIC;
REVOKE ALL ON TABLE trade FROM postgres;
GRANT ALL ON TABLE trade TO postgres;
GRANT ALL ON TABLE trade TO app;


--
-- Name: trade_trade_id_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE trade_trade_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE trade_trade_id_seq FROM postgres;
GRANT ALL ON SEQUENCE trade_trade_id_seq TO postgres;
GRANT ALL ON SEQUENCE trade_trade_id_seq TO app;


--
-- PostgreSQL database dump complete
--

