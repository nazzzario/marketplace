CREATE TABLE IF NOT EXISTS public.tbl_product
(
    id serial UNIQUE NOT NULL,
    reference uuid UNIQUE NOT NULL,
    owner_id integer NOT NULL,
    category_name character varying(15) NOT NULL,
    city character varying NOT NULL,
    product_title character varying(100) NOT NULL,
    product_description character varying(300) NOT NULL,
    state character varying(15) NOT NULL,
    status character varying(15) NOT NULL DEFAULT 'ACTIVE',
    created_date timestamp with time zone,
    modified_date timestamp with time zone,
    CONSTRAINT pk_tbl_product PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.tbl_product
    ADD CONSTRAINT fk_tbl_product_on_owner FOREIGN KEY (owner_id) REFERENCES public.tbl_user (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;
