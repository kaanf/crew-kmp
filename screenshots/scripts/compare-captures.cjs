// NODE_PATH="$(npm root -g)/goldie/node_modules" node screenshots/scripts/compare-captures.cjs <baseline> <actual>
// Compare decoded RGBA, not PNG compression or metadata. Both folders contain raw scene PNGs.
// Optional --ignore-top=<pixels> explicitly excludes simulator system chrome.
const { createCanvas, loadImage } = require('@napi-rs/canvas');
const path = require('node:path');
const scenes = ['dashboard', 'event-detail', 'rps', 'quest', 'leaderboard', 'tonights-roll'];
async function pixels(file) {
  const image = await loadImage(file);
  const canvas = createCanvas(image.width, image.height);
  const context = canvas.getContext('2d');
  context.drawImage(image, 0, 0);
  return { width: image.width, height: image.height, data: context.getImageData(0, 0, image.width, image.height).data };
}
async function main() {
  const [baseline, actual] = process.argv.slice(2);
  if (!baseline || !actual) throw new Error('Usage: compare-captures.cjs <baseline-dir> <actual-dir>');
  const option = process.argv.slice(4).find(arg => arg.startsWith('--ignore-top='));
  const ignoreTop = option ? Number(option.split('=')[1]) : 0;
  if (!Number.isInteger(ignoreTop) || ignoreTop < 0) throw new Error('Invalid --ignore-top value');
  if (ignoreTop) console.log(`Explicitly excluding the top ${ignoreTop} pixels (system chrome)`);
  let failed = false;
  for (const scene of scenes) {
    const a = await pixels(path.join(baseline, `${scene}.png`));
    const b = await pixels(path.join(actual, `${scene}.png`));
    if (a.width !== b.width || a.height !== b.height) throw new Error(`${scene}: dimensions changed`);
    if (ignoreTop >= a.height) throw new Error('Cannot exclude the entire screenshot');
    let changed = 0;
    let left = a.width, top = a.height, right = -1, bottom = -1;
    for (let i = ignoreTop * a.width * 4; i < a.data.length; i += 4) {
      if (a.data.subarray(i, i + 4).some((value, channel) => value !== b.data[i + channel])) {
        changed++;
        const x = (i / 4) % a.width, y = Math.floor(i / 4 / a.width);
        left = Math.min(left, x); right = Math.max(right, x);
        top = Math.min(top, y); bottom = Math.max(bottom, y);
      }
    }
    console.log(`${scene}: ${a.width}x${a.height}, ${changed} changed pixels${changed ? `, bounds [${left},${top}]-[${right},${bottom}]` : ''}`);
    failed ||= changed > 0;
  }
  process.exitCode = failed ? 1 : 0;
}
main().catch(error => { console.error(error.message); process.exitCode = 1; });
