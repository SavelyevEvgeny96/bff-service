CREATE EXTENSION IF NOT EXISTS pgcrypto;

DROP TABLE IF EXISTS product_payment_exception;
DROP TABLE IF EXISTS payment_methods;
DROP TABLE IF EXISTS products;

CREATE TABLE products (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name        varchar(100) NOT NULL,
  description varchar(500),

  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_products_name ON products(name);

CREATE TABLE payment_methods (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  type        varchar(30) NOT NULL,
  description varchar(200),

  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_payment_methods_type ON payment_methods(type);

CREATE TABLE product_payment_exception (
  id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id uuid NOT NULL,
  payment_id uuid NOT NULL,

  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_ppe_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT fk_ppe_payment FOREIGN KEY (payment_id) REFERENCES payment_methods(id) ON DELETE CASCADE,
  CONSTRAINT uq_ppe_product_payment UNIQUE (product_id, payment_id)
);

CREATE INDEX ix_ppe_product_id ON product_payment_exception(product_id);
CREATE INDEX ix_ppe_payment_id ON product_payment_exception(payment_id);

INSERT INTO products (name, description)
VALUES
  ('AccidentInsurance', 'Страхование от НС'),
  ('SogazFlat',         'Страхование квартиры');

INSERT INTO payment_methods ("type", description)
VALUES
  ('CARD', 'Банковская карта'),
  ('SBP',  'СБП');
