MERGE INTO user_t
(email, password_hash, name, tos_accepted)
KEY (email)
VALUES
('iv127@gmail.com', '456def', 'Name Example', true);
