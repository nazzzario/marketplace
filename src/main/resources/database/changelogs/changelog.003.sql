CREATE TABLE IF NOT EXISTS public.tbl_product_images
(
    id serial UNIQUE NOT NULL,
    reference uuid UNIQUE NOT NULL,
    product_id integer NOT NULL,
    product_url character varying(255) NOT NULL,
    cover boolean NOT NULL DEFAULT FALSE,
    created_date timestamp with time zone,
    modified_date timestamp with time zone,
    CONSTRAINT pk_tbl_product_images PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.tbl_product_images
    ADD CONSTRAINT fk_tbl_product_images_on_product FOREIGN KEY (product_id) REFERENCES public.tbl_product (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;
