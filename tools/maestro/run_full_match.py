#!/usr/bin/env python3
"""Create a UI account, join a test event and play against a bot."""
import getpass
import json
import os
import runpy
import subprocess
import tempfile
import time
from pathlib import Path
from urllib.request import Request, urlopen
from urllib.error import HTTPError

ROOT = Path(__file__).resolve().parents[2]
BASE = os.environ.get('MATCH_API_BASE', 'https://api.crewishere.com/api').rstrip('/')


def api(path, token=None, body=None, method=None):
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    request = Request(BASE + path, headers=headers,
                      data=None if body is None else json.dumps(body).encode(),
                      method=method or ('GET' if body is None else 'POST'))
    try:
        with urlopen(request, timeout=20) as response:
            raw = response.read()
            return json.loads(raw) if raw else None
    except HTTPError as exc:
        raise RuntimeError(f'API HTTP {exc.code}; response body suppressed') from None


def ui(file, params=None):
    cmd = ['maestro']
    if os.environ.get('MATCH_DEVICE'):
        cmd += ['--device', os.environ['MATCH_DEVICE']]
    cmd += ['test']
    for key, value in (params or {}).items():
        cmd += ['-e', f'{key}={value}']
    subprocess.run(cmd + [str(file)], cwd=ROOT, check=True)


def flow(commands):
    # No credentials in these temporary UI files. Entry codes stay in a private file.
    with tempfile.TemporaryDirectory(prefix='crew-match-') as directory:
        path = Path(directory) / 'flow.yaml'
        path.write_text('appId: com.kaanf.crew\n---\n' + commands)
        ui(path)


def main():
    if not BASE.startswith('https://'):
        raise RuntimeError('API base must use HTTPS')
    email = os.environ.get('MATCH_BOT_EMAIL') or input('Bot email: ')
    password = os.environ.get('MATCH_BOT_PASSWORD') or getpass.getpass('Bot password: ')
    bot = api('/auth/login', body={'email': email, 'password': password})['accessToken']
    events = api('/events', bot)
    matches = [e for e in events if 'strv' in e['title'].lower() and 'test' in e['title'].lower()]
    if len(matches) != 1:
        raise RuntimeError('Expected exactly one STRV test event; found ' + str(len(matches)))
    event = matches[0]
    prefix = '/events/' + event['id']
    detail = api(prefix, bot)
    print('Selected event:', detail['title'], '| status:', detail['status'], '| phase:', detail['phase'])
    if api(prefix + '/matches/current', bot) is not None:
        raise RuntimeError('Bot has an active match; reset fixture first')
    if not detail['hasMyTicket']:
        # Explicitly avoid buying a paid ticket.
        price = detail['price']
        if price.get('amount', -1) != 0:
            raise RuntimeError('Bot needs an existing ticket or a free test event')
        api(prefix + '/tickets', bot, {})
    ticket = api(prefix + '/my-ticket', bot)['ticket']
    if not ticket.get('checkedInAt'):
        api(prefix + '/check-in', bot, {'entryCode': ticket['entryCode']})
    test_email = os.environ.get('MATCH_PLAYER_EMAIL')
    if test_email:
        import re
        if not re.fullmatch(r'maestro\+\d+@example\.com', test_email):
            raise RuntimeError('Resume only supports generated Maestro accounts')
        flow('- openLink: ' + json.dumps('crew://event/' + event['id']) + '\n')
    else:
        test_email = f'maestro+{time.time_ns()}@example.com'
        ui(ROOT / '.maestro/subflows/account_setup.yaml', {
            'TEST_EMAIL': test_email, 'PASSWORD': 'Maestro123', 'FULL_NAME': 'Maestro Tester'})
    player = api('/auth/login', body={'email': test_email, 'password': 'Maestro123'})['accessToken']
    # Profile setup ends on profile; navigate dashboard then event via UI.
    import re
    title = json.dumps(re.escape(detail['title']))
    if not os.environ.get('MATCH_PLAYER_EMAIL'):
        flow('- tapOn: "Go back"\n- scrollUntilVisible:\n    element: ' + title +
             '\n- tapOn: ' + title + '\n')
    has_ticket = api(prefix, player)['hasMyTicket']
    if not has_ticket:
        flow('- tapOn: ".*Join event"\n'
             '- tapOn:\n    text: ".*Go to ticket detail"\n    optional: true\n'
             '- extendedWaitUntil:\n    visible: "Your door code"\n    timeout: 20000\n')
    else:
        flow('- tapOn: ".*Go to ticket detail"\n'
             '- extendedWaitUntil:\n    visible: "Your door code"\n    timeout: 20000\n')
    ticket = api(prefix + '/my-ticket', player)['ticket']
    flow('- scrollUntilVisible:\n    element: "Enter event with code"\n'
         '- tapOn: "Enter event with code"\n- tapOn:\n    point: "50%,35%"\n'
         '- inputText: ' + json.dumps(ticket['entryCode']) + '\n'
         '- extendedWaitUntil:\n    visible: "Enter the game|Your Match QR"\n    timeout: 30000\n'
         '- tapOn:\n    text: "Enter the game"\n    optional: true\n'
         '- tapOn:\n    text: "Skip"\n    optional: true\n')
    os.environ.update(MATCH_API_BASE=BASE, MATCH_EVENT_ID=event['id'],
                      MATCH_PLAYER_TOKEN=player, MATCH_BOT_TOKEN=bot)
    runpy.run_path(str(ROOT / 'tools/maestro/run_match.py'), run_name='__main__')
    if os.environ.get('MATCH_KEEP_PLAYER') == 'true':
        print('Generated player retained for inspection:', test_email)
    else:
        ui(ROOT / '.maestro/match/05_delete_player.yaml', {'DELETE_ACCOUNT_EMAIL': test_email})
        print('UI account deletion and signed-out state after restart verified. Bot account retained.')


if __name__ == '__main__':
    try:
        main()
    except (RuntimeError, KeyError, subprocess.CalledProcessError):
        print('Full match failed; inspect device and fixture. Credentials and API bodies suppressed.')
        raise SystemExit(1)
