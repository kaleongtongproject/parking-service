// .github/scripts/generate_reqs_commit.js
// CommonJS version with JSON-validation & auto-repair step for MACHINE_READABLE_REQUIREMENTS
const fs = require('fs');
const path = require('path');

const OPENAI_KEY = process.env.OPENAI_API_KEY;
if (!OPENAI_KEY) {
  console.error('OPENAI_API_KEY not set');
  process.exit(1);
}

const fetch = global.fetch || require('node-fetch');

function safeWriteTmp(content) {
  try {
    fs.mkdirSync('tmp', { recursive: true });
    fs.writeFileSync('tmp/reqs.md', content, 'utf8');
    console.log('Wrote tmp/reqs.md');
  } catch (e) {
    console.error('Failed to write tmp/reqs.md', e);
  }
}

function sampleRepoContext() {
  const root = process.cwd();
  const files = [];
  const addIf = (p, max = 20000) => {
    try {
      if (fs.existsSync(p) && fs.statSync(p).isFile()) {
        let raw = fs.readFileSync(p, 'utf8');
        if (raw.length > max) raw = raw.slice(0, max) + '\n\n...[truncated]';
        files.push({ name: path.relative(root, p), content: raw });
      }
    } catch (e) {}
  };
  addIf(path.join(root, 'README.md'));
  addIf(path.join(root, 'package.json'));
  addIf(path.join(root, 'docs', 'README.md'));
  addIf(path.join(root, 'docs', 'index.md'));
  const src = path.join(root, 'src');
  if (fs.existsSync(src) && fs.statSync(src).isDirectory()) {
    const kids = fs.readdirSync(src).slice(0, 6);
    kids.forEach((k) => {
      const p = path.join(src, k);
      if (fs.statSync(p).isFile()) addIf(p, 8000);
    });
  }
  if (files.length === 0) {
    return 'No README/docs/src samples found. Please add README.md or docs to allow the AI to infer context.';
  }
  return files
    .map((f) => `--- FILE: ${f.name} ---\n${f.content}\n`)
    .join('\n\n');
}

const contextText = sampleRepoContext();

// Prompt: ask the model to roleplay business owner interviewing clients and output structured markdown + JSON block
const prompt = `
You are acting as a senior product owner. Your task:
1) Simulate short informal interviews with imaginary clients/stakeholders to infer needs for this repository.
2) Produce a Markdown "REQS.md" document with:
   - Project summary (2-3 lines)
   - 6-12 high-level requirements, each with:
     - id (format REQ-1, REQ-2, ...)
     - title
     - description
     - acceptance_criteria (short)
     - priority (High/Medium/Low)
     - estimate (S/M/L)
     - implementation_indicators: an array of short strings (filenames, function names, or keywords) which if present in a PR diff indicate progress toward this requirement.

3) After the human-readable markdown, append a machine-readable JSON block delimited by triple backticks and labelled exactly:
\`\`\`json
MACHINE_READABLE_REQUIREMENTS
<JSON array>
\`\`\`

Rules:
- Keep human-readable REQS.md under ~1600 words.
- If you don't have enough info, make explicit assumptions and mark them with "ASSUMPTION: ..."
- Implementation indicators should be short, likely-to-appear tokens in code changes (filenames, endpoint paths, function names, or config keys).
- Do NOT include any real secrets; if you detect secret-like patterns, list them as "POTENTIAL SECRET: <path>".

Repo context:
${contextText}
`;

// Call OpenAI Responses API
async function callOpenAI(promptText) {
  const resp = await fetch('https://api.openai.com/v1/responses', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${OPENAI_KEY}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      model: 'gpt-4o-mini',
      input: promptText,
      max_output_tokens: 1600,
    }),
  });
  if (!resp.ok) {
    const t = await resp.text();
    throw new Error('OpenAI error: ' + resp.status + ' - ' + t);
  }
  const body = await resp.json();
  // Extract text (best effort)
  let out = '';
  if (Array.isArray(body.output) && body.output.length) {
    out = body.output
      .map((o) => {
        if (o.content && Array.isArray(o.content))
          return o.content.map((c) => c.text || '').join('');
        return o.text || '';
      })
      .join('\n');
  } else if (body.output_text) {
    out = body.output_text;
  } else {
    out = JSON.stringify(body);
  }
  return out;
}

// Extract MACHINE_READABLE_REQUIREMENTS block (if present)
function extractMachineBlock(text) {
  // matches code fence with optional "json", then the label line, then capture until closing fence
  const regex = /```(?:json)?\s*MACHINE_READABLE_REQUIREMENTS\s*([\s\S]*?)```/i;
  const m = text.match(regex);
  if (!m) return null;
  return m[1].trim();
}

// Ask OpenAI to repair invalid JSON; instruct it to return ONLY valid JSON array, nothing else.
async function repairJsonWithOpenAI(invalidJsonText) {
  const repairPrompt = `
The text below is intended to be a JSON array of requirement objects but it is currently malformed or invalid JSON. Fix it and return ONLY a valid JSON array (no surrounding text, no explanation).
Here is the malformed JSON:
\`\`\`
${invalidJsonText}
\`\`\`
Remember: return only valid JSON (an array) and nothing else.
`;
  const resp = await fetch('https://api.openai.com/v1/responses', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${OPENAI_KEY}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      model: 'gpt-4o-mini',
      input: repairPrompt,
      max_output_tokens: 800,
    }),
  });
  if (!resp.ok) {
    const t = await resp.text();
    throw new Error('OpenAI repair error: ' + resp.status + ' - ' + t);
  }
  const body = await resp.json();
  let out = '';
  if (Array.isArray(body.output) && body.output.length) {
    out = body.output
      .map((o) => {
        if (o.content && Array.isArray(o.content))
          return o.content.map((c) => c.text || '').join('');
        return o.text || '';
      })
      .join('\n');
  } else if (body.output_text) {
    out = body.output_text;
  } else {
    out = JSON.stringify(body);
  }
  return out.trim();
}

// Remove the machine block from human-readable text (if present)
function removeMachineBlock(text) {
  return text
    .replace(/```(?:json)?\s*MACHINE_READABLE_REQUIREMENTS\s*[\s\S]*?```/i, '')
    .trim();
}

(async () => {
  try {
    console.log('Calling OpenAI to generate REQS.md...');
    const aiText = await callOpenAI(prompt);

    // Try to extract machine-readable block
    const machineText = extractMachineBlock(aiText);
    let parsed = null;

    if (machineText) {
      try {
        parsed = JSON.parse(machineText);
        console.log(
          'Successfully parsed machine-readable JSON from model output.'
        );
      } catch (e) {
        console.warn(
          'Initial parse failed for machine JSON. Attempting repair via OpenAI...',
          e.message
        );
        try {
          const repaired = await repairJsonWithOpenAI(machineText);
          // sometimes the model may include fences or text — strip fences if present
          const maybeJson = repaired.replace(/^[\s`]*|[\s`]*$/g, '');
          parsed = JSON.parse(maybeJson);
          console.log(
            'Successfully repaired and parsed machine JSON via OpenAI.'
          );
        } catch (err2) {
          console.error('Failed to repair JSON via OpenAI:', err2);
          parsed = null;
        }
      }
    } else {
      console.log('No machine-readable block found in AI output.');
    }

    // If parsing still failed, fallback to empty array and warn
    if (!parsed) {
      console.warn(
        'No valid machine-readable JSON available. Falling back to an empty array for MACHINE_READABLE_REQUIREMENTS.'
      );
      parsed = [];
    }

    // Build final REQS.md: use human-readable content but remove any machine block, then append a guaranteed-valid JSON block
    const humanReadable = removeMachineBlock(aiText);
    const jsonBlock = JSON.stringify(parsed, null, 2);
    const final = `${humanReadable}\n\n\n\`\`\`json\nMACHINE_READABLE_REQUIREMENTS\n${jsonBlock}\n\`\`\`\n`;

    safeWriteTmp(final);
    console.log(
      'AI output saved to tmp/reqs.md (with validated machine JSON).'
    );
  } catch (err) {
    console.error('Failed to generate REQS.md:', err);
    process.exit(1);
  }
})();
