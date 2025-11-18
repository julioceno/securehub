CREATE TABLE password_reset_tokens (
   id TEXT PRIMARY KEY,
   user_id TEXT NOT NULL,
   token TEXT NOT NULL,
   expires_at TIMESTAMP NOT NULL,
   confirmed_at TIMESTAMP NULL,

   CONSTRAINT fk_password_reset_user
       FOREIGN KEY (user_id)
           REFERENCES users(id)
           ON DELETE CASCADE
);
