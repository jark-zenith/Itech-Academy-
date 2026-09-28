const PROVIDER_CONFIG = {
  openai: {
    key: "OPENAI_API_KEY",
    url: "https://api.openai.com/v1/responses",
    defaultModel: process.env.OPENAI_MODEL || "gpt-5-mini"
  },
  anthropic: {
    key: "ANTHROPIC_API_KEY",
    url: "https://api.anthropic.com/v1/messages",
    defaultModel: process.env.ANTHROPIC_MODEL || "claude-sonnet-4-5"
  },
  google: {
    key: "GEMINI_API_KEY",
    defaultModel: process.env.GEMINI_MODEL || "gemini-3.6-flash"
  }
};

function configured(provider) {
  const c = PROVIDER_CONFIG[provider];
  return Boolean(c && process.env[c.key]);
}

async function generate(provider, { model, system, messages, maxTokens = 1200 }) {
  if (!configured(provider)) {
    const error = new Error(`Provider ${provider} is not configured.`);
    error.code = "PROVIDER_NOT_CONFIGURED";
    error.status = 503;
    throw error;
  }

  const c = PROVIDER_CONFIG[provider];
  const selectedModel = model || c.defaultModel;

  if (provider === "openai") {
    const input = [
      { role: "developer", content: system },
      ...messages.map(m => ({ role: m.role === "assistant" ? "assistant" : "user", content: m.content }))
    ];
    const response = await fetch(c.url, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${process.env.OPENAI_API_KEY}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ model: selectedModel, input, max_output_tokens: maxTokens })
    });
    const body = await response.json();
    if (!response.ok) {
      const error = new Error(body.error?.message || "OpenAI request failed.");
      error.status = response.status;
      throw error;
    }
    return {
      text: body.output_text || "",
      model: selectedModel,
      usage: {
        input_tokens: body.usage?.input_tokens || 0,
        output_tokens: body.usage?.output_tokens || 0
      }
    };
  }

  if (provider === "anthropic") {
    const response = await fetch(c.url, {
      method: "POST",
      headers: {
        "x-api-key": process.env.ANTHROPIC_API_KEY,
        "anthropic-version": "2023-06-01",
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        model: selectedModel,
        max_tokens: maxTokens,
        system,
        messages: messages.map(m => ({
          role: m.role === "assistant" ? "assistant" : "user",
          content: m.content
        }))
      })
    });
    const body = await response.json();
    if (!response.ok) {
      const error = new Error(body.error?.message || "Anthropic request failed.");
      error.status = response.status;
      throw error;
    }
    return {
      text: (body.content || []).filter(x => x.type === "text").map(x => x.text).join(""),
      model: selectedModel,
      usage: {
        input_tokens: body.usage?.input_tokens || 0,
        output_tokens: body.usage?.output_tokens || 0
      }
    };
  }

  const url = `https://generativelanguage.googleapis.com/v1beta/models/${selectedModel}:generateContent`;
  const response = await fetch(url, {
    method: "POST",
    headers: {
      "x-goog-api-key": process.env.GEMINI_API_KEY,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      system_instruction: { parts: [{ text: system }] },
      contents: messages.map(m => ({
        role: m.role === "assistant" ? "model" : "user",
        parts: [{ text: m.content }]
      })),
      generationConfig: { maxOutputTokens: maxTokens }
    })
  });
  const body = await response.json();
  if (!response.ok) {
    const error = new Error(body.error?.message || "Gemini request failed.");
    error.status = response.status;
    throw error;
  }

  return {
    text: body.candidates?.[0]?.content?.parts?.map(p => p.text || "").join("") || "",
    model: selectedModel,
    usage: {
      input_tokens: body.usageMetadata?.promptTokenCount || 0,
      output_tokens: body.usageMetadata?.candidatesTokenCount || 0
    }
  };
}

module.exports = { PROVIDER_CONFIG, configured, generate };
