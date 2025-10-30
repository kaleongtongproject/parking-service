// .github/scripts/check_pr_against_reqs.js
const fs = require('fs');
const fetch = global.fetch || require('node-fetch');

const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const REPO = process.env.REPO;
const PR_NUMBER = process.env.PR_NUMBER;
const DEFAULT_BRANCH = process.env.DEFAULT_BRANCH || 'main';

if (!GITHUB_TOKEN || !REPO || !PR_NUMBER) {
  console.error('Missing required envs: GITHUB_TOKEN, REPO, PR_NUMBER');
  process.exit(1);
}

const [owner, repo] = REPO.split('/');

async function getReqsMd() {
  // Get docs/REQS.md from default branch via GitHub Contents API
  const url = `https://api.github.com/repos/${owner}/${repo}/contents/docs/REQS.md?ref=${DEFAULT_BRANCH}`;
  const resp = await fetch(url, {
    headers: {
      Authorization: `Bearer ${GITHUB_TOKEN}`,
      Accept: 'application/vnd.github.v3.raw',
    },
  });
  if (resp.status === 404) {
    return null;
  }
  if (!resp.ok) {
    const t = await resp.text();
    throw new Error('Failed to fetch REQS.md: ' + resp.status + ' - ' + t);
  }
  const txt = await resp.text();
  return txt;
}

function extractMachineJSON(reqsText) {
  // find a code fence with MACHINE_READABLE_REQUIREMENTS
  const regex = /```json\s*MACHINE_READABLE_REQUIREMENTS\s*([\s\S]*?)```/i;
  const m = reqsText.match(regex);
  if (!m) return null;
  const jsonText = m[1].trim();
  try {
    const parsed = JSON.parse(jsonText);
    return parsed;
  } catch (e) {
    console.error('Failed to parse machine-readable JSON', e);
    return null;
  }
}

async function listPrFiles() {
  const url = `https://api.github.com/repos/${owner}/${repo}/pulls/${PR_NUMBER}/files`;
  const resp = await fetch(url, {
    headers: {
      Authorization: `Bearer ${GITHUB_TOKEN}`,
      Accept: 'application/vnd.github.v3+json',
    },
  });
  if (!resp.ok) {
    const t = await resp.text();
    throw new Error('Failed to list PR files: ' + resp.status + ' - ' + t);
  }
  return await resp.json(); // array of files with filename, patch, status
}

async function postComment(body) {
  const url = `https://api.github.com/repos/${owner}/${repo}/issues/${PR_NUMBER}/comments`;
  const resp = await fetch(url, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${GITHUB_TOKEN}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ body }),
  });
  if (!resp.ok) {
    const t = await resp.text();
    console.error('Failed to post comment: ', resp.status, t);
  }
}

function indicatorMatches(indicator, file) {
  if (!indicator) return false;
  const low = indicator.toLowerCase();
  if (file.filename && file.filename.toLowerCase().includes(low)) return true;
  if (file.patch && file.patch.toLowerCase().includes(low)) return true;
  return false;
}

(async () => {
  try {
    const reqsText = await getReqsMd();
    if (!reqsText) {
      console.log('No docs/REQS.md found on default branch; skipping check.');
      await postComment(
        '⚠️ No `docs/REQS.md` found on default branch. The AI requirements generator needs `docs/REQS.md` to check PR against requirements.'
      );
      process.exit(0);
    }
    const reqs = extractMachineJSON(reqsText);
    if (!reqs || !Array.isArray(reqs)) {
      console.error(
        'No valid machine-readable requirements found in docs/REQS.md'
      );
      await postComment(
        '⚠️ `docs/REQS.md` exists but no valid `MACHINE_READABLE_REQUIREMENTS` JSON found. Please ensure the generator produced the JSON block.'
      );
      process.exit(0);
    }

    const prFiles = await listPrFiles();
    // Evaluate each requirement
    const results = reqs.map((req) => {
      const indicators = Array.isArray(req.implementation_indicators)
        ? req.implementation_indicators
        : [];
      const matched = [];
      for (const ind of indicators) {
        for (const f of prFiles) {
          if (indicatorMatches(ind, f)) {
            matched.push({ indicator: ind, filename: f.filename });
            break;
          }
        }
      }
      return {
        id: req.id || req.title || 'UNKNOWN',
        title: req.title || '',
        matched: matched,
        indicators_total: indicators.length,
      };
    });

    // Summarize
    let md = `## AI Requirements check — automated summary\n\n`;
    md += `This comment analyzes the PR and attempts to match changed files/patches against AI-generated implementation indicators from \`docs/REQS.md\`.\n\n`;
    md += `**Summary:** ${
      results.filter((r) => r.matched.length > 0).length
    } requirement(s) appear to have matching indicators; ${
      results.filter((r) => r.matched.length === 0).length
    } do not.\n\n`;

    for (const r of results) {
      md += `### ${r.id} — ${r.title}\n`;
      if (r.matched.length > 0) {
        md += `**Status:** ✅ Indicators matched (${r.matched.length}/${r.indicators_total})\n\n`;
        md += `**Matches:**\n`;
        r.matched.forEach((m) => {
          md += `- \`${m.indicator}\` found in \`${m.filename}\`\n`;
        });
      } else {
        md += `**Status:** ❌ No indicators matched (${r.matched.length}/${r.indicators_total})\n\n`;
        md += `**Suggested actions:**\n- Ensure PR modifies files or code paths referenced by the requirement (check filenames, endpoint names, or keywords listed in the requirement).\n`;
      }
      md += `\n`;
    }

    md += `\n---\n_This check uses simple token matching of user-specified implementation indicators. It is a heuristic — please review results manually._\n`;

    await postComment(md);
    console.log('Posted summary comment.');
  } catch (err) {
    console.error('Error checking PR against REQS:', err);
    await postComment(
      '⚠️ Failed to run automated requirement check: ' + String(err)
    );
    process.exit(1);
  }
})();
