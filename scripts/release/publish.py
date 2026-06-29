#!/usr/bin/env python3
"""Publish FizzleSMP modpack ZIPs to Modrinth and CurseForge.

Driven by environment variables set by the release workflow
(.github/workflows/release.yml). Each platform is independent and self-skips
when its token — and, for CurseForge, its project id — is absent.

Modrinth runs first and must succeed before CurseForge is attempted, keeping
CurseForge the terminal step: a re-run of a partially-failed release
re-attempts only what's left. Modrinth is idempotent by version_number;
CurseForge, being last, only runs when it hasn't already succeeded.

Both platforms receive the client pack as the primary file and the server pack
as an additional file on the same version.
"""
from __future__ import annotations

import json
import os
import sys
import time

import requests

MODRINTH_API = "https://api.modrinth.com/v2"
CURSEFORGE_API = "https://minecraft.curseforge.com/api"
OK = (200, 201)
TIMEOUT = 120


def log(msg: str) -> None:
    print(msg, flush=True)


def warn(msg: str) -> None:
    print(f"::warning::{msg}", flush=True)


def error(msg: str) -> None:
    print(f"::error::{msg}", flush=True)


def env(name: str, default: str = "") -> str:
    return os.environ.get(name, default).strip()


def csv(value: str) -> list[str]:
    return [item.strip() for item in value.split(",") if item.strip()]


def read_file(path: str) -> bytes:
    with open(path, "rb") as handle:
        return handle.read()


def request_with_retries(method: str, url: str, *, attempts: int = 3, **kwargs):
    """Issue a request, retrying only transient failures (transport error or
    5xx). 2xx and 4xx return immediately so the caller can react. Returns the
    final Response, or None if every attempt hit a transport error."""
    response = None
    for attempt in range(1, attempts + 1):
        try:
            response = requests.request(method, url, timeout=TIMEOUT, **kwargs)
        except requests.RequestException as exc:
            warn(f"{method} {url} errored ({exc}); attempt {attempt}/{attempts}")
            response = None
        else:
            if response.status_code in OK or response.status_code < 500:
                return response
            warn(f"{method} {url} -> HTTP {response.status_code}; "
                 f"attempt {attempt}/{attempts}: {response.text[:300]}")
        if attempt < attempts:
            time.sleep(10)
    return response


# ---------------------------------------------------------------- Modrinth ---

def modrinth_publish(client_mrpack: str, server_zip: str,
                     changelog: str) -> bool:
    token = env("MODRINTH_TOKEN")
    if not token:
        log("No MODRINTH_TOKEN; skipping Modrinth publish")
        return True
    project = env("MODRINTH_ID")
    version = env("VERSION")
    headers = {"Authorization": token}

    existing = requests.get(f"{MODRINTH_API}/project/{project}/version",
                            headers=headers, timeout=TIMEOUT)
    if existing.ok and any(v.get("version_number") == version
                           for v in existing.json()):
        log(f"Modrinth already has {project} {version}; skipping")
        return True

    prerelease = env("PRERELEASE") == "true"
    file_parts = ["client"]
    if server_zip and os.path.isfile(server_zip):
        file_parts.append("server")

    data = {
        "name": env("TITLE"),
        "version_number": version,
        "changelog": changelog,
        "dependencies": [],
        "game_versions": csv(env("GAME_VERSIONS")),
        "version_type": "beta" if prerelease else "release",
        "loaders": [loader.lower() for loader in csv(env("LOADERS"))],
        "featured": not prerelease,
        "project_id": project,
        "file_parts": file_parts,
        "primary_file": "client",
    }
    files = {
        "data": (None, json.dumps(data), "application/json"),
        "client": (os.path.basename(client_mrpack), read_file(client_mrpack),
                   "application/x-modrinth-modpack+zip"),
    }
    if "server" in file_parts:
        files["server"] = (os.path.basename(server_zip),
                           read_file(server_zip), "application/zip")

    log(f"Publishing to Modrinth project {project}")
    if "server" in file_parts:
        log(f"  Client: {os.path.basename(client_mrpack)}")
        log(f"  Server: {os.path.basename(server_zip)}")
    resp = request_with_retries("POST", f"{MODRINTH_API}/version",
                                headers=headers, files=files)
    if not resp or resp.status_code not in OK:
        error(f"Modrinth upload failed "
              f"(HTTP {getattr(resp, 'status_code', 'n/a')})")
        if resp is not None:
            log(resp.text[:500])
        return False
    log(f"✅ Published to Modrinth ({resp.json().get('id', '?')})")
    return True


# -------------------------------------------------------------- CurseForge ---

def curseforge_resolve_versions(token: str, game_versions: list[str],
                                loaders: list[str]) -> list[int] | None:
    """Resolve game-version and loader names to CurseForge numeric ids within
    the correct version-type category."""
    headers = {"X-Api-Token": token}
    versions = requests.get(f"{CURSEFORGE_API}/game/versions",
                            headers=headers, timeout=TIMEOUT)
    types = requests.get(f"{CURSEFORGE_API}/game/version-types",
                         headers=headers, timeout=TIMEOUT)
    if not (versions.ok and types.ok):
        return None

    slug_by_type = {t["id"]: t["slug"] for t in types.json()}
    want_versions = {name.lower() for name in game_versions}
    want_loaders = {name.lower() for name in loaders}
    ids = set()
    for version in versions.json():
        slug = slug_by_type.get(version["gameVersionTypeID"], "")
        name = version["name"].lower()
        if slug.startswith("minecraft") and name in want_versions:
            ids.add(version["id"])
        elif slug.startswith("modloader") and name in want_loaders:
            ids.add(version["id"])
    return sorted(ids)


def curseforge_publish(client_zip: str, server_zip: str,
                       changelog: str) -> bool:
    token = env("CURSEFORGE_TOKEN")
    project = env("CURSEFORGE_ID")
    if not token or not project:
        log("No CURSEFORGE_TOKEN or CURSEFORGE_ID; skipping CurseForge publish")
        return True

    game_versions = csv(env("GAME_VERSIONS"))
    loaders = csv(env("LOADERS"))
    version_ids = curseforge_resolve_versions(token, game_versions, loaders)
    if version_ids is None:
        error("Could not fetch the CurseForge game-version catalogues")
        return False
    if not version_ids:
        error(f"Could not resolve any CurseForge version ids for "
              f"{env('GAME_VERSIONS')} / {env('LOADERS')}")
        return False
    log(f"Resolved CurseForge version ids: {version_ids}")

    prerelease = env("PRERELEASE") == "true"
    url = f"{CURSEFORGE_API}/projects/{project}/upload-file"

    metadata = {
        "changelog": changelog,
        "changelogType": "markdown",
        "displayName": env("TITLE"),
        "gameVersions": version_ids,
        "releaseType": "beta" if prerelease else "release",
    }
    data = {"metadata": json.dumps(metadata)}
    files = {
        "file": (os.path.basename(client_zip), read_file(client_zip),
                 "application/zip"),
    }

    log(f"Publishing client pack to CurseForge project {project}")
    resp = request_with_retries("POST", url, headers={"X-Api-Token": token},
                                data=data, files=files)
    if not resp or resp.status_code not in OK:
        error(f"CurseForge client upload failed "
              f"(HTTP {getattr(resp, 'status_code', 'n/a')})")
        if resp is not None:
            log(resp.text[:500])
        return False
    parent_file_id = resp.json().get("id")
    log(f"✅ Published client pack to CurseForge (file {parent_file_id})")

    if server_zip and os.path.isfile(server_zip):
        server_metadata = {
            "changelog": changelog,
            "changelogType": "markdown",
            "displayName": f"{env('TITLE')} (Server)",
            "parentFileID": parent_file_id,
            "releaseType": "beta" if prerelease else "release",
        }
        server_data = {"metadata": json.dumps(server_metadata)}
        server_files = {
            "file": (os.path.basename(server_zip), read_file(server_zip),
                     "application/zip"),
        }

        log(f"Publishing server pack to CurseForge project {project}")
        resp = request_with_retries("POST", url,
                                    headers={"X-Api-Token": token},
                                    data=server_data, files=server_files)
        if not resp or resp.status_code not in OK:
            error(f"CurseForge server upload failed "
                  f"(HTTP {getattr(resp, 'status_code', 'n/a')})")
            if resp is not None:
                log(resp.text[:500])
            return False
        log(f"✅ Published server pack to CurseForge "
            f"(file {resp.json().get('id', '?')})")

    return True


def main() -> None:
    client_zip = env("CLIENT_ZIP")
    client_mrpack = env("CLIENT_MRPACK")
    server_zip = env("SERVER_ZIP")

    if not client_zip or not os.path.isfile(client_zip):
        error(f"CLIENT_ZIP not found: {client_zip}")
        sys.exit(1)

    changelog_file = env("CHANGELOG_FILE") or "/tmp/release-body.md"
    changelog = ""
    if os.path.isfile(changelog_file):
        with open(changelog_file, encoding="utf-8") as handle:
            changelog = handle.read()

    modrinth_file = client_mrpack if client_mrpack and os.path.isfile(
        client_mrpack) else client_zip
    if not modrinth_publish(modrinth_file, server_zip, changelog):
        sys.exit(1)
    if not curseforge_publish(client_zip, server_zip, changelog):
        sys.exit(1)


if __name__ == "__main__":
    main()
