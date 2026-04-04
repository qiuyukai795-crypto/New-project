CREATE TABLE IF NOT EXISTS shops (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    rating DOUBLE NOT NULL,
    avg_price INT NOT NULL,
    district VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    signature_dish VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS shop_tags (
    shop_id BIGINT NOT NULL,
    tag_name VARCHAR(50) NOT NULL,
    sort_order INT NOT NULL,
    PRIMARY KEY (shop_id, sort_order),
    CONSTRAINT fk_shop_tags_shop_id FOREIGN KEY (shop_id) REFERENCES shops (id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    shop_id BIGINT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at DATE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reviews_shop_id FOREIGN KEY (shop_id) REFERENCES shops (id)
);
