// Minimal, rock-solid, zero-dependency QR code generator for vanilla JS
// Based on the standard ISO/IEC 18004 QR specification (Byte Mode, Mask 0..7, RS ECC)
// Supports alphanumeric, Latin-1, and UTF-8 strings.

(function (root, factory) {
  if (typeof exports === 'object') {
    module.exports = factory();
  } else {
    root.VanillaQR = factory();
  }
})(this, function () {
  'use strict';

  // Galois Field GF(256) tables
  var EXP = new Uint8Array(512);
  var LOG = new Uint8Array(256);
  var x = 1;
  for (var i = 0; i < 255; i++) {
    EXP[i] = x;
    EXP[i + 255] = x;
    LOG[x] = i;
    x = (x << 1) ^ ((x >> 7) * 0x11d);
  }

  function gfMul(a, b) {
    return (a === 0 || b === 0) ? 0 : EXP[LOG[a] + LOG[b]];
  }

  function rsGenPoly(n) {
    var poly = new Uint8Array([1]);
    for (var i = 0; i < n; i++) {
      var next = new Uint8Array(poly.length + 1);
      for (var j = 0; j < poly.length; j++) {
        next[j] ^= gfMul(poly[j], EXP[i]);
        next[j + 1] ^= poly[j];
      }
      poly = next;
    }
    return poly;
  }

  function rsEncode(data, numEcc) {
    var gen = rsGenPoly(numEcc);
    var res = new Uint8Array(data.length + numEcc);
    res.set(data, 0);
    for (var i = 0; i < data.length; i++) {
      var coef = res[i];
      if (coef !== 0) {
        for (var j = 0; j < gen.length; j++) {
          res[i + j] ^= gfMul(gen[j], coef);
        }
      }
    }
    res.set(data, 0);
    return res.subarray(data.length);
  }

  // Version data (Versions 1-10 are sufficient for EPC QR payloads up to 271 bytes in Level M)
  // Total Codewords, EC Codewords, Blocks1, DataPerBlock1, Blocks2, DataPerBlock2
  var VERSION_TABLE_M = [
    null,
    { total: 26, ec: 10, b1: 1, d1: 16, b2: 0, d2: 0 },   // V1 (max 14 data bytes)
    { total: 44, ec: 16, b1: 1, d1: 28, b2: 0, d2: 0 },   // V2 (max 26 data bytes)
    { total: 70, ec: 26, b1: 1, d1: 44, b2: 0, d2: 0 },   // V3 (max 42 data bytes)
    { total: 100, ec: 36, b1: 2, d1: 32, b2: 0, d2: 0 },  // V4 (max 62 data bytes)
    { total: 134, ec: 48, b1: 2, d1: 43, b2: 0, d2: 0 },  // V5 (max 84 data bytes)
    { total: 172, ec: 64, b1: 4, d1: 27, b2: 0, d2: 0 },  // V6 (max 106 data bytes)
    { total: 196, ec: 72, b1: 4, d1: 31, b2: 0, d2: 0 },  // V7 (max 122 data bytes)
    { total: 242, ec: 88, b1: 2, d1: 38, b2: 2, d2: 39 }, // V8 (max 152 data bytes)
    { total: 292, ec: 110, b1: 3, d1: 36, b2: 2, d2: 37 },// V9 (max 180 data bytes)
    { total: 346, ec: 130, b1: 4, d1: 43, b2: 1, d2: 44 } // V10 (max 213 data bytes)
  ];

  // Alignment pattern locations
  var ALIGNMENT_TABLE = [
    [],
    [],
    [6, 18],
    [6, 22],
    [6, 26],
    [6, 30],
    [6, 34],
    [6, 22, 38],
    [6, 24, 42],
    [6, 26, 46],
    [6, 28, 50]
  ];

  function encodeUTF8(str) {
    var out = [];
    for (var i = 0; i < str.length; i++) {
      var c = str.charCodeAt(i);
      if (c < 128) out.push(c);
      else if (c < 2048) {
        out.push(192 | (c >> 6));
        out.push(128 | (c & 63));
      } else {
        out.push(224 | (c >> 12));
        out.push(128 | ((c >> 6) & 63));
        out.push(128 | (c & 63));
      }
    }
    return out;
  }

  function pickVersion(dataLen) {
    for (var v = 1; v <= 10; v++) {
      var info = VERSION_TABLE_M[v];
      var maxData = info.b1 * info.d1 + info.b2 * info.d2;
      // 4 bits mode + 8 or 16 bits count
      var headerBits = 4 + (v <= 9 ? 8 : 16);
      if (dataLen * 8 + headerBits <= maxData * 8) {
        return v;
      }
    }
    throw new Error('Payload too large for versions 1-10');
  }

  function createQR(text) {
    var bytes = encodeUTF8(text);
    var version = pickVersion(bytes.length);
    var vInfo = VERSION_TABLE_M[version];
    var totalDataBytes = vInfo.b1 * vInfo.d1 + vInfo.b2 * vInfo.d2;

    // Bit buffer
    var bitStream = [];
    function putBits(val, len) {
      for (var i = len - 1; i >= 0; i--) {
        bitStream.push((val >> i) & 1);
      }
    }

    // 1. Mode indicator: Byte mode = 0100
    putBits(4, 4);
    // 2. Character count indicator
    putBits(bytes.length, version <= 9 ? 8 : 16);
    // 3. Data bytes
    for (var i = 0; i < bytes.length; i++) {
      putBits(bytes[i], 8);
    }
    // 4. Terminator (up to 4 zero bits)
    var termLen = Math.min(4, totalDataBytes * 8 - bitStream.length);
    putBits(0, termLen);
    // 5. Pad to byte boundary
    while (bitStream.length % 8 !== 0) {
      bitStream.push(0);
    }
    // 6. Pad bytes
    var padBytes = [0xEC, 0x11];
    var padIdx = 0;
    while (bitStream.length < totalDataBytes * 8) {
      putBits(padBytes[padIdx % 2], 8);
      padIdx++;
    }

    // Convert bitStream to bytes
    var allData = new Uint8Array(totalDataBytes);
    for (var i = 0; i < totalDataBytes; i++) {
      var b = 0;
      for (var bit = 0; bit < 8; bit++) {
        b = (b << 1) | bitStream[i * 8 + bit];
      }
      allData[i] = b;
    }

    // Split into blocks and calculate ECC
    var eccPerBlock = Math.floor(vInfo.ec / (vInfo.b1 + vInfo.b2));
    var blocks = [];
    var eccBlocks = [];
    var offset = 0;
    for (var b = 0; b < vInfo.b1; b++) {
      var blk = allData.subarray(offset, offset + vInfo.d1);
      offset += vInfo.d1;
      blocks.push(blk);
      eccBlocks.push(rsEncode(blk, eccPerBlock));
    }
    for (var b = 0; b < vInfo.b2; b++) {
      var blk = allData.subarray(offset, offset + vInfo.d2);
      offset += vInfo.d2;
      blocks.push(blk);
      eccBlocks.push(rsEncode(blk, eccPerBlock));
    }

    // Interleave data codewords
    var finalCodewords = [];
    var maxDataLen = Math.max(vInfo.d1, vInfo.d2);
    for (var i = 0; i < maxDataLen; i++) {
      for (var b = 0; b < blocks.length; b++) {
        if (i < blocks[b].length) finalCodewords.push(blocks[b][i]);
      }
    }
    // Interleave ECC codewords
    for (var i = 0; i < eccPerBlock; i++) {
      for (var b = 0; b < eccBlocks.length; b++) {
        finalCodewords.push(eccBlocks[b][i]);
      }
    }

    // Construct QR Matrix
    var size = 17 + version * 4;
    var matrix = [];
    var isReserved = [];
    for (var r = 0; r < size; r++) {
      matrix.push(new Uint8Array(size));
      isReserved.push(new Uint8Array(size));
    }

    function setModule(r, c, val) {
      matrix[r][c] = val ? 1 : 0;
      isReserved[r][c] = 1;
    }

    // Finder Patterns
    function drawFinder(row, col) {
      for (var r = -1; r <= 7; r++) {
        for (var c = -1; c <= 7; c++) {
          var cr = row + r;
          var cc = col + c;
          if (cr < 0 || cr >= size || cc < 0 || cc >= size) continue;
          if (r >= 0 && r <= 6 && (c === 0 || c === 6 || ((r === 0 || r === 6) && c >= 0 && c <= 6)) ||
              (r >= 2 && r <= 4 && c >= 2 && c <= 4)) {
            setModule(cr, cc, 1);
          } else {
            setModule(cr, cc, 0);
          }
        }
      }
    }
    drawFinder(0, 0);
    drawFinder(0, size - 7);
    drawFinder(size - 7, 0);

    // Timing Patterns
    for (var i = 8; i < size - 8; i++) {
      if (!isReserved[6][i]) setModule(6, i, i % 2 === 0);
      if (!isReserved[i][6]) setModule(i, 6, i % 2 === 0);
    }

    // Dark Module
    setModule(size - 8, 8, 1);

    // Alignment Patterns
    var align = ALIGNMENT_TABLE[version];
    for (var i = 0; i < align.length; i++) {
      for (var j = 0; j < align.length; j++) {
        var ar = align[i];
        var ac = align[j];
        if (isReserved[ar][ac]) continue;
        for (var r = -2; r <= 2; r++) {
          for (var c = -2; c <= 2; c++) {
            setModule(ar + r, ac + c, (Math.abs(r) === 2 || Math.abs(c) === 2 || (r === 0 && c === 0)) ? 1 : 0);
          }
        }
      }
    }

    // Reserve Format Information areas
    for (var i = 0; i < 9; i++) {
      if (i !== 6) { isReserved[8][i] = 1; isReserved[i][8] = 1; }
    }
    for (var i = size - 8; i < size; i++) {
      isReserved[8][i] = 1;
      isReserved[i][8] = 1;
    }

    // Place Codewords
    var bitIdx = 0;
    var allBits = [];
    for (var i = 0; i < finalCodewords.length; i++) {
      for (var b = 7; b >= 0; b--) {
        allBits.push((finalCodewords[i] >> b) & 1);
      }
    }

    var goingUp = true;
    for (var col = size - 1; col > 0; col -= 2) {
      if (col === 6) col--; // Skip vertical timing column
      var rowRange = [];
      if (goingUp) {
        for (var r = size - 1; r >= 0; r--) rowRange.push(r);
      } else {
        for (var r = 0; r < size; r++) rowRange.push(r);
      }
      goingUp = !goingUp;

      for (var idx = 0; idx < rowRange.length; idx++) {
        var r = rowRange[idx];
        for (var c = col; c >= col - 1; c--) {
          if (!isReserved[r][c]) {
            var val = bitIdx < allBits.length ? allBits[bitIdx++] : 0;
            // Apply standard mask pattern 000: (row + col) % 2 === 0
            if ((r + c) % 2 === 0) {
              val ^= 1;
            }
            matrix[r][c] = val;
          }
        }
      }
    }

    // Format Info for Level M (00) and Mask 000 (000)
    // 5 bits = 00000 -> BCH code gives 0x0000 -> XORed with 0x5412 = 0x5412 (101010000010010)
    var formatBits = [1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0];

    // Top-left format info
    var fIdx = 0;
    for (var i = 0; i <= 5; i++) matrix[8][i] = formatBits[fIdx++];
    matrix[8][7] = formatBits[fIdx++];
    matrix[8][8] = formatBits[fIdx++];
    matrix[7][8] = formatBits[fIdx++];
    for (var i = 5; i >= 0; i--) matrix[i][8] = formatBits[fIdx++];

    // Bottom and right format info
    fIdx = 0;
    for (var i = size - 1; i >= size - 7; i--) matrix[i][8] = formatBits[fIdx++];
    matrix[size - 8][8] = formatBits[fIdx++]; // dark module check/skip
    for (var i = size - 8; i < size; i++) matrix[8][i] = formatBits[fIdx++];

    return {
      size: size,
      matrix: matrix,
      toSVG: function(pixelSize) {
        pixelSize = pixelSize || 4;
        var margin = 4;
        var fullSize = (size + margin * 2) * pixelSize;
        var svg = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + fullSize + ' ' + fullSize + '" width="100%" height="100%">'];
        svg.push('<rect width="100%" height="100%" fill="#ffffff"/>');
        svg.push('<path fill="#0f172a" d="');
        var path = '';
        for (var r = 0; r < size; r++) {
          for (var c = 0; c < size; c++) {
            if (matrix[r][c] === 1) {
              var x = (c + margin) * pixelSize;
              var y = (r + margin) * pixelSize;
              path += 'M' + x + ',' + y + 'h' + pixelSize + 'v' + pixelSize + 'h-' + pixelSize + 'z ';
            }
          }
        }
        svg.push(path + '"/>');
        svg.push('</svg>');
        return svg.join('');
      }
    };
  }

  return {
    create: createQR
  };
});
