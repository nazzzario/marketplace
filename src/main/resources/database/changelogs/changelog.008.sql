ALTER TABLE public.tbl_user
ADD COLUMN IF NOT EXISTS google_id character varying(255);

ALTER TABLE public.tbl_user ALTER COLUMN username TYPE varchar(125);
