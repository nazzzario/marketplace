-- add_columns.sql

-- Add the publish_date column with a default value
ALTER TABLE public.tbl_product
ADD COLUMN IF NOT EXISTS publish_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Update existing data for the publish_date column if needed
UPDATE public.tbl_product
SET publish_date = CURRENT_TIMESTAMP
WHERE publish_date IS NULL;

-- Add the view_count column with a default value
ALTER TABLE public.tbl_product
ADD COLUMN IF NOT EXISTS view_count INT DEFAULT 0;
