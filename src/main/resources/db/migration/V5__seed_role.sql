INSERT INTO role (code, label) VALUES
    ('VOLUNTEER', 'Volunteer'),
    ('BOARD_MEMBER', 'Board member'),
    ('FOSTER_PARENT', 'Foster parent'),
    ('KINSHIP', 'Kinship caregiver')
ON CONFLICT (code) DO NOTHING;