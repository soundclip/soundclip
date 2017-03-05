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
package soundclip;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.core.Project;
import soundclip.input.KeyMap;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Application Settings
 */
public class Settings
{
    private static final Logger Log = LogManager.getLogger(Settings.class);

    private final File SETTINGS_FILE = new File(System.getProperty("user.home"), ".soundclip.conf");

    private String lastOpenProjectPath = "";
    private String lastFileChooserDirectory = "";
    private final HashMap<String,String> recentProjects = new HashMap<>();
    private boolean progressCellsCountDown = true;
    private KeyMap keyMap = new KeyMap(null);

    public Settings()
    {
        if(SETTINGS_FILE.exists())
        {
            try
            {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode globalSettings = mapper.readTree(SETTINGS_FILE);

                lastOpenProjectPath = globalSettings.get("lastOpenProjectPath").asText("");
                lastFileChooserDirectory = globalSettings.get("lastFileChooserDirectory").asText("");

                JsonNode projects = globalSettings.get("recentProjects");
                if(projects != null)
                {
                    for(JsonNode recentProject : projects)
                    {
                        recentProjects.put(recentProject.get("path").asText(), recentProject.get("name").asText());
                    }
                }

                if(globalSettings.has("progressCellsCountDown"))
                {
                    progressCellsCountDown = globalSettings.get("progressCellsCountDown").asBoolean();
                }

                if(globalSettings.has("keyMap"))
                {
                    keyMap = new KeyMap(globalSettings.get("keyMap"));
                }
            }
            catch (IOException e)
            {
                Log.warn("Unable to open settings file", e);
            }
            catch (NullPointerException e)
            {
                Log.error("Malformed settings file", e);
            }
        }
        else
        {
            Log.info("No global application settings file found");
        }
    }

    public void save()
    {
        ObjectMapper m = new ObjectMapper();
        JsonFactory f = m.getFactory();

        try(JsonGenerator writer = f.createGenerator(SETTINGS_FILE, JsonEncoding.UTF8))
        {
            writer.useDefaultPrettyPrinter();

            writer.writeStartObject();
            {
                writer.writeStringField("lastOpenProjectPath", lastOpenProjectPath);
                writer.writeStringField("lastFileChooserDirectory", lastFileChooserDirectory);
                writer.writeArrayFieldStart("recentProjects");
                {
                    for(Map.Entry<String,String> p : recentProjects.entrySet())
                    {
                        writer.writeStartObject();
                        {
                            writer.writeStringField("path", p.getKey());
                            writer.writeStringField("name", p.getValue());
                        }
                        writer.writeEndObject();
                    }
                }
                writer.writeEndArray();
                writer.writeBooleanField("progressCellsCountDown", progressCellsCountDown);
                keyMap.save(writer);
            }
            writer.writeEndObject();
        }
        catch (IOException e)
        {
            Log.warn("Unable to save settings file", e);
        }
    }

    public String getLastOpenProjectPath()
    {
        return lastOpenProjectPath;
    }

    public void setLastOpenProjectPath(String lastOpenProjectPath)
    {
        this.lastOpenProjectPath = lastOpenProjectPath;
        save();
    }

    public String getLastFileChooserDirectory()
    {
        return lastFileChooserDirectory;
    }

    public void setLastFileChooserDirectory(String lastFileChooserDirectory)
    {
        this.lastFileChooserDirectory = lastFileChooserDirectory;
        save();
    }

    public Map<String, String> getRecentProjects()
    {
        return Collections.unmodifiableMap(recentProjects);
    }

    public void addRecentProject(Project p)
    {
        recentProjects.put(p.getPath(), p.getName());
    }

    public void removeRecentProject(String key)
    {
        recentProjects.remove(key);
        save();
    }

    public boolean shouldProgressCellsCountDown()
    {
        return progressCellsCountDown;
    }

    public void setProgressCellsCountDown(boolean progressCellsCountDown)
    {
        this.progressCellsCountDown = progressCellsCountDown;
        save();
    }

    public KeyMap getKeyMap()
    {
        return keyMap;
    }
}
