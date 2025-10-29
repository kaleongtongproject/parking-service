/**
 * .github/scripts/generate_reqs.js
 * Minimal script to:
 * - gather a few repo files (README, docs, package.json, small src samples)
 * - call OpenAI Responses API (ChatGPT) to generate business requirements -> REQS.md content
 * - create an Issue "AI: Generated Requirements" (or comment on PR)
 *
 * Note: This script is intentionally simple. For large repos: chunk files, redact secrets,
 * and avoid sending huge files.
 */

import fs from 'fs';
import path from 'path';
import fetch from 'node-fetch';

const openaiKey = process.env.OPENAI_API_KEY;
const githubToken = process.env.GITHUB_TOKEN;
const repo = process.env.REPO;
const prNumber = process.env.PR_NUMBER;
const ignoreGlobs = (process.env.IGNORE_GLOBS || '').split(',');

// Helper: read a file if it exists and is small
function safeRead(filePath, maxChars = 30000) {
  try {
    if (fs.existsSync(filePath)) {
      const raw = fs.readFileSync(filePath, 'utf8');
      return raw.length > maxChars
        ? raw.slice(0, maxChars) + '\n\n...[truncated]'
        : raw;
    }
  } catch (e) {}
  return null;
}

// Collect context files
const files = [];
const root = process.cwd();
const readme = safeRead(path.join(root, 'README.md'));
if (readme) files.push({ name: 'README.md', content: readme });

const pkg = safeRead(path.join(root, 'package.json'));
if (pkg) files.push({ name: 'package.json', content: pkg });

const docsIndex =
  safeRead(path.join(root, 'docs', 'index.md')) ||
  safeRead(path.join(root, 'docs', 'README.md'));
if (docsIndex) files.push({ name: 'docs/README', content: docsIndex });

// sample a few files from src/ (if exists)
const srcDir = path.join(root, 'src');
if (fs.existsSync(srcDir) && fs.statSync(srcDir).isDirectory()) {
  const children = fs.readdirSync(srcDir).slice(0, 6);
  for (const c of children) {
    const p = path.join(srcDir, c);
    if (fs.statSync(p).isFile()) {
      const content = safeRead(p, 15000);
      if (content) files.push({ name: `src/${c}`, content });
    }
  }
}

// Build a clean context summary to keep prompt short
let contextText = '';
for (const f of files) {
  contextText += `--- FILE: ${f.name} ---\n${f.content}\n\n`;
}
if (!contextText)
  contextText =
    'REPO ROOT: No README/docs/src files found (or they were too large). Please run on a repo with README or docs.';

// Prompt template (business requirements focused)
const prompt = `
You are a senior product manager and software architect with domain-agnostic expertise.
Given the repository context below, produce a comprehensive but concise "Business Requirements" document (in markdown) suitable to drop into REQS.md.

Required output structure in markdown:
# Project summary
A 2-3 line business summary of what this repo appears to do (use evidence lines).

# Goals (3-6)
List measurable goals or business objectives the project should target.

# User stories (8-14)
For each: As a <role>, I want <goal> so that <reason>.
Include for each: Acceptance Criteria (clear, testable), Priority (High/Medium/Low), Estimated Effort (T-shirt: S/M/L).

# Implementation tasks (10-20)
Break stories into actionable tasks (title, short description, suggested owner, labels, estimate S/M/L).

# Non-functional requirements
Performance, security, privacy, deployment constraints, and monitoring.

# Next steps (3)
What to schedule for the next sprint (prioritized).

Also include a small "Sources" section listing the files/paths you used for inference.

Repo context:
${contextText}

Rules:
- Keep output under 2000 words.
- If the repo appears to be a library or SDK, include an "Integration examples" task.
- If missing info, produce reasonable defaults but mark them as assumptions.
- Do NOT output any secrets. If you detect obvious secret patterns, call them out as "POTENTIAL SECRET" with file path.
`;

// Call OpenAI Responses API (v1/responses)
async function callOpenAI(promptText) {
  const resp = await fetch('https://api.openai.com/v1/responses', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${openaiKey}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      model: 'gpt-4o-mini', // choose a cost/quality appropriate model; change if needed
      input: promptText,
      max_output_tokens: 2000,
    }),
  });
  const body = await resp.json();
  // best-effort to get text
  const output =
    (body.output &&
      body.output.length &&
      body.output[0].content &&
      body.output[0].content.map((c) => c.text || '').join('')) ||
    JSON.stringify(body);
  return output;
}

// Post to GitHub: create issue or comment on PR
async function postToGitHub(title, bodyText) {
  const apiBase = 'https://api.github.com';
  if (prNumber) {
    // Post as PR comment
    const url = `${apiBase}/repos/${repo}/issues/${prNumber}/comments`;
    const res = await fetch(url, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${githubToken}`,
        'Content-Type': 'application/json',
        Accept: 'application/vnd.github+json',
      },
      body: JSON.stringify({
        body: `## AI-generated Requirements\n\n${bodyText}`,
      }),
    });
    return res.ok;
  } else {
    // Create an Issue titled "AI: Generated Requirements"
    const url = `${apiBase}/repos/${repo}/issues`;
    const res = await fetch(url, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${githubToken}`,
        'Content-Type': 'application/json',
        Accept: 'application/vnd.github+json',
      },
      body: JSON.stringify({
        title,
        body: `This issue contains AI-generated requirements.\n\n${bodyText}`,
      }),
    });
    return res.ok;
  }
}

// main
(async () => {
  try {
    console.log('Calling OpenAI with repo context...');
    const aiOutput = await callOpenAI(prompt);
    console.log('AI response length:', aiOutput.length);
    const created = await postToGitHub('AI: Generated Requirements', aiOutput);
    if (created) {
      console.log('Created Issue / Comment successfully.');
    } else {
      console.error(
        'Failed to create issue/comment. Check GITHUB_TOKEN permissions and repo variable.'
      );
      console.log(aiOutput.slice(0, 1000));
    }
  } catch (err) {
    console.error('Error:', err);
    process.exit(1);
  }
})();
