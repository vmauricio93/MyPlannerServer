ALTER TABLE task
ADD FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;