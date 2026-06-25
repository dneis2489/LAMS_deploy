const { randomUUID } = require("crypto");
const { Kafka, logLevel } = require("kafkajs");

const config = {
  brokers: envList("KAFKA_BROKERS", "localhost:9092"),
  topic: process.env.KAFKA_TOPIC || "lams.logs",
  clientId: process.env.KAFKA_CLIENT_ID || "lams-log-generator",
  mode: process.env.GENERATOR_MODE || "continuous",
  intervalMs: envInt("GENERATOR_INTERVAL_MS", 1000),
  burstSize: envInt("GENERATOR_BURST_SIZE", 1),
  startLeadMs: envInt("GENERATOR_START_LEAD_MS", 50)
};

const microservices = [
  {
    name: "auth-service",
    actions: [
      ["login", "Вход пользователя"],
      ["refreshToken", "Обновление токена"],
      ["logout", "Выход пользователя"]
    ]
  },
  {
    name: "orders-service",
    actions: [
      ["createOrder", "Создание заказа"],
      ["getOrder", "Получение заказа"],
      ["cancelOrder", "Отмена заказа"]
    ]
  },
  {
    name: "payment-service",
    actions: [
      ["authorizePayment", "Авторизация платежа"],
      ["capturePayment", "Списание платежа"],
      ["refundPayment", "Возврат платежа"]
    ]
  },
  {
    name: "notification-service",
    actions: [
      ["sendEmail", "Отправка email"],
      ["sendTelegram", "Отправка Telegram"],
      ["sendPush", "Отправка push"]
    ]
  }
];

const users = [
  "admin",
  "ivan.petrov",
  "anna.smirnova",
  "sergey.ivanov",
  "maria.kuznetsova",
  "guest"
];

const urls = [
  "/api/auth/login",
  "/api/orders",
  "/api/orders/{id}",
  "/api/payments/authorize",
  "/api/notifications/send"
];

const userAgents = [
  "Mozilla/5.0 Chrome/125.0",
  "Mozilla/5.0 Firefox/126.0",
  "PostmanRuntime/7.39.0",
  "LAMS-Mobile/1.0"
];

const kafka = new Kafka({
  clientId: config.clientId,
  brokers: config.brokers,
  logLevel: logLevel.INFO
});

const producer = kafka.producer();

async function main() {
  console.log(
    `LAMS log generator: brokers=${config.brokers.join(",")} topic=${config.topic} mode=${config.mode}`
  );

  await producer.connect();

  if (config.mode === "once") {
    await sendBurst();
    await producer.disconnect();
    return;
  }

  let stopped = false;
  process.on("SIGINT", () => {
    stopped = true;
  });
  process.on("SIGTERM", () => {
    stopped = true;
  });

  while (!stopped) {
    await sendBurst();
    await sleep(config.intervalMs);
  }

  await producer.disconnect();
}

async function sendBurst() {
  for (let index = 0; index < config.burstSize; index += 1) {
    const request = buildRequest();
    await producer.send({
      topic: config.topic,
      messages: [
        {
          key: request.correlationId,
          value: JSON.stringify(buildStartLog(request))
        },
        {
          key: request.correlationId,
          value: JSON.stringify(buildFinishLog(request))
        }
      ]
    });

    console.log(
      `${request.correlationId} ${request.microservice.name}.${request.action[0]} ${request.statusCode} ${request.duration}ms`
    );
  }
}

function buildRequest() {
  const microservice = pick(microservices);
  const action = pick(microservice.actions);
  const statusCode = randomStatus();
  const duration = randomInt(80, statusCode >= 500 ? 9000 : 2500);
  const startedAt = new Date(Date.now() - duration - config.startLeadMs);

  return {
    correlationId: randomUUID(),
    microservice,
    action,
    statusCode,
    duration,
    startedAt,
    finishedAt: new Date(startedAt.getTime() + duration),
    username: pick(users),
    url: pick(urls),
    ip: randomIp(),
    method: pick(["GET", "POST", "PUT", "PATCH", "DELETE"]),
    browser: pick(["Chrome", "Firefox", "Edge", "Mobile App", "Postman"]),
    device: pick(["desktop", "laptop", "tablet", "phone"]),
    userAgent: pick(userAgents)
  };
}

function buildStartLog(request) {
  return {
    correlation_id: request.correlationId,
    microservice_name: request.microservice.name,
    action_eng: request.action[0],
    action_rus: request.action[1],
    request_status_code: 102,
    log_date: request.startedAt.toISOString(),
    log_type_name: "start",
    username: request.username,
    duration: 0,
    event: `${request.action[0]} started`,
    ip: request.ip,
    browser: request.browser,
    userAgent: request.userAgent,
    url: request.url,
    method: request.method,
    device: request.device,
    message: "Request accepted by service"
  };
}

function buildFinishLog(request) {
  return {
    correlation_id: request.correlationId,
    microservice_name: request.microservice.name,
    action_eng: request.action[0],
    action_rus: request.action[1],
    request_status_code: request.statusCode,
    log_date: request.finishedAt.toISOString(),
    log_type_name: "finish",
    username: request.username,
    duration: request.duration,
    event: `${request.action[0]} finished`,
    ip: request.ip,
    browser: request.browser,
    userAgent: request.userAgent,
    url: request.url,
    method: request.method,
    device: request.device,
    message: request.statusCode >= 400 ? "Request finished with error" : "Request completed successfully"
  };
}

function randomStatus() {
  const roll = Math.random();
  if (roll < 0.72) {
    return pick([200, 201, 204]);
  }
  if (roll < 0.9) {
    return pick([400, 401, 403, 404]);
  }
  return pick([500, 502, 503]);
}

function randomIp() {
  return `10.${randomInt(0, 10)}.${randomInt(0, 255)}.${randomInt(1, 254)}`;
}

function envList(name, fallback) {
  return (process.env[name] || fallback)
    .split(",")
    .map((value) => value.trim())
    .filter(Boolean);
}

function envInt(name, fallback) {
  const parsed = Number.parseInt(process.env[name], 10);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : fallback;
}

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function pick(values) {
  return values[randomInt(0, values.length - 1)];
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

main().catch(async (error) => {
  console.error(error);
  try {
    await producer.disconnect();
  } catch (_) {
    // Nothing useful to do during shutdown.
  }
  process.exitCode = 1;
});
