CREATE TABLE IF NOT EXISTS public.tbl_user_favorite_products
(
    user_id serial REFERENCES tbl_user (id),
    product_id serial REFERENCES tbl_product (id),
    PRIMARY KEY (user_id, product_id)
);
