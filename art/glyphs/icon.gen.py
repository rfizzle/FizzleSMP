#!/usr/bin/env python3
"""Compose FizzleSMP's shield icon as a 128px .glyph in the Concord medallion
pattern: green glow halo, iron-grey stone bezel, dark brickwork field, with the
shield crest from art/icon.png downsampled and stamped as the centre motif.

glyph.py rasterizes the emitted .glyph deterministically.
"""
import math
import os
import struct
import zlib

N = 128
CX = CY = (N - 1) / 2.0
HERE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.join(HERE, '..', 'shield.png')

COL = {
    'ink':     '#0a0a0a',
    # green glow halo (FizzleSMP accent)
    'glow1':   '#56b366cc',
    'glow2':   '#3d8a4aa0',
    'glow3':   '#2d6b3a50',
    # iron-grey stone bezel (lit upper-left)
    'st_sh':   '#151515',
    'st_dark': '#282828',
    'st_mid':  '#404040',
    'st_lit':  '#5e5e5e',
    'st_spec': '#7a7a7a',
    # dark iron-green brickwork field
    'br_deep': '#0a1510',
    'br':      '#122a1e',
    'br_lit':  '#1a3828',
    'mortar':  '#081008',
    'vig':     '#060e08',
}

G = [[None] * N for _ in range(N)]


def dist(x, y):
    return math.hypot(x - CX, y - CY)


def ang(x, y):
    return math.atan2(y - CY, x - CX)


R_IN = 47.0
R_OUT = 57.0

# ---- 1. green glow halo -----------------------------------------------------
for y in range(N):
    for x in range(N):
        d = dist(x, y)
        if R_OUT < d <= R_OUT + 2:
            G[y][x] = 'glow1'
        elif R_OUT + 2 < d <= R_OUT + 4:
            G[y][x] = 'glow2'
        elif R_OUT + 4 < d <= R_OUT + 6.5:
            G[y][x] = 'glow3'

# ---- 2. iron-grey stone bezel annulus ----------------------------------------
for y in range(N):
    for x in range(N):
        d = dist(x, y)
        if R_IN <= d <= R_OUT:
            a = ang(x, y)
            base = math.cos(a - math.radians(225)) + \
                0.3 * (0.6 * math.sin(a * 8) + 0.4 * math.sin(a * 15 + 1.1))
            if d >= R_OUT - 1.2 or d <= R_IN + 1.0:
                G[y][x] = 'ink'
            elif base > 0.85:
                G[y][x] = 'st_spec'
            elif base > 0.25:
                G[y][x] = 'st_lit'
            elif base > -0.35:
                G[y][x] = 'st_mid'
            elif base > -0.8:
                G[y][x] = 'st_dark'
            else:
                G[y][x] = 'st_sh'

# ---- 3. dark brickwork field -------------------------------------------------
BRH, BRW = 8, 16
for y in range(N):
    for x in range(N):
        d = dist(x, y)
        if d >= R_IN - 1.0:
            continue
        row = int((y - (CY - R_IN)) // BRH)
        off = (BRW // 2) if (row % 2) else 0
        my = ((y - (CY - R_IN)) % BRH) < 1
        mx = ((x - off) % BRW) < 1
        if my or mx:
            G[y][x] = 'mortar'
        else:
            tone = (row * 3 + int((x - off) // BRW)) % 5
            G[y][x] = 'br_lit' if tone == 0 else ('br_deep' if tone == 3 else 'br')
        if d > R_IN - 5:
            G[y][x] = 'vig' if not (my or mx) else 'mortar'

for y in range(N):
    for x in range(N):
        if R_IN - 1.5 <= dist(x, y) < R_IN:
            G[y][x] = 'ink'

# ---- 4. centre motif — shield from art/icon.png, downsampled ----------------

def read_png(path):
    """Minimal stdlib-only PNG reader. Returns (w, h, rows of (r,g,b,a))."""
    with open(path, 'rb') as f:
        sig = f.read(8)
        assert sig == b'\x89PNG\r\n\x1a\n'
        ihdr = None
        idat = b''
        while True:
            hdr = f.read(8)
            if len(hdr) < 8:
                break
            length = struct.unpack('>I', hdr[:4])[0]
            ctype = hdr[4:8]
            data = f.read(length)
            f.read(4)
            if ctype == b'IHDR':
                w, h = struct.unpack('>II', data[:8])
                ihdr = (w, h, data[8], data[9])
            elif ctype == b'IDAT':
                idat += data
            elif ctype == b'IEND':
                break
        w, h, bd, ct = ihdr
        raw = zlib.decompress(idat)
        bpp = {2: 3, 6: 4, 0: 1, 4: 2}[ct] * (bd // 8)
        stride = 1 + w * bpp
        pixels = []
        prev_row = b'\x00' * (w * bpp)
        for y in range(h):
            row_data = raw[y * stride: (y + 1) * stride]
            ftype = row_data[0]
            scanline = bytearray(row_data[1:])
            for i in range(len(scanline)):
                a = scanline[i - bpp] if i >= bpp else 0
                b_val = prev_row[i] if y > 0 else 0
                if ftype == 1:
                    scanline[i] = (scanline[i] + a) & 0xff
                elif ftype == 2:
                    scanline[i] = (scanline[i] + b_val) & 0xff
                elif ftype == 3:
                    scanline[i] = (scanline[i] + (a + b_val) // 2) & 0xff
                elif ftype == 4:
                    c = prev_row[i - bpp] if i >= bpp and y > 0 else 0
                    p = a + b_val - c
                    pa, pb, pc = abs(p - a), abs(p - b_val), abs(p - c)
                    pr = a if pa <= pb and pa <= pc else (b_val if pb <= pc else c)
                    scanline[i] = (scanline[i] + pr) & 0xff
            prev_row = bytes(scanline)
            row = []
            for x in range(w):
                off = x * bpp
                if bpp == 4:
                    row.append(tuple(scanline[off:off + 4]))
                elif bpp == 3:
                    row.append((scanline[off], scanline[off + 1], scanline[off + 2], 255))
            pixels.append(row)
        return w, h, pixels


def quantize_motif(r, g, b):
    """Quantize with blue-shift correction: the background-removed shield has
    cool grey iron that skews blue/purple. Neutralize near-grey pixels before
    quantizing, then round each channel to the nearest 36."""
    # Neutralize near-grey pixels (iron) — if saturation is low, force neutral
    sat = max(r, g, b) - min(r, g, b)
    if sat < 35:
        avg = (r + g + b) // 3
        r, g, b = avg, avg, avg
    def q(v):
        return min(252, round(v / 36) * 36)
    return '#%02x%02x%02x' % (q(r), q(g), q(b))


# Read and downsample the shield into the inner field (~88px to fill the disc)
MOTIF_SIZE = 88
print(f'Reading {SRC}...')
sw, sh, spx = read_png(SRC)
print(f'  source: {sw}x{sh}')

bw, bh = sw / MOTIF_SIZE, sh / MOTIF_SIZE
motif = {}
for dy in range(MOTIF_SIZE):
    for dx in range(MOTIF_SIZE):
        # Pure center-point sampling — pick the single pixel at the center of
        # each block. Preserves hard edges on filigree and gem facets.
        sx = int((dx + 0.5) * bw)
        sy = int((dy + 0.5) * bh)
        sx = min(sx, sw - 1)
        sy = min(sy, sh - 1)
        r, g, b, a = spx[sy][sx]
        if a < 128:
            continue
        motif[(dx, dy)] = quantize_motif(r, g, b)

# Centre the motif in the medallion
OX = int(round(CX - MOTIF_SIZE / 2.0))
OY = int(round(CY - MOTIF_SIZE / 2.0))

S = {}
for (mx, my), col in motif.items():
    xi, yi = OX + mx, OY + my
    if 0 <= xi < N and 0 <= yi < N:
        if dist(xi, yi) < R_IN - 2.5:
            S[(xi, yi)] = col

# Ink-outline the motif silhouette so it reads off the brick field
for (x, y) in list(S.keys()):
    for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
        nx, ny = x + dx, y + dy
        if (nx, ny) not in S and 0 <= nx < N and 0 <= ny < N:
            if dist(nx, ny) < R_IN - 1.5:
                G[ny][nx] = 'ink'

# ---- flatten to hex grid ----------------------------------------------------
flat = {}
for y in range(N):
    for x in range(N):
        v = G[y][x]
        if v is not None:
            flat[(x, y)] = COL.get(v, v)
for (x, y), col in S.items():
    flat[(x, y)] = col

# ---- emit -------------------------------------------------------------------
pool = "@$%&*+=oOxX0123456789abcdefghijklmnpqrstuvwyzABCDEFGHIJKLMNPQRSTUVWYZ?!~^/<>[]{};:_|`(),-\"\'"
MAX_COLORS = len(pool)

used = []
for y in range(N):
    for x in range(N):
        c = flat.get((x, y))
        if c and c not in used:
            used.append(c)

# If too many colors, iteratively merge the closest pair until we fit.
def _rgb(h):
    h = h.lstrip('#')
    return int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)

while len(used) > MAX_COLORS:
    best_d, best_i, best_j = float('inf'), 0, 1
    for i in range(len(used)):
        ri, gi, bi = _rgb(used[i])
        for j in range(i + 1, len(used)):
            rj, gj, bj = _rgb(used[j])
            d = (ri - rj) ** 2 + (gi - gj) ** 2 + (bi - bj) ** 2
            if d < best_d:
                best_d, best_i, best_j = d, i, j
    victim = used[best_j]
    survivor = used[best_i]
    for k in list(flat.keys()):
        if flat[k] == victim:
            flat[k] = survivor
    used.pop(best_j)

print(f"  palette: {len(used)} colors (pool: {MAX_COLORS})")
ch = {c: pool[i] for i, c in enumerate(used)}

lines = [
    "# FizzleSMP shield icon — generated by icon.gen.py",
    "# Concord medallion pattern with shield motif from art/icon.png.",
    f"size: {N}", "", "legend:", "  . transparent",
]
for c in used:
    lines.append(f"  {ch[c]} {c}")
lines += ["", "frame:"]
for y in range(N):
    lines.append("  " + "".join(ch[flat[(x, y)]] if (x, y) in flat else "." for x in range(N)))

out_path = os.path.join(HERE, "icon.glyph")
with open(out_path, "w") as f:
    f.write("\n".join(lines) + "\n")
print(f"wrote {out_path}  ({len(used)} colors, {N}x{N})")
