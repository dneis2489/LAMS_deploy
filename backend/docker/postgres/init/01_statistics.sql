-- =========================================================
-- Source: create_total_count_request_stats_tabels.sql
-- =========================================================
BEGIN;

-- =========================================
-- Почасовая статистика
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_count_request_stats_hour
(
    stat_hour         timestamptz NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_stats_hour
        PRIMARY KEY (stat_hour)
);

-- =========================================
-- Посуточная статистика
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_count_request_stats_day
(
    stat_day          date NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_stats_day
        PRIMARY KEY (stat_day)
);

-- =========================================
-- Помесячная статистика
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_count_request_stats_month
(
    stat_month        date NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_stats_month
        PRIMARY KEY (stat_month)
);

COMMIT;

ALTER TABLE IF EXISTS public.total_count_request_stats_hour
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_count_request_stats_day
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_count_request_stats_month
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_request_status_stats_hour
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_request_status_stats_day
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_request_status_stats_month
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_duration_stats_hour
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_duration_stats_day
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.total_duration_stats_month
    ADD COLUMN IF NOT EXISTS anomaly boolean NOT NULL DEFAULT false;

-- =========================================================
-- Source: create_count_request_stats_tabels.sql
-- =========================================================
BEGIN;

-- =========================================
-- Почасовая статистика
-- =========================================
CREATE TABLE IF NOT EXISTS public.count_request_stats_hour
(
    stat_hour         timestamptz NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_stats_hour
        PRIMARY KEY (stat_hour, microservice_id, action_method_id),

    CONSTRAINT fk_method_stats_hour_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_stats_hour_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================
-- Посуточная статистика
-- =========================================
CREATE TABLE IF NOT EXISTS public.count_request_stats_day
(
    stat_day          date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_stats_day
        PRIMARY KEY (stat_day, microservice_id, action_method_id),

    CONSTRAINT fk_method_stats_day_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_stats_day_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================
-- Помесячная статистика
-- =========================================
CREATE TABLE IF NOT EXISTS public.count_request_stats_month
(
    stat_month        date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_stats_month
        PRIMARY KEY (stat_month, microservice_id, action_method_id),

    CONSTRAINT fk_method_stats_month_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_stats_month_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================
-- Индексы
-- =========================================
CREATE INDEX IF NOT EXISTS idx_method_stats_hour_microservice
    ON public.count_request_stats_hour(microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_stats_hour_action_method
    ON public.count_request_stats_hour (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_stats_day_microservice
    ON public.count_request_stats_day (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_stats_day_action_method
    ON public.count_request_stats_day (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_stats_month_microservice
    ON public.count_request_stats_month (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_stats_month_action_method
    ON public.count_request_stats_month (action_method_id);
COMMIT;

-- =========================================================
-- Source: create_total_request_status_stats_tables.sql
-- =========================================================
BEGIN;

-- =========================================
-- Почасовая статистика статусов
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_request_status_stats_hour
(
    stat_hour         timestamptz NOT NULL,
    request_status_id bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_total_request_status_stats_hour
        PRIMARY KEY (stat_hour, request_status_id),

    CONSTRAINT fk_total_request_status_stats_hour_status
        FOREIGN KEY (request_status_id)
        REFERENCES public.request_status (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================
-- Посуточная статистика статусов
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_request_status_stats_day
(
    stat_day          date NOT NULL,
    request_status_id bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_total_request_status_stats_day
        PRIMARY KEY (stat_day, request_status_id),

    CONSTRAINT fk_total_request_status_stats_day_status
        FOREIGN KEY (request_status_id)
        REFERENCES public.request_status (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================
-- Помесячная статистика статусов
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_request_status_stats_month
(
    stat_month        date NOT NULL,
    request_status_id bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_total_request_status_stats_month
        PRIMARY KEY (stat_month, request_status_id),

    CONSTRAINT fk_total_request_status_stats_month_status
        FOREIGN KEY (request_status_id)
        REFERENCES public.request_status (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

COMMIT;

-- =========================================================
-- Source: create_request_status_stats_tables.sql
-- =========================================================
BEGIN;

CREATE TABLE IF NOT EXISTS public.method_request_status_stats_hour
(
    stat_hour         timestamptz NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    request_status_id bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_request_status_stats_hour
        PRIMARY KEY (
            stat_hour,
            microservice_id,
            action_method_id,
            request_status_id
        ),

    CONSTRAINT fk_method_request_status_stats_hour_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_request_status_stats_hour_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_request_status_stats_hour_request_status
        FOREIGN KEY (request_status_id)
        REFERENCES public.request_status (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.method_request_status_stats_day
(
    stat_day          date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    request_status_id bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_request_status_stats_day
        PRIMARY KEY (
            stat_day,
            microservice_id,
            action_method_id,
            request_status_id
        ),

    CONSTRAINT fk_method_request_status_stats_day_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_request_status_stats_day_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_request_status_stats_day_request_status
        FOREIGN KEY (request_status_id)
        REFERENCES public.request_status (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.method_request_status_stats_month
(
    stat_month        date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    request_status_id bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_request_status_stats_month
        PRIMARY KEY (
            stat_month,
            microservice_id,
            action_method_id,
            request_status_id
        ),

    CONSTRAINT fk_method_request_status_stats_month_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_request_status_stats_month_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_request_status_stats_month_request_status
        FOREIGN KEY (request_status_id)
        REFERENCES public.request_status (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_hour_microservice
    ON public.method_request_status_stats_hour (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_hour_action_method
    ON public.method_request_status_stats_hour (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_hour_request_status
    ON public.method_request_status_stats_hour (request_status_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_day_microservice
    ON public.method_request_status_stats_day (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_day_action_method
    ON public.method_request_status_stats_day (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_day_request_status
    ON public.method_request_status_stats_day (request_status_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_month_microservice
    ON public.method_request_status_stats_month (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_month_action_method
    ON public.method_request_status_stats_month (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_request_status_stats_month_request_status
    ON public.method_request_status_stats_month (request_status_id);

COMMIT;

-- =========================================================
-- Source: create_total_duration_stats_hour.sql
-- =========================================================
BEGIN;

-- =========================================
-- Почасовая статистика длительности
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_duration_stats_hour
(
    stat_hour         timestamptz NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    total_duration_ms bigint NOT NULL DEFAULT 0,
    min_duration_ms   integer NOT NULL,
    max_duration_ms   integer NOT NULL,
    avg_duration_ms   numeric(14, 2) NOT NULL,
    avg_predict       NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_total_duration_stats_hour
        PRIMARY KEY (stat_hour)
);

-- =========================================
-- Посуточная статистика длительности
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_duration_stats_day
(
    stat_day          date NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    total_duration_ms bigint NOT NULL DEFAULT 0,
    min_duration_ms   integer NOT NULL,
    max_duration_ms   integer NOT NULL,
    avg_duration_ms   numeric(14, 2) NOT NULL,
    avg_predict       NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_total_duration_stats_day
        PRIMARY KEY (stat_day)
);

-- =========================================
-- Помесячная статистика длительности
-- =========================================
CREATE TABLE IF NOT EXISTS public.total_duration_stats_month
(
    stat_month        date NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
    total_duration_ms bigint NOT NULL DEFAULT 0,
    min_duration_ms   integer NOT NULL,
    max_duration_ms   integer NOT NULL,
    avg_duration_ms   numeric(14, 2) NOT NULL,
    avg_predict       NUMERIC(10, 2) NOT NULL DEFAULT 0,
    anomaly           boolean NOT NULL DEFAULT false,

    CONSTRAINT pk_total_duration_stats_month
        PRIMARY KEY (stat_month)
);

COMMIT;

-- =========================================================
-- Source: create_duration_table.sql
-- =========================================================
BEGIN;

-- =========================================================
-- 1. Почасовая статистика времени выполнения
-- =========================================================
CREATE TABLE IF NOT EXISTS public.method_duration_stats_hour
(
    stat_hour         timestamptz NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,

    request_count     bigint NOT NULL DEFAULT 0,
    total_duration_ms bigint NOT NULL DEFAULT 0,

    min_duration_ms   integer NOT NULL,
    max_duration_ms   integer NOT NULL,
    avg_duration_ms   numeric(14,2) NOT NULL,
	avg_predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_duration_stats_hour
        PRIMARY KEY (stat_hour, microservice_id, action_method_id),

    CONSTRAINT fk_method_duration_stats_hour_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_duration_stats_hour_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================================
-- 2. Посуточная статистика времени выполнения
-- =========================================================
CREATE TABLE IF NOT EXISTS public.method_duration_stats_day
(
    stat_day          date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,

    request_count     bigint NOT NULL DEFAULT 0,
    total_duration_ms bigint NOT NULL DEFAULT 0,

    min_duration_ms   integer NOT NULL,
    max_duration_ms   integer NOT NULL,
    avg_duration_ms   numeric(14,2) NOT NULL,
	avg_predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_duration_stats_day
        PRIMARY KEY (stat_day, microservice_id, action_method_id),

    CONSTRAINT fk_method_duration_stats_day_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_duration_stats_day_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================================
-- 3. Помесячная статистика времени выполнения
-- =========================================================
CREATE TABLE IF NOT EXISTS public.method_duration_stats_month
(
    stat_month        date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,

    request_count     bigint NOT NULL DEFAULT 0,
    total_duration_ms bigint NOT NULL DEFAULT 0,

    min_duration_ms   integer NOT NULL,
    max_duration_ms   integer NOT NULL,
    avg_duration_ms   numeric(14,2) NOT NULL,
	avg_predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_duration_stats_month
        PRIMARY KEY (stat_month, microservice_id, action_method_id),

    CONSTRAINT fk_method_duration_stats_month_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_duration_stats_month_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================================================
-- 4. Индексы
-- =========================================================
CREATE INDEX IF NOT EXISTS idx_method_duration_stats_hour_microservice
    ON public.method_duration_stats_hour (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_duration_stats_hour_action_method
    ON public.method_duration_stats_hour (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_duration_stats_day_microservice
    ON public.method_duration_stats_day (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_duration_stats_day_action_method
    ON public.method_duration_stats_day (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_duration_stats_month_microservice
    ON public.method_duration_stats_month (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_duration_stats_month_action_method
    ON public.method_duration_stats_month (action_method_id);

COMMIT;

-- =========================================================
-- Source: create_log_type_stats_tables.sql
-- =========================================================
BEGIN;

CREATE TABLE IF NOT EXISTS public.method_log_type_stats_hour
(
    stat_hour         timestamptz NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    log_type_id       bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_log_type_stats_hour
        PRIMARY KEY (
            stat_hour,
            microservice_id,
            action_method_id,
            log_type_id
        ),

    CONSTRAINT fk_method_log_type_stats_hour_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_log_type_stats_hour_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_log_type_stats_hour_log_type
        FOREIGN KEY (log_type_id)
        REFERENCES public.log_types (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.method_log_type_stats_day
(
    stat_day          date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    log_type_id       bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_log_type_stats_day
        PRIMARY KEY (
            stat_day,
            microservice_id,
            action_method_id,
            log_type_id
        ),

    CONSTRAINT fk_method_log_type_stats_day_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_log_type_stats_day_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_log_type_stats_day_log_type
        FOREIGN KEY (log_type_id)
        REFERENCES public.log_types (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.method_log_type_stats_month
(
    stat_month        date NOT NULL,
    microservice_id   bigint NOT NULL,
    action_method_id  bigint NOT NULL,
    log_type_id       bigint NOT NULL,
    request_count     bigint NOT NULL DEFAULT 0,
	predict           NUMERIC(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_method_log_type_stats_month
        PRIMARY KEY (
            stat_month,
            microservice_id,
            action_method_id,
            log_type_id
        ),

    CONSTRAINT fk_method_log_type_stats_month_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_log_type_stats_month_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_method_log_type_stats_month_log_type
        FOREIGN KEY (log_type_id)
        REFERENCES public.log_types (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_hour_microservice
    ON public.method_log_type_stats_hour (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_hour_action_method
    ON public.method_log_type_stats_hour (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_hour_log_type
    ON public.method_log_type_stats_hour (log_type_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_day_microservice
    ON public.method_log_type_stats_day (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_day_action_method
    ON public.method_log_type_stats_day (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_day_log_type
    ON public.method_log_type_stats_day (log_type_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_month_microservice
    ON public.method_log_type_stats_month (microservice_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_month_action_method
    ON public.method_log_type_stats_month (action_method_id);

CREATE INDEX IF NOT EXISTS idx_method_log_type_stats_month_log_type
    ON public.method_log_type_stats_month (log_type_id);

COMMIT;

-- =========================================================
-- Source: unique_users_stats_by_methods_solution.sql
-- =========================================================
CREATE TABLE IF NOT EXISTS public.unique_users_stats_hour
(
    id bigserial PRIMARY KEY,
    stat_hour timestamptz NOT NULL UNIQUE,
    unique_users_count integer NOT NULL,
    predict integer
);

CREATE TABLE IF NOT EXISTS public.unique_users_stats_day
(
    id bigserial PRIMARY KEY,
    stat_day date NOT NULL UNIQUE,
    unique_users_count integer NOT NULL,
    predict integer
);

CREATE TABLE IF NOT EXISTS public.unique_users_stats_month
(
    id bigserial PRIMARY KEY,
    stat_month date NOT NULL UNIQUE,
    unique_users_count integer NOT NULL,
    predict integer
);

-- =========================================================
-- 1. Расширяем существующие таблицы: добавляем список пользователей
-- =========================================================

ALTER TABLE public.unique_users_stats_hour
    ADD COLUMN IF NOT EXISTS unique_users text[] NOT NULL DEFAULT ARRAY[]::text[];

ALTER TABLE public.unique_users_stats_day
    ADD COLUMN IF NOT EXISTS unique_users text[] NOT NULL DEFAULT ARRAY[]::text[];

ALTER TABLE public.unique_users_stats_month
    ADD COLUMN IF NOT EXISTS unique_users text[] NOT NULL DEFAULT ARRAY[]::text[];

-- =========================================================
-- 2. Новые таблицы статистики по микросервису и методу
-- =========================================================

CREATE TABLE IF NOT EXISTS public.unique_users_stats_method_hour
(
    id bigserial PRIMARY KEY,
    stat_hour timestamptz NOT NULL,
    microservice_id bigint NOT NULL,
    action_method_id bigint NOT NULL,
    unique_users_count integer NOT NULL,
    unique_users text[] NOT NULL DEFAULT ARRAY[]::text[],
    predict integer,

    CONSTRAINT uq_unique_users_stats_method_hour
        UNIQUE (stat_hour, microservice_id, action_method_id),

    CONSTRAINT fk_unique_users_stats_method_hour_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_unique_users_stats_method_hour_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.unique_users_stats_method_day
(
    id bigserial PRIMARY KEY,
    stat_day date NOT NULL,
    microservice_id bigint NOT NULL,
    action_method_id bigint NOT NULL,
    unique_users_count integer NOT NULL,
    unique_users text[] NOT NULL DEFAULT ARRAY[]::text[],
    predict integer,

    CONSTRAINT uq_unique_users_stats_method_day
        UNIQUE (stat_day, microservice_id, action_method_id),

    CONSTRAINT fk_unique_users_stats_method_day_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_unique_users_stats_method_day_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.unique_users_stats_method_month
(
    id bigserial PRIMARY KEY,
    stat_month date NOT NULL,
    microservice_id bigint NOT NULL,
    action_method_id bigint NOT NULL,
    unique_users_count integer NOT NULL,
    unique_users text[] NOT NULL DEFAULT ARRAY[]::text[],
    predict integer,

    CONSTRAINT uq_unique_users_stats_method_month
        UNIQUE (stat_month, microservice_id, action_method_id),

    CONSTRAINT fk_unique_users_stats_method_month_microservice
        FOREIGN KEY (microservice_id)
        REFERENCES public.microservices (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_unique_users_stats_method_month_action_method
        FOREIGN KEY (action_method_id)
        REFERENCES public.action_methods (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- Индексы для чтения статистики
CREATE INDEX IF NOT EXISTS idx_uus_method_hour_period
    ON public.unique_users_stats_method_hour (stat_hour);

CREATE INDEX IF NOT EXISTS idx_uus_method_day_period
    ON public.unique_users_stats_method_day (stat_day);

CREATE INDEX IF NOT EXISTS idx_uus_method_month_period
    ON public.unique_users_stats_method_month (stat_month);

CREATE INDEX IF NOT EXISTS idx_uus_method_hour_microservice_action
    ON public.unique_users_stats_method_hour (microservice_id, action_method_id);

CREATE INDEX IF NOT EXISTS idx_uus_method_day_microservice_action
    ON public.unique_users_stats_method_day (microservice_id, action_method_id);

CREATE INDEX IF NOT EXISTS idx_uus_method_month_microservice_action
    ON public.unique_users_stats_method_month (microservice_id, action_method_id);

-- Полезный индекс для процедуры агрегации
CREATE INDEX IF NOT EXISTS idx_logs_stats_users_period_method
    ON public.logs (log_date, microservice_id, action_method_id, username)
    WHERE username IS NOT NULL;

-- =========================================================
-- 3. Обновлённая процедура инкрементального заполнения
-- =========================================================

CREATE OR REPLACE PROCEDURE public.fill_unique_users_stats_incremental(
    IN p_run_time timestamptz DEFAULT now()
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start  timestamptz;
    v_hour_end    timestamptz;

    v_day_start   date;
    v_day_end     date;

    v_month_start date;
    v_month_end   date;
BEGIN
    v_hour_end := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';

    v_day_start := date_trunc('day', v_hour_start)::date;
    v_day_end := v_day_start + 1;

    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    -- =====================================================
    -- Общая статистика за прошедший час
    -- =====================================================
    INSERT INTO public.unique_users_stats_hour
    (
        stat_hour,
        unique_users_count,
        unique_users,
        predict
    )
    SELECT
        v_hour_start,
        COUNT(DISTINCT l.username)::integer,
        COALESCE(
            ARRAY_AGG(DISTINCT l.username ORDER BY l.username)
                FILTER (WHERE l.username IS NOT NULL),
            ARRAY[]::text[]
        ),
        NULL
    FROM public.logs l
    WHERE l.log_date >= v_hour_start
      AND l.log_date < v_hour_end
      AND l.log_type_id = 1
      AND l.username IS NOT NULL
    ON CONFLICT (stat_hour)
    DO UPDATE SET
        unique_users_count = EXCLUDED.unique_users_count,
        unique_users = EXCLUDED.unique_users;

    -- =====================================================
    -- Общая статистика за день, к которому относится час
    -- =====================================================
    INSERT INTO public.unique_users_stats_day
    (
        stat_day,
        unique_users_count,
        unique_users,
        predict
    )
    SELECT
        v_day_start,
        COUNT(DISTINCT l.username)::integer,
        COALESCE(
            ARRAY_AGG(DISTINCT l.username ORDER BY l.username)
                FILTER (WHERE l.username IS NOT NULL),
            ARRAY[]::text[]
        ),
        NULL
    FROM public.logs l
    WHERE l.log_date >= v_day_start::timestamptz
      AND l.log_date < v_day_end::timestamptz
      AND l.log_type_id = 1
      AND l.username IS NOT NULL
    ON CONFLICT (stat_day)
    DO UPDATE SET
        unique_users_count = EXCLUDED.unique_users_count,
        unique_users = EXCLUDED.unique_users;

    -- =====================================================
    -- Общая статистика за месяц, к которому относится час
    -- =====================================================
    INSERT INTO public.unique_users_stats_month
    (
        stat_month,
        unique_users_count,
        unique_users,
        predict
    )
    SELECT
        v_month_start,
        COUNT(DISTINCT l.username)::integer,
        COALESCE(
            ARRAY_AGG(DISTINCT l.username ORDER BY l.username)
                FILTER (WHERE l.username IS NOT NULL),
            ARRAY[]::text[]
        ),
        NULL
    FROM public.logs l
    WHERE l.log_date >= v_month_start::timestamptz
      AND l.log_date < v_month_end::timestamptz
      AND l.log_type_id = 1
      AND l.username IS NOT NULL
    ON CONFLICT (stat_month)
    DO UPDATE SET
        unique_users_count = EXCLUDED.unique_users_count,
        unique_users = EXCLUDED.unique_users;

    -- =====================================================
    -- Статистика за час по микросервису и методу
    -- =====================================================
    DELETE FROM public.unique_users_stats_method_hour
    WHERE stat_hour = v_hour_start;

    INSERT INTO public.unique_users_stats_method_hour
    (
        stat_hour,
        microservice_id,
        action_method_id,
        unique_users_count,
        unique_users,
        predict
    )
    SELECT
        v_hour_start,
        l.microservice_id,
        l.action_method_id,
        COUNT(DISTINCT l.username)::integer,
        ARRAY_AGG(DISTINCT l.username ORDER BY l.username),
        NULL
    FROM public.logs l
    WHERE l.log_date >= v_hour_start
      AND l.log_date < v_hour_end
      AND l.log_type_id = 1
      AND l.username IS NOT NULL
    GROUP BY
        l.microservice_id,
        l.action_method_id;

    -- =====================================================
    -- Статистика за день по микросервису и методу
    -- =====================================================
    DELETE FROM public.unique_users_stats_method_day
    WHERE stat_day = v_day_start;

    INSERT INTO public.unique_users_stats_method_day
    (
        stat_day,
        microservice_id,
        action_method_id,
        unique_users_count,
        unique_users,
        predict
    )
    SELECT
        v_day_start,
        l.microservice_id,
        l.action_method_id,
        COUNT(DISTINCT l.username)::integer,
        ARRAY_AGG(DISTINCT l.username ORDER BY l.username),
        NULL
    FROM public.logs l
    WHERE l.log_date >= v_day_start::timestamptz
      AND l.log_date < v_day_end::timestamptz
      AND l.log_type_id = 1
      AND l.username IS NOT NULL
    GROUP BY
        l.microservice_id,
        l.action_method_id;

    -- =====================================================
    -- Статистика за месяц по микросервису и методу
    -- =====================================================
    DELETE FROM public.unique_users_stats_method_month
    WHERE stat_month = v_month_start;

    INSERT INTO public.unique_users_stats_method_month
    (
        stat_month,
        microservice_id,
        action_method_id,
        unique_users_count,
        unique_users,
        predict
    )
    SELECT
        v_month_start,
        l.microservice_id,
        l.action_method_id,
        COUNT(DISTINCT l.username)::integer,
        ARRAY_AGG(DISTINCT l.username ORDER BY l.username),
        NULL
    FROM public.logs l
    WHERE l.log_date >= v_month_start::timestamptz
      AND l.log_date < v_month_end::timestamptz
      AND l.log_type_id = 1
      AND l.username IS NOT NULL
    GROUP BY
        l.microservice_id,
        l.action_method_id;
END;
$$;

ALTER TABLE IF EXISTS public.total_count_request_stats_hour
    ALTER COLUMN request_count DROP NOT NULL;
ALTER TABLE IF EXISTS public.total_count_request_stats_day
    ALTER COLUMN request_count DROP NOT NULL;
ALTER TABLE IF EXISTS public.total_count_request_stats_month
    ALTER COLUMN request_count DROP NOT NULL;

ALTER TABLE IF EXISTS public.total_request_status_stats_hour
    ALTER COLUMN request_count DROP NOT NULL;
ALTER TABLE IF EXISTS public.total_request_status_stats_day
    ALTER COLUMN request_count DROP NOT NULL;
ALTER TABLE IF EXISTS public.total_request_status_stats_month
    ALTER COLUMN request_count DROP NOT NULL;

ALTER TABLE IF EXISTS public.total_duration_stats_hour
    ALTER COLUMN request_count DROP NOT NULL,
    ALTER COLUMN total_duration_ms DROP NOT NULL,
    ALTER COLUMN min_duration_ms DROP NOT NULL,
    ALTER COLUMN max_duration_ms DROP NOT NULL,
    ALTER COLUMN avg_duration_ms DROP NOT NULL;
ALTER TABLE IF EXISTS public.total_duration_stats_day
    ALTER COLUMN request_count DROP NOT NULL,
    ALTER COLUMN total_duration_ms DROP NOT NULL,
    ALTER COLUMN min_duration_ms DROP NOT NULL,
    ALTER COLUMN max_duration_ms DROP NOT NULL,
    ALTER COLUMN avg_duration_ms DROP NOT NULL;
ALTER TABLE IF EXISTS public.total_duration_stats_month
    ALTER COLUMN request_count DROP NOT NULL,
    ALTER COLUMN total_duration_ms DROP NOT NULL,
    ALTER COLUMN min_duration_ms DROP NOT NULL,
    ALTER COLUMN max_duration_ms DROP NOT NULL,
    ALTER COLUMN avg_duration_ms DROP NOT NULL;

ALTER TABLE IF EXISTS public.unique_users_stats_hour
    ALTER COLUMN unique_users_count DROP NOT NULL;
ALTER TABLE IF EXISTS public.unique_users_stats_day
    ALTER COLUMN unique_users_count DROP NOT NULL;
ALTER TABLE IF EXISTS public.unique_users_stats_month
    ALTER COLUMN unique_users_count DROP NOT NULL;

-- =========================================================
-- 4. Примеры выборок
-- =========================================================

-- Статистика по методам за день с расшифровкой названий
-- SELECT
--     s.stat_day,
--     m.microservice_name,
--     am.action_eng,
--     am.action_rus,
--     s.unique_users_count,
--     s.unique_users
-- FROM public.unique_users_stats_method_day s
-- JOIN public.microservices m ON m.id = s.microservice_id
-- JOIN public.action_methods am ON am.id = s.action_method_id
-- ORDER BY s.stat_day DESC, m.microservice_name, am.action_eng;

-- Запуск процедуры вручную
-- CALL public.fill_unique_users_stats_incremental(now());


-- =========================================================
-- Source: procedure_fill_total_count_request_stats.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.fill_total_count_request_stats_incremental(
    IN p_run_time timestamptz DEFAULT now()
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start   timestamptz;
    v_hour_end     timestamptz;
    v_day_start    date;
    v_day_end      date;
    v_month_start  date;
    v_month_end    date;
BEGIN
    v_hour_end   := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';
    v_day_start   := date_trunc('day', v_hour_start)::date;
    v_day_end     := (v_day_start + 1)::date;
    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end   := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    INSERT INTO public.total_count_request_stats_hour (stat_hour, request_count)
    SELECT v_hour_start, COUNT(*)
    FROM public.logs l
    WHERE l.log_date >= v_hour_start
      AND l.log_date <  v_hour_end
      AND l.log_type_id = 1
    ON CONFLICT (stat_hour) DO UPDATE SET
        request_count = EXCLUDED.request_count;

    INSERT INTO public.total_count_request_stats_day (stat_day, request_count)
    SELECT v_day_start, COALESCE(SUM(h.request_count), 0)
    FROM public.total_count_request_stats_hour h
    WHERE h.stat_hour >= v_day_start::timestamptz
      AND h.stat_hour <  v_day_end::timestamptz
    ON CONFLICT (stat_day) DO UPDATE SET
        request_count = EXCLUDED.request_count;

    INSERT INTO public.total_count_request_stats_month (stat_month, request_count)
    SELECT v_month_start, COALESCE(SUM(h.request_count), 0)
    FROM public.total_count_request_stats_hour h
    WHERE h.stat_hour >= v_month_start::timestamptz
      AND h.stat_hour <  v_month_end::timestamptz
    ON CONFLICT (stat_month) DO UPDATE SET
        request_count = EXCLUDED.request_count;
END;
$$;

-- =========================================================
-- Source: procedure_fill_count_request_stats_incremental.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.fill_count_request_stats_incremental(IN p_run_time timestamptz DEFAULT now())
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start timestamptz; v_hour_end timestamptz; v_day_start date; v_day_end date; v_month_start date; v_month_end date;
BEGIN
    v_hour_end := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';
    v_day_start := date_trunc('day', v_hour_start)::date;
    v_day_end := v_day_start + 1;
    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    INSERT INTO public.count_request_stats_hour (stat_hour, microservice_id, action_method_id, request_count)
    SELECT v_hour_start, l.microservice_id, l.action_method_id, COUNT(*)
    FROM public.logs l
    WHERE l.log_date >= v_hour_start AND l.log_date < v_hour_end AND l.log_type_id = 1
    GROUP BY l.microservice_id, l.action_method_id
    ON CONFLICT (stat_hour, microservice_id, action_method_id) DO UPDATE SET request_count = EXCLUDED.request_count;

    INSERT INTO public.count_request_stats_day (stat_day, microservice_id, action_method_id, request_count)
    SELECT v_day_start, h.microservice_id, h.action_method_id, SUM(h.request_count)
    FROM public.count_request_stats_hour h
    WHERE h.stat_hour >= v_day_start::timestamptz AND h.stat_hour < v_day_end::timestamptz
    GROUP BY h.microservice_id, h.action_method_id
    ON CONFLICT (stat_day, microservice_id, action_method_id) DO UPDATE SET request_count = EXCLUDED.request_count;

    INSERT INTO public.count_request_stats_month (stat_month, microservice_id, action_method_id, request_count)
    SELECT v_month_start, h.microservice_id, h.action_method_id, SUM(h.request_count)
    FROM public.count_request_stats_hour h
    WHERE h.stat_hour >= v_month_start::timestamptz AND h.stat_hour < v_month_end::timestamptz
    GROUP BY h.microservice_id, h.action_method_id
    ON CONFLICT (stat_month, microservice_id, action_method_id) DO UPDATE SET request_count = EXCLUDED.request_count;
END;
$$;

-- =========================================================
-- Source: procedure_fill_total_request_status_stats_incremental.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.fill_total_request_status_stats_incremental(IN p_run_time timestamptz DEFAULT now())
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start timestamptz; v_hour_end timestamptz; v_day_start date; v_day_end date; v_month_start date; v_month_end date;
BEGIN
    v_hour_end := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';
    v_day_start := date_trunc('day', v_hour_start)::date;
    v_day_end := v_day_start + 1;
    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    INSERT INTO public.total_request_status_stats_hour (stat_hour, request_status_id, request_count)
    SELECT v_hour_start, l.request_status_id, COUNT(*)
    FROM public.logs l
    WHERE l.log_type_id = 2 AND l.log_date >= v_hour_start AND l.log_date < v_hour_end
    GROUP BY l.request_status_id
    ON CONFLICT (stat_hour, request_status_id) DO UPDATE SET request_count = EXCLUDED.request_count;

    INSERT INTO public.total_request_status_stats_day (stat_day, request_status_id, request_count)
    SELECT v_day_start, h.request_status_id, SUM(h.request_count)
    FROM public.total_request_status_stats_hour h
    WHERE h.stat_hour >= v_day_start::timestamptz AND h.stat_hour < v_day_end::timestamptz
    GROUP BY h.request_status_id
    ON CONFLICT (stat_day, request_status_id) DO UPDATE SET request_count = EXCLUDED.request_count;

    INSERT INTO public.total_request_status_stats_month (stat_month, request_status_id, request_count)
    SELECT v_month_start, h.request_status_id, SUM(h.request_count)
    FROM public.total_request_status_stats_hour h
    WHERE h.stat_hour >= v_month_start::timestamptz AND h.stat_hour < v_month_end::timestamptz
    GROUP BY h.request_status_id
    ON CONFLICT (stat_month, request_status_id) DO UPDATE SET request_count = EXCLUDED.request_count;
END;
$$;

-- =========================================================
-- Source: procedure_fill_method_request_status_stats.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.fill_method_request_status_stats_incremental(IN p_run_time timestamptz DEFAULT now())
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start timestamptz; v_hour_end timestamptz; v_day_start date; v_day_end date; v_month_start date; v_month_end date;
BEGIN
    v_hour_end := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';
    v_day_start := date_trunc('day', v_hour_start)::date;
    v_day_end := v_day_start + 1;
    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    INSERT INTO public.method_request_status_stats_hour (stat_hour, microservice_id, action_method_id, request_status_id, request_count)
    SELECT v_hour_start, l.microservice_id, l.action_method_id, l.request_status_id, COUNT(*)
    FROM public.logs l
    WHERE l.log_type_id = 2 AND l.log_date >= v_hour_start AND l.log_date < v_hour_end
    GROUP BY l.microservice_id, l.action_method_id, l.request_status_id
    ON CONFLICT (stat_hour, microservice_id, action_method_id, request_status_id) DO UPDATE SET request_count = EXCLUDED.request_count;

    INSERT INTO public.method_request_status_stats_day (stat_day, microservice_id, action_method_id, request_status_id, request_count)
    SELECT v_day_start, h.microservice_id, h.action_method_id, h.request_status_id, SUM(h.request_count)
    FROM public.method_request_status_stats_hour h
    WHERE h.stat_hour >= v_day_start::timestamptz AND h.stat_hour < v_day_end::timestamptz
    GROUP BY h.microservice_id, h.action_method_id, h.request_status_id
    ON CONFLICT (stat_day, microservice_id, action_method_id, request_status_id) DO UPDATE SET request_count = EXCLUDED.request_count;

    INSERT INTO public.method_request_status_stats_month (stat_month, microservice_id, action_method_id, request_status_id, request_count)
    SELECT v_month_start, h.microservice_id, h.action_method_id, h.request_status_id, SUM(h.request_count)
    FROM public.method_request_status_stats_hour h
    WHERE h.stat_hour >= v_month_start::timestamptz AND h.stat_hour < v_month_end::timestamptz
    GROUP BY h.microservice_id, h.action_method_id, h.request_status_id
    ON CONFLICT (stat_month, microservice_id, action_method_id, request_status_id) DO UPDATE SET request_count = EXCLUDED.request_count;
END;
$$;

-- =========================================================
-- Source: procedure_fill_total_duration_stats_incremental.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.fill_total_duration_stats_incremental(IN p_run_time timestamptz DEFAULT now())
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start timestamptz; v_hour_end timestamptz; v_day_start date; v_day_end date; v_month_start date; v_month_end date;
BEGIN
    v_hour_end := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';
    v_day_start := date_trunc('day', v_hour_start)::date;
    v_day_end := v_day_start + 1;
    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    INSERT INTO public.total_duration_stats_hour (stat_hour, request_count, total_duration_ms, min_duration_ms, max_duration_ms, avg_duration_ms)
    SELECT v_hour_start, COUNT(*), SUM(l.duration)::bigint, MIN(l.duration), MAX(l.duration), ROUND(AVG(l.duration)::numeric, 2)
    FROM public.logs l
    WHERE l.log_type_id = 2 AND l.duration > 0 AND l.log_date >= v_hour_start AND l.log_date < v_hour_end
    HAVING COUNT(*) > 0
    ON CONFLICT (stat_hour) DO UPDATE SET
        request_count = EXCLUDED.request_count,
        total_duration_ms = EXCLUDED.total_duration_ms,
        min_duration_ms = EXCLUDED.min_duration_ms,
        max_duration_ms = EXCLUDED.max_duration_ms,
        avg_duration_ms = EXCLUDED.avg_duration_ms;

    INSERT INTO public.total_duration_stats_day (stat_day, request_count, total_duration_ms, min_duration_ms, max_duration_ms, avg_duration_ms)
    SELECT v_day_start, SUM(h.request_count), SUM(h.total_duration_ms), MIN(h.min_duration_ms), MAX(h.max_duration_ms), ROUND(SUM(h.total_duration_ms)::numeric / NULLIF(SUM(h.request_count), 0), 2)
    FROM public.total_duration_stats_hour h
    WHERE h.stat_hour >= v_day_start::timestamptz AND h.stat_hour < v_day_end::timestamptz
    HAVING COUNT(*) > 0
    ON CONFLICT (stat_day) DO UPDATE SET
        request_count = EXCLUDED.request_count,
        total_duration_ms = EXCLUDED.total_duration_ms,
        min_duration_ms = EXCLUDED.min_duration_ms,
        max_duration_ms = EXCLUDED.max_duration_ms,
        avg_duration_ms = EXCLUDED.avg_duration_ms;

    INSERT INTO public.total_duration_stats_month (stat_month, request_count, total_duration_ms, min_duration_ms, max_duration_ms, avg_duration_ms)
    SELECT v_month_start, SUM(h.request_count), SUM(h.total_duration_ms), MIN(h.min_duration_ms), MAX(h.max_duration_ms), ROUND(SUM(h.total_duration_ms)::numeric / NULLIF(SUM(h.request_count), 0), 2)
    FROM public.total_duration_stats_hour h
    WHERE h.stat_hour >= v_month_start::timestamptz AND h.stat_hour < v_month_end::timestamptz
    HAVING COUNT(*) > 0
    ON CONFLICT (stat_month) DO UPDATE SET
        request_count = EXCLUDED.request_count,
        total_duration_ms = EXCLUDED.total_duration_ms,
        min_duration_ms = EXCLUDED.min_duration_ms,
        max_duration_ms = EXCLUDED.max_duration_ms,
        avg_duration_ms = EXCLUDED.avg_duration_ms;
END;
$$;

-- =========================================================
-- Source: procedure_fill_duration_tables.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.fill_method_duration_stats_incremental(IN p_run_time timestamptz DEFAULT now())
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start timestamptz; v_hour_end timestamptz; v_day_start date; v_day_end date; v_month_start date; v_month_end date;
BEGIN
    v_hour_end := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';
    v_day_start := date_trunc('day', v_hour_start)::date;
    v_day_end := v_day_start + 1;
    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    INSERT INTO public.method_duration_stats_hour (stat_hour, microservice_id, action_method_id, request_count, total_duration_ms, min_duration_ms, max_duration_ms, avg_duration_ms)
    SELECT v_hour_start, l.microservice_id, l.action_method_id, COUNT(*), SUM(l.duration)::bigint, MIN(l.duration), MAX(l.duration), ROUND(AVG(l.duration)::numeric, 2)
    FROM public.logs l
    WHERE l.log_type_id = 2 AND l.duration > 0 AND l.log_date >= v_hour_start AND l.log_date < v_hour_end
    GROUP BY l.microservice_id, l.action_method_id
    ON CONFLICT (stat_hour, microservice_id, action_method_id) DO UPDATE SET
        request_count = EXCLUDED.request_count,
        total_duration_ms = EXCLUDED.total_duration_ms,
        min_duration_ms = EXCLUDED.min_duration_ms,
        max_duration_ms = EXCLUDED.max_duration_ms,
        avg_duration_ms = EXCLUDED.avg_duration_ms;

    INSERT INTO public.method_duration_stats_day (stat_day, microservice_id, action_method_id, request_count, total_duration_ms, min_duration_ms, max_duration_ms, avg_duration_ms)
    SELECT v_day_start, h.microservice_id, h.action_method_id, SUM(h.request_count), SUM(h.total_duration_ms), MIN(h.min_duration_ms), MAX(h.max_duration_ms), ROUND(SUM(h.total_duration_ms)::numeric / NULLIF(SUM(h.request_count), 0), 2)
    FROM public.method_duration_stats_hour h
    WHERE h.stat_hour >= v_day_start::timestamptz AND h.stat_hour < v_day_end::timestamptz
    GROUP BY h.microservice_id, h.action_method_id
    ON CONFLICT (stat_day, microservice_id, action_method_id) DO UPDATE SET
        request_count = EXCLUDED.request_count,
        total_duration_ms = EXCLUDED.total_duration_ms,
        min_duration_ms = EXCLUDED.min_duration_ms,
        max_duration_ms = EXCLUDED.max_duration_ms,
        avg_duration_ms = EXCLUDED.avg_duration_ms;

    INSERT INTO public.method_duration_stats_month (stat_month, microservice_id, action_method_id, request_count, total_duration_ms, min_duration_ms, max_duration_ms, avg_duration_ms)
    SELECT v_month_start, h.microservice_id, h.action_method_id, SUM(h.request_count), SUM(h.total_duration_ms), MIN(h.min_duration_ms), MAX(h.max_duration_ms), ROUND(SUM(h.total_duration_ms)::numeric / NULLIF(SUM(h.request_count), 0), 2)
    FROM public.method_duration_stats_hour h
    WHERE h.stat_hour >= v_month_start::timestamptz AND h.stat_hour < v_month_end::timestamptz
    GROUP BY h.microservice_id, h.action_method_id
    ON CONFLICT (stat_month, microservice_id, action_method_id) DO UPDATE SET
        request_count = EXCLUDED.request_count,
        total_duration_ms = EXCLUDED.total_duration_ms,
        min_duration_ms = EXCLUDED.min_duration_ms,
        max_duration_ms = EXCLUDED.max_duration_ms,
        avg_duration_ms = EXCLUDED.avg_duration_ms;
END;
$$;

-- =========================================================
-- Source: procedure_fill_method_log_type_stats.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.fill_method_log_type_stats_incremental(
    IN p_run_time timestamptz DEFAULT now()
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_hour_start   timestamptz;
    v_hour_end     timestamptz;
    v_day_start    date;
    v_day_end      date;
    v_month_start  date;
    v_month_end    date;
BEGIN
    v_hour_end   := date_trunc('hour', p_run_time);
    v_hour_start := v_hour_end - interval '1 hour';

    v_day_start   := date_trunc('day', v_hour_start)::date;
    v_day_end     := v_day_start + 1;

    v_month_start := date_trunc('month', v_hour_start)::date;
    v_month_end   := (date_trunc('month', v_hour_start) + interval '1 month')::date;

    -- Почасовая
    DELETE FROM public.method_log_type_stats_hour
    WHERE stat_hour = v_hour_start;

    INSERT INTO public.method_log_type_stats_hour (
        stat_hour,
        microservice_id,
        action_method_id,
        log_type_id,
        request_count
    )
    SELECT
        v_hour_start,
        l.microservice_id,
        l.action_method_id,
        l.log_type_id,
        COUNT(DISTINCT l.correlation_id) AS request_count
    FROM public.logs l
    JOIN public.log_types lt
        ON lt.id = l.log_type_id
    WHERE lt.log_type_name IN ('finish', 'Завершен', 'Без ответа')
      AND l.log_date >= v_hour_start
      AND l.log_date <  v_hour_end
    GROUP BY
        l.microservice_id,
        l.action_method_id,
        l.log_type_id;

    -- Суточная
    DELETE FROM public.method_log_type_stats_day
    WHERE stat_day = v_day_start;

    INSERT INTO public.method_log_type_stats_day (
        stat_day,
        microservice_id,
        action_method_id,
        log_type_id,
        request_count
    )
    SELECT
        v_day_start,
        h.microservice_id,
        h.action_method_id,
        h.log_type_id,
        SUM(h.request_count)
    FROM public.method_log_type_stats_hour h
    WHERE h.stat_hour >= v_day_start::timestamptz
      AND h.stat_hour <  v_day_end::timestamptz
    GROUP BY
        h.microservice_id,
        h.action_method_id,
        h.log_type_id;

    -- Помесячная
    DELETE FROM public.method_log_type_stats_month
    WHERE stat_month = v_month_start;

    INSERT INTO public.method_log_type_stats_month (
        stat_month,
        microservice_id,
        action_method_id,
        log_type_id,
        request_count
    )
    SELECT
        v_month_start,
        h.microservice_id,
        h.action_method_id,
        h.log_type_id,
        SUM(h.request_count)
    FROM public.method_log_type_stats_hour h
    WHERE h.stat_hour >= v_month_start::timestamptz
      AND h.stat_hour <  v_month_end::timestamptz
    GROUP BY
        h.microservice_id,
        h.action_method_id,
        h.log_type_id;

END;
$$;

