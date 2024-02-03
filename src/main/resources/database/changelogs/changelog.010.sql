-- Add the ad_raise_count column with a default value
ALTER TABLE public.tbl_product
ADD COLUMN IF NOT EXISTS ad_raise_count INT DEFAULT 0;