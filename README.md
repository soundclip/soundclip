# soundclip
[![Build Status](https://travis-ci.org/nlowe/soundclip.svg?branch=master)](https://travis-ci.org/nlowe/soundclip)
[![Coverage Status](https://coveralls.io/repos/github/nlowe/soundclip/badge.svg?branch=master)](https://coveralls.io/github/nlowe/soundclip?branch=master)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

An open source, sound cue management system for theatre.
Still a very early WIP.

## Supported Media Types
Currently, we're using the JavaFX Media API for playback.
The following file types are supported:

| Codec | Container | Extensions |
| ---- | --------- | ---------- |
| Uncompressed PCM | WAV / AIFF | `.wav`, `.aif`, `.aiff` |
| MP3  |  MP3      | `.mp3`     |
| MP4 (Audio Only) | AAC | `.mp4`, `.m4a`, `.m4v` |

For a more up to date list, please see the [JDK Documentation](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/media/package-summary.html).

## Building
You will need Java 8 and apache maven installed and available on
your `$PATH` (or `%PATH%`/`$env:PATH` if you're into that sort of thing).

Simply invoke maven to build all the things:

```bash
mvn package
```

## License
Copyright (C) 2016  Nathan Lowe

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.