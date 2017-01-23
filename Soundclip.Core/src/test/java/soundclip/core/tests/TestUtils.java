// Copyright (C) 2016  Nathan Lowe
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
package soundclip.core.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Utility functions for tests
 */
public class TestUtils
{
    private static final Logger Log = LogManager.getLogger(TestUtils.class);

    private static final LinkedList<File> tempFiles = new LinkedList<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for(File f : tempFiles)
            {
                try
                {
                    if(!f.delete())
                    {
                        Log.warn("Unable to delete {}", f.getAbsolutePath());
                    }
                }
                catch(Exception ex)
                {
                    Log.error("Exception deleting " + f.getAbsolutePath(), ex);
                }
            }
        }, "Temp File Cleanup"));
    }

    static File createTemporaryFolder()
    {
        File dir = Paths.get(System.getProperty("java.io.tmpdir"), "soundclip-"+ UUID.randomUUID().toString()).toFile();

        dir.mkdirs();

        tempFiles.add(dir);
        return dir;
    }

    static File createTemporaryFile(File basePath, String extension)
    {
        Path p = basePath == null ?
                Paths.get(System.getProperty("java.io.tmpdir"), "soundclip-"+ UUID.randomUUID().toString() + extension)
                : Paths.get(basePath.getAbsolutePath(), "soundclip-"+ UUID.randomUUID().toString() + extension);

        File f = p.toFile();

        tempFiles.add(f);
        return f;
    }

    static File createTemporaryFile(String extension)
    {
        return createTemporaryFile(null, extension);
    }
}
