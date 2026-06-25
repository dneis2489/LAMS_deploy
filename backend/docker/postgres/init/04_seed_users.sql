-- Demo users for local authorization through the frontend.
-- Passwords are BCrypt hashes generated with BCryptPasswordEncoder(12).

INSERT INTO public.roles (name)
VALUES
    ('ROLE_SUPER_ADMIN'),
    ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO public.users (
    email,
    username,
    password_hash,
    enabled,
    role_id
)
VALUES
    (
        'admin@lams.local',
        'admin',
        '$2a$12$IjmyNgK4reImiobwattweuufM.R/AlavzB9//mq3fz.E2uFaAdxcG',
        TRUE,
        (SELECT id FROM public.roles WHERE name = 'ROLE_SUPER_ADMIN')
    ),
    (
        'user@lams.local',
        'user',
        '$2a$12$Ku9TKdQpdUcQfB9K0nk1HO5mLNeI2CE1dwi91nstt7YXRi5PSCXWS',
        TRUE,
        (SELECT id FROM public.roles WHERE name = 'ROLE_ADMIN')
    )
ON CONFLICT (email) DO UPDATE
SET
    username = EXCLUDED.username,
    password_hash = EXCLUDED.password_hash,
    enabled = EXCLUDED.enabled,
    role_id = EXCLUDED.role_id,
    updated_at = CURRENT_TIMESTAMP;
