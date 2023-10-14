ALTER TABLE public.tbl_user RENAME COLUMN first_name TO username;
ALTER TABLE public.tbl_user RENAME COLUMN last_name TO email;
ALTER TABLE public.tbl_user ADD COLUMN password character varying(30) NOT NULL;

ALTER TABLE public.tbl_user ADD CONSTRAINT tbl_user_email_key UNIQUE (email);
