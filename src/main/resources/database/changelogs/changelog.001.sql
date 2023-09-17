CREATE TABLE IF NOT EXISTS public.tbl_user
(
    id serial UNIQUE NOT NULL,
    reference uuid UNIQUE NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30) NOT NULL,
    phone_number character varying(30) UNIQUE NOT NULL,
    created_date timestamp with time zone,
    modified_date timestamp with time zone,
    CONSTRAINT pk_tbl_user PRIMARY KEY (id)
);
