const buckets = new Map();

function rateLimit({ windowMs = 60_000, max = 60 } = {}) {
  return (req, res, next) => {
    const key = req.ip || req.headers["x-forwarded-for"] || "unknown";
    const now = Date.now();
    const current = buckets.get(key);

    if (!current || now >= current.resetAt) {
      buckets.set(key, { count: 1, resetAt: now + windowMs });
      return next();
    }

    current.count += 1;
    if (current.count > max) {
      return res.status(429).json({
        error: "Too many requests.",
        retryAfterSeconds: Math.ceil((current.resetAt - now) / 1000)
      });
    }

    next();
  };
}

module.exports = { rateLimit };
