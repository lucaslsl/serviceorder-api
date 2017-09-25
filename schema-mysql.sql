CREATE TABLE `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(30) NOT NULL,
  `last_name` VARCHAR(60) NOT NULL,
  `username` VARCHAR(30) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `deleted` BOOLEAN NOT NULL DEFAULT false,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `billable_item` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(60) NOT NULL,
  `description` VARCHAR(200) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `deleted` BOOLEAN NOT NULL DEFAULT false,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `customer` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(30) NOT NULL,
  `national_registration_number` VARCHAR(40) NOT NULL,
  `juridical` BOOLEAN NOT NULL DEFAULT false,
  `deleted` BOOLEAN NOT NULL DEFAULT false,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `national_registration_number` (`national_registration_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `customer_address` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT(20) NOT NULL,
  `formatted_address` VARCHAR(160) NOT NULL,
  `locality` VARCHAR(60) NOT NULL,
  `postal_code` VARCHAR(60) NOT NULL,
  `google_place_id` VARCHAR(50),
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `budget` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT(20) NOT NULL,
  `total` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `closed` BOOLEAN NOT NULL DEFAULT false,
  `deleted` BOOLEAN NOT NULL DEFAULT false,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `budget_item` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `billable_item_id` BIGINT(20) NOT NULL,
  `budget_id` BIGINT(20) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `quantity` INTEGER NOT NULL DEFAULT 0,
  `discount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `approved` BOOLEAN NOT NULL DEFAULT false,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `budget_id` (`budget_id`),
  KEY `billable_item_id` (`billable_item_id`),
  FOREIGN KEY (`billable_item_id`) REFERENCES `billable_item` (`id`),
  FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `budget_id` BIGINT(20) NOT NULL,
  `customer_id` BIGINT(20) NOT NULL,
  `subtotal` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `discount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `concluded_at` DATETIME,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `budget_id` (`budget_id`),
  KEY `customer_id` (`customer_id`),
  FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `additional_information` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parent_type` VARCHAR(60) NOT NULL,
  `parent_id` BIGINT(20) NOT NULL,
  `key` VARCHAR(30) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;