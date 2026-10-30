#!/usr/bin/env python3
"""Real-backend match runner. Credentials are read only from environment."""
import json
import os
import subprocess
import sys
import time
from pathlib import Path
from urllib.request import Request, urlopen
from urllib.error import HTTPError

ROOT = Path(__file__).resolve().parents[2]


def main():
    base = os.environ['MATCH_API_BASE'].rstrip('/')
    if not base.startswith('https://'):
        raise RuntimeError('MATCH_API_BASE must use HTTPS and include the API prefix')
    event = os.environ['MATCH_EVENT_ID']
    from urllib.parse import quote
    prefix = '/events/' + quote(event, safe='')
    tokens = {role: os.environ['MATCH_' + role + '_TOKEN'] for role in ('PLAYER', 'BOT')}
    timeout = float(os.environ.get('MATCH_TIMEOUT', '45'))

    def api(role, path, body=None):
        req = Request(base + path, data=None if body is None else json.dumps(body).encode(),
                      headers={'Authorization': 'Bearer ' + tokens[role], 'Content-Type': 'application/json'},
                      method='GET' if body is None else 'POST')
        try:
            with urlopen(req, timeout=15) as response:
                raw = response.read()
                return json.loads(raw) if raw else None
        except HTTPError as exc:
            raise RuntimeError(f'{role}: HTTP {exc.code} on {path}') from None

    def wait_state(states, match_id=None):
        deadline = time.monotonic() + timeout
        while time.monotonic() < deadline:
            snap = api('BOT', prefix + '/matches/current')
            if snap and (match_id is None or snap['matchId'] == match_id) and snap['state'] in states:
                return snap
            time.sleep(0.5)
        raise RuntimeError('Timed out waiting for match state: ' + ', '.join(states))

    def ui(step):
        command = ['maestro']
        if os.environ.get('MATCH_DEVICE'):
            command += ['--device', os.environ['MATCH_DEVICE']]
        command += ['test', str(ROOT / '.maestro' / 'match' / (step + '.yaml'))]
        subprocess.run(command, cwd=ROOT, check=True)

    for role in tokens:
        if api(role, prefix + '/matches/current') is not None:
            raise RuntimeError(role + ' already has an active match; use clean fixtures')
    player = api('PLAYER', prefix + '/my-participant')
    bot = api('BOT', prefix + '/my-participant')
    if player['matchQrToken'] == bot['matchQrToken']:
        raise RuntimeError('PLAYER and BOT must be different participants')
    tasks = api('BOT', '/tasks?eventId=' + quote(event, safe=''))
    task = next((t for t in tasks if t['category'] != 'PHOTO'), None)
    if task is None:
        raise RuntimeError('Fixture needs a non-photo task')
    # UI must already be connected to the event before the invite is sent.
    ui('00_idle')
    api('BOT', prefix + '/invite', {'scannedMatchQrToken': player['matchQrToken']})
    ui('01_accept_ready')
    snap = wait_state({'ReadyWaiting', 'ResultPending'})
    match_id = snap['matchId']
    route = prefix + '/matches/' + quote(match_id, safe='')
    api('BOT', route + '/ready', {})
    ui('02_report_loss')
    api('BOT', route + '/result', {'won': True})
    wait_state({'TaskPickPending'}, match_id)
    api('BOT', route + '/task/offer', {'taskId': task['id']})
    # Separate step ensures the loser sees the task before bot confirmation.
    ui('03_task')
    api('BOT', route + '/task/confirm', {'completed': True})
    wait_state({'Completed'}, match_id)
    ui('04_finish')
    api('BOT', route + '/finish', {})
    print('Match scenario completed; UI and backend checks passed.')


if __name__ == '__main__':
    try:
        main()
    except (RuntimeError, KeyError, subprocess.CalledProcessError) as exc:
        print('Match scenario failed: ' + str(exc), file=sys.stderr)
        sys.exit(1)
