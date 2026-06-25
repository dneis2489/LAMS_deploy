-- =========================================================
-- Source: func_create_logs_partition.sql
-- =========================================================
CREATE OR REPLACE FUNCTION public.create_logs_partition(p_date date)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
    v_from date;
    v_to date;
    v_partition_name text;
BEGIN
    v_from := date_trunc('month', p_date)::date;
    v_to := (v_from + interval '1 month')::date;

    v_partition_name := 'logs_' || to_char(v_from, 'YYYY_MM');

    IF NOT EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relname = v_partition_name
          AND n.nspname = 'public'
    ) THEN
        EXECUTE format(
            'CREATE TABLE public.%I PARTITION OF public.logs
             FOR VALUES FROM (%L) TO (%L)',
            v_partition_name,
            v_from,
            v_to
        );
    END IF;
END;
$$;

-- =========================================================
-- Source: procedure_ensure_logs_partitions.sql
-- =========================================================
CREATE OR REPLACE PROCEDURE public.ensure_logs_partitions(p_months_ahead integer DEFAULT 2)
LANGUAGE plpgsql
AS $$
DECLARE
    i integer;
    v_base date;
BEGIN
    v_base := date_trunc('month', current_date)::date;

    FOR i IN 0..p_months_ahead LOOP
        PERFORM public.create_logs_partition(
            (v_base + (i || ' months')::interval)::date
        );
    END LOOP;
END;
$$;

-- =========================================================
-- Source: function_ingest_kafka_log.sql
-- =========================================================
CREATE OR REPLACE FUNCTION public.ingest_kafka_log(
    p_correlation_id uuid,
    p_microservice_name varchar,
    p_action_eng varchar,
    p_action_rus varchar,
    p_request_status_code integer,
    p_log_date timestamptz,
    p_log_type_name varchar,
    p_log_data jsonb,
    p_username varchar DEFAULT NULL,
    p_duration integer DEFAULT NULL
)
RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
    v_log_id bigint;
    v_microservice_id bigint;
    v_action_method_id bigint;
    v_request_status_id bigint;
    v_log_type_id bigint;
    v_log_date timestamptz;
    v_has_same_correlation_id boolean;
BEGIN
    IF p_correlation_id IS NULL THEN
        RAISE EXCEPTION 'correlation_id is required';
    END IF;

    IF p_microservice_name IS NULL OR btrim(p_microservice_name) = '' THEN
        RAISE EXCEPTION 'microservice_name is required';
    END IF;

    IF p_action_eng IS NULL OR btrim(p_action_eng) = '' THEN
        RAISE EXCEPTION 'action_eng is required';
    END IF;

    v_log_date := COALESCE(p_log_date, now());

    IF to_regprocedure('public.create_logs_partition(date)') IS NOT NULL THEN
        PERFORM public.create_logs_partition(v_log_date::date);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM public.log_types WHERE id = 1) THEN
        INSERT INTO public.log_types (id, log_type_name) OVERRIDING SYSTEM VALUE
        VALUES (1, 'start')
        ON CONFLICT DO NOTHING;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM public.log_types WHERE id = 2) THEN
        INSERT INTO public.log_types (id, log_type_name) OVERRIDING SYSTEM VALUE
        VALUES (2, 'finish')
        ON CONFLICT DO NOTHING;
    END IF;

    INSERT INTO public.microservices (microservice_name)
    VALUES (btrim(p_microservice_name))
    ON CONFLICT (microservice_name) DO UPDATE
        SET microservice_name = EXCLUDED.microservice_name
    RETURNING id INTO v_microservice_id;

    INSERT INTO public.action_methods (microservice_id, action_eng, action_rus)
    VALUES (
        v_microservice_id,
        btrim(p_action_eng),
        COALESCE(NULLIF(btrim(p_action_rus), ''), btrim(p_action_eng))
    )
    ON CONFLICT (microservice_id, action_eng) DO UPDATE
        SET action_rus = EXCLUDED.action_rus
    RETURNING id INTO v_action_method_id;

    IF p_request_status_code IS NOT NULL THEN
        INSERT INTO public.request_status (request_status_code)
        VALUES (p_request_status_code)
        ON CONFLICT (request_status_code) DO UPDATE
            SET request_status_code = EXCLUDED.request_status_code
        RETURNING id INTO v_request_status_id;
    END IF;

    SELECT EXISTS (
        SELECT 1
        FROM public.logs
        WHERE correlation_id = p_correlation_id
        LIMIT 1
    ) INTO v_has_same_correlation_id;

    IF v_has_same_correlation_id THEN
        v_log_type_id := 2;
    ELSE
        SELECT id
        INTO v_log_type_id
        FROM public.log_types
        WHERE log_type_name = COALESCE(NULLIF(btrim(p_log_type_name), ''), 'start')
        ORDER BY id
        LIMIT 1;

        IF v_log_type_id IS NULL THEN
            INSERT INTO public.log_types (log_type_name)
            VALUES (COALESCE(NULLIF(btrim(p_log_type_name), ''), 'start'))
            ON CONFLICT (log_type_name) DO UPDATE
                SET log_type_name = EXCLUDED.log_type_name
            RETURNING id INTO v_log_type_id;
        END IF;
    END IF;

    INSERT INTO public.logs (
        correlation_id,
        microservice_id,
        action_method_id,
        username,
        request_status_id,
        log_date,
        log_type_id,
        log_data,
        duration
    )
    VALUES (
        p_correlation_id,
        v_microservice_id,
        v_action_method_id,
        NULLIF(btrim(p_username), ''),
        v_request_status_id,
        v_log_date,
        v_log_type_id,
        COALESCE(p_log_data, '{}'::jsonb),
        p_duration
    )
    RETURNING id INTO v_log_id;

    IF p_request_status_code >= 400
        AND to_regclass('public.notification_categories') IS NOT NULL
        AND to_regclass('public.notification_outbox') IS NOT NULL THEN
        INSERT INTO public.notification_categories (code, title)
        VALUES ('ERROR_LOG_RECEIVED', 'Пришел лог с ошибкой')
        ON CONFLICT (code) DO UPDATE
            SET title = EXCLUDED.title;

        INSERT INTO public.notification_outbox(category_id, subject, body)
        SELECT
            nc.id,
            'LAMS: пришел лог с ошибкой',
            'Получен лог с HTTP-статусом ' || p_request_status_code || E'\n'
                || 'Correlation ID: ' || p_correlation_id || E'\n'
                || 'Микросервис: ' || p_microservice_name || E'\n'
                || 'Событие: ' || p_action_eng || E'\n'
                || 'Дата лога: ' || v_log_date
        FROM public.notification_categories nc
        WHERE nc.code = 'ERROR_LOG_RECEIVED';
    END IF;

    RETURN v_log_id;
END;
$$;


-- =========================================================
-- Source: call_create_N_partition.sql
-- =========================================================
CALL public.ensure_logs_partitions(2);

