-- Add the is_non_locked column with a default value
ALTER TABLE public.tbl_user
ADD COLUMN IF NOT EXISTS is_non_locked boolean NOT NULL DEFAULT TRUE;