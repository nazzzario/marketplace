-- Add the time_period column with a default value
ALTER TABLE public.tbl_product
ADD COLUMN IF NOT EXISTS time_period INT DEFAULT 0;

-- Update existing value for the time_period column if needed
UPDATE public.tbl_product
SET time_period = 30
WHERE time_period IS NULL;