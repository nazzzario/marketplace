-- Add the region column with a default value
ALTER TABLE public.tbl_product
 ADD COLUMN IF NOT EXISTS region character varying(100) NOT NULL DEFAULT '';